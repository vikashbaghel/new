package com.app.rupyz.sales.staff

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.ItemSalesStaffBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.order.sales.StaffData

class StaffListAdapter(
    private var data: ArrayList<StaffData>,
    private var listener: StaffActionListener,
    private var hasInternetConnection: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sales_staff, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position, listener, hasInternetConnection)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSalesStaffBinding.bind(itemView)

        fun bindItem(
            model: StaffData,
            position: Int,
            listener: StaffActionListener,
            hasInternetConnection: Boolean
        ) {
            binding.tvId.text = "ID: " + model.employeeId
            binding.tvRole.text = "Role: " + (model.roles?.get(0) ?: "")
            binding.tvName.text = model.name
            binding.ivCall.setOnClickListener {
                listener.onCall(
                    model,
                    position
                )
            }
            binding.ivWhatsApp.setOnClickListener {
                listener.onWCall(
                    model,
                    position
                )
            }

            if (hasInternetConnection && PermissionModel.INSTANCE.getPermission(
                    AppConstant.EDIT_STAFF_PERMISSION,
                    false
                ) || hasInternetConnection && PermissionModel.INSTANCE.getPermission(
                    AppConstant.DEACTIVATE_STAFF_PERMISSION,
                    false
                )
            ) {
                binding.ivMore.visibility = View.VISIBLE
            } else {
                binding.ivMore.visibility = View.GONE
            }

            binding.ivMore.setOnClickListener { v ->
                //creating a popup menu
                val popup =
                    PopupMenu(v.context, binding.ivMore)
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_edit_and_delete)

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.EDIT_STAFF_PERMISSION,
                        false
                    ).not()
                ) {
                    popup.menu.getItem(0).isVisible = false
                }

                if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.DEACTIVATE_STAFF_PERMISSION,
                        false
                    ).not()
                ) {
                    popup.menu.getItem(1).isVisible = false
                }

                //adding click listener
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.edit_product -> {
                            listener.onEdit(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.delete_product -> {
                            listener.onDelete(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        else -> return@setOnMenuItemClickListener false
                    }
                }
                //displaying the popup
                popup.show()
            }

            itemView.setOnClickListener { listener.onGetInfo(model, position) }

        }
    }
}