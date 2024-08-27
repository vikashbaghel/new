package com.app.rupyz.sales.product

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.base.FailureResponse
import com.app.rupyz.generic.base.NetworkCallback
import com.app.rupyz.generic.helper.divideHeadersIntoQueryParams
import com.app.rupyz.generic.model.profile.product.ProductInfoModel
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddProductModel
import com.app.rupyz.model_kt.AddProductResponseModel
import com.app.rupyz.model_kt.AllCategoryInfoModel
import com.app.rupyz.model_kt.BranListResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.ProductDetailsResponseModel
import com.app.rupyz.model_kt.packagingunit.PackagingUnitData
import com.app.rupyz.model_kt.packagingunit.PackagingUnitInfoModel
import com.app.rupyz.model_kt.packagingunit.PackagingUnitResponseModel
import com.app.rupyz.retrofit.OfflineRetrofitClient.offlineApiInterface
import com.app.rupyz.retrofit.RetrofitClient.apiInterface
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call

class ProductRepository {
    fun getProductList(
        productLiveData: MutableLiveData<ProductInfoModel>,
        brandList: List<String?>,
        name: String,
        category: String,
        page: Int,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {

        val stringBuilder = StringBuilder()

        brandList.forEachIndexed { index, s ->
            stringBuilder.append(s)

            if (index < brandList.size) {
                stringBuilder.append(",")
            }
        }


        val uploadCred: Call<ProductInfoModel> = if (pageSize == null) {
            apiInterface.getProductList(
                SharedPref.getInstance().getInt(ORG_ID),
                name,
                stringBuilder,
                category,
                page
            )
        } else {
            offlineApiInterface.getProductListForDataDump(
                SharedPref.getInstance().getInt(ORG_ID),
                name,
                stringBuilder,
                category,
                page, pageSize, true, offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<ProductInfoModel?>() {
            override fun onSuccess(t: ProductInfoModel?) {
                CoroutineScope(IO).launch {
                    productLiveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val errorModel = ProductInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    errorModel.error = true
                    errorModel.errorCode = failureResponse?.errorCode
                    errorModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    productLiveData.postValue(errorModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }


    fun getProductListForOrderEdit(
        productLiveData: MutableLiveData<ProductInfoModel>,
        ids: ArrayList<Int?>
    ) {

        val stringBuilder = StringBuilder()
        ids.forEachIndexed { index, s ->
            stringBuilder.append(s)
            if (index < ids.size) {
                stringBuilder.append(",")
            }
        }

        val uploadCred: Call<ProductInfoModel> =
            apiInterface.getProductListForOrderEdit(
                SharedPref.getInstance().getInt(ORG_ID),
                stringBuilder
            )

        uploadCred.enqueue(object : NetworkCallback<ProductInfoModel?>() {
            override fun onSuccess(t: ProductInfoModel?) {
                CoroutineScope(IO).launch {
                    productLiveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val errorModel = ProductInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    errorModel.error = true
                    errorModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    productLiveData.postValue(errorModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getOffLineProductList(
        productLiveData: MutableLiveData<ProductInfoModel>,
        brandList: List<String?>,
        name: String,
        category: String,
        page: Int
    ) {

        DatabaseLogManager.getInstance().getProductListData(
            productLiveData,
            name,
            brandList,
            category,
            null,
            page
        )
    }

    fun getOffLineBrandList(
        brandListLiveData: MutableLiveData<BranListResponseModel>,
        name: String,
        page: Int
    ) {
        DatabaseLogManager.getInstance().getBrandListData(brandListLiveData, name, page)
    }

    fun getOffLineCategoryList(
        categoryLiveData: MutableLiveData<AllCategoryInfoModel>,
        customerId: Int?,
        name: String
    ) {
        DatabaseLogManager.getInstance().getCategoryListData(categoryLiveData, customerId, name)
    }

    fun getAllCategoryList(
        liveData: MutableLiveData<AllCategoryInfoModel>,
        customerId: Int?,
        name: String,
        page: Int?,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val uploadCred: Call<AllCategoryInfoModel> = if (pageSize == null) {
            apiInterface.getAllCategoryList(
                SharedPref.getInstance().getInt(ORG_ID),
                true,
                customerId,
                name, page, null, null, null
            )
        } else {
            offlineApiInterface.getAllCategoryList(
                SharedPref.getInstance().getInt(ORG_ID),
                true,
                customerId,
                name, page, pageSize, true, offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<AllCategoryInfoModel?>() {
            override fun onSuccess(t: AllCategoryInfoModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                    t?.data?.let { category ->
                        DatabaseLogManager.getInstance().insertCategoryListData(category)
                    }
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val errorModel = AllCategoryInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    errorModel.error = true
                    errorModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(errorModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun deleteProduct(
        liveData: MutableLiveData<GenericResponseModel>, id: Int, isForced: Boolean
    ) {
        val model = JsonObject()
        if (isForced) {
            model.addProperty("is_forced", true)
        }
        val uploadCred: Call<GenericResponseModel> =
            apiInterface.deleteProduct(SharedPref.getInstance().getInt(ORG_ID), id, model)


        uploadCred.enqueue(object : NetworkCallback<GenericResponseModel?>() {
            override fun onSuccess(t: GenericResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = GenericResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getAllCategoryListWithCustomer(
        liveData: MutableLiveData<AllCategoryInfoModel>,
        customerId: Int,
        name: String,
        headers: String?
    ) {
        val uploadCred: Call<AllCategoryInfoModel> =
            apiInterface.getAllCategoryListWithCustomer(
                SharedPref.getInstance().getInt(ORG_ID),
                customerId,
                name,
                headers.divideHeadersIntoQueryParams().first,
                headers.divideHeadersIntoQueryParams().second
            )

        uploadCred.enqueue(object : NetworkCallback<AllCategoryInfoModel?>() {
            override fun onSuccess(t: AllCategoryInfoModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = AllCategoryInfoModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }


    fun getBrandList(
        liveData: MutableLiveData<BranListResponseModel>,
        name: String,
        page: Int,
        pageSize: Int?,
        offlineDataLastSyncedTime: String?
    ) {
        val uploadCred: Call<BranListResponseModel> = if (pageSize == null) {
            apiInterface.getBrandList(
                SharedPref.getInstance().getInt(ORG_ID),
                name,
                true,
                page,
                null, null, null
            )
        } else {
            offlineApiInterface.getBrandList(
                SharedPref.getInstance().getInt(ORG_ID),
                name,
                true,
                page,
                pageSize, true, offlineDataLastSyncedTime
            )
        }

        uploadCred.enqueue(object : NetworkCallback<BranListResponseModel?>() {
            override fun onSuccess(t: BranListResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                    t?.data?.let { brand ->
                        DatabaseLogManager.getInstance().insertBrandListData(brand)
                    }
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val staffAddResponseModel = BranListResponseModel()
                try {
                    val parser = JsonParser()
                    var jsonObj: JsonObject? = null
                    jsonObj = parser.parse(failureResponse?.errorBody) as JsonObject?

                    staffAddResponseModel.error = true
                    staffAddResponseModel.message = jsonObj?.get("message")?.asString

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(staffAddResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getProductDetails(
        liveData: MutableLiveData<ProductDetailsResponseModel>,
        id: Int,
        customerId: Int?,
        requiredPricingGroupDetails: Boolean?
    ) {

        val uploadCred: Call<ProductDetailsResponseModel> =
            if (requiredPricingGroupDetails == true) {
                apiInterface.getProductTelescopicPricing(
                    SharedPref.getInstance().getInt(ORG_ID),
                    id,
                    customerId,
                    telescopic = true
                )
            } else {
                apiInterface.getProductDetails(
                    SharedPref.getInstance().getInt(ORG_ID),
                    id,
                    customerId
                )
            }

        uploadCred.enqueue(object : NetworkCallback<ProductDetailsResponseModel?>() {
            override fun onSuccess(t: ProductDetailsResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val addResponseModel = ProductDetailsResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    addResponseModel.error = true
                    addResponseModel.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(addResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getOfflineProductDetailsById(
        liveData: MutableLiveData<ProductDetailsResponseModel>,
        id: Int
    ) {
        DatabaseLogManager.getInstance().getOfflineProductDetailsById(liveData, id)
    }

    fun getProductDetailsUsingCode(
        liveData: MutableLiveData<ProductDetailsResponseModel>,
        code: String,
        customerId: Int?
    ) {

        val uploadCred: Call<ProductDetailsResponseModel> =

            apiInterface.getProductDetailsUsingCode(
                SharedPref.getInstance().getInt(ORG_ID),
                0,
                customerId,
                code
            )

        uploadCred.enqueue(object : NetworkCallback<ProductDetailsResponseModel?>() {
            override fun onSuccess(t: ProductDetailsResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val addResponseModel = ProductDetailsResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    addResponseModel.error = true
                    addResponseModel.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(addResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun addProduct(
        liveData: MutableLiveData<AddProductResponseModel>,
        product: AddProductModel,
    ) {

        val uploadCred: Call<AddProductResponseModel> =
            apiInterface.addProduct(SharedPref.getInstance().getInt(ORG_ID).toString(), product)

        uploadCred.enqueue(object : NetworkCallback<AddProductResponseModel?>() {
            override fun onSuccess(t: AddProductResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val addResponseModel = AddProductResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    addResponseModel.error = true
                    addResponseModel.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(addResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getCategoryList(
        liveData: MutableLiveData<AllCategoryInfoModel>,
        name: String?,
    ) {

        val uploadCred: Call<AllCategoryInfoModel> =
            apiInterface.getCategoryList(SharedPref.getInstance().getInt(ORG_ID), name)

        uploadCred.enqueue(object : NetworkCallback<AllCategoryInfoModel?>() {
            override fun onSuccess(t: AllCategoryInfoModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val addResponseModel = AllCategoryInfoModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    addResponseModel.error = true
                    addResponseModel.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(addResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun editProduct(
        liveData: MutableLiveData<AddProductResponseModel>,
        product: AddProductModel,
        id: Int
    ) {

        val uploadCred: Call<AddProductResponseModel> =
            apiInterface.editProduct(
                SharedPref.getInstance().getInt(ORG_ID).toString(),
                product,
                id
            )

        uploadCred.enqueue(object : NetworkCallback<AddProductResponseModel?>() {
            override fun onSuccess(t: AddProductResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val addResponseModel = AddProductResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    addResponseModel.error = true
                    addResponseModel.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(addResponseModel)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun addPackagingUnit(
        liveData: MutableLiveData<PackagingUnitInfoModel>,
        packagingUnitData: PackagingUnitData
    ) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<PackagingUnitInfoModel> =
            apiInterface.addPackagingUnit(id, packagingUnitData)

        uploadCred.enqueue(object : NetworkCallback<PackagingUnitInfoModel?>() {
            override fun onSuccess(t: PackagingUnitInfoModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                    Log.d("DEBUG", "success")
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = PackagingUnitInfoModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    model.error = true
                    model.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(model)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }

    fun getPackagingUnit(liveData: MutableLiveData<PackagingUnitResponseModel>) {
        val id = SharedPref.getInstance().getInt(ORG_ID)
        val uploadCred: Call<PackagingUnitResponseModel> =
            apiInterface.getPackagingUnit(id)

        uploadCred.enqueue(object : NetworkCallback<PackagingUnitResponseModel?>() {
            override fun onSuccess(t: PackagingUnitResponseModel?) {
                CoroutineScope(IO).launch {
                    liveData.postValue(t)
                }
            }

            override fun onFailure(failureResponse: FailureResponse?) {
                val model = PackagingUnitResponseModel()
                try {
                    val parser = JsonParser()
                    val jsonObj: JsonObject? =
                        parser.parse(failureResponse?.errorBody) as JsonObject?

                    model.error = true
                    model.message = jsonObj?.get("message")?.asString

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                CoroutineScope(IO).launch {
                    liveData.postValue(model)
                }
            }

            override fun onError(t: Throwable?) {
                Log.e("DEBUG", "ERROR = ${t?.message}")
            }
        })
    }
}
