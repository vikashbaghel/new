package com.app.rupyz.generic.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.app.rupyz.R
import com.app.rupyz.databinding.CustomDeleteDialogBinding
import com.app.rupyz.databinding.CustomInformationDialogBinding

object InformationDialog {

    @JvmStatic
    fun showDialog(
        context: Context,
        textHeading: String,
        textTitle: String,
    ) {
        val dialog = Dialog(context)
        val binding =
            CustomInformationDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        binding.tvHeading.text = textHeading
        binding.tvTitle.text = textTitle
        binding.ivClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}