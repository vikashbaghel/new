package com.app.rupyz.ui.organization.profile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ProductItemBinding
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.CalculatorHelper
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.ui.organization.profile.ProductActionListener
import java.util.*

class ProductListAdapter(
    var data: List<ProductList>,
    var mContext: Context,
    var listener: ProductActionListener,
    var isSlugAvailable: Boolean,
    var slug: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val viewTypeItem = 0
    private val viewTypeLoading = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeItem) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_item, parent, false)
            MyViewHolder(itemView)
        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_loading_item, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindItem(
                data[position],
                position,
                listener
            )
        } else if (holder is LoadingViewHolder) {
            holder.bindItem()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return viewTypeItem
    }

    class LoadingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bindItem() {

        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ProductItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: ProductList,
            position: Int,
            listener: ProductActionListener
        ) {
            if (model.code != null) {
                binding.tvProductCode.text = model.code.toString()
            } else {
                binding.tvProductCode.text = ""
            }

            binding.tvProductName.text = model.name.replaceFirstChar(Char::titlecase)

            if (model.packaging_level.isNullOrEmpty().not()) {
                if (model.packaging_level.size == 1) {
                    binding.tvOrderQty.visibility = View.VISIBLE
                    binding.tvPackagingLevel.visibility = View.GONE
                    binding.tvOrderQty.text =
                        itemView.context.getString(
                            R.string.product_quantity_string,
                            "${model.packaging_level[0].size}",
                            model.unit,
                            model.packaging_level[0].unit?.replaceFirstChar(Char::titlecase)
                        )

                    binding.tvProductUnit.text =
                        model.packaging_level[0].unit?.replaceFirstChar(Char::titlecase)

                } else if (model.packaging_level.size > 1) {
                    binding.tvPackagingLevel.visibility = View.VISIBLE
                    binding.tvPackagingLevel.text = "Pkg Level (${model.packaging_level.size})"
                    binding.tvOrderQty.visibility = View.GONE
                    binding.tvPackagingLevel.setOnClickListener {
                        listener.getPackagingLevelInfo(model)
                    }

                    val list = ArrayList<String>()
                    model.packaging_level.forEach {
                        list.add(it.unit!!)
                    }
                } else {
                    binding.tvOrderQty.visibility = View.GONE
                }
            }

            if (model.mrp_unit != null) {
                binding.tvMrpUnit.text =
                    model.mrp_unit.toString().replaceFirstChar(Char::titlecase)
            }

            if (model.unit != null) {
                binding.tvBuyersUnit.text =
                    model.unit.toString().replaceFirstChar(Char::titlecase)
            }

            binding.tvProductMrp.text =
                "MRP:  " + CalculatorHelper().convertCommaSeparatedAmount(model.mrp_price.toDouble(), AppConstant.FOUR_DECIMAL_POINTS) + " /"

            binding.tvProductPrice.text =
                CalculatorHelper().convertCommaSeparatedAmount(model.price.toDouble(), AppConstant.FOUR_DECIMAL_POINTS) + " /"


            if (model.gst_exclusive != null) {
                if (model.gst_exclusive) {
                    binding.tvGst.text = "(GST " + model.gst + "% extra)"
                } else {
                    binding.tvGst.text = "(GST " + model.gst + "% incl.)"
                }
            } else {
                binding.tvGst.visibility = View.GONE
            }

            try {
                if (model.displayPicUrl != null && model.displayPicUrl != "") {
                    ImageUtils.loadImage(model.displayPicUrl, binding.ivProduct)
                } else if (model.pics_urls != null && model.pics_urls.size > 0) {
                    ImageUtils.loadImage(model.pics_urls[0], binding.ivProduct)
                } else {
                    ImageUtils.loadImage("", binding.ivProduct)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            itemView.setOnClickListener {
                listener.getProductDetails(
                    model, position
                )
            }

        }
    }
}