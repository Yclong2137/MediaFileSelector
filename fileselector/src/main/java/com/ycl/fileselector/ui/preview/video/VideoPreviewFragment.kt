package com.ycl.fileselector.ui.preview.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.jzvd.Jzvd
import com.ycl.fileselector.R
import com.ycl.fileselector.internal.data.MediaItem

import com.ycl.fileselector.internal.data.SelectionSpec
import com.ycl.fileselector.utils.PathUtils
import kotlinx.android.synthetic.main.fragment_video_preview.*

/**
 * 视频预览
 */
class VideoPreviewFragment : Fragment() {

    /**
     *媒体文件
     */
    private val mediaItem by lazy { arguments?.getParcelable<MediaItem>(ARG_MEDIA_ITEM_DATA) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_video_preview, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaItem?.let { mediaItem ->
            val path = PathUtils.getPath(requireContext(), mediaItem.uri)
            SelectionSpec.INSTANCE.imageEngine.loadImage(
                requireContext(),
                video.thumbImageView, 
                mediaItem.uri
            )
            video.setUp(path, mediaItem.name)
        }

    }


    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }


    companion object {
        private const val ARG_MEDIA_ITEM_DATA = "ARG_MEDIA_ITEM_DATA"
        fun newInstance(mediaItem: MediaItem?) = VideoPreviewFragment().apply {
            val args = Bundle()
            args.putParcelable(ARG_MEDIA_ITEM_DATA, mediaItem)
            arguments = args
        }
    }

}