// let ctx = document.getElementById("canvas").getContext("2d");

let directionsManager;
let entities = [];

const verify_login = () => {
    let user = document.getElementById("login_user").value;
    let pass = document.getElementById("login_pass").value;

    fetch(
        "http://localhost:8070/user-service/user/auth", {
            headers: {
                "Authorization": "Basic " + btoa(user + ":" + pass),
                "Access-Control-Allow-Origin": "localhost:8070",
            }
        }
    ).then(response => {
        console.log(response);
        if (response.status === 204) {
            resume_login();
            return true;
        }
        return false;
    }).catch(err => {
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
    // document.getElementById("canvas").style.display = "block";
    // setInterval(() => vehicle_track(render_vehicles), 1000);
    return true;
}

const login = () => {
    verify_login();
}

const render_vehicles = (vehicles) => {
    // let canvas = document.getElementById("canvas");
    // ctx.clearRect(0, 0, canvas.width, canvas.height);
    // ctx.fillStyle = "#0a2239";
    // ctx.fillRect(0, 0, canvas.width, canvas.height);
    // console.log([canvas.width, canvas.height]);
    // let dims = [canvas.height / 2, canvas.width / 2];
    // for (let vehicle of vehicles) {
    //     coord = [vehicle.lat * (dims[0] / 180) + dims[0], vehicle.lng * (dims[1] / 90) + dims[1]]
    //     console.log(coord);
    //     ctx.beginPath();
    //     ctx.fillStyle = "#f28f3b";
    //     ctx.arc(coord[0], coord[1], 5, 0, 260);
    //     ctx.fill();
    // }
}

const vehicle_track = (callback) => {
    fetch(
        "http://localhost:8070/vehicle-service/vehicle"
    ).then(
        response => response.json()
    ).then(
        vehicles => {
            callback(vehicles);
        }
    ).catch(
        err => {
            console.error(err);
        }
    )
}

const loadNotifications = () => {
    fetch(
        "http://localhost:8070/notifications-service/notification/active"
    ).then(
        response => response.json()
    ).then(
        responses => {
            let notifs = document.getElementById("center_notifs");
            let content = "";

            for (let response of responses) {
                content += `<div class="notification">
                    <p><b>${response.title}</b></p>
                    <p>${response.body}</p>
                </div>`;
            }

            notifs.innerHTML = content;
        }
    ).catch(
        err => console.error(err)
    );
};

const loadRoutes = () => {
    /*
    TODO Should we add a name to the routes?
    [{
        "id": 27,
        "stops": [...]
    },...]
     */
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
                    dislayRouteSelector: false,
                    lastWaypointPushpinOptions: {
                        title: ''
                    }
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
                                pushpin.setOptions({subTitle: Math.ceil(response[0].eta/60) + " minutes"});
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

let modal;

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
                    content += `<div class='route-id'>Route ${route.id}</div>`;
                    let stops = route.stops;
                    for (let stop of stops) {
                        content += `<div class="stop">Stop ${stop.id}</div>`; // - <span class="eta"></span>
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

        //Load the module.
        // Microsoft.Maps.loadModule('CanvasOverlayModule', function () {
        // let locations = Microsoft.Maps.TestDataGenerator.getLocations(10, map.getBounds());

        setInterval(() =>
                vehicle_track((vehicles) => {
                    // map.layers.clear();
                    map.entities.clear();
                    let userPin = new Microsoft.Maps.Pushpin(userLocation.coords, {color: 'blue'});
                    map.entities.push(userPin);
                    map.entities.push(entities);

                    // locations = [];
                    for (let veh of vehicles) {
                        // locations.push({
                        //     latitude: veh.lat ? veh.lat : 0,
                        //     longitude: veh.lng ? veh.lng : 0,
                        //     altitude: 0,
                        //     altitudeReference: -1
                        // });
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
                        // let pin = new Microsoft.Maps.Pushpin({
                        //     latitude: veh.lat ? veh.lat : 0,
                        //     longitude: veh.lng ? veh.lng : 0,
                        // }, {
                        //     title: veh.name,
                        //     icon: 'https://www.pinclipart.com/picdir/big/109-1091177_tesla-model-x-clipart-tesla-model-s-clip.png',
                        //     color: 'red',
                        //     text: veh.id.toString()
                        // });
                        //
                        // //Add the pushpin to the map
                        // map.entities.push(pin);
                    }

                    // Implement the new custom overlay class.
                    // let overlay = new CanvasOverlay(function (canvas) {
                    //     //Calculate pixel coordinates of locations.
                    //     let points = map.tryLocationToPixel(locations, Microsoft.Maps.PixelReference.control);
                    //
                    //     let ctx = canvas.getContext("2d");
                    //     ctx.fillStyle = 'red';
                    //
                    //     let pi2 = 2 * Math.PI;
                    //
                    //     //Draw circles for each location.
                    //     for (let i = 0, len = points.length; i < len; i++) {
                    //         ctx.beginPath();
                    //         ctx.arc(points[i].x, points[i].y, 5, 0, pi2);
                    //         ctx.fill();
                    //         ctx.closePath();
                    //     }
                    // });
                    //
                    // //Add the custom overlay to the map.
                    // map.layers.insert(overlay);
                }),
            1000);
        // });
    });
};
