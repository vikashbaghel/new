package com.app.rupyz.sales.orders

import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.model_kt.PackagingLevelModel

interface OrderActionListener {
    fun onAddToCart(model: ProductList, position: Int)
    fun onGetProductInfo(model: ProductList, position: Int)
    fun onChangeQuantity(model: ProductList, qty: Double)
    fun getPriceSlabInfo(model: ProductList)
    fun getPackagingLevelInfo(model: ProductList)
    fun onNextButtonClick()
    fun changePackagingLevel(
        model: ProductList,
        position: Int,
        selectedPackagingLevel: PackagingLevelModel
    )
}