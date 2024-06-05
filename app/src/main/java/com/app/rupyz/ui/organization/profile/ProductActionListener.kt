package com.app.rupyz.ui.organization.profile

import com.app.rupyz.generic.model.profile.product.ProductList

interface ProductActionListener {
    fun onEditProduct(
        product: ProductList,
        position: Int
    )

    fun onDeleteProduct(
        product: ProductList,
        position: Int
    )

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