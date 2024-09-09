package com.app.rupyz.ui.discovery

import com.app.rupyz.model_kt.ProductSource

interface DiscoverySelectedListener {
    fun onProductClick(slug: String?, product: ProductSource?)
    fun onOrgClick(slug: String?)
    fun onProductShare(product: ProductSource?)
}