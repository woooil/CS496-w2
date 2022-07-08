package com.example.app_server;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_server.databinding.ActivitySignupBinding;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    private Button adduser;
    private Button idcheck;
    private final String BASE_URL = "https://1226-192-249-19-234.jp.ngrok.io";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText user_id = (EditText)findViewById(R.id.user_id);
        EditText pwd1 = (EditText)findViewById(R.id.pwd1);
        EditText pwd2 = (EditText)findViewById(R.id.pwd2);
        EditText email = (EditText)findViewById(R.id.email);
        EditText nickname = (EditText)findViewById(R.id.nickname);

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


        idcheck = (Button) this.findViewById(R.id.idCheck);
        idcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<Object> call = jsonPlaceHolderApi.check(user_id.getText().toString());

                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


            }
        });



        adduser = (Button) this.findViewById(R.id.add_user);
        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<Object> call = jsonPlaceHolderApi.signup(
                        new UserInfo(user_id.getText().toString(),
                                pwd1.getText().toString(),
                                pwd2.getText().toString(),
                                email.getText().toString(),
                                nickname.getText().toString()));

                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
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