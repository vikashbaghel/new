package com.app.rupyz.sales.beatplan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentAllBeatPlanListBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.DeleteDialog
import com.app.rupyz.generic.utils.RecyclerTouchListener
import com.app.rupyz.model_kt.BeatPlanModel
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity
import com.google.android.material.tabs.TabLayout

class AllBeatPlanListFragment : BaseFragment(), RecyclerTouchListener.ClickListener,DeleteDialog.IOnClickListener {
    private lateinit var binding: FragmentAllBeatPlanListBinding

    private lateinit var beatViewModel: BeatViewModel
    private lateinit var beatPlanInfoAdapter: BeatPlanInfoAdapter
    private var beatList: ArrayList<BeatPlanModel> = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var status: String = ""
    private var staffDetailsView: Boolean = false

    private var deleteBeatPlanModel: BeatPlanModel? = null
    private var deleteBeatPlanPosition: Int = -1
    private var userId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllBeatPlanListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beatViewModel = ViewModelProvider(requireActivity())[BeatViewModel::class.java]

        arguments?.let {
            if (arguments?.get(AppConstant.USER_ID) != null) {
                userId = arguments?.getInt(AppConstant.USER_ID)
            }

            if (arguments?.get(AppConstant.STAFF_DETAILS) != null) {
                staffDetailsView = true
            }
        }

        initTabLayout()

        initLayout()
    }

    private fun initLayout() {
        initRecyclerView()
        initObservers()

//        binding.swipeToRefresh.setOnRefreshListener {
//            binding.swipeToRefresh.isRefreshing = false
//            currentPage = 1
//            isPageLoading = true
//            loadBeatList()
//        }

        binding.tvAddNewBeat.setOnClickListener {
            startActivity(Intent(requireContext(), AddNewBeatPlanActivity::class.java))
        }
    }

    private fun initTabLayout() {
        if (arguments?.get(AppConstant.MY_BEAT_PLAN) != null) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.ALL))
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.ACTIVE))
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(AppConstant.UPCOMING_TARGET)
            )
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.CLOSED))
            binding.tvAddNewBeat.visibility = View.VISIBLE
        } else {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.ACTIVE))
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.APPROVED))
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.STATUS_PENDING))
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(AppConstant.CLOSED))
            binding.tvAddNewBeat.visibility = View.GONE
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                status = when (tab.text) {
                    AppConstant.ALL -> ""
                    AppConstant.CLOSED -> "${AppConstant.COMPLETED}, ${AppConstant.REJECTED}"
                    else -> tab.text.toString()
                }

                currentPage = 1

                loadBeatList()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = linearLayoutManager

        beatPlanInfoAdapter = BeatPlanInfoAdapter(beatList, staffDetailsView, false)

        binding.rvList.adapter = beatPlanInfoAdapter

        binding.rvList.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadBeatList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        binding.rvList.addOnItemTouchListener(
            RecyclerTouchListener(
                requireContext(),
                binding.rvList,
                this
            )
        )

    }

    private fun loadBeatList() {
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.getBeatPlanList(null, status, userId, currentPage)
    }

    private fun initObservers() {
        beatViewModel.beatPlanListLiveData.observe(viewLifecycleOwner) {
            if (isAdded) {
                binding.progressBar.visibility = View.GONE
                isPageLoading = false
                if (it.error == false) {
                    if (it.data.isNullOrEmpty().not()) {
                        binding.clEmptyData.visibility = View.GONE
                        it.data?.let { list ->
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
                        }
                    }
                } else {
                    if (it.errorCode != null && it.errorCode == 403){
                        logout()
                    } else {
                        showToast(it.message)
                    }
                }
            }
        }

        beatViewModel.deleteBeatPlanLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            if (it.error == false) {
                it.data?.let { model ->
                    if (model.isUsed == true) {
                        DeleteDialog.showDeleteDialog(requireActivity(),deleteBeatPlanModel!!.id,true,resources.getString(R.string.delete_beat_plan),it.message!!,this)
                    } else {
                        if (deleteBeatPlanPosition != -1 && deleteBeatPlanPosition < beatList.size) {
                            try {
                                beatList.removeAt(deleteBeatPlanPosition)
                                beatPlanInfoAdapter.notifyItemRemoved(deleteBeatPlanPosition)
                                beatPlanInfoAdapter.notifyItemRangeChanged(
                                    deleteBeatPlanPosition,
                                    beatList.size
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

  /*  private fun showDeleteDialog(isForced: Boolean, message: String, model: BeatPlanModel?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_beat_plan)
        tvTitle.text = message

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            beatViewModel.deleteBeatPlan(isForced, model?.id!!)
            dialog.dismiss()
        }

        dialog.show()
    }
*/
    override fun onResume() {
        super.onResume()
        binding.clEmptyData.visibility = View.GONE
        beatList.clear()
        beatPlanInfoAdapter.notifyDataSetChanged()
        if ((arguments?.get(AppConstant.MY_BEAT_PLAN) != null).not() && (status == "")) {
            this.status = AppConstant.ACTIVE
        }
        loadBeatList()
    }

    override fun onClick(view: View?, position: Int) {
        if (staffDetailsView) {
            startAllBeatActivity(position)
        } else {
            if (binding.rvList.findViewHolderForAdapterPosition(position)?.itemView != null) {

                val popup =
                    PopupMenu(
                        requireContext(),
                        binding.rvList.findViewHolderForAdapterPosition(position)?.itemView?.findViewById(
                            R.id.iv_more_info
                        )
                    )

                popup.inflate(R.menu.menu_beat_plan_view)
                //adding click listener

                if (beatList[position].status == AppConstant.COMPLETED.uppercase()
                ) {
                    popup.menu.getItem(1).isVisible = false
                    popup.menu.getItem(4).isVisible = false
                }

                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.menu_view -> {
                            startAllBeatActivity(position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.edit_product -> {
                            editBeatPlan(position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_history -> {
                            showBeatPlanHistory(position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_duplicate -> {
                            duplicateBeatPlan(position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.delete_product -> {
                            deleteBeatPlanModel = beatList[position]
                            deleteBeatPlanPosition = position

                            DeleteDialog.showDeleteDialog(requireActivity(),deleteBeatPlanModel!!.id,false,resources.getString(R.string.delete_beat_plan),resources.getString(R.string.delete_beat_plan_message),this)

                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
                //displaying the popup
                popup.show()
            }
        }
    }

    private fun duplicateBeatPlan(position: Int) {
        startActivity(
            Intent(requireContext(), AddNewBeatPlanActivity::class.java)
                .putExtra(
                    AppConstant.BEAT_ID,
                    beatList[position].id
                ).putExtra(AppConstant.DUPLICATE_BEAT_PLAN, true)
        )
    }

    private fun showBeatPlanHistory(position: Int) {
        startActivity(
            Intent(requireContext(), FragmentContainerActivity::class.java)
                .putExtra(
                    AppConstant.BEAT_ID,
                    beatList[position].id
                ).putExtra(
                    AppConstant.BEAT_PLAN_HISTORY,
                    true
                )
        )
    }

    private fun editBeatPlan(position: Int) {
        startActivity(
            Intent(requireContext(), AddNewBeatPlanActivity::class.java)
                .putExtra(
                    AppConstant.BEAT_ID,
                    beatList[position].id
                )
        )
    }

    private fun startAllBeatActivity(position: Int) {
        val newIntent = Intent(
            requireContext(),
            FragmentContainerActivity::class.java
        )
        newIntent.putExtra(AppConstant.BEAT_ID, beatList[position].id)
        if (arguments?.get(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION) != null) {
            newIntent.putExtra(AppConstant.BEAT_PLAN_APPROVAL_PERMISSION, true)
        }
        startActivity(newIntent)
    }

    override fun onDelete(model: Any, position: Any) {
        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.deleteBeatPlan(position as Boolean, model as Int)
    }
}