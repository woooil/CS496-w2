# minwooil


## Team
* * *
- KAIST School of Computing 김우일
- POSTECH Computer Science Engineering 변민우

<br>

## Overview
* * *
This app is almost the same as the Catchmind game. This app Supports multiple users through socket programming

<br>

## Development Env
* * *

`Client`
- OS: Android (minSdk: 21, targetSdk: 33)
- Language: Java
- IDE: Android Studio
- Target Device: Galaxy S7

`Server`
- Stack: Nodejs
- Language: JavaScript
- Framework: Express
- Database: MySQL
- Package : Socket.IO, bcrypt(for hashed password), 

<br>

## App 
* * *


홈 화면
- check mulitple id and nickname
- kakao login by using kakao SDK
- store hashed password in Database
- Automatic login using SharedPrefManager


리스트 화면
- It shows the real-time number of people in each room
- cannot join room if the game is in the room.
- cannot join room if the number of users in the room is 4 or more
- User can play Catchmind with others in each room

게임 화면
- It shows drawing in real-time
- Only artist can press the 'Start' button.
- Artist can control pen Color and Width
- The user except the artist can send the right answer.
- The user who get the right answer will be the next artist.
- Artist can banish user.


<br>

## Database
* * *
`users`
|Field|Type|Null|Key|Default|Extra|
|------|---|---|----|------|------|
|id| int(11)|NO|PRI|NULL|auto_increment|
|user_id| varchar(30)|YES| |NULL||
|pwd|varchar(500)|YES| |NULL||
|email| varchar(30)|YES| |NULL||
|nickname| varchar(30)|NO|  |NULL||


<br>

## REST API
* * *

- `POST`
<br> /auth/check  - Check the duplicate ID

- `POST`
<br> /auth/nickname  - Check the duplicate nickname

- `POST`
<br> /auth/register  - Sign up & check Password validation 

- `POST`
<br> /auth/kakosignup  - kakao login

- `POST`
<br> /auth/login  - check the presence of a user in DB & Sign in


<br>

## Socket Programming
* * *

`Server`

    socket.on('joinRoom', (new_user) => {
        socket.join(user.room);
        io.to(user.room).emit("information", room_info);
        socket.emit('message', formatMessage(botName, '환영합니다'));
        socket.broadcast.to(user.room).emit('message', formatMessage(botName, `${user.username} 님이 입장하였습니다.`));
    }
      
    ///////////////////////////////////////////////////////////////////
     socket.on('quit', () => {
      socket.broadcast.to(exit_room.name).emit("information", room_info);
      io.to(user.room).emit('message', formatMessage(botName, `${user.username} 님이 퇴장하였습니다.`));
    });
      
    ///////////////////////////////////////////////////////////////////
    socket.on('chatMessage', (msg) => {
      io.to(user.room).emit('message', formatMessage(user.username, msg));
      io.to(user.room).emit('correct', user.username);
    });

    ///////////////////////////////////////////////////////////////////
     socket.on('drawing', (data) => {
        socket.broadcast.to(user.room).emit('drawing', data);
    });
    
    ///////////////////////////////////////////////////////////////////
    socket.on('drawingStart', (data) => {
        socket.broadcast.to(user.room).emit('drawingStart', data);
    });
    
    ///////////////////////////////////////////////////////////////////
    socket.on('undo', () => {
        socket.broadcast.to(user.room).emit('undo');
    });

    ///////////////////////////////////////////////////////////////////
    socket.on('clear', () => {
        socket.broadcast.to(user.room).emit('clear');
    });
    
    ///////////////////////////////////////////////////////////////////
    socket.on('gameStart', () => {
        io.to(user.room).emit('gameStart');
        socket.broadcast.to(user.room).emit('guessStart');
        socket.emit("drawStart", room.answer);
    });
    ///////////////////////////////////////////////////////////////////
    socket.on('redCard', (exit_user) => {
        clientSocket.emit("redCard");
     });






