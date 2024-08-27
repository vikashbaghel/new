package com.app.rupyz.sales.orderdispatch

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityShipmentDetailsBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.helper.gone
import com.app.rupyz.generic.helper.visibility
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.IMAGE_UPLOAD_TAG
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.MyLocation
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.sales.home.MarkAttendanceBottomSheetDialogFragment
import com.app.rupyz.sales.orders.OrderViewModel
import com.app.rupyz.sales.pdfupload.PdfUploadViewModel
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.imageupload.MultipleImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.MultipleImageUploadListener
import com.app.rupyz.ui.organization.profile.activity.addphotos.AddPhotoListAdapter
import com.app.rupyz.ui.organization.profile.activity.addphotos.AddPhotoListAdapter.OnImageDeleteListener
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File

class ShipmentDetailsActivity : AppCompatActivity(),
    MultipleImageUploadListener, OnImageDeleteListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MarkAttendanceBottomSheetDialogFragment.IStartDayActionListener {
    private lateinit var binding: ActivityShipmentDetailsBinding
    private var paymentMode: String = "Paid"
    private lateinit var addPhotoListAdapter: AddPhotoListAdapter
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private lateinit var pdfUploadViewModel: PdfUploadViewModel

    private val pics: ArrayList<PicMapModel> = ArrayList()
    private val lrImage: ArrayList<Int> = ArrayList()
    private val photoModelList: ArrayList<AddedPhotoModel> = ArrayList()

    private var multiplePicCount = 0
    private var apiResponseCount = 0

    private lateinit var orderModel: DispatchedOrderModel

    private val orderViewModel: OrderViewModel by viewModels()

    private var geoLocationLat: Double = 0.00
    private var geoLocationLong: Double = 0.00

    private lateinit var locationPermissionUtils: LocationPermissionUtils
    private  var  fragment : MarkAttendanceBottomSheetDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShipmentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]
        pdfUploadViewModel = ViewModelProvider(this)[PdfUploadViewModel::class.java]

        locationPermissionUtils = LocationPermissionUtils(this, this)

        getUserCurrentLocation()

        initRecyclerView()
        initObservers()

        binding.spinnerPaymentMode.adapter = ArrayAdapter(
            this, R.layout.single_text_view_spinner_16dp_text,
            resources.getStringArray(R.array.shipment_payment_option)
        )

        binding.spinnerPaymentMode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    paymentMode = binding.spinnerPaymentMode.selectedItem.toString()
                }
            }

        if (intent.hasExtra(AppConstant.SHIPPED_ORDER)) {
            orderModel = intent.getParcelableExtra(AppConstant.SHIPPED_ORDER)!!
        } else if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
            binding.groupSteps.visibility = View.GONE
            orderModel = intent.getParcelableExtra(AppConstant.DISPATCH_MODEL)!!

            binding.tvCheckout.text = resources.getString(R.string.update)

            runOnUiThread {
                renderOrderDetails()
            }
        }

        if (orderModel.order_number != null) {
            binding.tvToolbarTitle.text =
                resources.getString(R.string.order_with_order_id, orderModel.order_number)
        }


        binding.clAddImage.setOnClickListener {
           /* MultipleImageUploadBottomSheetDialogFragment.newInstance(this).show(
                supportFragmentManager,
                IMAGE_UPLOAD_TAG
            )
*/
            val fragment = MultipleImageUploadBottomSheetDialogFragment.newInstance(this)
            val bundle = Bundle()
            bundle.putBoolean(AppConstant.DISABLE_GALLERY_PHOTO, true)
            bundle.putBoolean(AppConstant.DOCUMENT, true)
            fragment.arguments = bundle

            fragment.show(
                supportFragmentManager,
                IMAGE_UPLOAD_TAG
            )
        }

        binding.etFreightNumber.filters = arrayOf<InputFilter>(
            DigitsInputFilter(
                10,
                AppConstant.MAX_DIGIT_AFTER_DECIMAL
            )
        )

        binding.tvCheckout.setOnClickListener {
            validateData()
        }

        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvCancel.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }


    @SuppressLint("MissingPermission")
    private fun getUserCurrentLocation() {
        if (locationPermissionUtils.hasPermission()) {
            if (locationPermissionUtils.isGpsEnabled(this)) {
                setUpdatedLocationListener()
            } else {
                locationPermissionUtils.showEnableGpsDialog()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getUserCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpdatedLocationListener() {
        // for getting the current location update after every 2 seconds with high accuracy
        val myLocation = MyLocation()
        myLocation.getLocation(this, locationResult)
    }


    private var locationResult: MyLocation.LocationResult = object : MyLocation.LocationResult() {
        override fun gotLocation(myLocation: Location?) {

            if (Utils.isMockLocation(myLocation)) {
                val fragment =
                    MockLocationDetectedDialogFragment.getInstance(null)
                fragment.isCancelable = false
                fragment.show(
                    supportFragmentManager,
                    MockLocationDetectedDialogFragment::class.java.name
                )
            } else {
                myLocation?.let {
                    geoLocationLat = it.latitude
                    geoLocationLong = it.longitude
                    if (SharedPref.getInstance().getBoolean(AppConstant.START_DAY, false).not()) {
                        showStartDayDialog()
                    }
                }
            }
        }
    }

    private fun showStartDayDialog() {
        
        if (supportFragmentManager.fragments.firstOrNull { it.tag?.equals(MarkAttendanceBottomSheetDialogFragment::class.java.name) == true } == null){
            fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this, geoLocationLat, geoLocationLong)
            fragment?.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
        }else{
            if (fragment?.isVisible == false && fragment?.isAdded == false){
                fragment?.dismiss()
                supportFragmentManager.fragments.remove(fragment)
                fragment?.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
            }
        }
        
//        val fragment = MarkAttendanceBottomSheetDialogFragment.getInstance(this, geoLocationLat, geoLocationLong)
//        fragment.show(supportFragmentManager, MarkAttendanceBottomSheetDialogFragment::class.java.name)
  
    }

    private fun validateData() {
        pics.forEach { lrImage.add(it.id!!) }
        orderModel.lrNo = binding.etBillNumber.text.toString()
        orderModel.lrImages = lrImage
        orderModel.pics = pics
        orderModel.notes = binding.etNotes.text.toString()

        orderModel.invoice_number = binding.etOrderInvoiceNumber.text.toString()
        orderModel.transporter_name = binding.etTransporterName.text.toString()
        orderModel.transporter_mobile_number = binding.etTransporterContactNumber.text.toString()
        orderModel.driver_name = binding.etDriverName.text.toString()
        orderModel.driver_mobile_number = binding.etDriverContactNumber.text.toString()
        orderModel.vehicle_number = binding.etVehicleNumber.text.toString()
        orderModel.broker_information = binding.etBrokerDetails.text.toString()

        if (binding.etFreightNumber.text.toString().isNotEmpty()) {
            orderModel.freight_amount = binding.etFreightNumber.text.toString().toDouble()
        }

        orderModel.payment_information = paymentMode

        if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
            binding.progressBar.visibility = View.VISIBLE
            orderViewModel.updateOrderDispatched(
                orderModel,
                orderModel.order!!,
                orderModel.id!!
            )
        } else {

            startActivity(
                Intent(
                    this,
                    ShipmentReviewActivity::class.java
                ).putExtra(AppConstant.SHIPPED_ORDER, orderModel)
            )
        }
    }

    private fun renderOrderDetails() {
        if (orderModel.order != null) {
            binding.tvToolbarTitle.text = "Order No: " + orderModel.order
        }

        if (orderModel.lrNo != null && orderModel.lrNo!!.isNotEmpty()) {
            binding.etBillNumber.setText(orderModel.lrNo)
        }
        if (orderModel.invoice_number != null && orderModel.invoice_number!!.isNotEmpty()) {
            binding.etOrderInvoiceNumber.setText(orderModel.invoice_number)
        }
        if (orderModel.transporter_name != null && orderModel.transporter_name!!.isNotEmpty()) {
            binding.etTransporterName.setText(orderModel.transporter_name)
        }
        if (orderModel.transporter_mobile_number != null && orderModel.transporter_mobile_number!!.isNotEmpty()) {
            binding.etTransporterContactNumber.setText(orderModel.transporter_mobile_number)
        }
        if (orderModel.driver_name != null && orderModel.driver_name!!.isNotEmpty()) {
            binding.etDriverName.setText(orderModel.driver_name)
        }
        if (orderModel.driver_mobile_number != null && orderModel.driver_mobile_number!!.isNotEmpty()) {
            binding.etDriverContactNumber.setText(orderModel.driver_mobile_number)
        }
        if (orderModel.vehicle_number != null && orderModel.vehicle_number!!.isNotEmpty()) {
            binding.etVehicleNumber.setText(orderModel.vehicle_number)
        }
        if (orderModel.broker_information != null && orderModel.broker_information!!.isNotEmpty()) {
            binding.etBrokerDetails.setText(orderModel.broker_information)
        }
        if (orderModel.freight_amount != null && orderModel.freight_amount!! != 0.0) {
            binding.etFreightNumber.setText("${orderModel.freight_amount}")
        }

        if (orderModel.payment_information != null && orderModel.payment_information!!.isNotEmpty()) {
            if (orderModel.payment_information.equals(resources.getStringArray(R.array.shipment_payment_option)[0])) {
                binding.spinnerPaymentMode.setSelection(0)
            } else if (orderModel.payment_information.equals(resources.getStringArray(R.array.shipment_payment_option)[1])) {
                binding.spinnerPaymentMode.setSelection(1)
            }
        }


        if (orderModel.lrImagesUrl != null) {
            orderModel.lrImagesUrl!!.forEach { pic ->
                val addedPhotoModel = AddedPhotoModel()
                addedPhotoModel.imagePath = pic.url
                addedPhotoModel.onEditProduct = true
                addedPhotoModel.isUploading = false
                photoModelList.add(addedPhotoModel)
                pics.add(pic)
            }

            addPhotoListAdapter.notifyDataSetChanged()
        }

        if (orderModel.notes != null) {
            binding.etNotes.setText(orderModel.notes)
        }
    }


    private fun initRecyclerView() {
        binding.rvPicList.layoutManager = GridLayoutManager(this, 3)
        addPhotoListAdapter = AddPhotoListAdapter(photoModelList, this, false)
        binding.rvPicList.isNestedScrollingEnabled = false
        binding.rvPicList.adapter = addPhotoListAdapter
    }


    override fun onCameraUpload(fileName: String?) {
        if (photoModelList.size < 6) {
            binding.progressBar.visibility = View.VISIBLE
            multiplePicCount += 1
            lifecycleScope.launch {
                val compressedImageFile = Compressor.compress(
                    this@ShipmentDetailsActivity,
                    File(fileName!!)
                ) {
                    quality(30)
                    resolution(512, 512)
                    size(197_152)
                }
                imageUploadViewModel.uploadCredentials(compressedImageFile.path)
            }
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onGallerySingleUpload(fileName: String?) {
        if (photoModelList.size < 6) {
            binding.progressBar.visibility = View.VISIBLE
            multiplePicCount += 1
            lifecycleScope.launch {
                val compressedImageFile = Compressor.compress(
                    this@ShipmentDetailsActivity,
                    File(fileName!!)
                ) {
                    quality(30)
                    resolution(512, 512)
                    size(197_152)
                }
                imageUploadViewModel.uploadCredentials(compressedImageFile.path)
            }
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onGalleryMultipleUpload(fileList: List<String>?) {

        if (fileList != null && photoModelList.size < 6 && photoModelList.size + fileList.size <= 6) {
            val uploadedList = java.util.ArrayList<String>()
            if (fileList.size > 5) {
                for (i in 0..5) {
                    uploadedList.add(fileList[i])
                }
            } else if (fileList.size > 1) {
                uploadedList.addAll(fileList)
            }
            binding.progressBar.visibility = View.VISIBLE

            multiplePicCount += fileList.size

            for (i in fileList.indices) {
                lifecycleScope.launch {
                    val compressedImageFile = Compressor.compress(
                        this@ShipmentDetailsActivity,
                        File(fileList[i])
                    ) {
                        quality(30)
                        resolution(512, 512)
                        size(197_152)
                    }
                    imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun initObservers() {

        imageUploadViewModel.getCredLiveData()
            .observe(this) { (data, message): GenericResponseModel ->
                if (data?.id != null) {
                    binding.progressBar.gone()
                    apiResponseCount += 1
                    val picMapModel = PicMapModel()
                    picMapModel.id = data.id!!.toInt()
                    picMapModel.url = data.url
                    pics.add(picMapModel)
                    val addedPhotoModel = AddedPhotoModel()
                    addedPhotoModel.imagePath = picMapModel.url
                    addedPhotoModel.onEditProduct = true
                    addedPhotoModel.isUploading = false
                    photoModelList.add(addedPhotoModel)
                    photoModelList[0].isSelect = true

                    addPhotoListAdapter.notifyDataSetChanged()
                    if ( photoModelList.size==6)
                    {
                        binding.clAddImage.gone()
                    }

                    if (multiplePicCount == apiResponseCount) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                        multiplePicCount = 0
                        apiResponseCount = 0
                    }
                }
            }

        orderViewModel.orderDispatchLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }

            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
        }

        pdfUploadViewModel.uploadCredLiveData.observe(this) {
            if (it.error == false) {
                binding.progressBar.visibility=View.GONE
                it.data?.let { data ->
                    if (data.id != null) {
                        val picMapModel = PicMapModel()
                        picMapModel.id = data.id!!.toInt()
                        picMapModel.url = data.url
                        pics.add(picMapModel)

                        val addedPhotoModel = AddedPhotoModel()
                        addedPhotoModel.imagePath = picMapModel.url
                        addedPhotoModel.onEditProduct = true
                        addedPhotoModel.isUploading = false
                        photoModelList.add(addedPhotoModel)
                        photoModelList[0].isSelect = true
                        if ( photoModelList.size==6)
                        {
                            binding.clAddImage.gone()
                        }

                        addPhotoListAdapter.notifyDataSetChanged()
                        if (multiplePicCount == apiResponseCount) {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                            multiplePicCount = 0
                            apiResponseCount = 0
                        }
                    }
                }
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDeleteImage(position: Int, timeStamp: Long?) {
        if (photoModelList.size == 1) {
            photoModelList.clear()
            pics.clear()
            addPhotoListAdapter.notifyDataSetChanged()
        } else {
            photoModelList.removeAt(position)
            if (photoModelList.size<6)
            {
                binding.clAddImage.visibility()
            }
            pics.removeAt(position)
            initRecyclerView()
        }
    }

    override fun onImageSelect(orderModel: AddedPhotoModel, position: Int) {
    }

    override fun onPermissionsGiven() {
        super.onPermissionsGiven()
        getUserCurrentLocation()
    }

    override fun onPermissionsDenied() {
        super.onPermissionsDenied()
        getUserCurrentLocation()
    }

    override fun onGpsEnabled() {
        super.onGpsEnabled()
        Handler(Looper.myLooper()!!).postDelayed({
            getUserCurrentLocation()
        }, 2000)
    }

    override fun onDismissDialogForStartDay() {
        super.onDismissDialogForStartDay()
        finish()
    }
    override fun onUploadPdf() {
        super.onUploadPdf()
        onAddPdf()
    }
    private fun onAddPdf() {
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        uploadPdfActivityResultLauncher.launch(pdfIntent)
    }

    @SuppressLint("SuspiciousIndentation")
    private var uploadPdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {


            if (photoModelList.size < 6) {
                binding.progressBar.visibility = View.VISIBLE
                multiplePicCount += 1
                var isDocAvailable = false
                photoModelList.forEach {
                    if (it != null && it.type == AppConstant.DOCUMENT && !it.onEditProduct
                        && !it.isUploading
                    ) {
                        isDocAvailable = true
                        it.isUploading = true

                    }
                }
                val uri = result.data!!.data
                val path: String = FileUtils.getPdfFile(this, uri!!).absolutePath

                    pdfUploadViewModel.uploadCredentials(path)

                }
            } else {
                Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }
