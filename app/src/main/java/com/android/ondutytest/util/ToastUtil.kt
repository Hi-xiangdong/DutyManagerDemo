package com.android.ondutytest.util

import android.content.Context
import android.graphics.Color
import android.widget.Toast

/**
 * @Description Toast工具类
 *
 * @Author GXD
 * @Date 2022/9/16
 */
object ToastUtil {
    var toast: Toast? = null

    fun showShortToastNotRepeat (context: Context, string: String) {
        toast?.cancel()
        toast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        toast?.show()
    }

    fun showShortToast(context: Context, string: String) {
        val toast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun showLongToast(context: Context, string: String) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }

    fun showShortToast(context: Context, resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(context: Context, resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
    }
}