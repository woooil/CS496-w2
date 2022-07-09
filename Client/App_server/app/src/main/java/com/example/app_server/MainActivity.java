package com.example.app_server;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity {
    private TextView signUpTV;
    private EditText userIdET, passwordET;
    private TextView submitTV;
    private TextView kakaoTV;
    private final String BASE_URL = "http://192.249.18.196";

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
        kakaoTV = findViewById(R.id.kakaoTV);

        // Click event for sign up button
        signUpTV.setOnClickListener(v -> {
            Intent i = new Intent(this, SignupActivity.class);
            this.startActivity(i);
        });


        kakaoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(getApplicationContext()))
                    UserApiClient.getInstance().loginWithKakaoTalk(getApplicationContext(), callback);
                else UserApiClient.getInstance().loginWithKakaoAccount(getApplicationContext(), callback);
            }
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

    Function2<OAuthToken, Throwable, Unit> callback = (oAuthToken, throwable) -> {
        if (oAuthToken != null) {
            Log.i("[카카오] 로그인", "성공");
            updateKakaoLogin();
        }
        if (throwable != null) {
            Log.i("[카카오] 로그인", "실패");
            Log.e("signInKakao()", throwable.getLocalizedMessage());
        } return null;
    };

    private void updateKakaoLogin() {
        UserApiClient.getInstance().me((user, throwable) -> {
            if (user != null) {
                // @brief : 로그인 성공
                Log.i("[카카오] 로그인 정보", user.toString());
                // @brief : 로그인한 유저의 email주소와 token 값 가져오기. pw는 제공 X

                Log.i("[카카오] 로그인 정보", user.getKakaoAccount().getEmail());
            } else {
                // @brief : 로그인 실패

            }
            return null;
        });
    }
}


