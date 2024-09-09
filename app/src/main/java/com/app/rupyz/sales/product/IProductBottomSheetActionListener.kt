package com.app.rupyz.sales.product

import com.app.rupyz.generic.model.product.ProductDetailInfoModel

interface IProductBottomSheetActionListener {
    fun onProductAddToCartFromBottomSheet(model: ProductDetailInfoModel, position: Int)
    fun onDismissDialog(){}

    fun onDismissDialogWithMessage(message: String){}
}