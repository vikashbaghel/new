package com.app.rupyz.sales.lead

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetPaymentRejectedCommentBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.LeadLisDataItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LeadApproveOrRejectedBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetPaymentRejectedCommentBinding
    companion object {
        var model: LeadLisDataItem? = null
        var listener: ILeadBottomSheetActionListener? = null
        var action: String = ""
        fun getInstance(
            action: String,
            model: LeadLisDataItem,
            listener: ILeadBottomSheetActionListener
        ): LeadApproveOrRejectedBottomSheetDialog {
            this.action = action
            this.model = model
            this.listener = listener
            return LeadApproveOrRejectedBottomSheetDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetPaymentRejectedCommentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (action == AppConstant.STATUS_APPROVED){
            binding.hdReport.text = resources.getString(R.string.approve_lead)
            binding.buttonProceed.text = resources.getString(R.string.approve)
        } else {
            binding.hdReport.text = resources.getString(R.string.reject_lead)
            binding.buttonProceed.text = resources.getString(R.string.reject)
        }

        binding.buttonCancel.setOnClickListener {
            listener?.onDismissDialog(model)
            dismiss()
        }

        binding.ivBack.setOnClickListener {
            listener?.onDismissDialog(model)
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            if (action == AppConstant.STATUS_APPROVED) {
                listener?.approveCommentOfLead(binding.etComment.text.toString())
            } else if (action == AppConstant.STATUS_DISHONOUR) {
                listener?.rejectedCommentOfLead(binding.etComment.text.toString())
            }
            dismiss()
        }
    }

    interface ILeadBottomSheetActionListener {
        fun rejectedCommentOfLead(reason: String)
        fun approveCommentOfLead(reason: String)
        fun onDismissDialog(model: LeadLisDataItem?){}
    }

}