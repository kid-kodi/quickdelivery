package com.alaqos.kodi.opium.network;

/**
 * Created by Dosso on 7/13/2017.
 */

import com.alaqos.kodi.opium.model.Order;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @GET("api/order/list")
    Call<List<Order>> getInbox(@QueryMap Map<String, String> options);

}
