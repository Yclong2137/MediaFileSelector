package com.ycl.fileselector.ui.preview

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.viewpager.widget.ViewPager
import com.ycl.fileselector.FileSelector
import com.ycl.fileselector.R
import com.ycl.fileselector.internal.data.MediaItem
import com.ycl.fileselector.internal.data.PageAdapterProvider


import com.ycl.fileselector.internal.data.SelectionProvider
import com.ycl.fileselector.internal.data.SelectionSpec
import com.ycl.fileselector.internal.loader.MediaItemLoader
import com.ycl.fileselector.ui.IBarConfig
import com.ycl.fileselector.ui.adapter.IPageAdapter
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.view_bottom_bar_preview.*
import kotlinx.android.synthetic.main.view_top_bar.*

/**
 * 媒体文件预览基类
 */
abstract class BasePreviewActivity : AppCompatActivity(), View.OnClickListener, SelectionProvider,
    PageAdapterProvider, IBarConfig,
    ViewPager.OnPageChangeListener, LoaderManager.LoaderCallbacks<Cursor> {


    private lateinit var mSelectionSpec: SelectionSpec


    private lateinit var mViewPager: ViewPager


    private lateinit var iPageAdapter: IPageAdapter


    protected var mCursor: Cursor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { finish() }
        mSelectionSpec = SelectionSpec.INSTANCE
        provideSelectedItemCollection().onCreate(savedInstanceState)
        provideSelectedItemCollection().overwrite(
            intent.getParcelableArrayListExtra(
                ARG_SELECTED_DATA
            )
        )
        provideSelectedItemCollection().position =
            intent.getIntExtra(ARG_SELECTED_INDEX, 0)
        initView()
        initClick()
    }

    private fun initView() {
        mViewPager = viewpager
        iPageAdapter = providerAdapter()
        mViewPager.addOnPageChangeListener(this)
        mViewPager.adapter = iPageAdapter.pageAdapter
        mViewPager.post {
            mViewPager.currentItem = provideSelectedItemCollection().position
        }
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    private fun initClick() {
        btn_select.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
    }


    private fun select(originChecked: Boolean, position: Int) {
        mCursor?.moveToPosition(position)
        mCursor?.let {
            val mediaItem = MediaItem.valueOf(it, mSelectionSpec.mediaFilterType)
            if (originChecked) {
                provideSelectedItemCollection().remove(mediaItem)
            } else {
                if (provideSelectedItemCollection().count() < mSelectionSpec.maxSelectable) {
                    provideSelectedItemCollection().add(mediaItem)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.ip_select_limit, mSelectionSpec.maxSelectable),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

            }
            updateTopBar()
            updateBottomBar()
            btn_select.isChecked = !originChecked
        }
    }

    private fun confirm() {
        val result = Intent()
        result.putParcelableArrayListExtra(
            FileSelector.EXTRA_RESULT_SELECTION,
            ArrayList(provideSelectedItemCollection().asListOfUri())
        )
        result.putStringArrayListExtra(
            FileSelector.EXTRA_RESULT_SELECTION_PATH,
            ArrayList(provideSelectedItemCollection().asListOfString())
        )
        result.putParcelableArrayListExtra(
            FileSelector.EXTRA_RESULT_SELECTION_MEDIA,
            ArrayList(provideSelectedItemCollection().asList())
        )
        setResult(Activity.RESULT_OK, result)
        finish()
    }


    override fun updateTopBar() {
        toolbar.title = getString(
            R.string.ip_preview_image_count,
            mViewPager.currentItem + 1,
            getTotalCount()
        )
        btn_ok.text =
            if (mSelectionSpec.isShowProgressRate) getString(R.string.ip_select_complete).format(
                provideSelectedItemCollection().count(),
                mSelectionSpec.maxSelectable

            ) else getString(
                R.string.ip_select_complete,
                provideSelectedItemCollection().count(),
                mSelectionSpec.maxSelectable
            )
    }


    override fun updateBottomBar() {
        mCursor?.moveToPosition(mViewPager.currentItem)
        mCursor?.let {
            val mediaItem = MediaItem.valueOf(it, mSelectionSpec.mediaFilterType)
            btn_select.isChecked = provideSelectedItemCollection().isSelected(mediaItem)
        }

    }


    /**
     * 获取全部Image数量
     */
    private fun getTotalCount(): Int {
        return mCursor?.count ?: 0
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_ok -> {
                confirm()
            }
            R.id.btn_select -> {
                select(btn_select.isChecked, mViewPager.currentItem)
            }
            else -> {
            }
        }
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return MediaItemLoader.newInstance(
            this, mSelectionSpec
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        this.mCursor = data
        updateTopBar()
        updateBottomBar()
        iPageAdapter.swapCursor(data)

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        iPageAdapter.swapCursor(null)
    }


    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        updateTopBar()
        updateBottomBar()
    }


    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.let {
            provideSelectedItemCollection().onSaveInstanceState(it)
        }
    }


    override fun onDestroy() {
        viewpager.removeOnPageChangeListener(this)
        super.onDestroy()
    }


    companion object {
        const val ARG_SELECTED_DATA = "ARG_SELECTED_DATA"
        const val ARG_SELECTED_INDEX = "ARG_SELECTED_INDEX"

    }


}