package com.app.rupyz.sales.staffactivitytrcker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityFragmentContainerBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.BeatPlanModel
import com.app.rupyz.model_kt.StaffCurrentlyActiveDataModel
import com.app.rupyz.sales.attendance.MyAttendanceFragment
import com.app.rupyz.sales.beat.StaffAssignedToBeatListFragment
import com.app.rupyz.sales.beatplan.AllBeatPlanListFragment
import com.app.rupyz.sales.beatplan.AllDailyBeatPlanListFragment
import com.app.rupyz.sales.beatplan.BeatPlanHistoryFragment
import com.app.rupyz.sales.beatplan.BeatRouteTabFragment
import com.app.rupyz.sales.expense.ExpenseTrackerListFragment
import com.app.rupyz.sales.orders.IDataChangeListener
import com.app.rupyz.sales.preference.UserPreferencesFragment
import com.app.rupyz.sales.profile.ProfileFragment
import com.app.rupyz.sales.reminder.WeekMonthReminderFragment
import com.app.rupyz.sales.targets.AllTargetsListFragment
import com.app.rupyz.sales.targets.ProductTargetDetailsFragment
import com.app.rupyz.ui.more.MoreFragment
import java.text.SimpleDateFormat
import java.util.*

class FragmentContainerActivity : BaseActivity(), IDataChangeListener,
    AllDailyBeatPlanListFragment.IDailyBeatPlanStatusShowListener {
    private lateinit var binding: ActivityFragmentContainerBinding
    private var isDataUpdated: Boolean = false
    private var isDataChanging: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(AppConstant.USER_PREFERENCE)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.preferences)
            replaceFragment(R.id.frame_container, UserPreferencesFragment())
        }

        if (intent.hasExtra(AppConstant.IMAGE_TYPE_PROFILE)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.profile)
            replaceFragment(R.id.frame_container, ProfileFragment.newInstance(this))
        }

        if (intent.hasExtra(AppConstant.KNOW_MORE_TYPE)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.more)
            replaceFragment(R.id.frame_container, MoreFragment())
        }

        if (intent.hasExtra(AppConstant.EXPENSE_DETAILS)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.expenses)
            replaceFragment(R.id.frame_container, ExpenseTrackerListFragment())
        }

        if (intent.hasExtra(AppConstant.ACTIVITY_ID)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.activity)
            replaceFragment(R.id.frame_container, ActivityTrackerListFragment())
        }

        if (intent.hasExtra(AppConstant.STAFF_ID)) {
            var dateFilter = ""
            if (intent.hasExtra(AppConstant.DATE_FILTER)) {
                dateFilter = intent.getStringExtra(AppConstant.DATE_FILTER)!!
            }
            binding.tvToolbarTitle.text = resources.getString(R.string.activity_details)
            val staffId = intent.getIntExtra(AppConstant.STAFF_ID, 0)
            val staffName = intent.getStringExtra(AppConstant.STAFF_NAME)

            val fragment = MyActivityFragment()
            val bundle = Bundle()
            bundle.putInt(AppConstant.STAFF_ID, staffId)
            bundle.putString(AppConstant.STAFF_NAME, staffName)
            bundle.putString(AppConstant.DATE_FILTER, dateFilter)
            fragment.arguments = bundle

            replaceFragment(R.id.frame_container, fragment)
        }

        if (intent.hasExtra(AppConstant.ATTENDANCE)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.attendance)
            replaceFragment(R.id.frame_container, MyAttendanceFragment())
        }

        if (intent.hasExtra(AppConstant.PROFILE_SLUG)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.profile_settings)
            replaceFragment(R.id.frame_container, UserPreferencesFragment())
        }

        if (intent.hasExtra(AppConstant.TARGET_SALES)) {
            var staffId: Int? = null

            if (intent.hasExtra(AppConstant.USER_ID)) {
                staffId = intent.getIntExtra(AppConstant.USER_ID, 0)
                binding.tvToolbarTitle.text = resources.getString(R.string.targets)
            } else {
                binding.tvToolbarTitle.text = resources.getString(R.string.my_targets)
            }

            val fragment = AllTargetsListFragment()
            val bundle = Bundle()
            bundle.putInt(AppConstant.STAFF_ID, staffId ?: 0)
            fragment.arguments = bundle

            replaceFragment(R.id.frame_container, fragment)
        }


        if (intent.hasExtra(AppConstant.TARGET_PRODUCTS)) {
            var currentlyActiveTarget: StaffCurrentlyActiveDataModel? = null
            if (intent.hasExtra(AppConstant.TARGET_PRODUCTS_LIST)) {
                currentlyActiveTarget = intent.getParcelableExtra(AppConstant.TARGET_PRODUCTS_LIST)
            }
            binding.tvToolbarTitle.text = resources.getString(R.string.products_targets)

            val fragment = ProductTargetDetailsFragment()
            val bundle = Bundle()
            bundle.putParcelable(AppConstant.TARGET_PRODUCTS_LIST, currentlyActiveTarget)
            bundle.putBoolean(AppConstant.TARGET_PRODUCTS, true)
            fragment.arguments = bundle

            replaceFragment(R.id.frame_container, fragment)
        }


        // Open Reminder List for particular Date
        if (intent.hasExtra(AppConstant.REMINDERS)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.reminders)
            val fragment = WeekMonthReminderFragment()
            val bundle = Bundle()
            if (intent.hasExtra(AppConstant.DATE_FILTER)) {
                bundle.putString(
                    AppConstant.DATE_FILTER,
                    intent.getStringExtra(AppConstant.DATE_FILTER)
                )
            }
            fragment.arguments = bundle
            replaceFragment(R.id.frame_container, fragment)
        }

        // Open Staff List for Beat details
        if (intent.hasExtra(AppConstant.STAFF_DETAILS_FOR_BEAT)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.staff_assigned)

            if (intent.hasExtra(AppConstant.LOCATION) && intent.getStringExtra(AppConstant.LOCATION)
                    .isNullOrEmpty().not()
            ) {
                binding.tvToolbarSubTitle.visibility = View.VISIBLE
                binding.tvToolbarSubTitle.text = intent.getStringExtra(AppConstant.LOCATION)
            }

            val fragment = StaffAssignedToBeatListFragment()
            val bundle = Bundle()
            if (intent.hasExtra(AppConstant.BEAT_ID_FOR_ASSIGN_STAFF)) {
                bundle.putInt(AppConstant.BEAT_ID_FOR_ASSIGN_STAFF, intent.getIntExtra(AppConstant.BEAT_ID_FOR_ASSIGN_STAFF, 0))
            }
            fragment.arguments = bundle
            replaceFragment(R.id.frame_container, fragment)
        }

        if (intent.hasExtra(AppConstant.VIEW_BEAT_PLAN)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.beat_plan)
            val fragment = BeatRouteTabFragment()
            val bundle = Bundle()
            bundle.putBoolean(AppConstant.VIEW_BEAT_PLAN, true)
            fragment.arguments = bundle
            replaceFragment(R.id.frame_container, fragment)
        }

        if (intent.hasExtra(AppConstant.BEAT_ID)) {
            val fragment = AllDailyBeatPlanListFragment.getInstance(this)
            val bundle = Bundle()
            bundle.putInt(AppConstant.BEAT_ID, intent.getIntExtra(AppConstant.BEAT_ID, 0))
            if (intent.hasExtra(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION)) {
                bundle.putBoolean(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION, true)
            }
            fragment.arguments = bundle

            replaceFragment(R.id.frame_container, fragment)
        }

        if (intent.hasExtra(AppConstant.ALL_BEAT_PLAN)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.beat_plan_list)
            val fragment = AllBeatPlanListFragment()
            val bundle = Bundle()
            bundle.putInt(AppConstant.USER_ID, intent.getIntExtra(AppConstant.USER_ID, 0))
            if (intent.hasExtra(AppConstant.STAFF_DETAILS)) {
                bundle.putBoolean(AppConstant.STAFF_DETAILS, true)
            }
            if (intent.hasExtra(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION)) {
                bundle.putBoolean(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION, true)
            }
            fragment.arguments = bundle
            replaceFragment(R.id.frame_container, fragment)
        }

        if (intent.hasExtra(AppConstant.BEAT_PLAN_HISTORY)) {
            binding.tvToolbarTitle.text = resources.getString(R.string.beat_plan_history)
            val fragment = BeatPlanHistoryFragment()
            val bundle = Bundle()
            if (intent.hasExtra(AppConstant.BEAT_ID)) {
                bundle.putInt(AppConstant.BEAT_ID, intent.getIntExtra(AppConstant.BEAT_ID, 0))
            }
            fragment.arguments = bundle
            replaceFragment(R.id.frame_container, fragment)
        }

        binding.imgClose.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        if (isDataChanging.not()) {
            if (isDataUpdated) {
                val intent = Intent()
                setResult(RESULT_OK, intent)
            }
            finish()
        }
    }

    override fun showDailyBeatPlanStatus(status: String?, active: Boolean?) {
        binding.tvStatus.visibility = View.VISIBLE
        binding.tvStatus.text = status

        if (active == true) {
            binding.tvStatusSubTitle.visibility = View.VISIBLE
        }

        when (status) {
            AppConstant.APPROVED.uppercase(), AppConstant.ACTIVE.uppercase() -> {
                binding.tvStatus.setBackgroundResource(R.drawable.payment_approved_background)
                binding.tvStatus.setTextColor(resources.getColor(R.color.payment_approved_text_color))
            }

            AppConstant.PENDING.uppercase() -> {
                binding.tvStatus.setBackgroundResource(R.drawable.status_pending_background)
                binding.tvStatus.setTextColor(resources.getColor(R.color.pending_text_color))
            }
            AppConstant.REJECTED.uppercase() -> {
                binding.tvStatus.setBackgroundResource(R.drawable.payment_rejected_background)
                binding.tvStatus.setTextColor(resources.getColor(R.color.payment_rejected_text_color))
            }
            AppConstant.COMPLETED.uppercase() -> {
                binding.tvStatus.setBackgroundResource(R.drawable.status_closed_background)
                binding.tvStatus.setTextColor(resources.getColor(R.color.closed_text_color))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showBeatPlanName(info: BeatPlanModel) {
        binding.tvToolbarSubTitle.visibility = View.VISIBLE
        binding.tvToolbarSubTitle.text = "${
            DateFormatHelper.convertStringToCustomDateFormat(
                info.startDate,
                SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
            )
        } - ${
            DateFormatHelper.convertStringToCustomDateFormat(
                info.endDate, SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
            )
        }"
        binding.tvToolbarTitle.text = info.name
    }

    override fun onNotifyDataChange() {
        isDataUpdated = true
        isDataChanging = false
    }
}