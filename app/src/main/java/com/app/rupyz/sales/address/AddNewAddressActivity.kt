package com.app.rupyz.sales.address

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.ListPopupWindow
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddNewAddressBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.helper.setSafeOnClickListener
import com.app.rupyz.generic.helper.showView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.PostOfficeItem
import com.app.rupyz.ui.more.MoreViewModel

class AddNewAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityAddNewAddressBinding
    private lateinit var addressViewModel: AddressViewModel
    private val moreViewModel: MoreViewModel by viewModels()

    private var customerId: Int = -1

    private lateinit var addedAddressModel: CustomerAddressDataItem
    private lateinit var stateList: MutableList<String>
    private var isPopupShown = false

    private var companyName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addressViewModel = ViewModelProvider(this)[AddressViewModel::class.java]

        stateList = resources.getStringArray(R.array.states).toMutableList()

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, -1)
        }

        setStateSpinnerItems()
        initPinCodeLayout()

        changeAddressTypeSelection(ADDRESS_TYPE_SELECTED.WAREHOUSE)

        if (intent.hasExtra(AppConstant.ORDER_ADDRESS_ID)) {
            addedAddressModel = intent.getParcelableExtra(AppConstant.ORDER_ADDRESS_ID)!!
            binding.btnAdd.text = resources.getString(R.string.update)
            binding.tvToolbarTitle.text = resources.getString(R.string.update_address)

            initPreLayout(addedAddressModel)
        }

        initObservers()

        binding.imgClose.setOnClickListener { finish() }
        binding.btnAdd.setOnClickListener { submitData() }
        binding.btnCancel.setOnClickListener { finish() }

        binding.tvWarehouseType.setSafeOnClickListener {
            changeAddressTypeSelection(ADDRESS_TYPE_SELECTED.WAREHOUSE)
        }

        binding.tvGodownType.setSafeOnClickListener {
            changeAddressTypeSelection(ADDRESS_TYPE_SELECTED.GODOWN)
        }

        binding.tvOtherType.setSafeOnClickListener {
            changeAddressTypeSelection(ADDRESS_TYPE_SELECTED.OTHER)
        }

        binding.tlCompanyName.setEndIconOnClickListener {
            animateView(false)

            binding.tvOtherType.setTextColor(resources.getColor(R.color.color_727176, null))
            binding.tvOtherType.setBackgroundResource(R.drawable.address_type_selector)

            binding.etCompanyName.setText("")
            companyName = ADDRESS_TYPE_SELECTED.WAREHOUSE.name
            changeAddressTypeSelection(ADDRESS_TYPE_SELECTED.WAREHOUSE)
        }

        binding.etCompanyName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                companyName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun changeAddressTypeSelection(addressType: ADDRESS_TYPE_SELECTED) {
        when (addressType) {
            ADDRESS_TYPE_SELECTED.WAREHOUSE -> {
                binding.tvWarehouseType.setTextColor(resources.getColor(R.color.theme_purple, null))
                binding.tvWarehouseType.setBackgroundResource(R.drawable.address_type_selector_selected)

                binding.tvGodownType.setTextColor(resources.getColor(R.color.color_727176, null))
                binding.tvGodownType.setBackgroundResource(R.drawable.address_type_selector)

                binding.tvOtherType.setTextColor(resources.getColor(R.color.color_727176, null))
                binding.tvOtherType.setBackgroundResource(R.drawable.address_type_selector)
                companyName = ADDRESS_TYPE_SELECTED.WAREHOUSE.toString()
            }

            ADDRESS_TYPE_SELECTED.GODOWN -> {
                binding.tvWarehouseType.setTextColor(resources.getColor(R.color.color_727176, null))
                binding.tvWarehouseType.setBackgroundResource(R.drawable.address_type_selector)

                binding.tvGodownType.setTextColor(resources.getColor(R.color.theme_purple, null))
                binding.tvGodownType.setBackgroundResource(R.drawable.address_type_selector_selected)

                binding.tvOtherType.setTextColor(resources.getColor(R.color.color_727176, null))
                binding.tvOtherType.setBackgroundResource(R.drawable.address_type_selector)
                companyName = ADDRESS_TYPE_SELECTED.GODOWN.toString()

            }

            ADDRESS_TYPE_SELECTED.OTHER -> {
                binding.tvWarehouseType.setTextColor(resources.getColor(R.color.color_727176, null))
                binding.tvWarehouseType.setBackgroundResource(R.drawable.address_type_selector)

                binding.tvGodownType.setTextColor(resources.getColor(R.color.color_727176, null))
                binding.tvGodownType.setBackgroundResource(R.drawable.address_type_selector)

                binding.tvOtherType.setTextColor(resources.getColor(R.color.theme_purple, null))
                binding.tvOtherType.setBackgroundResource(R.drawable.address_type_selector_selected)

                binding.etCompanyName.setText("")
                companyName = ""

                animateView(true)
            }
        }
    }

    private fun animateView(isOther: Boolean) {
        if (isOther) {
            binding.tvWarehouseType.hideView()
            binding.tvGodownType.hideView()
            binding.tlCompanyName.showView()
        } else {
            binding.tvWarehouseType.showView()
            binding.tvGodownType.showView()
            binding.tlCompanyName.hideView()
        }
    }

    private fun initPinCodeLayout() {
        binding.etPinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etPinCode.hasFocus() && s.toString()
                        .isNotBlank() && s.toString().length == 6
                ) {
                    if (hasInternetConnection()) {
                        binding.pinCodeProgressBar.visibility = View.VISIBLE
                        moreViewModel.getPostalResponse(s.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setStateSpinnerItems() {
        val spinnerList = resources.getStringArray(R.array.states).toList()
        val listPopupWindow = ListPopupWindow(this, null, androidx.appcompat.R.attr.listPopupWindowStyle)
        listPopupWindow.anchorView = binding.tlSpinnerState
        val adapter = ArrayAdapter(this, R.layout.single_text_view_spinner_16dp_text, spinnerList)
        listPopupWindow.isModal = true
        listPopupWindow.setAdapter(adapter)
        binding.spinnerState.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
            null
        )
        listPopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            if (position < spinnerList.size) {
                binding.spinnerState.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                    null
                )
                binding.spinnerState.setText(spinnerList[position])
            }
            listPopupWindow.dismiss()
            isPopupShown = false
        }
        binding.spinnerState.setOnClickListener {
            binding.tlSpinnerState.endIconDrawable = null
            binding.spinnerState.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_inverted_black, null),
                null
            )
            if (isPopupShown) {
                listPopupWindow.dismiss()
            } else {
                listPopupWindow.show()
            }
            isPopupShown = !isPopupShown
        }

        listPopupWindow.setOnDismissListener {
            binding.spinnerState.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.drawable.ic_arrow_drop_down_black, null),
                null
            )
            isPopupShown = false
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initPreLayout(addressModel: CustomerAddressDataItem) {
        if (addressModel.name != null) {
            when (addressModel.name?.lowercase()) {
                resources.getString(R.string.warehouse).lowercase() -> {
                    changeAddressTypeSelection(ADDRESS_TYPE_SELECTED.WAREHOUSE)
                }

                resources.getString(R.string.godown).lowercase() -> {
                    changeAddressTypeSelection(ADDRESS_TYPE_SELECTED.GODOWN)
                }

                else -> {
                    changeAddressTypeSelection(ADDRESS_TYPE_SELECTED.OTHER)
                    binding.etCompanyName.setText(addressModel.name)
                }
            }

            companyName = addressModel.name ?: ""
        }

        if (addressModel.addressLine1 != null) {
            binding.etAddressLine1.setText(addressModel.addressLine1)
            binding.etAddressLine1.setSelection(addressModel.addressLine1?.length!!)
        }

        if (addressModel.city != null) {
            binding.etCity.setText(addressModel.city)
            binding.etCity.setSelection(addressModel.city?.length!!)
        }

        if (addressModel.state != null) {
            binding.spinnerState.setText(addressModel.state)
            binding.spinnerState.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            binding.tlSpinnerState.endIconDrawable =
                resources.getDrawable(R.drawable.ic_cross_outline, null)
        }

        if (addressModel.pincode != null) {
            binding.etPinCode.setText(addressModel.pincode)
            binding.etPinCode.setSelection(addressModel.pincode?.length!!)
        }

        binding.cbPrimaryAddress.isChecked = addressModel.isDefault

    }

    private fun initObservers() {
        addressViewModel.addAddressLiveData.observe(this) {
            binding.btnAdd.isEnabled = true
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            }
        }

        moreViewModel.postalCodeResponseLiveData.observe(this) {
            binding.pinCodeProgressBar.visibility = View.GONE
            if (it.status == "Success") {
                it.postOffice?.let { postal ->
                    if (postal.isNotEmpty()) {
                        autoFillPostalOffice(postal[0])
                    }
                }
            } else if (it.status != "Failed") {
                showToast(it.message)
            }
        }
    }

    private fun autoFillPostalOffice(postOfficeItem: PostOfficeItem) {
        binding.etCity.setText(postOfficeItem.district)
        if (postOfficeItem.state.isNullOrEmpty().not()) {
            binding.spinnerState.setText(postOfficeItem.state)
        }
    }

    private fun submitData() {
        when {
            companyName.isEmpty() -> {
                showToast("Name Required!")
            }

            binding.etAddressLine1.text.trim().toString().isEmpty() -> {
                showToast("Address Required!")
            }

            binding.etCity.text.trim().toString().isEmpty() -> {
                showToast("City Required!")
            }

            binding.spinnerState.text.isEmpty() -> {
                showToast("state Required!")
            }

            stateList.drop(1).contains(binding.spinnerState.text.toString()).not() -> {
                showToast("Please select valid state!!")
            }

            binding.etPinCode.text.trim().toString().isEmpty() -> {
                showToast("Pin Code Required!")
            }

            else -> {

                binding.progressBar.visibility = View.VISIBLE
                binding.btnAdd.isEnabled = false

                val addressModel = CustomerAddressDataItem()
                addressModel.name = companyName
                addressModel.addressLine1 = binding.etAddressLine1.text.toString()
                addressModel.city = binding.etCity.text.trim().toString()
                addressModel.state = binding.spinnerState.text.toString()
                addressModel.pincode = binding.etPinCode.text.trim().toString()
                addressModel.isDefault = binding.cbPrimaryAddress.isChecked

                if (intent.hasExtra(AppConstant.ORDER_ADDRESS_ID)) {
                    addressModel.id = addedAddressModel.id
                }

                addressViewModel.saveAddress(customerId, addressModel, hasInternetConnection())
            }
        }
    }

}

enum class ADDRESS_TYPE_SELECTED(val value: String) {
    WAREHOUSE("Warehouse"),
    GODOWN("Godown"),
    OTHER("Other")
}