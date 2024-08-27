package com.app.rupyz.sales.targets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.adapter.itemdecorator.DividerItemDecorator
import com.app.rupyz.databinding.ProductTargetsDetailsFragmentBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.ProductMetricsModel
import com.app.rupyz.model_kt.StaffCurrentlyActiveDataModel
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity

class ProductTargetDetailsFragment : BaseFragment(),
    ActiveTargetsListRvAdapter.ITargetProductActionListener {
    private lateinit var binding: ProductTargetsDetailsFragmentBinding
    private lateinit var targetProductsListRvAdapter: TargetProductsListRvAdapter
    private var productTargetList = ArrayList<ProductMetricsModel>()
    private var staffId: Int? = null
    var currentlyActiveTarget: StaffCurrentlyActiveDataModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProductTargetsDetailsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            staffId = arguments?.getInt(AppConstant.STAFF_ID)

            if (arguments?.get(AppConstant.TARGET_PRODUCTS) != null) {
                if (arguments?.get(AppConstant.TARGET_PRODUCTS_LIST) != null) {
                    currentlyActiveTarget =
                        arguments?.getParcelable(AppConstant.TARGET_PRODUCTS_LIST)
                }

                if (currentlyActiveTarget != null && currentlyActiveTarget?.productMetrics.isNullOrEmpty()
                        .not()
                ) {
                    productTargetList.addAll(currentlyActiveTarget?.productMetrics!!)
                }

                initTargetProductRecyclerView()

                binding.mainContent.visibility = View.VISIBLE
            }
        }
    }

    private fun initTargetProductRecyclerView() {
        binding.rvTarget.layoutManager = LinearLayoutManager(requireContext())
        targetProductsListRvAdapter = TargetProductsListRvAdapter(productTargetList)
        val itemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.item_divider_white
                )
            )
        binding.rvTarget.addItemDecoration(itemDecoration)
        binding.rvTarget.adapter = targetProductsListRvAdapter
    }

    override fun getTargetProductDetails() {
        startActivity(
            Intent(requireContext(), FragmentContainerActivity::class.java).putExtra(
                AppConstant.TARGET_PRODUCTS,
                true
            ).putExtra(AppConstant.TARGET_PRODUCTS_LIST, currentlyActiveTarget)
        )
    }
}