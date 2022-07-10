package com.example.app_server;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import petrov.kristiyan.colorpicker.ColorPicker;

public class RoomActivity extends AppCompatActivity {
  
  private TextView msgBox, roomTV;
  private LinearLayout[] plLL = new LinearLayout[4];
  private TextView[] plIcon = new TextView[4];
  private TextView[] plChat = new TextView[4];
  private EditText msgET;
  private Button msgBT, startBT;
  private UsersModal user;
  private final String BASE_URL = "http://192.249.18.196";
  private String room;
  private DrawView paint;
  private ImageButton save, color, stroke, undo;
  private RangeSlider rangeSlider;
  private Socket socket;
  
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
    msgBT = findViewById(R.id.msgBT);
    msgET = findViewById(R.id.msgET);
    msgBox = findViewById(R.id.msgBox);
    roomTV = findViewById(R.id.roomTV);
    paint = (DrawView) findViewById(R.id.draw_view);
    rangeSlider = (RangeSlider) findViewById(R.id.rangebar);
    undo = (ImageButton) findViewById(R.id.btn_undo);
    save = (ImageButton) findViewById(R.id.btn_save);
    color = (ImageButton) findViewById(R.id.btn_color);
    stroke = (ImageButton) findViewById(R.id.btn_stroke);
    startBT = findViewById(R.id.startBT);
    
    plLL[0] = findViewById(R.id.p1LL);
    plLL[1] = findViewById(R.id.p2LL);
    plLL[2] = findViewById(R.id.p3LL);
    plLL[3] = findViewById(R.id.p4LL);
    
    plIcon[0] = findViewById(R.id.p1Icon);
    plIcon[1] = findViewById(R.id.p2Icon);
    plIcon[2] = findViewById(R.id.p3Icon);
    plIcon[3] = findViewById(R.id.p4Icon);
    plIcon[0].setText(user.getNickname());
    
    plChat[0] = findViewById(R.id.p1Chat);
    plChat[1] = findViewById(R.id.p2Chat);
    plChat[2] = findViewById(R.id.p3Chat);
    plChat[3] = findViewById(R.id.p4Chat);
    
    roomTV.setText(room);
    
    socket = IO.socket(URI.create(BASE_URL));
    socket.connect();
    Log.d(null, "socket created!");
    socket.emit("hello", user.getNickname());
    Log.d(null, "socket emitted!");
    
    socket.on("information", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        JSONObject data = (JSONObject) args[0];
        try {
          JSONArray all_player = (JSONArray) data.get("all_player");
          List<String> exampleList = new ArrayList<String>();
          for (int i = 0; i < all_player.length(); i++) {
            exampleList.add(all_player.getString(i));
          }
          int size = exampleList.size();
          String[] players = exampleList.toArray(new String[size]);
          String artist = data.get("artist").toString();
          Log.d(null, "INFODD: " + players + " AND " + artist);
          updatePlayers(players, artist);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
    
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
  
    socket.on("drawing", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
//        paint.drawMove(args);
        JSONObject data = (JSONObject) args[0];
        try {
          Double x = (Double) data.get("x");
          Double y = (Double) data.get("y");
          paint.drawMove(x.floatValue(), y.floatValue());
          paint.invalidate();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
    
    socket.on("drawingStart", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        JSONObject data = (JSONObject) args[0];
        try {
          Double x = (Double) data.get("x");
          Double y = (Double) data.get("y");
//          paint.draw(paint.getmCanvas());
          paint.drawStart(x.floatValue(), y.floatValue());
          paint.invalidate();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
    
    msgBT.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        socket.emit("chatMessage", msgET.getText().toString());
        msgET.setText("");
      }
    });
    
    startBT.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        socket.emit("gameStart");
        startBT.setVisibility(View.INVISIBLE);
      }
    });
  
    color.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        final ColorPicker colorPicker = new ColorPicker(RoomActivity.this);
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
              @Override
              public void setOnFastChooseColorListener(int position, int color) {
                // get the integer value of color
                // selected from the dialog box and
                // set it as the stroke color
                paint.setColor(color);
              }
              @Override
              public void onCancel() {
                colorPicker.dismissDialog();
              }
            })
            // set the number of color columns
            // you want  to show in dialog.
            .setColumns(5)
            // set a default color selected
            // in the dialog
            .setDefaultColorButton(Color.parseColor("#000000"))
            .show();
      }
    });
    stroke.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (rangeSlider.getVisibility() == View.VISIBLE)
          rangeSlider.setVisibility(View.GONE);
        else
          rangeSlider.setVisibility(View.VISIBLE);
      }
    });
  
    rangeSlider.setValueFrom(0.0f);
    rangeSlider.setValueTo(100.0f);
    
    rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
      @SuppressLint("RestrictedApi")
      @Override
      public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
        paint.setStrokeWidth((int) value);
      }
    });
    
    paint.setSocket(socket);
    ViewTreeObserver vto = paint.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int width = paint.getMeasuredWidth();
        int height = paint.getMeasuredHeight();
        paint.init(height, width);
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
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    socket.disconnect();
  }
  
  private void updatePlayers(String[] players, String artist) {
    int size = players.length;
    int userPosition = Arrays.asList(players).indexOf(user.getNickname());
    String temp = players[userPosition];
    players[userPosition] = players[0];
    players[0] = temp;
    
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        for (int i = 1; i < size; i++) {
          plLL[i].setVisibility(View.VISIBLE);
          plIcon[i].setText(players[i]);
        }
        for (int i = size; i < 4; i++) {
          plLL[i].setVisibility(View.INVISIBLE);
          plIcon[i].setText("");
        }
  
        if (user.getNickname().equals(artist)) {
          startBT.setVisibility(View.VISIBLE);
        } else {
          startBT.setVisibility(View.INVISIBLE);
        }
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