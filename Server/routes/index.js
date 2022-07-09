var socketio = require('socket.io');
var express = require('express');
var router = express.Router();

var server = require('../app').server;

/* GET home page. */
router.get('/', function(req, res) {
   console.log("테스트 성공");
   res.json({ "aa" : "ㄴㄴㄴ" });
});

module.exports = router;
