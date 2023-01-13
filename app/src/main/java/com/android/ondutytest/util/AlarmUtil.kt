package com.android.ondutytest.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.android.ondutytest.AlarmBroadcast
import com.android.ondutytest.constant.Constant
import java.util.*

/**
 * @Description 闹钟工具类
 *
 * @Author GXD
 * @Date 2022.12.2
 */
object AlarmUtil {
    /**
     * @Description:
     *
     * @param bundle:闹钟触发后发送广播时传递的数据 calendar:设置闹钟的日期
     * @return
     */
    fun setAlarm(calendar: Calendar, context: Context, type: Int, bundle: Bundle) {
        val intent = Intent(context, AlarmBroadcast::class.java)
        var pendingIntent: PendingIntent? = null
        when (type) {
            0 -> {
                intent.putExtra("action", 0)
                intent.putExtras(bundle)
                pendingIntent = PendingIntent.getBroadcast(context, Constant.WARN_REQUEST_CODE,
                    intent, 0)
            }
            1 -> {
                intent.putExtra("action", 1)
                intent.putExtras(bundle)
                pendingIntent = PendingIntent.getBroadcast(context, Constant.TURNOFF_REQUEST_CODE,
                    intent, 0)
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}