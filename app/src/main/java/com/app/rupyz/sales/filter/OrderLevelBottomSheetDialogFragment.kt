package com.app.rupyz.sales.filter


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetAllLevelBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.customer.CustomerNameRvAdapter
import com.app.rupyz.sales.customer.CustomerViewModel
import com.app.rupyz.sales.orders.StatusFilterAdapter
import com.app.rupyz.sales.orders.adapter.AllLevelAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderLevelBottomSheetDialogFragment : BottomSheetDialogFragment(),
    AllLevelAdapter.CategoryListener,
    CustomerNameRvAdapter.ICustomerSelectListener,
    StatusFilterAdapter.StatusSelectListener {
    private lateinit var binding: BottomSheetAllLevelBinding
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var categoryListAdapter: AllLevelAdapter
    private var customerTypeList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var statusList: ArrayList<AllCategoryResponseModel> = ArrayList()
    private var customerParentId: Int? = null
    private var selectedValue: String? = null


    companion object {
        private lateinit var listener: IOrderFilterListener
        private var customerLevel = ""
        private var orderStatus = ""
        private var customerSelect: CustomerData? = null

        @JvmStatic
        fun newInstance(
            listener1: IOrderFilterListener,
            status: String,
            level: String,
            customer: CustomerData?

        ): OrderLevelBottomSheetDialogFragment {
            val fragment = OrderLevelBottomSheetDialogFragment()
            listener = listener1
            orderStatus = status
            customerLevel = level
            customerSelect = customer

            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetAllLevelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogNewTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]

        initRecyclerView()
        loadCustomerLevel()

        binding.ivBack.setOnClickListener {
            dismiss()
        }
        binding.buttonCancel.setOnClickListener {
            clearFilter()
        }

        binding.btnApply.setOnClickListener {
            listener.changeOrderFilter(
                orderStatus,
                customerLevel,
                customerSelect,
                selectedValue!!

            )
            dismiss()
        }
    }

    private fun loadCustomerLevel() {


        val model0 = AllCategoryResponseModel()
        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_0).isNullOrEmpty()) {

            model0.name = AppConstant.CUSTOMER_LEVEL_ALL
            selectedValue = resources.getString(R.string.all_order)
        }
        if (customerLevel.isEmpty()) {
            model0.isSelected = true
        }
        customerTypeList.add(0, model0)

        val model1 = AllCategoryResponseModel()
        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1).isNullOrEmpty()
                .not()
        ) {
            model1.name = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)

        }
        if (customerLevel.isEmpty().not() && customerLevel == AppConstant.CUSTOMER_LEVEL_1) {
            model1.isSelected = true
            selectedValue = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
        }

        customerTypeList.add(1, model1)
        val model2 = AllCategoryResponseModel()
        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2).isNullOrEmpty()
                .not()
        ) {
            model2.name = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)

        }

        if (customerLevel.isEmpty()
                .not() && customerLevel == AppConstant.CUSTOMER_LEVEL_2
        ) {
            model2.isSelected = true
            selectedValue = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
        }
        customerTypeList.add(2, model2)


        val model3 = AllCategoryResponseModel()
        if (SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3).isNullOrEmpty()
                .not()
        ) {
            model3.name = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)

        }
        if (customerLevel.isEmpty()
                .not() && customerLevel == AppConstant.CUSTOMER_LEVEL_3
        ) {
            model3.isSelected = true
            selectedValue = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
        }

        customerTypeList.add(3, model3)

        categoryListAdapter.notifyDataSetChanged()
    }

    private fun clearFilter() {
        orderStatus = ""
        customerLevel = ""
        customerSelect = null
        selectedValue = resources.getString(R.string.all_order)
        listener.changeOrderFilter(
            orderStatus,
            customerLevel,
            customerSelect,
            selectedValue!!
        )
        dismiss()
    }

    private fun initRecyclerView() {
        binding.rvCustomerType.setHasFixedSize(true)
        binding.rvCustomerType.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        categoryListAdapter = AllLevelAdapter(customerTypeList, this)
        binding.rvCustomerType.adapter = categoryListAdapter

    }


    val hasInternetConnection: Boolean
        get() {
            return Connectivity.hasInternetConnection(requireContext())
        }


    override fun onCategorySelect(model: AllCategoryResponseModel, position: Int) {
        if (model.isSelected.not()) {
            for (i in customerTypeList.indices) {
                customerTypeList[i].isSelected = false
            }
            customerLevel = customerTypeList[position].name.toString()
            selectedValue = customerTypeList[position].name.toString()
            customerTypeList[position].isSelected = true
            binding.rvCustomerType.post { categoryListAdapter.notifyDataSetChanged() }

            when (position) {
                0 -> customerLevel = ""
                1 -> customerLevel = AppConstant.CUSTOMER_LEVEL_1
                2 -> customerLevel = AppConstant.CUSTOMER_LEVEL_2
                3 -> customerLevel = AppConstant.CUSTOMER_LEVEL_3
            }

        }
    }

    override fun onCustomerSelect(model: CustomerData, position: Int) {

        customerParentId = model.id
        customerSelect = model

    }

    override fun onStatusSelect(model: AllCategoryResponseModel, position: Int) {
        if (model.isSelected.not()) {
            for (i in statusList.indices) {
                statusList[i].isSelected = false
            }


            orderStatus = if (position == 0) {
                ""
            } else {
                statusList[position].name.toString()
            }
            statusList[position].isSelected = true

        }
    }

    interface IOrderFilterListener {
        fun changeOrderFilter(
            orderStatus: String,
            customerLevel: String,
            customerSelect: CustomerData?,
            selectedValue: String
        )
    }

}