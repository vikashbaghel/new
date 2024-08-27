package com.app.rupyz.sales.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetOrderStatusBinding
import com.app.rupyz.model_kt.OrderStatusModel
import com.app.rupyz.sales.home.OrderStatusActionListener
import com.app.rupyz.sales.orders.adapter.OrderStatusUpdateListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderStatusByBottomSheetDialogFragment : BottomSheetDialogFragment(),
    OrderStatusUpdateListAdapter.IOrderDispatchListener {
    private lateinit var binding: BottomSheetOrderStatusBinding
    private lateinit var statusFilterAdapter: OrderStatusUpdateListAdapter


    companion object {
        private var itemsData1: MutableList<OrderStatusModel>?=null
        private lateinit var listener: IStatusListener
        private var position: String?=null
        private lateinit var orderStatusActionListener: OrderStatusActionListener

        @JvmStatic
        fun newInstance(
            itemsData: MutableList<OrderStatusModel>,
            listener1: IStatusListener,
            orderStatusActionListener1: OrderStatusActionListener,
            position1: String,
        ): OrderStatusByBottomSheetDialogFragment {
            val fragment = OrderStatusByBottomSheetDialogFragment()
            this.itemsData1 = itemsData
            this.listener = listener1
            this.position= position1
            this.orderStatusActionListener = orderStatusActionListener1

            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetOrderStatusBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogNewTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (itemsData1!!.isNotEmpty())
        {
            initTabLayout()
        }



        binding.ivBack.setOnClickListener {
            dismiss()
        }

    }

    private fun initTabLayout() {
        statusFilterAdapter = OrderStatusUpdateListAdapter(itemsData1!!,this, position!! )
        binding.tvOrderStatusList.adapter = statusFilterAdapter
        statusFilterAdapter.notifyDataSetChanged()

    }

    interface IStatusListener {
        fun applyStatus(status: String,orderStatusActionListener1: OrderStatusActionListener,position: Int)
    }

    override fun onUpdateStatus(model: OrderStatusModel, position: Int) {
        listener.applyStatus(model.name!!, orderStatusActionListener,position)
        dismiss()
    }

}