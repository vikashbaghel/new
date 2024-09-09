package com.app.rupyz.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.rupyz.R
import com.app.rupyz.databinding.MockLocationDialogLayoutBinding

class MockLocationDetectedDialogFragment : DialogFragment() {
    private lateinit var binding: MockLocationDialogLayoutBinding
    companion object {
        var listener: IMockLocationActionListener? = null

        fun getInstance(listener: IMockLocationActionListener?): MockLocationDetectedDialogFragment {
            this.listener = listener
            return MockLocationDetectedDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = MockLocationDialogLayoutBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener {
            listener?.onDismissDialogForMockLocation()
            dismiss()
        }

        binding.btnContinue.setOnClickListener {
            context?.startActivity(Intent(Settings.ACTION_SETTINGS))
            listener?.onDismissDialogForMockLocation()
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            listener?.onDismissDialogForMockLocation()
            dismiss()
        }
    }

    interface IMockLocationActionListener {
        fun onDismissDialogForMockLocation() {}
    }
}