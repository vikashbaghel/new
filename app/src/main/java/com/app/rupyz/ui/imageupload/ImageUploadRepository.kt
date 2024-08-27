package com.app.rupyz.ui.imageupload

import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.utils.FileUtils
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.S3UploadCredResponse
import com.app.rupyz.model_kt.S3UploadResponse
import com.app.rupyz.retrofit.RetrofitClient.apiInterface
import com.app.rupyz.retrofit.S3RetrofitClient.s3ApiInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ImageUploadRepository(var liveData: MutableLiveData<GenericResponseModel>) {

    fun uploadCred(path: String, prevS3Id: Int) {

        val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", "image")
                .addFormDataPart("file_name", FileUtils.getImageName(path))
                .addFormDataPart("content_type", FileUtils.getImageExtensions(path))
                .addFormDataPart("prev_s3_id", prevS3Id.toString())
                .build()

        val uploadCred: Call<S3UploadCredResponse> = apiInterface.s3CredUpload(requestBody)

        CoroutineScope(IO).launch {
            try {
                uploadCred.enqueue(object : Callback<S3UploadCredResponse?> {

                    override fun onFailure(call: Call<S3UploadCredResponse?>, t: Throwable) {
                        val model = GenericResponseModel()
                        model.error = true
                        model.message = t.message
                        liveData.postValue(model)
                    }

                    override fun onResponse(
                            call: Call<S3UploadCredResponse?>,
                            response: Response<S3UploadCredResponse?>,
                    ) {
                        uploadFileToS3(path, response)
                    }
                })
            } catch (e: Exception) {
                val model = GenericResponseModel()
                model.error = true
                model.message = "Something went wrong!!"
                CoroutineScope(IO).launch {
                    liveData.postValue(model)
                }
            }
        }
    }

    fun uploadFileToS3(path: String, response: Response<S3UploadCredResponse?>) {
        try {
            val s3Response: S3UploadCredResponse = response.body()!!

            val file = File(path)

            val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("key", response.headers()["key"]!!)
                    .addFormDataPart("x-amz-algorithm", response.headers()["x_amz_algorithm"]!!)
                    .addFormDataPart("x-amz-signature", response.headers()["x_amz_signature"]!!)
                    .addFormDataPart("x-amz-date", response.headers()["x_amz_date"]!!)
                    .addFormDataPart("Policy", response.headers()["policy"]!!)
                    .addFormDataPart(
                            "success_action_status",
                            s3Response.data?.get(0)?.successActionStatus!!
                    )
                    .addFormDataPart("x-amz-credential", response.headers()["x_amz_credential"]!!)
                    .addFormDataPart("Content-Type", s3Response.data[0].contentType!!)
                    .addFormDataPart("Content-Disposition", s3Response.data[0].contentDisposition!!)
                    .addFormDataPart("acl", s3Response.data[0].acl!!)
                    .addFormDataPart(
                            "file",
                            "sample.jpg", file.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .build()

            val uploadCred: Call<ResponseBody> =
                    s3ApiInterface.s3FileUpload(
                            response.headers()["upload_url"]!!,
                            body
                    )

            uploadCred.enqueue(object : NetworkCallback<ResponseBody?>() {
                override fun onSuccess(t: ResponseBody?) {
                    confirmUpload(s3Response)
                }

                override fun onFailure(failureResponse: FailureResponse?) {
                    val model = GenericResponseModel()
                    model.error = true
                    model.message = failureResponse?.errorMessage.toString()
                    liveData.postValue(model)
                }

                override fun onError(t: Throwable?) {
                    val model = GenericResponseModel()
                    model.error = true
                    model.message = t?.message
                    liveData.postValue(model)

                }
            })
        } catch (e: Exception) {
            val model = GenericResponseModel()
            model.error = true
            model.message = "Something went wrong!!"
            CoroutineScope(IO).launch {
                liveData.postValue(model)
            }
        }
    }

    private fun confirmUpload(s3upload: S3UploadCredResponse) {
        val id = s3upload.data?.get(0)?.id!!
        val fieldModel = S3UploadResponse(id)
        val uploadCred: Call<GenericResponseModel> = apiInterface.s3ConfirmUpload(fieldModel)

        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                if (t?.data != null) {
                    t.data?.id = id
                    liveData.postValue(t!!)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = GenericResponseModel()
                model.error = true
                model.message = failureResponse?.errorMessage.toString()
                liveData.postValue(model)
            }

            override fun onError(t: Throwable?) {
                val model = GenericResponseModel()
                model.error = true
                model.message = t?.message
                liveData.postValue(model)
            }
        })
    }
}