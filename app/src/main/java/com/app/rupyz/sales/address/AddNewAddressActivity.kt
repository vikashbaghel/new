package com.app.rupyz.sales.address

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddNewAddressBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.PostOfficeItem
import com.app.rupyz.ui.more.MoreViewModel
import com.app.rupyz.ui.organization.profile.adapter.CustomAutoCompleteAdapter

class AddNewAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityAddNewAddressBinding
    private lateinit var addressViewModel: AddressViewModel
    private val moreViewModel: MoreViewModel by viewModels()

    private var customerId: Int = -1

    private lateinit var addedAddressModel: CustomerAddressDataItem
    lateinit var stateList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addressViewModel = ViewModelProvider(this)[AddressViewModel::class.java]

        stateList = resources.getStringArray(R.array.states).toMutableList()

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, -1)
        }

        initStateSpinner()

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

    }

    private fun initStateSpinner() {
        val adapter = CustomAutoCompleteAdapter(this, stateList)
        binding.spinnerState.threshold = 0
        binding.spinnerState.setAdapter(adapter)

        binding.spinnerState.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, _, _ ->
                    binding.ivClearStateName.visibility = View.VISIBLE
                    binding.ivDropDown.visibility = View.GONE
                }

        binding.spinnerState.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.spinnerState.showDropDown()
            }
        }

        binding.spinnerState.setOnClickListener {
            binding.spinnerState.showDropDown()
        }

        binding.ivClearStateName.setOnClickListener {
            binding.spinnerState.setText("")
            binding.ivClearStateName.visibility = View.GONE
            binding.ivDropDown.visibility = View.VISIBLE
        }

        binding.etPinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etPinCode.hasFocus() && s.toString().isNotBlank() && s.toString().length == 6) {
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

    private fun initPreLayout(addressModel: CustomerAddressDataItem) {
        if (addressModel.name != null) {
            binding.etCompanyName.setText(addressModel.name)
            binding.etCompanyName.setSelection(addressModel.name?.length!!)
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
            binding.ivDropDown.visibility = View.GONE
            binding.ivClearStateName.visibility = View.VISIBLE
        }

        if (addressModel.pincode != null) {
            binding.etPinCode.setText(addressModel.pincode)
            binding.etPinCode.setSelection(addressModel.pincode?.length!!)
        }

        binding.cbPrimaryAddress.isChecked = addressModel.isDefault?: false

    }

    private fun initObservers() {
        addressViewModel.addAddressLiveData.observe(this) {
            binding.btnAdd.isEnabled = true
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
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
            binding.ivClearStateName.visibility = View.VISIBLE
            binding.ivDropDown.visibility = View.GONE
        }
    }

    private fun submitData() {
        when {
            binding.etCompanyName.text.trim().toString().isEmpty() -> {
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
                addressModel.name = binding.etCompanyName.text.toString()
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