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
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ListActivity extends AppCompatActivity {
  private TextView nicknameTV;
  private Button room1BT;
  private UsersModal user;

  private final String BASE_URL = "http://192.249.18.196";
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    
    nicknameTV = findViewById(R.id.nicknameTV);
    room1BT = findViewById(R.id.room1BT);
  

    final Socket socket = IO.socket(URI.create(BASE_URL));
    socket.connect();
    Log.d(null, "socket created!");
    socket.emit("hello", user.getNickname());
    Log.d(null, "socket emitted!");
    
    String username = user.getNickname().toString();
    String room = "ROOM0";
    Object temp = new String[]{
        username, room
    };
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

    user = SharedPrefManager.getInstance(this).getUser();
    nicknameTV.setText(user.getNickname());
    room1BT.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Intent i = new Intent(ListActivity.this, RoomActivity.class);
        i.putExtra("Room", "ROOM1");
        startActivity(i);
      }
    });
  }
}