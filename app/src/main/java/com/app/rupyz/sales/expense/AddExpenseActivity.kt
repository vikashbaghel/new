package com.app.rupyz.sales.expense

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddExpenseBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.IMAGE_UPLOAD_TAG
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import com.app.rupyz.sales.pdfupload.PdfUploadViewModel
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.imageupload.MultipleImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.MultipleImageUploadListener
import com.app.rupyz.ui.organization.profile.activity.addphotos.ProductPhotoListAdapter
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar


class AddExpenseActivity : BaseActivity(), ProductPhotoListAdapter.OnImageDeleteListener,
    MultipleImageUploadListener {
    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private lateinit var pdfUploadViewModel: PdfUploadViewModel

    private lateinit var addPhotoListAdapter: ProductPhotoListAdapter
    private val photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
    private val pics: ArrayList<PicMapModel> = ArrayList()

    private var expenseModel: ExpenseDataItem? = null
    private var expenseTrackerModel: ExpenseTrackerDataItem? = null

    private var multiplePicCount = 0
    private var expenseTrackerId = 0

    private lateinit var progressDialog: ProgressDialog

    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private var expenseDate: String? = null

    private var expenseTrackerStartDate: String? = null
    private var expenseTrackerEndDate: String? = null

    private val cal = Calendar.getInstance()
    private val day = cal[Calendar.DAY_OF_MONTH]
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val myCalendar = Calendar.getInstance()

    private val expenseDataItem = ExpenseDataItem()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]
        pdfUploadViewModel = ViewModelProvider(this)[PdfUploadViewModel::class.java]

        initRecyclerView()
        initLayout()
        initObserver()

        mStartDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateStartDate()
            }

        binding.tvExpenseDate.setOnClickListener {
            Utils.hideKeyboard(this)
            openStartDateCalendar()
        }

        binding.btnCancel.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun openStartDateCalendar() {
        val dialog = DatePickerDialog(
            this,

            android.R.style.ThemeOverlay_Material_Dialog,
            mStartDateSetListener,
            year, month, day)
        dialog.updateDate(year, month, day)
        dialog.datePicker
        val maxDate1 = Calendar.getInstance()
        maxDate1[Calendar.DAY_OF_MONTH]
        maxDate1[Calendar.MONTH] = month
        maxDate1[Calendar.YEAR] = year-1
        dialog.datePicker.maxDate= DateFormatHelper.convertStringToDate(cal.time.time.toString()).time
        dialog.datePicker.minDate = maxDate1.time.time
       /* if (!expenseTrackerModel?.endDateTime.isNullOrEmpty()) {
           // dialog.datePicker.maxDate = DateFormatHelper.convertStringToDate(expenseTrackerModel?.endDateTime).time
        } else {
            // dialog.datePicker.maxDate = cal.time.time
        }*/


        dialog.show()
    }


    private fun initRecyclerView() {
        binding.rvImages.layoutManager = GridLayoutManager(this, 3)
        addPhotoListAdapter = ProductPhotoListAdapter(photoModelList, this, true)
        binding.rvImages.adapter = addPhotoListAdapter
    }

    private fun updateStartDate() {
        val tempStartDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)
        if (!expenseTrackerStartDate.isNullOrEmpty() && DateFormatHelper.isDate1BeforeThenDate2(
                tempStartDate,
                expenseTrackerStartDate
            )
        ) {
            Toast.makeText(
                this,
                "Expense date can not be less then expense head start date!!",
                Toast.LENGTH_SHORT
            )
                .show()
        } else if (!expenseTrackerEndDate.isNullOrEmpty() && DateFormatHelper.isDate1GreaterThenDate2(
                tempStartDate,
                expenseTrackerEndDate
            )
        ) {
            Toast.makeText(
                this,
                "Expense date can not be greater then expense head end date!!",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            expenseDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)
            binding.tvExpenseDate.text =
                DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initLayout() {

        if (intent.hasExtra(AppConstant.TOTAL_EXPENSE_DETAILS)) {
            expenseTrackerModel = intent.getParcelableExtra(AppConstant.TOTAL_EXPENSE_DETAILS)

            if (expenseTrackerModel != null) {
                expenseTrackerId = expenseTrackerModel?.id!!
            }

            if (expenseTrackerModel?.startDateTime != null) {
                expenseTrackerStartDate =
                    DateFormatHelper.convertDateToIsoFormat(expenseTrackerModel?.startDateTime)
            }

            if (expenseTrackerModel?.endDateTime != null) {
                expenseTrackerEndDate =
                    DateFormatHelper.convertDateToIsoFormat(expenseTrackerModel?.endDateTime)
            }
        }

        if (intent.hasExtra(AppConstant.EXPENSE_DETAILS)) {
            expenseModel = intent.getParcelableExtra(AppConstant.EXPENSE_DETAILS)

            if (expenseModel != null) {

                if (expenseModel?.amount != null) {
                    binding.etAmount.setText("" + expenseModel?.amount)
                }

                if (expenseModel?.description != null) {
                    binding.etDescription.setText(expenseModel?.description)
                }

                if (expenseModel?.name != null) {
                    binding.etTitle.setText(expenseModel?.name)
                }

                if (expenseModel?.expenseDateTime != null) {
                    expenseDate =
                        DateFormatHelper.convertDateToIsoFormat(expenseModel?.expenseDateTime)
                    binding.tvExpenseDate.text =
                        DateFormatHelper.getMonthDate(expenseModel?.expenseDateTime)
                }

                if (expenseModel?.billProofUrls != null) {
                    expenseModel?.billProofUrls!!.forEachIndexed { _, picMapModel ->
                        pics.add(picMapModel)
                        multiplePicCount += 1

                        if (picMapModel.url!!.contains("?")) {
                            val addedPhotoModel = AddedPhotoModel()
                            addedPhotoModel.imagePath = picMapModel.url
                            addedPhotoModel.type = AppConstant.DOCUMENT
                            addedPhotoModel.onEditProduct = true
                            addedPhotoModel.isDisplayPicEnable = false
                            photoModelList.add(addedPhotoModel)
                        } else {
                            val addedPhotoModel = AddedPhotoModel()
                            addedPhotoModel.imagePath = picMapModel.url
                            addedPhotoModel.onEditProduct = true
                            addedPhotoModel.isDisplayPicEnable = false
                            photoModelList.add(addedPhotoModel)
                        }
                    }

                    if (photoModelList.size <= 5) {
                        photoModelList.add(null)
                    }

                    initRecyclerView()

                } else {
                    photoModelList.add(null)
                    addPhotoListAdapter.notifyItemInserted(0)
                }

                binding.btnAdd.text = resources.getString(R.string.update)
            }
        } else {
            photoModelList.add(null)
            addPhotoListAdapter.notifyItemInserted(0)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("uploading ...")
        progressDialog.setCancelable(false)

        binding.ivClose.setOnClickListener {
            finish()
        }

        binding.btnAdd.setOnClickListener {
            validateData()
        }

    }


    private fun validateData() {
        when {
            binding.etTitle.text.trim().toString().isEmpty() -> {
                showToast("Expense Title Required!")
            }

            expenseDate.isNullOrEmpty() ->
                showToast("Expense Date Required")

            binding.etAmount.text.toString().isEmpty() -> {
                showToast("Expense Amount Required!")
            }

            else -> {
                Utils.hideKeyboard(this)
                if (photoModelList.size > 1) {
                    if (hasInternetConnection()) {
                        uploadImage()
                    } else {
                        photoModelList.forEach {
                            it?.let {
                                pics.add(PicMapModel(it.imageId, it.imagePath))
                            }
                        }

                        expenseDataItem.billProofUrls = pics
                        addExpense()
                    }
                } else {
                    progressDialog.show()
                    addExpense()
                }
            }
        }
    }

    private fun uploadImage() {
        progressDialog.show()
        var isDocAvailable = false
        photoModelList.forEach {
            if (it != null && it.type == AppConstant.DOCUMENT && !it.onEditProduct
                && !it.isUploading
            ) {
                isDocAvailable = true
                it.isUploading = true
                pdfUploadViewModel.uploadCredentials(it.imagePath!!)
            }
        }

        if (!isDocAvailable) {
            addExpenseImage()
        }
    }

    private fun addExpenseImage() {
        progressDialog.show()
        var isEditImageAvailable = false
        if (pics.size > 0) {
            for (i in pics.size - 1 until photoModelList.size) {
                if (photoModelList[i] != null && photoModelList[i]!!.imagePath != null
                    && photoModelList[i]?.type != AppConstant.DOCUMENT
                ) {
                    if (!photoModelList[i]!!.onEditProduct) {
                        isEditImageAvailable = true
                        lifecycleScope.launch {
                            val compressedImageFile = Compressor.compress(
                                this@AddExpenseActivity,
                                File(photoModelList[i]!!.imagePath!!)
                            ) {
                                quality(30)
                                resolution(512, 512)
                                size(197_152)
                            }
                            imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                        }
                    }
                }
            }
        } else {
            for (i in photoModelList.indices) {
                if (photoModelList[i] != null && photoModelList[i]!!.imagePath != null
                    && photoModelList[i]?.type != AppConstant.DOCUMENT
                ) {
                    isEditImageAvailable = true
                    lifecycleScope.launch {
                        val compressedImageFile = Compressor.compress(
                            this@AddExpenseActivity,
                            File(photoModelList[i]!!.imagePath!!)
                        ) {
                            quality(30)
                            resolution(512, 512)
                            size(197_152)
                        }
                        imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                    }
                }
            }
        }
        if (!isEditImageAvailable) {
            addExpense()
        }
    }


    private fun addExpense() {
        expenseDataItem.name = binding.etTitle.text.toString()
        expenseDataItem.amount = binding.etAmount.text.toString().toDouble()
        expenseDataItem.description = binding.etDescription.text.toString()
        expenseDataItem.reimbursementtracker = expenseTrackerId
        expenseDataItem.expenseDateTime = expenseDate

        if (hasInternetConnection()) {
            val list = ArrayList<Int>()
            if (pics.size > 0) {
                pics.forEach {
                    list.add(it.id!!)
                }
            }
            expenseDataItem.billProof = list
        }

        if (expenseTrackerModel?.isSyncedToServer == null || expenseTrackerModel?.isSyncedToServer == true) {
            expenseDataItem.isUpdateReimbursementTracker = true
        }

        if (intent.hasExtra(AppConstant.EXPENSE_DETAILS)) {
            viewModel.updateExpenses(expenseModel?.id!!, expenseDataItem, hasInternetConnection())
        } else {
            viewModel.addExpense(expenseDataItem, hasInternetConnection())
        }
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

    @SuppressLint("Range")
    var uploadPdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            // Get the Uri of the selected file

            var isDocAvailable = false
            photoModelList.forEach {
                if (it != null && it.type == AppConstant.DOCUMENT) {
                    isDocAvailable = true
                }
            }

            if (!isDocAvailable) {
                val uri = result.data!!.data
                val path: String =
                    FileUtils.getPdfFile(this, uri!!).absolutePath

                if (photoModelList.size < 7) {
                    multiplePicCount += 1
                    photoModelList.removeAt(photoModelList.size - 1)
                    val addedPhotoModel = AddedPhotoModel()
                    addedPhotoModel.imagePath = path
                    addedPhotoModel.type = AppConstant.DOCUMENT
                    addedPhotoModel.onEditProduct = false
                    addedPhotoModel.isDisplayPicEnable = false
                    photoModelList.add(addedPhotoModel)

                    if (photoModelList.size < 6) {

                        photoModelList.add(null)
                    }
                    initRecyclerView()

                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.upload_max_six_images),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(
                    this,
                    getString(R.string.upload_max_one_doc),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun initObserver() {

        imageUploadViewModel.getCredLiveData().observe(
            this
        ) { genericResponseModel ->
            if (genericResponseModel.error == false) {
                genericResponseModel.data?.let { data ->
                    if (data.id != null) {
                        val picMapModel = PicMapModel()
                        picMapModel.id = data.id!!.toInt()
                        picMapModel.url = data.url
                        pics.add(picMapModel)
                        if (multiplePicCount == pics.size) {
                            addExpense()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "${genericResponseModel.message}", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }

        viewModel.addExpenseLiveData.observe(this) {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        pdfUploadViewModel.uploadCredLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { data ->
                    if (data.id != null) {
                        val picMapModel = PicMapModel()
                        picMapModel.id = data.id!!.toInt()
                        picMapModel.url = data.url
                        pics.add(picMapModel)

                        validateData()
                    }
                }
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDeleteImage(
        position: Int, key: Long?
    ) {
        if (photoModelList.size == 1) {
            photoModelList.clear()
            pics.clear()
            addPhotoListAdapter.notifyDataSetChanged()
        } else {
            photoModelList.removeAt(position)
            if (pics.isNotEmpty()) {
                pics.removeAt(position)
            }
            if (photoModelList.size < 6 && photoModelList[photoModelList.size - 1] != null) {
                photoModelList.add(null)
            }
            initRecyclerView()
        }
        multiplePicCount--
    }

    override fun onImageSelect(model: AddedPhotoModel, position: Int) {
    }


    override fun onEditImage(model: AddedPhotoModel, position: Int) {
    }

    override fun onAddImage() {
        Utils.hideKeyboard(this)
        val bundle = Bundle()
        bundle.putBoolean(AppConstant.DOCUMENT, true)
        val frag = MultipleImageUploadBottomSheetDialogFragment.newInstance(this)
        frag.arguments = bundle

        frag.show(supportFragmentManager, IMAGE_UPLOAD_TAG)
    }

    override fun onEditAlreadyUploadedImage() {
    }

    override fun onCameraUpload(
        fileName: String?
    ) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAt(photoModelList.size - 1)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            addedPhotoModel.isDisplayPicEnable = false
            photoModelList.add(addedPhotoModel)
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()

        } else {
            Toast.makeText(
                this,
                getString(R.string.upload_max_six_images),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    override fun onGallerySingleUpload(
        fileName: String?
    ) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAt(photoModelList.size - 1)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            addedPhotoModel.isDisplayPicEnable = false
            photoModelList.add(addedPhotoModel)
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()

        } else {
            Toast.makeText(
                this,
                getString(R.string.upload_max_six_images),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    override fun onGalleryMultipleUpload(
        fileList: List<String>
        ?
    ) {
        if (fileList != null && photoModelList.size < 7 && photoModelList.size + fileList.size <= 7) {
            photoModelList.removeAt(photoModelList.size - 1)
            addPhotoListAdapter.notifyItemRemoved(photoModelList.size)
            for (path in fileList) {
                val addedPhotoModel = AddedPhotoModel()
                addedPhotoModel.imagePath = path
                addedPhotoModel.onEditProduct = false
                addedPhotoModel.isDisplayPicEnable = false
                photoModelList.add(addedPhotoModel)
            }

            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()

            multiplePicCount += fileList.size

        } else {
            Toast.makeText(
                this,
                getString(R.string.upload_max_six_images),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

}