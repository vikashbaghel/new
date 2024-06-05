package com.app.rupyz.sales.beatplan

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetInfoLayoutBinding
import com.app.rupyz.generic.utils.AppConstant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DuplicateBeatInfoBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetInfoLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetInfoLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        private var listener: IDuplicateBeatPlanListener? = null

        fun newInstance(
            createBeatListener: IDuplicateBeatPlanListener
        ): DuplicateBeatInfoBottomSheetDialogFragment {
            listener = createBeatListener
            return DuplicateBeatInfoBottomSheetDialogFragment()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val heading = it.getString(AppConstant.HEADING)
            binding.tvHeading.text = heading

            val message = it.getString(AppConstant.MESSAGE)
            binding.tvMessage.text = message

        }

        binding.groupButtonLayout.visibility = View.VISIBLE

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            listener?.onCreateDuplicateBeatPlan()
            dismiss()
        }
    }

    interface IDuplicateBeatPlanListener {
        fun onCreateDuplicateBeatPlan()
    }
}