package com.app.rupyz.ui.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetDiscoveryFiterBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.BadgeFilterItem
import com.app.rupyz.model_kt.LocationFilterItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DiscoveryFilterBottomSheetDialogFragment(
    private val filterSelectedListener: FilterSelectedListener,
    var filterType: String,
    private val filter: String,
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDiscoveryFiterBinding

    private var location: String? = null
    private var badge: String? = null
    private lateinit var locationFilterAdapter: LocationFilterAdapter
    private lateinit var badgeListAdapter: BadgeListAdapter

    private val badgeModelList: ArrayList<BadgeFilterItem> = ArrayList()
    private val locationList: ArrayList<LocationFilterItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetDiscoveryFiterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()


        when (filterType) {
            AppConstant.FILTER_LOCATION -> {
                binding.clLocation.visibility = View.VISIBLE
                binding.tvHeading.text = "Locations"
                setLocation()
            }

            AppConstant.FILTER_BADGE -> {
                binding.clBadge.visibility = View.VISIBLE
                binding.tvHeading.text = "Badge"
                setBadge()
            }
        }

        binding.tvShowLocationResult.setOnClickListener {
            location?.let {
                filterSelectedListener.onLocationChange(location)
            }
            dismiss()
        }


        binding.tvShowBadgeResult.setOnClickListener {
            badge?.let {
                filterSelectedListener.onBadgeChange(badge)
            }
            dismiss()
        }

        binding.tvReset.setOnClickListener {
            if (filterType == AppConstant.FILTER_BADGE) {
                badgeModelList.forEach { it.isSelected = false }
                badgeListAdapter.notifyDataSetChanged()
                badge = ""
            } else {
                locationList.forEach { it.isSelected = false }
                locationFilterAdapter.notifyDataSetChanged()
                location = ""
            }
        }

    }

    private fun setBadge() {
        if (filter != ""){
            val index = badgeModelList.indexOfFirst{
                it.name == filter
            }
            badgeModelList[index].isSelected = true
            badgeListAdapter.notifyItemChanged(index)
        }
    }

    private fun setLocation() {
        if (filter != ""){
            val index = locationList.indexOfFirst{
                it.name == filter
            }
            locationList[index].isSelected = true
            locationFilterAdapter.notifyItemChanged(index)
            binding.rvLocation.scrollToPosition(index)
        }
    }

    private fun initRecyclerView() {
        val list = resources.getStringArray(R.array.states)

        list.drop(1).forEach {
            val locationFilterItem = LocationFilterItem(it, false)
            locationList.add(locationFilterItem)
        }

        binding.rvLocation.layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        locationFilterAdapter =
            LocationFilterAdapter(locationList, LocationFilterAdapter.OnClickListener {
                location = it.name
                locationList.forEach { it.isSelected = false }
                locationList[it.position!!].isSelected = true
                locationFilterAdapter.notifyDataSetChanged()
            })
        binding.rvLocation.adapter = locationFilterAdapter

        val badgeList = resources.getStringArray(R.array.badges)

        badgeList.forEach {
            val badgeFilterItem = BadgeFilterItem(it, false)
            badgeModelList.add(badgeFilterItem)
        }

        binding.rvBadge.layoutManager = GridLayoutManager(requireContext(), 6)
        badgeListAdapter = BadgeListAdapter(badgeModelList, BadgeListAdapter.OnClickListener {
            badge = it.name
            badgeModelList.forEach { it.isSelected = false }
            badgeModelList[it.position!!].isSelected = true
            badgeListAdapter.notifyDataSetChanged()
        })

        binding.rvBadge.adapter = badgeListAdapter

    }


}