package com.app.rupyz.sales.cart

import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetAddDiscountBinding
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.CartItemDiscountModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddDiscountBottomSheetDialogFragment(
        private val listener: IAddDiscountListener,
        private val editDiscount: Boolean,
        private val model: CartItemDiscountModel?,
        private val position: Int?
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetAddDiscountBinding
    private var discountType = AppConstant.DISCOUNT_TYPE_RUPEES

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetAddDiscountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (editDiscount) {
            if (model != null) {
                discountType = model.type!!
                if (discountType == AppConstant.DISCOUNT_TYPE_RUPEES) {
                    binding.rgDiscountType.check(R.id.rb_rupees)
                } else {
                    binding.rgDiscountType.check(R.id.rb_percentage)
                }
            }

            if (model?.name != null) {
                binding.etDiscountKey.setText(model.name.toString())
            }

            if (model?.value != null) {
                binding.etDiscountValue.setText(model.value.toString())
            }


            binding.buttonCancel.setBackgroundColor(resources.getColor(R.color.delete_discount_red))
            binding.buttonCancel.text = resources.getString(R.string.delete)

            binding.buttonProceed.text = resources.getString(R.string.save)

            binding.hdHeader.text = resources.getString(R.string.edit_discount)
        } else {
            binding.hdHeader.text = resources.getString(R.string.discount_with_max_value)
        }

        binding.etDiscountValue.filters = arrayOf<InputFilter>(
                DigitsInputFilter(
                        10,
                        AppConstant.MAX_DIGIT_AFTER_DECIMAL
                )
        )

        binding.rgDiscountType.setOnCheckedChangeListener { rg, _ ->
            val selectedId: Int = rg?.checkedRadioButtonId!!
            if (selectedId == R.id.rb_percentage) {
                discountType = AppConstant.DISCOUNT_TYPE_PERCENT
                binding.etDiscountValue.filters = arrayOf<InputFilter>(
                        DigitsInputFilter(
                                2,
                                AppConstant.MAX_DIGIT_AFTER_DECIMAL
                        )
                )
            } else {
                discountType = AppConstant.DISCOUNT_TYPE_RUPEES
                binding.etDiscountValue.filters = arrayOf<InputFilter>(
                        DigitsInputFilter(
                                10,
                                AppConstant.MAX_DIGIT_AFTER_DECIMAL
                        )
                )
            }
        }

        binding.buttonCancel.setOnClickListener {
            if (editDiscount) {
                listener.onDeleteDiscount(model, position)
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
        if (binding.etDiscountKey.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Enter discount name", Toast.LENGTH_SHORT).show()
        } else if (binding.etDiscountValue.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Enter discount value", Toast.LENGTH_SHORT).show()
        } else if (discountType == AppConstant.DISCOUNT_TYPE_PERCENT && binding.etDiscountValue.text.toString()
                        .toDouble() > 100.0
        ) {
            Toast.makeText(
                    requireContext(),
                    "You can not enter discount percentage mode then 100",
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            val cartItemDiscountModel = CartItemDiscountModel()
            cartItemDiscountModel.name = binding.etDiscountKey.text.toString()
            cartItemDiscountModel.value = binding.etDiscountValue.text.toString().toDouble()
            cartItemDiscountModel.type = discountType

            binding.etDiscountKey.setText("")
            binding.etDiscountValue.setText("")

            if (editDiscount) {
                listener.onEditDiscount(cartItemDiscountModel, position)
            } else {
                listener.onAddDiscount(cartItemDiscountModel)
            }

            dismiss()
        }
    }

    interface IAddDiscountListener {
        fun onAddDiscount(model: CartItemDiscountModel)
        fun onDeleteDiscount(model: CartItemDiscountModel?, position: Int?)
        fun onEditDiscount(model: CartItemDiscountModel?, position: Int?)
    }
}