package com.app.rupyz.ui.organization.profile

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityMyBusinessBinding
import com.app.rupyz.generic.base.BrowserActivity
import com.app.rupyz.generic.helper.EquiFaxReportHelper
import com.app.rupyz.generic.json.JsonHelper
import com.app.rupyz.generic.logger.Logger
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel
import com.app.rupyz.generic.network.ApiClient
import com.app.rupyz.generic.network.ApiInterface
import com.app.rupyz.generic.network.EquiFaxApiInterface
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.POLICY_URL
import com.app.rupyz.generic.utils.AppConstant.TERMS_URL
import com.app.rupyz.generic.utils.ImageUtils.loadBannerImage
import com.app.rupyz.generic.utils.ImageUtils.loadImage
import com.app.rupyz.generic.utils.ImageUtils.loadTeamImage
import com.app.rupyz.generic.utils.SharePrefConstant.LEGAL_NAME
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharePrefConstant.TOKEN
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utility
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.ui.common.UrlImageViewActivity
import com.app.rupyz.ui.equifax.EquiFaxMainActivity
import com.app.rupyz.ui.imageupload.ImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.ImageUploadListener
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.organization.profile.activity.OrgAddAchievementActivity
import com.app.rupyz.ui.organization.profile.activity.OrgAddTeamActivity
import com.app.rupyz.ui.organization.profile.activity.OrgEditIntroActivity
import com.app.rupyz.ui.organization.profile.activity.OrgViewModel
import com.app.rupyz.ui.organization.profile.activity.addphotos.OrgAddPhotoActivity
import com.app.rupyz.ui.organization.profile.adapter.MyBusinessTabLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MyBusinessActivity : AppCompatActivity(), View.OnClickListener,
    ImageUploadListener {
    private lateinit var binding: ActivityMyBusinessBinding

    private var mEquiFaxApiInterface: EquiFaxApiInterface? = null
    private lateinit var mApiInterface: ApiInterface

    private var equiFaxReportHelper: EquiFaxReportHelper? = null
    private var value = 1
    private var strBusinessName: String? =
        null
    private var strShortDescription: String? = null
    private var strAboutUs: String? = null
    private var noOfEmployees: String? = null
    private var strFirstAddressLine: String? =
        null
    private var strCity: String? = null
    private var strState: String? = null
    private var strPinCode: String? = null
    private var strIncorporationDate: String? = null
    private var aggregatedTurnover: String? = null
    private var businessNature: String? = null
    private var mData: OrgProfileInfoModel? = null
    var isDataChange = false
    private val slug = ""
    private var imageUploadType = 0
    private var imageUploadViewModel: ImageUploadViewModel? = null
    private var orgViewModel: OrgViewModel? = null
    private val profileDetailModel = OrgProfileDetail()
    private var adapter: MyBusinessTabLayout? = null
    private var mUtil: Utility? = null
    private var bannerUrl = ""
    private var profileUrl = ""
    private var fileNameLogo = ""
    private var fileNameBanner = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBusinessBinding.inflate(layoutInflater)

        equiFaxReportHelper = EquiFaxReportHelper.getInstance()

        mEquiFaxApiInterface = ApiClient.getRetrofit().create(
            EquiFaxApiInterface::class.java
        )

        mApiInterface = ApiClient.getRetrofit().create(
            ApiInterface::class.java
        )

        mUtil = Utility(this)
        initLayout()
        initTabLayout()
        imageUploadViewModel = ViewModelProvider(this).get(
            ImageUploadViewModel::class.java
        )
        orgViewModel = ViewModelProvider(this).get(OrgViewModel::class.java)
        initObservers()
    }

    private fun initLayout() {

        binding.imgEditProfile.setOnClickListener(this)
        binding.btnEditProfile.setOnClickListener(this)
        binding.btnWhatsappShare.setOnClickListener(this)
        binding.fabAdd.setOnClickListener(this)
        binding.btnBannerImage.setOnClickListener(this)
        binding.logoImageView.setOnClickListener(this)
        binding.btnShareProfile.setOnClickListener(this)
        binding.bannerImageView.setOnClickListener(this)
        binding.imgBack.setOnClickListener { finish() }



        binding.imgOption.setOnClickListener { v ->
            //creating a popup menu
            val popup = PopupMenu(v.context, binding.imgOption)
            //inflating menu from xml resource
            popup.inflate(R.menu.profile_menu_option)

            //adding click listener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.share_app -> {
                        Utility.shareApp(this, binding.imageShare.drawable as BitmapDrawable)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.rate_app -> {
                        Utility.rateApp(this)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.terms_condition -> {
                        initOpenBrowser(TERMS_URL, "Terms of Service")
                        return@setOnMenuItemClickListener true
                    }
                    R.id.privacy_policy -> {
                        initOpenBrowser(POLICY_URL, "Privacy Policy")
                        return@setOnMenuItemClickListener true
                    }
                    R.id.log_out -> {
                        doLogout()
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            //displaying the popup
            popup.show()
        }
    }

    private fun initTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("About"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Team"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Photos"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Achievements"))
        adapter = MyBusinessTabLayout(
            this, supportFragmentManager,
            binding.tabLayout.tabCount, isDataChange, profileDetailModel, ""
        )
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.fabAdd.visibility = View.GONE
        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.fabAdd.visibility = View.GONE
                } else {
                    binding.fabAdd.visibility = View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
                if (tab.position == 0) {
                    value = 1
                    binding.fabAdd.visibility = View.GONE
                } else if (tab.position == 1) {
                    value = 1
                    binding.fabAdd.visibility = View.VISIBLE
                } else if (tab.position == 2) {
                    value = 2
                    binding.fabAdd.visibility = View.VISIBLE
                } else if (tab.position == 3) {
                    value = 3
                    binding.fabAdd.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    private fun initObservers() {
        imageUploadViewModel!!.getCredLiveData().observe(this) { (data): GenericResponseModel ->
            binding.progressBar.visibility = View.GONE
            val model = OrgProfileDetail()
            if (data?.id != null) {
                if (imageUploadType == R.id.btnBannerImage || imageUploadType == R.id.bannerImageView) {
                    model.bannerImage = data.id!!.toInt()
                } else if (imageUploadType == R.id.logoImageView) {
                    model.logoImage = data.id!!.toInt()
                }
                orgViewModel!!.updateInfo(model)
            }
        }
        orgViewModel!!.getLiveData().observe(this) { orgProfileInfoModel: OrgProfileInfoModel ->
            bannerUrl = orgProfileInfoModel.data.banner_image_url
            profileUrl = orgProfileInfoModel.data.logo_image_url
            if (fileNameLogo.equals("", ignoreCase = true) && fileNameBanner.equals(
                    "",
                    ignoreCase = true
                )
            ) {
                loadImage(orgProfileInfoModel.data.logo_image_url, binding.logoImageView)
                loadImage(bannerUrl, binding.bannerImageView)
            } else {
                if (!fileNameLogo.equals("", ignoreCase = true)) {
                    binding.logoImageView.setImageURI(Uri.fromFile(File(fileNameLogo)))
                }
                if (!fileNameBanner.equals("", ignoreCase = true)) {
                    binding.bannerImageView.setImageURI(Uri.fromFile(File(fileNameBanner)))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        profileInfo()
    }

    private fun profileInfo() {
        Logger.errorLogger(this.javaClass.name, "profileInfo - " + SharedPref.getInstance().getString(TOKEN))
        val call = mEquiFaxApiInterface!!.getProfileInfo(
            SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN)
        )
        call.enqueue(object : Callback<OrgProfileInfoModel> {
            override fun onResponse(
                call: Call<OrgProfileInfoModel>,
                response: Response<OrgProfileInfoModel>
            ) {
                if (response.code() == 200) {
                    Logger.errorLogger(
                        this.javaClass.name,
                        "response - " + Gson().toJson(response.body())
                    )
                    val response1 = response.body()
                    equiFaxReportHelper!!.orgProfile = response1
                    if (response1!!.data != null) {
                        mData = response1
                        strBusinessName = response1.data.legalName
                        strShortDescription = response1.data.shortDescription
                        strFirstAddressLine = response1.data.addressLine1
                        strIncorporationDate = response1.data.incorporationDate
                        strPinCode = response1.data.pincode
                        strCity = response1.data.city
                        strState = response1.data.state
                        strAboutUs = response1.data.aboutUs
                        binding.txvBusinessName.setText(response1.data.legalName)
                        aggregatedTurnover = response1.data.aggregatedTurnover
                        noOfEmployees = response1.data.noOfEmployees.toString() + ""
                        SharedPref.getInstance().putString(LEGAL_NAME, response1.data.legalName)



                        initBadge(response1.data.complianceRating)
                        try {
                            val address = (response1.data.city + ", "
                                    + response1.data.state + ", "
                                    + response1.data.pincode)
                            binding.txvAddress.text = address
                        } catch (exception: Exception) {
                            binding.txvAddress.text = response1.data.addressLine1
                        }
                        binding.txvShortDescription.text = response1.data.shortDescription
                        if (response1.data.businessNature != null) {
                            businessNature = response1.data.businessNature
                        }
                        try {
                            bannerUrl = response1.data.banner_image_url
                            profileUrl = response1.data.logo_image_url
                        } catch (ex: Exception) {

                        }


                        if (response1.data?.banner_image_url != null) {
                            bannerUrl = response1.data.banner_image_url
                        }

                        loadBannerImage(bannerUrl, binding.bannerImageView)
                        if (response1.data?.logo_image_url != null) {
                            profileUrl = response1.data.logo_image_url
                        }
                        loadTeamImage(
                            response1.data.logo_image_url,
                            binding.logoImageView
                        )


                        try {
                            (this as EquiFaxMainActivity).initPrefix(response1.data.legalName)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    } else {
                    }
                }
            }

            override fun onFailure(call: Call<OrgProfileInfoModel>, t: Throwable) {
                Logger.errorLogger(this.javaClass.name, t.message)
                call.cancel()
            }
        })
    }

    private fun initBadge(complianceRating_dot: Double) {
        try {
            val complianceRating = complianceRating_dot.toInt()
            binding.txtBadgeValue.text = "$complianceRating_dot/5"
            when (complianceRating) {
                1 -> {
                    binding.badge.setImageResource(R.mipmap.ic_badge_amateur)
                    binding.txtBadgeTitle.text = "Amateur"
                }
                2 -> {
                    binding.badge.setImageResource(R.mipmap.ic_badge_basic)
                    binding.txtBadgeTitle.text = "Basic"
                }
                3 -> {
                    binding.badge.setImageResource(R.mipmap.ic_badge_upcoming)
                    binding.txtBadgeTitle.text = "Upcoming"
                }
                4 -> {
                    binding.badge.setImageResource(R.mipmap.ic_badge_respacted)
                    binding.txtBadgeTitle.text = "Respected"
                }
                5 -> {
                    binding.badge.setImageResource(R.mipmap.ic_badge_iconic)
                    binding.txtBadgeTitle.text = "Iconic"
                }
                else -> {
                    binding.badge.setImageResource(R.mipmap.ic_badge_amateur)
                    binding.txtBadgeTitle.text = "Amateur"
                }
            }
            binding.badgeLayout.visibility = View.VISIBLE
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_whatsapp_share -> Utility.shareWhatsApp(
                this,
                mData!!.data.legalName,
                mData!!.data.slug
            )
            R.id.btn_edit_profile, R.id.img_edit_profile -> {
                val intent = Intent(this, OrgEditIntroActivity::class.java)
                intent.putExtra("org_business_name", strBusinessName)
                intent.putExtra("org_incorporation_date", strIncorporationDate)
                intent.putExtra("org_city", strCity)
                intent.putExtra("org_pincode", strPinCode)
                intent.putExtra("org_short_description", strShortDescription)
                intent.putExtra("org_registered_address", strFirstAddressLine)
                intent.putExtra("business_nature", businessNature)
                intent.putExtra("about_us", strAboutUs)
                intent.putExtra("aggregatedTurnover", aggregatedTurnover)
                intent.putExtra("noOfEmployees", noOfEmployees)
                intent.putExtra("state", strState)
                startActivity(intent)
            }
            R.id.fab_add -> openActivity()
            R.id.btn_share_profile -> Utility.shareMyProfileWithAll(
                this,
                mData!!.data.legalName,
                mData!!.data.slug
            )
            R.id.btnBannerImage, R.id.bannerImageView -> {
                imageUploadType = view.id
                val fragment = ImageUploadBottomSheetDialogFragment.newInstance(this)
                if (bannerUrl.isNotEmpty()) {
                    val bundle = Bundle()
                    bundle.putBoolean(AppConstant.BOTTOM_SHEET_PREVIEW_TYPE, true)
                    bundle.putString(AppConstant.IMAGE_TYPE, AppConstant.IMAGE_TYPE_BANNER)
                    bundle.putString(AppConstant.IMAGE_URL, bannerUrl)
                    fragment.arguments = bundle
                }
                fragment.show(supportFragmentManager, "tag")
            }
            R.id.logoImageView -> {
                imageUploadType = view.id
                val fragment1 = ImageUploadBottomSheetDialogFragment.newInstance(this)
                if (profileUrl.isNotEmpty()) {
                    val bundle1 = Bundle()
                    bundle1.putBoolean(AppConstant.BOTTOM_SHEET_PREVIEW_TYPE, true)
                    bundle1.putString(AppConstant.IMAGE_TYPE, AppConstant.IMAGE_TYPE_PROFILE)
                    bundle1.putString(AppConstant.IMAGE_URL, profileUrl)
                    fragment1.arguments = bundle1
                }
                fragment1.show(supportFragmentManager, "tag")
            }
        }
    }


    private fun openActivity() {
        if (value == 1) {
            activityResultLauncher.launch(Intent(this, OrgAddTeamActivity::class.java))
        } else if (value == 2) {
            activityResultLauncher.launch(Intent(this, OrgAddPhotoActivity::class.java))
        } else if (value == 3) {
            activityResultLauncher.launch(Intent(this, OrgAddAchievementActivity::class.java))
        }
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    var activityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            isDataChange = true
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onCameraUpload(fileName: String?) {
        imageUploadViewModel!!.uploadCredentials(fileName!!)
        binding.progressBar.visibility = View.VISIBLE
        if (imageUploadType == R.id.btnBannerImage || imageUploadType == R.id.bannerImageView) {
            fileNameBanner = fileName
            binding.bannerImageView.setImageURI(Uri.fromFile(File(fileName)))
        } else if (imageUploadType == R.id.logoImageView) {
            fileNameLogo = fileName
            binding.logoImageView.setImageURI(Uri.fromFile(File(fileName)))
        }
    }

    override fun onGalleryUpload(fileName: String?) {
        imageUploadViewModel!!.uploadCredentials(fileName!!)
        binding.progressBar.visibility = View.VISIBLE
        if (imageUploadType == R.id.btnBannerImage || imageUploadType == R.id.bannerImageView) {
            fileNameBanner = fileName
            binding.bannerImageView.setImageURI(Uri.fromFile(File(fileName)))
        } else if (imageUploadType == R.id.logoImageView) {
            fileNameLogo = fileName
            binding.logoImageView.setImageURI(Uri.fromFile(File(fileName)))
        }
    }


    override fun onPreview(image_type: String?, image_url: String?) {
        var previewUrl = ""
        if (imageUploadType == R.id.btnBannerImage || imageUploadType == R.id.bannerImageView) {
            previewUrl = bannerUrl
        } else if (imageUploadType == R.id.logoImageView) {
            previewUrl = profileUrl
        }
        val viewBanner = Intent(this, UrlImageViewActivity::class.java)
        viewBanner.putExtra(AppConstant.IMAGE_URL, previewUrl)
        viewBanner.putExtra(AppConstant.IMAGE_PREVIEW, true)
        viewBanner.putExtra(AppConstant.IMAGE_TYPE, image_type)
        startActivity(viewBanner)
    }


    private fun initOpenBrowser(url: String, title: String) {
        val intent = Intent(this, BrowserActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("title", title)
        startActivity(intent)
    }


    private fun doLogout() {
        val call1: Call<String> = mApiInterface.logout(
            JsonHelper.getLogoutJson(
                "Android"
            ), "Bearer " + SharedPref.getInstance().getString(TOKEN)
        )
        call1.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                Logger.errorLogger(this.javaClass.name, response.code().toString() + "")
                mUtil!!.logout()
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                mUtil!!.logout()
            }
        })
    }

}