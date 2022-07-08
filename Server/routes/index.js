var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {
   console.log("테스트 성공");
   res.json({ "aa" : "ㄴㄴㄴ" });
});

module.exports = router;
