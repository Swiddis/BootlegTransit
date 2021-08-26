const express = require('express');
const routes = require('./routes/routes.js');
const pug = require('pug');
const session = require('express-session');

const app = express();
const port = 3000;

app.set('view engine', 'pug');
app.set('views', __dirname + "/views");
app.use(express.static("./public"));
app.use(session({
    secret: 'toPsEcreT',
    resave: true,
    saveUninitialized: true,
    cookie: {
        httpOnly: false
    }
}));

app.get('/', routes.index);
app.get('/login', routes.login);

app.listen(port, (err) => {
    if (err) {
        console.log("Error starting app.");
        console.log(err);
    }
    console.log("Server started at http://localhost:" + port)
});
