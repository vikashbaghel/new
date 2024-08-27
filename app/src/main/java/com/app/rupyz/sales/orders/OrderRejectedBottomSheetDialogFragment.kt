package com.app.rupyz.sales.orders

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetPaymentRejectedCommentBinding
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderRejectedBottomSheetDialogFragment(
    var model: OrderData,
    var listener: IOrderRejectedListener
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
            if (binding.etComment.text.toString().isEmpty()){
                binding.etComment.error = "Please enter the reason for rejection!!"
            } else{
                model.rejectReason = binding.etComment.text.toString()
                listener.commentOfOrderRejected(model)
                dismiss()
            }
        }
    }

    interface IOrderRejectedListener {
        fun commentOfOrderRejected(model: OrderData)
        fun onDismissDialog(model: OrderData)
    }

}