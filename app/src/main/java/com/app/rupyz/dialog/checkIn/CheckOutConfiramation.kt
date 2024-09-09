package com.app.rupyz.dialog.checkIn

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.app.rupyz.databinding.CheckoutConfiramationDialogBinding
import com.app.rupyz.generic.helper.hideView
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.sales.customer.CustomerDetailActivity


object CheckOutConfiramation {
    @JvmStatic
    fun showCheckOutConfirmationDialog(
        context: Context,
        customerName: String,
        titleMessage: String,
        customerID: Int,
        listener: ICheckOutConfirmationClickListener?,
        showCheckOut : Boolean
    ) {
        val dialog = Dialog(context)
        val binding: CheckoutConfiramationDialogBinding = CheckoutConfiramationDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        binding.tvTitle.text = titleMessage
        binding.ivClose.setOnClickListener { listener?.dismissed(); dialog.dismiss() }
        if (showCheckOut.not()){
            binding.btnApply.hideView()
        }
        binding.btnActivity.setOnClickListener {
            listener?.openActivity()
            dialog.dismiss()

        }
        binding.btnApply.setOnClickListener {
            listener?.onCheckOutConfirm(customerName, customerID)
            dialog.dismiss()
        }
        
        if (listener == null){
            binding.btnActivity.hideView()
            binding.btnApply.hideView()
        }
        
        dialog.show()
    }

}

interface ICheckOutConfirmationClickListener {
    fun onCheckOutConfirm(
        customerName: String,
        customerID: Int
    )
    fun openActivity()

    fun dismissed(){
    
    }

}
