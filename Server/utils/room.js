const { all } = require("../routes");

const allroom = [];

class Room {
    constructor(name, artist) {
        this.name = name;
        this.all_player = [];
        this.artist = artist;
        this.answer = null;
    }

    userjoin(new_user) {
        this.all_player.push(new_user);
    }
    userleave(exit_user){
        const index = this.all_player.findIndex(user => user === exit_user.username);
        
        if (index !== -1) {
            return this.all_player.splice(index, 1)[0];
        }
    }

    setartist(){
        if(this.all_player.length != 0 ) // 인원이 남아있을 때
        this.artist = this.all_player[0];
        else // 인원이 남아있지 않을 
        this.artist = null;
    }

    checkemptyplayer(){
        return this.all_player.length === 0;
    }
}

function Roomcreate(new_room) {
    allroom.push(new_room);
}

function getRoom(room_name){
    return allroom.find(room => room.name === room_name);
}

function UserJoinRoom(username, alreadyexist_room){
    alreadyexist_room.userjoin(username);
}

function InspectArtist(room_name, username){
    const room = getRoom(room_name);
    return room.artist == username;
}

function printallroom(){
    console.log("방 전체 출력!!");
    for(let i = 0; i < allroom.length; i++){
        console.log("룸 네임: ", allroom[i].name);
        for(let j = 0; j < allroom[i].all_player.length; j++){
            console.log("룸 모든 플레이어: ", allroom[i].all_player);
        }
    }
    
}

function DeleteRoom(room_name){
    const index = allroom.findIndex(room => room.name === room_name);

    if (index !== -1) {
        allroom.splice(index, 1)[0];
    }
    console.log("방 삭제 성공");
}

function howManypeople(){
    let howMany = [];
 
        if (allroom[0] == null)
        {
            howMany = [0,0];
        }
        else{
            if(allroom[0].name == "ROOM2"){
                howMany = [0, allroom[0].all_player.length];
            }
            else{
                if (allroom[1] == null)
                howMany = [allroom[0].all_player.length, 0];
                else
                    howMany = [allroom[0].all_player.length, allroom[1].all_player.length];
            }
        }
    console.log("how many ?? : ", howMany);
    return howMany;
}



module.exports ={
    Room,
    Roomcreate,
    getRoom,
    UserJoinRoom,
    printallroom,
    InspectArtist,
    DeleteRoom,
    howManypeople
}