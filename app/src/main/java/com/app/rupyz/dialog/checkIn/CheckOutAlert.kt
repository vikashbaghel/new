package com.app.rupyz.dialog.checkIn

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.app.rupyz.databinding.CheckoutAlertDialogBinding


object CheckOutAlert {
    @JvmStatic
    fun showCheckOutAlertDialog(
        context: Context,
        customerName: String
    ) {
        val dialog = Dialog(context)
        val binding: CheckoutAlertDialogBinding =
            CheckoutAlertDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        binding.tvTitle.text = customerName

        binding.ivClose.setOnClickListener { dialog.dismiss() }

        binding.btnApply.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}