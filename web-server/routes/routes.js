const path = require('path');

exports.index = (req, res) => {
    res.sendFile("index.html", { root: __dirname + "/../public" });
};
