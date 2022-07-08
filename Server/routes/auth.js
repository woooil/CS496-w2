var express = require('express');
const db = require('../model/db');
var router = express.Router();
const bcrypt = require('bcrypt')

/* GET home page. */
router.post('/register', function (req, res) {
    console.log("req는: ", req);
    const encryptedPwd = bcrypt.hashSync(req.body.pwd1, 10);
    console.log("encryptedpwd : ", encryptedPwd);

    db.query("insert into users (user_id, pwd, email, nickname) values(?, ?, ?, ?)", [req.body.user_id, encryptedPwd, req.body.email, req.body.nickname], (err, result) =>{
        if(err) throw err;
        
       

        res.json("데이터 insert 성공");
    })
});

// password utils
var genRandomString = function(length) {
    return crypto.randomBytes(Math.ceil(length/2))
      .toString('hex')
      .slice(0, length);
  };
  
var sha512 = function(password, salt) {
    var hash = crypto.createHmac('sha512', salt);
    hash.update(password);
    var value = hash.digest('hex');
    return {
        salt: salt,
        passwordHash: value
    };
}

function saltHashPassword(userPassword) {
    var salt = genRandomString(16);
    var passwordData = sha512(userPassword, salt);
    return passwordData;
}

router.get('/login/', (req, res, next) => {
    var post_data = req.body;
    var user_id = post_data.user_id;
    var user_password = post_data.password;
    var email = post_data.email;
    var nickname = post_data.nickname;
    
    db.query('select * from users where id=?', [user_id], function(err, result, fields) {
        db.on('error', function(err) {
            throw err;
        });
        
        if (result && result.length) {
            var right_password = result[0].pwd;
            if (user_password === right_password) 
                res.end(JSON.stringify(result[0]));
            else
                res.end(JSON.stringify('Wrong password!'));
        }
        else {
            res.json('User not exists!');
        }
    });
});

module.exports = router;