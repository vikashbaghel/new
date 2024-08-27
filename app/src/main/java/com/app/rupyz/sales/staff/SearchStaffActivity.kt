package com.app.rupyz.sales.staff

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.ActivitySearchStaffBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.order.sales.StaffData

class SearchStaffActivity : BaseActivity(), StaffActionListener {
    private lateinit var binding: ActivitySearchStaffBinding
    private var isUpdate: Boolean = false
    private lateinit var staffViewModel: StaffViewModel
    private lateinit var staffListAdapter: StaffListAdapter
    private var staffList = ArrayList<StaffData>()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        staffViewModel = ViewModelProvider(this)[StaffViewModel::class.java]

        binding.etSearch.requestFocus()

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            staffList.clear()
            Utils.hideKeyboard(this)
            validateSearch()
        }

        initRecyclerView()
        initObservers()

        validateSearch()

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                staffList.clear()
                validateSearch()
                Utils.hideKeyboard(this)
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
                    validateSearch()
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
            binding.tvErrorMessage.visibility = View.GONE

            staffList.clear()
            staffListAdapter.notifyDataSetChanged()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            validateSearch()
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
                validateSearch()
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
        staffViewModel.getStaffListData().observe(this) {
            binding.progressBar.visibility = View.GONE
            if (!it.data.isNullOrEmpty()) {
                isPageLoading = false
                binding.tvErrorMessage.visibility = View.GONE

                if (currentPage == 1) {
                    staffList.clear()
                }

                staffList.addAll(it.data!!)

                if (it.data!!.size < 30) {
                    isApiLastPage = true
                }

                staffListAdapter.notifyDataSetChanged()
            } else {
                isApiLastPage = true

                if (currentPage == 1) {
                    staffList.clear()
                    staffListAdapter.notifyDataSetChanged()
                }
                binding.tvErrorMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun validateSearch() {
        binding.progressBar.visibility = View.VISIBLE
        staffViewModel.getStaffList(
            "",
            binding.etSearch.text.toString(),
            currentPage,
            hasInternetConnection()
        )
    }


    override fun onCall(model: StaffData, position: Int) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${model.mobile}")
        startActivity(intent)
    }

    override fun onWCall(model: StaffData, position: Int) {
        val uri =
            Uri.parse(
                "https://api.whatsapp.com/send?phone=+91${model.mobile}" +
                        "&text= Hi, ${model.name}"
            )
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    }

    override fun onEdit(model: StaffData, position: Int) {
        isUpdate = true
        startActivity(
            Intent(this, AddNewStaffMemberActivity::class.java)
                .putExtra(AppConstant.STAFF_ID, model.id)
        )
    }

    override fun onDelete(model: StaffData, position: Int) {
        staffViewModel.deleteStaff(model.id!!)
    }

    override fun onGetInfo(model: StaffData, position: Int) {
        startActivity(
            Intent(this, StaffDetailsActivity::class.java).putExtra(
                AppConstant.STAFF_ID,
                model.id
            )
        )
    }
}