var express = require('express');
const db = require('../model/db');
var router = express.Router();
const bcrypt = require('bcrypt');

//duplicate id check 
router.post('/check', function (req, res) {


    console.log("아이디 체크");
    db.query("select * from users where user_id = ?", [req.body.user_id], (err1, result1) => {
        if (err1) throw err1;

        if (result1.length != 0) {
            console.log("이미 존재하는 아이디");
            res.json("이미 존재하는 아이디입니다.");
            return;
        }

        console.log("사용 가능한 아이디");
        res.json("사용 가능한 아이디입니다.");



    });

});

//sign up
router.post('/register', function (req, res) {
    console.log("레지스터");

    if(req.body.pwd1 != req.body.pwd2){
        res.json("비밀번호가 같아야 합니다.");
        return;
    }
    
    db.query("select * from users where user_id = ?", [req.body.user_id], (err1, result1) =>{
        if(err1) throw err1;

        if (result1.length != 0){
            res.json("이미 존재하는 아이디입니다.");
            return;
        }
        // console.log("req는: ", req);
        const encryptedPwd = bcrypt.hashSync(req.body.pwd1, 10);
        console.log("encryptedpwd : ", encryptedPwd);
        db.query("insert into users (user_id, pwd, email, nickname) values(?, ?, ?, ?)", [req.body.user_id, encryptedPwd, req.body.email, req.body.nickname], (err, result) => {
            if (err) throw err;

            res.json("회원가입이 완료되었습니다.");
        })

  
    
  });

});


//sign in
router.post('/login', (req, res, next) => {
    console.log("sssss");
    var post_data = req.body;
    var user_id = post_data.user_id;
    var user_password = post_data.password;
    
    db.query('select * from users where user_id=?', [user_id], function(err, result, fields) {
        db.on('error', function(err) {
            throw err;
        });
        
        if (result && result.length) {
            bcrypt.compare(user_password, result[0].pwd, (err, success) => {
                if(err) throw err;
                if (success == true) {
                    var json = {
                        user_id: result[0].user_id,
                        email: result[0].email,
                        nickname: result[0].nickname
                    }
                    res.json(json);
                }
                else
                    res.end(JSON.stringify('Wrong password!'));
            });
        }
        else {
            res.json('User not exists!');
        }
    });
});

module.exports = router;