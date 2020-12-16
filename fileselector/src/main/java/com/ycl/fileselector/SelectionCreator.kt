/*
 * Inspired by [Matisse][https://github.com/zhihu/Matisse].
 *
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

package com.ycl.fileselector

import android.content.Intent
import android.content.pm.ActivityInfo
import com.ycl.fileselector.engine.ImageEngine
import com.ycl.fileselector.internal.data.MediaFilterType
import com.ycl.fileselector.internal.data.SelectionSpec
import com.ycl.fileselector.ui.FileSelectorActivity
import com.ycl.fileselector.ui.route.YRoute

class SelectionCreator(
    private val mFileSelector: FileSelector,
    mediaFilterType: MediaFilterType
) {

    private val mSelectionSpec = SelectionSpec.cleanInstance

    init {
        mSelectionSpec.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mSelectionSpec.mediaFilterType = mediaFilterType
    }

    fun maxSelectable(maxSelectable: Int): SelectionCreator {
        if (maxSelectable < 1) {
            throw IllegalArgumentException("maxSelectable must be greater than or equal to one")
        }
        if (mSelectionSpec.maxSelectable > 0) {
            throw  IllegalStateException("already set maxSelectable")
        }
        mSelectionSpec.maxSelectable = maxSelectable
        return this
    }

    /**
     * 已选中的媒体文件
     */
    fun alreadySelectedIds(alreadySelectedIds: ArrayList<String>?): SelectionCreator {
        mSelectionSpec.alreadySelectedIds = alreadySelectedIds
        return this
    }




    /**
     * 最小的媒体文件大小
     */
    fun minMediaSize(minMediaSize: Int): SelectionCreator {
        mSelectionSpec.minMediaSize = minMediaSize
        return this
    }


    fun progressRate(isShow: Boolean): SelectionCreator {
        mSelectionSpec.isShowProgressRate = isShow
        return this
    }

//    fun theme(@StyleRes themeId: Int): SelectionCreator {
//        mSelectionSpec.themeId = themeId
//        return this
//    }

    fun imageEngine(imageEngine: ImageEngine): SelectionCreator {
        mSelectionSpec.imageEngine = imageEngine
        return this
    }

    fun restrictOrientation(screenOrientation: Int): SelectionCreator {
        mSelectionSpec.orientation = screenOrientation
        return this
    }


    fun forResult(callback: (resultCode: Int, data: Intent?) -> Unit) {
        val target = mFileSelector.getActivity()
        target?.let {
            val intent = Intent(it, FileSelectorActivity::class.java)
            YRoute(it.supportFragmentManager).startActivityForResult(intent, callback)
        }

    }


}