package com.app.rupyz.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.rupyz.databinding.GeoLocationWarningDialogLayoutBinding

class GeoFencingWarningDialogFragment : DialogFragment() {
    private lateinit var binding: GeoLocationWarningDialogLayoutBinding
    companion object {
        var listener: IGeoFencingActionListener? = null

        fun getInstance(listener: IGeoFencingActionListener): GeoFencingWarningDialogFragment {
            this.listener = listener
            return GeoFencingWarningDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = GeoLocationWarningDialogLayoutBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener {
            listener?.onDismissDialogForGeoFencing()
            dismiss()
        }

    }

    interface IGeoFencingActionListener {
        fun onDismissDialogForGeoFencing(){}
    }
}