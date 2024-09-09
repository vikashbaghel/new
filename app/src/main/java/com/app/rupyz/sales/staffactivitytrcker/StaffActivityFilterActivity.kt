package com.app.rupyz.sales.staffactivitytrcker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.StaffActivityFilterBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.REPORTING_TO
import com.app.rupyz.generic.utils.AppConstant.ROLE
import com.app.rupyz.generic.utils.AppConstant.STAFF
import com.app.rupyz.generic.utils.AppConstant.selected_item
import com.app.rupyz.model_kt.AssignedRoleItem
import com.app.rupyz.model_kt.StaffActivityFilterModel
import com.app.rupyz.model_kt.gallery.FilterData
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.sales.gallery.adapter.DebounceClickListener
import com.app.rupyz.sales.gallery.adapter.FilterGalleryPicsAdapter
import com.app.rupyz.sales.staff.StaffViewModel
import java.util.Locale


class StaffActivityFilterActivity : BaseActivity(), DebounceClickListener, OnRoleCheckListener,
    FilterStaffAdapter.OnStaffItemCheckListener {
    private lateinit var binding: StaffActivityFilterBinding

    private val staffViewModel: StaffViewModel by viewModels()

    private lateinit var adapter: FilterGalleryPicsAdapter
    private lateinit var roleAdapter: RoleFilterAdapter
    private lateinit var filterStaffAdapter: FilterStaffAdapter

    private lateinit var filterList: ArrayList<FilterData>
    private var assignedRoleList = ArrayList<AssignedRoleItem>()
    private var filteredRoleList: ArrayList<String?> = ArrayList()
    private var filteredStaffId: Int? = null
    private var staffList = ArrayList<StaffData>()

    private lateinit var staffRole: String
    private var filterType: String = ROLE

    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    private var currentPage = 1
    private var isApiLastPage = false
    private var isPageLoading = false

    private var staffActivityFilterModel: StaffActivityFilterModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StaffActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        staffRole = ""

        staffActivityFilterModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                AppConstant.ACTIVITY_FILTER,
                StaffActivityFilterModel::class.java
            )
        } else {
            intent.getParcelableExtra(
                AppConstant.ACTIVITY_FILTER
            )
        }

        if (staffActivityFilterModel == null) {
            staffActivityFilterModel = StaffActivityFilterModel()
        }

        getRoleList()

        listData()

        initRecyclerView()
        initRoleRecyclerView()

        initObservers()

        binding.btnApply.setOnClickListener {
            val intent = Intent()
            intent.putExtra(AppConstant.ACTIVITY_FILTER, staffActivityFilterModel)
            setResult(RESULT_OK, intent)
            finish()
            selected_item = 0
        }

        binding.imgClose.setOnClickListener {
            finish()
            selected_item = 0
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                when (filterType) {
                    ROLE -> filterRole(binding.etSearch.text.toString())
                    STAFF -> getStaffList()
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

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearSearch.visibility = View.GONE
                    when (filterType) {
                        ROLE -> filterRole(binding.etSearch.text.toString())
                        STAFF -> getStaffList()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishChecker, delay);
                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            when (filterType) {
                ROLE -> filterRole(binding.etSearch.text.toString())
                STAFF -> getStaffList()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                selected_item = 0
            }
        })

        if (staffActivityFilterModel != null) {
            if (staffActivityFilterModel?.filterRoleList.isNullOrEmpty().not() ||
                staffActivityFilterModel?.filterStaffId != null
            ) {
                binding.buttonClearFilter.visibility = View.VISIBLE
            } else {
                binding.buttonClearFilter.visibility = View.GONE
            }
        } else {
            binding.buttonClearFilter.visibility = View.GONE
        }

        binding.buttonClearFilter.setOnClickListener {
            staffActivityFilterModel?.filterStaffId = null
            staffActivityFilterModel?.filterRoleList?.clear()
            when (filterType) {
                ROLE -> {
                    resetRoleFilter()
                }

                STAFF -> resetStaffFilter()
            }

            binding.buttonClearFilter.visibility = View.GONE
        }
    }

    private fun resetStaffFilter() {
        staffList.forEach {
            it.isSelected = false
        }

        filterStaffAdapter.notifyDataSetChanged()
    }

    private fun resetRoleFilter() {
        assignedRoleList.forEach {
            it.isSelected = false
        }

        roleAdapter.notifyDataSetChanged()
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            validateSearch()
        }
    }

    private fun validateSearch() {
        when (filterType) {
            ROLE -> filterRole(binding.etSearch.text.toString())
            STAFF -> getStaffList()
        }
    }

    private fun getRoleList() {
        assignedRoleList.clear()
        staffViewModel.getRoleList(currentPage, hasInternetConnection())
    }


    private fun filterRole(text: String) {
        if (text.isEmpty()) {
            binding.clEmptyData.visibility = View.GONE
            roleAdapter.filterList(assignedRoleList)
        } else {
            val roleList: ArrayList<AssignedRoleItem> = ArrayList()
            for (item in assignedRoleList) {
                if (item.name?.lowercase(Locale.ROOT)!!.contains(text.lowercase(Locale.ROOT))) {
                    roleList.add(item)

                    roleList.forEach {
                        if (filteredRoleList.contains(it.name)) {
                            it.isSelected = true
                        }
                    }
                }
            }

            if (roleList.isEmpty()) {
                binding.clEmptyData.visibility = View.VISIBLE
                binding.rvDataView.visibility = View.GONE
            } else {
                binding.clEmptyData.visibility = View.GONE
                binding.rvDataView.visibility = View.VISIBLE
                roleAdapter.filterList(roleList)
            }
        }
    }

    private fun initRecyclerView() {
        adapter = FilterGalleryPicsAdapter(filterList, this)
        binding.rvFilter.adapter = adapter
    }

    private fun initRoleRecyclerView() {
        roleAdapter = RoleFilterAdapter(assignedRoleList, this)
        binding.rvDataView.adapter = roleAdapter
    }

    private fun initStaffRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvDataView.layoutManager = linearLayoutManager
        filterStaffAdapter = FilterStaffAdapter(staffList, this)
        binding.rvDataView.adapter = filterStaffAdapter
        binding.rvDataView.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getStaffList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        staffViewModel.assignedRoleListLiveData.observe(this) { data ->
            data?.let { res ->
                if (res.error == false) {
                    res.data?.let {
                        assignedRoleList.addAll(it)

                        if (staffActivityFilterModel?.filterRoleList.isNullOrEmpty().not()) {
                            filteredRoleList = ArrayList()
                            log("${staffActivityFilterModel?.filterRoleList}")
                            filteredRoleList.addAll(staffActivityFilterModel?.filterRoleList!!)

                            assignedRoleList.forEach { role ->
                                if (filteredRoleList.contains(role.name)) {
                                    role.isSelected = true
                                }
                            }
                        }

                        roleAdapter.notifyDataSetChanged()

                    }
                }
            }
        }

        staffViewModel.getStaffListData().observe(this) { data ->
            binding.progressBar.visibility = View.GONE
            binding.progressBarMain.visibility = View.GONE
            if (data.error == false) {
                if (data.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    data.data?.let {
                        isPageLoading = false
                        if (currentPage == 1) {
                            staffList.clear()
                        }

                        staffList.addAll(it)

                        if (staffActivityFilterModel?.filterStaffId != null) {
                            filteredStaffId = staffActivityFilterModel?.filterStaffId

                            staffList.forEach { staff ->
                                if (staff.id == filteredStaffId) {
                                    staff.isSelected = true
                                }
                            }
                        }

                        filterStaffAdapter.notifyDataSetChanged()

                        if (it.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        staffList.clear()
                        filterStaffAdapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                if (data.errorCode == 403) {
                    logout()
                } else {
                    showToast("${data.message}")
                }
            }
        }
    }


    private fun listData() {
        filterList = ArrayList()
        filterList.add(FilterData(ROLE, true))
        filterList.add(FilterData(REPORTING_TO, false))
    }


    override fun onDebounceClick(position: Int, data: FilterData) {
        binding.ivClearSearch.visibility = View.GONE
        binding.etSearch.setText("")
        when (position) {
            0 -> {
                filterType = ROLE
                initRoleRecyclerView()
                getRoleList()
            }

            1 -> {
                filterType = STAFF
                getStaffList()
                initStaffRecyclerView()
            }
        }

        filterList.forEach { it.isSelected = false }
        filterList[position].isSelected = true
        adapter.notifyDataSetChanged()
    }

    private fun getStaffList() {
        if (currentPage > 1) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBarMain.visibility = View.VISIBLE
        }

        isPageLoading = true
        isApiLastPage = false
        staffViewModel.getStaffList(
            staffRole, binding.etSearch.text.toString(), currentPage, hasInternetConnection()
        )
    }

    override fun onRoleCheckChange(name: String?, isCheck: Boolean) {
        if (isCheck) {
            filteredRoleList.add(name)
        } else {
            val index = filteredRoleList.indexOfLast { it == name }
            if (index != -1) {
                filteredRoleList.removeAt(index)
            }
        }

        staffActivityFilterModel?.filterRoleList = filteredRoleList

        binding.buttonClearFilter.visibility = View.VISIBLE
    }

    override fun onStaffCheckChange(model: StaffData, adapterPosition: Int) {
        staffList.forEach { it.isSelected = false }
        staffList[adapterPosition].isSelected = true
        filterStaffAdapter.notifyDataSetChanged()


        filteredStaffId = model.id
        staffActivityFilterModel?.filterStaffId = model.id

        binding.buttonClearFilter.visibility = View.VISIBLE
    }

}

