package com.app.rupyz.sales.orders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ProductPriceSlabListItemBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.model_kt.packagingunit.TelescopicPricingModel

class ProductPriceSlabListAdapter(
    private var data: ArrayList<TelescopicPricingModel>?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_price_slab_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data!![position])
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ProductPriceSlabListItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(model: TelescopicPricingModel) {
            binding.tvPriceSlab.text =
                itemView.resources.getString(R.string.price_slab_minimun_quantity, "" + model.qty)
            binding.tvPrice.text = CalculatorHelper().convertCommaSeparatedAmount(model.price, AppConstant.FOUR_DECIMAL_POINTS)
        }
    }

}