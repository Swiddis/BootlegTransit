// let ctx = document.getElementById("canvas").getContext("2d");

const verify_login = () => {
    let user = document.getElementById("login_user").value;
    let pass = document.getElementById("login_pass").value;

    fetch(
        "http://localhost:8070/user-service/auth", {
            headers: {
                "Authorization": "Basic " + btoa(user + ":" + pass)
            }
        }
    ).then(response => {
        console.log(response);
        return response.status == 204;
    }).catch(err => {
        console.error(err);
        return false;
    });
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
}

const login = () => {
    // if (!verify_login()) return;
    document.getElementById("title").style.display = "none";
    document.getElementById("login").style.display = "none";
    document.getElementById("main").style.display = "flex";
    loadNotifications();
    // document.getElementById("canvas").style.display = "block";
    // setInterval(() => vehicle_track(render_vehicles), 1000);
    return true;
}

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

let openModal = veh => {
    let modal = document.createElement("div");
    modal.classList.add("modal");

    document.body.appendChild(modal);
    modal.onclick = evt => {
        document.body.removeChild(modal);
    };
};

let map;
let userLocation;

let loadMapScenario = () => {
    getLocation(position => {
        showPosition(position);
        map = new Microsoft.Maps.Map(document.getElementById('myMap'), {
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

login();
