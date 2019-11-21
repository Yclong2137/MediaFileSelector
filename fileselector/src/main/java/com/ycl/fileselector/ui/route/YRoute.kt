package com.ycl.fileselector.ui.route

import android.content.Intent
import androidx.fragment.app.FragmentManager

class YRoute(fm: FragmentManager) {

    private val routerFragment: RouterFragment

    init {
        routerFragment = getRouterFragment(fm)
    }


    private fun getRouterFragment(fm: FragmentManager): RouterFragment {
        var findFragment = findFragment(fm)
        if (null == findFragment) {
            findFragment = RouterFragment.newInstance()
            fm.beginTransaction()
                .add(findFragment, TAG)
                .commitAllowingStateLoss()
            fm.executePendingTransactions()
        }

        return findFragment!!
    }


    fun startActivityForResult(
        clazz: Class<*>,
        callback: (resultCode: Int, data: Intent?) -> Unit
    ) {
        val intent = Intent(routerFragment.requireContext(), clazz)
        routerFragment.startActivityForResult(intent, callback)
    }


    fun startActivityForResult(intent: Intent, callback: (resultCode: Int, data: Intent?) -> Unit) {
        routerFragment.startActivityForResult(intent, callback)
    }


    private fun findFragment(fm: FragmentManager): RouterFragment? {
        return fm.findFragmentByTag(TAG) as? RouterFragment
    }

    companion object {
        private const val TAG = "YRoute"
    }

}