package com.app.rupyz.retrofit

import com.app.rupyz.generic.model.profile.category.CategoryInfoModel
import com.app.rupyz.generic.model.profile.product.ProductInfoModel
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel
import com.app.rupyz.model_kt.ActiveBeatRoutePlanResponseModel
import com.app.rupyz.model_kt.AddBeatModel
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.AddExpenseResponseModel
import com.app.rupyz.model_kt.AddLeadResponseModel
import com.app.rupyz.model_kt.AddNewAdminModel
import com.app.rupyz.model_kt.AddNewCustomerFormDataModel
import com.app.rupyz.model_kt.AddNewSegmentModel
import com.app.rupyz.model_kt.AddOrganizationModel
import com.app.rupyz.model_kt.AddPhotoModel
import com.app.rupyz.model_kt.AddPhotoResponseModel
import com.app.rupyz.model_kt.AddProductModel
import com.app.rupyz.model_kt.AddProductResponseModel
import com.app.rupyz.model_kt.AddTotalExpenseResponseModel
import com.app.rupyz.model_kt.AllCategoryInfoModel
import com.app.rupyz.model_kt.ApprovalRequestResponseModel
import com.app.rupyz.model_kt.AssignRolesResponseModel
import com.app.rupyz.model_kt.AttendanceDataItem
import com.app.rupyz.model_kt.AttendanceResponseModel
import com.app.rupyz.model_kt.BeatDetailsResponseModel
import com.app.rupyz.model_kt.BeatListResponseModel
import com.app.rupyz.model_kt.BeatPlanModel
import com.app.rupyz.model_kt.BeatRouteCustomerInfoModel
import com.app.rupyz.model_kt.BeatRouteDailyPlanResponseModel
import com.app.rupyz.model_kt.BeatRoutePlanListResponseModel
import com.app.rupyz.model_kt.BeatRoutePlanResponseModel
import com.app.rupyz.model_kt.BranListResponseModel
import com.app.rupyz.model_kt.CheckInOutResponseModel
import com.app.rupyz.model_kt.CheckInRequest
import com.app.rupyz.model_kt.CheckInResponse
import com.app.rupyz.model_kt.CheckoutRequest
import com.app.rupyz.model_kt.ConnectionModel
import com.app.rupyz.model_kt.CreateBeatRoutePlanModel
import com.app.rupyz.model_kt.CustomFormCreationModel
import com.app.rupyz.model_kt.CustomerAddressApiResponseModel
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.CustomerAddressListResponseModel
import com.app.rupyz.model_kt.CustomerFeedbackListResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.CustomerFollowUpListResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpSingleResponseModel
import com.app.rupyz.model_kt.CustomerInsightsResponse
import com.app.rupyz.model_kt.CustomerTypeResponseModel
import com.app.rupyz.model_kt.CustomerWiseSalesResponseModel
import com.app.rupyz.model_kt.DailySalesReportResponseModel
import com.app.rupyz.model_kt.DeviceActivityLogsPostModel
import com.app.rupyz.model_kt.DeviceActivityLogsResponseModel
import com.app.rupyz.model_kt.DiscoveryResponseModel
import com.app.rupyz.model_kt.DispatchedOrderDetailsModel
import com.app.rupyz.model_kt.DispatchedOrderListModel
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseResponseModel
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import com.app.rupyz.model_kt.ExpenseTrackerResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.GstInfoResponseModel
import com.app.rupyz.model_kt.LeadCategoryListResponseModel
import com.app.rupyz.model_kt.LeadCategoryResponseModel
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.LeadListResponseModel
import com.app.rupyz.model_kt.LiveLocationListModel
import com.app.rupyz.model_kt.LiveLocationResponseModel
import com.app.rupyz.model_kt.LogOutModel
import com.app.rupyz.model_kt.LoginModel
import com.app.rupyz.model_kt.MyNetworkResponseModel
import com.app.rupyz.model_kt.NetWorkConnectModel
import com.app.rupyz.model_kt.NetworkConnectResponseModel
import com.app.rupyz.model_kt.NetworkOrgModel
import com.app.rupyz.model_kt.NewUpdateCustomerInfoModel
import com.app.rupyz.model_kt.NotificationResponseModel
import com.app.rupyz.model_kt.OrderTakenByAdminModel
import com.app.rupyz.model_kt.OrgBeatListResponseModel
import com.app.rupyz.model_kt.OrganizationWiseSalesResponseModel
import com.app.rupyz.model_kt.PostalOfficeResponseModel
import com.app.rupyz.model_kt.PreferenceData
import com.app.rupyz.model_kt.PreferencesResponseModel
import com.app.rupyz.model_kt.PricingGroupResponseModel
import com.app.rupyz.model_kt.ProductDetailsResponseModel
import com.app.rupyz.model_kt.RecentSearchResponseModel
import com.app.rupyz.model_kt.ReminderListResponseModel
import com.app.rupyz.model_kt.S3ConfirmResponseModel
import com.app.rupyz.model_kt.S3UploadCredResponse
import com.app.rupyz.model_kt.S3UploadResponse
import com.app.rupyz.model_kt.StaffActiveResponseModel
import com.app.rupyz.model_kt.StaffCurrentlyActiveTargetResponseModel
import com.app.rupyz.model_kt.StaffTcPcInfoModel
import com.app.rupyz.model_kt.StaffTrackingDetailsResponseModel
import com.app.rupyz.model_kt.StaffUpcomingAndClosedTargetResponseModel
import com.app.rupyz.model_kt.StaffWiseSalesResponseModel
import com.app.rupyz.model_kt.TeamAggregatedInfoResponseModel
import com.app.rupyz.model_kt.TeamTrackingDetailsResponseModel
import com.app.rupyz.model_kt.TopCategoryResponseModel
import com.app.rupyz.model_kt.TopProductResponseModel
import com.app.rupyz.model_kt.UpdateAttendanceResponseModel
import com.app.rupyz.model_kt.UploadOfflineAttendanceModel
import com.app.rupyz.model_kt.UserLoginResponseModel
import com.app.rupyz.model_kt.UserPreferenceData
import com.app.rupyz.model_kt.UserPreferencesResponseModel
import com.app.rupyz.model_kt.VerifyWhatsAppNumberResponseModel
import com.app.rupyz.model_kt.checkIn.CheckInStatus
import com.app.rupyz.model_kt.gallery.GalleryResponseData
import com.app.rupyz.model_kt.order.customer.CustomerAddResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.CustomerDeleteOptionModel
import com.app.rupyz.model_kt.order.customer.CustomerDeleteResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerInfoModel
import com.app.rupyz.model_kt.order.customer.CustomerListForBeatModel
import com.app.rupyz.model_kt.order.customer.CustomerListWithStaffMappingModel
import com.app.rupyz.model_kt.order.customer.CustomerPanOrGstInfoModel
import com.app.rupyz.model_kt.order.customer.PanDataInfoModel
import com.app.rupyz.model_kt.order.customer.SegmentListResponseModel
import com.app.rupyz.model_kt.order.customer.UpdateCustomerInfoModel
import com.app.rupyz.model_kt.order.dashboard.DashboardIndoModel
import com.app.rupyz.model_kt.order.order_history.CreateOrderResponseModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.order_history.OrderDetailsInfoModel
import com.app.rupyz.model_kt.order.order_history.OrderInfoModel
import com.app.rupyz.model_kt.order.order_history.OrderUpdateResponseModel
import com.app.rupyz.model_kt.order.payment.PaymentRecordResponseModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.model_kt.order.payment.RecordPaymentDetailModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentInfoModel
import com.app.rupyz.model_kt.order.sales.StaffAddResponseModel
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.model_kt.order.sales.StaffInfoModel
import com.app.rupyz.model_kt.order.sales.StaffListWithBeatMappingModel
import com.app.rupyz.model_kt.order.sales.StaffListWithCustomerMappingModel
import com.app.rupyz.model_kt.packagingunit.PackagingUnitData
import com.app.rupyz.model_kt.packagingunit.PackagingUnitInfoModel
import com.app.rupyz.model_kt.packagingunit.PackagingUnitResponseModel
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiInterface {

    @POST("/v1/user/initiate_login/")
    fun loginUser(@Body loginResponse: LoginModel): Call<UserLoginResponseModel>

    @POST("/v1/user/logged_in/")
    fun otpVerify(@Body loginResponse: LoginModel?): Call<UserLoginResponseModel>

    @POST("/v1/notification/fcm-device/")
    fun saveFcmToken(
        @Body requestData: JsonObject,
    ): Call<GenericResponseModel>

    @POST("s3/upload/")
    fun s3CredUpload(@Body body: RequestBody): Call<S3UploadCredResponse>

    @POST
    fun s3FileUpload(@Url url: String, @Body body: RequestBody): Call<ResponseBody>

    @POST("/v1/s3/confirm/")
    fun s3ConfirmUpload(@Body body: S3UploadResponse): Call<GenericResponseModel>

    @POST("/v1/s3/confirm/")
    fun s3ConfirmPdfUpload(@Body body: S3UploadResponse): Call<S3ConfirmResponseModel>

    @POST("/v1/organization/{org_id}/product/")
    fun addProduct(
        @Path("org_id") org_id: String,
        @Body body: AddProductModel,
    ): Call<AddProductResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/category/")
    fun getCategoryList(
        @Path("org_id") org_id: Int, @Query("name") name: String?
    ): Call<AllCategoryInfoModel>

    @POST("/v1/organization/{org_id}/product/{product_id}/")
    fun editProduct(
        @Path("org_id") org_id: String,
        @Body body: AddProductModel,
        @Path("product_id") product_id: Int,
    ): Call<AddProductResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/product/{product_id}/")
    fun getProductDetails(
        @Path("org_id") orgId: Int,
        @Path("product_id") id: Int,
        @Query("customer_id") customerId: Int?
    ): Call<ProductDetailsResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/product/{product_id}/")
    fun getProductDetailsUsingCode(
        @Path("org_id") orgId: Int,
        @Path("product_id") productId: Int,
        @Query("customer_id") customerId: Int?,
        @Query("code") name: String
    ): Call<ProductDetailsResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/product/{product_id}/")
    fun getProductTelescopicPricing(
        @Path("org_id") orgId: Int,
        @Path("product_id") id: Int,
        @Query("customer_id") customerId: Int?,
        @Query("get_telescope_pricing_only") telescopic: Boolean?
    ): Call<ProductDetailsResponseModel>


    @POST("/v1/organization/{org_id}/image/")
    fun addPhoto(
        @Body body: AddPhotoModel,
        @Path("org_id") org_id: String,
    ): Call<AddPhotoResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("organization/{org_id}/info/")
    fun getProfileInfo(
        @Path("org_id") org_id: Int
    ): Call<OrgProfileInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("organization/{org_id}/info/")
    fun updateProfileInfo(
        @Path("org_id") org_id: Int,
        @Header("Authorization") auth: String?,
        @Body data: OrgProfileDetail?,
    ): Call<OrgProfileInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/")
    fun updateProfileBasicInfo(
        @Path("org_id") org_id: Int,
        @Body data: OrgProfileDetail?,
    ): Call<OrgProfileInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("organization/{slug}/")
    fun getProfileInfoUsingSlug(
        @Path("slug") slug: String,
        @Query("org_id") org_id: Int,
    ): Call<OrgProfileInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/gstin-info/")
    fun getProfileInfoUsingGstNumber(
        @Path("org_id") org_id: Int,
        @Query("primary_gstin") primary_gstin: String,
    ): Call<OrgProfileInfoModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("organization/{org_id}/connection/?page_no=1")
    fun getConnectionList(
        @Path("org_id") org_id: Int,
        @Query("status") status: String,
    ): Call<ConnectionModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("organization/discovery-search/")
    fun getDiscoverySearch(
        @Query("type") type: String,
        @Query("name") name: String,
        @Query("location") city: String,
        @Query("badge") badge: Int,
        @Query("is_search_hard") isHardSearch: Boolean,
    ): Call<DiscoveryResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("organization/discovery-search/")
    fun getDiscoverySearchWithPagination(
        @Query("type") type: String,
        @Query("name") name: String,
        @Query("location") city: String,
        @Query("badge") badge: Int,
        @Query("page_no") page_no: Int,
    ): Call<DiscoveryResponseModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("masterapp/search/history/")
    fun getDiscoverySearchHistory(
        @Query("page_no") page_no: Int, @Query("module_name") module_name: String
    ): Call<RecentSearchResponseModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("organization/{org_id}/connection/search/")
    fun getSuggestionList(
        @Path("org_id") org_id: Int,
        @Query("page_no") currentPage: Int,
    ): Call<NetworkOrgModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("organization/{org_id}/connection/search/?page_no=1")
    fun getSuggestionListSearch(
        @Path("org_id") org_id: Int,
        @Query("name") name: String?,
    ): Call<NetworkOrgModel>

    @POST("/v1/organization/{org_id}/connection/")
    fun followOrg(
        @Body body: NetWorkConnectModel,
        @Path("org_id") org_id: Int,
    ): Call<NetworkConnectResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/connection/")
    fun connectionInfo(
        @Path("org_id") org_id: Int,
    ): Call<MyNetworkResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/staff/")
    fun getStaffList(
        @Path("org_id") org_id: Int,
        @Query("roles") role: String?,
        @Query("name") name: String?,
        @Query("page_no") currentPage: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?,
        @Query("dd") dd: Boolean = false,
    ): Call<StaffInfoModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/staff/")
    fun getStaffListForAssignManager(
        @Path("org_id") org_id: Int,
        @Query("get_assignable_managers") getAssignableManagers: Int?,
        @Query("name") name: String?,
        @Query("page_no") currentPage: Int,
    ): Call<StaffInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/{customer_id}/mapping/")
    fun getStaffListUsingCustomerMapping(
        @Path("org_id") orgId: Int,
        @Path("customer_id") customerId: Int?,
        @Query("name") name: String?,
        @Query("selected") selected: Boolean?,
        @Query("page_no") currentPage: Int?,
    ): Call<StaffListWithCustomerMappingModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/type/")
    fun getCustomerTypeList(
        @Path("org_id") org_id: Int,
        @Query("name") name: String,
        @Query("page_no") currentPage: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<CustomerTypeResponseModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/custom-forms/customer/{currentPage}")
    fun getAddCustomerCustomFormData(
        @Path("org_id") org_id: Int,
        @Path("currentPage") currentPage: Int,
    ): Call<CustomFormCreationModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/gallery/")
    fun getPictureGalleryList(
        @Path("org_id") org_id: Int,
        @Query("user_ids") user_id: StringBuilder,
        @Query("customer_ids") customer_id: StringBuilder,
        @Query("module_type") module_type: StringBuilder,
        @Query("module_id") module_id: Int?,
        @Query("sub_module_type") sub_module_type: StringBuilder,
        @Query("by_date_range") by_date_range: String?,
        @Query("start_date") start_date: String?,
        @Query("end_date") end_date: String?,
        @Query("state") state: StringBuilder,
        @Query("sort_by") sort_by: String?,
        @Query("sort_order") sort_order: String?,
        @Query("page_no") page_no: Int,
    ): Call<GalleryResponseData>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/staff/")
    fun addStaff(
        @Body body: StaffData,
        @Path("org_id") org_id: Int,
    ): Call<StaffAddResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/staff/{staff_id}/delete/")
    fun deleteStaff(
        @Path("org_id") org_id: Int,
        @Path("staff_id") staff_id: Int,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/staff/{staff_id}/")
    fun getStaffById(
        @Path("org_id") org_id: Int,
        @Path("staff_id") staff_id: Int,
    ): Call<StaffAddResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/staff/{staff_id}/")
    fun updateStaff(
        @Body body: StaffData,
        @Path("org_id") org_id: Int,
        @Path("staff_id") staff_id: Int,
    ): Call<StaffAddResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/")
    fun getCustomerList(
        @Path("org_id") org_id: Int,
        @Query("customer_parent_id") customer_parent_id: Int?,
        @Query("name") name: String?,
        @Query("customer_level") customer_level: String?,
        @Query("customer_type") customer_type: StringBuilder,
        @Query("sort_by") sortBy: String,
        @Query("sort_order") sortByOrder: String,
        @Query("page_no") page_no: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?,
        @QueryMap customerFilterParams : HashMap<String, String>  = hashMapOf()
    ): Call<CustomerInfoModel>
    
 


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/{customer_id}/mapping/parents/")
    fun getCustomerListMapped(
        @Path("org_id") org_id: Int,
        @Path("customer_id") customer_id: Int?,
        @Query("name") name: String?,
        @Query("customer_level") customer_level: String?,
        @Query("customer_type") customer_type: StringBuilder,
        @Query("sort_by") sortBy: String,
        @Query("sort_order") sortByOrder: String,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("page_no") page_no: Int?,
        @Query("selected") selected: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?,
        @Query("ignore_mapping") ignoreMapping: Boolean?,

    ): Call<CustomerInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/staff/{staff_id}/mapping/")
    fun getCustomerListWithStaffMapping(
        @Path("org_id") org_id: Int,
        @Path("staff_id") staff_id: Int,
        @Query("name") name: String?,
        @Query("page_no") page_no: Int
    ): Call<CustomerListWithStaffMappingModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/RBAC/{org_id}/role/")
    fun getRoleList(
        @Path("org_id") org_id: Int,
        @Query("page_no") page_no: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<AssignRolesResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/segment/")
    fun getSegmentList(
        @Path("org_id") org_id: Int, @Query("page_no") page_no: Int
    ): Call<SegmentListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/{customer_id}")
    fun getCustomerById(
        @Path("org_id") org_id: Int,
        @Path("customer_id") customer_id: Int,

    ): Call<UpdateCustomerInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/profiles-with-org/{org_id}/")
    fun getOrderTakenByAdmin(
        @Path("org_id") org_id: Int,
        @Query("page_no") page_no: Int,
        @Query("name") name: String,
        @Query("dd") dd: Boolean,

    ): Call<OrderTakenByAdminModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/{customer_id}")
    fun getCustomerByIdNew(
        @Path("org_id") org_id: Int,
        @Path("customer_id") customer_id: Int,
    ): Call<NewUpdateCustomerInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/activity/checkin/")
    fun getCheckIn(
        @Path("org_id") org_id: Int,
        @Body checkInRequest: CheckInRequest,
    ): Call<CheckInResponse>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/activity/checkout/")
    fun getCheckOut(
        @Path("org_id") org_id: Int,
        @Body checkoutRequest: CheckoutRequest,
    ): Call<CheckInResponse>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/")
    fun getCustomerListForType(
        @Path("org_id") org_id: Int, @Query("customer_type") customer_type: String
    ): Call<CustomerInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/analytics/customer/")
    fun getCustomerWiseSalesList(
        @Path("org_id") org_id: Int,
        @Query("interval_type") interval_type: String,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("page_no") currentPage: Int
    ): Call<CustomerWiseSalesResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/analytics/staff/")
    fun getStaffWiseSalesList(
        @Path("org_id") org_id: Int,
        @Query("interval_type") interval_type: String,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("page_no") currentPage: Int
    ): Call<StaffWiseSalesResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/analytics/organization/")
    fun getOrganizationWiseSalesList(
        @Path("org_id") org_id: Int,
        @Query("interval_type") interval_type: String,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("page_no") currentPage: Int
    ): Call<OrganizationWiseSalesResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/analytics/product/")
    fun getTopProductList(
        @Path("org_id") org_id: Int,
        @Query("interval_type") interval_type: String,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("page_no") currentPage: Int
    ): Call<TopProductResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/analytics/product/")
    fun getTopCategoryList(
        @Path("org_id") org_id: Int,
        @Query("interval_type") interval_type: String,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("page_no") currentPage: Int,
        @Query("get_category") getCategory: Boolean
    ): Call<TopCategoryResponseModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/pan/")
    fun createOrganization(
        @Body body: PanDataInfoModel,
    ): Call<CustomerPanOrGstInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/gstin-info/")
    fun getCustomerDetailsByGst(
        @Path("org_id") org_id: Int,
        @Query("primary_gstin") primary_gstin: String,
    ): Call<CustomerPanOrGstInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/segment/")
    fun addNewSegment(
        @Path("org_id") org_id: Int,
        @Body body: AddNewSegmentModel,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v1/organization/{org_id}/category/")
    fun addNewCategory(
        @Path("org_id") org_id: Int,
        @Body body: JsonObject,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/segment/{segment_id}/")
    fun updateSegment(
        @Path("org_id") org_id: Int,
        @Path("segment_id") segment_id: Int,
        @Body body: AddNewSegmentModel,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/customer/")
    fun addCustomer(
        @Body body: CustomerData,
        @Path("org_id") org_id: Int,
    ): Call<CustomerAddResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/customer/")
    fun addCustomerNew(
        @Body body: AddNewCustomerFormDataModel,
        @Path("org_id") org_id: Int,
    ): Call<CustomerAddResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/customer/{customer_id}/")
    fun updateCustomer(
        @Body body: CustomerData,
        @Path("org_id") org_id: Int,
        @Path("customer_id") customer_id: Int,
    ): Call<CustomerAddResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/customer/{customer_id}/")
    fun updateCustomerNew(
        @Body body: AddNewCustomerFormDataModel,
        @Path("org_id") org_id: Int,
        @Path("customer_id") customer_id: Int,
    ): Call<NewUpdateCustomerInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/customer/{customer_id}/delete/")
    fun inactiveCustomer(
        @Path("org_id") org_id: Int,
        @Path("customer_id") customer_id: Int,
        @Body option: CustomerDeleteOptionModel,
    ): Call<CustomerDeleteResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/product/es/")
    fun getProductList(
        @Path("org_id") org_id: Int,
        @Query("name") name: String,
        @Query("brand") brand: StringBuilder,
        @Query("category") category: String,
        @Query("page_no") page_no: Int
    ): Call<ProductInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/product/")
    fun getProductListForDataDump(
        @Path("org_id") org_id: Int,
        @Query("name") name: String,
        @Query("brand") brand: StringBuilder,
        @Query("category") category: String,
        @Query("page_no") page_no: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<ProductInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/product/es/")
    fun getProductListForOrderEdit(
        @Path("org_id") org_id: Int, @Query("ids") ids: StringBuilder
    ): Call<ProductInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("organization/{org_id}/product/")
    fun getProductListForCustomer(
        @Path("org_id") org_id: Int,
        @Query("customer_id") customer_id: Int,
        @Query("page_no") page_no: Int
    ): Call<ProductInfoModel>

    @POST("/v1/organization/{org_id}/product/delete/{product_id}/")
    fun deleteProduct(
        @Path("org_id") org_id: Int, @Path("product_id") product_id: Int, @Body model: JsonObject
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/category/")
    fun getAllCategoryList(
        @Path("org_id") org_id: Int,
        @Query("dd") dd: Boolean,
        @Query("customer_id") customer_id: Int?,
        @Query("name") name: String,
        @Query("page_no") page: Int?,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<AllCategoryInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/{customer_id}/mapping/pc/")
    fun getAllCategoryListWithCustomer(
        @Path("org_id") orgId: Int,
        @Path("customer_id") customerId: Int,
        @Query("name") name: String,
        @Query("selected") selected: Boolean,
        @Query("page_no") pageNo: Int?
    ): Call<AllCategoryInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/product/brand/")
    fun getBrandList(
        @Path("org_id") org_id: Int,
        @Query("name") name: String,
        @Query("dd") dd: Boolean,
        @Query("page_no") page_no: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<BranListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/product/es/")
    fun getElasticSearchProductList(
        @Path("org_id") org_id: Int,
        @Query("customer_id") customer_id: Int,
        @Query("name") name: String,
        @Query("category") category: String,
        @Query("brand") brand: StringBuilder,
        @Query("page_no") page_no: Int
    ): Call<ProductInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/sales/dashboard/")
    fun getDashboardData(
        @Path("org_id") org_id: Int, @Query("for_dump") forDumpTrue: Boolean
    ): Call<DashboardIndoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/record-payment/")
    fun recordPayment(
        @Body body: RecordPaymentData,
        @Path("org_id") org_id: Int,
    ): Call<PaymentRecordResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/record-payment/")
    fun getRecordPaymentListById(
        @Path("org_id") org_id: Int,
        @Query("customer_id") customer_id: Int,
        @Query("page_no") page_no: Int
    ): Call<RecordPaymentInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/record-payment/")
    fun getRecordPaymentList(
        @Path("org_id") org_id: Int,
        @Query("status") payment_status: String,
        @Query("page_no") page_no: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<RecordPaymentInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/record-payment/{payment_id}/")
    fun getRecordPaymentDetails(
        @Path("org_id") org_id: Int, @Path("payment_id") payment_id: Int
    ): Call<RecordPaymentDetailModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/record-payment/{payment_id}/archived/")
    fun deletePayment(
        @Path("org_id") org_id: Int,
        @Body jsonObject: JsonObject,
        @Path("payment_id") payment_id: Int
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/record-payment/{id}/")
    fun updateRecordPayment(
        @Body body: RecordPaymentData,
        @Path("org_id") org_id: Int,
        @Path("id") id: Int,
    ): Call<PaymentRecordResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/{customer_id}/address/")
    fun getCustomerAddressList(
        @Path("org_id") org_id: Int,
        @Path("customer_id") customer_id: Int,
        @Query("page_no") page: Int?,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<CustomerAddressListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/customer/{customer_id}/address/")
    fun addCustomerAddress(
        @Path("org_id") org_id: Int,
        @Path("customer_id") customer_id: Int,
        @Body address: CustomerAddressDataItem
    ): Call<CustomerAddressApiResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/order/")
    fun confirmOrder(
        @Path("org_id") org_id: Int,
        @Body cartListResponseModel: OrderData,
    ): Call<CreateOrderResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/associate/")
    fun addOrganization(
        @Body model: AddOrganizationModel,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/associate/verify/")
    fun verifyOrganization(
        @Body model: AddOrganizationModel,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/associate/profile/")
    fun addNewAdmin(
        @Path("org_id") org_id: Int,
        @Body model: AddNewAdminModel,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/staff/profile/")
    fun updateMyProfileDetails(
        @Body model: StaffData
    ): Call<StaffAddResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/associate/profile/verify/")
    fun verifyNewAdmin(
        @Path("org_id") org_id: Int,
        @Body model: AddNewAdminModel,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("user/profile/")
    fun getProfile(@Query("org_id") org_id: Int): Call<UserLoginResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/staff/profile/")
    fun getStaffProfileDetails(): Call<UserLoginResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/order/")
    fun getOrderList(
        @Path("org_id") org_id: Int,
        @Query("delivery_status") deliveryStatus: String,
        @Query("fullfilled_by_ids") full_filled_by_ids: String?,
        @Query("customer_level") customerLevel: String,
        @Query("page_no") pageNo: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
        @Query("staff_id") staffId: Int?,
        @Query("order_id") orderId: Int?,
        @Query("transaction_ref_no") transactionRefNo: String?,
        @Query("customer_id") customerId: Int?,
        @Query("is_archived") isArchived: Boolean?,
        @Query("search_query") searchQuery: String?,
        @Query("customer_ids") customerIds: String?,
        @Query("payment_options") paymentOptions: String?,
        @Query("platform") platform: String?,
        @Query("user_ids") userIds: String?,
        @Query("sort_by") sortBy: String?,
        @Query("sort_order") sortOrder: String?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<OrderInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/order/")
    fun getSearchResultForOrderData(
        @Path("org_id") org_id: Int,
        @Query("delivery_status") delivery_status: String,
        @Query("customer") customer: String,
        @Query("page_no") page_no: Int
    ): Call<OrderInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/order/{order_id}/")
    fun getOrderById(
        @Path("org_id") org_id: Int,
        @Path("order_id") order_id: Int,
    ): Call<OrderDetailsInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/order/{order_id}/archived/")
    fun deleteOrder(
        @Path("org_id") org_id: Int, @Path("order_id") order_id: Int, @Body jsonObject: JsonObject
    ): Call<OrderUpdateResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/order/{id}/")
    fun updateOrderStatus(
        @Body body: JsonObject,
        @Path("org_id") org_id: Int,
        @Path("id") id: Int,
    ): Call<OrderUpdateResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/order-update/{order_id}/")
    fun updateOrder(
        @Path("org_id") org_id: Int, @Path("order_id") id: Int, @Body model: OrderData?
    ): Call<OrderDetailsInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/order/{order_id}/dispatch/")
    fun createOrderDispatched(
        @Path("org_id") org_id: Int, @Path("order_id") id: Int, @Body model: DispatchedOrderModel?
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/order/{order_id}/dispatch/{dispatch_id}/lr-update/")
    fun updateOrderDispatched(
        @Path("org_id") org_id: Int,
        @Path("order_id") id: Int,
        @Path("dispatch_id") dispatch_id: Int,
        @Body model: DispatchedOrderModel?
    ): Call<GenericResponseModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/order/{order_id}/dispatch/{dispatch_id}")
    fun getOrderDispatchedDetails(
        @Path("org_id") org_id: Int,
        @Path("order_id") id: Int,
        @Path("dispatch_id") dispatch_id: Int
    ): Call<DispatchedOrderDetailsModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/order/{order_id}/dispatch/")
    fun getOrderDispatchedListForDump(
        @Path("org_id") org_id: Int,
        @Path("order_id") id: Int,
        @Query("page_no") dispatchOrderPageCount: Int,
        @Query("page_size") pageSize: Int,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<DispatchedOrderListModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/order/")
    fun getRecentOrderListForCustomer(
        @Path("org_id") org_id: Int,
        @Query("customer_id") staff_id: Int,
        @Query("page_no") page_no: Int,
    ): Call<OrderInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/order/")
    fun getRecentOrderListForStaff(
        @Path("org_id") org_id: Int,
        @Query("staff_id") staff_id: Int,
    ): Call<OrderInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/record-payment/")
    fun getRecentPaymentListForStaff(
        @Path("org_id") org_id: Int,
        @Query("staff_id") staff_id: Int,
    ): Call<OrderInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/record-payment/")
    fun getRecentPaymentListForCustomer(
        @Path("org_id") org_id: Int,
        @Query("customer_id") customer: Int,
    ): Call<OrderInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/{customer_id}/insights/")
    fun getCustomerInsights(
        @Path("org_id") org_id: Int,
        @Path("customer_id") customerId: Int,
    ): Call<CustomerInsightsResponse>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v1/organization/{org_id}/productunit/")
    fun addPackagingUnit(
        @Path("org_id") org_id: Int,
        @Body packagingUnitData: PackagingUnitData,
    ): Call<PackagingUnitInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/productunit/")
    fun getPackagingUnit(
        @Path("org_id") org_id: Int,
    ): Call<PackagingUnitResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v1/organization/{org_id}/category/")
    fun addProductCategory(
        @Path("org_id") org_id: Int, @Body jsonObject: JsonObject
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/organization/{org_id}/category/")
    fun getProductCategory(
        @Path("org_id") org_id: Int, @Query("name") name: String
    ): Call<CategoryInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/preferences/")
    fun updatePreferences(
        @Path("org_id") org_id: Int, @Body updatePreferencesModel: PreferenceData
    ): Call<PreferencesResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/preferences/")
    fun getPreferencesInfo(
        @Path("org_id") org_id: Int
    ): Call<PreferencesResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/user/preferences/")
    fun getUserPreferencesInfo(
        @Path("org_id") org_id: Int
    ): Call<UserPreferencesResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/user/preferences/")
    fun setUserPreferencesInfo(
        @Path("org_id") org_id: Int, @Body jsonObject: UserPreferenceData
    ): Call<UserPreferencesResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/pricing-group/")
    fun getPricingGroupList(
        @Path("org_id") org_id: Int, @Query("is_with_id") is_with_id: Boolean
    ): Call<PricingGroupResponseModel>


    // Lead Management--------------------------------------

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/leadcategory/")
    fun getAllLeadCategoryList(
        @Path("org_id") org_id: Int,
        @Query("name") category: String,
        @Query("page_no") page: Int?,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<LeadCategoryListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/leadform/")
    fun getAllLeadList(
        @Path("org_id") org_id: Int,
        @Query("name") name: String,
        @Query("lead_category") category: String,
        @Query("page_no") page_no: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<LeadListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/leadcategory/")
    fun creteLeadCategory(
        @Path("org_id") org_id: Int, @Body jsonObject: JsonObject
    ): Call<LeadCategoryResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/leadform/")
    fun creteNewLead(
        @Path("org_id") org_id: Int, @Body jsonObject: LeadLisDataItem
    ): Call<AddLeadResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/leadform/{lead_id}/")
    fun updateLead(
        @Path("org_id") org_id: Int,
        @Path("lead_id") lead_id: Int,
        @Body jsonObject: LeadLisDataItem
    ): Call<AddLeadResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/leadform/{lead_id}/")
    fun getLeadInfo(
        @Path("org_id") org_id: Int,
        @Path("lead_id") lead_id: Int,
    ): Call<AddLeadResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/leadform/{lead_id}/delete/")
    fun delteLead(
        @Path("org_id") org_id: Int,
        @Path("lead_id") lead_id: Int,
    ): Call<AddLeadResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/leadform/mobile/")
    fun checkLeadMobileNumberExist(
        @Path("org_id") org_id: Int, @Query("mobile") mobile: String
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/gstin-info/")
    fun getGstInfo(
        @Path("org_id") org_id: Int, @Query("primary_gstin") primary_gstin: String
    ): Call<GstInfoResponseModel>

    // FeedBack FollowUp ------------------

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/activity/feedback/")
    fun addFeedbackFollowUp(
        @Path("org_id") org_id: Int, @Body model: CustomerFollowUpDataItem
    ): Call<CustomerFollowUpResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/feedback/")
    fun getCustomerFeedbackList(
        @Path("org_id") org_id: Int,
        @Query("customer_id") customerId: Int?,
        @Query("module_id") moduleId: Int?,
        @Query("is_all_true") isAllTrue: Boolean,
        @Query("page_no") page_no: Int
    ): Call<CustomerFollowUpListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/feedback/{feedback_id}/")
    fun getCustomerFeedbackDetails(
        @Path("org_id") org_id: Int,
        @Path("feedback_id") feedback_id: Int,
    ): Call<CustomerFollowUpResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/activity/feedback/{feedback_id}/delete/")
    fun deleteCustomerFeedbackDetails(
        @Path("org_id") org_id: Int,
        @Path("feedback_id") feedback_id: Int,
    ): Call<CustomerFollowUpResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/activity/feedback/{feedback_id}/")
    fun updateCustomerFeedbackDetails(
        @Path("org_id") org_id: Int,
        @Path("feedback_id") feedback_id: Int,
        @Body model: CustomerFollowUpDataItem,
    ): Call<CustomerFollowUpResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/activity/attendance/check/")
    fun addAttendance(
        @Path("org_id") orgId: Int,
        @Body model: AddCheckInOutModel?,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/attendance/check/")
    fun getAttendance(
        @Path("org_id") orgId: Int
    ): Call<CheckInOutResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/activity/attendance/bulk/")
    fun addOfflineAttendance(
        @Path("org_id") org_id: Int,
        @Body model: UploadOfflineAttendanceModel?,
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/logs/")
    fun getStaffTrackingDetails(
        @Path("org_id") orgId: Int,
        @Query("date") date: String,
        @Query("user_id") staffId: Int?,
        @Query("page_no") pageNo: Int?
    ): Call<StaffTrackingDetailsResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/team/dashboard/")
    fun getTeamTrackingDetails(
        @Path("org_id") org_id: Int,
        @Query("by_date_range") date: String,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
        @Query("name") name: String,
        @Query("roles") stringBuilder: StringBuilder,
        @Query("reporting_manager") filterReportingManager: Int?,
        @Query("sort_by") sortByTc: String?,
        @Query("sort_order") sortTcOrder: String?,
        @Query("sort_by") sortByPc: String?,
        @Query("sort_order") sortPcOrder: String?,
        @Query("sort_by") sortByOrderValue: String?,
        @Query("sort_order") sortOrderValueOrder: String?,
        @Query("sort_by") sortByDuration: String?,
        @Query("sort_order") sortDurationOrder: String?,
        @Query("page_no") pageNo: Int
    ): Call<TeamTrackingDetailsResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/followup/?dd=true")
    fun getFollowUpList(
        @Path("org_id") org_id: Int
    ): Call<CustomerFeedbackListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/details/")
    fun getDailySalesReport(
        @Path("org_id") org_id: Int, @Query("user_id") user_id: Int?, @Query("date") date: String
    ): Call<DailySalesReportResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/leadform/{lead_id}/approval/")
    fun approveLead(
        @Path("org_id") org_id: Int, @Path("lead_id") leadId: Int, @Body jsonObject: JsonObject
    ): Call<AddLeadResponseModel>


    // Expense Tracker -----------------------------------

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/reimbursement-tracker/")
    fun getTotalExpenseTrackerList(
        @Path("org_id") org_id: Int,
        @Query("status") status: String,
        @Query("page_no") page: Int?,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<ExpenseTrackerResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/reimbursement-tracker/{rt_id}/")
    fun getTotalExpenseTrackerDetails(
        @Path("org_id") org_id: Int, @Path("rt_id") rt_id: Int
    ): Call<AddTotalExpenseResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/reimbursement/{rem_id}/")
    fun getExpenseTrackerDetails(
        @Path("org_id") org_id: Int, @Path("rem_id") rem_id: Int
    ): Call<AddExpenseResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/reimbursement/")
    fun getExpenseList(
        @Path("org_id") org_id: Int,
        @Query("rt_id") rt_id: Int?,
        @Query("page_no") page: Int?,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<ExpenseResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/reimbursement-tracker/status/")
    fun getApprovalRequestList(
        @Path("org_id") org_id: Int, @Query("status") status: String
    ): Call<ApprovalRequestResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/reimbursement-tracker/")
    fun addTotalExpenseTracker(
        @Path("org_id") org_id: Int, @Body model: ExpenseTrackerDataItem
    ): Call<AddTotalExpenseResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/reimbursement/")
    fun addExpense(
        @Path("org_id") org_id: Int, @Body model: ExpenseDataItem
    ): Call<AddExpenseResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/reimbursement-tracker/{rt_id}/delete/")
    fun deleteExpenseTracker(
        @Path("org_id") org_id: Int, @Path("rt_id") rt_id: Int
    ): Call<AddTotalExpenseResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/reimbursement/{rem_id}/delete/")
    fun deleteExpense(
        @Path("org_id") org_id: Int, @Path("rem_id") rem_id: Int
    ): Call<AddExpenseResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/reimbursement-tracker/{rt_id}/status/")
    fun updateExpenseTrackerStatus(
        @Path("org_id") org_id: Int, @Path("rt_id") rt_id: Int, @Body jsonObject: JsonObject
    ): Call<AddTotalExpenseResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/reimbursement-tracker/{rt_id}/")
    fun updateExpenseTracker(
        @Path("org_id") org_id: Int, @Path("rt_id") rt_id: Int, @Body model: ExpenseTrackerDataItem
    ): Call<AddTotalExpenseResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/reimbursement/{rem_id}/")
    fun updateExpense(
        @Path("org_id") org_id: Int, @Path("rem_id") rt_id: Int, @Body model: ExpenseDataItem
    ): Call<AddExpenseResponseModel>

    // Attendance------------
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/attendance/")
    fun getAttendanceList(
        @Path("org_id") org_id: Int, @Query("month") month: String, @Query("year") year: String
    ): Call<AttendanceResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/attendance/")
    fun updateAttendance(
        @Path("org_id") id: Int, @Body model: AttendanceDataItem
    ): Call<UpdateAttendanceResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/attendance/{attendance_id}/delete/")
    fun deleteAttendance(
        @Path("org_id") org_id: Int, @Path("attendance_id") attendanceId: Int
    ): Call<UpdateAttendanceResponseModel>


    // Notification------------
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v1/notification/fcm/")
    fun getNotificationList(
        @Query("org_id") org_id: Int, @Query("page_no") page_no: Int
    ): Call<NotificationResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v1/notification/fcm/")
    fun readNotifications(
        @Query("org_id") org_id: Int, @Body jsonObject: JsonObject
    ): Call<GenericResponseModel>


    // Targets--------------------------
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/target/")
    fun getCurrentlyActiveTargets(
        @Path("org_id") org_id: Int, @Query("get_currently_active") get_currently_active: Boolean
    ): Call<StaffCurrentlyActiveTargetResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/target/set/")
    fun getCurrentlyActiveTargetsForStaff(
        @Path("org_id") org_id: Int,
        @Query("user_id") staffId: Int?,
        @Query("get_currently_active") get_currently_active: Boolean
    ): Call<StaffCurrentlyActiveTargetResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/target/")
    fun getUpcomingAndClosedTargets(
        @Path("org_id") org_id: Int,
        @Query("upcoming") get_currently_active: Boolean,
        @Query("closed") closed: Boolean
    ): Call<StaffUpcomingAndClosedTargetResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/target/set/")
    fun getUpcomingAndClosedTargetsForStaff(
        @Path("org_id") org_id: Int,
        @Query("upcoming") get_currently_active: Boolean,
        @Query("closed") closed: Boolean,
        @Query("user_id") staffId: Int?
    ): Call<StaffUpcomingAndClosedTargetResponseModel>


    // Beat Route -------------------------

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/beatroute/")
    fun createBeatRoutePlan(
        @Path("org_id") org_id: Int, @Body model: CreateBeatRoutePlanModel
    ): Call<BeatRoutePlanResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/beatroute/{beat_route_plan_id}/")
    fun updateBeatRoutePlan(
        @Path("org_id") org_id: Int,
        @Path("beat_route_plan_id") beat_route_plan_id: Int?,
        @Body model: CreateBeatRoutePlanModel
    ): Call<BeatRoutePlanResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beatroute/")
    fun getActiveBeatPlanList(
        @Path("org_id") org_id: Int,
        @Query("date") date: String?,
        @Query("user_id") user_id: Int?,
        @Query("is_active") get_currently_active: Boolean?,
    ): Call<ActiveBeatRoutePlanResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beatroute/{beat_route_plan_id}/")
    fun getStaffBeatPlanInfoList(
        @Path("org_id") org_id: Int,
        @Path("beat_route_plan_id") beat_route_plan_id: Int,
        @Query("date") date: String?
    ): Call<ActiveBeatRoutePlanResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beatroute/")
    fun getBeatPlanList(
        @Path("org_id") org_id: Int,
        @Query("date") date: String?,
        @Query("status") status: String?,
        @Query("user_id") user_id: Int?,
        @Query("page_no") page_no: Int,
    ): Call<BeatRoutePlanListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beatroute/{beat_route_plan_id}/plan/")
    fun getDailyBeatPlanList(
        @Path("org_id") org_id: Int,
        @Path("beat_route_plan_id") beat_route_plan_id: Int,
        @Query("date") date: String?
    ): Call<BeatRouteDailyPlanResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beatroute/approval/")
    fun getPendingBeatPlanList(
        @Path("org_id") org_id: Int,
        @Query("status") start: String,
        @Query("page_no") page_no: Int,
    ): Call<BeatRoutePlanListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beatroute/{beat_route_plan_id}/customerlist/")
    fun getCustomerListForBeat(
        @Path("org_id") org_id: Int,
        @Path("beat_route_plan_id") beat_route_plan_id: Int,
        @Query("name") name: String?,
        @Query("list_type") type: String?,
        @Query("date") date: String?,
        @Query("page_no") page_no: Int
    ): Call<BeatRouteCustomerInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beat/{beat_id}/mapping/")
    fun getCustomerMappingListForBeat(
        @Path("org_id") org_id: Int,
        @Path("beat_id") beatId: Int?,
        @Query("name") name: String?,
        @Query("date") date: String?,
        @Query("beatrouteplan_id") beatrouteplan_id: Int?,
        @Query("for_beat_plan") for_beat_plan: Boolean?,
        @Query("get_customer_details") get_customer_details: Boolean?,
        @Query("assigned_status") assigned_status: String,
        @Query("customer_level") customer_level: String?,
        @Query("customer_parent_id") customer_parent_id: Int?,
        @Query("customer_type") customer_type: StringBuilder,
        @Query("sort_by") sortBy: String,
        @Query("sort_order") sortByOrder: String,
        @Query("page_no") page_no: Int
    ): Call<CustomerListForBeatModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/staff/{staff_id}/mapping/beats/")
    fun getOrgBeatList(
        @Path("org_id") orgId: Int,
        @Path("staff_id") staffId: Int,
        @Query("name") name: String?,
        @Query("dd") dd: Boolean?,
        @Query("page_no") pageNo: Int,
        @Query("page_size") pageSize: Int?,
        @Query("for_dump") forDump: Boolean?,
        @Query("last_synced_time") offlineDataLastSyncedTime: String?
    ): Call<OrgBeatListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/customer/{customer_id}/mapping/beats/")
    fun getCustomerBeatMapping(
        @Path("org_id") orgId: Int,
        @Path("customer_id") customerId: Int,
        @Query("selected") selected: Boolean?,
        @Query("page_no") pageNo: Int?,
    ): Call<OrgBeatListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/beatroute/{beatroute_id}/delete/")
    fun deleteBeatPlan(
        @Path("org_id") org_id: Int,
        @Path("beatroute_id") beat_route_id: Int,
        @Body model: JsonObject
    ): Call<BeatRoutePlanResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/beatroute/{beatroute_id}/approval/")
    fun beatPlanApprovedOrRejected(
        @Path("org_id") org_id: Int,
        @Path("beatroute_id") beat_route_id: Int,
        @Body model: BeatPlanModel
    ): Call<BeatRoutePlanResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/logs/")
    fun getBeatPlanHistory(
        @Path("org_id") org_id: Int,
        @Query("module_id") module_id: Int?,
        @Query("module_type") module_type: String,
        @Query("page_no") page_no: Int
    ): Call<CustomerFollowUpListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("https://api.postalpincode.in/pincode/{pinCode}")
    fun getPostalResponse(@Path("pinCode") pinCode: String): Call<ArrayList<PostalOfficeResponseModel>>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/validate/mobile/{module}/")
    fun verifyWhatsAppNumberLiveData(
        @Path("org_id") org_id: Int,
        @Path("module") module: String,
        @Query("mobile") mobile: String,
    ): Call<VerifyWhatsAppNumberResponseModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v1/user/logout/")
    fun logout(@Body model: LogOutModel): Call<GenericResponseModel>

    // Send Device Logs
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/activity/devicelogs/{user_id}/")
    fun sendDeviceLogs(
        @Path("org_id") orgId: Int,
        @Path("user_id") userId: Int,
        @Query("date") particularDate: String?,
        @Body model: DeviceActivityLogsPostModel?
    ): Call<GenericResponseModel>


    // Reminders
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/reminder/")
    fun getReminderList(
        @Path("org_id") org_id: Int,
        @Query("filter_by") filterBy: String?,
        @Query("date") particularDate: String?,
        @Query("page_no") page_no: Int
    ): Call<ReminderListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/activity/reminder/{id}/delete/")
    fun deleteReminder(
        @Path("org_id") org_id: Int, @Path("id") id: Int?
    ): Call<GenericResponseModel>


    // Beat
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beat/")
    fun getBeatList(
        @Path("org_id") org_id: Int,
        @Query("name") name: String?,
        @Query("staff_id") filterAssignedStaff: Int,
        @Query("customer_level") customer_level: String?,
        @Query("customer_parent_id") customer_parent_id: Int?,
        @Query("sort_by") sortBy: String,
        @Query("sort_order") sortByOrder: String,
        @Query("page_no") page_no: Int
    ): Call<BeatListResponseModel>
    
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beat/")
    fun getBeatList(
            @Path("org_id") org_id: Int,
            @Query("name") name: String?,
            @Query("sort_by") sortBy: String,
            @Query("sort_order") sortByOrder: String,
            @Query("page_no") page_no: Int,
            @Query("dd") dd: Boolean,
                   ): Call<BeatListResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beat/{beat_id}/")
    fun getBeatDetails(
        @Path("org_id") org_id: Int,
        @Path("beat_id") beat_id: Int,
    ): Call<BeatDetailsResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/beat/")
    fun createBeat(
        @Path("org_id") org_id: Int, @Body model: AddBeatModel
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/beat/{beat_id}/")
    fun updateBeat(
        @Path("org_id") org_id: Int, @Path("beat_id") beat_id: Int?, @Body model: AddBeatModel
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/beat/{beat_id}/delete/")
    fun deleteBeat(
        @Path("org_id") org_id: Int, @Path("beat_id") beat_id: Int, @Body jsonObject: JsonObject
    ): Call<GenericResponseModel>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beat/{beat_id}/mapping/staff/")
    fun getStaffListWithBeatMapping(
        @Path("org_id") org_id: Int,
        @Path("beat_id") beat_id: Int?,
        @Query("name") name: String?,
        @Query("get_selected_only") getSelectedOnly: Boolean?,
        @Query("page_no") currentPage: Int,
    ): Call<StaffListWithCustomerMappingModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/beat/{beat_id}/mapping/staff/")
    fun getStaffListWithBeatMappingWithData(
        @Path("org_id") org_id: Int,
        @Path("beat_id") beat_id: Int?,
        @Query("name") name: String?,
        @Query("get_selected_only") getSelectedOnly: Boolean?,
        @Query("page_no") currentPage: Int,
    ): Call<StaffListWithBeatMappingModel>

    //Live location
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/v2/organization/{org_id}/live-location/{user_id}/")
    fun pushLiveLocationData(
        @Path("org_id") orgId: Int,
        @Path("user_id") userId: Int,
        @Body liveLocationListModel: LiveLocationListModel?
    ): Call<GenericResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/live-location/{user_id}/")
    fun getLiveLocationData(
        @Path("org_id") orgId: Int,
        @Path("user_id") userId: Int,
        @Query("create_date") createDate: String
    ): Call<LiveLocationResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/attendance/{attendance_id}")
    fun getAttendanceDetails(
        @Path("org_id") org_id: Int, @Path("attendance_id") attendance_id: Int
    ): Call<CustomerFollowUpSingleResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/custom-forms/activity/{activity_type_id}/")
    fun getCustomFormInputList(
        @Path("org_id") orgId: Int, @Path("activity_type_id") id: Int
    ): Call<CustomFormCreationModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/team/status/")
    fun getTeamAggregatedInfo(
        @Path("org_id") orgId: Int, @Query("date") date: String
    ): Call<TeamAggregatedInfoResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/team/attendance/")
    fun getActiveInActiveStaffInfo(
        @Path("org_id") orgId: Int,
        @Query("date") date: String,
        @Query("status") status: String,
        @Query("page_no") currentPage: Int
    ): Call<StaffActiveResponseModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/tcpc/logs/")
    fun getStaffTcPcInfo(
        @Path("org_id") orgId: Int,
        @Query("date") date: String,
        @Query("user_id") staffId: Int,
        @Query("is_pc") isPc: Boolean,
        @Query("page_no") page: Int
    ): Call<StaffTcPcInfoModel>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/devicelogs/{user_id}/")
    fun getDeviceLogs(
        @Path("org_id") orgId: Int,
        @Path("user_id") staffId: Int,
        @Query("date") date: String,
        @Query("page_no") page: Int
    ): Call<DeviceActivityLogsResponseModel>
    
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/v2/organization/{org_id}/activity/checkin/status/")
    fun getUserCheckInStatus(
        @Path("org_id") orgId: Int,
    ): Call<CheckInStatus>
}
