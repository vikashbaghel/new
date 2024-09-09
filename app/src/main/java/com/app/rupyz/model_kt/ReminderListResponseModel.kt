package com.app.rupyz.model_kt

import android.os.Parcelable
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.sales.reminder.SortReminderListByDate
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReminderListResponseModel(
    @field:SerializedName("data")
    val data: ReminderDataResponseModel? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class ReminderDataResponseModel(
    @field:SerializedName("results")
    val result: ArrayList<ReminderItemModel>? = null,

    @field:SerializedName("count")
    var count: Int? = null,

    ) : Parcelable

@Parcelize
data class ReminderItemModel(
    @field:SerializedName("business_name")
    val businessName: String? = null,

    @field:SerializedName("followup_updated_by")
    val followupUpdatedBy: Int? = null,

    @field:SerializedName("comments")
    val comments: String? = null,

    @field:SerializedName("module_type")
    val moduleType: String? = null,

    @field:SerializedName("logo_image_url")
    val logoImageUrl: String? = null,

    @field:SerializedName("followup_updated_at")
    val followupUpdatedAt: String? = null,

    @field:SerializedName("followup_created_by_name")
    val followupCreatedByName: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("created_by_name")
    val createdByName: String? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("followup_created_by")
    val followupCreatedBy: Int? = null,

    @field:SerializedName("updated_by_name")
    val updatedByName: String? = null,

    @field:SerializedName("followup_created_at")
    val followupCreatedAt: String? = null,

    @field:SerializedName("module_id")
    val moduleId: Int? = null,

    @field:SerializedName("followup_updated_by_name")
    val followupUpdatedByName: String? = null,

    @field:SerializedName("due_datetime")
    val dueDatetime: String? = null,

    var filterDate: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: String? = null,

    @field:SerializedName("pics_urls")
    val picsUrls: ArrayList<PicMapModel>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("followup_id")
    val followup_id: Int? = null,

    var viewType: Int? = null,

    @field:SerializedName("feedback_type")
    val feedbackType: String? = null
) : Parcelable, SortReminderListByDate(TYPE_GENERAL)

data class DateItem(
    val date: String
) : SortReminderListByDate(TYPE_DATE)
