var express = require('express');
const db = require('../model/db');
var router = express.Router();

/* GET home page. */
router.post('/register', function (req, res) {
    db.query("insert into test (user_id, pwd, email) values(?, ?, ?)", ["wooil", "wooil123", "wooil@naver.com"], (err, result) =>{
        if(err) throw err;

        console.log("회원가입 성공");
        res.json("데이터 insert 성공");
    })
    
});

module.exports = router;




