package com.ycl.fileselector.ui.adapter

import android.database.Cursor
import androidx.viewpager.widget.PagerAdapter

interface IPageAdapter {

    val pageAdapter: PagerAdapter

    fun swapCursor(newCursor: Cursor?)
}