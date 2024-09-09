package com.app.rupyz.sales.lead

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.organization.profile.CategoryListAdapter
import com.app.rupyz.adapter.organization.profile.CategoryListener
import com.app.rupyz.databinding.ActivityAllLeadListBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.sales.customer.CustomFormActivity
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment

@SuppressLint("NotifyDataSetChanged")
class AllLeadListActivity : BaseActivity(), CategoryListener,
    LeadListAdapter.ILeadActionListener, DeleteDialog.IOnClickListener,
    LeadApproveOrRejectedBottomSheetDialog.ILeadBottomSheetActionListener {
    private lateinit var binding: ActivityAllLeadListBinding
    private lateinit var leadViewModel: LeadViewModel
    private lateinit var leadListAdapter: LeadListAdapter
    private lateinit var categoryListAdapter: CategoryListAdapter

    private var leadList = ArrayList<LeadLisDataItem>()
    private var categoryList: ArrayList<AllCategoryResponseModel> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false

    private var isDataChange = false
    private var category: String = ""

    private var currentPage = 1
    private var customerInActivePosition = -1
    private var leadActionPosition = -1
    private var leadActionModel: LeadLisDataItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllLeadListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]

        initRecyclerView()
        initObservers()

        if (PermissionModel.INSTANCE.getPermission(
                AppConstant.VIEW_LEAD_CATEGORY_PERMISSION,
                false
            )
        ) {
            leadViewModel.getAllCategoryList("", hasInternetConnection())
            binding.hsvCategory.visibility = View.VISIBLE
        } else {
            binding.hsvCategory.visibility = View.GONE
        }

        loadLeadList()

        binding.etSearch.setOnClickListener {
            leadActionPosition = -1
            someActivityResultLauncher.launch(Intent(this, SearchLeadActivity::class.java))
        }

        binding.tvAllCategory.setOnClickListener {
            binding.tvAllCategory.setBackgroundResource(R.drawable.details_button_style)
            binding.tvAllCategory.setTextColor(getColor(R.color.white))
            for (i in categoryList.indices) {
                categoryList[i].isSelected = false
            }
            binding.tvMoreCategory.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
            binding.tvMoreCategory.setTextColor(getColor(R.color.tab_un_selected_color))
            category = ""
            leadList.clear()
            loadLeadList()
            leadListAdapter.notifyDataSetChanged()
            categoryListAdapter.notifyDataSetChanged()
        }

        binding.tvMoreCategory.setOnClickListener {
            binding.tvAllCategory.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
            binding.tvAllCategory.setTextColor(getColor(R.color.tab_un_selected_color))
            for (i in categoryList.indices) {
                categoryList[i].isSelected = false
            }
            categoryListAdapter.notifyDataSetChanged()
            binding.tvMoreCategory.setBackgroundResource(R.drawable.details_button_style)
            binding.tvMoreCategory.setTextColor(getColor(R.color.white))
            val intent =
                Intent(this, LeadCategoryActivity::class.java)
            categoryActivityResultLauncher.launch(intent)
        }

        if (!PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_LEAD_PERMISSION, false)) {
            binding.ivAddLead.visibility = View.GONE
        }


        binding.ivBack.setOnClickListener {
            // onBackPressed()
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddLead.setOnClickListener {
            leadActionPosition = -1
            someActivityResultLauncher.launch(Intent(this, AddNewLeadActivity::class.java))
        }

    }

    private var categoryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null && result.data!!.hasExtra(AppConstant.LEAD_CATEGORY)) {
                category = result.data?.getStringExtra(AppConstant.LEAD_CATEGORY)!!
                leadList.clear()
                loadLeadList()
                leadListAdapter.notifyDataSetChanged()
            }
        }
    }

    private var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            isDataChange = true
            if (result.data != null && result.data!!.hasExtra(AppConstant.DELETE_LEAD)) {
                if (leadActionPosition != -1) {
                    leadList.removeAt(leadActionPosition)
                    leadListAdapter.notifyItemRemoved(leadActionPosition)
                    leadListAdapter.notifyItemRangeChanged(leadActionPosition, leadList.size)
                }
            } else {
                currentPage = 1
                isApiLastPage = false
                isPageLoading = true
                loadLeadList()
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvList.layoutManager = linearLayoutManager

        leadListAdapter = LeadListAdapter(
            leadList, this, isStaffUser, hasInternetConnection()
        )

        binding.rvList.adapter = leadListAdapter

        binding.rvList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadLeadList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })


        binding.rvCategoryList.setHasFixedSize(true)
        binding.rvCategoryList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        categoryListAdapter = CategoryListAdapter(categoryList, this)
        binding.rvCategoryList.adapter = categoryListAdapter
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        leadViewModel.leadListLiveData.observe(this) { data ->
            binding.paginationProgressBar.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.clEmptyData.visibility = View.GONE

            if (data.error != true) {
                isPageLoading = false

                data.data?.let {
                    if (it.isNotEmpty()) {

                        if (currentPage == 1) {
                            leadList.clear()
                        }

                        leadList.addAll(it)

                        if (leadActionPosition != -1) {
                            leadListAdapter.notifyItemChanged(leadActionPosition)
                            leadActionPosition = -1
                        } else {
                            leadListAdapter.notifyDataSetChanged()
                        }

                        if (leadList.size < 30) {
                            isApiLastPage = true
                        }
                    } else {
                        isApiLastPage = true
                        if (currentPage == 1) {
                            binding.clEmptyData.visibility = View.VISIBLE
                            leadList.clear()
                            leadListAdapter.notifyDataSetChanged()
                        }
                    }
                }
            } else {
                if (data.errorCode != null && data.errorCode == 403) {
                    logout()
                } else {
                    showToast(data.message)
                }
            }
        }


        leadViewModel.leadCategoryLiveData.observe(this) {
            it.data?.let { list ->
                list.forEach { category ->
                    val model = AllCategoryResponseModel()
                    model.name = category.name
                    model.id = category.id
                    categoryList.add(model)
                }

                categoryListAdapter.notifyDataSetChanged()

                if (list.size > 5) {
                    binding.tvMoreCategory.visibility = View.VISIBLE
                }
            }
        }

        leadViewModel.addLeadLiveData.observe(this) {
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                if (customerInActivePosition != -1) {
                    leadList.removeAt(customerInActivePosition)
                    leadListAdapter.notifyItemRemoved(customerInActivePosition)
                    leadListAdapter.notifyItemRangeChanged(customerInActivePosition, leadList.size)

                    customerInActivePosition = -1
                }
            }
        }

        leadViewModel.approveRejectLeadLiveData.observe(this) {
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            stopDialog()
            if (it.error == false) {
                if (leadActionPosition != -1) {
                    leadList[leadActionPosition].status = it.data?.status
                    leadListAdapter.notifyItemChanged(leadActionPosition)
                    leadActionPosition = -1
                }
            } else {
                if (leadActionModel != null) {
                    leadList[leadActionPosition] = leadActionModel!!
                    leadListAdapter.notifyItemChanged(leadActionPosition)
                    leadActionPosition = -1
                }
            }
        }
    }

    private fun loadLeadList() {
        binding.clEmptyData.visibility = View.GONE
        leadViewModel.getLeadList("", category, currentPage, hasInternetConnection())
        if (currentPage > 1) {
            binding.paginationProgressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    override fun onCategorySelect(model: AllCategoryResponseModel, position: Int) {
        if (model.isSelected.not()) {
            customerInActivePosition = -1
            for (i in categoryList.indices) {
                categoryList[i].isSelected = false
            }
            category = categoryList[position].name.toString()
            categoryList[position].isSelected = true
            categoryListAdapter.notifyDataSetChanged()

            binding.tvAllCategory.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
            binding.tvAllCategory.setTextColor(getColor(R.color.tab_un_selected_color))
            binding.tvMoreCategory.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
            binding.tvMoreCategory.setTextColor(getColor(R.color.tab_un_selected_color))

            category = model.name ?: ""

            customerInActivePosition = -1
            currentPage = 1
            leadList.clear()
            leadListAdapter.notifyDataSetChanged()
            loadLeadList()
        }
    }

    override fun getLeadDetails(model: LeadLisDataItem, position: Int) {
        leadActionPosition = position
        someActivityResultLauncher.launch(
            Intent(this, LeadDetailsActivity::class.java).putExtra(
                AppConstant.LEAD_INFO,
                model
            )
        )
    }

    override fun onCall(model: LeadLisDataItem) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${model.mobile}")
        startActivity(intent)
    }

    override fun onWCall(model: LeadLisDataItem) {
        val uri =
            Uri.parse("https://api.whatsapp.com/send?phone=+91${model.mobile} &text=Hi, ${model.contactPersonName}")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    }

    override fun onEditLead(model: LeadLisDataItem, position: Int) {
        leadActionPosition = position
        someActivityResultLauncher.launch(
            Intent(this, AddNewLeadActivity::class.java).putExtra(
                AppConstant.LEAD_INFO,
                model
            )
        )
    }

    override fun onApprovedLead(model: LeadLisDataItem, position: Int) {
        leadActionPosition = position
        leadActionModel = model
        val fragment = LeadApproveOrRejectedBottomSheetDialog.getInstance(
            AppConstant.STATUS_APPROVED,
            model,
            this
        )
        fragment.show(supportFragmentManager, AllLeadListActivity::class.java.name)
    }

    override fun onRejectedLead(model: LeadLisDataItem, position: Int) {
        leadActionPosition = position
        leadActionModel = model
        val fragment = LeadApproveOrRejectedBottomSheetDialog.getInstance(
            AppConstant.STATUS_DISHONOUR,
            model,
            this
        )
        fragment.show(supportFragmentManager, AllLeadListActivity::class.java.name)
    }

    override fun rejectedCommentOfLead(reason: String) {
        startDialog("Rejecting lead")
        leadViewModel.approveOrRejectLead(leadActionModel?.id ?: 0, AppConstant.REJECTED, reason)
    }

    override fun approveCommentOfLead(reason: String) {
        startDialog("Approving lead")
        leadViewModel.approveOrRejectLead(leadActionModel?.id ?: 0, AppConstant.APPROVED, reason)
    }

    override fun onDismissDialog(model: LeadLisDataItem?) {
        if (leadActionPosition != -1 && leadActionModel != null) {
            leadList[leadActionPosition] = leadActionModel!!
            leadListAdapter.notifyItemChanged(leadActionPosition)
        }
    }

    override fun onDeleteLead(model: LeadLisDataItem, position: Int) {
        DeleteDialog.showDeleteDialog(
            this,
            model.id,
            position,
            resources.getString(R.string.delete_lead),
            resources.getString(R.string.delete_lead_message),
            this
        )
    }

    override fun onRecordActivity(model: LeadLisDataItem) {
        if (PermissionModel.INSTANCE.hasRecordActivityPermission()) {
            startActivity(
                Intent(
                    this,
                    CustomFormActivity::class.java
                ).putExtra(AppConstant.CUSTOMER_ID, model.id)
                    .putExtra(AppConstant.LEAD, model)
                    .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.LEAD_FEEDBACK)
            )
        } else {
            showToast(resources.getString(R.string.you_dont_have_permission_to_perform_this_action))
        }
    }

    override fun getStoreFrontInfo() {
        val fragment = InfoBottomSheetDialogFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.HEADING, resources.getString(R.string.storefront_lead))
        bundle.putString(AppConstant.MESSAGE, resources.getString(R.string.storefront_lead_message))
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, AppConstant.STORE_FRONT)
    }

    override fun viewCustomerLocation(model: LeadLisDataItem) {
        if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
            Utils.openMap(this, model.mapLocationLat, model.mapLocationLong, model.businessName)
        } else {
            showToast(resources.getString(R.string.no_location_found))
        }
    }

    override fun onBackPressed() {
        if (isDataChange) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }


    override fun onDelete(model: Any, position: Any) {
        leadViewModel.deleteLead(model as Int, hasInternetConnection())

        customerInActivePosition = position as Int
    }


}