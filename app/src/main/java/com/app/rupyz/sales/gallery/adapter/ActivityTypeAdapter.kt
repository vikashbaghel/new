package com.app.rupyz.sales.gallery.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityTypeItemBinding
import com.app.rupyz.model_kt.CustomerFeedbackStringItem

class ActivityTypeAdapter(
    private var data: ArrayList<CustomerFeedbackStringItem>,
    private var listener: OnItemActivityCheckListener,
    private val checkboxActivityMap: HashMap<Int, Boolean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_type_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position],listener,checkboxActivityMap)


    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterActivityList(filterList: ArrayList<CustomerFeedbackStringItem>) {
        data = filterList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size

    }
    override fun getItemViewType(position : Int) : Int {
        return  position
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ActivityTypeItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: CustomerFeedbackStringItem,
                     listener: OnItemActivityCheckListener,
                    checkboxActivityMap: HashMap<Int, Boolean>) {
            binding.stateTxt.text = model.stringValue
            binding.stateTxt.tag = model.id
            binding.stateTxt.isChecked = checkboxActivityMap[binding.stateTxt.tag] ?: false
            binding.stateTxt.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listener.onItemActivityCheck(model.stringValue, model.type!!)
                } else {
                    listener.onItemActivityUncheck(model.stringValue, model.type!!)

                }
                checkboxActivityMap[ binding.stateTxt.tag.toString().toInt()] = isChecked
            }

        }
    }
}
interface OnItemActivityCheckListener {
    fun onItemActivityCheck(name: String?, type: String)
    fun onItemActivityUncheck(name: String?, type: String)
}