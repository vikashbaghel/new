package com.app.rupyz.sales.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetGallerySortByBinding
import com.app.rupyz.databinding.BottomSheetSortCustomerBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.sales.beatplan.SortByBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GallerySortByBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetGallerySortByBinding

    companion object {
        private lateinit var listener: ISortingGalleryListener
        private var sortingDateOrder: String = ""
        //private var sortingSizeOrder: String = ""

        @JvmStatic
        fun newInstance(
                listener1: ISortingGalleryListener,
                sortByDate: String,
        ): GallerySortByBottomSheetDialogFragment {
            val fragment = GallerySortByBottomSheetDialogFragment()
            this.sortingDateOrder = sortByDate
            listener = listener1
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetGallerySortByBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sortingDateOrder.isEmpty()) {
            sortingDateOrder = AppConstant.SORTING_LEVEL_ASCENDING
        }

        if (sortingDateOrder.isEmpty() || sortingDateOrder == AppConstant.SORTING_LEVEL_ASCENDING) {
            binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_enable)
            binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_disable)
        } else {
            binding.ivTopArrow.setImageResource(R.drawable.ic_arrow_up_disable)
            binding.ivDownArrow.setImageResource(R.drawable.ic_arrow_down_enable)
        }

        binding.clSortCustomer.setOnClickListener {
            sortingDateOrder = when (sortingDateOrder) {
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
            listener.applySorting(sortingDateOrder)
            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            sortingDateOrder = ""
            listener.applySorting(sortingDateOrder)
            dismiss()
        }
        binding.ivBack.setOnClickListener {
            dismiss()
        }
    }

    interface ISortingGalleryListener {
        fun applySorting(dateOrder: String)
    }

}