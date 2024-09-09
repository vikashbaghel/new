package com.app.rupyz.ui.imageupload

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.app.rupyz.BuildConfig
import com.app.rupyz.MyApplication
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetMultipleImageUploadOptionBinding
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant.GALLERY_UPLOAD_PIC_ENABLE
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.getPath
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MultipleImageUploadBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetMultipleImageUploadOptionBinding
    private var outputFileUri: Uri? = null
    private val multiplePhotoList: MutableList<String> = ArrayList();

    private val appTag = "RUPYZ"
    private val photoFileName = "photo.jpg"
    private var photoFile: File? = null

    companion object {
        private lateinit var listener: MultipleImageUploadListener

        @JvmStatic
        fun newInstance(imageUploadListener: MultipleImageUploadListener): MultipleImageUploadBottomSheetDialogFragment {
            val fragment = MultipleImageUploadBottomSheetDialogFragment()
            listener = imageUploadListener
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetMultipleImageUploadOptionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if (arguments?.get(AppConstant.DISABLE_GALLERY_PHOTO) != null && SharedPref.getInstance()
                    .getBoolean(GALLERY_UPLOAD_PIC_ENABLE, false)
            ) {
                binding.groupGallery.visibility = View.GONE
            }
        }

        binding.ivCamera.setOnClickListener { checkCameraPermission() }
        binding.tvCamera.setOnClickListener { checkCameraPermission() }
        binding.ivGallery.setOnClickListener { checkGalleryPermission() }
        binding.tvGallery.setOnClickListener { checkGalleryPermission() }

        arguments?.let {
            if (arguments?.get(AppConstant.DOCUMENT) != null) {
                binding.groupPdf.visibility = View.VISIBLE
                binding.tvSizeWarning.text=MyApplication.instance.getString(R.string.attachment_size_restriction)
            } else {
                binding.groupPdf.visibility = View.GONE
                binding.tvSizeWarning.text=MyApplication.instance.getString(R.string.iamge_size_restriction)
            }
        }

        binding.ivPdf.setOnClickListener {
            listener.onUploadPdf()
            dismiss()
        }

        binding.tvPdf.setOnClickListener {
            listener.onUploadPdf()
            dismiss()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }
    }

    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            mGalleryPermissionResult.launch(WRITE_EXTERNAL_STORAGE)
        }
    }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(6)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.

            if (uris.isNotEmpty()) {
                uris.forEach {
                    val path = it.getPath(requireContext())
                    multiplePhotoList.add(path!!)
                }

                listener.onGalleryMultipleUpload(multiplePhotoList)
                dismiss()
            } else {
                Log.e("PhotoPicker", "No media selected")
            }
        }

    private val mGalleryPermissionResult: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                Toast.makeText(
                    requireContext(),
                    "Media Permission is required to perform this action.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncher.launch(
                arrayOf(CAMERA, READ_MEDIA_IMAGES)
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
            val allGranted = permissions.entries.firstOrNull { !it.value }
//                permissions.entries.forEach {
//                    isGranted = it.value
//                }

            if (allGranted == null) {
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
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Perform file operations on a background thread
                photoFile = withContext(Dispatchers.IO) {
                    getPhotoFileUri(System.currentTimeMillis().toString() + "-" + photoFileName)
                }

                if (photoFile != null) {
                    val outputFileUri = FileProvider.getUriForFile(
                        requireContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile!!
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)

                    resultCameraLauncher.launch(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var resultCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                photoFile?.absolutePath?.let { path -> listener.onCameraUpload(path) }
                dismiss()
            }
        }

    private suspend fun getPhotoFileUri(fileName: String): File {
        return withContext(Dispatchers.IO) {
            val mediaStorageDir =
                File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), appTag)

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(appTag, "failed to create directory")
            }

            File(mediaStorageDir.path + File.separator + fileName)
        }
    }

}