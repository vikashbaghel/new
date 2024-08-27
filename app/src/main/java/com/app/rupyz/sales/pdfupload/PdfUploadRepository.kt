package com.app.rupyz.sales.pdfupload

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.model_kt.*
import com.app.rupyz.retrofit.RetrofitClient.apiInterface
import com.app.rupyz.retrofit.S3RetrofitClient.s3ApiInterface
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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

class PdfUploadRepository(var liveData: MutableLiveData<GenericResponseModel>) {

    fun uploadCred(path: String) {
        val fileName = "Expense-details" + System.currentTimeMillis() + ".pdf"
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("type", "document")
            .addFormDataPart("file_name", fileName)
            .addFormDataPart("content_type", "pdf")
            .build()

        val uploadCred: Call<S3UploadCredResponse> = apiInterface.s3CredUpload(requestBody)

        CoroutineScope(IO).launch {
            try {
                uploadCred.enqueue(object : Callback<S3UploadCredResponse?> {

                    override fun onFailure(call: Call<S3UploadCredResponse?>, t: Throwable) {
                        Log.e("DEBUG", "ERROR = ${t.message}")
                    }

                    override fun onResponse(
                        call: Call<S3UploadCredResponse?>,
                        response: Response<S3UploadCredResponse?>,
                    ) {
                        uploadFileToS3(path, response)
                    }

                })
            } catch (e: Exception) {
                Log.e("DEBUG", "EXCEPTION ${e.message}")
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
                    "sample.pdf", file.asRequestBody("document/*".toMediaTypeOrNull())
                )
                .build()

            val uploadCred: Call<ResponseBody> =
                s3ApiInterface.s3FileUpload(response.headers()["upload_url"]!!, body)

            uploadCred.enqueue(object : NetworkCallback<ResponseBody?>() {
                override fun onSuccess(t: ResponseBody?) {
                    CoroutineScope(IO).launch {
                        confirmUpload(s3Response)
                    }
                }

                override fun onFailure(failureResponse: FailureResponse?) {
                    Log.e("DEBUG", "ERROR = ${failureResponse?.errorMessage}")
                }

                override fun onError(t: Throwable?) {
                    Log.e("DEBUG", "ERROR = ${t?.message}")
                }

            })
        } catch (e: Exception) {
            Log.e("DEBUG", "EXCEPTION ${e.message}")
        }
    }

    private fun confirmUpload(s3upload: S3UploadCredResponse) {
        val id = s3upload.data?.get(0)?.id!!
        val fieldModel = S3UploadResponse(id)
        val uploadCred: Call<S3ConfirmResponseModel> = apiInterface.s3ConfirmPdfUpload(fieldModel)

        uploadCred.enqueue(object : NetworkCallback<S3ConfirmResponseModel?>() {
            override fun onSuccess(t: S3ConfirmResponseModel?) {
                CoroutineScope(IO).launch {
                    val genericResponseModel = GenericResponseModel()
                    genericResponseModel.message = t?.message
                    genericResponseModel.error = t?.error

                    val dataItem = DataItem()
                    dataItem.url = t?.data?.url
                    dataItem.id = t?.data?.id.toString()
                    genericResponseModel.data = dataItem

                    liveData.postValue(genericResponseModel)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val responseModel = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    responseModel.error = true
                    responseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(responseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }
}