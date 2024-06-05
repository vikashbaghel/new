package com.app.rupyz.sales.targets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemTargetListBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.StaffTargetModel

class ActiveTargetsListRvAdapter(
    private var data: ArrayList<StaffTargetModel>,
    private var listener: ITargetProductActionListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_target_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTargetListBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: StaffTargetModel,
            position: Int,
            listener: ITargetProductActionListener
        ) {
            binding.tvTitle.text = model.title

            if (model.title == AppConstant.TARGET_SALES || model.title == AppConstant.TARGET_COLLECTION) {
                var targetAmount = CalculatorHelper().convertCommaSeparatedAmount(model.target, AppConstant.TWO_DECIMAL_POINTS)
                if (targetAmount!!.contains(".00")) {
                    targetAmount = targetAmount.replace(".00", "")
                }
                binding.tvTarget.text = targetAmount

                var achievedAmount = CalculatorHelper().convertCommaSeparatedAmount(model.achieved, AppConstant.TWO_DECIMAL_POINTS)
                if (achievedAmount!!.contains(".00")) {
                    achievedAmount = achievedAmount.replace(".00", "")
                }

                binding.tvAchieved.text = achievedAmount
            } else {
                binding.tvTarget.text = "${model.target?.toInt()}"
                binding.tvAchieved.text = "${model.achieved?.toInt()}"
            }

            if (model.achieved != 0.0) {
                val progress = (model.achieved!! / model.target!!) * 100
                binding.tvPercent.text = "${progress.toInt()}%"
            } else {
                binding.tvPercent.text = "0%"
            }

            if (model.title == AppConstant.TARGET_PRODUCTS) {
                binding.tvTarget.text = "#${model.target?.toInt()}"
                binding.tvAchieved.text = " - "
                binding.tvTitle.setTextColor(itemView.resources.getColor(R.color.theme_purple))
                binding.ivArrow.visibility = View.VISIBLE
                itemView.setOnClickListener {
                    listener.getTargetProductDetails()
                }
            } else {
                binding.tvTitle.setTextColor(itemView.resources.getColor(R.color.black))
                binding.ivArrow.visibility = View.GONE
            }
        }
    }

    interface ITargetProductActionListener {
        fun getTargetProductDetails()
    }
}