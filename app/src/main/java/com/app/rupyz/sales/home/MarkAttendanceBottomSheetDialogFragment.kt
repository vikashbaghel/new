package com.app.rupyz.sales.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.camera2.CameraCharacteristics
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetMarkAttendanceBinding
import com.app.rupyz.dialog.MockLocationDetectedDialogFragment
import com.app.rupyz.dialog.UploadDataWithImageDialogFragment
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.PaginationScrollListener
import com.app.rupyz.generic.helper.getBatteryInformation
import com.app.rupyz.generic.helper.getDeviceInformation
import com.app.rupyz.generic.helper.isBatteryOptimizationEnabled
import com.app.rupyz.generic.helper.isGpsEnabled
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity.Companion.hasInternetConnection
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.InformationDialog
import com.app.rupyz.generic.utils.LocationPermissionUtils
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.generic.utils.generateUniqueId
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.SaveAttendanceModel
import com.app.rupyz.model_kt.UploadingActionModel
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.sales.staff.StaffViewModel
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

class MarkAttendanceBottomSheetDialogFragment(
) : BottomSheetDialogFragment(), StaffListForJointActivityAdapter.IAssignStaffListener,
    SelfieListForAttendanceAdapter.ISelfieActionListener,
    LocationPermissionUtils.ILocationPermissionListener,
    MockLocationDetectedDialogFragment.IMockLocationActionListener {
    private lateinit var binding: BottomSheetMarkAttendanceBinding

    private var staffList: ArrayList<StaffData> = ArrayList()
    private var selfieList: ArrayList<String> = ArrayList()

    private lateinit var staffListForAssignAdapter: StaffListForJointActivityAdapter
    private lateinit var selfieAdapter: SelfieListForAttendanceAdapter
    private var locationManager: LocationManager? = null

    private var uploadingActionModel = UploadingActionModel()

    private lateinit var locationPermissionUtils: LocationPermissionUtils

    private val staffViewModel: StaffViewModel by viewModels()
    private val activityViewModel: StaffActivityViewModel by viewModels()
    private val imageUploadViewModel: ImageUploadViewModel by viewModels()

    private val picsIdList: ArrayList<Int> = ArrayList()
    private var addStaffIdSet: ArrayList<Int?>? = ArrayList()

    private var isPageLoading = false
    private var isApiLastPage = false
    private var staffCurrentPage = 1

    private var delay: Long = 500 // 1 seconds after user stops typing

    private var lastTextEdit: Long = 0

    private val photoFileName = "photo.jpg"
    private var photoFile: File? = null
    private val appTag = "RUPYZ"

    private var outputFileUri: Uri? = null

    private var rootView: View? = null

    private val uploadingFragment = UploadDataWithImageDialogFragment()

    private var attendanceType = AppConstant.ATTENDANCE_START_DAY
    private var activityType = ""

    private var isFirstTimeLoadStaff = true
    private var isJointActivityAvailable = true
    private val addAttendanceModel = AddCheckInOutModel()
    private var geoAddress: String = ""
    private var searchText = ""
    private var isAttached = false

    private var isPhotoMandateEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetMarkAttendanceBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        var listener: IStartDayActionListener? = null
        private var geoLocationLat: Double = 0.00
        private var geoLocationLong: Double = 0.00

        fun getInstance(
            listener: IStartDayActionListener?,
            geoLocationLat: Double,
            geoLocationLong: Double,
        ): MarkAttendanceBottomSheetDialogFragment {
            this.listener = listener
            this.geoLocationLat = geoLocationLat
            this.geoLocationLong = geoLocationLong
            return MarkAttendanceBottomSheetDialogFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG.not()
            && SharedPref.getInstance()
                .getBoolean(
                    SharePrefConstant.DISABLE_SCREENSHOT_ON_PRODUCTS,
                    false
                )
        ) {
            dialog?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        isCancelable = false
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationManager =
            this.context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationPermissionUtils = LocationPermissionUtils(this, requireActivity())

        if (arguments?.getBoolean(AppConstant.ATTENDANCE_END_DAY) != null
            && arguments?.getBoolean(AppConstant.ATTENDANCE_END_DAY, false) == true
        ) {
            attendanceType = AppConstant.ATTENDANCE_END_DAY
            binding.clActivityTypeStartDay.visibility = View.GONE
            binding.clActivityTypeEndDay.visibility = View.VISIBLE
            binding.tvHeading.text = resources.getString(R.string.how_was_your_day)

        } else {
            binding.clActivityTypeStartDay.visibility = View.VISIBLE
            binding.clActivityTypeEndDay.visibility = View.GONE
            binding.tvHeading.text = resources.getString(R.string.what_is_your_day_plan)
        }

        binding.tvDistributorVisit.text = resources.getString(
            R.string.distributor_visit,
            SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1),
            SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
        )

        isPhotoMandateEnabled =
            if (arguments?.getBoolean(AppConstant.ATTENDANCE_END_DAY) != null && arguments?.getBoolean(
                    AppConstant.ATTENDANCE_END_DAY,
                    false
                ) == true
            ) {
                SharedPref.getInstance()
                    .getBoolean(AppConstant.END_DAY_PHOTO_MANDATE, false)
            } else {
                SharedPref.getInstance()
                    .getBoolean(AppConstant.START_DAY_PHOTO_MANDATE, false)
            }

        if (isPhotoMandateEnabled) {
            val selfieText = getString(R.string.let_us_take_selfie_with_photo_mandate)
            // Find the position of the star (*)
            val starIndex = selfieText.indexOf("*")

            // Create a SpannableString to apply custom styles
            val spannable = SpannableString(selfieText)

            // Apply red color to the star (*)
            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#ef5350")),
                starIndex,
                starIndex + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Set the formatted text to the TextView
            binding.hdTakeSelfie.text = spannable
        }

        if (isAdded) {
            requireActivity().let {
                GeoLocationUtils.getAddress(
                    it,
                    latitude = geoLocationLat,
                    longitude = geoLocationLong
                ) { address ->
                    geoAddress = address ?: ""
                }
            }
        }

        binding.etComment.clearFocus()
        binding.etSearchStaff.clearFocus()

        setUpListener()

        initRecyclerView()
        initObservers()

        initStaffSearch()

        binding.scrollView.isNestedScrollingEnabled = true
        if (attendanceType == AppConstant.ATTENDANCE_START_DAY) {
            loadStaff()
        }

        binding.ivBack.setOnClickListener { dismiss() }

        binding.tvRegularBeat.setOnClickListener {
            activityType = AppConstant.ACTIVITY_TYPE_REGULAR_BEAT
            changeActivityBackground()
            setActivityView(AppConstant.BEAT)
            hideKeyboard()
            addStaffIdSet?.clear()
        }

        binding.tvJointActivity.setOnClickListener {
            activityType = AppConstant.ACTIVITY_TYPE_JOINT_ACTIVITY
            changeActivityBackground()
            setActivityView(AppConstant.ACTIVITY)
            binding.clStaffForJointActivity.visibility = View.VISIBLE

            staffCurrentPage = 1
            loadStaff()
        }


        binding.tvJointStaffActivityWithCount.setOnClickListener {
            if (binding.clStaffListForAssign.visibility == View.VISIBLE) {
                binding.clStaffListForAssign.visibility = View.GONE
            } else {
                binding.clStaffListForAssign.visibility = View.VISIBLE
            }

            hideKeyboard()
        }

        var isCommentOpen = false
        binding.clWriteComment.setOnClickListener {
            if (isCommentOpen) {
                binding.etComment.visibility = View.GONE
                isCommentOpen = false
                binding.ivWriteComment.setImageResource(R.drawable.ic_add)
            } else {
                binding.etComment.visibility = View.VISIBLE
                binding.ivWriteComment.setImageResource(R.drawable.ic_remove)
                isCommentOpen = true
            }
        }

        binding.tvMarkLeave.setOnClickListener {
            activityType = AppConstant.ACTIVITY_TYPE_MARK_LEAVE
            changeActivityBackground()
            setActivityView(AppConstant.ATTENDANCE_TYPE_CASUAL_LEAVE)
            hideKeyboard()
            addStaffIdSet?.clear()
        }

        binding.tvHOVisit.setOnClickListener {
            activityType = AppConstant.ACTIVITY_TYPE_OFFICE_VISIT
            changeActivityBackground()
            setActivityView(AppConstant.OFFICE_VISIT)
            hideKeyboard()
            addStaffIdSet?.clear()
        }

        binding.tvDistributorVisit.setOnClickListener {
            activityType = AppConstant.ACTIVITY_TYPE_DISTRIBUTOR_VISIT
            changeActivityBackground()
            setActivityView(AppConstant.DISTRIBUTOR)
            hideKeyboard()
            addStaffIdSet?.clear()
        }

        binding.tvOthersActivity.setOnClickListener {
            activityType = AppConstant.ACTIVITY_TYPE_OTHERS
            changeActivityBackground()
            setActivityView(AppConstant.OTHERS)
            hideKeyboard()
            addStaffIdSet?.clear()
        }

        binding.tvHalfDay.setOnClickListener {
            activityType = AppConstant.ACTIVITY_TYPE_HALF_DAY
            changeActivityBackground()
            setActivityView(AppConstant.ATTENDANCE_HALF_DAY)
            hideKeyboard()
            addStaffIdSet?.clear()
        }

        binding.tvFullDay.setOnClickListener {
            activityType = AppConstant.ACTIVITY_TYPE_FULL_DAY
            changeActivityBackground()
            setActivityView(AppConstant.ATTENDANCE_FULL_DAY)
            hideKeyboard()
            addStaffIdSet?.clear()
        }

        binding.hdTakeSelfie.setOnClickListener {
            if (selfieList.size <= 5) {
                checkCameraPermission()
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_more_then_6_pics), Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        binding.buttonAddToCart.setOnClickListener {
            if (activityType.isEmpty()) {
                Toast.makeText(requireContext(), "Please select any activity!!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (isPhotoMandateEnabled && selfieAdapter.itemCount == 0) {
                    if (isAdded) {
                        InformationDialog.showDialog(
                            requireContext(),
                            resources.getString(R.string.selfie_required),
                            getString(R.string.please_upload_a_selfie_to_complete_your_attendance)
                        )
                    }
                } else {
                    if (hasInternetConnection(requireContext())) {
                        uploadingFragment.show(
                            parentFragmentManager,
                            UploadDataWithImageDialogFragment::class.java.name
                        )

                        uploadingActionModel.type = AppConstant.ATTENDANCE

                        if (selfieList.isNotEmpty()) {
                            uploadingActionModel.imageExist = true
                            uploadingActionModel.imageCount = selfieList.size
                        }

                        Handler(Looper.myLooper()!!).postDelayed({
                            uploadingFragment.setListener(uploadingActionModel)
                        }, 300)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.no_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (selfieList.isEmpty().not()) {
                        if (hasInternetConnection(requireContext())) {
                            selfieList.forEach { selfieItem ->
                                lifecycleScope.launch {
                                    val compressedImageFile = Compressor.compress(
                                        requireContext(),
                                        File(selfieItem)
                                    ) {
                                        quality(100)
                                        size(197_152)
                                    }
                                    imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                                }
                            }
                        } else {
                            val pics: ArrayList<PicMapModel> = ArrayList()
                            selfieList.forEach {
                                pics.add(PicMapModel(generateUniqueId(), it))
                            }

                            if (attendanceType == AppConstant.ATTENDANCE_START_DAY) {
                                addAttendanceModel.startDayImagesInfo = pics
                            } else if (attendanceType == AppConstant.ATTENDANCE_END_DAY) {
                                addAttendanceModel.endDayImagesInfo = pics
                            }

                            addAttendance()
                        }
                    } else {
                        addAttendance()
                    }
                }
            }
        }

        binding.ivBack.setOnClickListener {
            listener?.onDismissDialogForStartDay()
            dismiss()
        }
    }

    private fun initObservers() {
        staffViewModel.getStaffListData().observe(this) { data ->
            binding.progressBarStaffListPagination.visibility = View.GONE
            isPageLoading = false
            if (data.data.isNullOrEmpty().not()) {

                isJointActivityAvailable = true
                binding.tvJointActivity.isEnabled = true
                if (activityType == AppConstant.ACTIVITY_TYPE_JOINT_ACTIVITY)
                    setActivityView(AppConstant.ACTIVITY)
                binding.tvNoStaffFound.visibility = View.GONE
                isPageLoading = false

                if (staffCurrentPage == 1) {
                    staffList.clear()
                }

                if (addStaffIdSet.isNullOrEmpty().not()) {
                    data.data?.forEach {
                        if (addStaffIdSet!!.contains(it.id)) {
                            it.isSelected = true
                            staffList.add(it)
                        } else {
                            staffList.add(it)
                        }
                    }
                } else {
                    staffList.addAll(data.data!!)
                }

                staffListForAssignAdapter.notifyDataSetChanged()


                if (data.data!!.size < 30) {
                    isApiLastPage = true
                }

                isFirstTimeLoadStaff = false
            } else {
                isApiLastPage = true
                if (staffCurrentPage == 1 && searchText.isBlank()) {
                    isJointActivityAvailable = false
                    binding.tvNoStaffFound.visibility = View.VISIBLE
                    binding.tvJointActivity.isEnabled = false
                    binding.tvJointActivity.setBackgroundResource(R.drawable.gray_right_top_corner_10dp_bg)
                }
            }
        }

        activityViewModel.addAttendanceLiveData.observe(requireActivity()) {
            if (isAdded) {
                if (it.error == true) {
                    if (it.errorCode == 403) {
                        Utility(requireContext()).logout()
                    } else {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }

                    if (hasInternetConnection(requireContext())) {
                        uploadingFragment.dismissOnError()
                    }
                    dismiss()

                } else {
                    if (hasInternetConnection(requireContext())) {
                        uploadingFragment.completeApiUploading()
                    }

                    if (attendanceType == AppConstant.ATTENDANCE_START_DAY) {
                        SharedPref.getInstance().putLong(
                            AppConstant.START_DAY_TIME,
                            Calendar.getInstance().timeInMillis
                        )
                        SharedPref.getInstance().putBoolean(AppConstant.START_DAY, true)

                        SharedPref.getInstance().putModelClass(
                            AppConstant.SAVE_ATTENDANCE_PREF,
                            SaveAttendanceModel(
                                date = DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time),
                                checkIn = true,
                                checkOut = null,
                                attendanceType
                            )
                        )
                    } else {
                        SharedPref.getInstance().putLong(
                            AppConstant.END_DAY_TIME,
                            Calendar.getInstance().timeInMillis
                        )
                        SharedPref.getInstance().putModelClass(
                            AppConstant.SAVE_ATTENDANCE_PREF,
                            SaveAttendanceModel(
                                date = DateFormatHelper.convertDateToIsoFormat(Calendar.getInstance().time),
                                checkIn = null,
                                checkOut = true
                            )
                        )
                    }
                    dismiss()
                    listener?.onSuccessfullyMarkAttendance()
                }
            }
        }

        imageUploadViewModel.getCredLiveData().observe(this) { model ->
            if (model.error == false) {
                model.data?.let { data ->
                    if (data.id != null) {
                        picsIdList.add(data.id?.toInt()!!)
                        if (selfieList.size == picsIdList.size) {
                            uploadingFragment.completeImageUploading()
                            addAttendance()
                        }
                    }
                }
            } else {
                uploadingFragment.dismissOnError()
                Toast.makeText(requireContext(), "" + model.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addAttendance() {
        if (attendanceType == AppConstant.ATTENDANCE_START_DAY) {
            addAttendanceModel.action = AppConstant.ATTENDANCE_CHECK_IN

            addAttendanceModel.activityType = activityType

            if (picsIdList.isNotEmpty()) {
                addAttendanceModel.startDayImages = picsIdList
            }

            if (addStaffIdSet.isNullOrEmpty().not()) {
                addAttendanceModel.jointStaffIds = addStaffIdSet
            }

            if (binding.etComment.text.toString().isNotEmpty()) {
                addAttendanceModel.startDayComments = binding.etComment.text.toString()
            }
        } else {
            addAttendanceModel.action = AppConstant.ATTENDANCE_CHECK_OUT
            addAttendanceModel.attendanceType = activityType

            if (picsIdList.isNotEmpty()) {
                addAttendanceModel.endDayImages = picsIdList
            }

            if (binding.etComment.text.toString().isNotEmpty()) {
                addAttendanceModel.endDayComments = binding.etComment.text.toString()
            }
        }
        addAttendanceModel.geoLocationLong = geoLocationLong
        addAttendanceModel.geoLocationLat = geoLocationLat
        addAttendanceModel.geoAddress = geoAddress

        addAttendanceModel.deviceInformation = requireContext().getDeviceInformation()
        addAttendanceModel.batteryPercent = requireContext().getBatteryInformation().first
        addAttendanceModel.batteryOptimisation = requireContext().isBatteryOptimizationEnabled()
        addAttendanceModel.locationPermission = requireContext().isGpsEnabled()


        activityViewModel.addAttendance(addAttendanceModel, hasInternetConnection(requireContext()))
    }

    private fun initStaffSearch() {

        val handler = Handler(Looper.myLooper()!!)

        binding.etSearchStaff.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                staffCurrentPage = 1
                isApiLastPage = false
                isPageLoading = true
                staffList.clear()
                loadStaff()
                binding.tvNoStaffFound.visibility = View.GONE
                Utils.hideKeyboard(requireActivity())
                return@setOnEditorActionListener true
            }
            false
        }

        binding.etSearchStaff.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(inputFinishCheckerForStaff);
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
                if (s.toString().length > 2) {
                    lastTextEdit = System.currentTimeMillis();
                    handler.postDelayed(inputFinishCheckerForStaff, delay);
                }

                if (s.isNullOrBlank()) {
                    staffCurrentPage = 1
                    isApiLastPage = false
                    staffList.clear()
                    staffListForAssignAdapter.notifyDataSetChanged()
                    loadStaff()
                }

                if (s.toString().isNotEmpty()) {
                    binding.ivClearSearchStaff.visibility = View.VISIBLE
                    loadStaff()
                } else {
                    binding.ivClearSearchStaff.visibility = View.GONE
                }
            }
        })

        binding.ivClearSearchStaff.setOnClickListener {
            binding.etSearchStaff.setText("")
            staffCurrentPage = 1
            isApiLastPage = false
            staffList.clear()
            staffListForAssignAdapter.notifyDataSetChanged()
            loadStaff()
        }
    }

    private val inputFinishCheckerForStaff = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            staffCurrentPage = 1
            isApiLastPage = false
            isPageLoading = true
            staffList.clear()
            binding.tvNoStaffFound.visibility = View.GONE
            staffListForAssignAdapter.notifyDataSetChanged()
            loadStaff()
        }
    }

    private fun loadStaff() {
        binding.progressBarStaffListPagination.visibility = View.VISIBLE

        staffViewModel.getStaffList(
            "", binding.etSearchStaff.text.toString(),
            staffCurrentPage, hasInternetConnection(requireContext())
        )
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvStaffListForAssign.layoutManager = linearLayoutManager
        staffListForAssignAdapter = StaffListForJointActivityAdapter(staffList, this)
        binding.rvStaffListForAssign.adapter = staffListForAssignAdapter


        binding.rvStaffListForAssign.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isPageLoading = true
                staffCurrentPage += 1
                loadStaff()
            }

            override fun isLastPage(): Boolean {
                return isApiLastPage
            }

            override fun isLoading(): Boolean {
                return isPageLoading
            }
        })

        val linearLayoutManager2 =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSelfieList.layoutManager = linearLayoutManager2
        selfieAdapter = SelfieListForAttendanceAdapter(selfieList, this)
        binding.rvSelfieList.adapter = selfieAdapter

    }

    private fun setActivityView(type: String) {
        when (type) {
            AppConstant.BEAT -> {
                binding.tvRegularBeat.setBackgroundResource(R.drawable.blue_stroke_let_top_corner_10dp_empty_bg)
                binding.tvRegularBeat.setTextColor(resources.getColor(R.color.theme_purple))
                binding.ivRegularBeat.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.theme_purple)
                )
                binding.clStaffListForAssign.visibility = View.GONE
                if (binding.etSearchStaff.text.isNullOrBlank().not()) {
                    binding.etSearchStaff.setText("")
                }
            }

            AppConstant.ACTIVITY -> {
                binding.tvJointActivity.setBackgroundResource(R.drawable.blue_stroke_right_top_corner_10dp_empty_bg)
                binding.tvJointActivity.setTextColor(resources.getColor(R.color.theme_purple))
                binding.ivJointActivity.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.theme_purple)
                )
            }

            AppConstant.ATTENDANCE_TYPE_CASUAL_LEAVE -> {
                binding.tvMarkLeave.setBackgroundResource(R.drawable.blue_stroke_1_dp_empty_bg)
                binding.tvMarkLeave.setTextColor(resources.getColor(R.color.theme_purple))
                binding.ivMarkLeave.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.theme_purple)
                )
                binding.clStaffListForAssign.visibility = View.GONE
                if (binding.etSearchStaff.text.isNullOrBlank().not()) {
                    binding.etSearchStaff.setText("")
                }
            }

            AppConstant.OFFICE_VISIT -> {
                binding.tvHOVisit.setBackgroundResource(R.drawable.blue_stroke_1_dp_empty_bg)
                binding.tvHOVisit.setTextColor(resources.getColor(R.color.theme_purple))
                binding.ivVisitOffice.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.theme_purple)
                )
                binding.clStaffListForAssign.visibility = View.GONE
                if (binding.etSearchStaff.text.isNullOrBlank().not()) {
                    binding.etSearchStaff.setText("")
                }
            }

            AppConstant.DISTRIBUTOR -> {
                binding.tvDistributorVisit.setBackgroundResource(R.drawable.blue_stroke_left_bottom_corner_10dp_empty_bg)
                binding.tvDistributorVisit.setTextColor(resources.getColor(R.color.theme_purple))
                binding.ivDistributorVisit.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.theme_purple)
                )
                binding.clStaffListForAssign.visibility = View.GONE
                if (binding.etSearchStaff.text.isNullOrBlank().not()) {
                    binding.etSearchStaff.setText("")
                }
            }

            AppConstant.OTHERS -> {
                binding.tvOthersActivity.setBackgroundResource(R.drawable.blue_stroke_right_bottom_corner_10dp_empty_bg)
                binding.tvOthersActivity.setTextColor(resources.getColor(R.color.theme_purple))
                binding.ivOthers.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.theme_purple)
                )
                binding.clStaffListForAssign.visibility = View.GONE
                if (binding.etSearchStaff.text.isNullOrBlank().not()) {
                    binding.etSearchStaff.setText("")
                }
            }

            AppConstant.ATTENDANCE_HALF_DAY -> {
                binding.tvHalfDay.setBackgroundResource(R.drawable.blue_stroke_left_rounded_corner_10dp_empty_bg)
                binding.tvHalfDay.setTextColor(resources.getColor(R.color.theme_purple))
                binding.ivHalfDay.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.theme_purple)
                )
                binding.clStaffListForAssign.visibility = View.GONE
                if (binding.etSearchStaff.text.isNullOrBlank().not()) {
                    binding.etSearchStaff.setText("")
                }
            }

            AppConstant.ATTENDANCE_FULL_DAY -> {
                binding.tvFullDay.setBackgroundResource(R.drawable.blue_stroke_right_rounded_corner_10dp_empty_bg)
                binding.tvFullDay.setTextColor(resources.getColor(R.color.theme_purple))
                binding.ivFullDay.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.theme_purple)
                )
                binding.clStaffListForAssign.visibility = View.GONE
                if (binding.etSearchStaff.text.isNullOrBlank().not()) {
                    binding.etSearchStaff.setText("")
                }
            }
        }
    }

    private fun changeActivityBackground() {
        binding.clStaffForJointActivity.visibility = View.GONE

        binding.tvRegularBeat.setBackgroundResource(R.drawable.gray_stroke_let_top_corner_10dp_empty_bg)
        binding.tvRegularBeat.setTextColor(resources.getColor(R.color.leve_text_color))
        binding.ivRegularBeat.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(R.color.toogle_color)
        )

        if (isJointActivityAvailable) {
            binding.tvJointActivity.setBackgroundResource(R.drawable.gray_stroke_right_top_corner_10dp_empty_bg)
        } else {
            if (searchText.isBlank().not()) {
                binding.tvJointActivity.setBackgroundResource(R.drawable.gray_stroke_right_top_corner_10dp_empty_bg)
            } else {
                binding.tvJointActivity.setBackgroundResource(R.drawable.gray_right_top_corner_10dp_bg)
            }
        }
        binding.tvJointActivity.setTextColor(resources.getColor(R.color.leve_text_color))
        binding.ivJointActivity.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(R.color.toogle_color)
        )

        binding.tvMarkLeave.setBackgroundResource(R.drawable.gray_stroke_half_dp_empty_bg)
        binding.tvMarkLeave.setTextColor(resources.getColor(R.color.leve_text_color))
        binding.ivMarkLeave.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(R.color.toogle_color)
        )
        binding.tvHOVisit.setBackgroundResource(R.drawable.gray_stroke_half_dp_empty_bg)
        binding.tvHOVisit.setTextColor(resources.getColor(R.color.leve_text_color))
        binding.ivVisitOffice.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(R.color.toogle_color)
        )

        binding.tvDistributorVisit.setBackgroundResource(R.drawable.gray_stroke_left_bottom_corner_10dp_empty_bg)
        binding.tvDistributorVisit.setTextColor(resources.getColor(R.color.leve_text_color))
        binding.ivDistributorVisit.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(R.color.toogle_color)
        )

        binding.tvOthersActivity.setBackgroundResource(R.drawable.gray_stroke_right_bottom_corner_10dp_empty_bg)
        binding.tvOthersActivity.setTextColor(resources.getColor(R.color.leve_text_color))
        binding.ivOthers.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(R.color.toogle_color)
        )

        binding.tvHalfDay.setBackgroundResource(R.drawable.gray_stroke_left_rounded_corner_10dp_empty_bg)
        binding.tvHalfDay.setTextColor(resources.getColor(R.color.leve_text_color))
        binding.ivHalfDay.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(R.color.toogle_color)
        )

        binding.tvFullDay.setBackgroundResource(R.drawable.gray_stroke_right_rounded_corner_10dp_empty_bg)
        binding.tvFullDay.setTextColor(resources.getColor(R.color.leve_text_color))
        binding.ivFullDay.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(R.color.toogle_color)
        )
    }

    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
            )
        } else {
            activityResultLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var isGranted = false

            permissions.entries.forEach {
                isGranted = it.value
            }

            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera Permission is required to perform this action.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(
            "android.intent.extras.CAMERA_FACING",
            CameraCharacteristics.LENS_FACING_FRONT
        )
        // Create a File reference for future access
        photoFile = getPhotoFileUri(System.currentTimeMillis().toString() + "-" + photoFileName)

        if (this.photoFile != null) {
            outputFileUri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile!!
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)

            try {
                if (isAttached && isDetached.not()) {
                    resultCameraLauncher.launch(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    private fun setUpListener() {
        dialog?.setOnShowListener { dialog ->
            val mDialog = dialog as BottomSheetDialog
            val bottomSheetView =
                mDialog.findViewById<View>(com.denzcoskun.imageslider.R.id.design_bottom_sheet)
            var bottomSheetBehavior: BottomSheetBehavior<View>

            if (bottomSheetView != null) {
                BottomSheetBehavior.from(bottomSheetView).state =
                    BottomSheetBehavior.STATE_EXPANDED;
            }

            bottomSheetView?.let {
//                val windowHeight = DimenUtils.getWindowVisibleHeight(activity as AppCompatActivity)
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
                bottomSheetBehavior.isDraggable = false
                bottomSheetBehavior.isHideable = false


                // Ensure focus is not on an EditText to prevent the keyboard from opening
                it.clearFocus()

                // Hide the keyboard
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                val currentFocus = requireActivity().currentFocus
                currentFocus?.let { view ->
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
    }

    private var resultCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val path = photoFile?.absolutePath
                path?.let {
                    selfieList.add(path)
                    selfieAdapter.notifyItemInserted(selfieList.size - 1)
                    binding.rvSelfieList.visibility = View.VISIBLE
                }
            }
        }

    private fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), appTag)

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            log("failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    interface IStartDayActionListener {
        fun onDismissDialogForStartDay() {}
        fun onSuccessfullyMarkAttendance() {}
        fun requiredBackgroundLocationFromPopUp() {}
    }

    override fun selectStaffForJointActivity(checked: Boolean, model: StaffData?) {
        if (!isFirstTimeLoadStaff) {
            if (checked) {
                addStaffIdSet?.add(model?.id)
            } else {
                if (addStaffIdSet.isNullOrEmpty().not()) {
                    val index = addStaffIdSet?.indexOfLast { it == model?.id }
                    if (index != -1) {
                        addStaffIdSet?.removeAt(index!!)
                    }
                }
            }
        }
    }

    override fun onRemoveSelfie(position: Int) {
        selfieList.removeAt(position)
        selfieAdapter.notifyItemRemoved(position)
        selfieAdapter.notifyItemRangeChanged(position, selfieList.size - 1)
        if (selfieList.size == 0) {
            binding.rvSelfieList.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val view = requireActivity().currentFocus
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}