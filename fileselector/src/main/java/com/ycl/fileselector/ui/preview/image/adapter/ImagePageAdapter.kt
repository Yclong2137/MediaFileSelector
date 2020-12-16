package com.ycl.fileselector.ui.preview.image.adapter

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView
import com.ycl.fileselector.internal.data.MediaFilterType
import com.ycl.fileselector.internal.data.MediaItem

import com.ycl.fileselector.internal.data.SelectionSpec
import com.ycl.fileselector.ui.adapter.IPageAdapter

class ImagePageAdapter(
    private val context: Context,
    private val cursor: Cursor? = null
) : PagerAdapter(), IPageAdapter {
    override val pageAdapter: PagerAdapter
        get() = this


    private var mCursor: Cursor? = null


    init {
        this.mCursor = cursor
    }


    override fun getCount(): Int {
        return mCursor?.count ?: 0
    }


    override fun swapCursor(newCursor: Cursor?) {
        mCursor = newCursor
        notifyDataSetChanged()
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = PhotoView(context)
        mCursor?.moveToPosition(position)
        mCursor?.let {
            val mediaItem = MediaItem.valueOf(it, MediaFilterType.IMAGE)
            SelectionSpec.INSTANCE.imageEngine.loadImage(context, photoView, mediaItem.uri)
        }
        container.addView(photoView)
        return photoView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }


    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}
