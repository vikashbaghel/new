package com.app.rupyz.model_kt

import android.os.Parcelable
import com.app.rupyz.generic.model.product.PicMapModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class S3UploadCredResponse(

    @field:SerializedName("data")
    val data: List<DataItem>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("error")
    val error: Boolean? = null
) : Parcelable

@Parcelize
data class DataItem(

    @field:SerializedName("content_type")
    val contentType: String? = null,

    @field:SerializedName("content_disposition")
    val contentDisposition: String? = null,

    @field:SerializedName("success_action_status")
    val successActionStatus: String? = null,

    @field:SerializedName("id")
    var id: String? = null,

    @field:SerializedName("otp_ref")
    var otpRef: String? = null,

    @field:SerializedName("url")
    var url: String? = null,

    @field:SerializedName("is_exists")
    var isExists: Boolean? = null,

    @field:SerializedName("time_stamp")
    var time_stamp: Long? = null,

    @field:SerializedName("acl")
    val acl: String? = null,

    @field:SerializedName("is_used")
    var isUsed: Boolean? = null
) : Parcelable

@Parcelize
data class S3UploadResponse(

    @field:SerializedName("file_id")
    val fileId: String? = null
) : Parcelable


@Parcelize
data class GenericResponseModel(

    @field:SerializedName("data")
    var data: DataItem? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null,

    var errorCode: Int? = null
) : Parcelable


@Parcelize
data class S3ConfirmResponseModel(

    @field:SerializedName("data")
    var data: PicMapModel? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

