package com.app.rupyz.sales.orders

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetInformationBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.sales.product.IProductBottomSheetActionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetInformationBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetInformationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
        setUpListener()
    }

    private fun setUpListener() {
        dialog?.setOnShowListener { dialog ->
            val mDialog = dialog as BottomSheetDialog
            val bottomSheetView = mDialog.findViewById<View>(com.denzcoskun.imageslider.R.id.design_bottom_sheet)
            var bottomSheetBehavior: BottomSheetBehavior<View>

            if (bottomSheetView != null) {
                BottomSheetBehavior.from(bottomSheetView).state =
                    BottomSheetBehavior.STATE_EXPANDED;
            }

            bottomSheetView?.let {
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
                bottomSheetBehavior.isDraggable = false
                bottomSheetBehavior.isHideable = false

                // Ensure focus is not on an EditText to prevent the keyboard from opening
                it.clearFocus()

                // Hide the keyboard
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                val currentFocus = requireActivity().currentFocus
                currentFocus?.let { view ->
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
    }

    companion object {
        private var listener: IDismissDialogListener? = null

        @JvmStatic
        fun newInstance(
            listener: IDismissDialogListener,
        ): InfoBottomSheetDialogFragment {
            val fragment = InfoBottomSheetDialogFragment()
            this.listener = listener
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val heading = it.getString(AppConstant.HEADING)
            binding.tvHeading.text = heading

            val message = it.getString(AppConstant.MESSAGE)
            binding.tvMessage.text = message

        }

        binding.buttonCancel.setOnClickListener {
            listener?.onDismissInformationDialog()
            dismiss()
        }
    }

    interface IDismissDialogListener{
        fun onDismissInformationDialog(){}
    }

}