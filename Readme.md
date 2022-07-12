# minwooil


## Team
* * *
- KAIST School of Computing 김우일
- POSTECH Computer Science Engineering 변민우

<br>

## Overview
* * *
This app is almost the same as Catch Mind game of Netmarble. 
<br>This app supports multiple users through socket programming.

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
- Package : Socket.IO, bcrypt(for hashed password)

<br>

## App 
* * *
<div style = "text-align: center;">
<image src = "./Server/public/images/loginpage.jpg" width ="250dp"> 
<image src = "./Server/public/images/signuppage.jpg" width ="250dp"> 
</div>

Login & Signup Page
- Checks duplicate id and nickname.
- Login with Kakao by using Kakao SDK.
- Stores hashed passwords in a database.
- Supports automatic login using SharedPrefManager.

<br>
<div style = "text-align: center;">
<image src = "./Server/public/images/listpage.jpg" width ="250dp"> 
</div>

List Page
- Shows the number of people in each room in real-time.
- Does not allow to join room if the game is in the room.
- Does not allow to join room if the number of users in the room is 4 or more.
- Users can play Catchmind game with others in each room.

<br>

<br>
<div style = "text-align: center;">
<image src = "./Server/public/images/beforestart.jpg" width ="250dp"> 
<image src = "./Server/public/images/playing.jpg" width ="250dp"> 
</div>

Room page

- Shows drawing in real-time.
- Only artist can press the 'Start' button.
- Artist can control pen color and width and can undo or clear drawing.
- Users except the artist can send the answer.
- Users who get the right answer will be the next artist.
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






