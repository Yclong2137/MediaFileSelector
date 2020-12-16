package com.ycl.fileselector.ui.preview.video

import com.ycl.fileselector.internal.data.SelectedItemCollection
import com.ycl.fileselector.ui.adapter.IPageAdapter
import com.ycl.fileselector.ui.preview.BasePreviewActivity
import com.ycl.fileselector.ui.preview.video.adapter.VideoPageAdapter

/**
 * 视频预览
 */
class VideoPreviewActivity : BasePreviewActivity() {

    private var mSelectedCollection = SelectedItemCollection(this)

    override fun provideSelectedItemCollection(): SelectedItemCollection {
        return mSelectedCollection
    }

    override fun providerAdapter(): IPageAdapter {
        return VideoPageAdapter(mCursor, supportFragmentManager)
    }
}