package com.app.rupyz.sales.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rupyz.databse.DatabaseLogManager
import com.app.rupyz.generic.model.profile.product.ProductInfoModel
import com.app.rupyz.model_kt.AddProductModel
import com.app.rupyz.model_kt.AddProductResponseModel
import com.app.rupyz.model_kt.AllCategoryInfoModel
import com.app.rupyz.model_kt.BranListResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.ProductDetailsResponseModel
import com.app.rupyz.model_kt.packagingunit.PackagingUnitData
import com.app.rupyz.model_kt.packagingunit.PackagingUnitInfoModel
import com.app.rupyz.model_kt.packagingunit.PackagingUnitResponseModel
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    var productLiveData = MutableLiveData<ProductInfoModel>()
    var productCategoryLiveData = MutableLiveData<AllCategoryInfoModel>()
    var categoryWithCustomerLiveData = MutableLiveData<AllCategoryInfoModel>()
    var deleteProductLiveData = MutableLiveData<GenericResponseModel>()
    var brandListLiveData = MutableLiveData<BranListResponseModel>()
    var productDetailsLiveData = MutableLiveData<ProductDetailsResponseModel>()
    var productTelescopicPricingLiveData = MutableLiveData<ProductDetailsResponseModel>()

    var addProductLiveData = MutableLiveData<AddProductResponseModel>()
    var addPackagingUnitData = MutableLiveData<PackagingUnitInfoModel>()
    var packagingUnitResponseModel = MutableLiveData<PackagingUnitResponseModel>()

    private val repository = ProductRepository()
    fun addProduct(product: AddProductModel) {
        viewModelScope.launch {
            repository.addProduct(addProductLiveData, product)
        }
    }

    fun editProduct(product: AddProductModel, id: Int) {
        viewModelScope.launch {
            repository.editProduct(addProductLiveData, product, id)
        }
    }

    fun addPackagingUnit(packagingUnitData: PackagingUnitData) {
        viewModelScope.launch {
            repository.addPackagingUnit(addPackagingUnitData, packagingUnitData)
        }
    }

    fun getPackagingUnit() {
        viewModelScope.launch {
            repository.getPackagingUnit(packagingUnitResponseModel)
        }
    }


    fun getProductList(
        name: String,
        brandList: ArrayList<String?>,
        category: String,
        page: Int,
        hasInternetConnection: Boolean,
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                repository.getProductList(
                    productLiveData,
                    brandList,
                    name,
                    category,
                    page,
                    null,
                    null
                )
            } else {
                repository.getOffLineProductList(productLiveData, brandList, name, category, page)
            }
        }
    }

    fun getProductListForOrderEdit(ids: ArrayList<Int?>) {
        viewModelScope.launch {
            repository.getProductListForOrderEdit(productLiveData, ids)
        }
    }

    fun getAllCategoryList(customerId: Int?, name: String, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                repository.getAllCategoryList(
                    productCategoryLiveData,
                    customerId,
                    name,
                    null,
                    null,
                    null
                )
            } else {
                repository.getOffLineCategoryList(productCategoryLiveData, customerId, name)
            }
        }
    }

    fun getAllCategoryListWithCustomer(
        customerId: Int, name: String, headers: String?,
        hasInternet: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternet) {
                repository.getAllCategoryListWithCustomer(
                    categoryWithCustomerLiveData,
                    customerId,
                    name,
                    headers
                )
            } else {
                DatabaseLogManager.getInstance().getProductCategoryListAssignToCustomer(
                    categoryWithCustomerLiveData, customerId, name
                )
            }
        }
    }

    fun deleteProduct(id: Int, isForced: Boolean) {
        viewModelScope.launch {
            repository.deleteProduct(deleteProductLiveData, id, isForced)
        }
    }

    fun getBrandList(name: String, page: Int, hasInternetConnection: Boolean) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                repository.getBrandList(
                    brandListLiveData,
                    name,
                    page,
                    null,
                    null
                )
            } else {
                repository.getOffLineBrandList(brandListLiveData, name, page)
            }
        }
    }


    fun getProductDetails(
        id: Int,
        customerId: Int?,
        hasInternetConnection: Boolean
    ) {
        viewModelScope.launch {
            if (hasInternetConnection) {
                repository.getProductDetails(
                    productDetailsLiveData,
                    id,
                    customerId,
                    false
                )
            } else {
                repository.getOfflineProductDetailsById(productDetailsLiveData, id)
            }
        }
    }

    fun getProductDetailsUsingCode(
        code: String,
        customerId: Int?
    ) {
        viewModelScope.launch {
            repository.getProductDetailsUsingCode(
                productDetailsLiveData,
                code,
                customerId
            )
        }
    }

    fun getProductTelescopicPricing(
        id: Int,
        customerId: Int?
    ) {
        viewModelScope.launch {
            repository.getProductDetails(
                productTelescopicPricingLiveData,
                id,
                customerId,
                true
            )
        }
    }

    fun getCategoryList(name: String?) {
        viewModelScope.launch {
            repository.getCategoryList(productCategoryLiveData, name)
        }
    }

}