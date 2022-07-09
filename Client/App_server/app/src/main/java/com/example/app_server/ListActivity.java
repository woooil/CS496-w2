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

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ListActivity extends AppCompatActivity {
  private TextView nicknameTV;
  private Button room1BT;
  private UsersModal user;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    
    nicknameTV = findViewById(R.id.nicknameTV);
    room1BT = findViewById(R.id.room1BT);
  
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