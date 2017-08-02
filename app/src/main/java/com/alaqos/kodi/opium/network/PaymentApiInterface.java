package com.alaqos.kodi.opium.network;

/**
 * Created by Dosso on 7/13/2017.
 */

import com.alaqos.kodi.opium.model.Payment;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface PaymentApiInterface {

    @GET("api/payment/list")
    Call<List<Payment>> getList(@QueryMap Map<String, String> options);

    @FormUrlEncoded
    @POST("api/payment/create")
    Call<Payment> create(@Field("orderId") String orderId,
                       @Field("montant") int montant,
                       @Field("paiementModeId") String paiementModeId,
                       @Field("libelle") String libelle,
                       @Field("lieu") String lieuId,
                       @Field("reference") String referenceId,
                       @Field("createdAt") String createdAt,
                         @Field("status") boolean status);

}
