var express = require('express');
const db = require('../model/db');
var router = express.Router();
const bcrypt = require('bcrypt')

/* GET home page. */
router.post('/register', function (req, res) {

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

            res.json("데이터 insert 성공");
        })

    })



  
    
});



module.exports = router;




