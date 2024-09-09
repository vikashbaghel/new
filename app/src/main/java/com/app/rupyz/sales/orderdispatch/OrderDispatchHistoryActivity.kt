package com.app.rupyz.sales.orderdispatch

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrderDispatchHistoryBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.custom.DownloadPdfTask
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.FileUtils.openPdf
import com.app.rupyz.model_kt.CartItem
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.KeyValuePairModel
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.sales.orders.OrderDetailActivity
import com.app.rupyz.sales.orders.OrderViewModel
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class OrderDispatchHistoryActivity : BaseActivity(),
    ProductImageViewPagerAdapter.ProductImageClickListener {
    private lateinit var binding: ActivityOrderDispatchHistoryBinding
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var addPhotoListAdapter: LrPhotoListAdapter
    private lateinit var dispatchHistoryProductListAdapter: DispatchHistoryProductListAdapter
    private lateinit var shipmentBasicDetailsAdapter: ShipmentBasicDetailsAdapter

    private val pics: ArrayList<PicMapModel> = ArrayList()
    private val cartItems: ArrayList<CartItem> = ArrayList()
    private val shipmentBasicDetailsList: ArrayList<KeyValuePairModel> = ArrayList()

    private var isShipmentDetailNotAvailable = 0


    private var dispatchModel: DispatchedOrderModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDispatchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        initRecyclerView()

        initObservers()

        getDispatchDetails()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (intent.hasExtra(AppConstant.IMAGE_PREVIEW)){
            binding.ivEdit.visibility = View.GONE
        }

        binding.ivEdit.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(
                    this,
                    ShipmentDetailsActivity::class.java
                ).putExtra(AppConstant.ORDER_EDIT, true)
                    .putExtra(AppConstant.DISPATCH_MODEL, dispatchModel)
            )
        }
    }

    private fun getDispatchDetails() {
        if (intent.hasExtra(AppConstant.ORDER_ID) ||
            intent.hasExtra(AppConstant.DISPATCH_ID)
        ) {
            binding.progressBar.visibility = View.VISIBLE
            orderViewModel.getOrderDispatchedDetails(
                intent.getIntExtra(AppConstant.ORDER_ID, 0),
                intent.getIntExtra(AppConstant.DISPATCH_ID, 0), hasInternetConnection()
            )
        }
    }

    private var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            getDispatchDetails()
        }
    }


    private fun initRecyclerView() {
        binding.rvProductItem.layoutManager = LinearLayoutManager(this)
        dispatchHistoryProductListAdapter = DispatchHistoryProductListAdapter(cartItems)
        binding.rvProductItem.adapter = dispatchHistoryProductListAdapter


        binding.rvDetails.layoutManager = GridLayoutManager(this, 2)
        shipmentBasicDetailsAdapter = ShipmentBasicDetailsAdapter(shipmentBasicDetailsList)
        binding.rvDetails.adapter = shipmentBasicDetailsAdapter
        binding.rvPhotos.layoutManager = GridLayoutManager(this, 6)
        addPhotoListAdapter = LrPhotoListAdapter(pics, this)
        binding.rvPhotos.adapter = addPhotoListAdapter

    }

    private fun initObservers() {
        orderViewModel.orderDispatchDetailsLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE

            it.data?.let { model ->

                dispatchModel = model

                if (model.totalAmount != null) {
                    binding.tvDispatchValue.text =
                        CalculatorHelper().convertCommaSeparatedAmount(model.totalAmount, AppConstant.TWO_DECIMAL_POINTS)
                    binding.clTotalDispatchValue.visibility = View.VISIBLE
                }

                if (!model.dispatchOrderFileUrl.isNullOrEmpty()) {
                    if (hasInternetConnection()) {
                        binding.ivDownloadPdf.visibility = View.VISIBLE
                    }
                    binding.ivDownloadPdf.setOnClickListener {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            mGalleryPermissionResult.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            mGalleryPermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                }

                if (model.createdAt != null) {
                    binding.tvToolbarTitle.text =
                        DateFormatHelper.convertIsoToDateAndTimeFormat(model.createdAt)
                }

                if (model.order_number != null) {
                    binding.tvToolbarTitle.text =
                        resources.getString(R.string.order_with_order_id, model.order_number)
                }

                cartItems.clear()


                if (!model.items.isNullOrEmpty()) {
                    cartItems.addAll(model.items!!.filter { cartItem -> cartItem.dispatchQty!! > 0 })
                    binding.tvTotalItems.text = "${model.items?.size}"
                    dispatchHistoryProductListAdapter.notifyDataSetChanged()
                }

                shipmentBasicDetailsList.clear()
                shipmentBasicDetailsAdapter.notifyDataSetChanged()

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

                pics.clear()

                if (model.lrImagesUrl.isNullOrEmpty().not()) {
                    pics.addAll(model.lrImagesUrl!!)
                    addPhotoListAdapter.notifyDataSetChanged()
                } else {
                    binding.tvPhotoHd.visibility = View.GONE
                    isShipmentDetailNotAvailable += 1
                }

                if (model.notes != null && model.notes!!.isNotEmpty()) {
                    binding.tvNotesHd.visibility = View.VISIBLE
                    binding.tvNotes.visibility = View.VISIBLE
                    binding.tvNotes.text = model.notes
                } else {
                    binding.tvNotesHd.visibility = View.GONE
                    binding.tvNotes.visibility = View.GONE
                    isShipmentDetailNotAvailable += 1
                }

                if (isShipmentDetailNotAvailable == 3) {
                    binding.clBillDetails.visibility = View.GONE
                } else {
                    binding.clBillDetails.visibility = View.VISIBLE
                }

                isShipmentDetailNotAvailable = 0
            }
        }
    }

    private val mGalleryPermissionResult: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                val fileName = "Order_Id-${dispatchModel?.id}.pdf"
                DownloadPdfTask(this, dispatchModel?.dispatchOrderFileUrl!!, fileName)
            } else {
                Toast.makeText(
                    this,
                    "Media Permission is required to perform this action.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    override fun onPdfClick(position: Int,url:String) {
        super.onPdfClick(position,url)
        if (url!=null)
        {
            openPdf(url,this)
        }


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

    override fun onBackPressed() {
        if (intent.hasExtra(AppConstant.NOTIFICATION)) {
            startActivity(
                Intent(
                    this,
                    OrderDetailActivity::class.java
                ).putExtra(AppConstant.ORDER_ID, intent.getIntExtra(AppConstant.ORDER_ID, 0))
                    .putExtra(AppConstant.NOTIFICATION, true)
            )
        }
        finish()
    }
}