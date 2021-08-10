let ctx = document.getElementById("canvas").getContext("2d");

const vehicle_track = () => {
    fetch(
        "http://localhost:8070/vehicle-service/vehicle"
    ).then(
        response => response.json()
    ).then(
        vehicles => {
            console.log(vehicles);
            let canvas = document.getElementById("canvas");
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = "#0a2239";
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            console.log([canvas.width, canvas.height]);
            let dims = [canvas.height / 2, canvas.width / 2];
            for (let vehicle of vehicles) {
                coord = [vehicle.lat * (dims[0] / 180) + dims[0], vehicle.lng * (dims[1] / 90) + dims[1]]
                console.log(coord);
                ctx.beginPath();
                ctx.fillStyle = "#f28f3b";
                ctx.arc(coord[0], coord[1], 5, 0, 260);
                ctx.fill();
            }
        }
    ).catch(
        err => {
            console.error(err);
        }
    )
}

const login = () => {
    document.getElementById("title").style.display = "none";
    document.getElementById("login").style.display = "none";
    document.getElementById("main").style.display = "block";
    document.getElementById("canvas").style.display = "block";
    setInterval(vehicle_track, 1000);
    return true;
}
