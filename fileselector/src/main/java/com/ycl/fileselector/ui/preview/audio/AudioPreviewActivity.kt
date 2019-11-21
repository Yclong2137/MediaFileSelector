package com.ycl.fileselector.ui.preview.audio
import com.ycl.fileselector.internal.data.SelectedItemCollection
import com.ycl.fileselector.ui.adapter.AudioPageAdapter
import com.ycl.fileselector.ui.adapter.IPageAdapter
import com.ycl.fileselector.ui.preview.BasePreviewActivity
/**
 * 图片预览
 */
class AudioPreviewActivity : BasePreviewActivity() {
    override fun providerAdapter(): IPageAdapter {
        return AudioPageAdapter(supportFragmentManager,this, mCursor)
    }


    private var mSelectedCollection = SelectedItemCollection(this)


    override fun provideSelectedItemCollection(): SelectedItemCollection {
        return mSelectedCollection
    }
}