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
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import com.ycl.fileselector.FileSelector
import com.ycl.fileselector.R
import com.ycl.fileselector.internal.data.MediaFilterType
import com.ycl.fileselector.internal.data.MediaItem


import com.ycl.fileselector.internal.data.SelectionProvider
import com.ycl.fileselector.internal.data.SelectionSpec
import com.ycl.fileselector.internal.loader.MediaItemLoader
import com.ycl.fileselector.ui.adapter.MediaItemsAdapter
import com.ycl.fileselector.ui.view.GridSpacingItemDecoration
import com.ycl.fileselector.utils.Utils
import com.ycl.fileselector.ui.preview.BasePreviewActivity
import com.ycl.fileselector.ui.preview.audio.AudioPreviewActivity
import com.ycl.fileselector.ui.preview.image.ImagePreviewActivity
import com.ycl.fileselector.ui.preview.video.VideoPreviewActivity
import com.ycl.fileselector.ui.route.YRoute

import kotlinx.android.synthetic.main.fragment_selector.*

class FileSelectorFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>, IGridDataChange {
    override fun onDataChange(data: List<MediaItem>) {
        refresh(data)
    }


    private var mFilterType: MediaFilterType? = null
    private lateinit var mMediaItemsAdapter: MediaItemsAdapter
    private lateinit var mSelectionProvider: SelectionProvider
    private lateinit var mSelectionSpec: SelectionSpec

    companion object {

        private const val ARG_TYPE = "ARG_TYPE"

        @JvmStatic
        fun newInstance(
            filterType: MediaFilterType
        ): FileSelectorFragment {
            val fragment = FileSelectorFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_TYPE, filterType)
            fragment.arguments = bundle
            return fragment
        }

    }

    private var iBarConfig: IBarConfig? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IBarConfig) {
            iBarConfig = context
        }
        if (context is SelectionProvider) {
            mSelectionProvider = context
        } else {
            throw IllegalStateException("Context must implement SelectionProvider.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mSelectionSpec = SelectionSpec.INSTANCE
        return inflater.inflate(R.layout.fragment_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFilterType = arguments?.getSerializable(ARG_TYPE) as MediaFilterType?
        initViews()
        mMediaItemsAdapter.setMediaItemCheckClickListener { _, _ ->
            iBarConfig?.updateTopBar()
            iBarConfig?.updateBottomBar()
        }
        mMediaItemsAdapter.setMediaItemClickListener { item, position ->
            when (item.mediaType) {
                MediaFilterType.IMAGE -> {
                    //媒体文件预览
                    val intent = Intent(requireContext(), ImagePreviewActivity::class.java)
                    intent.putParcelableArrayListExtra(
                        BasePreviewActivity.ARG_SELECTED_DATA,
                        ArrayList(mSelectionProvider.provideSelectedItemCollection().asList())
                    )
                    intent.putExtra(BasePreviewActivity.ARG_SELECTED_INDEX, position)
                    YRoute(childFragmentManager).startActivityForResult(intent) { resultCode, data ->
                        if (Activity.RESULT_OK == resultCode) {
                            val obtainMediaResult =
                                FileSelector.obtainMediaResult(data) ?: emptyList<MediaItem>()
                            //更新配置
                            refresh(obtainMediaResult)


                        }

                    }
                }

                MediaFilterType.VIDEO -> {
                    //媒体文件预览
                    val intent = Intent(requireContext(), VideoPreviewActivity::class.java)
                    intent.putParcelableArrayListExtra(
                        BasePreviewActivity.ARG_SELECTED_DATA,
                        ArrayList(mSelectionProvider.provideSelectedItemCollection().asList())
                    )
                    intent.putExtra(BasePreviewActivity.ARG_SELECTED_INDEX, position)
                    YRoute(childFragmentManager).startActivityForResult(intent) { resultCode, data ->
                        if (Activity.RESULT_OK == resultCode) {
                            val obtainMediaResult =
                                FileSelector.obtainMediaResult(data) ?: emptyList<MediaItem>()
                            //更新配置
                            refresh(obtainMediaResult)


                        }

                    }
                }
                MediaFilterType.AUDIO -> {
                    //媒体文件预览
                    val intent = Intent(requireContext(), AudioPreviewActivity::class.java)
                    intent.putParcelableArrayListExtra(
                        BasePreviewActivity.ARG_SELECTED_DATA,
                        ArrayList(mSelectionProvider.provideSelectedItemCollection().asList())
                    )
                    intent.putExtra(BasePreviewActivity.ARG_SELECTED_INDEX, position)
                    YRoute(childFragmentManager).startActivityForResult(intent) { resultCode, data ->
                        if (Activity.RESULT_OK == resultCode) {
                            val obtainMediaResult =
                                FileSelector.obtainMediaResult(data) ?: emptyList<MediaItem>()
                            //更新配置
                            refresh(obtainMediaResult)


                        }

                    }
                }
                else -> {
                }
            }
        }


        LoaderManager.getInstance(this).initLoader(0, null, this)

    }

    /**
     * 刷新列表
     */
    private fun refresh(obtainMediaResult: List<MediaItem>) {
        mSelectionProvider.provideSelectedItemCollection()
            .overwrite(obtainMediaResult)
        mMediaItemsAdapter.setNewData(obtainMediaResult)
        iBarConfig?.updateTopBar()
        iBarConfig?.updateBottomBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoaderManager.getInstance(this).destroyLoader(MediaItemLoader.LOADER_ID)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
        MediaItemLoader.newInstance(
            context!!,
            mSelectionSpec
        )

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        mMediaItemsAdapter.swapCursor(data)
        emptyTextView.visibility =
            if (mMediaItemsAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mMediaItemsAdapter.swapCursor(null)
    }

    private fun initViews() {
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                3,
                Utils.dp2px(requireContext(), 2f),
                false
            )
        )

        recyclerView.setHasFixedSize(true)
        mMediaItemsAdapter =
            MediaItemsAdapter(mFilterType, mSelectionProvider.provideSelectedItemCollection())
        recyclerView.adapter = mMediaItemsAdapter
    }


}