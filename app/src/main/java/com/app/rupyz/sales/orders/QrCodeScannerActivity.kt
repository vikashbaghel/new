package com.app.rupyz.sales.orders

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityQrCodeScannerBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.StringExtension.trimProductCodeFromScanner
import com.app.rupyz.generic.model.product.ProductDetailInfoModel
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.sales.cart.CartActivity
import com.app.rupyz.sales.product.IProductBottomSheetActionListener
import com.app.rupyz.sales.product.VariantsBottomSheetDialogFragment
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrCodeScannerActivity : BaseActivity(), IProductBottomSheetActionListener,
    InfoBottomSheetDialogFragment.IDismissDialogListener {
    private lateinit var binding: ActivityQrCodeScannerBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    private var cameraProvider: ProcessCameraProvider? = null

    private var customerId: Int? = null

    private var customerModel: CustomerData? = null

    private var cartListResponseModel: OrderData? = null

    private var isTelePhonicOrder: Boolean = false

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, -1)
        }

        if (intent.hasExtra(AppConstant.CUSTOMER)) {
            customerModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(AppConstant.CUSTOMER, CustomerData::class.java)
            } else {
                intent.getParcelableExtra(AppConstant.CUSTOMER)
            }
        }


        isTelePhonicOrder = intent.getBooleanExtra(AppConstant.IS_TELEPHONIC_ORDER, false)


        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        executeCamera()
        manageSharedPref()

        binding.ivBack.setOnClickListener {
            finish()
        }


        binding.ivCart.setOnClickListener {
            hideKeyboard()
            if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else {
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra(AppConstant.CUSTOMER_ID, customerId)
                intent.putExtra(AppConstant.CUSTOMER, customerModel)
                intent.putExtra(AppConstant.IS_TELEPHONIC_ORDER, isTelePhonicOrder)

                if (intent.hasExtra(AppConstant.ORDER_EDIT)) {
                    intent.putExtra(AppConstant.ORDER_EDIT, true)
                }

                startActivity(intent)
            }

        }
    }

    private fun executeCamera() {
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProvider = cameraProviderFuture.get()
        cameraProviderFuture.addListener({
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        binding.overlay.post {
            binding.overlay.setViewFinder()
        }
    }


    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {

        if (isDestroyed || isFinishing) {
            //This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            //java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return
        }

        cameraProvider?.unbindAll()

        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(binding.cameraPreview.width, binding.cameraPreview.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        class ScanningListener : ScanningResultListener {
            override fun onScanned(result: String) {
                runOnUiThread {
                    vibratePhone()
                    imageAnalysis.clearAnalyzer()
                    cameraProvider?.unbindAll()

                    val model = ProductList()
                    model.code = result

                    val fragmentTag = VariantsBottomSheetDialogFragment::class.java.name
                    val existingFragment =
                        supportFragmentManager.findFragmentByTag(fragmentTag)
                                as? VariantsBottomSheetDialogFragment

                    if (existingFragment?.isVisible != true) {
                        val fragment = VariantsBottomSheetDialogFragment.newInstance(
                            this@QrCodeScannerActivity,
                            model,
                            customerId,
                            0
                        )
                        val bundle = Bundle()
                        bundle.putBoolean(AppConstant.QR_CODE, true)
                        fragment.arguments = bundle
                        fragment.show(supportFragmentManager, fragmentTag)
                    }
                }
            }
        }

        val analyzer: ImageAnalysis.Analyzer = ZXingBarcodeAnalyzer(ScanningListener())

        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

    }

    override fun onDestroy() {
        super.onDestroy()
        // Shut down our background executor
        cameraExecutor.shutdown()
    }

    interface ScanningResultListener {
        fun onScanned(result: String)
    }

    override fun onDismissDialog() {
        super.onDismissDialog()
        manageSharedPref()
        executeCamera()
    }

    override fun onDismissDialogWithMessage(message: String) {
        super.onDismissDialogWithMessage(message)

        val fragmentTag = InfoBottomSheetDialogFragment::class.java.name
        val existingFragment =
            supportFragmentManager.findFragmentByTag(fragmentTag)
                    as? InfoBottomSheetDialogFragment

        val bundle = Bundle()
        bundle.putString(AppConstant.HEADING, resources.getString(R.string.unable_to_scan_qr))
        bundle.putString(
            AppConstant.MESSAGE,
            resources.getString(R.string.not_a_valid_product_code)
        )
        if (existingFragment?.isVisible != true) {
            val fragment = InfoBottomSheetDialogFragment.newInstance(this)
            fragment.isCancelable = false
            fragment.arguments = bundle
            fragment.show(supportFragmentManager, fragmentTag)
        }
    }

    override fun onProductAddToCartFromBottomSheet(model: ProductDetailInfoModel, position: Int) {
        manageSharedPref()
        executeCamera()
    }


    private fun manageSharedPref() {
        if (SharedPref.getInstance().getString(SharePrefConstant.CART_MODEL) != null) {
            val response = SharedPref.getInstance().getString(SharePrefConstant.CART_MODEL)

            if (!response.equals("")) {
                cartListResponseModel = Gson().fromJson(response, OrderData::class.java)

                if (cartListResponseModel?.customerId != null &&
                    cartListResponseModel?.customerId == customerId
                ) {
                    if (cartListResponseModel?.items.isNullOrEmpty().not()) {
                        binding.tvCartItemCount.text = "" + cartListResponseModel?.items?.size
                        binding.tvCartItemCount.visibility = View.VISIBLE
                    }
                }
            } else {
                binding.tvCartItemCount.visibility = View.GONE
            }
        }
    }

    override fun onDismissInformationDialog() {
        super.onDismissInformationDialog()
        executeCamera()
    }
}