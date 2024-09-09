package com.app.rupyz.ui.user

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityUpdateUserInfoBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.model_kt.PostOfficeItem
import com.app.rupyz.ui.imageupload.ImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.ImageUploadListener
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.more.MoreViewModel
import com.app.rupyz.ui.organization.profile.activity.OrgViewModel
import com.app.rupyz.ui.organization.profile.adapter.CustomAutoCompleteAdapter
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File

class UpdateUserInfoActivity : BaseActivity(), ImageUploadListener {
    private lateinit var binding: ActivityUpdateUserInfoBinding
    private lateinit var orgViewModel: OrgViewModel
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private val moreViewModel: MoreViewModel by viewModels()

    private var counter = 1
    private var isFromGst = false

    private var state = ArrayList<String>()

    private var logoImagePath: String? = null
    private var logoImageId: Int? = null
    private var prevS3LogoImageId: Int? = null
    private var logoImageUrl: String? = null

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orgViewModel = ViewModelProvider(this)[OrgViewModel::class.java]
        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("uploading ...")
        progressDialog!!.setCancelable(false)

        state.addAll(listOf(*resources.getStringArray(R.array.states)))

        binding.clMain.visibility = View.GONE
        binding.groupButtonLayout.visibility = View.GONE

        binding.progressBar.visibility = View.VISIBLE

        initLayout()
        initObservers()

        orgViewModel.getInfo()

        binding.clLogo.setOnClickListener {
            val fragment = ImageUploadBottomSheetDialogFragment.newInstance(this)
            fragment.show(supportFragmentManager, AppConstant.PROFILE_SLUG)
        }

        binding.btnAdd.setOnClickListener {
            if (binding.etFirstName.text.toString().isNotEmpty()) {
                progressDialog?.show()
                if (logoImagePath != null) {
                    lifecycleScope.launch {
                        val compressedImageFile = Compressor.compress(
                            this@UpdateUserInfoActivity,
                            File(logoImagePath)
                        ) {
                            quality(30)
                            resolution(512, 512)
                            size(1_197_152)
                        }
                        imageUploadViewModel.uploadCredentialsWithPrevS3Id(
                            compressedImageFile.path,
                            prevS3LogoImageId
                        )
                    }
                } else {
                    updateInfo()
                }
            } else {
                Toast.makeText(this, "Enter First nane", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener { finish() }
        binding.imgClose.setOnClickListener { finish() }
    }

    private fun initLayout() {
        val stateList: MutableList<String> =
            resources.getStringArray(R.array.states).toMutableList()

        val adapter = CustomAutoCompleteAdapter(this, stateList)
        binding.spinnerState.threshold = 0
        binding.spinnerState.setAdapter(adapter)

        binding.spinnerState.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                binding.ivClearStateName.visibility = View.VISIBLE
                binding.ivDropDown.visibility = View.GONE
            }

        binding.spinnerState.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.spinnerState.showDropDown()
            }
        }

        binding.spinnerState.setOnClickListener {
            binding.spinnerState.showDropDown()
        }

        binding.ivClearStateName.setOnClickListener {
            binding.spinnerState.setText("")
            binding.ivClearStateName.visibility = View.GONE
            binding.ivDropDown.visibility = View.VISIBLE
        }

        binding.etPinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etPinCode.hasFocus() && s.toString()
                        .isNotBlank() && s.toString().length == 6
                ) {
                    binding.pinCodeProgressBar.visibility = View.VISIBLE
                    moreViewModel.getPostalResponse(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun updateInfo() {
        val model = OrgProfileDetail()

        model.legalName = binding.etBusinessName.text.toString()
        model.primaryGstin = binding.etGstNo.text.toString()
        model.mobile = binding.etMobileNumber.text.toString()
        model.email = binding.etEmailId.text.toString()
        model.addressLine1 = binding.etAddressLine1.text.toString()
        model.city = binding.etCity.text.toString()
        model.state = binding.spinnerState.text.toString()
        model.pincode = binding.etPinCode.text.toString()
        model.first_name = binding.etFirstName.text.toString()
        model.last_name = binding.etLastName.text.toString()
        model.logoImage = logoImageId

        orgViewModel.updateBasicInfo(model)

    }

    private fun initObservers() {
        orgViewModel.getLiveData().observe(this) { it ->
            binding.progressBar.visibility = View.GONE
            it.data?.let { data ->
                inflateData(data)
            }
        }

        orgViewModel.updateUserLiveData.observe(this) {
            if (it.error.not()) {
                it?.let {
                    progressDialog?.dismiss()

                    Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
                    if (it.error == false) {
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            } else {
                progressDialog?.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        orgViewModel.getUserInfoFromGstLiveData.observe(this) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            if (it.error.not()) {
                it?.let {
                    it.data?.let { data ->
                        inflateData(data)
                    }
                }
            }
        }

        imageUploadViewModel.getCredLiveData().observe(this) { model ->
            if (model.error != null && model.error!!.not()) {
                progressDialog?.show()
                model.data?.let { data ->
                    if (data.id != null) {
                        logoImageId = data.id?.toInt()
                        updateInfo()
                    }
                }
            } else {
                progressDialog?.dismiss()
                Toast.makeText(this, model.message, Toast.LENGTH_SHORT).show()
            }
        }
        moreViewModel.postalCodeResponseLiveData.observe(this) {
            binding.pinCodeProgressBar.visibility = View.GONE
            if (it.status == "Success") {
                it.postOffice?.let { postal ->
                    if (postal.isNotEmpty()) {
                        autoFillPostalOffice(postal[0])
                    }
                }
            } else if (it.status != "Failed") {
                showToast(it.message)
            }
        }
    }


    private fun autoFillPostalOffice(postOfficeItem: PostOfficeItem) {
        binding.etCity.setText(postOfficeItem.district)
        if (postOfficeItem.state.isNullOrEmpty().not()) {
            binding.spinnerState.setText(postOfficeItem.state)
            binding.ivClearStateName.visibility = View.VISIBLE
            binding.ivDropDown.visibility = View.GONE
        }
    }


    private fun inflateData(it: OrgProfileDetail) {
        if (it.primaryGstin.isNullOrEmpty().not()) {
            binding.etGstNo.setText(it.primaryGstin)
        } else {
            counter += 1
        }

        if (it.legalName.isNullOrEmpty().not()) {
            binding.etBusinessName.setText(it.legalName)
        }

        if (it.mobile.isNullOrEmpty().not()) {
            binding.etMobileNumber.setText(it.mobile)
        }

        if (it.email.isNullOrEmpty().not()) {
            binding.etEmailId.setText(it.email)
        }

        if (it.addressLine1.isNullOrEmpty().not()) {
            binding.etAddressLine1.setText(it.addressLine1)
        }
        if (it.city.isNullOrEmpty().not()) {
            binding.etCity.setText(it.city)
        }
        if (it.state.isNullOrEmpty().not()) {
            binding.spinnerState.setText(it.state)
            binding.ivDropDown.visibility = View.GONE
            binding.ivClearStateName.visibility = View.VISIBLE
        }
        if (it.pincode.isNullOrEmpty().not()) {
            binding.etPinCode.setText(it.pincode)
        }

        if (it.first_name.isNullOrEmpty().not()) {
            binding.etFirstName.setText(it.first_name)
        }
        if (!it.last_name.isNullOrEmpty()) {
            binding.etLastName.setText(it.last_name)
        }

        if (it.logo_image_url.isNullOrEmpty().not()) {
            logoImageUrl = it.logo_image_url
            logoImageId = it.logoImage
            prevS3LogoImageId = it.logoImage

            binding.groupUploadHd.visibility = View.GONE
            binding.groupLogo.visibility = View.VISIBLE
            ImageUtils.loadImage(it.logo_image_url, binding.ivCompanyLogo)
        }

        binding.etGstNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty() && p0.toString().length == 15) {
                    if (!isFromGst) {
                        binding.progressBar.visibility = View.VISIBLE

                        orgViewModel.getInfoUsingGstNumber(p0.toString())
                        isFromGst = true
                    }
                } else {
                    isFromGst = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.clMain.visibility = View.VISIBLE
        binding.groupButtonLayout.visibility = View.VISIBLE
    }

    override fun onCameraUpload(fileName: String?) {
        logoImagePath = fileName
        binding.groupUploadHd.visibility = View.GONE
        binding.groupLogo.visibility = View.VISIBLE
        binding.ivCompanyLogo.setImageURI(Uri.fromFile(File(fileName!!)))
    }

    override fun onGalleryUpload(fileName: String?) {
        logoImagePath = fileName
        binding.groupUploadHd.visibility = View.GONE
        binding.groupLogo.visibility = View.VISIBLE
        binding.ivCompanyLogo.setImageURI(Uri.fromFile(File(fileName!!)))
    }
}