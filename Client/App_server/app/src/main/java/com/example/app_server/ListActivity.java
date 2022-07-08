package com.example.app_server;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {
  private TextView nicknameTV;
  private UsersModal user;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    
    user = getIntent().getParcelableExtra("user");
    nicknameTV = findViewById(R.id.nicknameTV);
    
    nicknameTV.setText(user.getNickname() + "!");
  }
}