package com.app.rupyz.sales.beatplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetSortCustomerBinding
import com.app.rupyz.generic.utils.AppConstant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortByBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetSortCustomerBinding

    companion object {
        private lateinit var listener: ISortByCustomerListener
        private var sortingOrder: String = ""

        @JvmStatic
        fun newInstance(
            listener1: ISortByCustomerListener,
            sortByOrder: String
        ): SortByBottomSheetDialogFragment {
            val fragment = SortByBottomSheetDialogFragment()
            this.sortingOrder = sortByOrder
            listener = listener1
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetSortCustomerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sortingOrder.isEmpty()) {
            sortingOrder = AppConstant.SORTING_LEVEL_ASCENDING
        }

        if (sortingOrder.isEmpty() || sortingOrder == AppConstant.SORTING_LEVEL_ASCENDING) {
            binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_enable)
            binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_disable)
        } else {
            binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_disable)
            binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_enable)
        }

        binding.clSortCustomer.setOnClickListener {
            sortingOrder = when (sortingOrder) {
                AppConstant.SORTING_LEVEL_ASCENDING -> {
                    binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_disable)

                    binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_enable)

                    AppConstant.SORTING_LEVEL_DESCENDING
                }

                AppConstant.SORTING_LEVEL_DESCENDING -> {
                    binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_enable)

                    binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                    AppConstant.SORTING_LEVEL_ASCENDING
                }

                else -> {
                    AppConstant.SORTING_LEVEL_ASCENDING
                }
            }
        }

        binding.buttonProceed.setOnClickListener {
            listener.applySortByName(sortingOrder)
            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            sortingOrder = ""
            listener.applySortByName(sortingOrder)
            dismiss()
        }
        binding.ivBack.setOnClickListener {
            dismiss()
        }
    }

    interface ISortByCustomerListener {
        fun applySortByName(order: String)
    }

}