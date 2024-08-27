package com.app.rupyz.sales.lead

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivitySearchLeadBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.sales.customer.CustomFormActivity
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment

@SuppressLint("SetTextI18n", "NotifyDataSetChanged")
class SearchLeadActivity : BaseActivity(), LeadListAdapter.ILeadActionListener,
    LeadApproveOrRejectedBottomSheetDialog.ILeadBottomSheetActionListener,
    DeleteDialog.IOnClickListener {
    private lateinit var binding: ActivitySearchLeadBinding
    private lateinit var leadViewModel: LeadViewModel
    private lateinit var leadListAdapter: LeadListAdapter

    private var leadList = ArrayList<LeadLisDataItem>()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var customerInActivePosition = -1

    private var leadActionPosition = -1
    private var leadActionModel: LeadLisDataItem? = null

    var delay: Long = 500 // 1 seconds after user stops typing

    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchLeadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]

        binding.etSearch.requestFocus()

        initRecyclerView()
        initObservers()

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            leadList.clear()
            Utils.hideKeyboard(this)
            loadLeadList()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                leadList.clear()
                loadLeadList()
                Utils.hideKeyboard(this)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis()
                    handler.postDelayed(inputFinishChecker, delay)
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            leadList.clear()
            leadListAdapter.notifyDataSetChanged()
        }

        binding.ivBack.setOnClickListener {
            //onBackPressed()
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            leadList.clear()
            loadLeadList()

            binding.clEmptyData.visibility = View.GONE
            leadListAdapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvList.layoutManager = linearLayoutManager

        leadListAdapter =
            LeadListAdapter(
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
    }


    private fun initObservers() {
        leadViewModel.leadListLiveData.observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            binding.clEmptyData.visibility = View.GONE

            if (data.error == false) {
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
                        }
                    }
                }
            } else {
                Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
            }
        }

        leadViewModel.approveRejectLeadLiveData.observe(this) {
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            enableTouch()
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

    private var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            loadLeadList()
        }
    }

    private fun loadLeadList() {
        leadViewModel.getLeadList(
            binding.etSearch.text.toString(),
            "",
            currentPage,
            hasInternetConnection()
        )
        binding.progressBar.visibility = View.VISIBLE
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
        disableTouch()
        binding.progressBar.visibility = View.VISIBLE
        leadViewModel.approveOrRejectLead(leadActionModel?.id ?: 0, AppConstant.REJECTED, reason)
    }

    override fun approveCommentOfLead(reason: String) {
        disableTouch()
        binding.progressBar.visibility = View.VISIBLE
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

    override fun onDelete(model: Any, position: Any) {
        leadViewModel.deleteLead(model as Int, hasInternetConnection())
        customerInActivePosition = position as Int
    }
}