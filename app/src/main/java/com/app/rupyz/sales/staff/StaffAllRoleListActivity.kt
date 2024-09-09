package com.app.rupyz.sales.staff

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.adapter.organization.profile.AllCategoryListAdapter
import com.app.rupyz.databinding.ActivityStaffAllRoleListBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.AllCategoryResponseModel

class StaffAllRoleListActivity : BaseActivity(), AllCategoryListAdapter.AllCategoryListener {
    private lateinit var binding: ActivityStaffAllRoleListBinding
    lateinit var adapter: AllCategoryListAdapter
    private lateinit var staffViewModel: StaffViewModel
    private var staffRoleList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffAllRoleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]

        initRecyclerView()

        initObservers()

        binding.progressBarApi.visibility = View.VISIBLE

        getRoleList()

        binding.ivBack.setOnClickListener {
            finish()
        }

    }

    private fun getRoleList() {
        staffViewModel.getRoleList(currentPage, hasInternetConnection())
    }

    private fun initRecyclerView() {
        binding.rvStaffRoleList.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvStaffRoleList.layoutManager = linearLayoutManager

        adapter = AllCategoryListAdapter(staffRoleList, this)
        binding.rvStaffRoleList.adapter = adapter

        binding.rvStaffRoleList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                getRoleList()
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

        staffViewModel.assignedRoleListLiveData.observe(this) { res ->
            binding.progressBarApi.visibility = View.GONE
            isPageLoading = false
            if (res.error == false) {
                if (res.data.isNullOrEmpty().not()) {
                    res.data?.let {
                        it.forEach { role ->
                            val model = AllCategoryResponseModel()
                            model.name = role.name
                            staffRoleList.add(model)
                        }

                        adapter.notifyDataSetChanged()

                        if (it.size < 30){
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                }
            } else {
                showToast(res.message)
            }
        }
    }

    override fun onSelect(model: AllCategoryResponseModel, position: Int) {
        val i = Intent()
        i.putExtra(AppConstant.STAFF_ROLE, model.name)
        setResult(RESULT_OK, i)
        finish()
    }
}