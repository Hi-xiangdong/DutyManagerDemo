package com.android.ondutytest.util

import android.content.Context
import android.telephony.SmsManager
import java.lang.Exception

/**
 * @Description 发送短信工具类
 *
 * @Author GXD
 * @Date 2023.1.12
 */
object SmsUtil {
    fun sendMessage(context: Context, number: String, content: String) {
        try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(
                number, null, content,
                null, null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}