var socketio = require('socket.io');
let express = require('express');
let path = require('path');
let favicon = require('static-favicon');
let logger = require('morgan');
let cookieParser = require('cookie-parser');
let bodyParser = require('body-parser');
const http = require('http');

let routes = require('./routes/index');
let users = require('./routes/users');
let authrouter = require('./routes/auth');
let list = require('./routes/list');
let db = require('./model/db');
const { userJoin, getCurrentUser, userLeave, getRoomUsers } = require('./utils/users');
const formatMessage = require('./utils/messages');

let app = express();
const server = app.listen(4000,() =>{
    console.log("연결 성공");
});;


// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(favicon());
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded());
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.get('/api', function (req, res) {
    db.query("select * from users",(err, result )=>{

        console.log("api 테스트 성공");
        console.log(result);
        res.send(result);

    })
    
});



app.use('/', routes);
app.use('/auth', authrouter)
app.use('/users', users);
app.use('/list', list);

/// catch 404 and forwarding to error handler
app.use(function(req, res, next) {
    let err = new Error('Not Found');
    err.status = 404;
    next(err);
});

/// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
// app.use(function(err, req, res, next) {
//     res.status(err.status || 500);
//     res.render('error', {
//         message: err.message,
//         error: {}
//     });
// });
  
const io = socketio(server);
const botName = "BOT";

io.on('connection', socket => {
    socket.on('joinRoom', (username) => {
      const room = "ROOM0";
      const user = userJoin(socket.id, username, room);
      console.log(user);
  
      socket.join(user.room);
  
      // Welcome current user
      socket.emit('message', formatMessage(botName, 'Welcome to ChatCord!'));
      
      // Broadcast when a user connects
      socket.broadcast.to(user.room).emit('message', formatMessage(botName, `${user.username} has joined the chat`));
      
      // Send users and room info
    //   io.to(user.room).emit('roomUsers', {
    //     room: user.room,
    //     users: getRoomUsers(user.room)
    //   });
    });
  
    // Runs when client disconnects
    socket.on('disconnect', () => {
      const user = userLeave(socket.id);
      if (user) {
        io.to(user.room).emit('message', formatMessage(botName, `${user.username} has left the chat`));
      
        // Send users and room info
        // io.to(user.room).emit('roomUsers', {
        //   room: user.room,
        //   users: getRoomUsers(user.room)
        // });
      }
    });
    
    // Listen for chat message
    socket.on('chatMessage', (msg) => {
      const user = getCurrentUser(socket.id);
      console.log(msg);
      io.to(user.room).emit('message', formatMessage(user.username, msg));
    });
  });

exports.server = server;
exports.app = app;