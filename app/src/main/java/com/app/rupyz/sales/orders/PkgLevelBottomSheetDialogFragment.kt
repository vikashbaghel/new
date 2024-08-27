package com.app.rupyz.sales.orders

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetPackagingLevelInfoBinding
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PkgLevelBottomSheetDialogFragment(
        var model: ProductList
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetPackagingLevelInfoBinding
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetPackagingLevelInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stringBuilder = StringBuilder()
        if (model.packaging_level.isNotEmpty()) {
            model.packaging_level.forEach { level ->
                stringBuilder.append(CalculatorHelper().calculateQuantity(level.size)).append(" \u0078 ").append(model.unit).append("  =  ")
                        .append(level.unit).append("\n")
            }
            binding.tvPackagingLevel.text = stringBuilder.toString()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }

    }
}