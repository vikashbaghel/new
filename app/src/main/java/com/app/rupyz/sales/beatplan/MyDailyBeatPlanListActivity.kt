package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityMyDailyBeatPlanListBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.model_kt.BeatRouteInfoModel
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity
import java.text.SimpleDateFormat
import java.util.*

class MyDailyBeatPlanListActivity : BaseActivity(),
    BeatPlanListInfoAdapter.IBeatPlanListInfoListener {
    private lateinit var binding: ActivityMyDailyBeatPlanListBinding
    private var beatId: Int = 0
    private lateinit var beatPlanInfoAdapter: BeatPlanListInfoAdapter
    private lateinit var beatViewModel: BeatViewModel
    private var beatList = ArrayList<BeatRouteDayListModel>()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var beatRouteInfoModel: BeatRouteInfoModel? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyDailyBeatPlanListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]
        beatId = intent.getIntExtra(AppConstant.BEAT_ROUTE_PLAN_ID, 0)

        if (intent.hasExtra(AppConstant.STAFF_DETAILS)) {
            binding.tvBeatDetails.visibility = View.GONE
        }

        if (intent.hasExtra(AppConstant.BEAT_ROUTE)) {
            beatRouteInfoModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    AppConstant.BEAT_ROUTE,
                    BeatRouteInfoModel::class.java
                )
            } else {
                intent.getParcelableExtra(AppConstant.BEAT_ROUTE)
            }


            binding.tvToolbarSubTitle.text = "${
                DateFormatHelper.convertStringToCustomDateFormat(
                    beatRouteInfoModel?.startDate,
                    SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )
            } - ${
                DateFormatHelper.convertStringToCustomDateFormat(
                    beatRouteInfoModel?.endDate, SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )
            }"
        }

        initRecyclerView()
        initObservers()

        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.getDailyBeatPlanList(beatId, null)

        binding.tvBeatDetails.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    FragmentContainerActivity::class.java
                ).putExtra(AppConstant.ALL_BEAT_PLAN, true)
            )
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvBeatPlanList.layoutManager = linearLayoutManager
        beatPlanInfoAdapter = BeatPlanListInfoAdapter(beatList, false, this)
        binding.rvBeatPlanList.adapter = beatPlanInfoAdapter

    }

    private fun initObservers() {
        beatViewModel.dailyBeatPlanListLiveData.observe(this) {
            isPageLoading = false
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {

                it.beatRouteInfoAndDayListModel?.let { data ->

                    data.beatRouteInfo?.let { info ->
                        setStatus(info.status)

                        if (info.isActive == true) {
                            binding.tvStatusSubTitle.visibility = View.VISIBLE
                        }
                    }

                    if (data.beatRouteDayPlan.isNullOrEmpty().not()) {
                        data.beatRouteDayPlan?.let { list ->
                            if (currentPage == 1) {
                                beatList.clear()
                            }

                            beatList.addAll(list)
                            beatPlanInfoAdapter.notifyDataSetChanged()

                        }
                    } else {
                        if (currentPage == 1) {
                            isApiLastPage = true
                            beatList.clear()
                            beatPlanInfoAdapter.notifyDataSetChanged()
                        } else {
                            isPageLoading = false
                        }
                    }
                }
            } else {
                showToast(it.message)
            }
        }
    }

    private fun setStatus(status: String?) {
        binding.tvStatus.text = status
        binding.tvStatus.visibility = View.VISIBLE

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
                binding.tvStatus.text = resources.getString(R.string.closed).uppercase()
            }
        }
    }

    override fun onBeatPlanInfo(model: BeatRouteDayListModel, position: Int) {
        val intent = Intent()
        intent.putExtra(AppConstant.BEAT_ID, model.beatId)
        intent.putExtra(AppConstant.BEAT_ROUTE_PLAN_ID, model.beatrouteplan)
        intent.putExtra(AppConstant.DATE_FILTER, model.date)
        setResult(RESULT_OK, intent)
        finish()
    }
}