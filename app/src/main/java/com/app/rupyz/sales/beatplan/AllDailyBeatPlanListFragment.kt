package com.app.rupyz.sales.beatplan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.databinding.FragmentAllDailyBeatPlanListBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.BeatPlanModel
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.sales.expense.ExpenseRejectedBottomSheetDialogFragment

class AllDailyBeatPlanListFragment : BaseFragment(),
    ExpenseRejectedBottomSheetDialogFragment.IExpenseRejectedListener,
    ApproveBottomSheetDialogFragment.IApproveActionListener,
    BeatPlanListInfoAdapter.IBeatPlanListInfoListener {
    private lateinit var binding: FragmentAllDailyBeatPlanListBinding
    private lateinit var beatViewModel: BeatViewModel
    private lateinit var beatPlanInfoAdapter: BeatPlanListInfoAdapter
    private var beatList: ArrayList<BeatRouteDayListModel> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var beatPlanId: Int = 0
    private var targetVisited: Boolean = false
    private var approvalIntent: Boolean = false

    companion object {
        private var listener: IDailyBeatPlanStatusShowListener? = null
        fun getInstance(listener: IDailyBeatPlanStatusShowListener?): AllDailyBeatPlanListFragment {
            this.listener = listener
            return AllDailyBeatPlanListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllDailyBeatPlanListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beatViewModel = ViewModelProvider(requireActivity())[BeatViewModel::class.java]

        arguments?.let {
            beatPlanId = arguments?.getInt(AppConstant.BEAT_ID)!!
            if (arguments?.get(AppConstant.TARGET_VISITS) != null) {
                targetVisited = arguments?.getBoolean(AppConstant.TARGET_VISITS)!!
            }

            if (arguments?.get(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION) != null && PermissionModel.INSTANCE
                    .getPermission(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION, false)
            ) {
                approvalIntent = true
            }
        }

        initRecyclerView()
        initObservers()

        binding.btnApprove.setOnClickListener {
            showApproveDialog()
        }
        binding.btnReject.setOnClickListener {
            showRejectDialog()
        }
    }

    private fun showRejectDialog() {
        val fragment = ExpenseRejectedBottomSheetDialogFragment(this)
        fragment.show(childFragmentManager, AppConstant.BEAT)
    }

    private fun showApproveDialog() {
        val fragment = ApproveBottomSheetDialogFragment.getInstance(
            this,
            AllDailyBeatPlanListFragment::class.java.name
        )
        fragment.show(childFragmentManager, AppConstant.BEAT)
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = linearLayoutManager
        beatPlanInfoAdapter = BeatPlanListInfoAdapter(beatList, targetVisited, this)
        binding.rvList.adapter = beatPlanInfoAdapter
    }

    private fun loadBeatList() {
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.getDailyBeatPlanList(beatPlanId, null)
    }

    private fun initObservers() {
        beatViewModel.dailyBeatPlanListLiveData.observe(requireActivity()) {
            isPageLoading = false
            if (isAdded) {
                binding.progressBar.visibility = View.GONE
                if (it.error == false) {

                    it.beatRouteInfoAndDayListModel?.let { data ->

                        if (data.beatRouteInfo != null) {
                            data.beatRouteInfo?.let { info ->

                                listener?.showBeatPlanName(info)

                                listener?.showDailyBeatPlanStatus(info.status, info.isActive)
                                if (info.status == "PENDING") {
                                    if (approvalIntent) {
                                        binding.btnLayout.visibility = View.VISIBLE
                                    }
                                }
                                if (approvalIntent) {
                                    binding.groupUser.visibility = View.VISIBLE
                                    ImageUtils.loadTeamImage(info.profilePicUrl, binding.ivUser)
                                    binding.tvUserName.text = info.createdByName
                                } else {
                                    binding.groupUser.visibility = View.GONE
                                }
                            }
                        }

                        if (data.beatRouteDayPlan.isNullOrEmpty().not()) {
                            binding.clEmptyData.visibility = View.GONE
                            data.beatRouteDayPlan?.let { list ->
                                if (currentPage == 1) {
                                    beatList.clear()
                                }

                                beatList.addAll(list)
                                beatPlanInfoAdapter.notifyDataSetChanged()
                                if (list.size < 30) {
                                    isApiLastPage = true
                                }
                            }
                        } else {
                            if (currentPage == 1) {
                                binding.clEmptyData.visibility = View.VISIBLE
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

        beatViewModel.beatStatusChangeLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                listener?.showDailyBeatPlanStatus(it.data?.status, it.data?.isActive)
                binding.btnLayout.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.clEmptyData.visibility = View.GONE
        beatList.clear()
        beatPlanInfoAdapter.notifyDataSetChanged()
        loadBeatList()
    }

    override fun expenseRejected(reason: String) {
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.beatPlanApprovedOrRejected(beatPlanId, AppConstant.REJECTED, reason)
    }

    override fun approvalConformation(reason: String) {
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.beatPlanApprovedOrRejected(beatPlanId, AppConstant.APPROVED, reason)
    }

    interface IDailyBeatPlanStatusShowListener {
        fun showDailyBeatPlanStatus(status: String?, active: Boolean?)
        fun showBeatPlanName(info: BeatPlanModel)
    }

    override fun onBeatPlanInfo(model: BeatRouteDayListModel, position: Int) {
        val intent = Intent(requireContext(), MyBeatPlanActivity::class.java)
        intent.putExtra(AppConstant.BEAT_ID, model.beatId)
        intent.putExtra(AppConstant.BEAT_ROUTE_PLAN_ID, model.beatrouteplan)
        intent.putExtra(AppConstant.DATE_FILTER, model.date)
        intent.putExtra(AppConstant.STAFF_DETAILS, true)
        startActivity(intent)
    }
}