package com.app.rupyz.sales.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivitySalesAndAmountKnowMoreBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.sales.staff.TotalAmountReceivedDetailsFragment

class SalesAndAmountKnowMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySalesAndAmountKnowMoreBinding
    private var staffData: StaffData? = null
    private var customerType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesAndAmountKnowMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(AppConstant.STAFF_NAME)){
            staffData = intent.getParcelableExtra(AppConstant.STAFF_NAME)
        }

        if (intent.hasExtra(AppConstant.CUSTOMER_TYPE)){
            customerType = AppConstant.CUSTOMER_TYPE
        }

        if (intent.hasExtra(AppConstant.KNOW_MORE_TYPE)) {
            if (intent.getStringExtra(AppConstant.KNOW_MORE_TYPE).equals(AppConstant.TOTAL_SALES)) {
                binding.tvToolbarTitle.text = resources.getString(R.string.total_sales)

            } else {
                binding.tvToolbarTitle.text = resources.getString(R.string.amount_received)

                replaceFragment(TotalAmountReceivedDetailsFragment(staffData!!, customerType))
            }
        }

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun replaceFragment(fragment: Fragment) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, AppConstant.TOTAL_AMOUNT_RECEIVE)
        ft.commit()
    }
}