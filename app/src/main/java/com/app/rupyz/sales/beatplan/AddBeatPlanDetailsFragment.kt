package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.R
import com.app.rupyz.databinding.FragmentBeatPlanDetailsBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.model_kt.CreateBeatRoutePlanModel
import com.app.rupyz.model_kt.OrgBeatModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddBeatPlanDetailsFragment : BaseFragment(),
        CreateDailyBeatPlanDetailsListAdapter.CreateBeatListener, CancelBeatDayBottomSheetDialogFragment.ICancelBeatDayListener {
    private lateinit var binding: FragmentBeatPlanDetailsBinding
    private lateinit var adapter: CreateDailyBeatPlanDetailsListAdapter
    private var dailyList: ArrayList<BeatRouteDayListModel> = ArrayList()
    private val beatList = ArrayList<OrgBeatModel>()
    private lateinit var beatViewModel: BeatViewModel
    private var mStartDateSetListener: DatePickerDialog.OnDateSetListener? = null

    private var currentPage = 1

    private val cal = Calendar.getInstance()
    private val year = cal[Calendar.YEAR]
    private val month = cal[Calendar.MONTH]
    private val myCalendar = Calendar.getInstance()
    private val day = cal[Calendar.DAY_OF_MONTH]

    private var startDate: String? = null
    private var dailyBeatModelPositionForChooseCustomer = -1
    private var cancelDatePosition = -1
    private val selectDayBeat = BeatCustomerResponseModel()

    companion object {
        private lateinit var listener: CreateBeatPlanListener
        private lateinit var beatPlanModel: CreateBeatRoutePlanModel
        private var beatId: Int = 0
        private var isDuplicateBeatPlan = false

        fun newInstance(
                createBeatListener: CreateBeatPlanListener,
                beatId: Int,
                model: CreateBeatRoutePlanModel,
                isDuplicateBeatPlan: Boolean
        ): AddBeatPlanDetailsFragment {
            listener = createBeatListener
            this.beatId = beatId
            beatPlanModel = model
            this.isDuplicateBeatPlan = isDuplicateBeatPlan
            return AddBeatPlanDetailsFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentBeatPlanDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beatViewModel = ViewModelProvider(this)[BeatViewModel::class.java]


        selectDayBeat.addCustomer = ArrayList()
        selectDayBeat.removeCustomer = ArrayList()
        selectDayBeat.selectAllCustomer = true
        selectDayBeat.deSelectAllCustomer = false
        selectDayBeat.subLevelSet = ArrayList()

        mStartDateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = month
                    myCalendar[Calendar.DAY_OF_MONTH] = day
                    updateStartDate()
                }

        if (beatPlanModel.name.isNullOrEmpty().not()) {
            binding.tvName.text = beatPlanModel.name
        }
        if (beatPlanModel.startDate.isNullOrEmpty().not() && beatPlanModel.endDate.isNullOrEmpty()
                        .not()
        ) {
            binding.tvDate.text = "${
                DateFormatHelper.convertStringToCustomDateFormat(
                        beatPlanModel.startDate,
                        SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )
            } - ${
                DateFormatHelper.convertStringToCustomDateFormat(
                        beatPlanModel.endDate,
                        SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )
            }"
        }

        initRecyclerView()
        initObservers()

        CoroutineScope(Dispatchers.IO).launch {
            if (beatPlanModel.beatRouteDayPlan.isNullOrEmpty().not()) {
                if (isDuplicateBeatPlan) {
                    beatId = 0
                    createDuplicateBeatPlanWithDates()
                } else {
                    beatPlanModel.beatRouteDayPlan?.forEach {
                        val beatPlanModel = it
                        val selectDayBeat = BeatCustomerResponseModel()
                        selectDayBeat.selectAllCustomer = it.allowAllCustomers
                        selectDayBeat.addCustomer = ArrayList()
                        selectDayBeat.removeCustomer = ArrayList()
                        beatPlanModel.selectDayBeat = selectDayBeat
                        beatPlanModel.isUpdate = true
                        dailyList.add(beatPlanModel)
                    }
                }
            }
            else if (beatPlanModel.startDate.isNullOrEmpty()
                            .not() && beatPlanModel.endDate.isNullOrEmpty().not()
            ) {
                val list: List<String> = getDates(beatPlanModel.startDate!!, beatPlanModel.endDate!!)
                list.forEach {
                    val model = BeatRouteDayListModel()
                    model.date = it
                    model.orgBeatList = beatList
                    model.targetCustomersCount = 0
                    model.moduleName = ""
                    model.beatName = ""
                    model.purpose = ""
                    model.nightStay = ""
                    model.selectDayBeat = selectDayBeat
                    dailyList.add(model)
                    updateDate()
                }

                beatPlanModel.beatRouteDayPlan = dailyList
            }
            else if (beatPlanModel.startDate.isNullOrEmpty()
                            .not() && beatPlanModel.endDate.isNullOrEmpty()
            ) {
                val model = BeatRouteDayListModel()
                model.date = beatPlanModel.startDate
                model.orgBeatList = beatList
                model.targetCustomersCount = 0
                model.moduleName = ""
                model.beatName = ""
                model.purpose = ""
                model.nightStay = ""
                model.selectDayBeat = selectDayBeat
                dailyList.add(model)
                updateDate()
            }
            CoroutineScope(Dispatchers.Main).launch {
                initRecyclerView()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            beatViewModel.searchBeat("", currentPage, hasInternetConnection())
        }


        binding.btnCancel.setOnClickListener {
            listener.onCancelCreateBeatPlanHead()
        }

        binding.btnAdd.setOnClickListener {
            validateData()
        }

        binding.clAddADate.setOnClickListener {
            if (dailyList.isEmpty()) {
                openStartDateCalendar()
            } else {
                val model = BeatRouteDayListModel()
                model.date = getNextDates()
                model.targetCustomersCount = 0
                model.orgBeatList = beatList
                model.moduleName = ""
                model.beatName = ""
                model.purpose = ""
                model.nightStay = ""
                model.selectDayBeat = selectDayBeat
                dailyList.add(model)

                adapter.notifyItemInserted(dailyList.size)
                adapter.notifyItemRangeChanged(dailyList.size - 1, dailyList.size)

                updateDate()

                hideKeyboard()
            }
        }
    }

    private fun createDuplicateBeatPlanWithDates() {
        startDate = beatPlanModel.startDate

        val beatDuration = beatPlanModel.beatRouteDayPlan!!.size

        myCalendar.time = DateFormatHelper.convertStringToDate(startDate)
        myCalendar.add(Calendar.DAY_OF_MONTH, beatDuration - 1)

        val endDate = DateFormatHelper.convertDateToIsoFormat(myCalendar.time)

        val list: List<String> = getDates(startDate!!, endDate)

        beatPlanModel.beatRouteDayPlan?.forEachIndexed { index, beatRouteDayListModel ->
            beatRouteDayListModel.date = list[index]
            beatRouteDayListModel.isUpdate = true
            dailyList.add(beatRouteDayListModel)
        }
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


    private fun updateStartDate() {
        startDate = DateFormatHelper.convertDateTo_YYYY_MM_DD_Format(myCalendar.time)
        binding.tvDate.text = DateFormatHelper.convertDateToMonthStringFormat(myCalendar.time)

        beatPlanModel.startDate = startDate

        val model = BeatRouteDayListModel()
        model.date = startDate
        model.orgBeatList = beatList
        model.targetCustomersCount = 0
        model.moduleName = ""
        model.beatName = ""
        model.purpose = ""
        model.nightStay = ""
        model.selectDayBeat = selectDayBeat
        dailyList.add(model)

        adapter.notifyItemInserted(dailyList.size)
        adapter.notifyItemRangeChanged(dailyList.size - 1, dailyList.size)

        updateDate()
    }

    private fun validateData() {
        dailyList.forEach {
            if (it.moduleType.equals(AppConstant.BEAT_TYPE_LOCATION) && it.moduleName.isNullOrEmpty()
                    || it.moduleType.equals(AppConstant.BEAT) && it.beatId == null
            ) {
                if (it.beatId == null) {
                    showToast(
                            "You Enter a Beat which is Not Created Yet for : " + DateFormatHelper.getMonthDate(
                                    it.date
                            )
                    )
                } else {
                    showToast(
                            "Please Complete the Information for Date : " + DateFormatHelper.getMonthDate(
                                    it.date
                            )
                    )
                }
                return
            }
        }

        uploadBeat()
    }

    private fun uploadBeat() {
        Utils.hideKeyboard(requireActivity())

        dailyList.forEach {
            it.orgBeatList = null
        }
        beatPlanModel.beatRouteDayPlan = dailyList


        beatPlanModel.deviceInformation = requireContext().getDeviceInformation()
        beatPlanModel.batteryPercent = requireContext().getBatteryInformation().first
        beatPlanModel.batteryOptimisation = requireContext().isBatteryOptimizationEnabled()
        beatPlanModel.locationPermission = requireContext().isGpsEnabled()


        binding.progressBar.visibility = View.VISIBLE
        beatViewModel.createBeatPlan(beatPlanModel, beatId)
    }

    private fun initRecyclerView() {
        binding.rvDailyPlan.layoutManager = LinearLayoutManager(requireContext())
        adapter = CreateDailyBeatPlanDetailsListAdapter(dailyList, this, beatPlanModel.isActive)
        binding.rvDailyPlan.adapter = adapter
    }


    private fun initObservers() {
        beatViewModel.createBeatPlanLiveData.observe(requireActivity()) {
            binding.progressBar.visibility = View.GONE
            showToast(it.message ?: resources.getString(R.string.something_went_wrong))
            binding.btnAdd.isEnabled = true
            if (it.error == false) {
                listener.successfullyCreatedBeatPlan()
            }
        }

        beatViewModel.orgBeatListLiveData.observe(requireActivity()) {
            if (it.error == false) {
                it.data?.let { beatPlan ->
                    if (beatPlan.isNotEmpty()) {
                        beatList.addAll(beatPlan)
                        if (beatPlan.size == 30) {
                            currentPage++
                            beatViewModel.searchBeat("", currentPage, hasInternetConnection())
                        } else {
                            dailyList.forEachIndexed { index, beatRouteDayListModel ->
                                beatRouteDayListModel.orgBeatList = beatList
                            }

                            if (binding.rvDailyPlan.isComputingLayout.not()){
                                adapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        dailyList.forEachIndexed { index, beatRouteDayListModel ->
                            beatRouteDayListModel.orgBeatList = beatList
                            adapter.notifyItemChanged(index)
                        }
                    }
                }
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

    private fun getNextDates(): String {
        val date: String
        val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        var date1: Date? = null

        if (dailyList.isEmpty().not()) {
            val dateString1 = dailyList[dailyList.size - 1].date
            try {
                date1 = dateString1?.let { df1.parse(it) }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val cal1: Calendar = Calendar.getInstance()
            cal1.time = date1!!

            cal1.add(Calendar.DATE, 1)
            date = df1.format(cal1.time)
            return date
        } else {
            return ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun selectCustomer(
            model: BeatRouteDayListModel,
            position: Int,
            targetCustomerIds: ArrayList<Int>?
    ) {
        hideKeyboard()
        dailyBeatModelPositionForChooseCustomer = position
        someActivityResultLauncher.launch(
                Intent(
                        requireContext(),
                        SelectCustomerForBeatPlanPlanActivity::class.java
                ).putExtra(AppConstant.BEAT, model)
                        .putExtra(
                                AppConstant.TARGET_CUSTOMER,
                                dailyList[dailyBeatModelPositionForChooseCustomer]
                        )
        )
    }

    var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null
                && result.data!!.hasExtra(AppConstant.ALL_BEAT_PLAN)
        ) {
            val model = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data!!.getParcelableExtra(
                        AppConstant.ALL_BEAT_PLAN,
                        BeatRouteDayListModel::class.java
                )
            } else {
                result.data!!.getParcelableExtra(
                        AppConstant.ALL_BEAT_PLAN
                )
            }
            if (model != null) {
                model.isFirstTime = false
                dailyList[dailyBeatModelPositionForChooseCustomer] = model
                adapter.notifyItemChanged(dailyBeatModelPositionForChooseCustomer)
            }
        }
    }

    override fun duplicateDate(model: BeatRouteDayListModel, position: Int) {
        hideKeyboard()

        val duplicateModel = dailyList[position].copy()
        duplicateModel.date = getNextDates()
        if (model.isUpdate == true) {
            duplicateModel.selectDayBeat = selectDayBeat
        }
        if (model.isCancelled == true) {
            duplicateModel.isCancelled = false
            duplicateModel.cancelReason = ""
        }
        duplicateModel.isDuplicate = true


        dailyList.add(duplicateModel)

        adapter.notifyItemInserted(dailyList.size)
        adapter.notifyItemRangeChanged(dailyList.size, dailyList.size + 1)

        updateDate()
    }

    @SuppressLint("SetTextI18n")
    private fun updateDate() {
        var startDate: String? = ""
        var endDate: String? = ""

        dailyList.forEachIndexed { index, beatRouteDayListModel ->
            if (index == 0) {
                startDate = beatRouteDayListModel.date
            }

            if (index == dailyList.size - 1) {
                endDate = beatRouteDayListModel.date
            }
        }

        activity?.runOnUiThread {
            binding.tvDate.text = "${
                DateFormatHelper.convertStringToCustomDateFormat(
                    startDate,
                    SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )
            } - ${
                DateFormatHelper.convertStringToCustomDateFormat(
                    endDate,
                    SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                )
            }"

            if (dailyList.size == 31) {
                binding.clAddADate.visibility = View.GONE
            } else {
                binding.clAddADate.visibility = View.VISIBLE
            }
        }

    }

    override fun holiday(model: BeatRouteDayListModel, position: Int) {
        dailyList[position].moduleType = AppConstant.HOLIDAY
        adapter.notifyItemChanged(position)
    }

    override fun deleteBeat(model: BeatRouteDayListModel, position: Int) {
        dailyList.removeAt(dailyList.size - 1)
        adapter.notifyItemRemoved(dailyList.size - 1)
        adapter.notifyItemRangeChanged(dailyList.size - 1, dailyList.size)
        updateDate()
    }

    override fun cancelDate(model: BeatRouteDayListModel, position: Int) {
        cancelDatePosition = position
        val fragment = CancelBeatDayBottomSheetDialogFragment.newInstance(this, model.date)
        fragment.show(childFragmentManager, AddBeatPlanDetailsFragment::class.java.simpleName)
    }

    override fun onCancelBeatDayWithReason(reason: String) {
        if (cancelDatePosition != -1) {
            dailyList[cancelDatePosition].isCancelled = true
            dailyList[cancelDatePosition].cancelReason = reason
            adapter.notifyItemChanged(cancelDatePosition)
        }
    }
}