package com.app.rupyz.sales.address

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.ActivityConfirmAddressBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.makeToast
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.Address
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.orders.PaymentAgainstOrderActivity

class ConfirmAddressActivity : BaseActivity(), AddressListAdapter.AddressListener {
    private lateinit var binding: ActivityConfirmAddressBinding
    private lateinit var addressListAdapter: AddressListAdapter

    private lateinit var addressViewModel: AddressViewModel

    private var addressList = ArrayList<CustomerAddressDataItem>()

    private lateinit var cartListResponseModel: OrderData
    private var customerId: Int = -1
    private var confirmAddress: CustomerAddressDataItem? = null
    private var customerModel: CustomerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addressViewModel = ViewModelProvider(this)[AddressViewModel::class.java]

        if (intent.hasExtra(AppConstant.CART_ITEM)) {
            cartListResponseModel = intent.getParcelableExtra(AppConstant.CART_ITEM)!!
            if (cartListResponseModel.customerId != null) {
                customerId = cartListResponseModel.customerId!!
            }
        } else if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, -2)
        }

        if (intent.hasExtra(AppConstant.CUSTOMER)) {
            customerModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.CUSTOMER, CustomerData::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.CUSTOMER)
            }
        }

        initRecyclerView()

        initObservers()

        binding.tvAddNewAddress.setOnClickListener {
            startActivity(
                    Intent(
                            this,
                            AddNewAddressActivity::class.java
                    ).putExtra(AppConstant.CUSTOMER_ID, customerId)
            )
        }

        binding.ivBack.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }

        binding.btnAdd.setOnClickListener {
            if (addressList.isEmpty()) {
                makeToast("Add Address!!")
            } else if (confirmAddress == null) {
                makeToast("Please Select Address!!")
            } else {
                onDeliverToThisAddress()
            }
        }
    }

    private fun initObservers() {
        addressViewModel.addressLiveData.observe(this) {
            if (it.data.isNullOrEmpty().not()) {
                addressList.addAll(it.data!!)

                if (intent.hasExtra(AppConstant.ORDER_ADDRESS_ID)) {
                    val addressId = intent.getIntExtra(AppConstant.ORDER_ADDRESS_ID, 0)
                    val index = addressList.indexOfFirst { address -> address.id == addressId }
                    if (index != -1) {
                        addressList[index].isSelected = true
                        confirmAddress = addressList[index]
                    } else {
                        addressList[0].isSelected = true
                        confirmAddress = addressList[0]
                    }
                } else {
                    addressList[0].isSelected = true
                    confirmAddress = addressList[0]
                }

                addressListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvAddressList.layoutManager = linearLayoutManager
        addressListAdapter = AddressListAdapter(addressList, this)
        binding.rvAddressList.adapter = addressListAdapter
    }

    override fun onResume() {
        super.onResume()
        addressList.clear()
        addressViewModel.getAddressList(customerId, hasInternetConnection())
    }

    override fun onSelectAddress(model: CustomerAddressDataItem, positon: Int) {
        if (positon < addressList.size) {
            addressList.forEach { it.isSelected = false }
            addressList[positon].isSelected = true
            addressListAdapter.notifyDataSetChanged()

            confirmAddress = model
        }
    }

    private fun onDeliverToThisAddress() {
        if (intent.hasExtra(AppConstant.ORDER_ADDRESS_ID)) {
            val intent = Intent()
            intent.putExtra(AppConstant.ORDER_ADDRESS_ID, confirmAddress)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            val address = Address()
            address.id = confirmAddress?.id
            address.name = confirmAddress?.name
            address.addressLine1 = confirmAddress?.addressLine1
            address.city = confirmAddress?.city
            address.state = confirmAddress?.state
            address.pincode = confirmAddress?.pincode

            cartListResponseModel.address = address
            startActivity(
                    Intent(this, PaymentAgainstOrderActivity::class.java
                    ).putExtra(AppConstant.CART_ITEM, cartListResponseModel
                    ).putExtra(AppConstant.CUSTOMER, customerModel)
            )

        }
    }

    override fun onEditAddress(model: CustomerAddressDataItem, positon: Int) {
        startActivity(
                Intent(
                        this,
                        AddNewAddressActivity::class.java
                ).putExtra(AppConstant.CUSTOMER_ID, customerId)
                        .putExtra(AppConstant.ORDER_ADDRESS_ID, model)
        )
    }
}