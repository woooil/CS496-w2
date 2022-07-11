package com.example.app_server;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
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
  
  private TextView msgBox, roomTV, wordTV;
  private LinearLayout[] plLL = new LinearLayout[4];
  private TextView[] plIcon = new TextView[4];
  private TextView[] plChat = new TextView[4];
  private EditText msgET;
  private Button msgBT, startBT;
  private UsersModal user;
  private final String BASE_URL = "http://192.249.18.196";
  private String room;
  private DrawView paint;
  private Button clear, color, stroke, undo;
  private RangeSlider rangeSlider;
  private Socket socket;
  private Boolean isArtist = false;
  private LinearLayout drawingOptionLL, msgLL;
  
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
    undo = findViewById(R.id.btn_undo);
    clear = findViewById(R.id.btn_clear);
    color = findViewById(R.id.btn_color);
    stroke = findViewById(R.id.btn_stroke);
    startBT = findViewById(R.id.startBT);
    drawingOptionLL = findViewById(R.id.drawingOptionLL);
    msgLL = findViewById(R.id.msgLL);
    wordTV = findViewById(R.id.wordTV);
    
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
        outputMessage((JSONObject) args[0]);
      }
    });
  
    socket.on("drawing", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
//        paint.drawMove(args);
        JSONObject data = (JSONObject) args[0];
        try {
          Double x = Double.parseDouble(String.valueOf(data.get("x")));
          Double y = Double.parseDouble(String.valueOf(data.get("y")));
          paint.drawMove(x.floatValue(), y.floatValue());
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
          Log.d(null, "DRAWSTART" + data);
          Double x = Double.parseDouble(String.valueOf(data.get("x")));
          Double y = Double.parseDouble(String.valueOf(data.get("y")));
          int color = Integer.parseInt(data.get("color").toString());
          int width = Integer.parseInt(data.get("width").toString());
          paint.setColor(color);
          paint.setStrokeWidth(width);
          paint.drawStart(x.floatValue(), y.floatValue());
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
    
    socket.on("undo", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        paint.undoDrawing();
      }
    });
    
    socket.on("clear", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        paint.clearDawing();
      }
    });
    
    socket.on("drawStart", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        String word = (String) args[0];
        drawStart(word);
      }
    });
    
    socket.on("guessStart", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        guessStart();
      }
    });
    
    socket.on("correct", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            String winner = args[0].toString();
            Toast.makeText(RoomActivity.this, winner + "님이 정답을 맞췄습니다!", Toast.LENGTH_SHORT).show();
            updateDrawState(false);
            if (user.getNickname().equals(winner))
              startBT.setVisibility(View.VISIBLE);
          }
        });
      }
    });
    
    msgET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
          sendChat();
          return true;
        }
        return false;
      }
    });
    
    msgBT.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendChat();
      }
    });
    
    startBT.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        socket.emit("gameStart");
        startBT.setVisibility(View.INVISIBLE);
      }
    });
    
    socket.on("gameStart", new Emitter.Listener() {
      @Override
      public void call(Object... args) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(RoomActivity.this, "게임이 시작되었습니다!", Toast.LENGTH_SHORT).show();
            paint.clear();
          }
        });
      }
    });
  
    undo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        paint.undo();
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
    clear.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        paint.clear();
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
  
  
  
  private void outputMessage(JSONObject message) {
    Log.d(null, message.toString());
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        try {
          String name = message.get("username").toString();
          String text = message.get("text").toString();
          String msg = msgBox.getText().toString();
          String msgReceived;
          if (name.equals("BOT")) msgReceived = "\n" + text;
          else msgReceived = "\n[" + name + "] " + text;
          String newMsg = msg.substring(msg.indexOf("\n") + 1) + msgReceived;
          msgBox.setText(newMsg);
          if (name.equals("BOT")) return;
          for (int i = 0; i < 4; i++) {
            if (plIcon[i].getText().toString().equals(name)) {
              plChat[i].setText(text);
              return;
            }
          }
          Toast.makeText(RoomActivity.this, "Message error!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
          e.printStackTrace();
        }
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
          isArtist = true;
          startBT.setVisibility(View.VISIBLE);
        } else {
          isArtist = false;
          startBT.setVisibility(View.INVISIBLE);
        }
      }
    });
  }
  
  private void drawStart(String word) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        startBT.setVisibility(View.INVISIBLE);
        wordTV.setText(word);;
        updateDrawState(true);
      }
    });
  }
  
  private void guessStart() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        updateDrawState(false);
      }
    });
  }
  
  private void updateDrawState(Boolean draw) {
    if (draw) {
      paint.setIsAllowedToDraw(true);
      drawingOptionLL.setVisibility(View.VISIBLE);
      msgLL.setVisibility(View.INVISIBLE);
      wordTV.setVisibility(View.VISIBLE);
    } else {
      paint.setIsAllowedToDraw(false);
      drawingOptionLL.setVisibility(View.INVISIBLE);
      msgLL.setVisibility(View.VISIBLE);
      wordTV.setVisibility(View.INVISIBLE);
    }
  }
  
  private void sendChat() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        socket.emit("chatMessage", msgET.getText().toString());
        msgET.setText("");
        msgET.clearFocus();
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