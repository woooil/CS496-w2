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
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
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


        kakaoTV.setOnClickListener(v -> {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(this, kakaoCallback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(this, kakaoCallback);
                }
        });

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

    Function2<OAuthToken, Throwable, Unit> kakaoCallback = new Function2<OAuthToken, Throwable, Unit>() {
        @Override
        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
            if (oAuthToken != null) {

            }
            if (throwable != null) {

                Log.d("펑션", "Message : " + throwable.getLocalizedMessage());
            }
            getKaKaoProfile();
            return null;
        }
    };

    private void getKaKaoProfile() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) { //success
                    Log.d("qnffjdh", "Kakao 이름 =" + user.getKakaoAccount().getProfile().getNickname());
                    String nick = user.getKakaoAccount().getProfile().getNickname().toString();

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

                    Call<Object> call = jsonPlaceHolderApi.kakaosignup(nick);

                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if(response.body().toString().equals("회원가입이 완료되었습니다.")){
                                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    });
                }
                if (throwable != null) {
                    Log.d("인보크", "invoke: " + throwable.getLocalizedMessage());
                }
                return null;
            }
        });
    }
}


