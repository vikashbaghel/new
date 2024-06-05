package com.app.rupyz.ui.organization.profile.activity.addproduct

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.PackagingUnitItemBinding
import com.app.rupyz.generic.helper.DigitsInputFilter
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.PackagingLevelModel

class PackagingLevelAdapter(
    private var data: ArrayList<PackagingLevelModel>,
    private val onClickListener: IPackagingUnitListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.packaging_unit_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, onClickListener)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MyViewHolder).enableTextWatcher()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MyViewHolder).disableTextWatcher()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textWatcher: TextWatcher? = null

        private val binding = PackagingUnitItemBinding.bind(itemView)
        fun bindItem(
            model: PackagingLevelModel,
            position: Int,
            onClickListener: IPackagingUnitListener
        ) {
            if (position == 0) {
                binding.ivDelete.visibility = View.GONE
            } else {
                binding.ivDelete.visibility = View.VISIBLE
            }

            binding.edtPackagingSize.filters = arrayOf<InputFilter>(
                DigitsInputFilter(
                    7,
                    AppConstant.MAX_DIGIT_AFTER_DECIMAL
                )
            )

            binding.tvPackagingUnit.setOnClickListener {
                onClickListener.addPackagingUnit(position, model)
            }

            binding.tvPackagingUnit.text = model.unit

            binding.edtPackagingSize.setText(CalculatorHelper().calculateQuantity(model.size))

            textWatcher = MyTextWatcher(model)

            if (model.buyersUnit.isNullOrEmpty().not()) {
                binding.groupPackagingLevel.visibility = View.VISIBLE
                binding.tvPackagingUnitPerSize.text = model.buyersUnit
            } else {
                binding.groupPackagingLevel.visibility = View.GONE
            }

            binding.ivDelete.setOnClickListener {
                onClickListener.onDeletePackagingUnit(position, model)
            }
        }

        fun enableTextWatcher() {
            binding.edtPackagingSize.addTextChangedListener(textWatcher)
            binding.edtPackagingSize.tag = adapterPosition
        }

        fun disableTextWatcher() {
            binding.edtPackagingSize.removeTextChangedListener(textWatcher)
        }
    }

    class MyTextWatcher(
        private val model: PackagingLevelModel
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            if (input.toString().isNotEmpty() && input.toString() != ".") {
                model.size = input.toString().toDouble()
            } else {
                model.size = null
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    interface IPackagingUnitListener {
        fun onDeletePackagingUnit(position: Int, model: PackagingLevelModel)
        fun addPackagingUnit(position: Int, model: PackagingLevelModel)
    }
}
