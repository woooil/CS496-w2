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
const { Room, Roomcreate, getRoom, UserJoinRoom, printallroom, InspectArtist, DeleteRoom } = require('./utils/room');
const { exit } = require('process');

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
    socket.on('joinRoom', (new_user) => {
    console.log("username: ", new_user);
  
    const alreadyexist_room = getRoom(new_user.room);
    console.log("alreadyexist_room : ", alreadyexist_room)

      if (alreadyexist_room == undefined){  // 룸이 없을 때 새로운 룸을 만들고 한명 들어감
      Roomcreate(new Room(new_user.room, new_user.username));
      UserJoinRoom(new_user.username, getRoom(new_user.room));
    }
    else{ // 룸이 이미 있을 때 그 룸에 들어감
        UserJoinRoom(new_user.username, alreadyexist_room)
    }
  
    

    printallroom();
     
    const user = userJoin(socket.id, new_user.username, new_user.room);

    const cur_user = getCurrentUser(socket.id);
    const cur_room = getRoom(cur_user.room);
    let room_info = {
      all_player: cur_room.all_player,
      artist: cur_room.artist
    }

    

    console.log(user);

    socket.join(user.room);

    io.to(user.room).emit("information", room_info);

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
      console.log("나가기");
      const exit_user = getCurrentUser(socket.id);
      const exit_room = getRoom(exit_user.room);
      
      //나가는 사람이 artist일 때
      if(InspectArtist(exit_user.room, exit_user.username) == true){
        exit_room.userleave(exit_user);
        exit_room.setartist(); // all_player의 0번 째 index 사람을 artist로 설정 
      }
      else exit_room.userleave(exit_user);
      
      
    
      const user = userLeave(socket.id);
      console.log("exit_user : ", exit_user, " | exit_room: ", exit_room );


      let room_info = {
        all_player: exit_room.all_player,
        artist: exit_room.artist
      }
      socket.broadcast.to(exit_room.name).emit("information", room_info);

      if (exit_room.checkemptyplayer() == true){ //아무도 없을 때
        DeleteRoom(exit_room.name);
      }
      else{
        if (user) {
          io.to(user.room).emit('message', formatMessage(botName, `${user.username} has left the chat`));
        } 
      }
     
    });
    
    // Listen for chat message
    socket.on('chatMessage', (msg) => {
      const user = getCurrentUser(socket.id);
      console.log("user: " , user,  "msg is : ", msg);
      io.to(user.room).emit('message', formatMessage(user.username, msg));
    });


    socket.on('drawing', (data) => {
        const user = getCurrentUser(socket.id);
        console.log("드로잉포인트 1 : ", data.x, " ", data.y);
        socket.broadcast.to(user.room).emit('drawing', data);

    });


    socket.on('drawingStart', (data) => {
        const user = getCurrentUser(socket.id);
        console.log("스타트포인트 2 : ", data.x, " ", data.y);
        socket.broadcast.to(user.room).emit('drawingStart', data);

    });

  socket.on('gameStart', () => {
    const user = getCurrentUser(socket.id);
    const room = getRoom(user.room);
    console.log("게임스타트 소켓 + room 네임: ", room, " user: ", user);
    io.to(user.room).emit('gameStart', "게임이 시작 되었습니다.");
    socket.broadcast.to(user.room).emit('guessStart', );

  });

  socket.on('checkcorrect', () => {
    // const user = getCurrentUser(socket.id);
    // const room = getRoom(user.room);
    // console.log("게임스타트 소켓 + room 네임: ", room, " user: ", user);
    // io.to(user.room).emit('gameStart', "게임이 시작 되었습니다.");
    // socket.broadcast.to(user.room).emit('guessStart',);

  });





    
  });

exports.app = app;