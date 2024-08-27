package com.app.rupyz.sales.customer.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.Sections
import com.app.rupyz.sales.customer.CustomFormStepFragment


class AddNewCustomerTabPagerAdapter(private  val customerId: Int,private val steps :  MutableList<Sections?> , fragmentManager: FragmentManager , lifecycleManager : Lifecycle) : FragmentStateAdapter(fragmentManager,lifecycleManager) {
	
	override fun getItemCount() : Int {
		return 4
	}
	
	override fun createFragment(position : Int) : Fragment {
		return CustomFormStepFragment().apply {
			arguments = Bundle().apply {
				putInt("pageNUmber",(position + 1 ))
				putInt(AppConstant.CUSTOMER_ID,customerId)
			}
		}
	}
}