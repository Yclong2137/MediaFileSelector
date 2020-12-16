package com.ycl.fileselector.ui.preview.audio

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.ycl.fileselector.R
import com.ycl.fileselector.internal.data.MediaItem

import com.ycl.fileselector.utils.PathUtils
import kotlinx.android.synthetic.main.frag_voice_play.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AudioFragment : BaseFragmentJudgeVisible() {
    companion object {
        const val LITIFANT_ID = "litigant_id"
        const val SHOW_MEDIAITEM= "show_mediaitem"
        const val SHOW_MEDIAITEM_NAME = "show_mediaitem_name"
        const val SHOW_MEDIAITEM_PATH = "show_mediaitem_path"
        fun newInstance(mediaItem: MediaItem?): AudioFragment {
            val fragment = AudioFragment()
            val bundle = Bundle()
            bundle.putParcelable(SHOW_MEDIAITEM, mediaItem)
            fragment.arguments = bundle
            return fragment
        }
    }
    /**
     *媒体文件
     */
    private val mediaItem by lazy { arguments?.getParcelable<MediaItem>(SHOW_MEDIAITEM) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("AudioFragment.onCreateView")
        val view = inflater.inflate(R.layout.frag_voice_play, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        println("AudioFragment.initView::")
        if (mediaItem != null) {
            val name = mediaItem?.name
            var path: String? = ""
            if (null != mediaItem?.uri) {
                path = PathUtils.getPath(requireContext(), mediaItem!!.uri)
            }

            filePath = path
            if (!path.isNullOrEmpty()) {
                addWavFile(path)
            }
            bt_start.setOnClickListener {
                if (!isCanPlay) {
                    Toast.makeText(context, "文件不存在，无法播放", LENGTH_SHORT).show()

                } else if (!isPlaying()) {
                    startPlaying()
                }
            }
            bt_pause_resume.setOnClickListener {
                if (isPlaying()) {
                    pausePlay()
                }
            }
            tv_title?.text = name
        }
    }

    var player: MediaPlayer? = null
    var filePath: String? = ""
    var isCanPlay: Boolean = false
    fun addWavFile(path: String) {
        try {
            val file = File(path)
            if (player == null) {
                player = MediaPlayer()
            }
            if (file.exists()) {
                filePath = file.absolutePath
                player?.reset()
                player?.setDataSource(filePath)
                player?.prepare()
//            player?.setLooping(true)
                // play over call back
                player?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                    stopPlaying("1")
                })

                isCanPlay = true
            } else {
                isCanPlay = false
                Toast.makeText(context, "文件不存在", LENGTH_SHORT).show()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun stopPlaying(status: String = "") {
        if (player != null) {
            player?.stop()
            player?.reset()
            bt_start?.visibility = View.VISIBLE
            bt_pause_resume?.visibility = View.GONE
            if (status == "1" && !filePath.isNullOrEmpty()) {
                try {
                    player?.setDataSource(filePath)
                    player?.prepare()
//            player?.setLooping(true)
                    // play over call back
                    player?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                        stopPlaying("1")
                    })

                    tv_voice_time?.setText("0:00")
                } catch (e: Exception) {

                }
            }
        }
    }

    //--------------------------------------------------------------------------
    var mTimerTask: TimerTask? = null
    var mTimer: Timer? = null
    fun startPlaying() {
        if (player != null) {
            player?.start()
            bt_start.visibility = View.GONE
            bt_pause_resume.visibility = View.VISIBLE

            //----------定时器记录播放进度---------//
            mTimerTask = object : TimerTask() {
                override fun run() {
                    if (activity != null) {
                        activity!!.runOnUiThread(Runnable {
                            if (isPlaying()) {
                                val curPosition = player!!.getCurrentPosition()

                                tv_voice_time.text = formatTime(curPosition)
                            }
                        })
                    }

                }
            }
            if (mTimer == null) {
                mTimer = Timer()
            }
            mTimer!!.schedule(mTimerTask, 0, 10)

        }
    }

    fun formatTime(ms: Int): String {
        val dateFormat = SimpleDateFormat("m:ss")
        return dateFormat.format(Date(ms.toLong()))
    }

    fun pausePlay() {
        if (player != null) {
            player?.pause()
            bt_start.visibility = View.VISIBLE
            bt_pause_resume.visibility = View.GONE
        }
    }

    fun isPlaying(): Boolean {
        try {
            return player != null && player!!.isPlaying()
        } catch (e: Exception) {
            return false
        }

    }

    /**
     * 清除多个重要引用
     */
    private fun clearRelation() {
        stopPlaying()
        if (player != null) {
            player = null
        }
        cancelTimer()
    }

    fun cancelTimer() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
        if (mTimerTask != null) {
            mTimerTask!!.cancel()
            mTimerTask = null
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        println("AudioFragment.setUserVisibleHint::isVisibleToUser::${isVisibleToUser}")

    }

    /**
     * 设置图片颜色
     */
    private fun setDrawableColor(resId: Int, color: Int, target: ImageView) {
        val vectorDrawableCompat =
            VectorDrawableCompat.create(resources, resId, resources.newTheme())
        vectorDrawableCompat?.setTint(color)
        target.setImageResource(resId)
    }
    private fun animateVoice(maxPeak: Float) {
        if (maxPeak < 0f || maxPeak > 0.5f) {
            return
        }
        iv_voice_img?.animate()
            ?.scaleX(1 + maxPeak)
            ?.scaleY(1 + maxPeak)
            ?.setDuration(10)
            ?.start()
    }
    override fun onVisible() {
        super.onVisible()
        println("AudioFragment.onVisible::filePath::${filePath}")

    }

    override fun onInVisible() {
        super.onInVisible()
        println("AudioFragment.onInVisible::filePath::${filePath}")
        stopPlaying("1")
        cancelTimer()
    }

    override fun onStart() {
        super.onStart()
        println("AudioFragment.onStart::filePath::${filePath}")
    }

    override fun onPause() {
        super.onPause()
        println("AudioFragment.onPause::filePath::${filePath}")
    }

    override fun onStop() {
        super.onStop()
        println("AudioFragment.onStop::filePath::${filePath}")

    }

    override fun onDestroyView() {
        clearRelation()
        super.onDestroyView()
        println("AudioFragment.onDestroyView::filePath::${filePath}")

    }

    override fun onDestroy() {
        super.onDestroy()
        println("AudioFragment.onDestroy::filePath::${filePath}")

    }
}