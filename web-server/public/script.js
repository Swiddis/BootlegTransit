let modal;
let activeRoute;
let activeRoutes = [];
let map;
let userLocation;

document.getElementById('login_user').addEventListener('keypress', (evt) => {
    if (evt.code == 'Enter')
        verify_login()
});
document.getElementById('login_pass').addEventListener('keypress', (evt) => {
    if (evt.code == 'Enter')
        verify_login()
});

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
    let selectedRoute;
    let selectedIndex = -1;

    for (let i = 0; i < activeRoutes.length; i++) {
        let rt = activeRoutes[i];
        if (rt.id == id) {
            selectedRoute = rt;
            selectedIndex = i;
            break;
        }
    }

    if (selectedRoute) {
        activeRoutes.splice(selectedIndex, 1);
        return;
    }

    fetch("http://localhost:8070/stops-service/route/" + id)
        .then(response => response.json())
        .then(response => {
            for (let route of activeRoutes) {
                route.erase(map);
            }
            // activeRoutes = [];
            let route = new Route(id, response.color, response.stops);
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
                let properWidth = img.width;
                while (properWidth > scale.width) {
                    properWidth--;
                }
                let properScale = properWidth / img.width;
                c.width = img.width * properScale;
                c.height = img.height * properScale;

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

                    for (let veh of vehicles) {
                        //Create custom Pushpin
                        let img = veh.name.toLowerCase().includes('car')
                            ? '/car.png'
                            : (veh.name.toLowerCase().includes('train')
                                ? '/train.png'
                                : '/bus.png');
                        createImagePushpin({
                                latitude: veh.lat ? veh.lat : 0,
                                longitude: veh.lng ? veh.lng : 0,
                            }, img,
                            {width: veh.name.toLowerCase().includes('bus') ? 60 : 80, height: 25}, (pin) => {
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
