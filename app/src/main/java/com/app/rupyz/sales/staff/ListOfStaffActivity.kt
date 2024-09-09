package com.app.rupyz.sales.staff

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.organization.profile.CategoryListAdapter
import com.app.rupyz.adapter.organization.profile.CategoryListener
import com.app.rupyz.databinding.ActivityListOfStaffBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.order.sales.StaffData

class ListOfStaffActivity : BaseActivity(), StaffActionListener, CategoryListener,
    DeleteDialog.IOnClickListener {
    private lateinit var binding: ActivityListOfStaffBinding
    private var isDataChange = false
    private lateinit var staffViewModel: StaffViewModel

    private lateinit var staffListAdapter: StaffListAdapter
    private lateinit var categoryListAdapter: CategoryListAdapter

    private var staffRoleList: ArrayList<AllCategoryResponseModel> = ArrayList()

    private var staffList = ArrayList<StaffData>()
    private var isUpdate: Boolean = false
    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private lateinit var staffRole: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]
        initRecyclerView()
        initObservers()

        staffRole = ""

        staffViewModel.getRoleList(1, hasInternetConnection())

        loadStaffData()

        binding.ivBack.setOnClickListener {
            if (isDataChange) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
            }
            finish()
        }

        if (hasInternetConnection().not() || PermissionModel.INSTANCE.getPermission(
                AppConstant.CREATE_STAFF_PERMISSION,
                false
            ).not()
        ) {
            binding.ivAddStaff.visibility = View.GONE
        }

        binding.ivAddStaff.setOnClickListener {
            someActivityResultLauncher.launch(
                Intent(
                    this@ListOfStaffActivity,
                    AddNewStaffMemberActivity::class.java
                )
            )
        }

        binding.ivSearch.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SearchStaffActivity::class.java
                )
            )
        }

        binding.tvAllCategory.setOnClickListener {
            binding.tvAllCategory.setBackgroundResource(R.drawable.details_button_style)
            binding.tvAllCategory.setTextColor(getColor(R.color.white))
            for (i in staffRoleList.indices) {
                staffRoleList[i].isSelected = false
            }
            binding.tvMoreCategory.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
            binding.tvMoreCategory.setTextColor(getColor(R.color.tab_un_selected_color))
            staffRole = ""
            staffList.clear()
            loadStaffData()
            staffListAdapter.notifyDataSetChanged()
            categoryListAdapter.notifyDataSetChanged()
        }

        binding.tvMoreCategory.setOnClickListener {
            binding.tvAllCategory.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
            binding.tvAllCategory.setTextColor(getColor(R.color.tab_un_selected_color))
            for (i in staffRoleList.indices) {
                staffRoleList[i].isSelected = false
            }
            categoryListAdapter.notifyDataSetChanged()
            binding.tvMoreCategory.setBackgroundResource(R.drawable.details_button_style)
            binding.tvMoreCategory.setTextColor(getColor(R.color.white))
            val intent =
                Intent(this, StaffAllRoleListActivity::class.java)
            roleListActivityLauncher.launch(intent)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private var roleListActivityLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null && result.data?.hasExtra(AppConstant.STAFF_ROLE)!!) {
                staffRole = result.data?.getStringExtra(AppConstant.STAFF_ROLE)!!
                staffList.clear()
                currentPage = 1
                loadStaffData()
                staffListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvStaffList.layoutManager = linearLayoutManager
        staffListAdapter = StaffListAdapter(staffList, this, hasInternetConnection())
        binding.rvStaffList.adapter = staffListAdapter

        binding.rvStaffList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadStaffData()
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
        categoryListAdapter = CategoryListAdapter(staffRoleList, this)
        binding.rvCategoryList.adapter = categoryListAdapter
    }


    var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            currentPage = 1
            loadStaffData()
        }
    }


    private fun loadStaffData() {
        staffViewModel.getStaffList(staffRole, "", currentPage, hasInternetConnection())
        if (currentPage > 1) {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        staffViewModel.getStaffListData().observe(this) { data ->
            data.data?.let { it ->
                isPageLoading = false
                binding.progressBarApi.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                if (it.isNotEmpty()) {
                    binding.message.visibility = View.GONE
                    if (currentPage == 1) {
                        staffList.clear()
                    }
                    staffList.addAll(it)
                    staffListAdapter.notifyDataSetChanged()

                    if (it.size < 30) {
                        isApiLastPage = true
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        binding.message.visibility = View.VISIBLE
                        staffList.clear()
                        staffListAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        staffViewModel.assignedRoleListLiveData.observe(this) { data ->
            data?.let { res ->

                if (res.error == false) {
                    res.data?.let {

                        it.forEach { role ->
                            val model = AllCategoryResponseModel()
                            model.name = role.name
                            staffRoleList.add(model)
                        }

                        categoryListAdapter.notifyDataSetChanged()

                        if (staffRoleList.size <= 5) {
                            binding.tvMoreCategory.visibility = View.GONE
                        } else {
                            binding.tvMoreCategory.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        staffViewModel.deleteStaffByIdLiveData.observe(this) {
            if (it.error == false) {
                currentPage = 1
                staffList.clear()
                staffViewModel.getStaffList(staffRole, "", currentPage, hasInternetConnection())
            }
        }
    }

    override fun onCall(model: StaffData, position: Int) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${model.mobile}")
        startActivity(intent)
    }

    override fun onWCall(model: StaffData, position: Int) {
        val uri =
            Uri.parse("https://api.whatsapp.com/send?phone=+91${model.mobile}&text= Hi, ${model.name}")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    }

    override fun onEdit(model: StaffData, position: Int) {
        isUpdate = true
        someActivityResultLauncher.launch(
            Intent(this, AddNewStaffMemberActivity::class.java)
                .putExtra(AppConstant.STAFF_ID, model.id)
        )
    }

    override fun onDelete(model: StaffData, position: Int) {
        DeleteDialog.showDeleteDialog(this,model.id,position,resources.getString(R.string.delete_staff),resources.getString(R.string.delete_staff_message),this)
    }


    override fun onGetInfo(model: StaffData, position: Int) {
        if (hasInternetConnection()) {
            startActivity(
                Intent(this, StaffDetailsActivity::class.java).putExtra(
                    AppConstant.STAFF_ID,
                    model.id
                )
            )
        }
    }

    override fun onBackPressed() {
        if (isDataChange) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    override fun onCategorySelect(model: AllCategoryResponseModel, position: Int) {
        if (model.isSelected == false) {
            for (i in staffRoleList.indices) {
                staffRoleList[i].isSelected = false
            }
            staffRole = staffRoleList[position].name.toString()
            staffRoleList[position].isSelected = true
            binding.tvAllCategory.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
            binding.tvAllCategory.setTextColor(getColor(R.color.tab_un_selected_color))
            binding.tvMoreCategory.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
            binding.tvMoreCategory.setTextColor(getColor(R.color.tab_un_selected_color))

            currentPage = 1
            staffList.clear()
            loadStaffData()
            staffListAdapter.notifyDataSetChanged()
        }
        categoryListAdapter!!.notifyDataSetChanged()
    }

    override fun onDelete(model: Any, position: Any) {
        staffViewModel.deleteStaff(model as Int)
    }
}