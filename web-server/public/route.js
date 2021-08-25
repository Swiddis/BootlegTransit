class Route {
    /**
     * Creates a route object with the given waypoints and the supplied color
     * @param waypoints - Waypoints in [{lat,lng},...] format
     * @param color - String. ie 'green'
     */
    constructor(id, color, waypoints) {
        this.id = id;
        this.waypoints = waypoints;
        this.color = color;
        this.lastUpdated = -1;
        this.etas = [];
    }

    updateEtas = () => {
        if (Date.now() - this.lastUpdated < 30000) //Only update very 30 seconds
            return;

        this.lastUpdated = Date.now();
        fetch("http://localhost:8070/schedule-service/eta/" + this.id)
            .then(response => response.json())
            .then(response => {
                if (response.length == 0)
                    return;
                console.log(response);
                this.etas = response;
            });

    };

    draw = (map) => {
        this.updateEtas();
        this.polyline = new Microsoft.Maps.Polyline([], {
            strokeColor: this.color,
            strokeThickness: 5
        });
        let ptsa = [];
        let first;
        let usedLocs = [];
        for (let stop of this.waypoints) {
            if (!first)
                first = stop;
            ptsa.push(stop.lat);
            ptsa.push(stop.lng);
            let loc = {
                latitude: stop.lat,
                longitude: stop.lng
            };
            if (usedLocs.includes(loc.latitude + "," + loc.longitude))
                continue;

            // usedLocs.push(loc.latitude + "," + loc.longitude);
            let pushpin = new Microsoft.Maps.Pushpin(loc,
                {
                    color: 'red',
                    title: 'Stop ' + stop.id
                });
            let eta = -1;
            for (let time of this.etas) {
                if (time.stop == stop.id) {
                    eta = time.eta;
                }
            }
            if (eta != -1) {
                eta = Math.ceil(eta / 60);
                pushpin.setOptions({subTitle: eta + " minute" + (eta != 1 ? "s" : "")});
            }
            map.entities.push(pushpin);
        }
        if (first) {
            ptsa.push(first.lat);
            ptsa.push(first.lng);
        }
        let curve = getCurvePoints(ptsa, 0.1, true);
        let locs = [];
        for (let i = 0; i < curve.length; i++) {
            let pt = curve[i++];
            let pt2 = curve[i];
            locs.push(new Microsoft.Maps.Location(pt, pt2));
        }
        this.polyline.setLocations(locs);
        map.entities.push(this.polyline);
    };

    erase = (map) => {
        map.entities.remove(this.polyline);
    };
}