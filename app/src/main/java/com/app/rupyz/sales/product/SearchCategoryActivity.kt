package com.app.rupyz.sales.product

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.adapter.organization.profile.AllCategoryListAdapter
import com.app.rupyz.databinding.ActivitySearchCategoryBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.logger.Logger
import com.app.rupyz.generic.network.ApiClient
import com.app.rupyz.generic.network.EquiFaxApiInterface
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharePrefConstant.TOKEN
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.AllCategoryInfoModel
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.sales.lead.LeadViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchCategoryActivity : BaseActivity(), AllCategoryListAdapter.AllCategoryListener {
    private lateinit var binding: ActivitySearchCategoryBinding
    private var isDataChange: Boolean = false
    private lateinit var adapter: AllCategoryListAdapter
    private var mEquiFaxApiInterface: EquiFaxApiInterface? = null
    private var categoryList: ArrayList<AllCategoryResponseModel> = ArrayList()

    private lateinit var leadViewModel: LeadViewModel

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    var delay: Long = 500
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mEquiFaxApiInterface = ApiClient.getRetrofit().create(
                EquiFaxApiInterface::class.java
        )
        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]

        binding.etSearch.requestFocus()

        Utility(this)

        initRecyclerView()
        initObservers()

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            categoryList.clear()
            Utils.hideKeyboard(this)
            validateSearch()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                categoryList.clear()
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

            categoryList.clear()
            adapter.notifyDataSetChanged()
        }

        binding.ivBack.setOnClickListener { finish() }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            validateSearch()
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvCategoryList.setHasFixedSize(true)
        binding.rvCategoryList.layoutManager = layoutManager
        adapter = AllCategoryListAdapter(
                categoryList,
                this
        )
        binding.rvCategoryList.adapter = adapter

        binding.rvCategoryList.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
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
        leadViewModel.leadCategoryLiveData.observe(this) {
            isPageLoading = false
            binding.progressBar.visibility = View.GONE
            it.data?.let { list ->
                list.forEach { category ->
                    val model = AllCategoryResponseModel()
                    model.name = category.name
                    model.id = category.id
                    categoryList.add(model)
                }

                adapter.notifyDataSetChanged()

                if (list.size < 30) {
                    isApiLastPage = true
                }
            }
        }
    }

    private fun validateSearch() {

        binding.progressBar.visibility = View.VISIBLE

        if (intent.hasExtra(AppConstant.LEAD_CATEGORY)) {
            searchLeadCategory()
        } else {
            searchProductCategory()
        }
    }

    private fun searchLeadCategory() {
        leadViewModel.getAllCategoryList(binding.etSearch.text.toString(), hasInternetConnection())
    }


    private fun searchProductCategory() {
        val call: Call<AllCategoryInfoModel> = mEquiFaxApiInterface!!.getSearchedCategoryList(
                SharedPref.getInstance().getInt(ORG_ID),
                "Bearer " + SharedPref.getInstance().getString(TOKEN),
                currentPage,
                binding.etSearch.text.toString(), true
        )

        call.enqueue(object : Callback<AllCategoryInfoModel?> {
            override fun onResponse(
                    call: Call<AllCategoryInfoModel?>,
                    response: Response<AllCategoryInfoModel?>
            ) {
                binding.progressBar.visibility = View.GONE
                if (response.code() == 200) {
                    val response1 = response.body()
                    if (response1?.data.isNullOrEmpty().not()) {
                        isPageLoading = false
                        categoryList.addAll(response1?.data!!)
                        binding.tvErrorMessage.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                        if (response1.data?.size!! < 30) {
                            isApiLastPage = true
                        }
                    } else if (categoryList.size == 0) {
                        isApiLastPage = true
                    } else {
                        binding.rvCategoryList.visibility = View.GONE
                        binding.tvErrorMessage.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(
                            this@SearchCategoryActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AllCategoryInfoModel?>, t: Throwable) {
                Logger.errorLogger(this.javaClass.name, t.message)
                call.cancel()
            }
        })
    }

    override fun onSelect(model: AllCategoryResponseModel, position: Int) {
        val i = Intent()
        i.putExtra(AppConstant.CATEGORY, model.name)
        setResult(RESULT_OK, i)
        finish()
    }
}