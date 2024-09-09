package com.app.rupyz.sales.beatplan

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetCancelBeatDayBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class CancelBeatDayBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetCancelBeatDayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetCancelBeatDayBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        var listener: ICancelBeatDayListener? = null
        var date: String? = ""
        fun newInstance(
            listener: ICancelBeatDayListener,
            date: String?
        ): CancelBeatDayBottomSheetDialogFragment {
            this.listener = listener
            this.date = date
            return CancelBeatDayBottomSheetDialogFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDate.text = DateFormatHelper.convertStringToCustomDateFormat(
            date,
            SimpleDateFormat("dd MMM yy, EEE", Locale.ENGLISH)
        )
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            if (binding.etComment.text.toString().isEmpty()) {
                binding.etComment.error = "Please enter the reason for cancel this date!!"
            } else {
                listener?.onCancelBeatDayWithReason(binding.etComment.text.toString())
                dismiss()
            }
        }
    }

    interface ICancelBeatDayListener {
        fun onCancelBeatDayWithReason(reason: String)
    }

}