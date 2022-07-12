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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ListActivity extends AppCompatActivity {
  private TextView nicknameTV;
  private Button room1BT, room2BT, logoutBT;
  private UsersModal user;
  private final String BASE_URL = "http://192.249.18.196";
  private static Socket socket;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    
    nicknameTV = findViewById(R.id.nicknameTV);
    room1BT = findViewById(R.id.room1BT);
    room2BT = findViewById(R.id.room2BT);
    logoutBT = findViewById(R.id.logoutBT);
    
    if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
      finish();
      startActivity(new Intent(this, MainActivity.class));
    }
  
    user = SharedPrefManager.getInstance(this).getUser();
    
    socket = IO.socket(URI.create(BASE_URL));
    socket.connect();
    Log.d(null, "socket created!");
    socket.emit("hello", user.getNickname());
    Log.d(null, "socket emitted!");
    
    socket.emit("howMany");
    socket.on("howMany", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        try {
          JSONArray arr = (JSONArray) args[0];
          List<Integer> list = new ArrayList<Integer>();
          for(int i = 0; i < arr.length(); i++){
            list.add(arr.getInt(i));
          }
          room1BT.setText("ROOM 1 (" + list.get(0) + "/4)");
          room2BT.setText("ROOM 2 (" + list.get(1) + "/4)");
          Log.d(null, "TYPEIS: " + args[0].getClass());
          Log.d(null, "STINRG: " + args[0]);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });

    nicknameTV.setText(user.getNickname());
    room1BT.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(ListActivity.this, RoomActivity.class);
        i.putExtra("Room", "ROOM1");
        startActivity(i);
      }
    });
    room2BT.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(ListActivity.this, RoomActivity.class);
        i.putExtra("Room", "ROOM2");
        startActivity(i);
      }
    });
    logoutBT.setOnClickListener(v -> {
      SharedPrefManager.getInstance(this).logout();
    });
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    socket.disconnect();
  }
  
  public static Socket getSocket() { return socket; }
}