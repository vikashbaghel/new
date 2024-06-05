package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityCustomerFeedbackBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.GeoLocationUtils
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.NameAndValueSetInfoModel
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.sales.customforms.FormBinding
import com.app.rupyz.sales.customforms.FormItemHandlerFactory
import com.app.rupyz.sales.customforms.FormItemType
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.orderdispatch.LrPhotoListAdapter
import com.app.rupyz.sales.staffactivitytrcker.StaffActivityViewModel
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.app.rupyz.ui.organization.profile.adapter.ProductImageViewPagerAdapter

class CustomerFeedbackDetailActivity : BaseActivity(),
        ProductImageViewPagerAdapter.ProductImageClickListener {
    private lateinit var binding: ActivityCustomerFeedbackBinding

    private var isDataChange: Boolean = false
    private lateinit var activityViewModel: StaffActivityViewModel
    private var feedbackId: Int = -1

    private lateinit var addPhotoListAdapter: LrPhotoListAdapter
    private val pics: ArrayList<PicMapModel> = ArrayList()
    private var myActivity = false

    private var customerType: String = ""
    private var notificationOrgId: Int? = null


    private val headingParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityViewModel = ViewModelProvider(this)[StaffActivityViewModel::class.java]

        headingParams.setMargins(0, 40, 16, 0)


        initObservers()

        binding.mainContent.visibility = View.GONE
        binding.shimmerLayout.visibility = View.VISIBLE

        if (intent.hasExtra(AppConstant.ORGANIZATION)) {
            notificationOrgId = intent.getIntExtra(AppConstant.ORGANIZATION, 0)
        }

        if (intent.hasExtra(AppConstant.ACTIVITY_ID)) {
            feedbackId = intent.getIntExtra(AppConstant.ACTIVITY_ID, -1)
            activityViewModel.getCustomerFeedbackDetails(notificationOrgId, feedbackId)
        }

        if (intent.hasExtra(AppConstant.CUSTOMER_TYPE)) {
            customerType = intent.getStringExtra(AppConstant.CUSTOMER_TYPE)!!
        }

        if (intent.hasExtra(AppConstant.ACTIVITY_TYPE)) {
            binding.ivMore.visibility = View.GONE
            myActivity = true
        }

        binding.ivMore.setOnClickListener {
            //creating a popup menu
            val popup = PopupMenu(this, binding.ivMore)
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_edit_and_delete)
            popup.menu.getItem(1).isVisible = false

            //adding click listener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.delete_product -> {
                        showDeleteDialog()
                        return@setOnMenuItemClickListener true
                    }

                    R.id.edit_product -> {
                        someActivityResultLauncher.launch(
                                Intent(this, CustomFormActivity::class.java)
                                        .putExtra(AppConstant.CUSTOMER_FEEDBACK, feedbackId).putExtra(
                                                AppConstant.CUSTOMER_ID,
                                                intent.getIntExtra(AppConstant.CUSTOMER_ID, -1)
                                        ).putExtra(AppConstant.ACTIVITY_TYPE, customerType)
                        )
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }

        binding.ivClose.setOnClickListener {
            onBackPressed()
        }
    }

    private var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            isDataChange = true
            activityViewModel.getCustomerFeedbackDetails(notificationOrgId, feedbackId)
        }
    }

    private fun showDeleteDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_delete_dialog)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tvHeading = dialog.findViewById<TextView>(R.id.tv_heading)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val ivClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tv_delete)

        tvHeading.text = resources.getString(R.string.delete_feedback)
        tvTitle.text = resources.getString(R.string.delete_feedback_message)

        ivClose.setOnClickListener { dialog.dismiss() }
        tvCancel.setOnClickListener { dialog.dismiss() }

        tvDelete.setOnClickListener {

            activityViewModel.deleteFeedback(feedbackId)
            dialog.dismiss()
        }

        dialog.show()
    }


    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        activityViewModel.getFeedbackDetailLiveData.observe(this) {
            binding.shimmerLayout.visibility = View.GONE
            binding.mainContent.visibility = View.VISIBLE
            if (it.error == false) {
                it.data?.let { model ->
                    if (model.feedbackType.isNullOrEmpty().not()) {
                        binding.tvToolbarTitle.text = model.feedbackType
                    }

                    binding.tvDate.text =
                            DateFormatHelper.convertIsoToDateAndTimeFormat(model.createdAt)

                    binding.tvCreatedBy.text = model.createdByName
                    if (model.geoLocationLat != 0.0 && model.geoLocationLong != 0.0) {

                        if (model.geoAddress.isNullOrEmpty().not()) {
                            binding.tvLocation.text = model.geoAddress
                        } else {
                            binding.tvLocation.text = GeoLocationUtils.getAddress(
                                    this, model.geoLocationLat!!, model.geoLocationLong!!)
                        }

                        binding.tvLocation.setOnClickListener {
                            Utils.openMap(
                                    this,
                                    model.geoLocationLat, model.geoLocationLong,
                                    model.feedbackType
                            )
                        }
                        binding.ivLocation.setOnClickListener {
                            Utils.openMap(
                                    this,
                                    model.geoLocationLat, model.geoLocationLong,
                                    model.feedbackType
                            )
                        }
                    }

                    if (myActivity) {
                        if (!model.customerName.isNullOrEmpty()) {
                            binding.tvCustomerName.text = model.customerName
                        } else if (!model.businessName.isNullOrEmpty()) {
                            binding.tvCustomerName.text = model.businessName
                        }
                    } else if (intent.hasExtra(AppConstant.CUSTOMER_TYPE)) {
                        when (intent.getStringExtra(AppConstant.CUSTOMER_TYPE)) {
                            AppConstant.CUSTOMER_FEEDBACK -> binding.tvToolbarTitle.text =
                                    AppConstant.CUSTOMER + " Activity"

                            AppConstant.LEAD_FEEDBACK -> binding.tvToolbarTitle.text =
                                    AppConstant.LEAD + " Activity"
                        }
                    }

                    if (model.customFormData.isNullOrEmpty().not()) {
                        binding.formLayout.removeAllViews()

                        model.customFormData?.forEach { formItem ->
                            if (formItem.value.isNullOrEmpty().not()) {
                                initFormData(formItem)
                            }
                        }
                    } else {

                        if (model.comments.isNullOrEmpty().not()) {
                            val nameAndValueSetInfoModel = NameAndValueSetInfoModel()
                            nameAndValueSetInfoModel.label = resources.getString(R.string.comment)
                            nameAndValueSetInfoModel.value = model.comments
                            initFormData(nameAndValueSetInfoModel)
                        }

                        if (model.picsUrls.isNullOrEmpty().not()) {
                            val stringBuilder = StringBuilder()
                            model.picsUrls?.forEachIndexed { index, pic ->
                                stringBuilder.append(pic.url)
                                if (index != model.picsUrls!!.size - 1) {
                                    stringBuilder.append(",")
                                }
                            }
                            val nameAndValueSetInfoModel = NameAndValueSetInfoModel()
                            nameAndValueSetInfoModel.label = resources.getString(R.string.comment)
                            nameAndValueSetInfoModel.value = stringBuilder.toString()
                            initFormData(nameAndValueSetInfoModel)
                        } else {
                        }
                    }
                }
            } else {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initFormData(formItem: NameAndValueSetInfoModel) {
        val textView = TextView(this)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.size_14sp))
        textView.setTextColor(resources.getColor(R.color.leve_text_color))
        // Load the font from resources and set it to the TextView
        val typeface = ResourcesCompat.getFont(this, R.font.poppins_medium)
        textView.typeface = typeface
        textView.layoutParams = headingParams

        textView.text = resources.getString(R.string.custom_forms_heading,
                formItem.label)

        binding.formLayout.addView(textView)

        val formItemType = FormItemType.valueOf(formItem.type!!)
        // Assuming form item type string matches enum name
        val handler = FormItemHandlerFactory.getFormViewHandler(formItemType)
        handler.handleViewFormItem(this, formItem,
                FormBinding(binding.formLayout), supportFragmentManager)

    }

    override fun onImageClick(position: Int) {
        if (pics.size > 0) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            for (pic in pics) {
                val model = ImageViewModel(0, 0, pic.url)
                imageViewModelArrayList.add(model)
            }

            imageListModel.data = imageViewModelArrayList
            startActivity(
                    Intent(this, OrgPhotosViewActivity::class.java).putExtra(
                            AppConstant.PRODUCT_INFO,
                            imageListModel
                    ).putExtra(AppConstant.IMAGE_POSITION, position)
            )
        } else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (intent.hasExtra(AppConstant.NOTIFICATION)) {
            startActivity(
                    Intent(
                            this, SalesMainActivity::class.java
                    )
            )
        } else if (isDataChange) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        super.onBackPressed()
    }
}