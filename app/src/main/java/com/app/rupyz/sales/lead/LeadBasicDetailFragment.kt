package com.app.rupyz.sales.lead

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentLeadBasicDetailBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import java.util.*

class LeadBasicDetailFragment : Fragment() {
    private lateinit var binding: FragmentLeadBasicDetailBinding
    private lateinit var leadViewModel: LeadViewModel
    private var leadModel: LeadLisDataItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeadBasicDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leadViewModel = ViewModelProvider(requireActivity())[LeadViewModel::class.java]

        initLayout()
        arguments?.let {
            leadModel = arguments?.getParcelable(AppConstant.LEAD_INFO)!!
            if (leadModel != null) {
                initObservers(leadModel!!)
            }
        }
    }

    private fun initLayout() {
        with(binding.mapView) {
            // Initialise the MapView
            onCreate(null)
            isClickable = false
            // Set the map ready callback to receive the GoogleMap object
            getMapAsync {
                MapsInitializer.initialize(requireActivity())
            }
        }
    }

    private fun initObservers(model: LeadLisDataItem) {
        if (!model.leadCategoryName.isNullOrEmpty()) {
            binding.tvLeadCategory.text = model.leadCategoryName?.replaceFirstChar(
                Char::titlecase
            )
        }

        if (model.source.isNullOrEmpty().not() && model.source.equals(AppConstant.STORE_FRONT)) {
            binding.tvStoreFrontView.visibility = View.VISIBLE
            binding.tvStoreFrontView.setOnClickListener {
                val fragment = InfoBottomSheetDialogFragment()
                val bundle = Bundle()
                bundle.putString(AppConstant.HEADING, resources.getString(R.string.storefront_lead))
                bundle.putString(
                    AppConstant.MESSAGE,
                    resources.getString(R.string.storefront_lead_message)
                )
                fragment.arguments = bundle
                fragment.show(childFragmentManager, AppConstant.STORE_FRONT)
            }
        } else {
            binding.tvStoreFrontView.visibility = View.GONE
        }

        if (!model.contactPersonName.isNullOrEmpty()) {
            binding.tvContactPerson.text = model.contactPersonName?.replaceFirstChar(
                Char::titlecase
            )
        }
        if (!model.businessName.isNullOrEmpty()) {
            binding.tvBusinessName.text = model.businessName?.replaceFirstChar(
                Char::titlecase
            )
        }
        if (!model.gstin.isNullOrEmpty()) {
            binding.tvGstInfo.text = model.gstin
        }
        if (!model.designation.isNullOrEmpty()) {
            binding.tvDesignation.text = model.designation?.replaceFirstChar(
                Char::titlecase
            )
        }
        if (!model.mobile.isNullOrEmpty()) {
            binding.tvPrimaryMobileNumber.text = model.mobile
        }

        if (!model.email.isNullOrEmpty()) {
            binding.tvEmail.text = model.email
        }
        if (!model.addressLine1.isNullOrEmpty()) {
            binding.tvAddress.text = model.addressLine1?.replaceFirstChar(
                Char::titlecase
            )
        }
        if (!model.city.isNullOrEmpty()) {
            binding.tvCity.text = model.city?.replaceFirstChar(
                Char::titlecase
            )
        }
        if (!model.state.isNullOrEmpty()) {
            binding.tvState.text = model.state?.replaceFirstChar(
                Char::titlecase
            )
        }

        if (model.logoImageUrl.isNullOrEmpty().not()){
            ImageUtils.loadImage(model.logoImageUrl, binding.ivCompanyImage)
            binding.groupProfileImage.visibility = View.VISIBLE
        }

        if (model.mapLocationLat != null && model.mapLocationLat != 0.0
            && model.mapLocationLong != null && model.mapLocationLong!= 0.0){
            binding.groupMapView.visibility = View.VISIBLE

            with(binding.mapView) {
                getMapAsync { map ->
                    setMapLocation(map, LatLng(model.mapLocationLat!!, model.mapLocationLong!!), true)
                }
            }
        }

        if (!model.pincode.isNullOrEmpty()) {
            binding.tvPincode.text = model.pincode
        }
        if (!model.follow_update.isNullOrEmpty()) {
            binding.tvFollowDate.text = DateFormatHelper.getMonthDate(model.follow_update)
        }
        if (!model.comments.isNullOrEmpty()) {
            binding.tvComment.text = model.comments
        }
    }


    private fun setMapLocation(map: GoogleMap, position: LatLng, updateLeadLocation: Boolean) {
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
            addMarker(MarkerOptions().position(position))
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

}