package com.app.rupyz.sales.beat

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityBeatListBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.BeatListDataItem
import com.app.rupyz.sales.beatplan.*
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity
import com.google.gson.JsonObject

class BeatListActivity : BaseActivity(), BeatListAdapter.IBeatActionListener,
    SortByBottomSheetDialogFragment.ISortByCustomerListener,
    BeatCustomerFilterBottomSheetDialogFragment.IBeatCustomerFilterListener {
    private lateinit var binding: ActivityBeatListBinding

    private val beatViewModel: BeatViewModel by viewModels()

    private lateinit var beatAdapter: BeatListAdapter

    private var beatList = ArrayList<BeatListDataItem>()

    private var sortByOrder = ""
    private var filterCustomerLevel = ""

    private var lastTextEdit: Long = 0
    private var handler: Handler = Handler(Looper.myLooper()!!)

    private var delay: Long = 500

    private var isDataChange = false
    private var isPageLoading = false
    private var isApiLastPage = false
    private var levelFilterApply = false
    private var customerTypeFilterApply = false
    private var assignedStaffFilterApply = false

    private var filterAssignedStaff: Pair<Int, String> = Pair(0, "")
    private var filterParentCustomer: Pair<Int, String> = Pair(0, "")
    private var filterCount = 0
    private var currentPage = 1

    private var deleteBeatPosition = -1
    private var editBeatPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE
        initRecyclerView()
        initObservers()
        getBeatList()

        if (isStaffUser){
            binding.tvCreateBeatPlan.visibility = View.VISIBLE
        } else {
            binding.tvCreateBeatPlan.visibility = View.GONE
        }

        binding.tvAddBeat.setOnClickListener {
            someActivityResultLauncher.launch(Intent(this, AddNewBeatActivity::class.java))
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.etSearch.text.toString().isNotEmpty()) {
                    currentPage = 1
                    beatList.clear()
                    getBeatList()
                    Utils.hideKeyboard(this)
                } else {
                    Toast.makeText(this, "Please enter some value!!", Toast.LENGTH_SHORT).show()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker);
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishChecker, delay);
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
            currentPage = 1
            isApiLastPage = false
            isPageLoading = true
            beatList.clear()
            beatAdapter.notifyDataSetChanged()
            getBeatList()
            binding.ivClearSearch.visibility = View.GONE
        }

        binding.imgClose.setOnClickListener {
            onBackPressed()
        }

        binding.tvCreateBeatPlan.setOnClickListener {
            startActivity(Intent(this, AddNewBeatPlanActivity::class.java))
        }

        binding.tvViewBeatPlan.setOnClickListener {
            startActivity(
                Intent(
                    this, FragmentContainerActivity::class.java
                ).putExtra(AppConstant.VIEW_BEAT_PLAN, true)
            )
        }

        binding.tvSortBy.setOnClickListener {
            val fragment = SortByBottomSheetDialogFragment.newInstance(this, sortByOrder)
            fragment.show(
                supportFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name
            )
        }

        binding.tvFilter.setOnClickListener {
            val fragment = BeatCustomerFilterBottomSheetDialogFragment.newInstance(
                this, filterCustomerLevel, filterAssignedStaff, filterParentCustomer
            )
            val bundle = Bundle()
            bundle.putBoolean(AppConstant.STAFF_DETAILS, true)
            bundle.putBoolean(AppConstant.CUSTOMER_TYPE, false)
            fragment.arguments = bundle
            fragment.show(
                supportFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name
            )
        }

    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            isApiLastPage = false
            isPageLoading = true
            beatList.clear()
            beatAdapter.notifyDataSetChanged()
            getBeatList()
        }
    }

    private fun initObservers() {
        beatViewModel.beatListLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                isPageLoading = false
                if (it.data.isNullOrEmpty().not()) {

                    if (currentPage == 1) {
                        beatList.clear()
                    }

                    beatList.addAll(it.data!!)
                    beatAdapter.notifyDataSetChanged()

                    if (it.data.size < 30) {
                        isApiLastPage = true
                    }
                } else {
                    isApiLastPage = true
                    binding.clEmptyData.visibility = View.VISIBLE
                }
            } else {
                if (it.errorCode != null && it.errorCode == 403) {
                    logout()
                } else {
                    showToast(it.message)
                }
            }
        }

        beatViewModel.deleteBeatLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                isDataChange = true
                if (deleteBeatPosition != -1) {
                    beatList.removeAt(deleteBeatPosition)
                    beatAdapter.notifyItemRemoved(deleteBeatPosition)

                    beatAdapter.notifyItemRangeChanged(deleteBeatPosition, beatList.size)
                    deleteBeatPosition = -1
                }
            } else {
                if (it.data?.isUsed == true) {
                    showDeleteDialog(beatList[deleteBeatPosition].id, true, it.message)
                } else {
                    showLongToastWithAction(it.message)
                }
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvBeatList.layoutManager = linearLayoutManager
        beatAdapter = BeatListAdapter(beatList, this)
        binding.rvBeatList.adapter = beatAdapter


        binding.rvBeatList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getBeatList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

    }

    private fun getBeatList() {
        binding.clEmptyData.visibility = View.GONE
        beatViewModel.getBeatList(
            binding.etSearch.text.toString(),
            filterAssignedStaff.first,
            filterCustomerLevel,
            filterParentCustomer.first,
            sortByOrder,
            currentPage
        )
    }

    override fun setBeatSelect(model: BeatListDataItem) {
        someActivityResultLauncher.launch(
            Intent(this, BeatDetailsActivity::class.java).putExtra(
                AppConstant.BEAT_ID, model.id
            )
        )
    }

    private fun showDeleteDialog(id: Int?, isForced: Boolean, message: String?) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_beat)

        if (isForced && message.isNullOrEmpty().not()) {
            tvTitle.text = message
            tvDelete.text = resources.getString(R.string.confirm)
        } else {
            tvTitle.text = resources.getString(R.string.delete_beat_message)
        }

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val jsonObject = JsonObject()
            jsonObject.addProperty("is_forced", isForced)
            beatViewModel.deleteBeat(id!!, jsonObject)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDeleteBeat(position: Int, model: BeatListDataItem) {
        deleteBeatPosition = position
        showDeleteDialog(model.id, false, "")
    }

    override fun onEditBeat(position: Int, model: BeatListDataItem) {
        editBeatPosition = position
        someActivityResultLauncher.launch(
            Intent(this, AddNewBeatActivity::class.java).putExtra(
                AppConstant.BEAT_ID, model.id
            )
        )
    }

    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            isDataChange = true
            currentPage = 1
            isApiLastPage = false
            getBeatList()
        }
    }

    override fun applyFilter(
        customerLevel: String,
        parentCustomer: Pair<Int, String>,
        assignedStaff: Pair<Int, String>
    ) {
        filterParentCustomer = parentCustomer
        filterCustomerLevel = customerLevel
        filterAssignedStaff = assignedStaff

        if (filterAssignedStaff.first != 0 && assignedStaffFilterApply.not()) {
            ++filterCount
            assignedStaffFilterApply = true
        }

        if (filterCustomerLevel.isEmpty().not() && levelFilterApply.not()) {
            ++filterCount
            levelFilterApply = true
        }

        if (filterParentCustomer.second.isEmpty().not() && customerTypeFilterApply.not()) {
            ++filterCount
            customerTypeFilterApply = true
        }

        if (filterAssignedStaff.first == 0 && assignedStaffFilterApply) {
            --filterCount
            assignedStaffFilterApply = false
        }

        if (filterCustomerLevel.isEmpty() && levelFilterApply) {
            --filterCount
            levelFilterApply = false
        }

        if (filterParentCustomer.second.isEmpty() && customerTypeFilterApply) {
            --filterCount
            customerTypeFilterApply = false
        }

        binding.tvFilterCount.text = "$filterCount"
        binding.tvFilterCount.visibility = View.VISIBLE

        if (filterCount == 0) {
            binding.tvFilterCount.visibility = View.GONE
        }

        currentPage = 1

        binding.clEmptyData.visibility = View.GONE

        beatList.clear()
        beatAdapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.VISIBLE
        getBeatList()
    }

    override fun applySortByName(order: String) {
        sortByOrder = order

        currentPage = 1

        binding.clEmptyData.visibility = View.GONE

        currentPage = 1
        beatList.clear()
        beatAdapter.notifyDataSetChanged()

        getBeatList()
    }

    override fun onBackPressed() {
        if (isDataChange) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}