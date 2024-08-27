package com.app.rupyz.sales.expense

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetPaymentRejectedCommentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExpenseRejectedBottomSheetDialogFragment(
    var listener: IExpenseRejectedListener
) : BottomSheetDialogFragment() {
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

        binding.buttonProceed.text = resources.getString(R.string.submit)

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            if (binding.etComment.text.toString().isEmpty()) {
                binding.etComment.error = "Please enter the reason for rejection!!"
            } else {
                listener.expenseRejected(binding.etComment.text.toString())
                dismiss()
            }
        }
    }

    interface IExpenseRejectedListener {
        fun expenseRejected(reason: String)
    }

}