package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityCustomerProfileBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.CopyClipBoardHelper
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity

class CustomerProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityCustomerProfileBinding

    private val customerViewModel: CustomerViewModel by viewModels()

    private var customerData: CustomerData? = null
    private var customerId: Int = -1

    private var isDataChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainContent.visibility = View.GONE
        initObservers()

        binding.detailsProgressBar.visibility = View.VISIBLE

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, 0)
            customerViewModel.getCustomerById(customerId, hasInternetConnection())
        }

        if (PermissionModel.INSTANCE.getPermission(
                        AppConstant.EDIT_CUSTOMER_PERMISSION,
                        false
                )){
            binding.tvEditCustomer.visibility = View.VISIBLE
        } else {
            binding.tvEditCustomer.visibility = View.GONE
        }

        binding.ivBack.setOnClickListener { onBackPressed() }

    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        customerViewModel.getCustomerByIdData().observe(this) { data ->
            binding.detailsProgressBar.visibility = View.GONE
            if (data.error == false) {
                data.data?.let { model ->
                    customerData = model
                    initCustomerData(customerData!!)
                }
            } else {
                showToast(data.message)
            }
        }
    }

    private fun initCustomerData(model: CustomerData) {
        binding.tvCustomerName.text = model.name?.replaceFirstChar(Char::titlecase)

        if (model.logoImageUrl.isNullOrEmpty().not()) {
            ImageUtils.loadImage(model.logoImageUrl, binding.ivCustomer)

            binding.ivCustomer.setOnClickListener {
                viewCustomerPhoto(model)
            }
        }

        if (model.customerLevel.isNullOrEmpty().not()) {
            when (model.customerLevel) {
                AppConstant.CUSTOMER_LEVEL_1 -> {
                    binding.hdDistributor.visibility = View.GONE
                    binding.tvDistributorName.visibility = View.GONE
                }
                AppConstant.CUSTOMER_LEVEL_2 -> {

                    if (model.customerParentName.isNullOrEmpty().not()) {

                        binding.hdDistributor.text =
                            SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)

                        val spannable = SpannableString("${model.customerParentName}")

                        val start = 0

                        spannable.setSpan(
                            ForegroundColorSpan(getColor(R.color.theme_purple)),
                            start, // start
                            spannable.length, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )

                        spannable.setSpan(
                            UnderlineSpan(),
                            start, // start
                            spannable.length, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )

                        binding.hdDistributor.visibility = View.VISIBLE
                        binding.tvDistributorName.text = spannable
                        binding.tvDistributorName.visibility = View.VISIBLE
                    } else {
                        binding.hdDistributor.visibility = View.GONE
                        binding.tvDistributorName.visibility = View.GONE
                    }
                }

                AppConstant.CUSTOMER_LEVEL_3 -> {
                    if (model.customerParentName.isNullOrEmpty().not()) {
                        binding.hdDistributor.text =
                            SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)

                        val spannable = SpannableString(
                            "${model.customerParentName}"
                        )

                        val start = 0

                        spannable.setSpan(
                            ForegroundColorSpan(getColor(R.color.theme_purple)),
                            start, // start
                            spannable.length, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )

                        spannable.setSpan(
                            UnderlineSpan(),
                            start, // start
                            spannable.length, // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )

                        binding.tvDistributorName.text = spannable
                        binding.tvDistributorName.visibility = View.VISIBLE
                        binding.hdDistributor.visibility = View.VISIBLE
                    } else {
                        binding.hdDistributor.visibility = View.GONE
                        binding.tvDistributorName.visibility = View.GONE
                    }
                }
            }
        } else {
            binding.hdDistributor.visibility = View.GONE
            binding.tvDistributorName.visibility = View.GONE
        }

        binding.tvDistributorName.setOnClickListener {
            if (model.customerParent != null) {
                startActivity(
                    Intent(this, CustomerDetailActivity::class.java)
                        .putExtra(AppConstant.CUSTOMER_ID, model.customerParent)
                )
            }
        }

        if (!model.contactPersonName.isNullOrEmpty()) {
            binding.tvContactPerson.text =
                model.contactPersonName?.replaceFirstChar(Char::titlecase)
            binding.hdContactPerson.visibility = View.VISIBLE
            binding.tvContactPerson.visibility = View.VISIBLE
        } else {
            binding.hdContactPerson.visibility = View.GONE
            binding.tvContactPerson.visibility = View.GONE
        }

        if (model.mobile.isNullOrEmpty().not()) {
            binding.tvMobileNumber.text = model.mobile
            binding.hdMobileNo.visibility = View.VISIBLE
            binding.tvMobileNumber.visibility = View.VISIBLE

            binding.tvMobileNumber.setOnClickListener {
                CopyClipBoardHelper.copyText(binding.tvMobileNumber.text.toString(), this)
            }
        } else {
            binding.hdMobileNo.visibility = View.GONE
            binding.tvMobileNumber.visibility = View.GONE
        }

        val stringBuilder = StringBuilder()
        if (model.city.isNullOrEmpty().not()) {
            stringBuilder.append(model.city?.replaceFirstChar(Char::titlecase))
        }
        if (model.state.isNullOrEmpty().not()) {
            stringBuilder.append(" , ")
            stringBuilder.append(
                model.state?.replaceFirstChar(
                    Char::titlecase
                )
            )
        }

        if (model.pincode.isNullOrEmpty().not()) {
            stringBuilder.append(" - ")
            stringBuilder.append(model.pincode)
        }

        if (stringBuilder.isEmpty().not()) {
            binding.tvAddress.text = stringBuilder
            binding.hdAddress.visibility = View.VISIBLE
            binding.tvAddress.visibility = View.VISIBLE

            binding.tvAddress.setOnClickListener {
                CopyClipBoardHelper.copyText(binding.tvAddress.text.toString(), this)
            }
        } else {
            binding.tvAddress.visibility = View.VISIBLE
            binding.hdAddress.visibility = View.GONE
        }

        if (model.beat_list.isNullOrEmpty().not()) {
            val beatStringBuilder = StringBuilder()
            model.beat_list?.forEachIndexed { index, s ->
                beatStringBuilder.append(s)
                if (index != model.beat_list?.size!! - 1) {
                    beatStringBuilder.append("\n")
                }
            }
            binding.tvBeat.text = beatStringBuilder
            binding.tvBeat.visibility = View.VISIBLE
            binding.hdBeat.visibility = View.VISIBLE
        } else {
            binding.tvBeat.visibility = View.GONE
            binding.hdBeat.visibility = View.GONE
        }

        if (model.customer_type.isNullOrEmpty().not()) {
            binding.tvCustomerType.text = model.customer_type
            binding.hdCustomerType.visibility = View.VISIBLE
            binding.tvCustomerType.visibility = View.VISIBLE
        } else {
            binding.hdCustomerType.visibility = View.GONE
            binding.tvCustomerType.visibility = View.GONE
        }

        if (model.email.isNullOrEmpty().not()) {
            binding.tvEmail.text = model.email
            binding.tvEmail.visibility = View.VISIBLE
            binding.hdEmail.visibility = View.VISIBLE
            binding.hdEmail.setOnClickListener {
                CopyClipBoardHelper.copyText(binding.hdEmail.text.toString(), this)
            }
        } else {
            binding.tvEmail.visibility = View.GONE
            binding.hdEmail.visibility = View.GONE
        }

        if (model.panId.isNullOrEmpty().not()) {
            binding.tvPanNumber.text = model.panId
            binding.tvPanNumber.visibility = View.VISIBLE
            binding.hdBusinessPan.visibility = View.VISIBLE
            binding.tvPanNumber.setOnClickListener {
                CopyClipBoardHelper.copyText(binding.tvPanNumber.text.toString(), this)
            }
        } else {
            binding.tvPanNumber.visibility = View.GONE
            binding.hdBusinessPan.visibility = View.GONE
        }

        if (model.gstin.isNullOrEmpty().not()) {
            binding.tvGst.text = model.gstin
            binding.tvGst.visibility = View.VISIBLE
            binding.hdGstNumber.visibility = View.VISIBLE
            binding.tvGst.setOnClickListener {
                CopyClipBoardHelper.copyText(binding.tvGst.text.toString(), this)
            }
        } else {
            binding.tvGst.visibility = View.GONE
            binding.hdGstNumber.visibility = View.GONE
        }

        binding.mainContent.visibility = View.VISIBLE

        binding.tvEditCustomer.setOnClickListener { v ->
            //creating a popup menu
            someActivityResultLauncher.launch(
                Intent(this, NewAddCustomerActivity::class.java)
                    .putExtra(AppConstant.CUSTOMER_ID, customerData?.id)
            )
        }

    }

    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            isDataChange = true
            binding.detailsProgressBar.visibility = View.VISIBLE
            customerViewModel.getCustomerById(customerId, hasInternetConnection())
        }
    }

    private fun viewCustomerPhoto(model: CustomerData) {
        if (model.logoImageUrl.isNullOrEmpty().not()) {
            val imageListModel = OrgImageListModel()

            val imageViewModelArrayList = ArrayList<ImageViewModel>()

            val imageModel = ImageViewModel(0, 0, model.logoImageUrl)
            imageViewModelArrayList.add(imageModel)

            imageListModel.data = imageViewModelArrayList
            startActivity(
                Intent(this, OrgPhotosViewActivity::class.java)
                    .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                    .putExtra(AppConstant.IMAGE_POSITION, 0)
            )
        } else {
            showToast(resources.getString(R.string.customer_pic_not_available))
        }
    }

    override fun onBackPressed() {
        if (isDataChange){
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
    }

}