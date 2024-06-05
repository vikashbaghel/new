package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentAssignedLeadBeatBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.sales.customer.CustomFormActivity
import com.app.rupyz.sales.lead.AddNewLeadActivity
import com.app.rupyz.sales.lead.LeadDetailsActivity
import com.app.rupyz.sales.lead.LeadListAdapter
import com.app.rupyz.sales.lead.LeadViewModel
import com.app.rupyz.sales.orders.InfoBottomSheetDialogFragment
import java.util.Locale

class AssignedLeadsBeatFragment : BaseFragment(), LeadListAdapter.ILeadActionListener {
    private lateinit var binding: FragmentAssignedLeadBeatBinding

    private lateinit var beatViewModel: BeatViewModel
    private lateinit var leadViewModel: LeadViewModel
    private lateinit var leadListAdapter: LeadListAdapter

    private var leadList = ArrayList<LeadLisDataItem>()
    private var filteredLeadList = ArrayList<LeadLisDataItem>()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var currentPage = 1

    private var customerInActivePosition = -1

    var delay: Long = 500 // 1 seconds after user stops typing

    var lastTextEdit: Long = 0
    var handler: Handler = Handler(Looper.myLooper()!!)

    companion object {
        var beatId = 0
        var beatDate = ""
        fun getInstance(beatID: Int, beatDate: String): AssignedLeadsBeatFragment {
            beatId = beatID
            this.beatDate = beatDate
            return AssignedLeadsBeatFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignedLeadBeatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]
        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]

        initRecyclerView()
        initObservers()
        loadLeadList()

        binding.ivSearch.setOnClickListener {
            currentPage = 1
            filteredLeadList.clear()
            Utils.hideKeyboard(requireActivity())
            validateLeadSearch()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentPage = 1
                leadList.clear()
                validateLeadSearch()
                Utils.hideKeyboard(requireActivity())
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishChecker);
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 1) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishChecker, delay);
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    filteredLeadList.clear()
                    filteredLeadList.addAll(leadList)
                    leadListAdapter.notifyDataSetChanged()
                    binding.ivClearSearch.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            filteredLeadList.clear()

            filteredLeadList.addAll(leadList)
            leadListAdapter.notifyDataSetChanged()
        }
    }


    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            currentPage = 1
            filteredLeadList.clear()
            leadListAdapter.notifyDataSetChanged()

            validateLeadSearch()
            binding.clEmptyData.visibility = View.GONE
        }
    }

    private fun validateLeadSearch() {
        filterLeadSearch(binding.etSearch.text.toString(), leadList)
    }

    private fun filterLeadSearch(text: String, leadList: ArrayList<LeadLisDataItem>) {
        val tempCustomerList: ArrayList<LeadLisDataItem> = ArrayList()

        //looping through existing elements
        for (s in leadList) {
            //if the existing elements contains the search input
            if (s.businessName?.lowercase(Locale.getDefault())!!
                            .contains(text.lowercase(Locale.getDefault()))
            ) {
                //adding the element to filtered list
                tempCustomerList.add(s)
            }
        }

        //calling a method of the adapter class and passing the filtered list
        filteredLeadList.clear()
        filteredLeadList.addAll(tempCustomerList)
        leadListAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = linearLayoutManager

        leadListAdapter =
            LeadListAdapter(
                    filteredLeadList, this, isStaffUser(), hasInternetConnection()
                )

        binding.rvList.adapter = leadListAdapter
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        beatViewModel.leadListLiveData.observe(requireActivity()) { data ->
            binding.progressBar.visibility = View.GONE

            if (data.error != true) {
                isPageLoading = false

                data.beatRouteCustomerDataModel?.let { it ->
                    if (it.leadList.isNullOrEmpty().not()) {

                        leadList.clear()
                        leadList.addAll(it.leadList!!)

                        filteredLeadList.clear()
                        filteredLeadList.addAll(it.leadList!!)

                        leadListAdapter.notifyDataSetChanged()

                    } else {
                        leadList.clear()
                        leadListAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
            }
        }

        leadViewModel.addLeadLiveData.observe(requireActivity()) {
            Toast.makeText(requireContext(), "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error == false) {
                if (customerInActivePosition != -1) {
                    leadList.removeAt(customerInActivePosition)
                    leadListAdapter.notifyItemRemoved(customerInActivePosition)

                    customerInActivePosition = -1
                }
            } else {
                showToast(it.message)
            }
        }
    }

    private var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            loadLeadList()
            leadListAdapter.notifyDataSetChanged()
        }
    }

    private fun loadLeadList() {
        beatViewModel.getLeadListForBeat(
                beatId,
                "VISITED_LEADS",
                beatDate,
                currentPage
        )
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun getLeadDetails(model: LeadLisDataItem, position: Int) {
        someActivityResultLauncher.launch(
                Intent(requireContext(), LeadDetailsActivity::class.java).putExtra(
                        AppConstant.LEAD_INFO,
                        model
                )
        )
    }

    override fun onCall(model: LeadLisDataItem) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${model.mobile}")
        startActivity(intent)
    }

    override fun onWCall(model: LeadLisDataItem) {
        val uri =
                Uri.parse("https://api.whatsapp.com/send?phone=+91${model.mobile} &text=Hi, ${model.contactPersonName}")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(sendIntent)
    }

    override fun onEditLead(model: LeadLisDataItem, position: Int) {
        someActivityResultLauncher.launch(
                Intent(requireContext(), AddNewLeadActivity::class.java).putExtra(
                        AppConstant.LEAD_INFO,
                        model
                )
        )
    }

    override fun onDeleteLead(model: LeadLisDataItem, position: Int) {
        showDeleteDialog(model, position)
    }

    override fun onRecordActivity(model: LeadLisDataItem) {
        if (PermissionModel.INSTANCE.hasRecordActivityPermission()) {
            startActivity(
                    Intent(
                            requireContext(),
                            CustomFormActivity::class.java
                    ).putExtra(AppConstant.CUSTOMER_ID, model.id)
                            .putExtra(AppConstant.CUSTOMER, model)
                            .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.LEAD_FEEDBACK)
            )
        } else {
            showToast(resources.getString(R.string.you_dont_have_permission_to_perform_this_action))
        }
    }

    override fun getStoreFrontInfo() {
        val fragment = InfoBottomSheetDialogFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.HEADING, resources.getString(R.string.storefront_lead))
        bundle.putString(AppConstant.MESSAGE, resources.getString(R.string.storefront_lead_message))
        fragment.arguments = bundle
        fragment.show(childFragmentManager, AppConstant.STORE_FRONT)
    }

    private fun showDeleteDialog(model: LeadLisDataItem, position: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_lead)
        tvTitle.text = resources.getString(R.string.delete_lead_message)

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {
            leadViewModel.deleteLead(model.id!!, hasInternetConnection())

            customerInActivePosition = position

            dialog.dismiss()
        }

        dialog.show()
    }

    override fun viewCustomerLocation(model: LeadLisDataItem) {
        if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
            Utils.openMap(requireContext(), model.mapLocationLat, model.mapLocationLong, model.businessName)
        } else {
            showToast(resources.getString(R.string.no_location_found))
        }
    }
}
