package com.app.rupyz.generic.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.app.rupyz.R
import com.app.rupyz.databinding.CustomDeleteDialogBinding

object DeleteDialog {

    @JvmStatic
    fun showDeleteDialog(
        context: Context,
        model: Any?,
        position: Any,
        textHeading: String,
        textTitle: String,
        listener: IOnClickListener
    ) {
        val dialog = Dialog(context)
        val binding: CustomDeleteDialogBinding =
            CustomDeleteDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        binding.tvHeading.text = textHeading
        binding.tvTitle.text = textTitle
        binding.ivClose.setOnClickListener { dialog.dismiss() }
        binding.tvCancel.setOnClickListener { dialog.dismiss() }

        binding.tvDelete.setOnClickListener {
            listener.onDelete(model!!, position)
            dialog.dismiss()
        }

        dialog.show()
    }




    interface IOnClickListener {
        fun onDelete(model: Any, position: Any)
    }
}