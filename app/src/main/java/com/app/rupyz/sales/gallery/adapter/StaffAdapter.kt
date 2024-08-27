package com.app.rupyz.sales.gallery.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.StaffItemBinding
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.order.sales.StaffData


class StaffAdapter(
    private var data: ArrayList<StaffData>?,
    private var listener: OnItemCheckListener,
    private val checkboxStaffMap: HashMap<Int, Boolean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.staff_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            (holder as MyViewHolder).bindItem(data!![position], listener, checkboxStaffMap)
        } catch (e: Exception) {
            println()
        }


    }

    override fun getItemCount(): Int {
        return data!!.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = StaffItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            data: StaffData, listener: OnItemCheckListener,
            checkboxStaffMap: HashMap<Int, Boolean>
        ) {

            try {
                binding.staffName.text = data.name
                binding.staffName.tag = data.user

                if (data.profilePicUrl.isNullOrEmpty().not()) {
                    ImageUtils.loadImage(data.profilePicUrl, binding.imgStaff)
                } else {
                    binding.imgStaff.setImageResource(R.mipmap.no_photo_available)
                }

                binding.checkBox.isChecked = checkboxStaffMap[binding.staffName.tag] ?: false
                binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView.isPressed) {
                        if (isChecked) {
                            listener.onItemCheck(data.user!!)
                        } else {
                            listener.onItemUncheck(data.user!!)

                        }
                        checkboxStaffMap[binding.staffName.tag.toString().trim().toInt()] =
                            isChecked
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}

interface OnItemCheckListener {
    fun onItemCheck(id: Int?)
    fun onItemUncheck(id: Int?)
}


