package com.app.rupyz.model_kt.order.customer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerDetailsResponseModel(
	val data: Data? = null,
	val message: String? = null,
	val error: Boolean? = null
) : Parcelable

@Parcelize
data class Data(
	val pincode: String? = null,
	val paymentTerm: String? = null,
	val city: String? = null,
	val mobile: String? = null,
	val createdAt: String? = null,
	val gstin: String? = null,
	val profileLogo: String? = null,
	val panId: String? = null,
	val updatedAt: String? = null,
	val organization: Int? = null,
	val name: String? = null,
	val creditLimit: Int? = null,
	val addressLine1: String? = null,
	val id: Int? = null,
	val contactPersonName: String? = null,
	val outstandingAmount: Int? = null,
	val state: String? = null,
	val addressLine2: String? = null,
	val email: String? = null
) : Parcelable
