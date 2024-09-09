package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLoginResponseModel(

    @field:SerializedName("data")
    var data: UserInfoData? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class Credentials(

    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("refresh_token")
    val refreshToken: String? = null,

    @field:SerializedName("scope")
    val scope: String? = null,

    @field:SerializedName("token_type")
    val tokenType: String? = null,

    @field:SerializedName("expires_in")
    val expiresIn: Int? = null
) : Parcelable

@Parcelize
data class UserInfoData(

    @field:SerializedName("otp_ref")
    var otpRef: String? = null,

    @field:SerializedName("staff_name")
    val staffName: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("access_type")
    val accessType: String? = null,

    @field:SerializedName("auth")
    val auth: String? = null,

    @field:SerializedName("credentials")
    val credentials: Credentials? = null,

    @field:SerializedName("roles")
    val roles: List<String?>? = null,

    @field:SerializedName("login")
    val login: Boolean? = null,

    @field:SerializedName("permissions")
    val permissions: List<String>? = null,

    @field:SerializedName("staff_id")
    val staffId: Int? = null,

    @field:SerializedName("hierarchy")
    val hierarchy: Boolean = false,

    @field:SerializedName("org_name")
    val orgName: String? = null,

    @field:SerializedName("logo_image")
    val logoImage: String? = null,

    @field:SerializedName("department")
    val department: String? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("checkin_time")
    val checkinTime: String? = null,

    @field:SerializedName("is_active")
    val isActive: Boolean? = null,

    @field:SerializedName("ord_id")
    val ordId: Int? = null,

    @field:SerializedName("org_ids")
    val orgIds: ArrayList<OrganizationInfoModel> = ArrayList(),

    @field:SerializedName("last_login")
    val lastLogin: String? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("profile_pic")
    val profilePic: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("org_id")
    val orgId: Int? = null,

    @field:SerializedName("employee_id")
    val employeeId: String? = null,

    @field:SerializedName("checkout_time")
    val checkoutTime: String? = null,

    @field:SerializedName("designation")
    val designation: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("is_demo")
    val isDemo: Boolean? = null,

    @field:SerializedName("pan_id")
    val panId: String? = null,

    @field:SerializedName("equifax_generated")
    val equifaxGenerated: Boolean? = null,

    @field:SerializedName("reg_step")
    val regStep: Int? = null,

    @field:SerializedName("address_line_1")
    val addressLine1: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("address_line_2")
    val addressLine2: String? = null,

    @field:SerializedName("profile_pic_url")
    val profilePicUrl: String? = null,

    @field:SerializedName("experian_generated")
    val experianGenerated: Boolean? = null,

    @field:SerializedName("pincode")
    val pincode: String? = null,

    @field:SerializedName("din_no")
    val dinNo: String? = null,

    @field:SerializedName("middle_name")
    val middleName: String? = null,

    @field:SerializedName("full_name")
    val fullName: String? = null,

    @field:SerializedName("rupyz_id")
    val rupyzId: String? = null,

    var updatedAt: Long? = null,

    @field:SerializedName("nationality")
    val nationality: String? = null,

    @field:SerializedName("father_name")
    val fatherName: String? = null,

    @field:SerializedName("profile_id")
    val profileId: Int? = null,

    @field:SerializedName("dob")
    val dob: String? = null,

    @field:SerializedName("org_preferences")
    val orgPreferences: PreferenceData? = null
) : Parcelable


@Parcelize
data class SaveAttendanceModel(
        val date: String? = null,
        val checkIn: Boolean? = null,
        val checkOut: Boolean? = null,
        val attendanceType: String?= null
) : Parcelable