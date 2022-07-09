package com.example.app_server;

import android.content.Intent;
import android.database.Observable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
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
    private EditText userIdET, passwordET;
    private TextView submitTV;
    private final String BASE_URL = "http://192.249.18.204";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ListActivity.class));
            return;
        }

        signUpTV = findViewById(R.id.signUpTV);
        userIdET = findViewById(R.id.idET);
        passwordET = findViewById(R.id.passwordET);
        submitTV = findViewById(R.id.submitTV);

        // Click event for sign up button
        signUpTV.setOnClickListener(v -> {
            Intent i = new Intent(this, SignupActivity.class);
            this.startActivity(i);
        });

        submitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.MINUTES)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

                Call<Object> call = jsonPlaceHolderApi.login(
                    userIdET.getText().toString(),
                    passwordET.getText().toString());

                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {

                            String result = response.body().toString();

                            if (result.contains("id")) {
                                Log.d(null, "Log in succeed: " + result);
                                Toast.makeText(getApplicationContext(), "Log in succeed!", Toast.LENGTH_LONG).show();
                                JSONParser parser = new JSONParser();
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    UsersModal user = new UsersModal(
                                        obj.get("user_id").toString(),
                                        obj.get("email").toString(),
                                        obj.get("nickname").toString()
                                    );
                                    
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                   
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), ListActivity.class));
                                } catch (JSONException e) {
                                    Log.e(null, "JSON error occurred: " + e);
                                }
                            }
                            else {
                                Log.d(null, "Log in failed: " + result);
                                Toast.makeText(getApplicationContext(), "Log in failed!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    });
            }
        });
    }
}


