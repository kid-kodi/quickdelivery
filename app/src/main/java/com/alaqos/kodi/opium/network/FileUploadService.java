package com.alaqos.kodi.opium.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by kodi on 6/24/2017.
 */

public interface FileUploadService {
    @Multipart
    @POST("api/upload")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);
}
