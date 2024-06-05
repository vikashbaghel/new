package com.app.rupyz.sales.staff

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemCustomerListForAssignBinding
import com.app.rupyz.model_kt.NameAndIdSetInfoModel

class AssignBeatForStaffAdapter(
    private var data: ArrayList<NameAndIdSetInfoModel>,
    private var listener: IAssignBeatForStaffListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer_list_for_assign, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCustomerListForAssignBinding.bind(itemView)

        fun bindItem(model: NameAndIdSetInfoModel, position: Int, listener: IAssignBeatForStaffListener) {
            binding.cbCustomerName.text = model.name

            binding.cbCustomerName.setOnCheckedChangeListener(null)

            binding.cbCustomerName.isChecked = model.isSelected == true

            binding.cbCustomerName.setOnCheckedChangeListener { _, isChecked ->
                listener.onBeatSelect(isChecked, model)
            }
        }
    }

    interface IAssignBeatForStaffListener {
        fun onBeatSelect(checked: Boolean, model: NameAndIdSetInfoModel)
    }
}