package com.app.rupyz.ui.imageupload

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetImageUploadOptionBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.getPath
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File


class ImageUploadBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetImageUploadOptionBinding
    private var outputFileUri: Uri? = null
    private var imageType: String? = null
    private var imageUrl: String? = null

    private val appTag = "RUPYZ"
    private val photoFileName = "photo.jpg"
    private var photoFile: File? = null
    private val multiplePhotoList: MutableList<String> = ArrayList()

    companion object {
        private var listenerImage: ImageUploadListener? = null

        @JvmStatic
        fun newInstance(imageUploadListener: ImageUploadListener): ImageUploadBottomSheetDialogFragment {
            val fragment = ImageUploadBottomSheetDialogFragment()
            listenerImage = imageUploadListener
            return fragment
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetImageUploadOptionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments.let {
            if (arguments?.get(AppConstant.DISABLE_GALLERY_PHOTO) != null
                    && SharedPref.getInstance().getBoolean(
                            SharePrefConstant.GALLERY_UPLOAD_PIC_ENABLE, false)
            ) {
                binding.groupGallery.visibility = View.GONE
            }

            if (arguments?.getString(AppConstant.IMAGE_TYPE) != null) {
                imageType = arguments?.getString(AppConstant.IMAGE_TYPE)
            }
            if (arguments?.getString(AppConstant.IMAGE_URL) != null) {
                imageUrl = arguments?.getString(AppConstant.IMAGE_URL)
            }
        }

        binding.ivCamera.setOnClickListener { checkCameraPermission() }
        binding.tvCamera.setOnClickListener { checkCameraPermission() }
        binding.ivGallery.setOnClickListener { checkGalleryPermission() }
        binding.tvGallery.setOnClickListener { checkGalleryPermission() }

        binding.ivBack.setOnClickListener {
            dismiss()
        }
    }

    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mGalleryPermissionResult.launch(
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            )
        } else {
            mGalleryPermissionResult.launch(
                    arrayOf(WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    private val mGalleryPermissionResult =
            registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                var isGranted = false

                permissions.entries.forEach {
                    isGranted = it.value
                }

                if (isGranted) {
                    // Launch the photo picker and let the user choose only images.
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    Toast.makeText(
                            requireContext(),
                            "Media Permission is required to perform this action.",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }

    private val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    val path = uri.getPath(requireContext())
                    if (path != null) {
                        multiplePhotoList.add(path)
                    }
                    if (multiplePhotoList.size > 0) {
                        listenerImage?.onGalleryUpload(multiplePhotoList[0])
                    }
                    dismiss()

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncher.launch(
                    arrayOf(CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
            )
        } else {
            activityResultLauncher.launch(
                    arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE)
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
                resultCameraLauncher.launch(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var resultCameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    listenerImage?.onCameraUpload(photoFile?.absolutePath!!)
                    dismiss()
                }
            }


    private fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir =
                File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), appTag)

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(appTag, "failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

}