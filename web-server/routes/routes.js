const fetch = require('node-fetch');

exports.index = (req, res) => {
    console.log(req.session.auth);
    res.render('index', {
        user: req.session.auth
    });
};

exports.login = (req, res) => {
    let auth = req.get('Authorization');
    let user = auth.split(" ")[1];
    // res.setHeader('Access-Control-Allow-Credentials', 'true')
    user = Buffer.from(user, 'base64').toString().split(':', 2)[0];

    fetch("http://cloud-gateway:8080/user-service/user/auth", {
        headers: {
            "Authorization": auth,
            "Access-Control-Allow-Origin": "localhost:8070",
        }
    })
        .then(response => {
            if (response.status === 204) {
                res.statusCode = 204;
                req.session.auth = {user, isAuthenticated: true};
                console.log("Added cookie.", req.session.auth);
                res.send({success: true});
                return;
            }
            res.statusCode = 400;
            res.send({success: false});
        })
        .catch(err => {
            console.error(err);
            res.send({success: false});
        });
};