package com.app.rupyz.sales.orderdispatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityShipmentReviewBinding
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.KeyValuePairModel
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.sales.orders.OrderQuantitySetAdapter
import com.app.rupyz.sales.orders.OrderViewModel
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter.ProductImageClickListener

class ShipmentReviewActivity : AppCompatActivity(), ProductImageClickListener {
    private lateinit var binding: ActivityShipmentReviewBinding
    private lateinit var model: DispatchedOrderModel

    private lateinit var dispatchedProductListAdapter: ReviewShipmentProductListAdapter
    private lateinit var shipmentBasicDetailsAdapter: ShipmentBasicDetailsAdapter
    private lateinit var addPhotoListAdapter: LrPhotoListAdapter
    private lateinit var orderQuantitySetAdapter: OrderQuantitySetAdapter

    private val pics: ArrayList<PicMapModel> = ArrayList()
    private val shipmentBasicDetailsList: ArrayList<KeyValuePairModel> = ArrayList()
    private val cartItems: ArrayList<CartItem> = ArrayList()
    private var orderQuantitySetList: ArrayList<Map.Entry<String, Double>> = ArrayList()

    private lateinit var orderViewModel: OrderViewModel

    private var isShipmentDetailNotAvailable = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShipmentReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        initRecyclerView()

        initObservers()

        if (intent.hasExtra(AppConstant.SHIPPED_ORDER)) {
            model = intent.getParcelableExtra(AppConstant.SHIPPED_ORDER)!!

            cartItems.clear()

            if (model.order_number != null) {
                binding.tvToolbarTitle.text =
                    resources.getString(R.string.order_with_order_id, model.order_number)
            }

            if (!model.items.isNullOrEmpty()) {
                cartItems.addAll(model.items!!)
                dispatchedProductListAdapter.notifyDataSetChanged()

                orderQuantitySetList.addAll(CalculatorHelper().calculateTotalOrderQuantity(model.items!!, true))
                orderQuantitySetAdapter.notifyDataSetChanged()
            }

            if (model.lrNo != null && model.lrNo!!.isNotEmpty()) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(resources.getString(R.string.lr_bilty_number), model.lrNo)
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }

            if (model.invoice_number != null && model.invoice_number!!.isNotEmpty()) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(
                        resources.getString(R.string.order_invoice_no),
                        model.invoice_number
                    )
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }
            if (model.transporter_name != null && model.transporter_name!!.isNotEmpty()) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(
                        resources.getString(R.string.transporter_name),
                        model.transporter_name
                    )
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }
            if (model.transporter_mobile_number != null && model.transporter_mobile_number!!.isNotEmpty()) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(
                        resources.getString(R.string.transporter_contact_number),
                        model.transporter_mobile_number
                    )
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }
            if (model.driver_name != null && model.driver_name!!.isNotEmpty()) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(resources.getString(R.string.driver_name), model.driver_name)
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }
            if (model.driver_mobile_number != null && model.driver_mobile_number!!.isNotEmpty()) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(
                        resources.getString(R.string.driver_contact_number),
                        model.driver_mobile_number
                    )
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }
            if (model.vehicle_number != null && model.vehicle_number!!.isNotEmpty()) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(
                        resources.getString(R.string.vehicle_number),
                        model.vehicle_number
                    )
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }
            if (model.broker_information != null && model.broker_information!!.isNotEmpty()) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(
                        resources.getString(R.string.broker_details),
                        model.broker_information
                    )
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }
            if (model.freight_amount != null && model.freight_amount!! != 0.0) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(
                        resources.getString(R.string.freight),
                        "${model.freight_amount}"
                    )
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }

            if (model.payment_information != null && model.payment_information!!.isNotEmpty()) {
                shipmentBasicDetailsList.add(
                    KeyValuePairModel(
                        resources.getString(R.string.payment),
                        model.payment_information
                    )
                )
                shipmentBasicDetailsAdapter.notifyItemInserted(shipmentBasicDetailsList.size - 1)
            }

            if (shipmentBasicDetailsList.size == 0) {
                isShipmentDetailNotAvailable += 1
            }

            if (model.pics != null && model.pics!!.isNotEmpty()) {
                pics.addAll(model.pics!!)
                addPhotoListAdapter.notifyDataSetChanged()
            } else {
                binding.tvPhotoHd.visibility = View.GONE
                isShipmentDetailNotAvailable += 1
            }

            if (model.notes != null && model.notes!!.isNotEmpty()) {
                binding.tvNotesHd.visibility = View.VISIBLE
                binding.tvNotes.text = model.notes
            } else {
                binding.tvNotesHd.visibility = View.GONE
                isShipmentDetailNotAvailable += 1
            }

            if (isShipmentDetailNotAvailable == 3) {
                binding.clBillDetails.visibility = View.GONE
            }

            if (model.carryRemainingOrder == true) {
                model.isClosed = false
                binding.tvCarryItemInfo.text =
                    resources.getString(R.string.you_are_carry_remaining_order)
            } else {
                model.isClosed = true
                binding.tvCarryItemInfo.text =
                    resources.getString(R.string.you_are_not_carry_remaining_order)
            }
        }

        binding.tvCheckout.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvCheckout.isEnabled = false
            orderViewModel.createOrderDispatched(model, model.orderId!!)
        }

        binding.ivBack.setOnClickListener { finish() }

        binding.tvCancel.setOnClickListener {
            finish()
        }
    }

    private fun initObservers() {
        orderViewModel.orderDispatchLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE

            binding.tvCheckout.isEnabled = true

            if (it.error == false) {
                val intent = Intent(this, CreateShipmentOrderActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvProductItem.layoutManager = LinearLayoutManager(this)
        dispatchedProductListAdapter = ReviewShipmentProductListAdapter(cartItems)
        binding.rvProductItem.adapter = dispatchedProductListAdapter

        binding.rvDetails.layoutManager = GridLayoutManager(this, 2)
        shipmentBasicDetailsAdapter = ShipmentBasicDetailsAdapter(shipmentBasicDetailsList)
        binding.rvDetails.adapter = shipmentBasicDetailsAdapter

        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        addPhotoListAdapter = LrPhotoListAdapter(pics, this)
        binding.rvPhotos.adapter = addPhotoListAdapter

        binding.rvOrderQuantity.layoutManager = LinearLayoutManager(this)
        orderQuantitySetAdapter = OrderQuantitySetAdapter(orderQuantitySetList)
        binding.rvOrderQuantity.adapter = orderQuantitySetAdapter

    }

    override fun onImageClick(position: Int) {
        if (pics.size > 0) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            for (pic in pics) {
                val model = ImageViewModel(0, 0, pic.url)
                imageViewModelArrayList.add(model)
            }

            imageListModel.data = imageViewModelArrayList
            startActivity(
                Intent(this, OrgPhotosViewActivity::class.java)
                    .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                    .putExtra(AppConstant.IMAGE_POSITION, position)
            )
        } else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show()
        }
    }
}