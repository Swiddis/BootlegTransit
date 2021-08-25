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