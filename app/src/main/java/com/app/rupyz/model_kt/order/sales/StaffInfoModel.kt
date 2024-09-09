package com.app.rupyz.model_kt.order.sales

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.staff.StringListTypeConverter
import com.app.rupyz.model_kt.Headers
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.StaffModelForBeatData
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StaffInfoModel(

    @field: SerializedName("data")
    var data: List<StaffData>? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null
) : Parcelable

@TypeConverters(StringListTypeConverter::class)
@Entity(tableName = "staff_table")
@Parcelize
data class StaffData(
    @PrimaryKey
    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("user")
    var user: Int? = null,

    var profile_pic: Int? = null,

    @field:SerializedName("manager_staff_id")
    var managerStaffId: Int? = null,

    @field:SerializedName("total_payment_amount_received")
    var total_payment_amount_received: Double? = null,

    @field:SerializedName("manager_name")
    var managerName: String? = null,

    @field:SerializedName("parent")
    var parent: Int? = null,

    @Ignore
    @field:SerializedName("permissions")
    val permissions: List<String>? = null,

    @field:SerializedName("logo_image")
    var logoImage: String? = null,

    @field:SerializedName("org_name")
    var orgName: String? = null,

    @field:SerializedName("total_amount_sales")
    var total_amount_sales: Double? = null,

    @field:SerializedName("last_location_lat")
    var last_location_lat: Double? = null,

    @field:SerializedName("last_location_long")
    var last_location_long: Double? = null,

    @field:SerializedName("last_location_at")
    var last_location_at: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("profile_pic_url")
    var profilePicUrl: String? = null,

    @field:SerializedName("employee_id")
    var employeeId: String? = null,

    @field:SerializedName("mobile")
    var mobile: String? = null,

    @field:SerializedName("department")
    var department: String? = null,

    @field:SerializedName("pan_id")
    var panId: String? = null,

    @field:SerializedName("email")
    var email: String? = null,

    @field:SerializedName("address_line_1")
    var addressLine1: String? = null,

    @field:SerializedName("joining_date")
    var joiningDate: String? = null,

    @Ignore
    @field:SerializedName("bank_details")
    var bankDetails: BankDetails? = null,


    @field:SerializedName("roles")
    var roles: ArrayList<String>? = null,

    @Ignore
    @field:SerializedName("add_set")
    var addCustomerSet: ArrayList<Int>? = null,

    @Ignore
    @field:SerializedName("remove_set")
    var removeCustomerSet: ArrayList<Int>? = null,

    @Ignore
    @field:SerializedName("beats")
    var beats: List<Int>? = null,

    @Ignore
    @field:SerializedName("add_beats")
    var addBeats: List<Int>? = null,

    @Ignore
    @field:SerializedName("remove_beats")
    var removeBeats: List<Int>? = null,

    @Ignore
    @field:SerializedName("customer_set_info")
    var customerSetInfo: List<NameAndIdSetInfoModel>? = null,

    @field:SerializedName("allow_all_customer")
    var allow_all_customer: Boolean = false,

    @field:SerializedName("auto_assign_new_customers")
    var auto_assign_new_customers: Boolean = false,

    @field:SerializedName("disallow_all_customer")
    var disallow_all_customer: Boolean = false,

    @Ignore
    @field:SerializedName("select_customer")
    var selectCustomer: UpdateMappingModel? = null,

    @Ignore
    @field:SerializedName("select_beat")
    var selectBeat: UpdateMappingModel? = null,
    var isUpdatedExternally: Boolean = false,

    var isSelected: Boolean = false,
    var deSelectAllStaff: Boolean? = null

) : Parcelable

@Parcelize
data class BankDetails(

    @field:SerializedName("account_number")
    var accountNumber: String? = null,

    @field:SerializedName("branch")
    var branch: String? = null,

    @field:SerializedName("bank")
    var bank: String? = null,

    @field:SerializedName("ifsc_code")
    var ifscCode: String? = null

) : Parcelable

@Parcelize
data class StaffListWithCustomerMappingModel(

        @field: SerializedName("data")
    var data: List<NameAndIdSetInfoModel>? = null,

        @field:SerializedName("headers")
    val headers: Headers? = null,

        @field:SerializedName("message")
    var message: String? = null,

        @field:SerializedName("error")
    var error: Boolean? = null,
) : Parcelable

@Parcelize
data class StaffListWithBeatMappingModel(

    @field: SerializedName("data")
    val data: List<StaffModelForBeatData>? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,
) : Parcelable

@Parcelize
data class UpdateMappingModel(

    @field: SerializedName("add_set")
    var addSet: ArrayList<Int?>? = null,

    @field: SerializedName("remove_set")
    var removeSet: ArrayList<Int?>? = null,

    @field:SerializedName("disallow_all")
    var disallowAll: Boolean? = null,

    @field:SerializedName("allow_all")
    var allowAll: Boolean? = null
) : Parcelable








