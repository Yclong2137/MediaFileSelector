package com.ycl.fileselector

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ycl.fileselector.internal.data.MediaFilterType
import com.ycl.fileselector.internal.data.MediaItem
import java.lang.ref.WeakReference

class FileSelector {
    private var mContext: WeakReference<FragmentActivity>? = null
    private var mFragment: WeakReference<Fragment>? = null

    private constructor(fragment: Fragment) : this(fragment.activity, fragment)

    private constructor(activity: FragmentActivity) : this(activity, null)

    private constructor(activity: FragmentActivity?, fragment: Fragment?) {
        mContext = if (activity != null) WeakReference(activity) else null
        mFragment = if (fragment != null) WeakReference(fragment) else null
    }

    companion object {

        val K = 1024

        const val EXTRA_RESULT_SELECTION = "EXTRA_RESULT_SELECTION"

        const val EXTRA_RESULT_SELECTION_PATH = "EXTRA_RESULT_SELECTION_PATH"

        const val EXTRA_RESULT_SELECTION_MEDIA = "EXTRA_RESULT_SELECTION_MEDIA"


        @JvmStatic
        fun from(activity: FragmentActivity) = FileSelector(activity)


        @JvmStatic
        fun from(fragment: Fragment) = FileSelector(fragment)

        @JvmStatic
        fun obtainResult(data: Intent?) =
            data?.getParcelableArrayListExtra<Uri>(EXTRA_RESULT_SELECTION)

        @JvmStatic
        fun obtainPathResult(data: Intent?) =
            data?.getStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH)


        @JvmStatic
        fun obtainMediaResult(data: Intent?) =
            data?.getParcelableArrayListExtra<MediaItem>(EXTRA_RESULT_SELECTION_MEDIA)

    }

    fun getActivity() = mContext?.get()

    fun getFragment() = mFragment?.get()

    fun choose(
        mediaFilterType: MediaFilterType
    ): SelectionCreator =
        SelectionCreator(this, mediaFilterType)
}