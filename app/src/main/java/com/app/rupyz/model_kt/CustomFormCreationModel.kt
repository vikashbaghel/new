package com.app.rupyz.model_kt

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class CustomFormCreationModel(

        @field:SerializedName("data")
        val data: CustomFormCreationData? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("error")
        var error: Boolean? = null
) : Parcelable

@Parcelize
data class CustomFormCreationData(

        @field:SerializedName("sections")
        val sections: List<Sections?>? = null,

        @field:SerializedName("form_name")
        val formName: String? = null

) : Parcelable

@Parcelize
data class InputProps(

        @field:SerializedName("placeholder")
        val placeholder: String? = null,

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("options")
        val options: List<ValueLabelModel>? = null,

        @field:SerializedName("maxLength")
        val maxLength: Int? = null
) : Parcelable

@Parcelize
data class ValueLabelModel(

        @field:SerializedName("label")
        val label: String? = null,

        @field:SerializedName("value")
        val value: String? = null
) : Parcelable
@Parcelize
data class Sections(

        @field:SerializedName("form_items")
        val formItems: List<FormItemsItem?>? = null,

        @field:SerializedName("name")
        val name: String? = null
) : Parcelable

@Parcelize
data class FieldProps(

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("label")
        val label: String? = null,

        @field:SerializedName("required")
        val required: Boolean? = null
) : Parcelable

@Parcelize
data class FormItemsItem(

        @field:SerializedName("field_props")
        val fieldProps: FieldProps? = null,

        @field:SerializedName("type")
        val type: String? = null,

        @field:SerializedName("is_custom")
        val isCustom: Boolean? = null,

        @field:SerializedName("input_props")
        val inputProps: InputProps? = null,

        @field:SerializedName("status")
        val status: String? = null
) : Parcelable
