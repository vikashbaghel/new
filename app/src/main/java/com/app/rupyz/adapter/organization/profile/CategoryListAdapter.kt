package com.app.rupyz.adapter.organization.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.CategoryListInsideItemBinding
import com.app.rupyz.model_kt.AllCategoryResponseModel
import kotlin.math.min

class CategoryListAdapter(
    private var data: List<AllCategoryResponseModel>,
    private var listener: CategoryListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_list_inside_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener)
    }

    override fun getItemCount(): Int {
        val limit = 5
        return min(data.size, limit)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = CategoryListInsideItemBinding.bind(itemView)
        fun bindItem(model: AllCategoryResponseModel, position: Int, listener: CategoryListener) {
            binding.tvItem.text = model.name.toString()


            binding.tvItem.setOnClickListener { listener.onCategorySelect(model, position) }

            if (model.isSelected == true) {
                itemView.setBackgroundResource(R.drawable.details_button_style)
                binding.tvItem.setTextColor(itemView.context.resources.getColor(R.color.white))
            } else {
                itemView.setBackgroundResource(R.drawable.add_product_gray_btn_gredient)
                binding.tvItem.setTextColor(itemView.context.resources.getColor(R.color.tab_un_selected_color))
            }
        }
    }
}

interface CategoryListener {
    fun onCategorySelect(model: AllCategoryResponseModel, position: Int)

}
