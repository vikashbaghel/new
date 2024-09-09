package com.app.rupyz.sales.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.rupyz.R
import com.app.rupyz.databinding.ProfileFragmnetBinding
import com.app.rupyz.generic.base.BaseFragment
import com.app.rupyz.generic.helper.StringHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.AppConstant.PROFILE_IMAGE
import com.app.rupyz.generic.utils.ImageUtils
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.sales.orders.IDataChangeListener
import com.app.rupyz.sales.organization.OrganizationViewModel
import com.app.rupyz.ui.imageupload.ImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.ImageUploadListener
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File

class ProfileFragment : BaseFragment(), ImageUploadListener {
    private lateinit var binding: ProfileFragmnetBinding
    private lateinit var organizationViewModel: OrganizationViewModel
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private var staffId: Int? = null
    private var prevS3Id: Int? = null

    companion object {
        private lateinit var dataChangeListener: IDataChangeListener

        @JvmStatic
        fun newInstance(IDatChangeListener: IDataChangeListener): ProfileFragment {
            val fragment = ProfileFragment()
            dataChangeListener = IDatChangeListener
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmnetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        organizationViewModel = ViewModelProvider(this)[OrganizationViewModel::class.java]
        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]

        initObservers()

        organizationViewModel.getProfileInfo()

        binding.ivUserImage.setOnClickListener {
            val fragment = ImageUploadBottomSheetDialogFragment.newInstance(this)
            fragment.show(childFragmentManager, AppConstant.IMAGE_TYPE_PROFILE)
        }
    }

    private fun initObservers() {
        organizationViewModel.profileLiveData.observe(requireActivity()) { data ->
            if (data.error == false) {
                data.data?.let { model ->
                    staffId = model.id
                    binding.tvEmployeeId.text = model.employeeId
                    binding.tvStaffName.text = model.name
                    binding.tvCompanyName.text = model.orgName

                    if (model.roles.isNullOrEmpty().not()) {
                        binding.tvStaffRole.text =
                            resources.getString(R.string.profile_role, model.roles!![0])
                    }

                    binding.tvMobileNumber.text = model.mobile

                    binding.tvStaffIcon.text =
                        StringHelper.printName(model.name).trim().substring(0, 1)

                    if (model.email.isNullOrEmpty().not()) {
                        binding.tvEmail.text = model.email
                    } else {
                        binding.tvEmail.visibility = View.GONE
                    }

                    if (model.profilePicUrl.isNullOrEmpty().not()) {
                        binding.tvStaffIcon.visibility = View.GONE
                        ImageUtils.loadImage(model.profilePicUrl, binding.ivUserImage)
                    }

                    if (model.profilePic != null) {
                        prevS3Id = model.profilePic.toInt()
                    }

                    binding.mainContent.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
                binding.mainContent.visibility = View.GONE
            }
        }

        imageUploadViewModel.getCredLiveData().observe(requireActivity()) {
            if (it.error == false) {
                val model = StaffData()
                model.profile_pic = it.data?.id?.toInt()
                organizationViewModel.updateStaffProfile(model)
            } else {
                binding.progressBar.visibility = View.GONE
                binding.ivUserImage.isEnabled = true
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        organizationViewModel.updateStaffByIdLiveData.observe(requireActivity()) {
            dataChangeListener.onNotifyDataChange()
            binding.progressBar.visibility = View.GONE
            binding.ivUserImage.isEnabled = true
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            if (it.data?.profilePicUrl.isNullOrEmpty().not()) {
                SharedPref.getInstance().putString(PROFILE_IMAGE, it?.data?.profilePicUrl)
            }
        }
    }

    override fun onCameraUpload(fileName: String?) {
        dataChangeListener.dataChangeInitiate()
        binding.tvStaffIcon.visibility = View.GONE
        binding.ivUserImage.setImageURI(Uri.fromFile(File(fileName!!)))
        binding.ivUserImage.isEnabled = false
        uploadImage(fileName)
    }

    override fun onGalleryUpload(fileName: String?) {
        dataChangeListener.dataChangeInitiate()
        binding.tvStaffIcon.visibility = View.GONE
        binding.ivUserImage.setImageURI(Uri.fromFile(File(fileName!!)))
        binding.ivUserImage.isEnabled = false
        uploadImage(fileName)
    }

    private fun uploadImage(fileName: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val compressedImageFile = Compressor.compress(
                requireActivity(),
                File(fileName)
            ) {
                quality(10)
                resolution(512, 512)
                size(1_197_152)
            }
            imageUploadViewModel.uploadCredentialsWithPrevS3Id(compressedImageFile.path, prevS3Id)
        }
    }
}