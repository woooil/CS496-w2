package com.example.app_server;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomActivity extends AppCompatActivity {
  
  private TextView nicknameTV, msgBox, roomTV;
  private EditText msgET;
  private Button logoutBT, msgBT;
  private UsersModal user;
  private final String BASE_URL = "http://192.249.18.204";
  private String room;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room);
  
    Intent i = getIntent();
    room = i.getStringExtra("Room");
    
    if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
      finish();
      startActivity(new Intent(this, MainActivity.class));
    }
    
    user = SharedPrefManager.getInstance(this).getUser();
    nicknameTV = findViewById(R.id.nicknameTV);
    logoutBT = findViewById(R.id.logoutBT);
    msgBT = findViewById(R.id.msgBT);
    msgET = findViewById(R.id.msgET);
    msgBox = findViewById(R.id.msgBox);
    roomTV = findViewById(R.id.roomTV);
    
    nicknameTV.setText(user.getNickname() + "!");
    roomTV.setText(room);
    
    logoutBT.setOnClickListener(v -> {
      SharedPrefManager.getInstance(this).logout();
    });
    
    final Socket socket = IO.socket(URI.create(BASE_URL));
    socket.connect();
    Log.d(null, "socket created!");
    socket.emit("hello", user.getNickname());
    Log.d(null, "socket emitted!");
    
    String username = user.getNickname().toString();
    JSONObject new_user = new JSONObject();
    try {
      new_user.put("username", username);
      new_user.put("room", room);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    socket.emit("joinRoom", new_user);
//    socket.on("roomUsers", new Emitter.Listener() {
//      @Override
//      public void call(Object... args) {
//        outputRoomName(args[0].toString());
//        outputUsers(args[1].toString());
//      }
//    });
    socket.on("message", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        outputMessage(args[0].toString());
      }
    });
    
    msgBT.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        socket.emit("chatMessage", msgET.getText().toString());
        msgET.setText("");
      }
    });
  }
  
  private void outputMessage(String message) {
    Log.d(null, message);
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        msgBox.setText(message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
      }
    });
  }

//  private void outputRoomName(String room) {
//    Toast.makeText(getApplicationContext(), "You are currently in room " + room, Toast.LENGTH_SHORT).show();
//  }

//  private void outputUsers(String users) {
//    Toast.makeText(getApplicationContext(), "Current users: " + users, Toast.LENGTH_SHORT).show();
//  }
}