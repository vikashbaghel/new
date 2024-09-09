package com.app.rupyz.model_kt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.rupyz.databse.customer.IntListConverter
import com.app.rupyz.databse.order.PicMapListTypeConverter
import com.app.rupyz.generic.model.product.PicMapModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExpenseResponseModel(

        @field:SerializedName("data")
        var data: List<ExpenseDataItem>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null
) : Parcelable

@TypeConverters(IntListConverter::class, PicMapListTypeConverter::class)
@Entity(tableName = "expenses_list_table")
@Parcelize
data class ExpenseDataItem(

        @field:SerializedName("amount")
        var amount: Double? = null,

        @field:SerializedName("bill_proof")
        var billProof: ArrayList<Int>? = null,

        @field:SerializedName("bill_proof_urls")
        var billProofUrls: ArrayList<PicMapModel>? = null,

        @field:SerializedName("description")
        var description: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("created_by")
        var createdBy: Int? = null,

        @field:SerializedName("reimbursementtracker")
        var reimbursementtracker: Int? = null,

        @field:SerializedName("expense_date_time")
        var expenseDateTime: String? = null,

        @field:SerializedName("updated_at")
        var updatedAt: String? = null,

        @field:SerializedName("organization")
        var organization: Int? = null,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("updated_by")
        var updatedBy: String? = null,

        @PrimaryKey
        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("user")
        var user: Int? = null,

        @field:SerializedName("source")
        var source: String? = null,

        var isSyncedToServer: Boolean? = null,

        var isUpdateReimbursementTracker: Boolean? = null

) : Parcelable

@Parcelize
data class AddExpenseResponseModel(

        @field:SerializedName("data")
        var data: ExpenseDataItem? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null
) : Parcelable
