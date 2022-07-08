var express = require('express');
const db = require('../model/db');
var router = express.Router();
let crypto = require('crypto');

/* GET home page. */
router.post('/register', function (req, res) {
    db.query("insert into test (user_id, pwd, email) values(?, ?, ?)", ["wooil", "wooil123", "wooil@naver.com"], (err, result) =>{
        if(err) throw err;

        console.log("회원가입 성공");
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