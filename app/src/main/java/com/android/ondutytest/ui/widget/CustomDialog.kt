package com.android.ondutytest.ui.widget

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View

/**
 * @Description 自定义对话框
 *
 * @Author GXD
 * @Date 2022.11.28
 */
class CustomDialog(context: Context, width: Int = WIDTH, height: Int = HEIGHT, layout: View)
    : Dialog(context) {
    companion object {
        private const val WIDTH = 560
        private const val HEIGHT = 320
    }
    init {
        setContentView(layout)
        val params = window?.attributes
        params?.gravity = Gravity.CENTER
        params?.width = width
        params?.height = height
        window?.attributes = params
    }
}