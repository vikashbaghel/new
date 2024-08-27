package com.app.rupyz.dialog.checkIn

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.databinding.CheckedInDialogBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.gone
import com.app.rupyz.generic.helper.visibility
import com.app.rupyz.model_kt.CheckInRequest
import com.app.rupyz.model_kt.CheckoutRequest


class CheckedInDialogFragment : DialogFragment() {
    private lateinit var binding: CheckedInDialogBinding


    private val checkOutViewModel: CheckOutViewModel by viewModels()


    companion object {
        var customerId: Int? = null
        var customerName: String? = null
        var listener: ICheckInClickListener? = null
        fun getInstance(
            customerId: Int,
            customerName: String,
            listener: ICheckInClickListener? = null
            ): CheckedInDialogFragment {
            this.customerName = customerName
            this.customerId = customerId
            this.listener = listener
            return CheckedInDialogFragment()
        }
    }
    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CheckedInDialogBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        binding.tvTitle.text = buildString {

            append(customerName)
            append("\n")
            append(MyApplication.instance.getString(R.string.checked_in_msg))
        }
        binding.ivClose.setOnClickListener { dialog?.dismiss() }

        binding.btnActivity.setOnClickListener {
            dialog?.dismiss()

        }
        binding.btnApply.setOnClickListener {
            binding.progressBarMain.visibility()
            val checkoutRequest = CheckoutRequest(customerId)
            checkOutViewModel.getCheckOutData(
                checkoutRequest,
                true
            )
        }


        binding.ivClose.setOnClickListener {
            dismiss()
        }

    }

    private fun initObservers() {
        checkOutViewModel.getCheckOut().observe(this) { data ->
            binding.progressBarMain.gone()
            if (data.error == false) {
                listener?.onConfirm(CheckInRequest(customer_id =  customerId))
                Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
            } else {
                if (data.errorCode != null && data.errorCode == 403) {
                
                } else {
                    Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
                }
            }
            dialog?.dismiss()
            
        }

    }

}


