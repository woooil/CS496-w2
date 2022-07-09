const express = require('express');
const socketio = require('socket.io');
var router = express.Router();

const server = require('../app');
const io = socketio(server);

router.get('/', function(req, res) {
  res.send('respond with a resource in list');

});

module.exports = router;