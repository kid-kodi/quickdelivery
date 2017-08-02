package com.alaqos.kodi.opium.network;

/**
 * Created by kodi on 6/16/2017.
 */

import com.alaqos.kodi.opium.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;


public interface UserApiInterface {
    @GET("api/user/list")
    Call<List<User>> getList(@QueryMap Map<String, String> options);

    @GET("api/user/read/{id}")
    Call<User> getOne(@Path("id") int id);

    @FormUrlEncoded
    @POST("user/create")
    Call<User> create(@Field("first_name") String first,
                      @Field("last_name") String last,
                      @Field("email") String email,
                      @Field("password") String password);
}
