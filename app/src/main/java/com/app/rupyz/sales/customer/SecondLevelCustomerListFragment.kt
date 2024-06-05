package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentSecondLevelCustomerListBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.beatplan.SelectCustomerForBeatPlanFragment
import com.app.rupyz.sales.beatplan.SortByBottomSheetDialogFragment
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.payment.AddRecordPaymentActivity
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity

class SecondLevelCustomerListFragment : BaseFragment(), CustomerActionListener,
        SortByBottomSheetDialogFragment.ISortByCustomerListener,
        CustomerFilterBottomSheetDialogFragment.IBeatCustomerFilterListener {
    private lateinit var binding: FragmentSecondLevelCustomerListBinding
    private val customerViewModel: CustomerViewModel by viewModels()

    private lateinit var customerAdapter: ListOfAllCustomerAdapter
    private var customerList = ArrayList<CustomerData>()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var filterCustomerLevel = ""
    private var assignedStaffFilterApply = false

    private var filterAssignedStaff: Pair<Int, String> = Pair(0, "")
    private var filterCustomerType: ArrayList<CustomerTypeDataItem> = ArrayList()
    private var sortByOrder: String = ""

    private var filterCount = 0
    private var levelFilterApply = false
    private var customerTypeFilterApply = false

    private var customerId: Int = 0
    var delay: Long = 500 // 1 seconds after user stops typing
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondLevelCustomerListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerId = arguments?.getInt(AppConstant.CUSTOMER_ID, 0) ?: 0

        initRecyclerView()

        initObservers()

        loadCustomerList()

        binding.tvSortBy.setOnClickListener {
            val fragment = SortByBottomSheetDialogFragment.newInstance(this, sortByOrder)
            fragment.show(childFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name)
        }

        binding.tvFilter.setOnClickListener {
            val fragment = CustomerFilterBottomSheetDialogFragment.newInstance(
                    this,
                    filterCustomerLevel,
                    filterAssignedStaff,
                    filterCustomerType
            )
            val bundle = Bundle()
            bundle.putBoolean(AppConstant.CUSTOMER_TYPE, false)
            fragment.arguments = bundle
            fragment.show(childFragmentManager, SelectCustomerForBeatPlanFragment::class.java.name)
        }

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            customerList.clear()
            Utils.hideKeyboard(requireActivity())
            loadCustomerList()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                customerList.clear()
                customerAdapter.notifyDataSetChanged()
                customerList.clear()
                loadCustomerList()
                Utils.hideKeyboard(requireActivity())
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
                    loadCustomerList()
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
            binding.clEmptyData.visibility = View.GONE

            customerList.clear()
            customerAdapter.notifyDataSetChanged()
        }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            customerList.clear()
            customerAdapter.notifyDataSetChanged()
            loadCustomerList()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomerList.layoutManager = linearLayoutManager
        customerAdapter =
                ListOfAllCustomerAdapter(
                        customerList,
                        this,
                        false,
                        hasInternetConnection()
                )

        binding.rvCustomerList.adapter = customerAdapter

        binding.rvCustomerList.addOnScrollListener(object :
                PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadCustomerList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

    }

    private fun loadCustomerList() {
        if (currentPage == 1) {
            binding.shimmerCustomer.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
        customerViewModel.getCustomerList(
                customerId,
                binding.etSearch.text.toString(),
                filterCustomerLevel,
                filterCustomerType,
                sortByOrder,
                currentPage,
                hasInternetConnection()
        )
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        customerViewModel.getCustomerListData().observe(viewLifecycleOwner) { data ->
            binding.shimmerCustomer.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            if (data.error == false) {

                if (data.data.isNullOrEmpty().not()) {
                    data.data?.let { it ->
                        isPageLoading = false

                        binding.clEmptyData.visibility = View.GONE
                        if (currentPage == 1) {
                            customerList.clear()
                        }

                        it.forEach { customerData ->
                            customerData.customerParentName = null
                            customerList.add(customerData)
                        }

                        customerAdapter.notifyDataSetChanged()

                        if (it.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    if (currentPage == 1) {
                        isApiLastPage = true
                        customerList.clear()
                        customerAdapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }

            } else {
                showToast(data.message)
            }
        }

    }

    override fun onCall(model: CustomerData, position: Int) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${model.mobile}")
        startActivity(intent)
    }

    override fun onWCall(model: CustomerData, position: Int) {
        val uri =
                Uri.parse("https://api.whatsapp.com/send?phone=+91${model.mobile}&text=Hi, ${model.name}")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    }

    override fun onNewOrder(model: CustomerData, position: Int) {
        if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
            SharedPref.getInstance().clearCart()
            startActivity(
                    Intent(requireContext(), CreateNewOrderForCustomerActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                            .putExtra(AppConstant.CUSTOMER_ID, model.id)
                            .putExtra(AppConstant.PAYMENT_INFO, model.paymentTerm)
            )
        } else {
            showToast(resources.getString(R.string.create_order_permission))
        }
    }

    override fun onRecordPayment(model: CustomerData, position: Int) {
        if (PermissionModel.INSTANCE.getPermission(AppConstant.VIEW_PAYMENT_PERMISSION, false)) {
            someActivityResultLauncher.launch(
                Intent(
                    requireContext(),
                    AddRecordPaymentActivity::class.java
                ).putExtra(AppConstant.CUSTOMER, model)
                .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                    .putExtra(AppConstant.CUSTOMER_ID, model.id)
            )
        } else {
            showToast(resources.getString(R.string.payment_permission))
        }
    }

    override fun onGetCustomerInfo(model: CustomerData) {
        startActivity(
                Intent(requireContext(), CustomerDetailActivity::class.java)
                        .putExtra(AppConstant.CUSTOMER_ID, model.id)
        )
    }

    override fun recordCustomerActivity(model: CustomerData) {
        if (PermissionModel.INSTANCE.hasRecordActivityPermission()) {
            someActivityResultLauncher.launch(
                    Intent(requireContext(), CustomFormActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER_ID, model.id)
                            .putExtra(AppConstant.CUSTOMER, model)
                            .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.CUSTOMER_FEEDBACK)
            )
        } else {
            showToast(resources.getString(R.string.you_dont_have_permission_to_perform_this_action))
        }
    }

    override fun viewCustomerPhoto(model: CustomerData) {
        if (model.logoImageUrl.isNullOrEmpty().not()) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            val imageModel = ImageViewModel(0, 0, model.logoImageUrl)
            imageViewModelArrayList.add(imageModel)

            imageListModel.data = imageViewModelArrayList
            startActivity(
                    Intent(requireContext(), OrgPhotosViewActivity::class.java)
                            .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                            .putExtra(AppConstant.IMAGE_POSITION, 0)
            )
        } else {
            showToast(resources.getString(R.string.customer_pic_not_available))
        }
    }

    override fun viewCustomerLocation(model: CustomerData) {
        if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
            Utils.openMap(requireContext(), model.mapLocationLat, model.mapLocationLong, model.name)
        } else {
            showToast(resources.getString(R.string.no_location_found))
        }

    }


    var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            customerList.clear()
            customerAdapter.notifyDataSetChanged()

            loadCustomerList()
        }
    }

    override fun applyFilter(
            customerLevel: String,
            customerType: ArrayList<CustomerTypeDataItem>,
            assignedStaff: Pair<Int, String>
    ) {
        filterCustomerType = customerType
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

        if (filterCustomerType.isEmpty().not() && customerTypeFilterApply.not()) {
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

        if (filterCustomerType.isEmpty() && customerTypeFilterApply) {
            --filterCount
            customerTypeFilterApply = false
        }

        binding.tvFilterCount.text = "$filterCount"
        binding.tvFilterCount.visibility = View.VISIBLE

        if (filterCount == 0) {
            binding.tvFilterCount.visibility = View.GONE
        }

        currentPage = 1
        customerList.clear()
        customerAdapter.notifyDataSetChanged()

        binding.clEmptyData.visibility = View.GONE
        loadCustomerList()
    }

    override fun applySortByName(order: String) {
        sortByOrder = order

        currentPage = 1
        customerList.clear()
        customerAdapter.notifyDataSetChanged()

        binding.clEmptyData.visibility = View.GONE
        loadCustomerList()
    }
}
