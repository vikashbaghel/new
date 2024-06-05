package com.app.rupyz.sales.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.sales.orders.IDataChangeListener
import com.app.rupyz.sales.staff.SalesAndOrderDetailsFragment

class CustomerDetailFragmentPagerAdapter(
    context: FragmentActivity,
    var customerId: Int,
    var fragmentList: ArrayList<String>,
    private val isSecondLevelAvailable: Boolean,
    var isIDataChangeListener: IDataChangeListener
) :
    FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return if (isSecondLevelAvailable) {
                val fragment = SecondLevelCustomerListFragment()
                val args = Bundle()
                args.putInt(AppConstant.CUSTOMER_ID, customerId)
                fragment.arguments = args
                fragment
            } else {
                val fragment = SalesAndOrderDetailsFragment()
                val args = Bundle()
                args.putInt(AppConstant.CUSTOMER_ID, customerId)
                fragment.arguments = args
                fragment
            }
        } else if (position == 1) {
            return if (isSecondLevelAvailable) {
                val fragment = SalesAndOrderDetailsFragment()
                val args = Bundle()
                args.putInt(AppConstant.CUSTOMER_ID, customerId)
                fragment.arguments = args
                return fragment
            } else {
                val fragment = CustomerActivityFragment()
                val args = Bundle()
                args.putInt(AppConstant.CUSTOMER_ID, customerId)
                args.putString(AppConstant.CUSTOMER_TYPE, AppConstant.CUSTOMER_FEEDBACK)
                fragment.arguments = args
                return fragment
            }
        } else {
            val fragment = CustomerActivityFragment()
            val args = Bundle()
            args.putInt(AppConstant.CUSTOMER_ID, customerId)
            args.putString(AppConstant.CUSTOMER_TYPE, AppConstant.CUSTOMER_FEEDBACK)
            fragment.arguments = args
            return fragment
        }
    }

}