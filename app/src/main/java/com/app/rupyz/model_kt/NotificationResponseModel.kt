package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationResponseModel(

    @field:SerializedName("data")
    val data: NotificationDataModel? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Parcelize
data class NotificationDataModel(

    @field:SerializedName("unread_count")
    val unreadCount: Int? = null,

    @field:SerializedName("results")
    val results: List<NotificationListItemModel>? = null
) : Parcelable

@Parcelize
data class NotificationListItemModel(

    @field:SerializedName("app")
    val app: String? = null,

    @field:SerializedName("profile")
    val profile: Int? = null,

    @field:SerializedName("icon_image")
    val iconImage: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("is_seen")
    var isSeen: Boolean? = null,

    @field:SerializedName("submodule_name")
    val submoduleName: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("module_uid")
    val moduleUid: String? = null,

    @field:SerializedName("payload")
    val payload: NotificationPayloadModel? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("module_name")
    val moduleName: String? = null,

    @field:SerializedName("user")
    val user: Int? = null
) : Parcelable


@Parcelize
data class NotificationPayloadModel(

    @field:SerializedName("module_uid")
    val moduleUid: Int? = null,

    @field:SerializedName("parent_module_uid")
    val parentModuleUid: Int? = null,

    @field:SerializedName("module_name")
    var module_name: String? = null,

    @field:SerializedName("parent_module_name")
    var parentModuleName: String? = null

) : Parcelable
