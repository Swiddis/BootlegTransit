let directionsManager;
let entities = [];
let modal;
let activeRoute;
let activeRoutes = [];
let map;
let userLocation;

const verify_login = () => {
    let user = document.getElementById("login_user").value;
    let pass = document.getElementById("login_pass").value;

    fetch("http://localhost:8070/user-service/user/auth", {
        headers: {
            "Authorization": "Basic " + btoa(user + ":" + pass),
            "Access-Control-Allow-Origin": "localhost:8070",
        }
    })
        .then(response => {
            console.log(response);
            if (response.status === 204) {
                resume_login();
                return true;
            }
            return false;
        })
        .catch(err => {
            console.error(err);
            return false;
        });
}

const resume_login = () => {
    document.getElementById("title").style.display = "none";
    document.getElementById("login").style.display = "none";
    document.getElementById("main").style.display = "flex";
    loadNotifications();
    loadRoutes();
    loadMapScenario();
    return true;
}

const login = () => {
    verify_login();
}

const register = () => {
    let user = document.getElementById("login_user").value;
    let pass = document.getElementById("login_pass").value;
    let reg_status = document.getElementById("register_status");
    reg_status.innerText = "Registering...";
    reg_status.style.color = "#ccc";

    fetch(
        "http://localhost:8070/user-service/user", {
            method: "POST",
            body: JSON.stringify({
                username: user,
                password: pass,
                roles: ["USER"]
            }),
            headers: {
                "Access-Control-Allow-Origin": "localhost:8070",
                "Content-Type": "application/json",
            },
        }
    ).then(response => {
        console.log(response);
        if (response.status === 200) {
            reg_status.innerText = "Registered!";
            reg_status.style.color = "#0a0";
        } else {
            reg_status.innerText = "Registration failed.";
            reg_status.style.color = "#c00";
        }
    }).catch(err => {
        console.log(err);
        reg_status.innerText = "Error Occurred.";
        reg_status.style.color = "#c00";
    });
}

const vehicle_track = (callback) => {
    fetch("http://localhost:8070/vehicle-service/vehicle")
        .then(response => response.json())
        .then(vehicles => {
            callback(vehicles);
            document.getElementById("lost-conn").style.display = "none";
        })
        .catch(err => {
            console.error(err);
            document.getElementById("lost-conn").style.display = "block";
        })
}

const loadNotifications = () => {
    fetch("http://localhost:8070/notifications-service/notification/active")
        .then(response => response.json())
        .then(responses => {
            let notifs = document.getElementById("center_notifs");
            let content = "";

            for (let response of responses) {
                content += `<div class="notification">
                    <p><b>${response.title}</b></p>
                    <p>${response.body}</p>
                </div>`;
            }

            notifs.innerHTML = content;
        })
        .catch(err => console.error(err));
};

const loadRoutes = () => {
    fetch("http://localhost:8070/stops-service/route")
        .then(response => response.json())
        .then(responses => {
            let routes = document.getElementById("center_routes");
            let content = "";

            for (let response of responses) {
                content += `<div class="route" onclick="selectRoute(${response.id})">
                    <p><b>Route ${response.id}</b></p>
                </div>`;
            }

            routes.innerHTML = content;
        })
        .catch(err => console.error(err));
};

const selectRoute = id => {
    //Load the directions module.
    activeRoute = id;
    fetch("http://localhost:8070/stops-service/route/" + id)
        .then(response => response.json())
        .then(response => {
            for (let route of activeRoutes) {
                route.erase(map);
            }
            activeRoutes = [];
            let route = new Route(id, 'green', response.stops);
            activeRoutes.push(route);
            route.draw(map);

            // Center the map around the route
            let locs = [];
            for (let stop of response.stops) {
                locs.push(new Microsoft.Maps.Location(stop.lat, stop.lng))
            }
            map.setView({
                bounds: Microsoft.Maps.LocationRect.fromLocations(locs),
                padding: 50
            });
        })
        .catch(err => console.error(err));
};

let getLocation = (callback) => {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(callback);
        navigator.geolocation.watchPosition((position) => userLocation = position);
    } else {
        console.log("Geolocation is not supported by this browser.");
    }
};

let showPosition = (position) => {
    console.log("Latitude: " + position.coords.latitude +
        "\nLongitude: " + position.coords.longitude);
};

let closeModal = () => {
    document.body.removeChild(modal);
};

let openModal = veh => {
    fetch("http://localhost:8070/vehicle-service/vehicle/" + veh.id)
        .then(response => response.json())
        .then(vehicle => {
            let routeId = vehicle.routeId;
            fetch("http://localhost:8070/stops-service/route/" + routeId)
                .then(response => response.json())
                .then(route => {
                    if (route.status && route.status == 500) {
                        console.log("No route");
                        alert("That vehicle has no route. Uh oh!");
                        return;
                    }

                    modal = document.createElement("div");
                    modal.classList.add("modal");

                    document.body.appendChild(modal);

                    let content = "<div id='stops-box'><div class='close' onclick='closeModal()'>X</div>";
                    content += `<div class='route-id'>Route ${route.id}</div><hr>`;
                    let stops = route.stops;
                    for (let stop of stops) {
                        content += `<li class="stop">Stop ${stop.id}</li>`; // - <span class="eta"></span>
                    }
                    content += "</div>";

                    modal.innerHTML = content;

                })
                .catch(err => console.log(err));
        })
        .catch(err => console.log(err));
};

let loadMapScenario = () => {
    getLocation(position => {
        showPosition(position);
        map = new Microsoft.Maps.Map('#myMap', {
            center: new Microsoft.Maps.Location(position.coords.latitude, position.coords.longitude),
            zoom: 15
        });

        let createImagePushpin = (location, imgUrl, scale, callback) => {
            let img = new Image();
            img.onload = () => {
                let c = document.createElement('canvas');
                c.width = scale.width;
                c.height = scale.height;

                let context = c.getContext('2d');

                //Draw scaled image
                context.drawImage(img, 0, 0, c.width, c.height);

                let pin = new Microsoft.Maps.Pushpin(location, {
                    //Generate a base64 image URL from the canvas.
                    icon: c.toDataURL(),
                    // Anchor based on the center of the image.
                    anchor: new Microsoft.Maps.Point(c.width / 2, c.height / 2)
                });

                if (callback) {
                    callback(pin);
                }
            };
            img.src = imgUrl;
        };

        setInterval(() =>
                vehicle_track((vehicles) => {
                    map.entities.clear();
                    for (let route of activeRoutes) {
                        route.draw(map);
                    }
                    let userPin = new Microsoft.Maps.Pushpin(userLocation.coords, {color: 'blue'});
                    map.entities.push(userPin);
                    map.entities.push(entities);

                    for (let veh of vehicles) {
                        //Create custom Pushpin
                        createImagePushpin({
                                latitude: veh.lat ? veh.lat : 0,
                                longitude: veh.lng ? veh.lng : 0,
                            }, '/car.png',
                            {width: 80, height: 25}, (pin) => {
                                pin.setOptions({title: veh.name});
                                map.entities.push(pin);
                                Microsoft.Maps.Events.addHandler(pin, 'click', () => {
                                    openModal(veh);
                                });
                            });
                    }
                }),
            1000);
    });
};


function getCurvePoints(pts, tension, isClosed, numOfSegments) {

    // use input value if provided, or use a default value
    tension = (typeof tension != 'undefined') ? tension : 0.5;
    isClosed = isClosed ? isClosed : false;
    numOfSegments = numOfSegments ? numOfSegments : 16;

    var _pts = [],
        res = [], // clone array
        x, y, // our x,y coords
        t1x, t2x, t1y, t2y, // tension vectors
        c1, c2, c3, c4, // cardinal points
        st, t, i; // steps based on num. of segments

    // clone array so we don't change the original
    //
    _pts = pts.slice(0);

    // The algorithm require a previous and next point to the actual point array.
    // Check if we will draw closed or open curve.
    // If closed, copy end points to beginning and first points to end
    // If open, duplicate first points to befinning, end points to end
    if (isClosed) {
        _pts.unshift(pts[pts.length - 1]);
        _pts.unshift(pts[pts.length - 2]);
        _pts.unshift(pts[pts.length - 1]);
        _pts.unshift(pts[pts.length - 2]);
        _pts.push(pts[0]);
        _pts.push(pts[1]);
    } else {
        _pts.unshift(pts[1]); //copy 1. point and insert at beginning
        _pts.unshift(pts[0]);
        _pts.push(pts[pts.length - 2]); //copy last point and append
        _pts.push(pts[pts.length - 1]);
    }

    // ok, lets start..

    // 1. loop goes through point array
    // 2. loop goes through each segment between the 2 pts + 1e point before and after
    for (i = 2; i < (_pts.length - 4); i += 2) {
        for (t = 0; t <= numOfSegments; t++) {

            // calc tension vectors
            t1x = (_pts[i + 2] - _pts[i - 2]) * tension;
            t2x = (_pts[i + 4] - _pts[i]) * tension;

            t1y = (_pts[i + 3] - _pts[i - 1]) * tension;
            t2y = (_pts[i + 5] - _pts[i + 1]) * tension;

            // calc step
            st = t / numOfSegments;

            // calc cardinals
            c1 = 2 * Math.pow(st, 3) - 3 * Math.pow(st, 2) + 1;
            c2 = -(2 * Math.pow(st, 3)) + 3 * Math.pow(st, 2);
            c3 = Math.pow(st, 3) - 2 * Math.pow(st, 2) + st;
            c4 = Math.pow(st, 3) - Math.pow(st, 2);

            // calc x and y cords with common control vectors
            x = c1 * _pts[i] + c2 * _pts[i + 2] + c3 * t1x + c4 * t2x;
            y = c1 * _pts[i + 1] + c2 * _pts[i + 3] + c3 * t1y + c4 * t2y;

            //store points in array
            res.push(x);
            res.push(y);

        }
    }

    return res;
}
