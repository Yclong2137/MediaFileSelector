package com.ycl.fileselector.internal.data

import com.ycl.fileselector.ui.adapter.IPageAdapter

/**
 * PageAdapter提供器
 */
interface PageAdapterProvider {

    fun providerAdapter(): IPageAdapter

}