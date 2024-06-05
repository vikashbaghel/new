package com.app.rupyz.generic.network;

import com.app.rupyz.generic.model.LoginModelForAccess;
import com.app.rupyz.generic.model.forcedUpdate.UpdateResponse;
import com.app.rupyz.generic.model.blog.HomeDataInfo;
import com.app.rupyz.generic.model.createemi.CreateEMI;
import com.app.rupyz.generic.model.createemi.experian.ExperianCreateEMIResponse;
import com.app.rupyz.generic.model.createemi.experian.ExperianEMIResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    @POST("user/initiate_login/")
    Call<String> loginUser(@Body LoginModelForAccess loginResponse);

    @POST("user/logged_in/")
    Call<String> otpVerify(@Body LoginModelForAccess loginResponse);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("user/logout/")
    Call<String> logout(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/pan/")
    Call<String> createOrganization(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/individual/")
    Call<String> profileUpdate(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/individual/")
    Call<String> getReviewData(@Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/gstin/")
    Call<String> gstin(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/signatory/")
    Call<String> authorizedSignatory(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/review/")
    Call<String> getReviewInformation(@Query("org_id") int org_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/review/")
    Call<String> initUpdateInformation(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("cs/experian/initiated/")
    Call<String> initExperian(@Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("cs/experian/generate/1.1/")
    Call<String> generateExperian(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("cs/experian/generate/2/")
    Call<String> generateStep2(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("cs/experian/generate/2.1/")
    Call<String> generateStep2Point1(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("cs/experian/pan-check/")
    Call<String> getDashboardData(@Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("blog/public")
    Call<String> getBlogs(@Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("blog/public")
    Call<String> getBlogsById(@Query("slug_str") String slug_str);

    @GET("masterapp/maintenance")
    Call<String> getMaintenance();

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("masterapp/av/public/")
    Call<UpdateResponse> checkUpdate(@Query("version_no") int version, @Query("device_type") String decice);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("blog/public/")
    Call<String> getMicroBlogs(@Header("Authorization") String auth, @Query("slug_str") String blog_id);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("masterapp/homepage/")
    Call<HomeDataInfo> getHomePageData1(@Query("org_type") String org_type, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("cs/experian/emi/update/")
    Call<ExperianCreateEMIResponse> createEMI(@Body CreateEMI createEMI, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("cs/experian/emi/")
    Call<ExperianEMIResponse> getEMIList(@Query("page_no") int page_no, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("notification/fcm-device/")
    Call<String> saveFcmToken(@Body String requestData, @Header("Authorization") String auth);

}
