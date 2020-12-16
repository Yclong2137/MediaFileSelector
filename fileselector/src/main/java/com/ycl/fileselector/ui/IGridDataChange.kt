package com.ycl.fileselector.ui

import com.ycl.fileselector.internal.data.MediaItem

interface IGridDataChange {

    fun onDataChange(data: List<MediaItem>)
}