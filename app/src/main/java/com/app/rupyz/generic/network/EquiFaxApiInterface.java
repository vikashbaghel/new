package com.app.rupyz.generic.network;

import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse11;
import com.app.rupyz.generic.model.createemi.CreateEquifaxIndividualEMI;
import com.app.rupyz.generic.model.organization.createorganization.CreateOrganizationRequest;
import com.app.rupyz.generic.model.organization.createorganization.CreateOrganizationResponse;
import com.app.rupyz.generic.model.profile.achievement.AchievementInfoModel;
import com.app.rupyz.generic.model.profile.achievement.createAchievement.CreateAchievementRequest;
import com.app.rupyz.generic.model.profile.achievement.createAchievement.CreateAchievementResponse;
import com.app.rupyz.generic.model.profile.achievement.deleteAchievement.DeleteAchievementRequest;
import com.app.rupyz.generic.model.profile.achievement.deleteAchievement.DeleteAchievementResponse;
import com.app.rupyz.generic.model.profile.product.ProductInfoModel;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel;
import com.app.rupyz.generic.model.profile.profileInfo.createProfile.CreateProfileInfoModel;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.CreateTeamRequest;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.CreateTeamResponse;
import com.app.rupyz.generic.model.profile.profileInfo.deleteProfile.DeleteTeamRequest;
import com.app.rupyz.generic.model.profile.profileInfo.deleteProfile.DeleteTeamResponse;
import com.app.rupyz.generic.model.profile.profileInfo.team.TeamInfo;
import com.app.rupyz.generic.model.profile.testimonial.TestimonialInfoModel;
import com.app.rupyz.generic.model.profile.testimonial.createTestimonial.CreateTestimonialRequest;
import com.app.rupyz.generic.model.profile.testimonial.createTestimonial.CreateTestimonialResponse;
import com.app.rupyz.generic.model.report_download.DownloadInfo;
import com.app.rupyz.model_kt.AllCategoryInfoModel;
import com.app.rupyz.model_kt.DeleteImageModel;
import com.app.rupyz.model_kt.GenericResponseModel;
import com.app.rupyz.model_kt.OrgImageListModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EquiFaxApiInterface {

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("cs/equifax/report/")
    Call<String> getReport(@Query("report_for") String report_for, @Query("org_id") int org_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("cs/equifax/consent/")
    Call<String> initEquiFaxOtp(@Query("org_id") int ord_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("cs/equifax/consent/")
    Call<String> checkEquiFaxOtp(@Body String requestBody, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("cs/equifax/emi/update/")
    Call<CreateEMIResponse11> createEMI(@Body CreateEquifaxIndividualEMI createEMI, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("cs/equifax/emi/")
    Call<CreateEMIResponse> getEMIDetails(@Query("account_no") String account_no, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("cs/equifax/emi/")
    Call<CreateEMIResponse> getEMIList(@Query("page_no") int page_no, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("cs/pdf-report/")
    Call<DownloadInfo> downloadReport(@Query("org_id") int org_id, @Query("report_type") String report_type, @Query("report_for") String report_for, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/{org_id}/category/")
    Call<String> getAllCategoryList(@Path("org_id") int org_id, @Header("Authorization") String auth, @Query("is_with_id") boolean is_with_id);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/{org_id}/product/")
    Call<ProductInfoModel> getProductList(@Path("org_id") int org_id, @Header("Authorization") String auth, @Query("category") String category, @Query("page_no") Integer page);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/{org_id}/category/")
    Call<AllCategoryInfoModel> getSearchedCategoryList(@Path("org_id") int org_id, @Header("Authorization") String auth, @Query("page_no") Integer page, @Query("name") String name, @Query("is_with_id") boolean is_with_id);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("biz/{slug}/product/?page_no=1")
    Call<ProductInfoModel> getSlugProductList(@Path("slug") String slug, @Header("Authorization") String auth);

    @POST("organization/{org_id}/product/delete/{product_id}/")
    Call<String> deleteProduct(@Path("org_id") String org_id, @Path("product_id") Integer product_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/{org_id}/info/")
    Call<OrgProfileInfoModel> getProfileInfo(@Path("org_id") int org_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/{org_id}/info/")
    Call<OrgProfileInfoModel> updateProfileInfo(@Path("org_id") int org_id, @Header("Authorization") String auth, @Body CreateProfileInfoModel createProfileInfoModel);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/{org_id}/team/")
    Call<CreateTeamResponse> createTeamMember(@Path("org_id") int org_id, @Header("Authorization") String auth, @Body CreateTeamRequest createTeamRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/{org_id}/team/")
    Call<TeamInfo> getTeamMemberList(@Path("org_id") int org_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/{org_id}/team/delete/")
    Call<DeleteTeamResponse> deleteTeamMember(@Path("org_id") int org_id, @Header("Authorization") String auth, @Body DeleteTeamRequest deleteTeamRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("biz/{slug}/team/")
    Call<TeamInfo> getSlugTeamMemberList(@Path("slug") String slug, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/{org_id}/testimonial/")
    Call<TestimonialInfoModel> getTestimonials(@Path("org_id") int org_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("biz/{slug}/testimonial/?page_no=1")
    Call<TestimonialInfoModel> getSlugTestimonials(@Path("slug") String slug);


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/{org_id}/achievement/")
    Call<AchievementInfoModel> getAchievement(@Path("org_id") int org_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("biz/{slug}/achievement/")
    Call<AchievementInfoModel> getSlugAchievement(@Path("slug") String slug, @Header("Authorization") String auth);


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("biz/{slug}/image/")
    Call<OrgImageListModel> getSlugImage(@Path("slug") String slug, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/{org_id}/image/")
    Call<OrgImageListModel> getOrgImage(@Path("org_id") int org_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/{org_id}/image/delete/")
    Call<GenericResponseModel> deleteOrgImage(@Path("org_id") int org_id, @Body DeleteImageModel jsonObject, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/{org_id}/achievement/")
    Call<CreateAchievementResponse> createAchievement(@Path("org_id") int org_id, @Header("Authorization") String auth, @Body CreateAchievementRequest createAchievementRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/{org_id}/achievement/delete/")
    Call<DeleteAchievementResponse> deleteAchievement(@Path("org_id") int org_id, @Header("Authorization") String auth, @Body DeleteAchievementRequest deleteAchievementRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("organization/{org_id}/product/{product_id}/")
    Call<String> getProductDetails(@Path("org_id") int org_id, @Path("product_id") int id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("/v1/organization/{org_id}/product/{product_id}/")
    Call<String> getProductDetailsForCustomer(@Path("org_id") int org_id, @Path("product_id") int id, @Query("customer_id") int customer_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("biz/{slug}/product/{nanoid}/counter/")
    Call<String> setViewCount(@Path("slug") String slug, @Path("nanoid") String nanoid, @Query("counter_field") String type);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/{org_id}/testimonial/")
    Call<CreateTestimonialResponse> addTestimonial(@Path("org_id") int org_id, @Header("Authorization") String auth, @Body CreateTestimonialRequest createTestimonialRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("organization/{org_id}/testimonial/delete/")
    Call<CreateTestimonialResponse> deleteTestimonial(@Path("org_id") int org_id, @Header("Authorization") String auth, @Body JsonObject jsonObject);

    //organization/pan/
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("v2/organization/pan/")
    Call<CreateOrganizationResponse> createOrganization(@Header("Authorization") String auth, @Body CreateOrganizationRequest createOrganizationRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("/v1/cs/equifax/errors/")
    Call<String> getCommercialMaskedMobile(@Query("org_id") int org_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/v1/cs/equifax/errors/")
    Call<String> postCommercialMaskedMobile(@Body String requestBody, @Header("Authorization") String auth);
}

