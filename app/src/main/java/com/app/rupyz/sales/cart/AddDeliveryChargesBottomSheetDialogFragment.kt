package com.app.rupyz.sales.cart

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetAddDeliveryChargesBinding
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.CartItemDiscountModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddDeliveryChargesBottomSheetDialogFragment(
    private val listener: IAddDeliveryChargesListener,
    private val editCharges: Boolean,
    private val model: CartItemDiscountModel?,
    private val position: Int?
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddDeliveryChargesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetAddDeliveryChargesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.etChargesValue.filters = arrayOf<InputFilter>(
            DigitsInputFilter(
                9,
                AppConstant.MAX_DIGIT_AFTER_DECIMAL
            )
        )

        if (editCharges) {
            if (model?.name != null) {
                binding.etChargesKey.setText(model.name.toString())
            }

            if (model?.value != null) {
                binding.etChargesValue.setText(CalculatorHelper().calculateQuantity(model.value))
            }

            binding.buttonCancel.setBackgroundColor(
                resources.getColor(
                    R.color.delete_discount_red,
                    resources.newTheme()
                )
            )
            binding.buttonCancel.setTextColor(
                resources.getColor(
                    R.color.white,
                    resources.newTheme()
                )
            )
            binding.buttonCancel.text = resources.getString(R.string.delete)

            binding.buttonProceed.text = resources.getString(R.string.save)

            binding.hdHeader.text = resources.getString(R.string.edit_charges)
        } else {
            binding.hdHeader.text = resources.getString(R.string.others_charges_with_max_value)
        }

        binding.buttonCancel.setOnClickListener {
            if (editCharges) {
                listener.onDeleteCharges(model, position)
            }

            dismiss()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.buttonProceed.setOnClickListener {
            validateDiscount()
        }
    }

    private fun validateDiscount() {
        if (binding.etChargesKey.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Enter discount name", Toast.LENGTH_SHORT).show()
        } else if (binding.etChargesValue.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Enter discount value", Toast.LENGTH_SHORT).show()
        } else {
            val cartItemDiscountModel = CartItemDiscountModel()
            cartItemDiscountModel.name = binding.etChargesKey.text.toString()
            cartItemDiscountModel.value = binding.etChargesValue.text.toString().toDouble()

            binding.etChargesKey.setText("")
            binding.etChargesValue.setText("")

            if (editCharges) {
                listener.onEditCharges(cartItemDiscountModel, position)
            } else {
                listener.onAddCharges(cartItemDiscountModel)
            }

            dismiss()
        }
    }

    interface IAddDeliveryChargesListener {
        fun onAddCharges(model: CartItemDiscountModel)
        fun onDeleteCharges(model: CartItemDiscountModel?, position: Int?)
        fun onEditCharges(model: CartItemDiscountModel?, position: Int?)
    }
}