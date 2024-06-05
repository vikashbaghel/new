package com.app.rupyz.sales.reminder

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.LeadListLayoutBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.ReminderItemModel
import com.app.rupyz.sales.customer.CustomFormActivity
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity

class TodayTomorrowReminderFragment : BaseFragment(),
        ReminderListAdapter.IReminderListDetailsListener,
        ReminderInfoBottomSheetDialogFragment.IRemindersListener {
    private lateinit var binding: LeadListLayoutBinding

    private val viewModel: RemindersViewModel by viewModels()

    private lateinit var category: String
    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var particularDate: String? = null

    private lateinit var adapter: ReminderListAdapter

    private var reminderList = ArrayList<ReminderItemModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = LeadListLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        category = arguments?.getString(AppConstant.TAB_NAME)!!

        binding.tvErrorMessage.text = resources.getString(R.string.you_dont_have_any_reminders)

        initRecyclerView()
        initObservers()

        binding.swipeToRefresh.setOnRefreshListener {
            currentPage = 1
            loadReminderList()
            binding.swipeToRefresh.isRefreshing = false
        }

    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible){
            loadReminderList()
        }
    }
    private fun loadReminderList() {
        viewModel.getReminderList(category, particularDate, currentPage)
        if (currentPage > 1) {
            binding.paginationProgressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = linearLayoutManager

        adapter =
                ReminderListAdapter(
                        reminderList, this
                )

        binding.rvList.adapter = adapter

        binding.rvList.addOnScrollListener(object :
                PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                currentPage += 1
                loadReminderList()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })
    }


    private fun initObservers() {
        viewModel.reminderListLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            binding.paginationProgressBar.visibility = View.GONE
            isPageLoading = false
            if (it.error == false) {
                if (it.data?.result.isNullOrEmpty().not()) {
                    it.data?.result?.let { list ->
                        if (currentPage == 1) {
                            reminderList.clear()
                        }
                        reminderList.addAll(list)
                        adapter.notifyDataSetChanged()

                        if (list.size < 30) {
                            isApiLastPage = true
                        }
                    }
                } else {
                    isApiLastPage = true
                    if (currentPage == 1) {
                        reminderList.clear()
                        adapter.notifyDataSetChanged()
                        binding.clEmptyData.visibility = View.VISIBLE
                    }
                }
            } else {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.deleteReminderLiveData.observe(requireActivity()) {
            if (it.error == false) {
                currentPage = 1
                reminderList.clear()
                adapter.notifyDataSetChanged()

                isApiLastPage = false
                loadReminderList()
            } else {
                binding.progressBar.visibility = View.GONE
                showToast(it.message)
            }
        }
    }

    override fun onGetReminderDetails(model: ReminderItemModel) {
        val fragment = ReminderInfoBottomSheetDialogFragment.newInstance(model, this)
        fragment.show(childFragmentManager, TodayTomorrowReminderFragment::class.java.name)
    }

    override fun viewCustomerPhoto(model: ReminderItemModel) {
        if (model.logoImageUrl.isNullOrEmpty().not()) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            val imageModel = ImageViewModel(0, 0, model.logoImageUrl)
            imageViewModelArrayList.add(imageModel)

            imageListModel.data = imageViewModelArrayList
            startActivity(
                    Intent(requireContext(), OrgPhotosViewActivity::class.java)
                            .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                            .putExtra(AppConstant.IMAGE_POSITION, 0)
            )
        } else {
            showToast(resources.getString(R.string.customer_pic_not_available))
        }
    }

    override fun onDeleteReminder(model: ReminderItemModel?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_reminders)
        tvTitle.text = resources.getString(R.string.delete_reminder_message)

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.deleteReminder(model?.id)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onEditReminder(model: ReminderItemModel?) {
        if (PermissionModel.INSTANCE.hasRecordActivityPermission()) {
            someActivityResultLauncher.launch(
                    Intent(requireContext(), CustomFormActivity::class.java)
                            .putExtra(AppConstant.REMINDERS, true)
                            .putExtra(AppConstant.CUSTOMER_ID, model?.moduleId)
                            .putExtra(AppConstant.CUSTOMER_FEEDBACK, model?.followup_id)
                            .putExtra(AppConstant.ACTIVITY_TYPE, model?.moduleType)
            )
        } else {
            showToast(resources.getString(R.string.you_dont_have_permission_to_perform_this_action))
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            currentPage = 1
            isApiLastPage = false

            loadReminderList()
        }
    }
}