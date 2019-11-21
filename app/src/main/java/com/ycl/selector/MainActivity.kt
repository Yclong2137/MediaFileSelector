package com.ycl.selector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ycl.fileselector.FileSelector
import com.ycl.fileselector.internal.data.MediaFilterType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_photo.setOnClickListener {
            FileSelector.from(this)
                .choose(MediaFilterType.IMAGE)
                .maxSelectable(9)
                .forResult { resultCode, data ->

                }
        }
        btn_video.setOnClickListener {
            FileSelector.from(this)
                .choose(MediaFilterType.VIDEO)
                .maxSelectable(9)
                .forResult { resultCode, data ->

                }
        }
        btn_audio.setOnClickListener {
            FileSelector.from(this)
                .choose(MediaFilterType.AUDIO)
                .maxSelectable(9)
                .forResult { resultCode, data ->

                }
        }
    }
}
