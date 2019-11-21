package com.ycl.fileselector.ui.preview.video.adapter

import android.database.Cursor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.ycl.fileselector.internal.data.MediaFilterType
import com.ycl.fileselector.internal.data.MediaItem

import com.ycl.fileselector.ui.adapter.IPageAdapter
import com.ycl.fileselector.ui.preview.video.VideoPreviewFragment

class VideoPageAdapter(cursor: Cursor?, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), IPageAdapter {

    private var mCursor: Cursor? = null

    init {
        this.mCursor = cursor
    }

    override fun getItem(position: Int): Fragment {
        mCursor?.moveToPosition(position)
        val mediaItem = mCursor?.let {
            MediaItem.valueOf(it, MediaFilterType.VIDEO)
        }
        return VideoPreviewFragment.newInstance(mediaItem)
    }

    override fun getCount(): Int {
        return mCursor?.count ?: 0
    }

    override val pageAdapter: PagerAdapter
        get() = this


    override fun swapCursor(newCursor: Cursor?) {
        this.mCursor = newCursor
        notifyDataSetChanged()
    }
}