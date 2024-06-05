package com.app.rupyz.sales.orderdispatch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.itemdecorator.DividerItemDecorator
import com.app.rupyz.databinding.ActivityCreateShipmentOrderBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.orders.OrderViewModel

class CreateShipmentOrderActivity : BaseActivity(),
    DispatchedProductListAdapter.IDispatchProductUncheckListener {
    private lateinit var binding: ActivityCreateShipmentOrderBinding
    private lateinit var orderViewModel: OrderViewModel
    private var orderData: OrderData? = null

    private val cartItems: ArrayList<CartItem> = ArrayList()
    private val dispatchedItem: ArrayList<CartItem> = ArrayList()

    lateinit var dispatchedProductListAdapter: DispatchedProductListAdapter
    private var dispatchOrderModel: DispatchedOrderModel? = null

    private var isSingleUncheck = false
    private var isTotalListChecked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateShipmentOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        dispatchOrderModel = DispatchedOrderModel()

        initRecyclerView()

        initObservers()

        if (intent.hasExtra(AppConstant.ORDER_ID)) {
            binding.progressBar.visibility = View.VISIBLE
            orderViewModel.getOrderDataById(
                intent.getIntExtra(
                    AppConstant.ORDER_ID, 0
                ),
                hasInternetConnection()
            )
        }

        binding.cbYes.setOnCheckedChangeListener { _, check ->
            Utils.hideKeyboard(this)
            if (check) {
                binding.cbNo.isChecked = false
                dispatchOrderModel?.carryRemainingOrder = true
            }
        }

        binding.cbNo.setOnCheckedChangeListener { _, check ->
            Utils.hideKeyboard(this)
            if (check) {
                binding.cbYes.isChecked = false
                dispatchOrderModel?.carryRemainingOrder = false
            }
        }


        binding.tvCheckout.setOnClickListener {
            Utils.hideKeyboard(this)
            dispatchedItem.clear()


            cartItems.forEach {
                if (it.isSelected == true && it.dispatchQty == 0.0) {
                    Toast.makeText(
                        this,
                        "Shipment quantity can not be empty or zero",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (it.isSelected == true) {
                    dispatchedItem.add(it)
                }
            }

            if (dispatchedItem.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please select at-least one product to be dispatch!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                dispatchOrderModel?.items = dispatchedItem

                startActivity(
                    Intent(
                        this,
                        ShipmentDetailsActivity::class.java
                    ).putExtra(AppConstant.SHIPPED_ORDER, dispatchOrderModel)
                )
            }
        }

        binding.cbSelectAll.setOnCheckedChangeListener { _, check ->
            Utils.hideKeyboard(this)

            if (check) {
                if (!isTotalListChecked) {
                    cartItems.forEachIndexed { index, cartItem ->
                        cartItem.isSelected = true
                        dispatchedProductListAdapter.notifyItemChanged(index)
                    }
                    isSingleUncheck = false
                    isTotalListChecked = true
                }
            } else {
                if (!isSingleUncheck) {
                    cartItems.forEachIndexed { index, cartItem ->
                        cartItem.isSelected = false
                        dispatchedProductListAdapter.notifyItemChanged(index)
                    }
                    isTotalListChecked = false
                    isSingleUncheck = false
                }
            }
        }

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun initRecyclerView() {
        binding.rvProductItem.layoutManager = LinearLayoutManager(this)
        val itemDecoration =
            DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.item_divider_gray))
        binding.rvProductItem.addItemDecoration(itemDecoration)
        dispatchedProductListAdapter = DispatchedProductListAdapter(cartItems, this)
        binding.rvProductItem.adapter = dispatchedProductListAdapter
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        orderViewModel.getOrderByIdLiveData().observe(this) { data ->
            data.data?.let { model ->
                binding.progressBar.visibility = View.GONE
                binding.clProductList.visibility = View.VISIBLE

                orderData = model

                dispatchOrderModel?.order_number = orderData!!.orderId
                dispatchOrderModel?.orderId = orderData!!.id
                dispatchOrderModel?.carryRemainingOrder = true

                binding.tvToolbarTitle.text =
                    resources.getString(R.string.order_with_order_id, orderData!!.orderId)

                if (orderData != null && !orderData?.items.isNullOrEmpty()) {
                    orderData?.items?.forEach { it.isSelected = true }
                    cartItems.addAll(orderData?.items!!.filter { it.qty!!.minus(it.totalDispatchedQty!!) > 0 })

                    dispatchedProductListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val newIntent = Intent()
        setResult(RESULT_OK, newIntent)
        finish()
    }

    override fun onCheckChange(model: CartItem) {
        Utils.hideKeyboard(this)
        var isUnCheck = false

        isSingleUncheck = false
        isTotalListChecked = true

        cartItems.forEach { cartItem ->
            if (cartItem.isSelected == false) {
                isUnCheck = true
                isSingleUncheck = true
                isTotalListChecked = false
            }
        }

        binding.cbSelectAll.isChecked = !isUnCheck
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }
}