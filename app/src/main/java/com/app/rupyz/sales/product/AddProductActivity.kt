package com.app.rupyz.sales.product

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.InputFilter
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.toHtml
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrgAddProductBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.IMAGE_UPLOAD_TAG
import com.app.rupyz.generic.utils.StringUtils
import com.app.rupyz.generic.utils.Validations
import com.app.rupyz.generic.utils.getPath
import com.app.rupyz.model_kt.AddProductModel
import com.app.rupyz.model_kt.AddSpecificationModel
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.PackagingLevelModel
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.imageupload.MultipleImageUploadBottomSheetDialogFragment.Companion.newInstance
import com.app.rupyz.ui.imageupload.MultipleImageUploadListener
import com.app.rupyz.ui.organization.profile.activity.addphotos.ProductPhotoListAdapter
import com.app.rupyz.ui.organization.profile.activity.addproduct.PackagingLevelAdapter
import com.app.rupyz.ui.organization.profile.activity.addproduct.UnitSelectedListener
import com.app.rupyz.ui.organization.profile.adapter.SpecificationListAdapter
import com.google.gson.JsonObject
import com.yalantis.ucrop.UCrop
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects

class AddProductActivity : BaseActivity(), SpecificationListAdapter.OnDelete,
    MultipleImageUploadListener, ProductPhotoListAdapter.OnImageDeleteListener,
    UnitSelectedListener, PackagingLevelAdapter.IPackagingUnitListener {
    private lateinit var binding: ActivityOrgAddProductBinding
    var adapter: SpecificationListAdapter? = null

    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private lateinit var productViewModel: ProductViewModel

    private var addPhotoListAdapter: ProductPhotoListAdapter? = null
    private lateinit var packagingLevelAdapter: PackagingLevelAdapter

    private var singleItemMap = HashMap<String, String>()
    private val specificationList = ArrayList<AddSpecificationModel>()
    private val pics: ArrayList<PicMapModel> = ArrayList()
    private val packagingLevelList = ArrayList<PackagingLevelModel>()
    private var photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()

    private var selectedUnitForProduct = ""
    private var selectedMrpUnitForProduct: String? = null
    private var selectedPackagingUnitForProduct: String? = null
    private var strSpecificationKey: String? = null
    private var strSpecificationDescription: String? = null

    private val specification = JsonObject()

    private var editProductId = 0
    private var gstSelected: Double? = null
    private var gstType = 0
    private var selectedPhotoForDisplay: Int? = null
    private var editPhotoPosition = -1
    private var multiplePicCount = 0
    private var displayPic = -1
    private var addPackagingUnitPosition = -1

    private var gstTypeRadioButton: RadioButton? = null
    private var addProductDialog: Dialog? = null
    private var tvAddImageDialogHeading: TextView? = null
    private var tvAddProductDialogHeading: TextView? = null
    private var tvAddProductPercentage: TextView? = null
    private var tvAddImagePercentage: TextView? = null
    private var addImageProgressBar: ProgressBar? = null
    private var addProductProgressBar: ProgressBar? = null
    private var addImageDialogLayout: ConstraintLayout? = null


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrgAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        initRecyclerView()
        initPackagingLevelRv()
        initLayout()

        if (intent != null && intent.hasExtra(AppConstant.EDIT_PRODUCT)) {
            binding.mainContent.visibility = View.GONE
            initPreLayout(intent.getIntExtra(AppConstant.PRODUCT_ID, 0))
            binding.btnAddProduct.text = "Update"
            binding.tvToolbarTitle.text = "Update product"
        } else {
            photoModelList.add(null)
            addPhotoListAdapter!!.notifyItemInserted(0)
        }

        initObservers()

        productViewModel.getCategoryList("")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initLayout() {
        addProductDialog = Dialog(this)
        addProductDialog?.setContentView(R.layout.add_product_dialog)
        addProductDialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        addProductDialog?.setCancelable(false)

        tvAddImageDialogHeading = addProductDialog?.findViewById(R.id.tv_uploading_pic_heading)
        tvAddProductDialogHeading = addProductDialog?.findViewById(R.id.tv_uploading_type_heading)
        tvAddProductPercentage =
            addProductDialog?.findViewById(R.id.tv_product_uploading_percentage)
        tvAddImagePercentage = addProductDialog?.findViewById(R.id.tv_photo_uploading_percentage)
        addImageProgressBar = addProductDialog?.findViewById(R.id.photo_progress_bar)
        addProductProgressBar = addProductDialog?.findViewById(R.id.product_progress_bar)
        addImageDialogLayout = addProductDialog?.findViewById(R.id.cl_image_upload)

        binding.edtMrpPrice.filters = arrayOf<InputFilter>(
            DigitsInputFilter(
                10,
                AppConstant.FOUR_DIGIT_AFTER_DECIMAL
            )
        )
        binding.edtPrice.filters = arrayOf<InputFilter>(
            DigitsInputFilter(
                10,
                AppConstant.FOUR_DIGIT_AFTER_DECIMAL
            )
        )

        binding.spinnerGst.adapter = ArrayAdapter<Any?>(
            this, R.layout.single_text_view_spinner_16dp_text,
            this.resources.getStringArray(R.array.gst_list)
        )
        binding.spinnerGst.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                gstSelected = binding.spinnerGst.selectedItem.toString().toDouble()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.tvUnit.setOnClickListener {
            ProductUnitBottomSheetDialogFragment(
                this,
                AppConstant.BUYERS_UNIT
            ).show(supportFragmentManager, "BuyersUnit")
        }
        binding.tvMrpUnit.setOnClickListener {
            ProductUnitBottomSheetDialogFragment(this, AppConstant.MRP_UNIT)
                .show(supportFragmentManager, "MrpUnit")
        }

        binding.edtDescription.setOnTouchListener { v, event ->
            if (v.id == R.id.edt_description) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        binding.edtSpecificationDescription.setOnTouchListener { v, event ->
            if (v.id == R.id.edt_specification_description) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        packagingLevelList.add(PackagingLevelModel())
        packagingLevelAdapter.notifyItemInserted(0)
        packagingLevelAdapter.notifyItemRangeChanged(
            0,
            1
        )

        binding.btnAddPackagingLevel.setOnClickListener { validatePackagingLevel() }

        binding.imgClose.setOnClickListener {
            finish()
        }
        binding.btnAddSpecification.setOnClickListener {
            if (validateSpecification()) {
                singleItemMap[strSpecificationKey!!] = strSpecificationDescription!!
                specificationList.add(
                    AddSpecificationModel(
                        strSpecificationKey,
                        strSpecificationDescription
                    )
                )
                createSpecification()
            }
        }

        binding.acTvCategory.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.acTvCategory.showDropDown()
            }
        }

        binding.acTvCategory.setOnClickListener {
            binding.acTvCategory.showDropDown()
        }

        binding.btnAddProduct.setOnClickListener {
            validateProduct()
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun validatePackagingLevel() {
        if (selectedUnitForProduct.isEmpty()) {
            Toast.makeText(
                this@AddProductActivity,
                "Please Select Buyers Unit",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            addPackagingLevel()
        }
    }

    private fun validatePackagingLevelList(): Pair<Boolean, String> {
        var isValidPackaging = true

        var pair = Pair(true, "")
        if (packagingLevelList.isEmpty()) {
            pair = Pair(false, "Please enter at-least one packaging Unit")
        } else {
            packagingLevelList.forEach {
                if (it.unit.isNullOrEmpty() || it.size == null) {
                    isValidPackaging = false
                    pair = Pair(false, "Enter the packaging unit and size")
                }
                return@forEach
            }
        }

        return if (isValidPackaging) {
            Pair(true, "")
        } else {
            pair
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initPreLayout(id: Int) {
        disableTouch()
        binding.progressBar.visibility = View.VISIBLE
        editProductId = id
        productViewModel.getProductDetails(
            editProductId,
            customerId = null,
            hasInternetConnection()
        )
    }

    private fun validateProduct() {
        gstType = binding.radioGroupGst.checkedRadioButtonId
        for (map in singleItemMap) {
            specification.addProperty(map.key, map.value)
        }
        if (binding.acTvCategory.text.toString() == "") {
            Toast.makeText(
                this@AddProductActivity,
                "Product Category Required.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.acTvCategory.text.toString().length <= 2) {
            Toast.makeText(
                this@AddProductActivity,
                "Product category length should be more than 1",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.etName.text.toString() == "") {
            Toast.makeText(this@AddProductActivity, "Product Name Required.", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.etProductCode.text.toString() == "") {
            Toast.makeText(this@AddProductActivity, "Product Code Required.", Toast.LENGTH_SHORT)
                .show()
        } else if (!Validations().isValidProductCode(binding.etProductCode.text.toString())) {
            Toast.makeText(
                this@AddProductActivity,
                "Valid Product Code Required.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.etHsnCode.text.toString() == "") {
            Toast.makeText(
                this@AddProductActivity,
                "Product HSN Code Required.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (gstType == -1) {
            Toast.makeText(
                this@AddProductActivity,
                "Please select any GST type.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.edtMrpPrice.text.toString() == "") {
            Toast.makeText(this@AddProductActivity, "Please add MRP price", Toast.LENGTH_SHORT)
                .show()
        } else if (selectedMrpUnitForProduct == null) {
            Toast.makeText(
                this@AddProductActivity,
                "Please select product MRP unit.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.edtPrice.text.toString() == "") {
            Toast.makeText(
                this@AddProductActivity,
                "Please add Buyer's price",
                Toast.LENGTH_SHORT
            ).show()
        } else if (selectedUnitForProduct.isEmpty()) {
            Toast.makeText(
                this@AddProductActivity,
                "Please select Buyer's unit.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (validatePackagingLevelList().first.not()) {
            Toast.makeText(
                this@AddProductActivity,
                validatePackagingLevelList().second,
                Toast.LENGTH_SHORT
            ).show()
        } else if (photoModelList.size < 2) Toast.makeText(
            this@AddProductActivity,
            "At-least 1 image required.",
            Toast.LENGTH_SHORT
        ).show() else if (selectedPhotoForDisplay == null) {
            Toast.makeText(
                this@AddProductActivity,
                "Please select one image for display",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            addProductImage()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addProductImage() {

        if (intent.hasExtra(AppConstant.EDIT_PRODUCT)) {
            tvAddProductDialogHeading?.text = resources.getString(R.string.updating_product)
        } else {
            tvAddProductDialogHeading?.text = resources.getString(R.string.adding_product)
        }

        var isEditImageAvailable = false
        if (pics.size > 0) {
            startImageUploadProgressBar()
            addImageDialogLayout?.visibility = View.VISIBLE
            val photoCountForUploading = photoModelList.filter { it?.onEditProduct == false }.size
            tvAddImageDialogHeading?.text = "Uploading $photoCountForUploading Photo"
            addProductDialog?.show()
            for (i in pics.size - 1 until photoModelList.size) {
                if (photoModelList[i]?.imagePath != null) {
                    if (photoModelList[i]?.onEditProduct!!.not()) {
                        isEditImageAvailable = true
                        lifecycleScope.launch {
                            val compressedImageFile = Compressor.compress(
                                this@AddProductActivity,
                                File(photoModelList[i]!!.imagePath!!)
                            ) {
                                quality(100)
                                size(4_197_152)
                            }
                            imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                        }
                    }
                }
            }
        } else {
            for (i in photoModelList.indices) {
                if (photoModelList[i]?.imagePath != null) {
                    isEditImageAvailable = true
                    addImageDialogLayout?.visibility = View.VISIBLE
                    val photoCountForUploading = photoModelList.filterNotNull().size
                    tvAddImageDialogHeading?.text = "Uploading $photoCountForUploading Photo"
                    startImageUploadProgressBar()
                    addProductDialog?.show()
                    lifecycleScope.launch {
                        val compressedImageFile = Compressor.compress(
                            this@AddProductActivity,
                            File(photoModelList[i]!!.imagePath!!)
                        ) {
                            quality(100)
                            size(4_197_152)
                        }
                        imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                    }
                }
            }
        }
        if (!isEditImageAvailable) {
            addImageDialogLayout?.visibility = View.GONE
            addProductDialog?.show()
            startProductUploadProgressBar()
            addProduct()
        }
    }

    private fun startProductUploadProgressBar() {
        val progress = 10
        object : CountDownTimer(10000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var time: Int = progress - (millisUntilFinished / 1000).toInt()
                time *= 10
                Log.e("DEBUG", "--------$time")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (time < 70) {
                        tvAddProductPercentage?.text = "$time %"
                        addProductProgressBar?.setProgress(time, true)
                    }
                }
            }

            override fun onFinish() {
            }
        }.start()
    }

    private fun startImageUploadProgressBar() {
        val progress = 10
        object : CountDownTimer(10000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var time: Int = progress - (millisUntilFinished / 1000).toInt()
                time *= 10
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (time < 70) {
                        tvAddImagePercentage?.text = "$time %"
                        addImageProgressBar?.setProgress(time, true)
                    } else {
                        this.cancel()
                    }
                }
            }

            override fun onFinish() {
            }
        }.start()
    }

    private fun addProduct() {
        try {
            val addProductModel = AddProductModel()
            addProductModel.category = binding.acTvCategory.text.toString()
            addProductModel.name = binding.etName.text.toString()
            addProductModel.description = binding.edtDescription.text.toHtml()

            if (!specification.toString().equals("", ignoreCase = true)) {
                addProductModel.specification = specification
            }

            try {
                displayPic = pics[selectedPhotoForDisplay!!].id!!
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (displayPic != -1) {
                addProductModel.displayPic = displayPic
            }
            addProductModel.hsnCode = binding.etHsnCode.text.toString()
            addProductModel.brand = binding.etProductBrand.text.toString()
            if (binding.etProductCode.text.toString() != "") {
                addProductModel.code = binding.etProductCode.text.toString()
            }
            val picsIdList: MutableList<Int?> = ArrayList()
            for (i in pics.indices) {
                picsIdList.add(pics[i].id)
            }
            addProductModel.pics = picsIdList
            if (binding.edtPrice.text.toString().isNotEmpty()) {
                addProductModel.price = binding.edtPrice.text.toString().toDouble()
            }
            if (binding.edtMrpPrice.text.toString().isNotEmpty()) {
                addProductModel.mrpPrice = binding.edtMrpPrice.text.toString().toDouble()
            }

            gstTypeRadioButton = findViewById(gstType)

            if (gstTypeRadioButton!!.text == resources.getString(R.string.inclusive)) {
                addProductModel.gstExclusive = false
            } else if (gstTypeRadioButton!!.text == resources.getString(R.string.exclusive)) {
                addProductModel.gstExclusive = true
            }

            addProductModel.gst = gstSelected
            addProductModel.unit = selectedUnitForProduct
            addProductModel.mrpUnit = selectedMrpUnitForProduct
            addProductModel.packagingLevel = packagingLevelList
            addProductModel.isOutOfStock = binding.cbMarkOutOfStock.isChecked
            if (intent.hasExtra(AppConstant.EDIT_PRODUCT)) {
                productViewModel.editProduct(addProductModel, editProductId)
            } else {
                productViewModel.addProduct(addProductModel)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        imageUploadViewModel.getCredLiveData().observe(this) { model ->
            binding.progressBar.visibility = View.GONE
            if (model.error == false) {
                model.data?.let { data ->
                    if (data.id != null) {
                        val picMapModel = PicMapModel()
                        picMapModel.id = data.id!!.toInt()
                        picMapModel.url = data.url
                        pics.add(picMapModel)
                        if (multiplePicCount == pics.size) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tvAddImagePercentage?.text = "100 %"
                                addImageProgressBar?.setProgress(100, false)
                            }
                            addProduct()
                        }
                    }
                }
            } else {
                addProductDialog?.dismiss()
                Toast.makeText(this, "" + model.message, Toast.LENGTH_SHORT).show()
            }
        }

        productViewModel.addProductLiveData.observe(this) { addProductResponseModel ->
            Toast.makeText(this, addProductResponseModel.message, Toast.LENGTH_SHORT).show()
            if (addProductResponseModel.error != null && !addProductResponseModel.error!!) {
                tvAddProductPercentage?.text = "100 %"
                addProductProgressBar?.setProgress(100, false)
                Handler(Looper.myLooper()!!).postDelayed({
                    addProductDialog?.dismiss()
                    Toast.makeText(this, addProductResponseModel.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra(AppConstant.PRODUCT_INFO, addProductResponseModel)
                    setResult(RESULT_OK, intent)
                    finish()
                }, 500)
            } else {
                addProductDialog?.dismiss()
            }
        }

        productViewModel.productDetailsLiveData.observe(this) {
            enableTouch()
            binding.progressBar.visibility = View.GONE
            binding.mainContent.visibility = View.VISIBLE
            if (it.error == false) {
                it.data?.let { mData ->
                    binding.acTvCategory.setText(mData.category)
                    binding.etName.setText(mData.name)
                    binding.etName.setSelection(mData.name.length)

                    if (mData.pics_map != null && mData.pics_map.size > 0) {
                        for (i in mData.pics_map.indices) {
                            pics.add(mData.pics_map[i])
                            multiplePicCount += 1
                            val addedPhotoModel = AddedPhotoModel()
                            addedPhotoModel.imagePath = mData.pics_urls[i]
                            addedPhotoModel.onEditProduct = true
                            photoModelList.add(addedPhotoModel)
                        }
                        if (photoModelList.size <= 5) {
                            photoModelList.add(null)
                        }
                        binding.groupSelectImage.visibility = View.VISIBLE
                    } else {
                        photoModelList.add(null)
                        binding.groupSelectImage.visibility = View.GONE
                    }
                    binding.etHsnCode.setText(mData.hsn_code)
                    initRecyclerView()
                    if (mData.display_pic != null) {
                        for (i in pics.indices) {
                            if (mData.display_pic == pics[i].id) {
                                photoModelList[i]!!.isSelect = true
                                selectedPhotoForDisplay = i
                                break
                            }
                        }
                        addPhotoListAdapter!!.notifyDataSetChanged()
                    }
                    binding.edtPrice.setText("" + mData.price)
                    binding.etProductCode.setText(mData.code)
                    binding.edtMrpPrice.setText("" + mData.mrp_price)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.edtDescription.setText(
                            Html.fromHtml(mData.description, Html.FROM_HTML_MODE_LEGACY)
                        )
                    } else {
                        binding.edtDescription.setText(
                            Html.fromHtml(mData.description)
                        )
                    }

                    binding.etProductBrand.setText("" + mData.brand)
                    selectedPackagingUnitForProduct = mData.packaging_unit

                    binding.tvMrpUnit.text = mData.mrp_unit
                    selectedMrpUnitForProduct = mData.mrp_unit

                    if (mData.unit.isNullOrEmpty().not()) {
                        binding.tvUnit.text = mData.unit
                        selectedUnitForProduct = mData.unit
                    }


                    if (mData.packaging_level.isNullOrEmpty().not()) {
                        packagingLevelList.clear()
                        for (i in mData.packaging_level.indices) {
                            val model = PackagingLevelModel()
                            model.unit = mData.packaging_level[i].unit
                            model.size = mData.packaging_level[i].size
                            model.buyersUnit = selectedUnitForProduct
                            packagingLevelList.add(model)
                        }
                        packagingLevelAdapter.notifyDataSetChanged()

                        if (packagingLevelList.size == 4) {
                            binding.btnAddPackagingLevel.visibility = View.GONE
                        }
                    }
                    binding.cbMarkOutOfStock.isChecked = mData.is_out_of_stock
                    if (mData.gst_exclusive != null) {
                        if (mData.gst_exclusive) {
                            binding.radioGroupGst.check(R.id.rb_gst_exclusive)
                        } else {
                            binding.radioGroupGst.check(R.id.rb_gst_inclusive)
                        }
                    }
                    if (mData.gst != null) {
                        val gst = mData.gst.toString().toDouble()
                        val gstInt = gst.toInt()
                        val index =
                            listOf(*resources.getStringArray(R.array.gst_list)).indexOf(
                                gstInt.toString()
                            )
                        binding.spinnerGst.setSelection(index)
                    }
                    singleItemMap = mData.specification
                    for (map in singleItemMap) {
                        specificationList.add(
                            AddSpecificationModel(
                                map.key,
                                map.value
                            )
                        )
                    }
                    createSpecification()
                }
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        productViewModel.productCategoryLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { list ->
                    val str: MutableList<String?> = ArrayList()
                    list.forEach { category ->
                        str.add(category.name)
                    }
                    binding.acTvCategory.threshold = 1
                    binding.acTvCategory.setAdapter(
                        ArrayAdapter(
                            this,
                            R.layout.single_text_view_spinner_16dp_text, str
                        )
                    )
                }
            }
        }
    }

    private fun createSpecification() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSpecification.setHasFixedSize(true)
        binding.recyclerViewSpecification.layoutManager = layoutManager
        adapter = SpecificationListAdapter(this, specificationList, this)
        binding.recyclerViewSpecification.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerViewSpecification.context,
            layoutManager.orientation
        )
        binding.recyclerViewSpecification.addItemDecoration(dividerItemDecoration)
        binding.edtSpecificationDescription.setText("")
        binding.edtKey.setText("")
    }

    private fun validateSpecification(): Boolean {
        var temp = true
        strSpecificationKey = binding.edtKey.text.toString()
        strSpecificationDescription = binding.edtSpecificationDescription.text.toString()
        if (StringUtils.isBlank(strSpecificationKey)) {
            Toast.makeText(
                this,
                "Enter Key for Specification",
                Toast.LENGTH_SHORT
            ).show()
            temp = false
        } else if (StringUtils.isBlank(strSpecificationDescription)) {
            Toast.makeText(
                this,
                "Enter Description for Specification",
                Toast.LENGTH_SHORT
            ).show()
            temp = false
        }
        return temp
    }

    override fun onItemDelete(key: String?, position: Int) {
        singleItemMap.remove(key)
        specificationList.removeAt(position)
        adapter!!.notifyItemRemoved(position)
        adapter!!.notifyItemRangeChanged(position, specificationList.size)
    }

    private fun initRecyclerView() {
        binding.rvImages.layoutManager = GridLayoutManager(this, 3)
        addPhotoListAdapter = ProductPhotoListAdapter(photoModelList, this, true)
        binding.rvImages.adapter = addPhotoListAdapter
    }

    override fun onCameraUpload(fileName: String?) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAt(photoModelList.size - 1)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            photoModelList.add(addedPhotoModel)
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()
            if (photoModelList.size > 1) {
                binding.groupSelectImage.visibility = View.VISIBLE
            } else {
                binding.groupSelectImage.visibility = View.GONE
            }
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onGallerySingleUpload(fileName: String?) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAt(photoModelList.size - 1)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            photoModelList.add(addedPhotoModel)
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()
            if (photoModelList.size > 1) {
                binding.groupSelectImage.visibility = View.VISIBLE
            } else {
                binding.groupSelectImage.visibility = View.GONE
            }
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onGalleryMultipleUpload(fileList: List<String>?) {
        if (fileList != null && photoModelList.size < 7 && photoModelList.size + fileList.size <= 7) {
            photoModelList.removeAt(photoModelList.size - 1)
            addPhotoListAdapter!!.notifyItemRemoved(photoModelList.size)
            for (path in fileList) {
                val addedPhotoModel = AddedPhotoModel()
                addedPhotoModel.imagePath = path
                addedPhotoModel.onEditProduct = false
                photoModelList.add(addedPhotoModel)
            }
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()
            if (photoModelList.size > 1) {
                binding.groupSelectImage.visibility = View.VISIBLE
            } else {
                binding.groupSelectImage.visibility = View.GONE
            }
            multiplePicCount += fileList.size
        } else {
            Toast.makeText(this, getString(R.string.upload_max_six_images), Toast.LENGTH_SHORT)
                .show()
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            val filePath = resultUri?.getPath(this)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = filePath
            addedPhotoModel.onEditProduct = false
            photoModelList[editPhotoPosition] = addedPhotoModel
            initRecyclerView()
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            handleCropError(result.data)
        }
    }

    override fun onDeleteImage(position: Int, key: Long?) {
        if (photoModelList.size == 1) {
            photoModelList.clear()
            pics.clear()
            addPhotoListAdapter!!.notifyDataSetChanged()
            selectedPhotoForDisplay = null
        } else {
            if (photoModelList[position]!!.isSelect) {
                selectedPhotoForDisplay = null
            }
            photoModelList.removeAt(position)
            if (pics.isNotEmpty()) {
                if (pics.size > position) {
                    pics.removeAt(position)
                }
            }
            if (photoModelList.size < 6 && photoModelList[photoModelList.size - 1] != null) {
                photoModelList.add(null)
            }
            initRecyclerView()
        }
        multiplePicCount--
        if (photoModelList.size > 1) {
            binding.groupSelectImage.visibility = View.VISIBLE
        } else {
            binding.groupSelectImage.visibility = View.GONE
        }
    }

    override fun onImageSelect(model: AddedPhotoModel, position: Int) {
        selectedPhotoForDisplay = position
        for (i in photoModelList.indices) {
            if (photoModelList[i] != null) {
                photoModelList[i]?.isSelect = i == position
            }
        }
        addPhotoListAdapter!!.notifyDataSetChanged()
    }

    private fun startCrop(uri: Uri) {
        val croppedIntent = UCrop.of(
            uri,
            Uri.fromFile(File(cacheDir, System.currentTimeMillis().toString() + ".png"))
        )
            .useSourceImageAspectRatio()
            .getIntent(this)
        someActivityResultLauncher.launch(croppedIntent)
    }

    private fun handleCropError(result: Intent?) {
        val cropError = UCrop.getError(result!!)
        if (cropError != null) {
            Toast.makeText(this, cropError.message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initPackagingLevelRv() {
        binding.rvPackagingUnit.layoutManager = LinearLayoutManager(this)
        packagingLevelAdapter =
            PackagingLevelAdapter(packagingLevelList, this)
        binding.rvPackagingUnit.adapter = packagingLevelAdapter
    }


    @SuppressLint("SetTextI18n")
    override fun onUnitSelected(unit: String, type: String) {
        if (type == AppConstant.BUYERS_UNIT) {
            selectedUnitForProduct = unit
            binding.tvUnit.text = unit

            packagingLevelList.forEach {
                it.buyersUnit = selectedUnitForProduct
            }

            packagingLevelAdapter.notifyDataSetChanged()

        } else if (type == AppConstant.MRP_UNIT) {
            selectedMrpUnitForProduct = unit
            binding.tvMrpUnit.text = unit
        } else if (type == AppConstant.PACKAGING_UNIT) {
            val index = packagingLevelList.findLast { it.unit == unit }
            if (index == null && addPackagingUnitPosition != -1) {
                selectedPackagingUnitForProduct = unit
                packagingLevelList[addPackagingUnitPosition].unit = selectedPackagingUnitForProduct
                packagingLevelAdapter.notifyItemChanged(addPackagingUnitPosition)
            } else {
                showToast("Packaging unit already exist.")
            }
        }
    }

    override fun onAddImage() {
        newInstance(this).show(supportFragmentManager, IMAGE_UPLOAD_TAG)

    }

    override fun onEditImage(model: AddedPhotoModel, position: Int) {
        editPhotoPosition = position
        startCrop(Uri.fromFile(File(Objects.requireNonNull(model.imagePath))))
    }

    override fun onEditAlreadyUploadedImage() {}

    override fun onUploadPdf() {}

    private fun addPackagingLevel() {
        val packaging = PackagingLevelModel()
        packaging.buyersUnit = selectedUnitForProduct
        packagingLevelList.add(packaging)
        packagingLevelAdapter.notifyItemInserted(packagingLevelList.size - 1)
        packagingLevelAdapter.notifyItemRangeChanged(
            packagingLevelList.size - 2,
            packagingLevelList.size - 1
        )
        if (packagingLevelList.size == 4) {
            binding.btnAddPackagingLevel.visibility = View.GONE
        }
    }

    override fun onDeletePackagingUnit(position: Int, model: PackagingLevelModel) {
        packagingLevelList.removeAt(position)
        packagingLevelAdapter.notifyItemRemoved(position)
        packagingLevelAdapter.notifyItemRangeChanged(position, packagingLevelList.size)

        if (packagingLevelList.size < 4) {
            binding.btnAddPackagingLevel.visibility = View.VISIBLE
        }
    }

    override fun addPackagingUnit(position: Int, model: PackagingLevelModel) {
        addPackagingUnitPosition = position
        ProductUnitBottomSheetDialogFragment(
            this,
            AppConstant.PACKAGING_UNIT
        ).show(supportFragmentManager, "PackagingUnit")
    }
}