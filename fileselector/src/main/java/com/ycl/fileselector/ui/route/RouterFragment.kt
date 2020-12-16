package com.ycl.fileselector.ui.route

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.absoluteValue

/**
 * 路由fragment
 */
class RouterFragment : Fragment() {
    private val mCallbacks = SparseArray<(resultCode: Int, data: Intent?) -> Unit>()
    private val mCodeGenerator = Random()

    private val requestCodeSet = HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }


    fun startActivityForResult(intent: Intent, callback: (resultCode: Int, data: Intent?) -> Unit) {
        val requestCode = makeRequestCode()
        mCallbacks.put(requestCode, callback)
        startActivityForResult(intent, requestCode)
    }


    /**
     * 生成唯一的requestCode
     */
    private fun makeRequestCode(): Int {
        var code: Int
        do {
            code = mCodeGenerator.nextInt(1000).absoluteValue
            if (!requestCodeSet.contains(code)) {
                requestCodeSet.add(code)
            }
        } while (!requestCodeSet.contains(code))

        return code
    }


    companion object {
        fun newInstance() = RouterFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val callback = mCallbacks.get(requestCode)
        callback?.invoke(resultCode, data)
        mCallbacks.remove(requestCode)
    }

}