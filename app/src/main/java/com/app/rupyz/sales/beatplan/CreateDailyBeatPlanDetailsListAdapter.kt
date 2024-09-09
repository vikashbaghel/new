package com.app.rupyz.sales.beatplan

import android.annotation.SuppressLint
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.rupyz.R
import com.app.rupyz.databinding.CreateBeatPlanDailyItemBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.BeatCustomerResponseModel
import com.app.rupyz.model_kt.BeatRouteDayListModel
import com.app.rupyz.ui.organization.profile.adapter.CustomAutoCompleteAdapter
import java.text.SimpleDateFormat
import java.util.*

class CreateDailyBeatPlanDetailsListAdapter(
    var data: List<BeatRouteDayListModel>,
    var listener: CreateBeatListener,
    var isActive: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.create_beat_plan_daily_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItem(
            data,
            data[position],
            listener,
            isActive,
            position
        )
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MyViewHolder).enableTextWatcher()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MyViewHolder).disableTextWatcher()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = CreateBeatPlanDailyItemBinding.bind(itemView)
        var nightStayTextWatcher: TextWatcher? = null
        var purposeTextWatcher: TextWatcher? = null
        var newVisitTextWatcher: TextWatcher? = null

        @SuppressLint("SetTextI18n")
        fun bindItem(
            data: List<BeatRouteDayListModel>,
            model: BeatRouteDayListModel,
            listener: CreateBeatListener,
            isActive: Boolean,
            position: Int
        ) {
            val selectDayBeat = BeatCustomerResponseModel()
            selectDayBeat.addCustomer = ArrayList()
            selectDayBeat.removeCustomer = ArrayList()
            selectDayBeat.selectAllCustomer = true
            selectDayBeat.deSelectAllCustomer = false
            selectDayBeat.subLevelSet = ArrayList()

            var beatId: Int? = null

            val arrayAdapter = ArrayAdapter(
                itemView.context, R.layout.single_text_view_spinner_16dp_text,
                itemView.resources.getStringArray(R.array.beat_type)
            )

            binding.spinnerBeatType.adapter = arrayAdapter

            var moduleType = ""

            if (model.isCancelled == true) {
                binding.tvCanceledDate.visibility = View.VISIBLE
                binding.tvCancelReason.visibility = View.VISIBLE
                binding.tvCancelReason.text = model.cancelReason

                binding.etBeatName.visibility = View.GONE
                binding.clCustomers.visibility = View.GONE

                binding.groupNightStay.visibility = View.GONE
                binding.groupPurpose.visibility = View.GONE
                binding.acvModuleName.setText("")

                binding.spinnerBeatType.setBackgroundResource(R.drawable.edit_text_disable_background)
                binding.acvModuleName.setBackgroundResource(R.drawable.edit_text_disable_background)
                binding.etNewVisit.setBackgroundResource(R.drawable.edit_text_disable_background)

                binding.spinnerBeatType.isEnabled = false
                binding.etBeatName.isEnabled = false
                binding.clCustomers.isEnabled = false
                binding.etNewVisit.isEnabled = false

                binding.etNightStay.setText("")
                binding.etPurpose.setText("")
                binding.etNewVisit.setText("")

                model.selectDayBeat = selectDayBeat

                binding.spinnerBeatType.onItemSelectedListener = null
            }
            else {

                binding.spinnerBeatType.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
                binding.acvModuleName.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
                binding.etNewVisit.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)

                binding.spinnerBeatType.isEnabled = true
                binding.etBeatName.isEnabled = true
                binding.clCustomers.isEnabled = true
                binding.etNewVisit.isEnabled = true

                binding.tvCanceledDate.visibility = View.GONE
                binding.tvCancelReason.visibility = View.GONE

                when (model.moduleType) {
                    AppConstant.HOLIDAY -> {
                        binding.spinnerBeatType.setSelection(2)
                    }
                    AppConstant.BEAT_TYPE_LOCATION -> {
                        binding.spinnerBeatType.setSelection(1)
                    }
                    AppConstant.BEAT -> {
                        binding.spinnerBeatType.setSelection(0)
                    }
                }

                binding.spinnerBeatType.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            when (position) {
                                0 -> {
                                    binding.acvModuleName.visibility = View.VISIBLE
                                    binding.etBeatName.visibility = View.GONE
                                    binding.clCustomers.visibility = View.VISIBLE
                                    moduleType = AppConstant.BEAT

                                    if (model.beatName.isNullOrEmpty().not()) {
                                        binding.acvModuleName.setText(model.beatName)

                                        binding.ivClearBeatName.visibility = View.VISIBLE
                                        binding.ivDropDown.visibility = View.GONE

                                        val index =
                                            model.orgBeatList?.indexOfFirst { it.name == binding.acvModuleName.text.toString() }
                                        if (index != null && index != -1) {
                                            val orgBeatModel = model.orgBeatList!![index]
                                            beatId = orgBeatModel.id

                                            model.beatId = beatId
                                            model.beatName = orgBeatModel.name

//                                        var customerCount = 0
//
//
//                                        customerCount =
//                                            if (model.isUpdate == true && model.isDuplicate == true) {
//                                                orgBeatModel.customerCount!!
//                                            } else {
//                                                model.targetCustomersCount!!
//                                            }
//
//                                        if (model.selectDayBeat != null) {
//                                            model.selectDayBeat?.let { day ->
//
//                                                if (day.deSelectAllCustomer == true) {
//                                                    customerCount = 0
//                                                }
//                                                if (day.addCustomer.isNullOrEmpty()
//                                                        .not()
//                                                ) {
//                                                    customerCount += day.addCustomer?.size!!
//                                                } else if (day.removeCustomer.isNullOrEmpty()
//                                                        .not()
//                                                ) {
//                                                    customerCount -= day.removeCustomer?.size!!
//                                                }
//
//                                                if (day.subLevelSet.isNullOrEmpty().not()) {
//                                                    customerCount += day.subLevelSet?.size!!
//                                                }
//                                            }
//                                            binding.tvCustomerCount.text =
//                                                itemView.resources.getString(
//                                                    R.string.customer_count_for_beat,
//                                                    "$customerCount"
//                                                )
//                                        } else {
//                                            binding.tvCustomerCount.text =
//                                                itemView.resources.getString(
//                                                    R.string.customer_count_for_beat,
//                                                    "$customerCount"
//                                                )
//                                        }
                                        }
                                        binding.acvModuleName.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
                                    } else {
                                        if (model.orgBeatList.isNullOrEmpty().not()) {
                                            binding.acvModuleName.setText("")
                                            binding.acvModuleName.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
                                        } else {
                                            binding.acvModuleName.setText("No Beat Assign")
                                            binding.acvModuleName.isEnabled = false
                                            binding.acvModuleName.setBackgroundResource(R.drawable.edit_text_disable_background)
                                        }

                                        binding.ivClearBeatName.visibility = View.GONE
                                        binding.ivDropDown.visibility = View.VISIBLE
                                    }

                                    binding.etBeatName.setText("")

                                    enableInputs()
                                    binding.clCustomers.isEnabled = beatId != null

                                    if (binding.clCustomers.isEnabled) {
                                        binding.clCustomers.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
                                    } else {
                                        binding.clCustomers.setBackgroundResource(R.drawable.edit_text_disable_background)
                                    }

                                    model.moduleType = moduleType
                                }

                                1 -> {
                                    binding.acvModuleName.visibility = View.INVISIBLE
                                    binding.etBeatName.visibility = View.VISIBLE
                                    binding.clCustomers.visibility = View.GONE
                                    moduleType = AppConstant.BEAT_TYPE_LOCATION

                                    model.selectDayBeat = selectDayBeat

                                    if (model.moduleName.isNullOrEmpty().not()) {
                                        binding.etBeatName.setText(model.beatName)
                                    } else {
                                        binding.etBeatName.setText("")
                                    }
                                    enableInputs()
                                    model.moduleType = moduleType
                                }

                                2 -> {
                                    binding.acvModuleName.visibility = View.INVISIBLE
                                    binding.etBeatName.visibility = View.VISIBLE
                                    binding.clCustomers.visibility = View.GONE

                                    moduleType = AppConstant.HOLIDAY
                                    model.moduleType = moduleType
                                    model.moduleName = ""
                                    model.beatName = ""
//                                binding.tvCustomerCount.text = itemView.resources.getString(
//                                    R.string.customer_count_for_beat,
//                                    ""
//                                )
                                    model.nightStay = ""
                                    model.purpose = ""
                                    binding.groupNightStay.visibility = View.GONE
                                    binding.groupPurpose.visibility = View.GONE
                                    binding.acvModuleName.setText("")

                                    binding.etBeatName.setBackgroundResource(R.drawable.edit_text_disable_background)
                                    binding.etBeatName.isEnabled = false
                                    binding.etBeatName.setText("")
                                    binding.etNightStay.setBackgroundResource(R.drawable.edit_text_disable_background)
                                    binding.etNightStay.isEnabled = false
                                    binding.etNightStay.setText("")
                                    binding.etPurpose.setBackgroundResource(R.drawable.edit_text_disable_background)
                                    binding.etPurpose.isEnabled = false
                                    binding.etPurpose.setText("")
                                    binding.etNewVisit.setBackgroundResource(R.drawable.edit_text_disable_background)
                                    binding.etNewVisit.isEnabled = false
                                    binding.etNewVisit.setText("")

                                    model.selectDayBeat = selectDayBeat
                                }

                            }
                        }
                    }

                if (model.targetLeadsCount != null && model.targetLeadsCount != 0) {
                    binding.etNewVisit.setText("${model.targetLeadsCount}")
                } else {
                    binding.etNewVisit.setText("")
                }
            }

            if (model.nightStay.isNullOrEmpty().not()) {
                binding.etNightStay.setText("${model.nightStay}")
                binding.groupNightStay.visibility = View.VISIBLE
            } else {
                binding.etNightStay.setText("")
                binding.groupNightStay.visibility = View.GONE
            }


            if (model.purpose.isNullOrEmpty().not()) {
                binding.etPurpose.setText("${model.purpose}")
                binding.groupPurpose.visibility = View.VISIBLE
            } else {
                binding.groupPurpose.visibility = View.GONE
                binding.etPurpose.setText("")
            }

            binding.tvDate.text = DateFormatHelper.convertStringToMonthFormat(model.date)

//            binding.tvCustomerCount.text = itemView.resources.getString(
//                R.string.customer_count_for_beat, ""
//            )

            val beatList: ArrayList<String> = ArrayList()
            model.orgBeatList?.forEach {
                beatList.add(it.name!!)
            }

            val adapter = CustomAutoCompleteAdapter(itemView.context, beatList)
            binding.acvModuleName.threshold = 0
            binding.acvModuleName.setAdapter(adapter)
            binding.acvModuleName.isEnabled = true

            binding.acvModuleName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    beatId = null
                    if (p0.isNullOrEmpty().not() && beatList.isEmpty()
                            .not() && beatList.contains(p0.toString()).not()
                    ) {
                        model.beatId = null
                        model.beatName = null
//                        binding.tvCustomerCount.text =
//                            itemView.resources.getString(
//                                R.string.customer_count_for_beat,
//                                ""
//                            )
                    }
                }
            })

            binding.acvModuleName.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, _, _ ->
                    val index =
                        model.orgBeatList?.indexOfFirst { it.name == binding.acvModuleName.text.toString() }
                    if (index != null && index != -1) {
                        val orgBeatModel = model.orgBeatList!![index]
                        beatId = orgBeatModel.id

                        model.beatId = beatId
                        model.beatName = orgBeatModel.name

//                        binding.tvCustomerCount.text =
//                            itemView.resources.getString(
//                                R.string.customer_count_for_beat,
//                                "${orgBeatModel.customerCount}"
//                            )

                        model.targetCustomersCount = orgBeatModel.customerCount

                        model.selectDayBeat = selectDayBeat

                        binding.ivClearBeatName.visibility = View.VISIBLE
                        binding.ivDropDown.visibility = View.GONE
                    }
                    binding.clCustomers.isEnabled = true
                    binding.clCustomers.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
                }

            binding.acvModuleName.setOnDismissListener {
                if (beatId == null) {
                    binding.clCustomers.isEnabled = false
                    binding.clCustomers.setBackgroundResource(R.drawable.edit_text_disable_background)
                }
            }

            binding.acvModuleName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.acvModuleName.showDropDown()
                }
            }

            binding.acvModuleName.setOnClickListener {
                binding.acvModuleName.showDropDown()
            }

            binding.ivClearBeatName.setOnClickListener {
                binding.acvModuleName.setText("")
                model.beatName = ""
                model.allowAllCustomers = false
                binding.clCustomers.isEnabled = false
                binding.clCustomers.setBackgroundResource(R.drawable.edit_text_disable_background)

                binding.ivClearBeatName.visibility = View.GONE
                binding.ivDropDown.visibility = View.VISIBLE
            }

            nightStayTextWatcher = NightStayTextWatcher(model)
            purposeTextWatcher = PurposeTextWatcher(model)
            newVisitTextWatcher = NewVisitTextWatcher(model)

            binding.etBeatName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(value: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    model.moduleName = value.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            binding.tvCustomerCount.paintFlags =
                binding.tvCustomerCount.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            binding.ivMore.setOnClickListener { v ->
                //creating a popup menu
                val popup =
                    PopupMenu(v.context, binding.ivMore)
                //inflating menu from xml resource
                popup.inflate(R.menu.create_beat_plan_menu)

                if (moduleType == AppConstant.HOLIDAY) {
                    popup.menu.getItem(0).isVisible = false
                    popup.menu.getItem(1).isVisible = false
                } else {
                    popup.menu.getItem(0).isVisible = true
                    popup.menu.getItem(1).isVisible = true
                }

                popup.menu.findItem(R.id.menu_delete_beat).isVisible =
                    position > 0 && position == data.size - 1

                if (model.isUpdate == true
                    && isActive == true
                    && DateFormatHelper.isDate1EqualThenDate2(
                        DateFormatHelper.convertStringToCustomDateFormat(
                            model.date, SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                        ), DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time)
                    )
                ) {
                    popup.menu.findItem(R.id.menu_cancel_date).isVisible = true
                    popup.menu.findItem(R.id.menu_delete_beat).isVisible = false
                } else {
                    popup.menu.findItem(R.id.menu_cancel_date).isVisible = false
                }

                if (model.isCancelled == true){
                    popup.menu.findItem(R.id.menu_night_stay).isVisible = false
                    popup.menu.findItem(R.id.menu_purpose).isVisible = false
                    popup.menu.findItem(R.id.menu_holiday).isVisible = false
                    popup.menu.findItem(R.id.menu_cancel_date).isVisible = false
                    popup.menu.findItem(R.id.menu_delete_beat).isVisible = false
                }

                //adding click listener
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.menu_night_stay -> {
                            binding.groupNightStay.visibility = View.VISIBLE
                            binding.etNightStay.requestFocus()
                            return@setOnMenuItemClickListener true
                        }
                        R.id.menu_purpose -> {
                            binding.groupPurpose.visibility = View.VISIBLE
                            binding.etPurpose.requestFocus()
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_duplicate -> {
                            listener.duplicateDate(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_holiday -> {
                            listener.holiday(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_delete_beat -> {
                            listener.deleteBeat(model, position)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_cancel_date -> {
                            listener.cancelDate(model, position)
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
                //displaying the popup
                popup.show()
            }

            binding.ivClosePurpose.setOnClickListener {
                binding.etPurpose.setText("")
                model.purpose = ""
                binding.groupPurpose.visibility = View.GONE
            }
            binding.ivCloseNightStay.setOnClickListener {
                binding.etNightStay.setText("")
                model.nightStay = ""
                binding.groupNightStay.visibility = View.GONE
            }

            binding.clCustomers.setOnClickListener {
                binding.acvModuleName.clearFocus()
                listener.selectCustomer(model, position, model.targetCustomerIds)
            }
        }

        fun enableInputs() {
            binding.etBeatName.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
            binding.etBeatName.isEnabled = true
            binding.etNightStay.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
            binding.etNightStay.isEnabled = true
            binding.etPurpose.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
            binding.etPurpose.isEnabled = true
            binding.etNewVisit.setBackgroundResource(R.drawable.edit_text_white_with_stroke_background)
            binding.etNewVisit.isEnabled = true
        }

        fun enableTextWatcher() {
            binding.etNightStay.addTextChangedListener(nightStayTextWatcher)
            binding.etNightStay.tag = adapterPosition

            binding.etPurpose.addTextChangedListener(purposeTextWatcher)
            binding.etPurpose.tag = adapterPosition

            binding.etNewVisit.addTextChangedListener(newVisitTextWatcher)
            binding.etNewVisit.tag = adapterPosition
        }

        fun disableTextWatcher() {
            binding.etNightStay.removeTextChangedListener(nightStayTextWatcher)
            binding.etPurpose.removeTextChangedListener(purposeTextWatcher)
            binding.etNewVisit.removeTextChangedListener(newVisitTextWatcher)
        }
    }

    class NightStayTextWatcher(
        private val model: BeatRouteDayListModel
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            model.nightStay = input.toString()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    class PurposeTextWatcher(
        private val model: BeatRouteDayListModel
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            model.purpose = input.toString()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    class NewVisitTextWatcher(
        private val model: BeatRouteDayListModel
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(input: CharSequence, start: Int, before: Int, count: Int) {
            if (input.toString().isEmpty().not()) {
                model.targetLeadsCount = input.toString().toInt()
            } else {
                model.targetLeadsCount = 0
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    interface CreateBeatListener {
        fun selectCustomer(
            model: BeatRouteDayListModel,
            position: Int,
            targetCustomerIds: ArrayList<Int>?
        )

        fun duplicateDate(model: BeatRouteDayListModel, position: Int)
        fun holiday(model: BeatRouteDayListModel, position: Int)
        fun deleteBeat(model: BeatRouteDayListModel, position: Int)
        fun cancelDate(model: BeatRouteDayListModel, position: Int)
    }

}
