/*
 * Copyright (c) 2018 Li Zhao Tai Lang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ycl.fileselector.ui.adapter

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.ycl.fileselector.R
import com.ycl.fileselector.internal.data.MediaFilterType
import com.ycl.fileselector.internal.data.MediaItem

import com.ycl.fileselector.internal.data.SelectedItemCollection
import com.ycl.fileselector.utils.Utils
import com.ycl.fileselector.internal.data.SelectionSpec
import kotlinx.android.synthetic.main.adapter_image_list_item.view.*


class MediaItemsAdapter(
    mediaFilterType: MediaFilterType?,
    selectedItemCollection: SelectedItemCollection
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mImageSize: Int = 0             //每个条目的大小


    private val mMediaFilterType = mediaFilterType
    private var mSelectedItemCollection = selectedItemCollection
    private var mCursor: Cursor? = null
    private var mContext: Context? = null

    /**
     * 更新配置
     */
    fun setNewData(list: List<MediaItem>) {
        this.mSelectedItemCollection.overwrite(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mCursor?.moveToPosition(position)

        mCursor?.let {

            when (mMediaFilterType) {
                MediaFilterType.IMAGE -> {
                    val image = MediaItem.valueOf(it, MediaFilterType.IMAGE)
                    with((holder as MediaItemViewHolder).itemView) {
                        mContext?.let { context ->
                            SelectionSpec.INSTANCE.imageEngine.loadImage(
                                context,
                                iv_thumb,
                                image.uri
                            )
                        }

                        cb_check.isChecked = mSelectedItemCollection.isSelected(image)

                    }
                }
                MediaFilterType.VIDEO -> {
                    val video = MediaItem.valueOf(it, MediaFilterType.VIDEO)
                    with((holder as MediaItemViewHolder).itemView) {
                        mContext?.let { context ->
                            SelectionSpec.INSTANCE.imageEngine.loadImage(
                                context,
                                iv_thumb,
                                video.uri
                            )
                            tv_duration.visibility = View.VISIBLE
                            tv_duration.text = DateUtils.formatElapsedTime(video.duration / 1000)
                        }

                        cb_check.isChecked = mSelectedItemCollection.isSelected(video)

                    }
                }
                MediaFilterType.AUDIO -> {
                    val audio = MediaItem.valueOf(it, MediaFilterType.AUDIO)
                    with((holder as MediaItemViewHolder).itemView) {
                        mContext?.let { context ->
                            iv_thumb.scaleType = ImageView.ScaleType.FIT_XY
                            iv_thumb.setImageResource(R.drawable.ic_audio_default)
                            tv_duration.visibility = View.VISIBLE
                            tv_duration.text = DateUtils.formatElapsedTime(audio.duration / 1000)
                        }

                        cb_check.isChecked = mSelectedItemCollection.isSelected(audio)

                    }

                }
                MediaFilterType.DOCUMENT -> {
                    val document = MediaItem.valueOf(it, MediaFilterType.DOCUMENT)
                    with((holder as MediaItemViewHolder).itemView) {

                    }
                }
                null -> {

                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        return MediaItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_image_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mCursor?.count ?: 0

    init {
        if (selectedItemCollection.mContext is Activity) {
            mImageSize = Utils.getImageItemWidth(selectedItemCollection.mContext)
        }


    }


    /**
     * 得到资源文件中图片的Uri
     * @param context 上下文对象
     * @param id 资源id
     * @return Uri
     */
    fun getUriFromDrawableRes(context: Context, @DrawableRes resId: Int): Uri {
        val resources = context.resources
        val path = StringBuffer()
        path.append(ContentResolver.SCHEME_ANDROID_RESOURCE)
        path.append("://")
        path.append(resources.getResourcePackageName(resId))
        path.append("/")
        path.append(resources.getResourceTypeName(resId))
        path.append("/")
        path.append(resources.getResourceEntryName(resId))
        return Uri.parse(path.toString())
    }


    fun swapCursor(newCursor: Cursor?) {
        mCursor = newCursor
        notifyDataSetChanged()
    }

    /**
     * Item点击事件
     */
    private var mediaItemClickListener: ((item: MediaItem, position: Int) -> Unit)? = null


    /**
     * Item选中点击事件
     */
    private var mediaItemCheckClickListener: ((item: MediaItem, position: Int) -> Unit)? = null


    fun setMediaItemClickListener(func: (item: MediaItem, position: Int) -> Unit) {
        this.mediaItemClickListener = func
    }

    fun setMediaItemCheckClickListener(func: (item: MediaItem, position: Int) -> Unit) {
        this.mediaItemCheckClickListener = func
    }


    inner class MediaItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            //让图片是个正方形
            itemView.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)
            itemView.setOnClickListener {
                mCursor?.moveToPosition(layoutPosition)
                if (mMediaFilterType != null && mCursor != null) {
                    val item = MediaItem.valueOf(mCursor!!, mMediaFilterType)
                    mediaItemClickListener?.invoke(item, layoutPosition)
                }

            }
            itemView.check_view.setOnClickListener {
                if (mMediaFilterType != null && mCursor != null) {
                    mCursor?.moveToPosition(layoutPosition)
                    val item = MediaItem.valueOf(mCursor!!, mMediaFilterType)
                    if (mSelectedItemCollection.isSelected(item)) {
                        mSelectedItemCollection.remove(item)
                        itemView.cb_check.isChecked = false

                    } else {
                        if (mSelectedItemCollection.count() < SelectionSpec.INSTANCE.maxSelectable) {
                            mSelectedItemCollection.add(item)

                            mContext?.let {
                                val ta =
                                    mContext?.obtainStyledAttributes(intArrayOf(R.attr.media_selected_backgroundColor))
                                val color = ta?.getColor(0, 0)
                                ta?.recycle()
                                color?.let {
                                    itemView.setBackgroundColor(it)
                                }
                            }

                            itemView.cb_check.isChecked = true
                        } else {
                            mContext?.let {
                                Toast.makeText(
                                    it,
                                    it.getString(R.string.error_over_count),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                    mediaItemCheckClickListener?.invoke(item, layoutPosition)
                }
            }
        }

    }


}