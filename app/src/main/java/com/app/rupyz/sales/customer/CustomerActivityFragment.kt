package com.app.rupyz.sales.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentCustomerActivityBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import kotlin.math.roundToInt

class CustomerActivityFragment : BaseFragment(),
        CustomerActivityAdapter.ICustomerFeedbackActionListener {
    private lateinit var binding: FragmentCustomerActivityBinding
    private lateinit var customerActivityAdapter: CustomerActivityAdapter
    private var activityList = ArrayList<CustomerFollowUpDataItem>()
    private lateinit var activityViewModel: StaffActivityViewModel

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var customerId: Int = -1
    private var customerType: String = ""

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityViewModel = ViewModelProvider(this)[StaffActivityViewModel::class.java]

        initObservers()

        arguments?.let {
            customerType = it.getString(AppConstant.CUSTOMER_TYPE)!!
            customerId = it.getInt(AppConstant.CUSTOMER_ID)
            binding.progressBar.visibility = View.VISIBLE
        }

        initRecyclerView()
    }

    private fun initObservers() {
        activityViewModel.getCustomerFeedbackListLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                isPageLoading = false
                if (it.data.isNullOrEmpty().not()) {
                    binding.clEmptyData.visibility = View.GONE
                    if (currentPage == 1) {
                        activityList.clear()
                    }
                    activityList.addAll(it.data!!)

                    if (it.data!!.size < 30) {
                        isApiLastPage = true
                    }
                    customerActivityAdapter.notifyDataSetChanged()
                } else {
                    if (currentPage == 1) {
                        isApiLastPage = true
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomerList.layoutManager = linearLayoutManager

        customerActivityAdapter =
                CustomerActivityAdapter(
                        activityList, this, customerType
                )

        binding.rvCustomerList.adapter = customerActivityAdapter

        binding.rvCustomerList.addOnScrollListener(object :
                PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadActivityPage()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }

    private fun loadActivityPage() {
        binding.clEmptyData.visibility = View.GONE
        activityViewModel.getCustomerFeedback(customerId, currentPage, customerType, hasInternetConnection())
    }

    override fun getFeedbackDetails(model: CustomerFollowUpDataItem) {
        someActivityResultLauncher.launch(
                Intent(
                        requireContext(),
                        CustomerFeedbackDetailActivity::class.java
                ).putExtra(AppConstant.ACTIVITY_ID, model.id)
                        .putExtra(AppConstant.CUSTOMER_ID, customerId)
                        .putExtra(AppConstant.CUSTOMER_TYPE, customerType)
        )
    }

    override fun getMapLocation(model: CustomerFollowUpDataItem) {
        if (model.geoLocationLat != null && model.geoLocationLat?.roundToInt() != 0) {
            Utils.openMap(
                    requireContext(),
                    model.geoLocationLat,
                    model.geoLocationLong,
                    model.label
                         )
        } else {
            showToast(resources.getString(R.string.no_location_found))
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
            currentPage = 1
            binding.progressBar.visibility = View.VISIBLE
            loadActivityPage()
        }
    }

    override fun onResume() {
        super.onResume()
        loadActivityPage()
    }

}