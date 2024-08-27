package com.app.rupyz.sales.staffactivitytrcker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetSortStaffActivityBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.StaffActivitySortingModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StaffActivitySortByBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetSortStaffActivityBinding

    companion object {
        private lateinit var listener: ISortByStaffActivityListener
        private var sortingOrder: StaffActivitySortingModel? = null

        @JvmStatic
        fun newInstance(
            listener1: ISortByStaffActivityListener,
            sortByOrder: StaffActivitySortingModel?
        ): StaffActivitySortByBottomSheetDialogFragment {
            val fragment = StaffActivitySortByBottomSheetDialogFragment()
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
        binding = BottomSheetSortStaffActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sortingOrder != null) {
            if (sortingOrder?.tcFilter != null) {
                sortingOrder?.tcFilter?.let { tcFilter ->
                    if (tcFilter.second.isNullOrEmpty().not()
                        && tcFilter.second == AppConstant.SORTING_LEVEL_ASCENDING
                    ) {
                        binding.ivTopTcArrow.setImageResource(R.drawable.ic_arrow_up_enable)
                        binding.ivDownTcArrow.setImageResource(R.drawable.ic_arrow_down_disable)
                    } else {
                        binding.ivTopTcArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                        binding.ivDownTcArrow.setImageResource(R.drawable.ic_arrow_down_enable)
                    }
                }
            } else {
                sortingOrder?.tcFilter = Pair("tc_count", "")
            }

            if (sortingOrder?.pcFilter != null) {
                sortingOrder?.pcFilter?.let { pcFilter ->
                    if (pcFilter.second.isNullOrEmpty().not()
                        && pcFilter.second == AppConstant.SORTING_LEVEL_ASCENDING
                    ) {
                        binding.ivTopPcArrow.setImageResource(R.drawable.ic_arrow_up_enable)
                        binding.ivDownPcArrow.setImageResource(R.drawable.ic_arrow_down_disable)
                    } else {
                        binding.ivTopPcArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                        binding.ivDownPcArrow.setImageResource(R.drawable.ic_arrow_down_enable)
                    }
                }
            } else {
                sortingOrder?.pcFilter = Pair("pc_count", "")
            }


            if (sortingOrder?.orderValueFilter != null) {
                sortingOrder?.orderValueFilter?.let { orderValueFilter ->
                    if (orderValueFilter.second.isNullOrEmpty().not()
                        && orderValueFilter.second == AppConstant.SORTING_LEVEL_ASCENDING
                    ) {
                        binding.ivTopOrderValueArrow.setImageResource(R.drawable.ic_arrow_up_enable)
                        binding.ivDownOrderValueArrow.setImageResource(R.drawable.ic_arrow_down_disable)
                    } else {
                        binding.ivTopOrderValueArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                        binding.ivDownOrderValueArrow.setImageResource(R.drawable.ic_arrow_down_enable)
                    }
                }
            } else {
                sortingOrder?.orderValueFilter = Pair("order_value", "")
            }


            if (sortingOrder?.durationFilter != null) {
                sortingOrder?.durationFilter?.let { durationFilter ->
                    if (durationFilter.second.isNullOrEmpty().not()
                        && durationFilter.second == AppConstant.SORTING_LEVEL_ASCENDING
                    ) {
                        binding.ivTopDurationArrow.setImageResource(R.drawable.ic_arrow_up_enable)
                        binding.ivDownDurationArrow.setImageResource(R.drawable.ic_arrow_down_disable)
                    } else {
                        binding.ivTopDurationArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                        binding.ivDownDurationArrow.setImageResource(R.drawable.ic_arrow_down_enable)
                    }
                }
            } else {
                sortingOrder?.durationFilter = Pair("duration", "")
            }
        } else {
            sortingOrder = StaffActivitySortingModel()
        }

        binding.clTc.setOnClickListener {
            when (sortingOrder?.tcFilter?.second) {
                null, "" -> {
                    binding.ivTopTcArrow.setImageResource(R.drawable.ic_arrow_up_enable)
                    binding.ivDownTcArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                    sortingOrder?.tcFilter = Pair("tc_count", AppConstant.SORTING_LEVEL_ASCENDING)
                }

                AppConstant.SORTING_LEVEL_ASCENDING -> {
                    binding.ivTopTcArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                    binding.ivDownTcArrow.setImageResource(R.drawable.ic_arrow_down_enable)

                    sortingOrder?.tcFilter = Pair("tc_count", AppConstant.SORTING_LEVEL_DESCENDING)
                }

                AppConstant.SORTING_LEVEL_DESCENDING -> {
                    binding.ivTopTcArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                    binding.ivDownTcArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                    sortingOrder?.tcFilter = Pair("tc_count", "")
                }
            }
        }

        binding.clPc.setOnClickListener {

            when (sortingOrder?.pcFilter?.second) {
                null, "" -> {
                    binding.ivTopPcArrow.setImageResource(R.drawable.ic_arrow_up_enable)
                    binding.ivDownPcArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                    sortingOrder?.pcFilter = Pair("pc_count", AppConstant.SORTING_LEVEL_ASCENDING)
                }

                AppConstant.SORTING_LEVEL_ASCENDING -> {
                    binding.ivTopPcArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                    binding.ivDownPcArrow.setImageResource(R.drawable.ic_arrow_down_enable)

                    sortingOrder?.pcFilter = Pair("pc_count", AppConstant.SORTING_LEVEL_DESCENDING)
                }

                AppConstant.SORTING_LEVEL_DESCENDING -> {
                    binding.ivTopPcArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                    binding.ivDownPcArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                    sortingOrder?.pcFilter = Pair("pc_count", "")
                }
            }
        }

        binding.clOrderValue.setOnClickListener {

            when (sortingOrder?.orderValueFilter?.second) {
                null, "" -> {
                    binding.ivTopOrderValueArrow.setImageResource(R.drawable.ic_arrow_up_enable)
                    binding.ivDownOrderValueArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                    sortingOrder?.orderValueFilter = Pair("order_value", AppConstant.SORTING_LEVEL_ASCENDING)
                }

                AppConstant.SORTING_LEVEL_ASCENDING -> {
                    binding.ivTopOrderValueArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                    binding.ivDownOrderValueArrow.setImageResource(R.drawable.ic_arrow_down_enable)

                    sortingOrder?.orderValueFilter = Pair("order_value", AppConstant.SORTING_LEVEL_DESCENDING)
                }

                AppConstant.SORTING_LEVEL_DESCENDING -> {
                    binding.ivTopOrderValueArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                    binding.ivDownOrderValueArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                    sortingOrder?.orderValueFilter = Pair("order_value", "")
                }
            }
        }

        binding.clDuration.setOnClickListener {

            when (sortingOrder?.durationFilter?.second) {
                null, "" -> {
                    binding.ivTopDurationArrow.setImageResource(R.drawable.ic_arrow_up_enable)
                    binding.ivDownDurationArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                    sortingOrder?.durationFilter = Pair("duration", AppConstant.SORTING_LEVEL_ASCENDING)
                }

                AppConstant.SORTING_LEVEL_ASCENDING -> {
                    binding.ivTopDurationArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                    binding.ivDownDurationArrow.setImageResource(R.drawable.ic_arrow_down_enable)

                    sortingOrder?.durationFilter = Pair("duration", AppConstant.SORTING_LEVEL_DESCENDING)
                }

                AppConstant.SORTING_LEVEL_DESCENDING -> {
                    binding.ivTopDurationArrow.setImageResource(R.drawable.ic_arrow_up_disable)
                    binding.ivDownDurationArrow.setImageResource(R.drawable.ic_arrow_down_disable)

                    sortingOrder?.durationFilter = Pair("duration", "")
                }
            }
        }

        binding.buttonProceed.setOnClickListener {
            listener.applySorting(sortingOrder)
            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            listener.applySorting(null)
            dismiss()
        }
        binding.ivBack.setOnClickListener {
            dismiss()
        }
    }

    interface ISortByStaffActivityListener {
        fun applySorting(sorting: StaffActivitySortingModel?)
    }

}