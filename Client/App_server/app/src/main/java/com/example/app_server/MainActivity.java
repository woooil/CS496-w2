package com.example.app_server;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private TextView signUpTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Click event for sign up button
        signUpTV = findViewById(R.id.signUpTV);
        signUpTV.setOnClickListener(v -> {
            Intent i = new Intent(this, SignupActivity.class);
            this.startActivity(i);
        });
    }
}


