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



module.exports = router;




