package com.android.ondutytest.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * @Description 时间工具类
 *
 * @Author GXD
 * @Date 2022/4/22
 */
object TimeUtil {
    /**
     * 获取当前时间
     */
    fun getNowDate(): String {
        val simpleDateFormat = SimpleDateFormat("yy-MM-dd-HH-mm-ss", Locale.CHINA)
        return simpleDateFormat.format(Date())
    }

    fun getFormatTime(seconds: Int): String {
        var hour = 0
        var minute = 0
        var second = 0
        if (seconds >= 60) {
            minute = seconds / 60
            second = seconds % 60
            if (minute >= 60) {
                hour = minute / 60
                minute %= 60
            }
        } else {
            second = seconds
        }
        return String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" +
                String.format("%02d", second)
    }
}