package com.app.rupyz.sales.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityCustomerDetailBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.model.org_image.ImageViewModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.PermissionModel
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.OrgImageListModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.sales.orders.CreateNewOrderForCustomerActivity
import com.app.rupyz.sales.orders.IDataChangeListener
import com.app.rupyz.sales.payment.AddRecordPaymentActivity
import com.app.rupyz.ui.organization.profile.OrgPhotosViewActivity
import com.google.android.material.tabs.TabLayout

class CustomerDetailActivity : BaseActivity(), IDataChangeListener {
    private lateinit var binding: ActivityCustomerDetailBinding

    private val customerViewModel: CustomerViewModel by viewModels()

    private var customerData: CustomerData? = null
    private var isDataChange: Boolean = false
    private var customerId: Int = -1

    private lateinit var customerDetailFragmentPagerAdapter: CustomerDetailFragmentPagerAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainContent.visibility = View.GONE
        initObservers()

        binding.detailsProgressBar.visibility = View.VISIBLE

        if (intent.hasExtra(AppConstant.CUSTOMER_ID)) {
            customerId = intent.getIntExtra(AppConstant.CUSTOMER_ID, 0)
            customerViewModel.getCustomerById(customerId, hasInternetConnection())
        }

        binding.clCustomerDetails.root.setOnClickListener {
            someActivityResultLauncher.launch(
                    Intent(
                            this,
                            CustomerProfileActivity::class.java
                    ).putExtra(AppConstant.CUSTOMER_ID, customerId)
            )
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
                if (data.errorCode != null && data.errorCode == 403) {
                    logout()
                } else {
                    showToast(data.message)
                }
            }
        }
    }

    private fun initCustomerData(model: CustomerData) {
        binding.clCustomerDetails.tvCustomerName.text =
                model.name?.replaceFirstChar(Char::titlecase)

        if (model.logoImageUrl.isNullOrEmpty().not()) {
            ImageUtils.loadImage(model.logoImageUrl, binding.clCustomerDetails.ivCustomer)

            binding.clCustomerDetails.ivCustomer.setOnClickListener {
                viewCustomerPhoto(model)
            }
        }

        if (model.customerLevel.isNullOrEmpty().not()) {
            binding.clCustomerDetails.tvCustomerLevel.text =
                    SharedPref.getInstance().getString(model.customerLevel)
            binding.clCustomerDetails.tvCustomerLevel.visibility = View.VISIBLE

            var customerSubLevelName = ""
            var customerSubLevelCount = 0
            when (model.customerLevel) {
                AppConstant.CUSTOMER_LEVEL_1 -> {
                    binding.clCustomerDetails.tvCustomerLevel.backgroundTintList =
                            ColorStateList.valueOf(
                                    getColor(R.color.customer_level_one_background)
                            )
                    binding.clCustomerDetails.tvCustomerLevel.setTextColor(getColor(R.color.customer_level_one_text_color))
                    customerSubLevelName =
                            SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                    customerSubLevelCount = model.level_2_customer_count ?: 0
                }

                AppConstant.CUSTOMER_LEVEL_2 -> {
                    binding.clCustomerDetails.tvCustomerLevel.backgroundTintList =
                            ColorStateList.valueOf(getColor(R.color.customer_level_two_background))
                    binding.clCustomerDetails.tvCustomerLevel.setTextColor(getColor(R.color.customer_level_two_text_color))

                    if (model.customerParentName.isNullOrEmpty().not()) {
                        val spannable = SpannableString(
                                "${
                                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_1)
                                } : ${model.customerParentName}"
                        )

                        val start = spannable.length - model.customerParentName?.length!!

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

                        binding.clCustomerDetails.tvParentCustomerName.text = spannable
                        binding.clCustomerDetails.tvParentCustomerName.visibility = View.VISIBLE
                    }

                    customerSubLevelName =
                            SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_3)
                    customerSubLevelCount = model.level_3_customer_count ?: 0
                }

                AppConstant.CUSTOMER_LEVEL_3 -> {
                    binding.clCustomerDetails.tvCustomerLevel.backgroundTintList =
                            ColorStateList.valueOf(
                                    getColor(R.color.customer_level_three_background)
                            )

                    binding.clCustomerDetails.tvCustomerLevel.setTextColor(getColor(R.color.customer_level_three_text_color))

                    if (model.customerParentName.isNullOrEmpty().not()) {
                        val spannable = SpannableString(
                                "${
                                    SharedPref.getInstance().getString(AppConstant.CUSTOMER_LEVEL_2)
                                } : ${model.customerParentName}"
                        )

                        val start = spannable.length - model.customerParentName?.length!!

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

                        binding.clCustomerDetails.tvParentCustomerName.text = spannable
                        binding.clCustomerDetails.tvParentCustomerName.visibility = View.VISIBLE
                    }
                }
            }

            if (isDataChange.not()) {
                initTabLayout(customerSubLevelName, customerSubLevelCount)
            }
        } else {
            binding.clCustomerDetails.tvCustomerLevel.visibility = View.GONE
        }

        binding.clCustomerDetails.tvParentCustomerName.setOnClickListener {
            if (model.customerParent != null) {
                startActivity(
                        Intent(this, CustomerDetailActivity::class.java)
                                .putExtra(AppConstant.CUSTOMER_ID, model.customerParent)
                )
            }
        }

        if (!model.contactPersonName.isNullOrEmpty()) {
            binding.clCustomerDetails.tvAuthorizePersonName.text =
                    model.contactPersonName?.replaceFirstChar(Char::titlecase)
            binding.clCustomerDetails.tvAuthorizePersonName.visibility = View.VISIBLE
        } else {
            binding.clCustomerDetails.tvAuthorizePersonName.visibility = View.GONE
        }

        if (model.mobile.isNullOrEmpty().not()) {
            binding.clCustomerDetails.ivPhoneCall.isEnabled = true
            binding.clCustomerDetails.ivPhoneCall.alpha = 1f
            binding.clCustomerDetails.ivWhatsCall.isEnabled = true
            binding.clCustomerDetails.ivWhatsCall.alpha = 1f
        } else {
            binding.clCustomerDetails.ivPhoneCall.isEnabled = false
            binding.clCustomerDetails.ivPhoneCall.alpha = 0.3f
            binding.clCustomerDetails.ivWhatsCall.isEnabled = false
            binding.clCustomerDetails.ivWhatsCall.alpha = 0.3f
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
            binding.clCustomerDetails.tvLocation.text = stringBuilder
            binding.clCustomerDetails.tvLocation.visibility = View.VISIBLE
        } else {
            binding.clCustomerDetails.tvLocation.visibility = View.GONE
        }

        if (model.city.isNullOrEmpty().not()) {
            binding.clCustomerDetails.tvLocation.text = model.city
            binding.clCustomerDetails.tvLocation.visibility = View.VISIBLE

            if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
                binding.clCustomerDetails.tvLocation.paintFlags =
                        binding.clCustomerDetails.tvLocation.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                binding.clCustomerDetails.tvLocation.paintFlags =
                        binding.clCustomerDetails.tvLocation.paintFlags or Paint.ANTI_ALIAS_FLAG
            }

            binding.clCustomerDetails.tvLocation.setOnClickListener {
                viewCustomerLocation(model)
            }
        } else {
            binding.clCustomerDetails.tvLocation.visibility = View.GONE
        }

        binding.mainContent.visibility = View.VISIBLE

        binding.clCustomerDetails.tvNewOrder.setOnClickListener {
            if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_ORDER_PERMISSION, false)) {
                SharedPref.getInstance().clearCart()
                startActivity(
                        Intent(this, CreateNewOrderForCustomerActivity::class.java)
                                .putExtra(AppConstant.CUSTOMER, model)
                                .putExtra(AppConstant.CUSTOMER_NAME, model.name)
                                .putExtra(AppConstant.CUSTOMER_ID, model.id)
                                .putExtra(AppConstant.PAYMENT_INFO, model.paymentTerm)
                )
            } else {
                showToast(resources.getString(R.string.create_order_permission))
            }
        }

        if (hasInternetConnection() && PermissionModel.INSTANCE.getPermission(
                        AppConstant.EDIT_CUSTOMER_PERMISSION,
                        false
                )
        ) {
            binding.clCustomerDetails.ivMore.visibility = View.VISIBLE
        } else {
            binding.clCustomerDetails.ivMore.visibility = View.GONE
        }

        if (hasInternetConnection().not()) {
            if (model.isSyncedToServer == false) {
                binding.clCustomerDetails.ivMore.visibility = View.VISIBLE
            } else {
                binding.clCustomerDetails.ivMore.visibility = View.GONE
            }
        }


        binding.clCustomerDetails.ivMore.setOnClickListener { v ->
            //creating a popup menu
            val popup =
                    PopupMenu(v.context, binding.clCustomerDetails.ivMore)
            //inflating menu from xml resource
            popup.inflate(R.menu.customer_action_menu)

            popup.menu.getItem(1).isVisible = false

            //adding click listener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.edit_product -> {
                        someActivityResultLauncher.launch(
                                Intent(this, AddCustomerActivity::class.java)
                                        .putExtra(AppConstant.CUSTOMER_ID, customerData?.id)
                        )
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }

        binding.clCustomerDetails.ivPhoneCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${model.mobile}")
            startActivity(intent)
        }
        binding.clCustomerDetails.ivWhatsCall.setOnClickListener {
            val uri =
                    Uri.parse("https://api.whatsapp.com/send?phone=+91${model.mobile} &text=Hi, ${model.name}")
            val sendIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(sendIntent)
        }

        binding.clCustomerDetails.tvRecordActivity.setOnClickListener {
            if (PermissionModel.INSTANCE.hasRecordActivityPermission()) {
                startActivity(
                        Intent(this, CustomFormActivity::class.java)
                                .putExtra(AppConstant.CUSTOMER_ID, model.id)
                                .putExtra(AppConstant.CUSTOMER, model)
                                .putExtra(AppConstant.ACTIVITY_TYPE, AppConstant.CUSTOMER_FEEDBACK)
                )
            } else {
                showToast(resources.getString(R.string.you_dont_have_permission_to_perform_this_action))
            }
        }

        binding.clCustomerDetails.tvRecordPayment.setOnClickListener {
            if (PermissionModel.INSTANCE.getPermission(AppConstant.CREATE_PAYMENT_PERMISSION, false)) {
                startActivity(
                        Intent(
                                this@CustomerDetailActivity,
                                AddRecordPaymentActivity::class.java
                        )
                                .putExtra(AppConstant.CUSTOMER, model)
                                        .putExtra(AppConstant.CUSTOMER_NAME,
                                        model.name
                                )
                                .putExtra(
                                        AppConstant.CUSTOMER_ID,
                                        intent.getIntExtra(AppConstant.CUSTOMER_ID, 0)
                                ).putExtra(AppConstant.CUSTOMER, model)
                )
            } else {
                showToast(resources.getString(R.string.create_payment_permission))
            }
        }

    }

    private fun viewCustomerLocation(model: CustomerData) {
        if (model.mapLocationLat != 0.0 && model.mapLocationLong != 0.0) {
            Utils.openMap(this, model.mapLocationLat, model.mapLocationLong, model.name)
        } else {
            showToast(resources.getString(R.string.no_location_found))
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

    var someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            isDataChange = true
            binding.detailsProgressBar.visibility = View.VISIBLE
            customerViewModel.getCustomerById(customerId, hasInternetConnection())
        }
    }


    private fun initTabLayout(customerSubLevelName: String, customerSubLevelCount: Int) {

        var isSecondLevelAvailable = false
        val fragmentList = if (customerSubLevelName.isNotBlank() && customerSubLevelCount != 0) {
            isSecondLevelAvailable = true
            binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText("$customerSubLevelCount $customerSubLevelName")
            )
            binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(resources.getString(R.string.order))
            )
            binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(resources.getString(R.string.activity))
            )

            binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_customer_tab)
            binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_order_tab)
            binding.tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_record_activity)

            arrayListOf(
                    "$customerSubLevelCount $customerSubLevelName",
                    AppConstant.ORDER,
                    AppConstant.ACTIVITY
            )
        } else {
            binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(resources.getString(R.string.order))
            )
            binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(resources.getString(R.string.activity))
            )


            binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_order_tab)
            binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_record_activity)

            arrayListOf(
                    AppConstant.ORDER,
                    AppConstant.ACTIVITY
            )
        }

        customerDetailFragmentPagerAdapter = CustomerDetailFragmentPagerAdapter(
                this, customerId, fragmentList, isSecondLevelAvailable, this
        )

        binding.viewPager.adapter = customerDetailFragmentPagerAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })



        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })

    }

    override fun onBackPressed() {
        if (isDataChange) {
            val intent = Intent()
            intent.putExtra("isDataChange", true)
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    override fun onNotifyDataChange() {
        isDataChange = true
    }
}