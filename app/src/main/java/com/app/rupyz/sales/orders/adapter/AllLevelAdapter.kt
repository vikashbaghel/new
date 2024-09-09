package com.app.rupyz.sales.orders.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemAllLevelBinding
import com.app.rupyz.model_kt.AllCategoryResponseModel
import kotlin.math.min

class AllLevelAdapter(
    private var data: List<AllCategoryResponseModel>,
    private var listener: CategoryListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_level, parent, false)
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
        private val binding = ItemAllLevelBinding.bind(itemView)
        fun bindItem(model: AllCategoryResponseModel, position: Int, listener: CategoryListener) {
            binding.tvStatus.text = model.name.toString()

            binding.checkbox.setOnCheckedChangeListener { _, _ ->
                listener.onCategorySelect(model, position)

            }

            if (model.isSelected) {
                binding.checkbox.isChecked=model.isSelected
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.black))
                itemView.setBackgroundResource(R.drawable.edit_text_grey_with_stroke_background)

            } else {
                binding.checkbox.isChecked=model.isSelected
                itemView.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.leve_text_color))

            }
        }
    }
    interface CategoryListener {
        fun onCategorySelect(model: AllCategoryResponseModel, position: Int)

    }
}






