package com.ycl.fileselector.ui.adapter

import android.content.Context
import android.database.Cursor
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.ycl.fileselector.internal.data.MediaFilterType
import com.ycl.fileselector.internal.data.MediaItem
import com.ycl.fileselector.ui.preview.audio.AudioFragment


class AudioPageAdapter(
    fm: FragmentManager
    , private val context: Context
    , private val cursor: Cursor?=null
) : FragmentStatePagerAdapter(fm), IPageAdapter {
    override val pageAdapter: PagerAdapter
        get() = this

    override fun getItem(position: Int): Fragment {
        mCursor?.moveToPosition(position)
//        println("AudioPageAdapter.getItem::position::${position}")
        val letMediaItem = mCursor?.let {
//            println("AudioPageAdapter.getItem::let::111")
            val mediaItem = MediaItem.valueOf(it, MediaFilterType.AUDIO)
//            println("AudioPageAdapter.getItem::mediaItem::${mediaItem}")
//            val path = PathUtils.getPath(context, mediaItem.uri)
//            path
            mediaItem

        }
//        println("AudioPageAdapter.getItem::position::${position},finish")
//        val name = letMediaItem?.name
//        var path:String?=""
//        if (null!=letMediaItem?.uri){
//            path = PathUtils.getPath(context, letMediaItem.uri)
//        }
        return AudioFragment.newInstance(letMediaItem)
    }


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


//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        println("AudioPageAdapter.instantiateItem：：position：：${position}")
//        val photoView = PhotoView(context)
//        mCursor?.moveToPosition(position)
//        mCursor?.let {
//            val mediaItem = MediaItem.valueOf(it, MediaFilterType.IMAGE)
//            SelectionSpec.INSTANCE.imageEngine.loadImage(context, photoView, mediaItem.uri)
//        }
//        container.addView(photoView)
//        return photoView
//    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // 覆写destroyItem并且空实现,这样每个Fragment中的视图就不会被销毁
        super.destroyItem(container, position, `object`)
    }


//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view == `object`
//    }


    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}
