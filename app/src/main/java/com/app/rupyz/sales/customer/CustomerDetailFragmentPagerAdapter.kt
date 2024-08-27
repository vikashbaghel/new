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
        when (position) {
            /***
             * Insights Fragment Index is 0 in @[CustomerDetailActivity] @see{ @method initTabLayout() }
             * @return [InsightsFragment] fragment
             * */
            0    -> {
                val fragment = InsightsFragment()
                val args = Bundle()
                args.putInt(AppConstant.CUSTOMER_ID, customerId)
                args.putString(AppConstant.CUSTOMER_TYPE, AppConstant.CUSTOMER_FEEDBACK)
                fragment.arguments = args
                return fragment
            }
            /***
             * Order List Fragment Index is 1 in @[CustomerDetailActivity] @see{ @method initTabLayout() }
             * @return [SalesAndOrderDetailsFragment] fragment
             * */
            1    -> {
                val fragment = SalesAndOrderDetailsFragment()
                val args = Bundle()
                args.putInt(AppConstant.CUSTOMER_ID, customerId)
                fragment.arguments = args
                return fragment
            }
            /***
             * Customer Activity Fragment Index is 2 in @[CustomerDetailActivity] @see{ @method initTabLayout() }
             * @return [CustomerActivityFragment] fragment
             * */
            2    -> {
                val fragment = CustomerActivityFragment()
                val args = Bundle()
                args.putInt(AppConstant.CUSTOMER_ID, customerId)
                args.putString(AppConstant.CUSTOMER_TYPE, AppConstant.CUSTOMER_FEEDBACK)
                fragment.arguments = args
                return fragment
            }
            /***
             * Customer Child List If Customer is LEVEL-1 or LEVEL-2 Fragment Index is 3 in @[CustomerDetailActivity] @see{ @method initTabLayout() }
             * @return [SecondLevelCustomerListFragment] fragment
             * */
            3    -> {
                val fragment = SecondLevelCustomerListFragment()
                val args = Bundle()
                args.putInt(AppConstant.CUSTOMER_ID, customerId)
                fragment.arguments = args
                return fragment
            }
            /***
             * Inventory List Fragment Index is 4 in @[CustomerDetailActivity] @see{ @method initTabLayout() }
             * @return [InventoryFragment] fragment
             * */
            else -> {
                val fragment = InventoryFragment()
                val args = Bundle()
                args.putInt(AppConstant.CUSTOMER_ID, customerId)
                args.putString(AppConstant.CUSTOMER_TYPE, AppConstant.CUSTOMER_FEEDBACK)
                fragment.arguments = args
                return fragment
                
            }
        }
   }

}