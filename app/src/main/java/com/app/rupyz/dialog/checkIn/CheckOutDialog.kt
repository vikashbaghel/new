package com.app.rupyz.dialog.checkIn

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.databinding.CheckoutDialogBinding


object CheckOutDialog {
    @JvmStatic
    fun showCheckOutDialog(
        context: Context,
        customerName: String,
        customerID: Int,
        listener: ICheckOutConClickListener
    ) {
        val dialog = Dialog(context)
        val binding: CheckoutDialogBinding =
            CheckoutDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        binding.tvTitle.text = customerName
        binding.tvSubTitle.text = buildString {
            append(MyApplication.instance.getString(R.string.check_out_msg))
            append(" ")
            append(customerName)
        }

        binding.ivClose.setOnClickListener { dialog.dismiss() }
        binding.btnNo.setOnClickListener { dialog.dismiss() }

        binding.btnApply.setOnClickListener {
            listener.onCheckoutConfirm(customerName, customerID)
            dialog.dismiss()
        }
        dialog.show()
    }


}

interface ICheckOutConClickListener {
    fun onCheckoutConfirm(
        customerName: String,
        customerID: Int
    )


}