package com.app.rupyz.generic.network;

import com.app.rupyz.generic.model.organization.busineeaddress.BusinessInfo;
import com.app.rupyz.generic.model.organization.busineeaddress.businessupdate.BusinessInfoUpdate;
import com.app.rupyz.generic.model.organization.busineeaddress.businessupdate.UpdateBusinessInfo;
import com.app.rupyz.generic.model.organization.gstinfo.GSTInfo;
import com.app.rupyz.generic.model.organization.saveorganization.SaveOrganizationRequest;
import com.app.rupyz.generic.model.organization.saveorganization.SaveOrganizationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OnboardingInterface {
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/v2/organization/pan/")
    Call<String> createOrganization(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/v2/organization/pan/claim/")
    Call<String> claimProfile(@Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/v2/organization/{org_id}/business-details/")
    Call<SaveOrganizationResponse> saveOrganizationDetail(@Path("org_id") int org_id, @Header("Authorization") String auth, @Body SaveOrganizationRequest saveOrganizationRequest);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("/v2/organization/{org_id}/gstin/")
    Call<GSTInfo> getGstList(@Path("org_id") int org_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/v2/organization/{org_id}/gstin/")
    Call<String> gstin(@Path("org_id") int org_id, @Body String requestData, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("/v2/organization/{org_id}/address/")
    Call<BusinessInfo> getBusinessAddress(@Path("org_id") int ord_id, @Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/v2/organization/{org_id}/address/")
    Call<UpdateBusinessInfo> updateBusinessAddress(@Path("org_id") int ord_id, @Header("Authorization") String auth, @Body BusinessInfoUpdate businessInfoUpdate);
}
