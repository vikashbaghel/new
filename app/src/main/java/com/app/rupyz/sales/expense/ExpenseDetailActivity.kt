package com.app.rupyz.sales.expense

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityExpenseDetailBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.custom.DownloadPdfTask
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter

class ExpenseDetailActivity : BaseActivity(),
    ProductImageViewPagerAdapter.ProductImageClickListener, DeleteDialog.IOnClickListener {
    private lateinit var binding: ActivityExpenseDetailBinding
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseModel: ExpenseDataItem
    private var expenseTrackerModel: ExpenseTrackerDataItem? = null

    private lateinit var addPhotoListAdapter: LrPhotoListAdapter
    private val pics: ArrayList<PicMapModel> = ArrayList()

    private var isDataChange: Boolean = false
    private var staffRole: String = ""
    private var expenseStatus: String = ""
    private var pdfUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        initObservers()
        initRecyclerView()

        if (intent.hasExtra(AppConstant.EXPENSE_DETAILS)) {
            expenseModel = intent.getParcelableExtra(AppConstant.EXPENSE_DETAILS)!!

            viewModel.getExpenseDetails(expenseModel.id!!, hasInternetConnection())
        }

        if (intent.hasExtra(AppConstant.TOTAL_EXPENSE_DETAILS)) {
            expenseTrackerModel = intent.getParcelableExtra(AppConstant.TOTAL_EXPENSE_DETAILS)
        }

        if (intent.hasExtra(AppConstant.STAFF_ROLE)) {
            staffRole = intent.getStringExtra(AppConstant.STAFF_ROLE)!!
        }

        if (intent.hasExtra(AppConstant.EXPENSE_STATUS)) {
            expenseStatus = intent.getStringExtra(AppConstant.EXPENSE_STATUS) ?: ""
        }

        if (staffRole == AppConstant.STAFF) {
            if (expenseStatus == AppConstant.ACTIVE || expenseStatus == AppConstant.REJECTED) {
                binding.ivMore.visibility = View.VISIBLE
            } else {
                if (hasInternetConnection() && expenseModel.isSyncedToServer == false) {
                    binding.ivMore.visibility = View.GONE
                }
            }
        } else {
            binding.ivMore.visibility = View.GONE
        }

        if (hasInternetConnection().not()) {
            if (expenseModel.isSyncedToServer == false) {
                binding.ivMore.visibility = View.VISIBLE
            } else {
                binding.ivMore.visibility = View.GONE
            }
        }

        binding.ivMore.setOnClickListener {
            //creating a popup menu
            val popup =
                PopupMenu(this, binding.ivMore)
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_edit_and_delete)
            //adding click listener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.delete_product -> {
                        if (hasInternetConnection().not() && expenseModel.isSyncedToServer == null) {
                            showToast(resources.getString(R.string.this_feature_isn_t_available_offline))
                        } else {
                            DeleteDialog.showDeleteDialog(
                                this,
                                expenseModel,
                                "",
                                resources.getString(R.string.delete_reimbursement),
                                resources.getString(R.string.delete_reimbursement_message),
                                this
                            )
                        }
                        return@setOnMenuItemClickListener true
                    }

                    R.id.edit_product -> {
                        someActivityResultLauncher.launch(
                            Intent(
                                this,
                                AddExpenseActivity::class.java
                            )
                                .putExtra(AppConstant.EXPENSE_DETAILS, expenseModel)
                                .putExtra(AppConstant.TOTAL_EXPENSE_DETAILS, expenseTrackerModel)
                        )
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }

        binding.ivClose.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            isDataChange = true
            viewModel.getExpenseDetails(expenseModel.id!!, hasInternetConnection())
        }
    }


    private fun initRecyclerView() {
        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        addPhotoListAdapter = LrPhotoListAdapter(pics, this)
        binding.rvPhotos.adapter = addPhotoListAdapter

    }

    private fun initObservers() {
        viewModel.expenseDetailsLiveData.observe(this) {
            if (it.error == false) {
                it.data?.let { data ->
                    expenseModel = data
                    initData(data)
                }
            }
        }

        viewModel.addExpenseLiveData.observe(this) {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun initData(model: ExpenseDataItem) {
        if (model.amount != null) {
            binding.tvAmount.text = CalculatorHelper().convertCommaSeparatedAmount(
                model.amount,
                AppConstant.TWO_DECIMAL_POINTS
            )
        }

        if (model.expenseDateTime != null) {
            binding.tvDate.text = DateFormatHelper.getMonthDate(model.expenseDateTime)
        }

        if (model.name != null) {
            binding.tvTitle.text = model.name
        }

        if (model.description != null) {
            binding.groupDescription.visibility = View.VISIBLE
            binding.tvDescription.text = model.description
        } else {
            binding.groupDescription.visibility = View.GONE
        }

        if (!model.billProofUrls.isNullOrEmpty()) {
            binding.groupImages.visibility = View.VISIBLE
            pics.clear()
            pics.addAll(model.billProofUrls!!)
            addPhotoListAdapter.notifyDataSetChanged()
        } else {
            binding.groupImages.visibility = View.GONE
        }
    }

    override fun onPdfClick(position: Int,url:String) {
        super.onPdfClick(position,url)
        pdfUrl = pics[position].url

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mGalleryPermissionResult.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            mGalleryPermissionResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private val mGalleryPermissionResult: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                if (pdfUrl != null) {
                    val fileName = "Expense-Details-" + expenseModel.id + ".pdf"
                    DownloadPdfTask(this, pdfUrl, fileName)
                }
            } else {
                Toast.makeText(
                    this,
                    "Media Permission is required to perform this action.",
                    Toast.LENGTH_SHORT
                ).show()
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

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (isDataChange) {
            finishWithResponse()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    private fun finishWithResponse() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDelete(model: Any, position: Any) {
        viewModel.deleteExpenses(expenseModel, hasInternetConnection())
    }
}