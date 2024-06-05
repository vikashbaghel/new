package com.app.rupyz.sales.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityExpenseTrackerFragmentBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref

class ExpenseTrackerListFragment : BaseFragment() {
    private lateinit var binding: ActivityExpenseTrackerFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityExpenseTrackerFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvMyExpense.setOnClickListener {
            binding.tvMyExpense.setBackgroundColor(resources.getColor(R.color.theme_purple))
            binding.tvApproveExpense.setBackgroundColor(resources.getColor(R.color.white))
            binding.tvMyExpense.setTextColor(resources.getColor(R.color.white))
            binding.tvApproveExpense.setTextColor(resources.getColor(R.color.expense_dark_gray))

            replaceFragment(R.id.frame_container, MyExpenseListFragment())
        }

        binding.tvApproveExpense.setOnClickListener {
            binding.tvMyExpense.setBackgroundColor(resources.getColor(R.color.white))
            binding.tvApproveExpense.setBackgroundColor(resources.getColor(R.color.theme_purple))

            binding.tvMyExpense.setTextColor(resources.getColor(R.color.expense_dark_gray))
            binding.tvApproveExpense.setTextColor(resources.getColor(R.color.white))

            replaceFragment(R.id.frame_container, ApprovalRequestsListFragment())
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        /* Setting the fragment on SetMenuVisibility because if "Hierarchy" permission is changes
            dynamically from the backend wee need to replace fragment accordingly
            */

        if (menuVisible) {
            if (isStaffUser()) {
                replaceFragment(R.id.frame_container, MyExpenseListFragment())

                if (SharedPref.getInstance().getBoolean(
                        AppConstant.STAFF_HIERARCHY,
                        false
                    ) && PermissionModel.INSTANCE.getPermission(
                        AppConstant.REIMBURSEMENT_APPROVAL_PERMISSION,
                        false
                    )
                ) {
                    binding.clExpensesTab.visibility = View.VISIBLE
                } else {
                    binding.clExpensesTab.visibility = View.GONE
                }
            } else {
                replaceFragment(R.id.frame_container, ApprovalRequestsListFragment())
                binding.clExpensesTab.visibility = View.GONE
            }
        }
    }
}