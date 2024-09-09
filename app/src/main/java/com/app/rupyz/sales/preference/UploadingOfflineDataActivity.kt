package com.app.rupyz.sales.preference

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityUploadingOfflineDataBinding
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.toast.MessageHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.CheckInOutModel
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.OfflineAttendanceModel
import com.app.rupyz.model_kt.UploadOfflineAttendanceModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class UploadingOfflineDataActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityUploadingOfflineDataBinding

    private val uploadViewModel: UploadingViewModel by viewModels()
    private val offlineViewModel: OfflineViewModel by viewModels()
    private val imageUploadViewModel: ImageUploadViewModel by viewModels()

    private var customerList = ArrayList<CustomerData>()
    private var customerAddressList = ArrayList<CustomerAddressDataItem>()
    private var leadList = ArrayList<LeadLisDataItem>()
    private var orderList = ArrayList<OrderData>()
    private var paymentList = ArrayList<RecordPaymentData>()
    private var customerActivityList = ArrayList<CustomerFollowUpDataItem>()
    private var expenseHeadList = ArrayList<ExpenseTrackerDataItem>()
    private var expenseList = ArrayList<ExpenseDataItem>()
    private var bulkAttendanceList: ArrayList<OfflineAttendanceModel>? = ArrayList()

    private var addedPicList = ArrayList<Int>()
    private var photoModelListForUpload = ArrayList<AddedPhotoModel?>()

    private var uploadingType = ""

    private var uploadingCustomerIndex = 0
    private var uploadingCustomerAddressIndex = 0
    private var uploadingLeadIndex = 0
    private var uploadingPaymentIndex = 0
    private var uploadingOrderIndex = 0
    private var uploadingActivityIndex = 0
    private var uploadingExpenseHeadIndex = 0
    private var uploadingExpenseListIndex = 0
    private var uploadingBulkAttendanceIndex = 0

    private var logoImageId: Int = 0

    private var uploadCustomerModel: CustomerData? = null
    private var uploadCustomerAddressModel: CustomerAddressDataItem? = null
    private var uploadLeadModel: LeadLisDataItem? = null
    private var uploadOrderModel: OrderData? = null
    private var uploadPaymentModel: RecordPaymentData? = null
    private var uploadActivityModel: CustomerFollowUpDataItem? = null
    private var uploadExpenseHeadModel: ExpenseTrackerDataItem? = null
    private var uploadExpenseListModel: ExpenseDataItem? = null
    private var uploadingSingleAttendanceModel: AddCheckInOutModel? = null
    private var uploadingBulkAttendanceModel: UploadOfflineAttendanceModel? = null

    private val bulkAttendanceHashMap: HashMap<Pair<Int, String>, List<Int>> = HashMap()

    private var uploadingDone = false

    private val progressMax = 100 // Maximum value for the progress bar
    private var currentProgress = 0 // Current progress value
    private var progressIncrement = 0
    private var uploadMultipleImageCount = 0
    private var uploadingImageCounter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set FLAG_KEEP_SCREEN_ON to keep the screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityUploadingOfflineDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvCancelUpload.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        offlineViewModel.isOfflineDataAvailable()

        initObservers()
        getOfflineData()
    }

    private fun getOfflineData() {
        offlineViewModel.isAttendanceDataAvailable()
    }

    private fun incrementProgress() {
        if (currentProgress < progressMax) {
            currentProgress += progressIncrement
            if (currentProgress > progressMax) {
                currentProgress = progressMax
                uploadingDone = true
            }
            binding.downloadProgressBar.progress = currentProgress
        }
    }

    private fun initObservers() {
        offlineViewModel.offlineDataAvailableLiveData.observe(this) { data ->
            log("total available size = ${data.second}")
            if (data.second > 0) {
                progressIncrement = progressMax / data.second
            }
        }

        offlineViewModel.attendanceLiveData.observe(this) {
            if (it.data.isNullOrEmpty().not()) {
                madeAttendanceData(it.data!!)
            } else {
                offlineViewModel.getCustomerNotSyncedData()
            }

        }

        offlineViewModel.offlineCustomerLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    customerList.addAll(it.data!!)
                    uploadingCustomerData()
                } else {
                    offlineViewModel.getCustomerAddressNotSyncedData()
                }
            }
        }

        offlineViewModel.offlineAddressLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    customerAddressList.addAll(it.data!!)
                    uploadingCustomerAddressData()
                } else {
                    offlineViewModel.getLeadNotSyncedData()
                }
            }
        }

        offlineViewModel.offlineLeadLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    leadList.addAll(it.data!!)
                    uploadingLeadData()
                } else {
                    offlineViewModel.getOrderNotSyncedData()
                }
            }
        }

        offlineViewModel.offlineOrderLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    orderList.addAll(it.data!!)
                    uploadOrders()
                } else {
                    offlineViewModel.getPaymentNotSyncedData()
                }
            }
        }


        offlineViewModel.offlinePaymentLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    paymentList.addAll(it.data!!)
                    uploadingPayment()
                } else {
                    offlineViewModel.getCustomerActivityListData()
                }
            }
        }

        offlineViewModel.offlineCustomerActivityLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    customerActivityList.addAll(it.data!!)
                    uploadingActivity()
                } else {
                    offlineViewModel.getExpenseHeadListData()
                }
            }
        }

        offlineViewModel.offlineExpenseHeadLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    expenseHeadList.addAll(it.data!!)
                    uploadingExpenseHead()
                } else {
                    offlineViewModel.getExpenseListData()
                }
            }
        }


        offlineViewModel.offlineExpenseListLiveData.observe(this) {
            if (it.error == false) {
                if (it.data.isNullOrEmpty().not()) {
                    expenseList.addAll(it.data!!)
                    uploadingExpenseList()
                } else {
                    finishActivity()
                }
            }
        }

        imageUploadViewModel.getCredLiveData().observe(this) { model ->
            if (model.error != null && model.error!!.not()) {
                model.data?.let { data ->
                    if (data.id != null) {
                        logoImageId = data.id!!.toInt()
                        updateImageInfo()

                        when (uploadingType) {
                            AppConstant.ATTENDANCE,
                            AppConstant.BULK_ATTENDANCE,
                            AppConstant.ACTIVITY,
                            AppConstant.PAYMENT,
                            AppConstant.EXPENSE_LIST,
                            AppConstant.ORDER -> {
                                uploadingImageCounter++
                                uploadMultipleImage(uploadingImageCounter)
                            }
                        }
                    }
                }
            } else {
                showToast(model.message)
            }
        }

        uploadViewModel.addAttendanceLiveData.observe(this) {
            if (it.error == false) {
                uploadViewModel.deleteAttendanceData()
                offlineViewModel.getCustomerNotSyncedData()
            }
        }

        uploadViewModel.addBulkAttendanceLiveData.observe(this) {
            if (it.error == false) {
                uploadViewModel.deleteAttendanceData()
                offlineViewModel.getCustomerNotSyncedData()
            }
        }

        uploadViewModel.customerAddLiveData.observe(this) {
            if (it.error == false) {
                if (it?.data?.id != null) {
                    uploadViewModel.updateCustomerRelatedActivity(uploadCustomerModel?.id!!, it.data.id!!)
                }
            } else {
                showToast(it.message)
                uploadViewModel.updateCustomerError(uploadCustomerModel?.id!!, it.message)
                uploadingCustomerIndex++
                uploadingCustomerData()
            }
        }

        uploadViewModel.updateCustomerLiveData.observe(this) {
            if (it.error == false) {
                uploadingCustomerIndex++
                uploadingCustomerData()
            }
        }

        uploadViewModel.addAddressLiveData.observe(this) {
            if (it.error == false) {
                if (it?.data?.id != null) {
                    uploadViewModel.updateOrderAddressModel(uploadCustomerAddressModel?.id, it.data.id)
                }
            } else {
                showToast(it.message)
                uploadingCustomerAddressIndex++
                uploadingCustomerAddressData()
            }
        }

        uploadViewModel.updateAddressLiveData.observe(this) {
            if (it.error == false) {
                uploadingCustomerAddressIndex++
                uploadingCustomerAddressData()
            }
        }

        uploadViewModel.confirmOrderLiveData.observe(this) {
            if (it.error == false) {
                uploadingOrderIndex++
                uploadViewModel.deleteOfflineOrders(uploadOrderModel?.id)
                uploadOrders()
            } else {
                showToast(it.message)
                uploadingOrderIndex++
                uploadOrders()
            }
        }

        uploadViewModel.addLeadLiveData.observe(this) {
            if (it.error == false) {
                if (it.data?.id != null) {
                    uploadViewModel.updateLeadRelatedActivity(uploadLeadModel?.id!!, it.data?.id!!)
                }
            } else {
                showToast(it.message)
                uploadingLeadIndex++
                uploadingLeadData()
            }
        }

        uploadViewModel.updateLeadLiveData.observe(this) {
            if (it.error == false) {
                uploadingLeadIndex++
                uploadingLeadData()
            }
        }

        uploadViewModel.paymentRecordLiveData.observe(this) {
            if (it.error == false) {
                uploadViewModel.deletePaymentRecords(uploadPaymentModel?.id)
                uploadingPaymentIndex++
                uploadingPayment()
            } else {
                showToast(it.message)
                uploadingPaymentIndex++
                uploadingPayment()
            }
        }

        uploadViewModel.addFeedbackFollowUpLiveData.observe(this) {
            if (it.error == false) {
                uploadViewModel.deleteCustomerActivity(uploadActivityModel?.id)
                uploadingActivityIndex++
                uploadingActivity()
            } else {
                showToast(it.message)
                uploadingActivityIndex++
                uploadingActivity()
            }
        }


        uploadViewModel.addTotalExpenseLiveData.observe(this) {
            if (it.error == false) {
                if (it?.data?.id != null) {
                    uploadViewModel.updateExpenseHeadRelatedModel(uploadExpenseHeadModel?.id!!, it.data?.id!!)
                }
            } else {
                showToast(it.message)
                uploadingExpenseHeadIndex++
                uploadingExpenseHead()
            }
        }

        uploadViewModel.updateExpenseLiveData.observe(this) {
            if (it.error == false) {
                uploadingExpenseHeadIndex++
                uploadingExpenseHead()
            }
        }

        uploadViewModel.addExpenseListLiveData.observe(this) {
            if (it.error == false) {
                uploadViewModel.deleteExpenseList(uploadExpenseListModel?.id)
                uploadingExpenseListIndex++
                uploadingExpenseList()
            } else {
                showToast(it.message)
                uploadingExpenseListIndex++
                uploadingExpenseList()
            }
        }
    }

    private fun madeAttendanceData(list: List<AddCheckInOutModel>) {
        if (list.size > 1) {
            val groupedMapMap: Map<String, List<AddCheckInOutModel>> =
                    list.groupBy { date ->
                        DateFormatHelper.convertStringToCustomDateFormat(
                                date.createdAt!!,
                                SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH)
                        )
                    }

            val attendanceList = ArrayList<OfflineAttendanceModel>()
            val attendanceModel = OfflineAttendanceModel()

            for (date: String in groupedMapMap.keys) {
                attendanceModel.date = date
                val groupItems: List<AddCheckInOutModel>? = groupedMapMap[date]
                groupItems?.forEach { data ->
                    if (data.action == AppConstant.ATTENDANCE_CHECK_IN) {
                        val checkIn = CheckInOutModel()
                        checkIn.timestamp = data.createdAt
                        checkIn.action = data.action
                        checkIn.attendanceType = data.attendanceType
                        checkIn.activityType = data.activityType
                        checkIn.geoLocationLat = data.geoLocationLat
                        checkIn.geoLocationLong = data.geoLocationLong
                        checkIn.startDayImages = data.startDayImages
                        checkIn.startDayComments = data.startDayComments
                        checkIn.startDayImagesInfo = data.startDayImagesInfo
                        checkIn.jointStaffIds = data.jointStaffIds
                        attendanceModel.checkIn = checkIn
                    }

                    if (data.action == AppConstant.ATTENDANCE_CHECK_OUT) {
                        val checkOut = CheckInOutModel()
                        checkOut.timestamp = data.createdAt
                        checkOut.action = data.action
                        checkOut.attendanceType = data.attendanceType
                        checkOut.activityType = data.activityType
                        checkOut.geoLocationLat = data.geoLocationLat
                        checkOut.geoLocationLong = data.geoLocationLong
                        checkOut.endDayImages = data.endDayImages
                        checkOut.endDayImagesInfo = data.endDayImagesInfo
                        checkOut.endDayComments = data.endDayComments
                        attendanceModel.checkOut = checkOut
                    }
                }

                if (attendanceModel.checkOut == null) {
                    val checkOut = CheckInOutModel()
                    attendanceModel.checkOut = checkOut
                }

                attendanceList.add(attendanceModel)
            }

            val uploadAttendanceModel = UploadOfflineAttendanceModel()
            uploadAttendanceModel.attendanceList = attendanceList

            uploadingBulkAttendance(uploadAttendanceModel)
        } else {
            uploadSingleAttendance(list[0])
        }
    }

    private fun uploadingBulkAttendance(uploadAttendanceModel: UploadOfflineAttendanceModel?) {
        uploadingBulkAttendanceModel = uploadAttendanceModel

        uploadingBulkAttendanceModel?.attendanceList?.forEachIndexed { index, offlineAttendanceModel ->
            if (offlineAttendanceModel.checkIn?.startDayImagesInfo.isNullOrEmpty().not()) {
                bulkAttendanceHashMap[Pair(index, AppConstant.ATTENDANCE_CHECK_IN)] = listOf()
            } else if (offlineAttendanceModel.checkOut?.endDayImagesInfo.isNullOrEmpty().not()) {
                bulkAttendanceHashMap[Pair(index, AppConstant.ATTENDANCE_CHECK_OUT)] = listOf()
            }
        }

        if (bulkAttendanceHashMap.isNotEmpty()) {
            bulkAttendanceList = uploadingBulkAttendanceModel?.attendanceList
            uploadingBulkAttendanceModel()
        } else {
            uploadViewModel.addBulkAttendance(uploadAttendanceModel)
        }
    }

    private fun uploadingBulkAttendanceModel() {
        uploadingType = AppConstant.BULK_ATTENDANCE
        if (bulkAttendanceList?.size!! > uploadingBulkAttendanceIndex) {

            val uploadModel = bulkAttendanceList!![uploadingBulkAttendanceIndex]

            if (uploadModel.checkIn?.startDayImagesInfo.isNullOrEmpty().not()
                    && uploadModel.checkIn?.isImageIdListUploaded == null) {
                uploadImagesWithList(uploadModel.checkIn?.startDayImagesInfo)
            } else if (uploadModel.checkOut?.endDayImagesInfo.isNullOrEmpty().not()
                    && uploadModel.checkOut?.isImageIdListUploaded == null) {
                uploadImagesWithList(uploadModel.checkOut?.endDayImagesInfo)
            } else {
                uploadingBulkAttendanceIndex++
                uploadingBulkAttendanceModel()
            }
        } else {
            uploadViewModel.addBulkAttendance(uploadingBulkAttendanceModel)
        }
    }

    private fun uploadImagesWithList(list: ArrayList<PicMapModel>?) {
        val photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
        addedPicList = ArrayList()
        list?.forEach { picMapModel ->
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = picMapModel.url
            addedPhotoModel.onEditProduct = true
            photoModelList.add(addedPhotoModel)
        }

        uploadMultipleImageCount = photoModelList.filterNotNull().size

        photoModelListForUpload = ArrayList()
        photoModelListForUpload.addAll(photoModelList)
        uploadingImageCounter = 0
        uploadMultipleImage(uploadingImageCounter)
    }

    private fun uploadSingleAttendance(addCheckInOutModel: AddCheckInOutModel?) {
        uploadingType = AppConstant.ATTENDANCE
        uploadingSingleAttendanceModel = addCheckInOutModel
        if (addCheckInOutModel?.startDayImages.isNullOrEmpty().not() ||
                addCheckInOutModel?.endDayImages.isNullOrEmpty().not()) {
            uploadViewModel.addAttendance(uploadingSingleAttendanceModel)

        } else if (addCheckInOutModel?.startDayImagesInfo.isNullOrEmpty().not() ||
                addCheckInOutModel?.endDayImagesInfo.isNullOrEmpty().not()) {

            val photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()

            addedPicList = ArrayList()
            uploadingSingleAttendanceModel?.startDayImagesInfo?.forEach { picMapModel ->
                val addedPhotoModel = AddedPhotoModel()
                addedPhotoModel.imagePath = picMapModel.url
                addedPhotoModel.onEditProduct = true
                photoModelList.add(addedPhotoModel)
            }

            uploadMultipleImageCount = photoModelList.filterNotNull().size

            photoModelListForUpload = ArrayList()
            photoModelListForUpload.addAll(photoModelList)
            uploadingImageCounter = 0
            uploadMultipleImage(uploadingImageCounter)
        } else {
            uploadViewModel.addAttendance(uploadingSingleAttendanceModel)
        }
    }

    private fun uploadingPayment() {
        uploadingType = AppConstant.PAYMENT
        if (paymentList.size > uploadingPaymentIndex) {
            uploadPaymentModel = paymentList[uploadingPaymentIndex]

            if (uploadPaymentModel != null) {
                if (uploadPaymentModel?.paymentImages.isNullOrEmpty().not()) {
                    uploadViewModel.recordPayment(uploadPaymentModel!!)
                    incrementProgress()
                } else if (uploadPaymentModel?.paymentImagesInfo.isNullOrEmpty().not()) {
                    val photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
                    addedPicList = ArrayList()
                    uploadPaymentModel?.paymentImagesInfo?.forEach { picMapModel ->
                        val addedPhotoModel = AddedPhotoModel()
                        addedPhotoModel.imagePath = picMapModel.url
                        addedPhotoModel.onEditProduct = true
                        photoModelList.add(addedPhotoModel)
                    }

                    uploadMultipleImageCount = photoModelList.filterNotNull().size

                    photoModelListForUpload = ArrayList()
                    photoModelListForUpload.addAll(photoModelList)
                    uploadingImageCounter = 0
                    uploadMultipleImage(uploadingImageCounter)
                } else {
                    uploadViewModel.recordPayment(uploadPaymentModel!!)
                    incrementProgress()
                }
            }
        } else {
            offlineViewModel.getCustomerActivityListData()
        }
    }

    private fun uploadOrders() {
        uploadingType = AppConstant.ORDER
        if (orderList.size > uploadingOrderIndex) {
            uploadOrderModel = orderList[uploadingOrderIndex]

            if (uploadOrderModel != null) {
                if (uploadOrderModel?.orderImages.isNullOrEmpty().not()) {
                    uploadViewModel.confirmOrder(uploadOrderModel!!)
                    incrementProgress()
                } else if (uploadOrderModel?.orderImagesInfo.isNullOrEmpty().not()) {
                    val photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
                    addedPicList = ArrayList()
                    uploadOrderModel?.orderImagesInfo?.forEach { picMapModel ->
                        val addedPhotoModel = AddedPhotoModel()
                        addedPhotoModel.imagePath = picMapModel.url
                        addedPhotoModel.onEditProduct = true
                        photoModelList.add(addedPhotoModel)
                    }

                    uploadMultipleImageCount = photoModelList.filterNotNull().size

                    photoModelListForUpload = ArrayList()
                    photoModelListForUpload.addAll(photoModelList)
                    uploadingImageCounter = 0

                    uploadMultipleImage(uploadingImageCounter)
                } else {
                    uploadViewModel.confirmOrder(uploadOrderModel!!)
                    incrementProgress()
                }
            }
        } else {
            offlineViewModel.getPaymentNotSyncedData()
        }
    }

    private fun uploadingLeadData() {
        uploadingType = AppConstant.LEAD
        if (leadList.size > uploadingLeadIndex) {
            uploadLeadModel = leadList[uploadingLeadIndex]

            if (uploadLeadModel != null) {
                if (uploadLeadModel?.imageLogo != null) {
                    uploadViewModel.addNewLead(uploadLeadModel!!)
                    incrementProgress()
                } else if (uploadLeadModel?.logoImageUrl.isNullOrEmpty().not()) {
                    uploadSingleImage(uploadLeadModel?.logoImageUrl!!)
                } else {
                    uploadViewModel.addNewLead(uploadLeadModel!!)
                }
            }
        } else {
            offlineViewModel.getOrderNotSyncedData()
        }
    }

    private fun uploadingCustomerData() {
        uploadingType = AppConstant.CUSTOMER
        if (customerList.size > uploadingCustomerIndex) {
            uploadCustomerModel = customerList[uploadingCustomerIndex]

            if (uploadCustomerModel != null) {
                if (uploadCustomerModel?.imageLogo != null) {
                    uploadViewModel.saveCustomer(uploadCustomerModel!!)
                    incrementProgress()
                } else if (uploadCustomerModel?.logoImageUrl.isNullOrEmpty().not()) {
                    uploadSingleImage(uploadCustomerModel?.logoImageUrl!!)
                } else {
                    uploadViewModel.saveCustomer(uploadCustomerModel!!)
                }
            }
        } else {
            offlineViewModel.getCustomerAddressNotSyncedData()
        }
    }

    private fun uploadingCustomerAddressData() {
        uploadingType = AppConstant.ADDRESS
        if (customerAddressList.size > uploadingCustomerAddressIndex) {
            uploadCustomerAddressModel = customerAddressList[uploadingCustomerAddressIndex]

            if (uploadCustomerAddressModel != null) {
                uploadViewModel.saveCustomerAddress(uploadCustomerAddressModel!!, uploadCustomerAddressModel?.customer!!)
            }
        } else {
            offlineViewModel.getLeadNotSyncedData()
        }
    }

    private fun uploadingActivity() {
        uploadingType = AppConstant.ACTIVITY
        if (customerActivityList.size > uploadingActivityIndex) {
            uploadActivityModel = customerActivityList[uploadingActivityIndex]

            if (uploadActivityModel != null) {
                if (uploadActivityModel?.pics.isNullOrEmpty().not()) {
                    uploadViewModel.recordActivity(uploadActivityModel!!)
                    incrementProgress()
                } else if (uploadActivityModel?.picsUrls.isNullOrEmpty().not()) {
                    val photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
                    addedPicList = ArrayList()
                    uploadActivityModel?.picsUrls?.forEach { picMapModel ->
                        val addedPhotoModel = AddedPhotoModel()
                        addedPhotoModel.imagePath = picMapModel.url
                        addedPhotoModel.onEditProduct = true
                        photoModelList.add(addedPhotoModel)
                    }
                    uploadMultipleImageCount = photoModelList.filterNotNull().size

                    photoModelListForUpload = ArrayList()
                    photoModelListForUpload.addAll(photoModelList)
                    uploadingImageCounter = 0

                    uploadMultipleImage(uploadingImageCounter)
                } else {
                    uploadViewModel.recordActivity(uploadActivityModel!!)
                    incrementProgress()
                }
            }
        } else {
            offlineViewModel.getExpenseHeadListData()
        }
    }

    private fun uploadingExpenseHead() {
        uploadingType = AppConstant.EXPENSE
        if (expenseHeadList.size > uploadingExpenseHeadIndex) {
            uploadExpenseHeadModel = expenseHeadList[uploadingExpenseHeadIndex]

            if (uploadExpenseHeadModel != null) {
                uploadViewModel.uploadExpenseHead(uploadExpenseHeadModel!!)
                incrementProgress()
            }
        } else {
            offlineViewModel.getExpenseListData()
        }
    }

    private fun uploadingExpenseList() {
        uploadingType = AppConstant.EXPENSE_LIST
        if (expenseList.size > uploadingExpenseListIndex) {
            uploadExpenseListModel = expenseList[uploadingExpenseListIndex]

            if (uploadExpenseListModel != null) {
                if (uploadExpenseListModel?.billProof.isNullOrEmpty().not()) {
                    uploadViewModel.uploadExpenseList(uploadExpenseListModel!!)
                    incrementProgress()
                } else if (uploadExpenseListModel?.billProofUrls.isNullOrEmpty().not()) {
                    val photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
                    addedPicList = ArrayList()
                    uploadExpenseListModel?.billProofUrls?.forEach { picMapModel ->
                        val addedPhotoModel = AddedPhotoModel()
                        addedPhotoModel.imagePath = picMapModel.url
                        addedPhotoModel.onEditProduct = true
                        photoModelList.add(addedPhotoModel)
                    }
                    uploadMultipleImageCount = photoModelList.filterNotNull().size

                    photoModelListForUpload = ArrayList()
                    photoModelListForUpload.addAll(photoModelList)
                    uploadingImageCounter = 0

                    uploadMultipleImage(uploadingImageCounter)
                } else {
                    uploadViewModel.uploadExpenseList(uploadExpenseListModel!!)
                    incrementProgress()
                }
            }
        } else {
            finishActivity()
        }
    }

    private fun uploadSingleImage(logoImageUrl: String) {
        launch {
            val compressedImageFile = Compressor.compress(
                    this@UploadingOfflineDataActivity, File(logoImageUrl)
            ) {
                quality(30)
                resolution(512, 512)
                size(1_197_152)
            }

            imageUploadViewModel.uploadCredentials(
                    compressedImageFile.path
            )
        }
    }

    private fun uploadMultipleImage(imageCounter: Int) {
        if (imageCounter < uploadMultipleImageCount) {
            if (photoModelListForUpload.size > imageCounter
                    && photoModelListForUpload[imageCounter] != null
                    && photoModelListForUpload[imageCounter]?.imagePath != null) {
                uploadSingleImage(photoModelListForUpload[imageCounter]?.imagePath!!)
            }
        }
    }

    private fun updateImageInfo() {
        when (uploadingType) {
            AppConstant.CUSTOMER -> {
                uploadCustomerModel?.imageLogo = logoImageId
                uploadingCustomerData()
            }

            AppConstant.LEAD -> {
                uploadLeadModel?.imageLogo = logoImageId
                uploadingLeadData()
            }

            AppConstant.ACTIVITY -> {
                addedPicList.add(logoImageId)
                if (addedPicList.size == uploadMultipleImageCount) {
                    uploadActivityModel?.pics = addedPicList

                    uploadingActivity()
                }
            }

            AppConstant.ORDER -> {
                addedPicList.add(logoImageId)
                if (addedPicList.size == uploadMultipleImageCount) {
                    uploadOrderModel?.orderImages = addedPicList

                    uploadOrders()
                }
            }

            AppConstant.PAYMENT -> {
                addedPicList.add(logoImageId)
                if (addedPicList.size == uploadMultipleImageCount) {
                    uploadPaymentModel?.paymentImages = addedPicList

                    uploadingPayment()
                }
            }

            AppConstant.ATTENDANCE -> {
                addedPicList.add(logoImageId)
                if (addedPicList.size == uploadMultipleImageCount) {
                    if (uploadingSingleAttendanceModel?.action == AppConstant.ATTENDANCE_CHECK_IN) {
                        uploadingSingleAttendanceModel?.startDayImages = addedPicList
                    } else if (uploadingSingleAttendanceModel?.action == AppConstant.ATTENDANCE_CHECK_OUT) {
                        uploadingSingleAttendanceModel?.endDayImages = addedPicList
                    }

                    uploadSingleAttendance(uploadingSingleAttendanceModel)
                }
            }

            AppConstant.BULK_ATTENDANCE -> {
                addedPicList.add(logoImageId)
                if (addedPicList.size == uploadMultipleImageCount) {

                    val attendanceModel = bulkAttendanceList!![uploadingBulkAttendanceIndex]

                    if (attendanceModel.checkIn?.startDayImagesInfo.isNullOrEmpty().not()
                            && attendanceModel.checkIn?.isImageIdListUploaded == null) {
                        attendanceModel.checkIn?.startDayImages = addedPicList
                        attendanceModel.checkIn?.isImageIdListUploaded = true
                    } else if (attendanceModel.checkOut?.endDayImagesInfo.isNullOrEmpty().not()
                            && attendanceModel.checkOut?.isImageIdListUploaded == null) {
                        attendanceModel.checkOut?.endDayImages = addedPicList
                        attendanceModel.checkOut?.isImageIdListUploaded = true
                    }

                    bulkAttendanceList!![uploadingBulkAttendanceIndex] = attendanceModel

                    uploadingBulkAttendanceModel()

                }
            }

            AppConstant.EXPENSE_LIST -> {
                addedPicList.add(logoImageId)
                if (addedPicList.size == uploadMultipleImageCount) {
                    uploadExpenseListModel?.billProof = addedPicList

                    uploadingExpenseList()
                }
            }

        }
    }

    fun showToast(message: String?) {
        MessageHelper().initMessage(
                message,
                findViewById(android.R.id.content)
        )
    }

    private fun showCancelDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setCancelable(false)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)


        tvHeading.text = resources.getString(R.string.cancel_upload)
        tvTitle.text = resources.getString(R.string.cancel_upload_message)


        ivClose.visibility = View.GONE

        tvCancel.text = resources.getString(R.string.yes_cancel)

        tvDelete.text = resources.getString(R.string.no_continue)

        tvCancel.setOnClickListener {
            dialog.dismiss()
            MyApplication.instance.setPerformedValue(false)
            finish()
        }

        tvDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onBackPressed() {
        if (uploadingDone) {
            finishActivity()
        } else {
            showCancelDialog()
        }
    }

    private fun finishActivity() {
        MyApplication.instance.setPerformedValue(false)
        val i = Intent()
        setResult(RESULT_OK, i)
        finish()
    }

}