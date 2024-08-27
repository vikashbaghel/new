package com.app.rupyz.sales.filter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetOrderSortByBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.order.order_history.SortByData
import com.app.rupyz.sales.orders.adapter.SortByAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderListSortByBottomSheetDialogFragment : BottomSheetDialogFragment(),
    SortByAdapter.SortByListener {
    private lateinit var binding: BottomSheetOrderSortByBinding
    private lateinit var sortByAdapter: SortByAdapter
    private var sortByList: ArrayList<SortByData> = ArrayList()

    companion object {
        private lateinit var listener: ISortingOrderListener
        private var sortingBy: String = ""
        private var sortingOrder: String = ""
        private var sortingSelected: Boolean = false
        private var sortingPosition: Int? = null

        @JvmStatic
        fun newInstance(
            listener1: ISortingOrderListener,
            sortingSelected1: Boolean,
            sortingOrder1: String,
            sortingPosition1: Int
        ): OrderListSortByBottomSheetDialogFragment {
            val fragment = OrderListSortByBottomSheetDialogFragment()
            listener = listener1
            sortingSelected = sortingSelected1
            sortingPosition = sortingPosition1
            sortingOrder = sortingOrder1
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetOrderSortByBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sortingPosition == -1) {
            listData1()
        } else {
            listData()
        }

        initRecyclerView()

        binding.buttonProceed.setOnClickListener {
            listener.applySorting(sortingBy, sortingOrder, sortingSelected, sortingPosition!!)
            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            sortingOrder = AppConstant.SORTING_LEVEL_DESCENDING
            sortingBy = AppConstant.CREATED_AT
            sortingPosition = -1
            listener.applySorting(sortingBy, sortingOrder, false, sortingPosition!!)
            dismiss()
        }
        binding.ivBack.setOnClickListener {
            dismiss()
        }
    }

    private fun initRecyclerView() {
        sortByAdapter = SortByAdapter(
            sortByList, this, sortingSelected, sortingPosition!!, sortingOrder
        )
        binding.rvSort.adapter = sortByAdapter
    }

    private fun listData() {
        sortByList.add(
            SortByData(
                resources.getString(R.string.order_value),
                AppConstant.TOTAL_AMOUNT,
                R.drawable.order_approve,
                false
            )
        )
        sortByList.add(
            SortByData(
                resources.getString(R.string.create_date),
                AppConstant.CREATED_AT,
                R.drawable.event_repeat,
                false
            )
        )
        sortByList.add(
            SortByData(
                resources.getString(R.string.updated_date),
                AppConstant.UPDATE_AT,
                R.drawable.edit_calendar,
                false
            )
        )
        sortByList.add(
            SortByData(
                resources.getString(R.string.delivery_date_1),
                AppConstant.DELIVERY_AT,
                R.drawable.local_shipping,
                false
            )
        )

        if (sortingPosition != -1) {
            sortingSelected = true
            sortByList[sortingPosition ?: 0].isSelected = true
            initRecyclerView()
        }
    }

    private fun listData1() {
        sortByList.add(
            SortByData(
                resources.getString(R.string.order_value),
                AppConstant.TOTAL_AMOUNT,
                R.drawable.order_approve,
                false
            )
        )
        sortByList.add(
            SortByData(
                resources.getString(R.string.create_date),
                AppConstant.CREATED_AT,
                R.drawable.event_repeat,
                true
            )
        )
        sortByList.add(
            SortByData(
                resources.getString(R.string.updated_date),
                AppConstant.UPDATE_AT,
                R.drawable.edit_calendar,
                false
            )
        )
        sortByList.add(
            SortByData(
                resources.getString(R.string.delivery_date_1),
                AppConstant.DELIVERY_AT,
                R.drawable.local_shipping,
                false
            )
        )
    }

    interface ISortingOrderListener {
        fun applySorting(sortBy: String, sortOrder: String, sortingSelected: Boolean, position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSortBySelect(
        model: SortByData,
        sortByOrder: String,
        selectedPosition: Int,
        select: Boolean
    ) {
        sortingOrder = sortByOrder
        sortingBy = model.nameValue
        sortingPosition = selectedPosition
        sortingSelected = true

        sortByList.forEach { it.isSelected = false }
        sortByList[selectedPosition].isSelected = true
        initRecyclerView()

    }

}