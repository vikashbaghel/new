package com.app.rupyz.sales.payment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetPaymentRejectedCommentBinding
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentRejectedBottomSheetDialogFragment(
    var model: RecordPaymentData,
    var listener: IPaymentRejectedListener
) :
    BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetPaymentRejectedCommentBinding

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

        binding.buttonCancel.setOnClickListener {
            listener.onDismissDialog(model)
            dismiss()
        }

        binding.ivBack.setOnClickListener {
            listener.onDismissDialog(model)
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            if (binding.etComment.text.toString().isEmpty()) {
                binding.etComment.error = "Please enter the reason for dishonour!!"
            } else {
                model.rejectReason = binding.etComment.text.toString()
                listener.commentOfPaymentRejected(model)
                dismiss()
            }
        }
    }

    interface IPaymentRejectedListener {
        fun commentOfPaymentRejected(model: RecordPaymentData)
        fun onDismissDialog(model: RecordPaymentData)
    }

}