package com.app.rupyz.dialog

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.app.rupyz.BuildConfig
import com.app.rupyz.R
import com.app.rupyz.databinding.BottomSheetMultipleImageUploadOptionBinding
import com.app.rupyz.generic.helper.log
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.getPath
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/***
 * @since 01/05/2024
 *
 * @see it will only be used if @param isPhotoPickerRecycler is true
 *
 * @property ImageUploadBottomSheet is the Image Upload Bottom Sheet Dialog Fragment where user can chose from camera, gallery or pdf and get the list of image URI's
 *
 * @param pictureCount is a number of photos that can be selected
 ***/
class ImageUploadBottomSheet(private val pictureCount: Int,private val onPhotoSelected     : (List<String>) -> Unit) : BottomSheetDialogFragment(){

    private var bottomSheetBinding  : BottomSheetMultipleImageUploadOptionBinding? = null
    private val multiplePhotoList   : MutableList<String> = ArrayList()
    private val photoFileName       : String = "photo.jpg"
    private var photoFileUri        : Uri? = null
    private lateinit var photoFile  : File


    private var takePictureResultLauncher : ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()){
        if (photoFileUri != null) {
            val path = photoFileUri
            if (path != null) {
                multiplePhotoList.add(photoFile.absolutePath)
            }
            onPhotoSelected.invoke(multiplePhotoList)
            dismiss()
        } else {
            Log.d("PhotoPicker", "No media selected")
        }

    }

    private var pickVisualMediaResultLauncher : ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            val path = uri.getPath(requireContext())
            if (path != null) {
                multiplePhotoList.add(path)
            }
            onPhotoSelected.invoke(multiplePhotoList)
            dismiss()
        } else {
            Log.d("PhotoPicker", "No media selected ${uri}")
        }

    }

    private var pickMultipleVisualMediaResultLauncher : ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(if (pictureCount <= 1){ 2 } else{  pictureCount })){ uris ->

        if (uris.isNotEmpty()) {
            uris.forEach {
                val path = it.getPath(requireContext())
                multiplePhotoList.add(path!!)
            }
            onPhotoSelected.invoke(multiplePhotoList)
            dismiss()
        } else {
            Log.e("PhotoPicker", "No media selected")
        }

    }

    private val permissionResultLauncher : ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

        if (photoFileUri == null) {
            CoroutineScope(Dispatchers.IO).launch {  photoFile = withContext(Dispatchers.IO) { getPhotoFileUri(System.currentTimeMillis().toString() + "-" + photoFileName) }
                photoFileUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile)
            }
        }

        val allGranted = permissions.entries.firstOrNull { !it.value }
        if (allGranted == null) {
            if (permissions[Manifest.permission.CAMERA] != null) {
                takePictureResultLauncher.launch(photoFileUri!!)
            }else{
                if (pictureCount <= 1){
                    pickVisualMediaResultLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }else{
                    pickMultipleVisualMediaResultLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }

        } else {
            Toast.makeText(requireContext(), "Camera Permission is required to perform this action.", Toast.LENGTH_SHORT).show()
        }

    }

    private val pdfPickPermissionResultLauncher : ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissions ->

        if (permissions) {
            val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
            pdfIntent.type = "application/pdf"
            pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
            pdfIntent.action = Intent.ACTION_GET_CONTENT
            uploadPdfActivityResultLauncher.launch(pdfIntent)
        } else {
            Toast.makeText(requireContext(), "Permission is required to perform this action.", Toast.LENGTH_SHORT).show()
        }

    }

    private var uploadPdfActivityResultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
            val uri = result.data!!.data
            val path: String = FileUtils.getPdfFile((context as FragmentActivity), uri!!).absolutePath
            multiplePhotoList.add(path)
            onPhotoSelected.invoke(multiplePhotoList)
        }
    }


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
        try {
            CoroutineScope(Dispatchers.IO).launch {
                photoFile = withContext(Dispatchers.IO) { getPhotoFileUri(System.currentTimeMillis().toString() + "-" + photoFileName) }
                photoFileUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile)
            }
        } catch (e: Exception) { log(e.toString()) }
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        bottomSheetBinding = BottomSheetMultipleImageUploadOptionBinding.inflate(inflater,container,false)
        return bottomSheetBinding?.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        arguments?.let {
            if (arguments?.containsKey(AppConstant.DISABLE_GALLERY_PHOTO) == true && SharedPref.getInstance().getBoolean(
                    SharePrefConstant.GALLERY_UPLOAD_PIC_ENABLE , false)) {
                bottomSheetBinding?.groupGallery?.visibility = View.GONE
            }

            if (arguments?.containsKey(AppConstant.DOCUMENT) == true) {
                bottomSheetBinding?.groupPdf?.visibility = View.VISIBLE
            }
            else {
                bottomSheetBinding?.groupPdf?.visibility = View.GONE
            }

        }

        bottomSheetBinding?.tvCamera?.setOnClickListener {
            if (photoFileUri == null){
                CoroutineScope(Dispatchers.IO).launch {
                    photoFile = withContext(Dispatchers.IO) { getPhotoFileUri(System.currentTimeMillis().toString() + "-" + photoFileName) }
                    photoFileUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile)
                }
            }
            checkCameraPermission()
        }

        bottomSheetBinding?.tvGallery?.setOnClickListener {
            checkGalleryPermission()
        }

        bottomSheetBinding?.tvPdf?.setOnClickListener {
            uploadPdf()
        }

        bottomSheetBinding?.ivBack?.setOnClickListener {
            dismiss()
        }

    }

    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (pictureCount <= 1){
                pickVisualMediaResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }else{
                pickMultipleVisualMediaResultLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        } else {
            permissionResultLauncher.launch(arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionResultLauncher.launch(arrayOf(Manifest.permission.CAMERA , Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            permissionResultLauncher.launch(arrayOf(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    private fun uploadPdf() {
        pdfPickPermissionResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    /***
     * @since 01/05/2024
     *
     * @return File a new file is created with fileName parameter and the file is returned
     *
     * @see getPhotoFileUri method is used to create a new file with the given fileName
     *
     * @param fileName is the name of the file that will be created
     * ***/
    private fun getPhotoFileUri(fileName: String): File {
        val appTag = "RUPYZ"
        val mediaStorageDir = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), appTag)
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) { Log.d(appTag, "failed to create directory") }
        return File(mediaStorageDir.path + File.separator + fileName)
    }


}
