# minwooil


## Team
- KAIST School of Computing 김우일
- POSTECH Computer Science Engineering 변민우

## Overview
....

<br>

## Development Env

`Client`
- OS: Android (minSdk: 26, targetSdk: 32)
- Language: Java
- IDE: Android Studio
- Target Device: Galaxy S7

`Server`
- Stack: Nodejs
- Language: JavaScript
- Framework: Express
- Database: MySQL
- Package : Socket.IO, bcrypt(for hashed password), 

## Feature

## App 


## Database
`users`
|Field|Type|Null|Key|Default|Extra|
|------|---|---|----|------|------|
|id| int(11)|NO|PRI|NULL|auto_increment|
|user_id| varchar(30)|NO| |NULL||
|pwd|varchar(500)|NO| |NULL||
|email| varchar(30)|NO| |NULL||
|nickname| varchar(30)|NO|  |NULL||



## REST API

- `POST`
<br> /auth/check  - Check the duplicate ID

- `POST`
<br> /auth/register  - Sign up & check Password validation 

- `POST`
<br> /auth/login  - check the presence of a user in DB & Sign in

## Socket Programming

`Server`

    socket.on('joinRoom', (new_user) => {
        socket.join(user.room);
        io.to(user.room).emit("information", room_info);
        socket.emit('message', formatMessage(botName, '환영합니다'));
        socket.broadcast.to(user.room).emit('message', formatMessage(botName, `${user.username} 님이 입장하였습니다.`));
    }
      
    ///////////////////////////////////////////////////////////////////
     socket.on('disconnect', () => {
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





