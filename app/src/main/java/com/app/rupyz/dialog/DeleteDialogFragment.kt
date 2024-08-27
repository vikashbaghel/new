package com.app.rupyz.dialog

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.rupyz.R
import com.app.rupyz.databinding.CustomDeleteDialogBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref

class DeleteDialogFragment : DialogFragment() {
    private lateinit var binding: CustomDeleteDialogBinding

    companion object {
        var listener: IDeleteDialogListener? = null
        var action: String = ""
        fun getInstance(action: String, listener: IDeleteDialogListener): DeleteDialogFragment {
            this.listener = listener
            this.action = action
            return DeleteDialogFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CustomDeleteDialogBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val transferChildCount = arguments?.getInt(AppConstant.ADD_COUNT)
        val customerLevel = arguments?.getString(AppConstant.CUSTOMER_LEVEL)
        val customerName = arguments?.getString(AppConstant.CUSTOMER_NAME)

        when (action) {
            AppConstant.Delete.DELETE_CUSTOMER -> {
                binding.tvHeading.text = resources.getString(R.string.delete_customer)
                binding.tvTitle.text = resources.getString(R.string.delete_customer_message)
            }
            AppConstant.TRANSFER_CUSTOMER -> {
                var transferLevel = ""
                var parentCustomerLevel = ""
                if (customerLevel == AppConstant.CUSTOMER_LEVEL_1) {
                    parentCustomerLevel = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                    transferLevel = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                } else if (customerLevel == AppConstant.CUSTOMER_LEVEL_2) {
                    parentCustomerLevel = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                    transferLevel = SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
                }
                binding.tvHeading.text = resources.getString(R.string.transfer_customer_count,
                        "$transferChildCount", transferLevel)

                val transferMessage = getString(R.string.transfer_customer_message,
                        "$transferChildCount", transferLevel, customerName, parentCustomerLevel)

                // Apply bold style to specific parts of the string
                val spannableStringBuilder = SpannableStringBuilder(transferMessage)
               spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD),
                        transferMessage.indexOf(customerName!!),
                       transferMessage.indexOf(customerName) + customerName.length,
                       Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD),
                        transferMessage.indexOf(parentCustomerLevel),
                        transferMessage.indexOf(parentCustomerLevel) + parentCustomerLevel.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                // Set the formatted text to your TextView
                binding.tvTitle.text = spannableStringBuilder
                binding.tvDelete.text = resources.getString(R.string.transfer)

            }
        }

        binding.tvDelete.setOnClickListener {
            listener?.onDeleteButtonClick()
            dismiss()
        }

        binding.ivClose.setOnClickListener {
            listener?.onCancelButtonClick()
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            listener?.onCancelButtonClick()
            dismiss()
        }

    }

    interface IDeleteDialogListener {
        fun onDeleteButtonClick() {}
        fun onCancelButtonClick() {}
    }
}