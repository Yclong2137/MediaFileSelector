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

package com.ycl.fileselector.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ycl.fileselector.FileSelector
import com.ycl.fileselector.R
import com.ycl.fileselector.internal.data.*

import com.ycl.fileselector.ui.preview.BasePreviewActivity
import com.ycl.fileselector.ui.preview.audio.AudioPreviewActivity
import com.ycl.fileselector.ui.preview.image.ImagePreviewActivity
import com.ycl.fileselector.ui.route.YRoute
import kotlinx.android.synthetic.main.view_bottom_bar_grid.*
import kotlinx.android.synthetic.main.view_top_bar.*

/**
 * Created by lizhaotailang on 30/01/2018.
 */
class FileSelectorActivity : AppCompatActivity(), SelectionProvider, IBarConfig, View.OnClickListener {


    private var mSelectedCollection = SelectedItemCollection(this)
    private lateinit var mSelectionSpec: SelectionSpec

    private var iGridDataChange: IGridDataChange? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        mSelectionSpec = SelectionSpec.INSTANCE
       setTheme(mSelectionSpec.themeId)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_image_grid)

        if (mSelectionSpec.needOrientationRestriction()) {
            requestedOrientation = mSelectionSpec.orientation.toInt()
        }

        initViews()

        mSelectedCollection.onCreate(savedInstanceState)
        updateTopBar()
        updateBottomBar()

        val title = when (mSelectionSpec.mediaFilterType) {
            MediaFilterType.IMAGE -> {
                "图片"
            }
            MediaFilterType.VIDEO -> {
                "视频"
            }
            MediaFilterType.AUDIO -> {
                "音频"
            }
            MediaFilterType.DOCUMENT -> {
                "文件"
            }
        }
        toolbar.title = title
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { finish() }
        val fragment = FileSelectorFragment.newInstance(
            mSelectionSpec.mediaFilterType
        )
        if (fragment is IGridDataChange) {
            this.iGridDataChange = fragment
        }
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                fragment,
                FileSelectorFragment::class.java.simpleName
            )
            .commitAllowingStateLoss()

        initClick()


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_ok -> {
                confirm()
            }
            R.id.btn_preview -> {
                preview()
            }
            else -> {
            }
        }
    }

    /**
     * 预览
     */
    private fun preview() {
        when (mSelectionSpec.mediaFilterType) {
            MediaFilterType.IMAGE -> {
                previewImage()
            }
            MediaFilterType.AUDIO -> {
                previewAudio()
            }
            MediaFilterType.VIDEO -> {
                previewVideo()
            }
            MediaFilterType.DOCUMENT -> {
                previewDocument()
            }
        }
    }

    /**
     * 文档预览
     */
    private fun previewDocument() {

    }

    /**
     * 视频预览
     */
    private fun previewVideo() {

    }

    /**
     * 音频预览
     */
    private fun previewAudio() {
        val intent = Intent(this, AudioPreviewActivity::class.java)
        intent.putParcelableArrayListExtra(
            BasePreviewActivity.ARG_SELECTED_DATA,
            ArrayList(mSelectedCollection.asList())
        )
        YRoute(supportFragmentManager).startActivityForResult(intent) { resultCode, data ->
            if (Activity.RESULT_OK == resultCode) {
                val obtainMediaResult =
                    FileSelector.obtainMediaResult(data) ?: emptyList<MediaItem>()
                //更新配置
                iGridDataChange?.onDataChange(obtainMediaResult)


            }

        }
    }

    /**
     * 图片预览
     */
    private fun previewImage() {
        val intent = Intent(this, ImagePreviewActivity::class.java)
        intent.putParcelableArrayListExtra(
            BasePreviewActivity.ARG_SELECTED_DATA,
            ArrayList(mSelectedCollection.asList())
        )
        YRoute(supportFragmentManager).startActivityForResult(intent) { resultCode, data ->
            if (Activity.RESULT_OK == resultCode) {
                val obtainMediaResult =
                    FileSelector.obtainMediaResult(data) ?: emptyList<MediaItem>()
                //更新配置
                iGridDataChange?.onDataChange(obtainMediaResult)


            }

        }
    }


    /**
     * 确认
     */
    private fun confirm() {
        val result = Intent()
        result.putParcelableArrayListExtra(
            FileSelector.EXTRA_RESULT_SELECTION,
            ArrayList(mSelectedCollection.asListOfUri())
        )
        result.putStringArrayListExtra(
            FileSelector.EXTRA_RESULT_SELECTION_PATH,
            ArrayList(mSelectedCollection.asListOfString())
        )
        result.putParcelableArrayListExtra(
            FileSelector.EXTRA_RESULT_SELECTION_MEDIA,
            ArrayList(mSelectedCollection.asList())
        )
        setResult(Activity.RESULT_OK, result)
        finish()
    }


    private fun initClick() {
        btn_ok.setOnClickListener(this)
        btn_preview.setOnClickListener(this)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.let {
            mSelectedCollection.onSaveInstanceState(it)
        }
    }


    override fun provideSelectedItemCollection(): SelectedItemCollection = mSelectedCollection


    private fun initViews() {


    }

    override fun updateTopBar() {
        btn_ok.text =
            if (mSelectionSpec.isShowProgressRate) getString(R.string.ip_select_complete).format(
                mSelectedCollection.count(),
                mSelectionSpec.maxSelectable
            ) else getString(
                R.string.ip_select_complete,
                mSelectedCollection.count(),
                mSelectionSpec.maxSelectable
            )
    }

    override fun updateBottomBar() {
        btn_preview.text =
            if (mSelectionSpec.isShowProgressRate) getString(R.string.ip_preview_count).format(
                mSelectedCollection.count(),
                mSelectionSpec.maxSelectable
            ) else getString(R.string.ip_preview_count, mSelectedCollection.count())
        btn_preview.isEnabled = mSelectedCollection.isNotEmpty()
    }


}