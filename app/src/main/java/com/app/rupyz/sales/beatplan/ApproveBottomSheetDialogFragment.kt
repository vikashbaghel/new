package com.app.rupyz.sales.beatplan

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetApproveViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ApproveBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetApproveViewBinding
    companion object {
        var listener: IApproveActionListener? = null
        var screen: String = ""
        fun getInstance(
            listener: IApproveActionListener,
            screen: String
        ): ApproveBottomSheetDialogFragment {
            this.listener = listener
            this.screen = screen
            return ApproveBottomSheetDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetApproveViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (screen == AllDailyBeatPlanListFragment::class.java.name) {
            binding.buttonProceed.text = resources.getString(R.string.submit)
            binding.hdConformation.text = resources.getString(R.string.are_you_sure_for_beat_approve)
            binding.hdReport.text = resources.getString(R.string.approve_beat_plan)
        } else {
            binding.buttonProceed.text = resources.getString(R.string.yes)
            binding.hdReport.text = resources.getString(R.string.approve_payment)
            binding.hdConformation.visibility = View.VISIBLE
        }

        binding.buttonCancel.setOnClickListener {
            listener?.onCancelApproval()
            dismiss()
        }

        binding.ivBack.setOnClickListener {
            listener?.onCancelApproval()
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            listener?.approvalConformation(binding.etComment.text.toString())
            dismiss()
        }
    }

    interface IApproveActionListener {
        fun approvalConformation(reason: String)
        fun onCancelApproval(){}
    }

}