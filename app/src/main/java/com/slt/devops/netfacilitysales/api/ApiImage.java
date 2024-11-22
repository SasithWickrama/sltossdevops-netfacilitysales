package com.slt.devops.netfacilitysales.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import com.slt.devops.netfacilitysales.models.UpdateResponse;

public interface ApiImage {


    String BASE_URL = "https://serviceportal.slt.lk/ApiSales/ImageApi/";


    @Multipart
    @POST("ApiImage.php?apicall=upload")
    Call<UpdateResponse> uploadImage(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file,
                                     @Part("desc") RequestBody desc,
                                     @Part("location") RequestBody location);

}
