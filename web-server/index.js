const express = require('express');
const routes = require('./routes/routes.js');
const pug = require('pug')

const app = express();
const port = 3000;

app.set('view engine', 'pug');
app.set('views', __dirname + "/views");
app.use(express.static("./public"));

app.get('/', routes.index);

app.listen(port, (err) => {
    if (err) {
        console.log("Error starting app.");
        console.log(err);
    }
    console.log("Server started at http://localhost:" + port)
});
