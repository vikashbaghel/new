package com.app.rupyz.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.rupyz.R
import com.app.rupyz.databinding.AddProductDialogBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.UploadingActionModel

class UploadDataWithImageDialogFragment : DialogFragment() {
    private lateinit var binding: AddProductDialogBinding
    private var imageUploadCountDownTimer: CountDownTimer? = null
    private var apiUploadCountDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = AddProductDialogBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun setListener(actionModel: UploadingActionModel) {
        if (isAdded) {
            if (actionModel.imageExist == true && actionModel.imageCount != null) {
                binding.clImageUpload.visibility = View.VISIBLE
                val imageCount = actionModel.imageCount
                val imageText = if (imageCount!! > 1) "Images" else "Image"
                binding.tvUploadingPicHeading.text = resources.getString(
                    R.string.uploading_images_with_count,
                    "$imageCount", imageText
                )
                startImageUploadProgressBar()
            } else {
                binding.clImageUpload.visibility = View.GONE
                startApiUploadProgressBar()
            }

            when (actionModel.type) {
                AppConstant.ATTENDANCE -> {
                    binding.tvUploadingTypeHeading.text =
                        resources.getString(R.string.adding_attendance)
                    binding.ivAddProduct.setImageResource(R.drawable.ic_start_day_finger_print)
                }

                AppConstant.ACTIVITY -> {
                    binding.ivAddProduct.setImageResource(R.drawable.ic_record_activity)
                    if (actionModel.typeUploaded == true) {
                        binding.tvUploadingTypeHeading.text =
                            resources.getString(R.string.updating_activity)
                    } else {
                        binding.tvUploadingTypeHeading.text =
                            resources.getString(R.string.adding_activity)
                    }

                }
            }
        }
    }

    fun completeImageUploading() {
        binding.tvPhotoUploadingPercentage.text = "100 %"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.photoProgressBar.setProgress(100, true)
        } else {
            binding.photoProgressBar.progress = 100
        }

        imageUploadCountDownTimer?.cancel()

        startApiUploadProgressBar()
    }

    fun completeApiUploading() {

        binding.tvProductUploadingPercentage.text = "100 %"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.productProgressBar.setProgress(100, true)
        } else {
            binding.productProgressBar.progress = 100
        }

        apiUploadCountDownTimer?.cancel()

        dismiss()
    }

    fun dismissOnError() {
        imageUploadCountDownTimer?.cancel()
        apiUploadCountDownTimer?.cancel()
        dismiss()
    }

    private fun startImageUploadProgressBar() {
        val progress = 10
        imageUploadCountDownTimer = object : CountDownTimer(10000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var time: Int = progress - (millisUntilFinished / 1000).toInt()
                time *= 10
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (time < 70) {
                        binding.tvPhotoUploadingPercentage.text = "$time %"
                        binding.photoProgressBar.setProgress(time, true)
                    } else {
                        this.cancel()
                    }
                }
            }

            override fun onFinish() {
            }
        }.start()
    }

    private fun startApiUploadProgressBar() {
        val progress = 10
        apiUploadCountDownTimer = object : CountDownTimer(10000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                var time: Int = progress - (millisUntilFinished / 1000).toInt()
                time *= 10
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (time < 70) {
                        binding.tvProductUploadingPercentage.text = "$time %"
                        binding.productProgressBar.setProgress(time, true)
                    } else {
                        this.cancel()
                    }
                }
            }

            override fun onFinish() {
            }
        }.start()
    }


    fun isStaffUser(): Boolean {
        val appAccessType = SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE)
        return appAccessType != AppConstant.ACCESS_TYPE_MASTER
    }

    fun hasInternetConnection(): Boolean {
        return Connectivity.hasInternetConnection(requireContext())
    }
}