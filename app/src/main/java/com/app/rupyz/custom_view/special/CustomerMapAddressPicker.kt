package com.app.rupyz.custom_view.special

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentActivity
import com.app.rupyz.R
import com.app.rupyz.custom_view.type.FormItemType
import com.app.rupyz.databinding.ItemViewCustomePickAddressBinding
import com.app.rupyz.dialog.CustomerGeoMapLocationDialog
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.model_kt.FormItemsItem
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.google.android.gms.maps.model.LatLng

class CustomerMapAddressPicker : LinearLayoutCompat {

    private val binding =
        ItemViewCustomePickAddressBinding.inflate(LayoutInflater.from(context), this, true)
    private var formFields: FormItemsItem? = null
    private var latLong = LatLng(0.toDouble(), 0.toDouble())
    private val customerGeoMapLocationDialog = CustomerGeoMapLocationDialog.newInstance()
    private var formItemType: FormItemType? = null
    private var onLocationFetchedListener : ((Address?) -> Unit)? = null
    private var geoAddress: Address? = null


    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context) : super(context, null) {
        init(null)
    }

    fun init(attrs: AttributeSet?) {
        orientation = VERTICAL
        binding.clChangeLocation.setOnClickListener {
            if ((context as FragmentActivity).supportFragmentManager.findFragmentByTag(
                    CustomerGeoMapLocationDialog.TAG
                ) == null
            ) {
                customerGeoMapLocationDialog.show(
                    (context as FragmentActivity).supportFragmentManager,
                    CustomerGeoMapLocationDialog.TAG
                )
            }
        }
        customerGeoMapLocationDialog.setOnLocationFetched { latLng, completeAddress ->
            latLong = latLng
            binding.gvLocation.showView()
            binding.tvSetNewLocation.text = resources.getString(R.string.update_location)
            binding.tvGeoLocation.text = completeAddress.trim()
        }

        binding.tvRemoveLocation.setOnClickListener {
            binding.tvSetNewLocation.text = resources.getString(R.string.fetch_location)
            binding.gvLocation.hideView()
            latLong = LatLng(0.toDouble(), 0.toDouble())
            binding.tvGeoLocation.text = null
         /*   onLocationFetchedListener?.invoke(null)*/
        }
        
        customerGeoMapLocationDialog.setOnLocationFetchedListener { address->
            this.geoAddress = address
            onLocationFetchedListener?.invoke(address)
        }
        setDefaultStyle()
    }


    fun getFieldValue(): NameAndValueSetInfoModel {
        val model = NameAndValueSetInfoModel()
        model.name = formFields?.fieldProps?.name
        model.label = formFields?.fieldProps?.label
        model.isRequired = formFields?.fieldProps?.required
        model.isCustom = formFields?.isCustom
        model.type = formFields?.type
        model.subModuleType = formFields?.type
        model.subModuleId = formFields?.fieldProps?.name
        model.value = binding.tvGeoLocation.text.toString()
        model.geoAddressValue = binding.tvGeoLocation.text.toString()
        model.geoLocationLong = latLong.longitude
        model.geoLocationLat = latLong.latitude
        return model
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDefaultStyle() {
        formFields?.let {
            binding.gvLocation.hideView()
            binding.tvTitle.text = it.inputProps?.placeholder
        }
    }

    fun setMapData(formFieldsData: FormItemsItem) {
        formFields = formFieldsData
        init(null)
        setDefaultStyle()
    }


    override fun getRootView(): View {
        return binding.root
    }


    fun getFormFields(): FormItemsItem? {
        return formFields
    }

    fun getFieldType(): FormItemType {
        return formItemType ?: FormItemType.DROPDOWN
    }

    fun setFormItemType(type: FormItemType) {
        this.formItemType = type
    }

    fun setValue(address: String, geoLocationLat: Double, geoLocationLong: Double) {
        latLong = LatLng(geoLocationLat, geoLocationLong)
        binding.gvLocation.showView()
        binding.tvSetNewLocation.text = resources.getString(R.string.update_location)
        binding.tvGeoLocation.text = address
    }
    
    fun setOnLocationFetchedListener(onLocationFetched : (Address?) -> Unit){
        this.onLocationFetchedListener = onLocationFetched
    }

}