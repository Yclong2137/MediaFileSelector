package com.ycl.fileselector.ui.preview.image

import com.ycl.fileselector.internal.data.SelectedItemCollection
import com.ycl.fileselector.ui.adapter.IPageAdapter
import com.ycl.fileselector.ui.preview.image.adapter.ImagePageAdapter
import com.ycl.fileselector.ui.preview.BasePreviewActivity

/**
 * 图片预览
 */
class ImagePreviewActivity : BasePreviewActivity() {
    override fun providerAdapter(): IPageAdapter {
        return ImagePageAdapter(this, mCursor)
    }


    private var mSelectedCollection = SelectedItemCollection(this)


    override fun provideSelectedItemCollection(): SelectedItemCollection {
        return mSelectedCollection
    }


}