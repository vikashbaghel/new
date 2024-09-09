package com.app.rupyz.sales.beatplan

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentCreateBeatPlanHeaderBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.CreateBeatRoutePlanModel
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CreateBeatPlanHeaderFragment : BaseFragment(),
    DuplicateBeatInfoBottomSheetDialogFragment.IDuplicateBeatPlanListener {
    private lateinit var binding: FragmentCreateBeatPlanHeaderBinding
    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val myCalendar = Calendar.getInstance()

    private val day = cal[Calendar.DAY_OF_MONTH]
    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var mEndDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private var startDate: String? = null
    private var endDate: String? = null
    private lateinit var beatViewModel: BeatViewModel

    companion object {
        private lateinit var listener: CreateBeatPlanListener
        private var createBeatRoutePlanModel: CreateBeatRoutePlanModel? = null
        private var isDuplicateBeatPlan = false

        fun newInstance(
            createBeatListener: CreateBeatPlanListener,
            createBeatRoutePlanModel: CreateBeatRoutePlanModel,
            isDuplicateBeatPlan: Boolean
        ): CreateBeatPlanHeaderFragment {
            listener = createBeatListener
            this.createBeatRoutePlanModel = createBeatRoutePlanModel
            this.isDuplicateBeatPlan = isDuplicateBeatPlan
            return CreateBeatPlanHeaderFragment()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBeatPlanHeaderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]

        initObservers()

        if (createBeatRoutePlanModel != null && createBeatRoutePlanModel?.name.isNullOrEmpty()
                .not()
        ) {
            binding.etBeatPlanName.setText(createBeatRoutePlanModel?.name)

            if (isDuplicateBeatPlan) {
                binding.etBeatPlanName.setText("Copy of ${createBeatRoutePlanModel?.name}")
                binding.tvEndDate.isEnabled = false
                binding.tvEndDate.setBackgroundResource(R.drawable.edit_text_disable_background)
                binding.tvDateChangeWarning.text =
                    resources.getString(R.string.duplicate_beat_plan_date_message)
                binding.tvDateChangeWarning.visibility = View.VISIBLE

            } else {
                startDate = createBeatRoutePlanModel?.startDate
                binding.tvStartDate.text = DateFormatHelper.convertStringToCustomDateFormat(
                    startDate,
                    SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )
                binding.tvStartDate.isEnabled = false
                binding.tvStartDate.setBackgroundResource(R.drawable.edit_text_disable_background)


                endDate = createBeatRoutePlanModel?.endDate
                binding.tvEndDate.text = DateFormatHelper.convertStringToCustomDateFormat(
                    endDate,
                    SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )
                binding.tvEndDate.isEnabled = false
                binding.tvEndDate.setBackgroundResource(R.drawable.edit_text_disable_background)

                binding.tvDateChangeWarning.visibility = View.VISIBLE
            }
        }

        binding.tvStartDate.setOnClickListener {
            Utils.hideKeyboard(requireActivity())
            openStartDateCalendar()
        }

        binding.tvEndDate.setOnClickListener {
            if (startDate.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please Select Start Day First!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Utils.hideKeyboard(requireActivity())
                opeEndDateCalendar()
            }
        }

        mStartDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateStartDate()
            }

        mEndDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day

                if (startDate == null) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter start Date first!!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    updateEndDate()
                }
            }

        binding.btnCancel.setOnClickListener {
            listener.onCancelCreateBeatPlanHead()
        }

        binding.btnAdd.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        if (binding.etBeatPlanName.text.toString().isEmpty()) {
            showToast("Beat Plan Name Can Not be Empty!!")
            return
        } else if (isDuplicateBeatPlan) {
            if (startDate.isNullOrEmpty()) {
                showToast("Start Date Can Not be Empty!!")
                return
            } else {
                openDuplicateWarningBottomSheet()
            }
        } else {
            createBeatPlan()
        }
    }

    private fun openDuplicateWarningBottomSheet() {
        val fragment = DuplicateBeatInfoBottomSheetDialogFragment.newInstance(this)
        val bundle = Bundle()
        bundle.putString(AppConstant.HEADING, resources.getString(R.string.duplicate_beat_plan))
        val message =
            "This will create Duplicate Beat Plan with name ${binding.etBeatPlanName.text.toString()} and You can update later."
        bundle.putString(AppConstant.MESSAGE, message)
        fragment.arguments = bundle
        fragment.show(
            childFragmentManager,
            DuplicateBeatInfoBottomSheetDialogFragment::class.java.simpleName
        )
    }

    private fun createBeatPlan() {
        if (createBeatRoutePlanModel == null) {
            createBeatRoutePlanModel = CreateBeatRoutePlanModel()
        }
        createBeatRoutePlanModel?.name = binding.etBeatPlanName.text.toString()
        createBeatRoutePlanModel?.startDate = startDate
        createBeatRoutePlanModel?.endDate = endDate

        listener.onCreateBeatPlanHead(createBeatRoutePlanModel!!)
    }

    private fun createDuplicateBeatPlanWithDates() {
        createBeatRoutePlanModel?.name = binding.etBeatPlanName.text.toString()
        createBeatRoutePlanModel?.startDate = startDate
        createBeatRoutePlanModel?.endDate = endDate

        startDate = createBeatRoutePlanModel?.startDate

        val beatDuration = createBeatRoutePlanModel?.beatRouteDayPlan!!.size

        myCalendar.time = DateFormatHelper.convertStringToDate(startDate)
        myCalendar.add(Calendar.DAY_OF_MONTH, beatDuration - 1)

        val endDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)

        val list: List<String> = getDates(startDate!!, endDate)

        createBeatRoutePlanModel?.beatRouteDayPlan?.forEachIndexed { index, beatRouteDayListModel ->
            beatRouteDayListModel.date = list[index]
            beatRouteDayListModel.isUpdate = true
        }

        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.createBeatPlan(
            createBeatRoutePlanModel!!,
            0
        )
    }

    private fun initObservers() {
        beatViewModel.createBeatPlanLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            showToast(it.message ?: resources.getString(R.string.something_went_wrong))
            if (it.error == false) {
                listener.successfullyCreatedBeatPlan()
            }
        }
    }

    private fun getDates(dateString1: String, dateString2: String): List<String> {
        val dates: ArrayList<String> = ArrayList()
        val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = df1.parse(dateString1)
            date2 = df1.parse(dateString2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal1: Calendar = Calendar.getInstance()
        cal1.time = date1!!
        val cal2: Calendar = Calendar.getInstance()

        cal2.time = date2!!
        while (!cal1.after(cal2)) {
            dates.add(df1.format(cal1.time))
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }

    private fun openStartDateCalendar() {
        val dialog = DatePickerDialog(
            requireContext(),
            android.R.style.ThemeOverlay_Material_Dialog,
            mStartDateSetListener,
            year, month, day
        )
        dialog.updateDate(year, month, day)
        dialog.datePicker.minDate =
            Calendar.getInstance().time.time
        dialog.show()
    }

    private fun opeEndDateCalendar() {
        val dialog = DatePickerDialog(
            requireContext(),
            android.R.style.ThemeOverlay_Material_Dialog,
            mEndDateSetListener,
            year, month, day
        )
        dialog.updateDate(year, month, day)

        if (startDate.isNullOrEmpty().not()) {
            dialog.datePicker.minDate =
                DateFormatHelper.convertStringToDate(startDate).time
            dialog.datePicker.maxDate =
                DateFormatHelper.addOneMonthToDate(startDate).time
        }
        dialog.show()
    }

    private fun updateStartDate() {
        val tempStartDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)
        if (endDate != null && DateFormatHelper.isDate1BeforeThenDate2(endDate!!, tempStartDate)) {
            Toast.makeText(
                requireContext(),
                "End date can not be less then start date!!",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            startDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)
            binding.tvStartDate.text =
                DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)
        }
    }

    private fun updateEndDate() {
        val tempEndDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)

        if (startDate != null && DateFormatHelper.isDate1BeforeThenDate2(
                tempEndDate!!,
                startDate
            )
        ) {
            Toast.makeText(
                requireContext(),
                "End date can not be less then start date!!",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            endDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)
            binding.tvEndDate.text =
                DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)
        }
    }

    override fun onCreateDuplicateBeatPlan() {
        createDuplicateBeatPlanWithDates()
    }

}