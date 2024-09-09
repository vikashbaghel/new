package com.app.rupyz.sales.lead

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.adapter.organization.profile.AllCategoryListAdapter
import com.app.rupyz.databinding.ActivityLeadCategoryBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.sales.product.AddCategoryActivity
import com.app.rupyz.sales.product.SearchCategoryActivity

class LeadCategoryActivity : BaseActivity(),
    AllCategoryListAdapter.AllCategoryListener {
    private lateinit var binding: ActivityLeadCategoryBinding

    private lateinit var leadViewModel: LeadViewModel
    private var categoryList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private lateinit var adapter: AllCategoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeadCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]

        initRecyclerView()
        initObservers()

        leadViewModel.getAllCategoryList("", hasInternetConnection())

        binding.ivClose.setOnClickListener {
            finish()
        }
        if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_LEAD_CATEGORY_PERMISSION, false)) {
            binding.ivAddCategory.visibility = View.VISIBLE
        } else {
            binding.ivAddCategory.visibility = View.GONE
        }

        binding.ivAddCategory.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(
                    this,
                    AddCategoryActivity::class.java
                ).putExtra(AppConstant.LEAD_CATEGORY, true)
            )
        }

        binding.etSearch.setOnClickListener {
            searchCategoryResultLauncher.launch(
                Intent(
                    this,
                    SearchCategoryActivity::class.java
                ).putExtra(AppConstant.LEAD_CATEGORY, true)
            )
        }
    }

    private var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            leadViewModel.getAllCategoryList("", hasInternetConnection())
        }
    }

    private var searchCategoryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->

        if (result.data != null && result.data!!.hasExtra(AppConstant.CATEGORY)) {
            val category = result.data?.getStringExtra(AppConstant.CATEGORY)!!
            val intent = Intent()
            intent.putExtra(AppConstant.LEAD_CATEGORY, category)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initRecyclerView() {
        binding.rvCategoryList.setHasFixedSize(true)
        binding.rvCategoryList.layoutManager = LinearLayoutManager(this)
        adapter = AllCategoryListAdapter(categoryList, this)
        binding.rvCategoryList.adapter = adapter
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {

        leadViewModel.leadCategoryLiveData.observe(this) {
            it.data?.let { list ->
                categoryList.clear()
                list.forEach { category->
                    val model = AllCategoryResponseModel()
                    model.name = category.name
                    model.id = category.id
                    categoryList.add(model)
                }

                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onSelect(model: AllCategoryResponseModel, position: Int) {
        val intent = Intent()
        intent.putExtra(AppConstant.LEAD_CATEGORY, model.name)
        setResult(RESULT_OK, intent)
        finish()
    }
}