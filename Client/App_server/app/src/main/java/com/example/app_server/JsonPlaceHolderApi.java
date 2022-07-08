package com.example.app_server;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {
    @GET("/api")
    Call<Object> getPosts();

    @POST("/auth/register")
    Call < Object > signup(@Body UserInfo info);
}