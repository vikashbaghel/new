package com.app.rupyz.ui.organization.profile

import com.app.rupyz.generic.model.profile.product.ProductList

interface ProductActionListener {

    fun onShareProduct(
        product: ProductList,
        position: Int
    )

    fun getProductDetails(
        product: ProductList,
        position: Int
    )

    fun getPackagingLevelInfo(model: ProductList)

}