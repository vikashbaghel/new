package com.app.rupyz.sales.beatplan

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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentAssignedCustomerBeatBinding
import com.app.rupyz.dialog.DeleteDialogFragment
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CheckInRequest
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.CustomerDeleteOptionModel
import com.app.rupyz.sales.customer.CustomFormActivity
import com.app.rupyz.sales.customer.CustomerActionListener
import com.app.rupyz.sales.customer.CustomerDetailActivity
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.customer.ListOfAllCustomerAdapter
import com.app.rupyz.sales.customer.NewAddCustomerActivity
import com.app.rupyz.sales.customer.TransferCustomerActivity
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.payment.AddRecordPaymentActivity
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import java.util.Locale

class AssignedCustomerBeatFragment : BaseFragment(), CustomerActionListener {
    private lateinit var binding: FragmentAssignedCustomerBeatBinding
    private lateinit var beatViewModel: BeatViewModel
    private lateinit var customerViewModel: CustomerViewModel

    private lateinit var customerAdapter: ListOfAllCustomerAdapter
    private var customerList = ArrayList<CustomerData>()
    private var filteredCustomerList = ArrayList<CustomerData>()

    private var customerInActivePosition = -1
    private var customerID: Int? = null

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var isDeleteCustomerDialogShow = false

    private var customerInActiveModel: CustomerData? = null

    var delay: Long = 500 // 1 seconds after user stops typing
    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    companion object {
        var beatId = 0
        var beatDate = ""
        var customerLevel = ""
        fun getInstance(
            beatID: Int,
            beatDate: String,
            customerLevel: String
        ): AssignedCustomerBeatFragment {
            beatId = beatID
            this.beatDate = beatDate
            this.customerLevel = customerLevel
            return AssignedCustomerBeatFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignedCustomerBeatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]

        binding.etSearch.requestFocus()

        initRecyclerView()

        initObservers()

        loadCustomerList()

        binding.ivSearch.setOnClickListener {
            filteredCustomerList.clear()
            Utils.hideKeyboard(requireActivity())
            currentPage = 1
            isPageLoading = true
            isApiLastPage = false
            loadCustomerList()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filteredCustomerList.clear()
                currentPage = 1
                isPageLoading = true
                isApiLastPage = false
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
                    currentPage = 1
                    isPageLoading = true
                    isApiLastPage = false
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

            filteredCustomerList.clear()
            filteredCustomerList.addAll(customerList)

            customerAdapter.notifyDataSetChanged()

        }
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            isPageLoading = true
            isApiLastPage = false
            loadCustomerList()
            binding.clEmptyData.visibility = View.GONE
        }
    }

    private fun filterCustomerSearch(
        text: String,
        originalCustomerList: ArrayList<CustomerData>
    ) {

        if (originalCustomerList.isNotEmpty()) {
            //new array list that will hold the filtered data
            val tempCustomerList: ArrayList<CustomerData> = ArrayList()

            //looping through existing elements
            for (s in originalCustomerList) {
                //if the existing elements contains the search input
                if (s.name?.lowercase(Locale.getDefault())!!
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    //adding the element to filtered list
                    tempCustomerList.add(s)
                }
            }

            //calling a method of the adapter class and passing the filtered list
            filteredCustomerList.clear()
            filteredCustomerList.addAll(tempCustomerList)
            customerAdapter.notifyDataSetChanged()
        }

    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomerList.layoutManager = linearLayoutManager
        customerAdapter =
            ListOfAllCustomerAdapter(
                filteredCustomerList,
                this,
                requireActivity().supportFragmentManager,
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
            binding.progressBar.visibility = View.VISIBLE
        }
        beatViewModel.getCustomerList(
            beatId,
            binding.etSearch.text.toString(),
            customerLevel,
            beatDate,
            currentPage
        )
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        beatViewModel.customerListLiveData.observe(requireActivity()) { data ->
            if (isAdded) {
                binding.progressBar.visibility = View.GONE
                isPageLoading = false

                if (currentPage == 1) {
                    customerList.clear()
                    filteredCustomerList.clear()
                }

                data.beatRouteCustomerDataModel?.let { it ->
                    if (it.customersList.isNullOrEmpty().not()) {

                        customerList.addAll(it.customersList!!)
                        filteredCustomerList.addAll(it.customersList!!)

                        customerAdapter.notifyDataSetChanged()

                        if (it.customersList!!.size < 30) {
                            isApiLastPage = true
                        }
                    } else {
                        if (currentPage == 1) {
                            isApiLastPage = true
                            customerList.clear()
                            filteredCustomerList.clear()
                            customerAdapter.notifyDataSetChanged()
                        }
                    }


                    if (customerList.isEmpty()) {
                        binding.clEmptyData.visibility = View.VISIBLE
                    } else {
                        binding.clEmptyData.visibility = View.GONE
                    }
                }
            }
        }

        customerViewModel.updateCustomerLiveData().observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                customerList.clear()
                filteredCustomerList.clear()
                loadCustomerList()
            }

            Toast.makeText(requireContext(), "" + it.message, Toast.LENGTH_SHORT).show()
        }

        customerViewModel.getCheckIn().observe(requireActivity()) { data ->

            binding.progressBar.visibility = View.GONE
            if (data.error == false) {
                showToast(data.message)
                if (customerID != null) {
                    startActivity(
                        Intent(requireContext(), CustomerDetailActivity::class.java)
                            .putExtra(AppConstant.CUSTOMER_ID, customerID)
                            .putExtra(AppConstant.CUSTOMER_CHECKED_IN_STATUS, true)

                    )
                }

            } else {
                showToast(data.message)
            }
        }

        customerViewModel.customerDeleteLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                if (it.data != null) {
                    it.data.let { data ->
                        if (data.isUsed == true && data.childCount != null && data.childCount!! > 0) {
                            showDeleteDialog(AppConstant.TRANSFER_CUSTOMER, data.childCount)
                        } else {
                            if (isDeleteCustomerDialogShow.not()) {
                                showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
                            } else {
                                isDeleteCustomerDialogShow = false
                                if (customerInActivePosition != -1 && customerInActivePosition < customerList.size) {
                                    customerList.removeAt(customerInActivePosition)
                                    customerAdapter.notifyItemRemoved(customerInActivePosition)
                                    customerAdapter.notifyItemRangeChanged(
                                        customerInActivePosition,
                                        customerList.size
                                    )
                                    customerInActivePosition = -1
                                }
                            }
                        }
                    }
                }
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
                    .putExtra(AppConstant.CUSTOMER, model)
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
                ).putExtra(AppConstant.CUSTOMER_NAME, model.name)
                    .putExtra(AppConstant.CUSTOMER_ID, model.id)
            )
        } else {
            showToast(resources.getString(R.string.payment_permission))
        }
    }

    override fun onEdit(model: CustomerData, position: Int) {
        someActivityResultLauncher.launch(
            Intent(requireContext(), NewAddCustomerActivity::class.java)
                .putExtra(AppConstant.CUSTOMER_ID, model.id)
        )
    }

    override fun onInActiveCustomer(model: CustomerData, position: Int) {
        customerInActivePosition = position
        customerInActiveModel = model
        val customer = CustomerDeleteOptionModel()
        customer.checkChildren = true
        binding.progressBar.visibility = View.VISIBLE
        customerViewModel.inactiveCustomer(model.id!!, customer, hasInternetConnection())
    }

    private fun showDeleteDialog(action: String, count: Int?) {
        val fragment = DeleteDialogFragment.getInstance(
            action,
            object : DeleteDialogFragment.IDeleteDialogListener {
                override fun onDeleteButtonClick() {
                    super.onDeleteButtonClick()
                    if (action == AppConstant.TRANSFER_CUSTOMER) {
                        transferCustomerActivityResultLauncher.launch(
                            Intent(
                                requireContext(),
                                TransferCustomerActivity::class.java
                            )
                                .putExtra(
                                    AppConstant.CUSTOMER,
                                    customerInActiveModel
                                )
                        )
                    } else {
                        isDeleteCustomerDialogShow = true
                        val customer = CustomerDeleteOptionModel()
                        customer.checkChildren = false
                        customer.isCustomerDelete = true
                        binding.progressBar.visibility = View.VISIBLE
                        customerViewModel.inactiveCustomer(
                            customerInActiveModel?.id!!,
                            customer,
                            hasInternetConnection()
                        )
                    }
                }
            })

        if (action == AppConstant.TRANSFER_CUSTOMER) {
            val bundle = Bundle()
            bundle.putInt(AppConstant.ADD_COUNT, count ?: 0)
            bundle.putString(AppConstant.CUSTOMER_LEVEL, customerInActiveModel?.customerLevel)
            bundle.putString(AppConstant.CUSTOMER_NAME, customerInActiveModel?.name)
            fragment.arguments = bundle
        }

        fragment.show(childFragmentManager, DeleteDialogFragment::class.java.name)
    }


    var transferCustomerActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                showDeleteDialog(AppConstant.Delete.DELETE_CUSTOMER, null)
            }
        }

    override fun onGetCustomerInfo(model: CustomerData, isUserCheckedIn: Boolean) {
        startActivity(
            Intent(requireContext(), CustomerDetailActivity::class.java)
                .putExtra(AppConstant.CUSTOMER_ID, model.id)
                .putExtra(AppConstant.CUSTOMER_CHECKED_IN_STATUS, isUserCheckedIn)

        )
    }

    override fun onGetCheckInfo(model: CheckInRequest) {

        customerID = model.customer_id
        customerViewModel.getCheckInData(model, hasInternetConnection())
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


    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            customerList.clear()
            customerAdapter.notifyDataSetChanged()

            loadCustomerList()
        }
    }

    override fun getCustomerParentDetails(model: CustomerData, position: Int) {
        if (model.customerParent != null) {
            startActivity(
                Intent(requireContext(), CustomerDetailActivity::class.java)
                    .putExtra(AppConstant.CUSTOMER_ID, model.customerParent)
            )
        }
    }

    override fun viewCustomerLocation(model: CustomerData) {
        if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
            Utils.openMap(requireContext(), model.mapLocationLat, model.mapLocationLong, model.name)
        } else {
            showToast(resources.getString(R.string.no_location_found))
        }

    }
}
