// let ctx = document.getElementById("canvas").getContext("2d");

let directionsManager;
let entities = [];
let modal;

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
        .then(vehicles => callback(vehicles))
        .catch(err => console.error(err))
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
    Microsoft.Maps.loadModule('Microsoft.Maps.Directions', () => {
        //Create an instance of the directions manager.
        if (!directionsManager)
            directionsManager = new Microsoft.Maps.Directions.DirectionsManager(map);
        directionsManager.clearAll();
        entities = [];

        fetch("http://localhost:8070/stops-service/route/" + id)
            .then(response => response.json())
            .then(response => {
                let first;
                for (let stop of response.stops) {
                    let location = new Microsoft.Maps.Location(stop.lat, stop.lng);
                    let waypoint = new Microsoft.Maps.Directions.Waypoint({
                        address: "Stop " + stop.id,
                        location: location
                    });
                    if (!first)
                        first = stop
                    directionsManager.addWaypoint(waypoint);
                }
                if (first) {
                    let location = new Microsoft.Maps.Location(first.lat, first.lng)
                    let waypoint = new Microsoft.Maps.Directions.Waypoint({
                        address: "Stop " + stop.id,
                        location: location
                    });
                    directionsManager.addWaypoint(waypoint);
                }
                //Specify the element in which the itinerary will be rendered.
                directionsManager.setRenderOptions({
                    drivingPolylineOptions: {
                        strokeColor: 'green',
                        strokeThickness: 8
                    },
                    lastWaypointPushpinOptions: {
                        title: ''
                    }
                });
                directionsManager.setRequestOptions({
                    routeDraggable: false,
                    maxRoutes: 1,
                    routeOptimization: "shortestDistance"
                });


                //Calculate directions.
                directionsManager.calculateDirections();
                Microsoft.Maps.Events.addHandler(directionsManager, 'directionsUpdated', () => {
                    let pushpins = directionsManager.getAllPushpins();
                    for (let pushpin of pushpins) {
                        console.log(pushpin);
                        let title = pushpin.getTitle();
                        let stopId = title.split(" ")[1];
                        console.log(stopId);

                        fetch("http://localhost:8070/schedule-service/" + stopId)
                            .then(response => response.json())
                            .then(response => {
                                if (response.length == 0)
                                    return;
                                pushpin.setOptions({subTitle: Math.ceil(response[0].eta / 60) + " minutes"});
                            });
                    }
                });
            })
            .catch(err => console.error(err));
    });
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

                    console.log(route);
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

let map;
let userLocation;

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
                    //Anchor based on the center of the image.
                    anchor: new Microsoft.Maps.Point(c.width / 2, c.height / 2)
                });

                if (callback) {
                    callback(pin);
                }
            };
            img.src = imgUrl;
        };

        //Register the custom module.
        Microsoft.Maps.registerModule('CanvasOverlayModule', 'CanvasOverlayModule.js');

        setInterval(() =>
                vehicle_track((vehicles) => {
                    map.entities.clear();
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
