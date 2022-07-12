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
const { userJoin, getCurrentUser, userLeave, getRoomUsers, getUserbyname } = require('./utils/users');
const formatMessage = require('./utils/messages');
const { Room, Roomcreate, getRoom, UserJoinRoom, printallroom, InspectArtist, DeleteRoom, howManypeople } = require('./utils/room');
const { exit } = require('process');
const internal = require('stream');
const { compileClient } = require('jade');

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

  socket.on("howMany", () =>{
    socket.emit("howMany", howManypeople());
  }) 


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

    console.log("들어갈 때 : ", user.room);
    io.to(user.room).emit("information", room_info);

    // Welcome current user
    socket.emit('message', formatMessage(botName, '환영합니다'));
    
    // Broadcast when a user connects
    socket.broadcast.to(user.room).emit('message', formatMessage(botName, `${user.username} 님이 입장하였습니다.`));
    io.emit("howMany", howManypeople());

    });
  
    // Runs when client disconnects
    socket.on('quit', () => {
      console.log("나가기");
      const exit_user = getCurrentUser(socket.id);
      const exit_room = getRoom(exit_user.room);
      
      //나가는 사람이 artist일 때
      if(InspectArtist(exit_user.room, exit_user.username) == true){
        exit_room.userleave(exit_user);
        exit_room.setartist(); // all_player의 0번 째 index 사람을 artist로 설정 
      }
      else exit_room.userleave(exit_user);
      
      socket.leave(exit_room.name);
      const user = userLeave(socket.id);
      console.log("exit_user : ", exit_user, " | exit_room: ", exit_room );


      let room_info = {
        all_player: exit_room.all_player,
        artist: exit_room.artist
      }
      console.log("나가는 방 : ", exit_room.name);
      socket.broadcast.to(exit_room.name).emit("information", room_info);

      if (exit_room.checkemptyplayer() == true){ //아무도 없을 때
        DeleteRoom(exit_room.name);
      }
      else{
        if (user) {
          io.to(user.room).emit('message', formatMessage(botName, `${user.username} 님이 퇴장하였습니다.`));
        } 
      }

      io.emit("howMany", howManypeople());
     
    });
    
    // Listen for chat message
    socket.on('chatMessage', (msg) => {
      const user = getCurrentUser(socket.id);
      const gameroom = getRoom(user.room);
      console.log("user: " , user,  "msg is : ", msg);
      io.to(user.room).emit('message', formatMessage(user.username, msg));
      if (msg == gameroom.answer) { // 정답일 떼

        io.to(user.room).emit('correct', user.username);
        gameroom.artist = user.username;
        gameroom.answer = null;
        console.log("새로운 아티스트 : ", gameroom.artist);
        console.log("정답 null : ", gameroom.answer);
        
      }
    });


    socket.on('drawing', (data) => {
        const user = getCurrentUser(socket.id);
        // console.log("드로잉포인트 1 : ", data.x, " ", data.y);
        socket.broadcast.to(user.room).emit('drawing', data);

    });


    socket.on('drawingStart', (data) => {
        const user = getCurrentUser(socket.id);
        // console.log("스타트포인트 2 : ", data.x, " ", data.y);
        socket.broadcast.to(user.room).emit('drawingStart', data);

    });

  socket.on('undo', () => {
    const user = getCurrentUser(socket.id);
    // console.log("스타트포인트 2 : ", data.x, " ", data.y);
    socket.broadcast.to(user.room).emit('undo');

  });

  socket.on('clear', () => {
    const user = getCurrentUser(socket.id);
    // console.log("스타트포인트 2 : ", data.x, " ", data.y);
    socket.broadcast.to(user.room).emit('clear');

  });

  socket.on('gameStart', () => {
    const user = getCurrentUser(socket.id);
    const room = getRoom(user.room);
    console.log("게임스타트 소켓 + room 네임: ", room, " user: ", user);
    io.to(user.room).emit('gameStart');
    socket.broadcast.to(user.room).emit('guessStart');
    db.query("select * from words", (err, word) =>{
      const rand = Math.floor(Math.random() * (word.length));
      room.answer = word[rand].word;
      console.log("랜덤 단어: ", word[rand].word);
      socket.emit("drawStart", room.answer);
    })
   
  });

  socket.on('redCard', (exit_user) => {
    const user = getCurrentUser(socket.id);
    const cur_room = getRoom(user.room);
    const user_object = getUserbyname(exit_user);
    // cur_room.userleave(exit_user);
    // userLeave(user_object.id);
    console.log("강퇴 당하는 유저 : ", user_object);
    let clientSocket = io.sockets.sockets.get(user_object.id);
    clientSocket.emit("redCard");

    
  });




    
  });

exports.app = app;