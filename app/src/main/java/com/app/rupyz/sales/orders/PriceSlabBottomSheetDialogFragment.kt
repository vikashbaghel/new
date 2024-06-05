package com.app.rupyz.sales.orders

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetPriceSlabInfoBinding
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PriceSlabBottomSheetDialogFragment(
    var model: ProductList
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetPriceSlabInfoBinding
    private lateinit var adapter: ProductPriceSlabListAdapter
    private var slabList = ArrayList<TelescopicPricingModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetPriceSlabInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (model.telescopePricing.isNotEmpty()){
            slabList.addAll(model.telescopePricing)
        }
        initRecyclerView()

        binding.ivBack.setOnClickListener {
            dismiss()
        }

    }

    private fun initRecyclerView() {
        binding.rvSlab.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductPriceSlabListAdapter(slabList)
        binding.rvSlab.adapter = adapter
    }
}