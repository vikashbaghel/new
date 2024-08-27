package com.app.rupyz.sales.beatplan

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityAddNewBeatPlanBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.model_kt.CreateBeatRoutePlanModel

class AddNewBeatPlanActivity : BaseActivity(), CreateBeatPlanListener {
    private lateinit var binding: ActivityAddNewBeatPlanBinding
    private var beatPlanName: String? = null
    private var beatId: Int = 0
    private lateinit var beatViewModel: BeatViewModel
    private val createBeatRoutePlanModel = CreateBeatRoutePlanModel()

    private var isDuplicateBeatPlan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewBeatPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]

        initObservers()

        if (intent.hasExtra(AppConstant.BEAT_ID)) {
            beatId = intent.getIntExtra(AppConstant.BEAT_ID, 0)

            binding.progressBar.visibility = View.VISIBLE
            beatViewModel.getBeatPlanInfoForEdit(beatId)

            if (intent.hasExtra(AppConstant.DUPLICATE_BEAT_PLAN)) {
                isDuplicateBeatPlan = true
                binding.tvToolbarTitle.text = resources.getString(R.string.duplicate_beat_plan)
            } else {
                binding.tvToolbarTitle.text = resources.getString(R.string.edit_beat_plan)
            }
        }


        if (beatId == 0) {
            addCreatePlanHeadFragment()
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        beatViewModel.dailyBeatPlanListLiveData.observe(this) {
            if (it.error == false) {
                it.beatRouteInfoAndDayListModel?.beatRouteInfo?.let { info ->

                    createBeatRoutePlanModel.name = info.name
                    createBeatRoutePlanModel.startDate = info.startDate
                    createBeatRoutePlanModel.endDate = info.endDate
                    createBeatRoutePlanModel.isActive = info.isActive ?: false

                    it.beatRouteInfoAndDayListModel?.beatRouteDayPlan?.let { day ->
                        if (day.isEmpty().not()) {
                            createBeatRoutePlanModel.beatRouteDayPlan =
                                day
                        }

                        if (isDuplicateBeatPlan && day.isEmpty().not()) {
                            createBeatRoutePlanModel.beatPlanDuplicateFromId =
                                day[0].beatrouteplan ?: 0
                        }
                    }

                    binding.progressBar.visibility = View.GONE
                    addCreatePlanHeadFragment()
                }
            }
        }
    }

    private fun addCreatePlanHeadFragment() {
        val createBeatPlan =
            CreateBeatPlanHeaderFragment.newInstance(
                this,
                createBeatRoutePlanModel,
                isDuplicateBeatPlan
            )
        addFragment(R.id.container, createBeatPlan)
    }

    override fun onCreateBeatPlanHead(model: CreateBeatRoutePlanModel) {
        beatPlanName = model.name
        if (beatId == 0) {
            model.beatRouteDayPlan = ArrayList()
        }
        val addBeatPlanDetails =
            AddBeatPlanDetailsFragment.newInstance(this, beatId, model, isDuplicateBeatPlan)
        addFragment(R.id.container, addBeatPlanDetails)
    }

    override fun onCancelCreateBeatPlanHead() {
        onBackPressed()
    }

    override fun onSelectCustomer(
        model: BeatRouteDayListModel,
        targetCustomerIds: ArrayList<Int>?
    ) {

    }


    override fun successfullyCreatedBeatPlan() {
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

}