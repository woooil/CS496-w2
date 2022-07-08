package com.example.app_server;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {
    @GET("/api")
    Call<Object> getPosts();
}