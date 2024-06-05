package com.app.rupyz.sales.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.app.rupyz.R
import com.app.rupyz.databinding.ImageForLocationPermissionGuideBinding
import com.app.rupyz.sales.targets.TargetsListViewPagerAdapter

class LocationPermissionImageViewPagerAdapter(private val data: List<Int>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_for_location_permission_guide, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(data[position], position)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ImageForLocationPermissionGuideBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            model: Int,
            position: Int
        ) {

            binding.tvStep.text =
                itemView.context.resources.getString(R.string.steps, "${(position + 1)}")

            when (position) {
                0 -> binding.tvTitle.text = itemView.resources.getString(R.string.go_to_setting_app)
                1 -> binding.tvTitle.text = itemView.resources.getString(R.string.select_apps)
                2 -> binding.tvTitle.text = itemView.resources.getString(R.string.select_app_managment)
                3 -> binding.tvTitle.text = itemView.resources.getString(R.string.select_rupyz_app)
                4 -> binding.tvTitle.text = itemView.resources.getString(R.string.select_permissions)
                5 -> binding.tvTitle.text = itemView.resources.getString(R.string.select_location)
                6 -> binding.tvTitle.text = itemView.resources.getString(R.string.select_allow_all_the_time)
            }

            binding.imageViewMain.setImageResource(model)
        }
    }

}