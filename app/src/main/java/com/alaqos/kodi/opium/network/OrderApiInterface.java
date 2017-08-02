package com.alaqos.kodi.opium.network;

/**
 * Created by kodi on 6/16/2017.
 */


import com.alaqos.kodi.opium.model.Order;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;


public interface OrderApiInterface {

    /*private int id;
    private String from;
    private String subject;
    private String message;
    private String timestamp;
    private String picture;
    private boolean isImportant;
    private boolean isRead;
    private int color = -1;*/

    @FormUrlEncoded
    @POST("api/order/create")
    Call<Order> create(@Field("from") String from,
                       @Field("reference") String reference,
                       @Field("montant") int montant,
                       @Field("paye") int paye,
                       @Field("reste") int reste,
                       @Field("timestamp") String timestamp,
                       @Field("picture") String picture,
                       @Field("status") boolean status);


    @FormUrlEncoded
    @POST("api/order/update/{id}")
    Call<Order> update(
            @Path("id") String id,
            @Field("from") String from,
                       @Field("reference") String reference,
                       @Field("montant") int montant,
                       @Field("paye") int paye,
                       @Field("reste") int reste,
                       @Field("timestamp") String timestamp,
                       @Field("picture") String picture,
                       @Field("status") boolean status);

}
