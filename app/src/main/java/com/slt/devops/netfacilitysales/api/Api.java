package com.slt.devops.netfacilitysales.api;


import com.slt.devops.netfacilitysales.models.EquipmentResponse;
import com.slt.devops.netfacilitysales.models.LoginResponse;
import com.slt.devops.netfacilitysales.models.UpdateResponse;
import com.slt.devops.netfacilitysales.models.VersionResponse;
import com.slt.devops.netfacilitysales.models.networkfacility.LandlineResponce;


import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST("getCode")
    Call<LoginResponse> getCode(
            @Field("mobile") String sid,
            @Field("appversion") String appversion
    );

    @FormUrlEncoded
    @POST("checkCode")
    Call<LoginResponse> checkCode(
            @Field("mobile") String sid,
            @Field("passcode") String passcode,
            @Field("appversion") String appversion
    );


    @FormUrlEncoded
    @POST("checkVersion")
    Call<VersionResponse> checkVersion(
            @Field("sid") String sid,
            @Field("appversion") String appversion
    );

    @FormUrlEncoded
    @POST("userLogin")
    Call<LoginResponse> userLogin(
            @Field("sid") String sid,
            @Field("pwd") String pwd,
            @Field("mobile") String mobile,
            @Field("appversion") String appversion
    );






//NETWORK FACILITY
   @FormUrlEncoded
   @POST("getDPlist")
   Call<EquipmentResponse> getDPlist(
           @Field("sid") String sid,
           @Field("rtom") String rtom,
           @Field("eqtype") String eqtype,
           @Field("lat") String lat,
           @Field("lon") String lon
   );

   @FormUrlEncoded
   @POST("getNOlist")
   Call<LandlineResponce> getNOlist(
           @Field("sid") String sid,
           @Field("rtom") String rtom,
           @Field("svtype") String svtype,
           @Field("notype") String notype,
           @Field("noarea") String noarea,
           @Field("nostart") String nostart,
           @Field("noend") String noend,
           @Field("nolea") String nolea
   );

   @FormUrlEncoded
   @POST("uploadNetFacility")
   Call<UpdateResponse> uploadNetFacility(
           @Field("sid") String sid,
           @Field("rtom") String rtom,
           @Field("svtype") String svtype,
           @Field("dplocation") String dplocation,
           @Field("dp") String dp,
           @Field("dpindex") String dpindex,
           @Field("dpdistance") String dpdistance,
           @Field("reservedno") String reservedno,
           @Field("cushon") String cushon,
           @Field("cusname") String cusname,
           @Field("email") String email,
           @Field("tp") String tp,
           @Field("nic") String nic,
           @Field("address") String address,
           @Field("cuslon") String cuslon,
           @Field("cuslat") String cuslat,
           @Field("bbpkg") String bbpkg,
           @Field("iptvpkg") String iptvpkg
   );

    @Multipart
    @POST("uploadNetFacilitywithImage")
    Call<UpdateResponse> uploadNetFacilitywithImage(
            @Part("sid") String sid,
            @Part("rtom") String rtom,
            @Part("svtype") String svtype,
            @Part("dplocation") String dplocation,
            @Part("dp") String dp,
            @Part("dpindex") String dpindex,
            @Part("dpdistance") String dpdistance,
            @Part("reservedno") String reservedno,
            @Part("cushon") String cushon,
            @Part("cusname") String cusname,
            @Part("email") String email,
            @Part("tp") String tp,
            @Part("nic") String nic,
            @Part("address") String address,
            @Part("cuslon") String cuslon,
            @Part("cuslat") String cuslat,
            @Part MultipartBody.Part[] imagearray
    );


}



