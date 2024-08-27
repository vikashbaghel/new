package com.app.rupyz.dialog.checkIn

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.databinding.CheckInItemBinding
import com.app.rupyz.generic.helper.gone
import com.app.rupyz.generic.helper.visibility
import com.app.rupyz.generic.model.product.PicMapModel
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddedPhotoModel
import com.app.rupyz.model_kt.CheckInRequest
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.ui.imageupload.ImageUploadViewModel
import com.app.rupyz.ui.imageupload.MultipleImageUploadBottomSheetDialogFragment
import com.app.rupyz.ui.imageupload.MultipleImageUploadListener
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File


class CheckInDialogFragment : DialogFragment(), CheckInPhotoListAdapter.OnImageDeleteListener,
    MultipleImageUploadListener {
    private lateinit var binding: CheckInItemBinding
    private var multiplePicCount = 0
    var addPhotoListAdapter: CheckInPhotoListAdapter? = null
    var photoModelList: ArrayList<AddedPhotoModel?> = ArrayList()
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private val pics: ArrayList<PicMapModel> = ArrayList()
    private val checkInRequest = CheckInRequest()
    val list = ArrayList<Int>()


    companion object {
        var listener: ICheckInClickListener? = null
        var customerData: CustomerData? = null
        fun getInstance(
            action: CustomerData,
            listener: ICheckInClickListener
        ): CheckInDialogFragment {
            this.listener = listener
            this.customerData = action
            return CheckInDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CheckInItemBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        imageUploadViewModel = ViewModelProvider(this)[ImageUploadViewModel::class.java]
        initObservers()
        binding.tvTitle.text = customerData?.name
        photoModelList.add(null)
        addPhotoListAdapter!!.notifyItemInserted(0)
        binding.tvSubTitle.text = buildString {
            append(MyApplication.instance.getString(R.string.check_in_msg))
            append(" ")
            append(customerData?.name)
        }


        val imageInput = SharedPref.getInstance()
            .getBoolean(AppConstant.CHECK_IMAGE_INPUT, false)

        binding.btnApply.text = MyApplication.instance.getString(R.string.submit)
        if (imageInput) {
            binding.layoutPhoto.visibility = View.VISIBLE
            binding.btnApply.text = MyApplication.instance.getString(R.string.submit)

        } else {
            binding.btnApply.text = MyApplication.instance.getString(R.string.confirm)
        }


        binding.ivClose.setOnClickListener {
            dismiss()
        }
        if (SharedPref.getInstance().getBoolean(AppConstant.CHECK_IMAGE_REQUIRED, false)) {
            binding.tvImageHd.visibility()

        } else {
            binding.tvImageHd.gone()
        }


        binding.btnApply.setOnClickListener {

            when {
                SharedPref.getInstance().getBoolean(AppConstant.CHECK_IMAGE_REQUIRED, false) -> {
                    if (photoModelList.size < 2) {
                        showToast("Please upload image")
                    } else {
                        addImage(it.context)
                    }
                }

                else -> {
                    checkInRequest.customer_id = customerData?.id
                    checkInRequest.geo_location_long = customerData?.geoLocationLong
                    checkInRequest.geo_location_lat = customerData?.geoLocationLat
                    checkInRequest.geo_address = customerData?.geoAddress
                    checkInRequest.images = list
                    listener?.onConfirm(checkInRequest)
                    dismiss()
                }
            }
        }
    }

    private fun submitData() {
        if (pics.size > 0) {
            pics.forEach {
                list.add(it.id!!)
            }
            checkInRequest.customer_id = customerData?.id
            checkInRequest.geo_location_long = customerData?.geoLocationLong
            checkInRequest.geo_location_lat = customerData?.geoLocationLat
            checkInRequest.geo_address = customerData?.geoAddress
            checkInRequest.images = list
            listener?.onConfirm(checkInRequest)
            dismiss()

        }
    }

    private fun addImage(context: Context) {
        binding.progressBarMain.visibility = View.VISIBLE

        if (pics.size > 0) {
            for (i in pics.size until photoModelList.size) {
                if (photoModelList[i] != null && photoModelList[i]!!.imagePath != null) {
                    if (!photoModelList[i]!!.onEditProduct) {
                        lifecycleScope.launch {
                            val compressedImageFile = Compressor.compress(
                                context,
                                File(photoModelList[i]!!.imagePath!!)
                            ) {
                                quality(30)
                                resolution(512, 512)
                                size(197_152)
                            }
                            imageUploadViewModel.uploadCredentials(compressedImageFile.path)
                        }
                    }
                }
            }
        } else {
            for (i in photoModelList.indices) {
                if (photoModelList[i] != null && photoModelList[i]!!.imagePath != null) {

                    lifecycleScope.launch {
                        val compressedImageFile = Compressor.compress(
                            context,
                            File(photoModelList[i]!!.imagePath!!)
                        ) {
                            quality(30)
                            resolution(512, 512)
                            size(197_152)
                        }
                        imageUploadViewModel.uploadCredentials(compressedImageFile.path)

                    }
                }
            }
        }


    }

    private fun initObservers() {
        binding.progressBarMain.visibility = View.GONE
        imageUploadViewModel.getCredLiveData().observe(this) { genericResponseModel ->
            if (genericResponseModel.error == false) {
                genericResponseModel.data?.let { data ->
                    if (data.id != null) {
                        //  showToast("${data.id}")
                        val picMapModel = PicMapModel()
                        picMapModel.id = data.id!!.toInt()
                        picMapModel.url = data.url
                        pics.add(picMapModel)
                        if (pics.size != 0) {
                            submitData()
                        }

                    }
                }
            } else {
                showToast("${genericResponseModel.message}")
                //progressDialog.dismiss()
            }
        }

    }

    private fun initRecyclerView() {
        binding.rvImages.layoutManager = GridLayoutManager(context, 3)
        addPhotoListAdapter = CheckInPhotoListAdapter(photoModelList, this, true)
        binding.rvImages.adapter = addPhotoListAdapter
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onAddImage() {
        val fragment = MultipleImageUploadBottomSheetDialogFragment.newInstance(this)
        val bundle = Bundle()
        bundle.putBoolean(AppConstant.DISABLE_GALLERY_PHOTO, true)
        fragment.arguments = bundle
        fragment.show(requireActivity().supportFragmentManager, AppConstant.IMAGE_UPLOAD_TAG)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteImage(position: Int, timeStamp: Long?) {

        if (photoModelList.size > 1) {
            photoModelList.removeAt(position)
            addPhotoListAdapter!!.notifyDataSetChanged()
        }
        if (photoModelList.size < 6 && photoModelList[photoModelList.size - 1] != null) {
            photoModelList.add(null)

        }
    }

    override fun onCameraUpload(fileName: String?) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAt(photoModelList.size - 1)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            photoModelList.add(addedPhotoModel)
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()
        } else {
            showToast(getString(R.string.upload_max_six_images))
        }
    }

    override fun onGallerySingleUpload(fileName: String?) {
        if (photoModelList.size < 7) {
            multiplePicCount += 1
            photoModelList.removeAt(photoModelList.size - 1)
            val addedPhotoModel = AddedPhotoModel()
            addedPhotoModel.imagePath = fileName
            addedPhotoModel.onEditProduct = false
            photoModelList.add(addedPhotoModel)
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()
        } else {
            showToast(getString(R.string.upload_max_six_images))
        }
    }

    override fun onGalleryMultipleUpload(fileList: List<String>?) {
        if (fileList != null && photoModelList.size < 7 && photoModelList.size + fileList.size <= 7) {
            photoModelList.removeAt(photoModelList.size - 1)
            addPhotoListAdapter!!.notifyItemRemoved(photoModelList.size)
            for (path in fileList) {
                val addedPhotoModel = AddedPhotoModel()
                addedPhotoModel.imagePath = path
                addedPhotoModel.onEditProduct = false
                photoModelList.add(addedPhotoModel)
            }
            if (photoModelList.size < 6) {
                photoModelList.add(null)
            }
            initRecyclerView()
            multiplePicCount += fileList.size
        } else {
            showToast(getString(R.string.upload_max_six_images))

        }
    }

    fun showToast(name: String) {
        Toast.makeText(context, name, Toast.LENGTH_SHORT)
            .show()
    }


}

interface ICheckInClickListener {
    fun onConfirm(model: CheckInRequest)
}
