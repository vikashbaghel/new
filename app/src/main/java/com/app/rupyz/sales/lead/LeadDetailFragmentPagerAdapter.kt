package com.app.rupyz.sales.lead

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.sales.customer.CustomerActivityFragment

class LeadDetailFragmentPagerAdapter(
    context: FragmentActivity,
    var customerId: Int,
    var fragmentList: ArrayList<String>,
    var leadModel: LeadLisDataItem
) :
    FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            val fragment = LeadBasicDetailFragment()
            val args = Bundle()
            args.putParcelable(AppConstant.LEAD_INFO, leadModel)
            args.putInt(AppConstant.CUSTOMER_ID, customerId)
            fragment.arguments = args
            fragment
        } else {
            val fragment = CustomerActivityFragment()
            val args = Bundle()
            args.putInt(AppConstant.CUSTOMER_ID, customerId)
            args.putString(AppConstant.CUSTOMER_TYPE, AppConstant.LEAD_FEEDBACK)
            fragment.arguments = args
            fragment
        }
    }

}