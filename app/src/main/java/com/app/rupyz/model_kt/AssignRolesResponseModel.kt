package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AssignRolesResponseModel(

    @field:SerializedName("data")
    var data: List<AssignedRoleItem>? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("error")
    var error: Boolean? = null
) : Parcelable

@Entity(tableName = "staff_roles_table")
@Parcelize
data class AssignedRoleItem(
    @Ignore
    @field:SerializedName("permissions")
    var permissions: List<String>? = null,

    @field:SerializedName("organization")
    var organization: Int? = null,

    @field:SerializedName("name")
    var name: String? = null,

    var isSelected: Boolean? = null,

    @PrimaryKey
    @field:SerializedName("id")
    var id: Int? = null
) : Parcelable
