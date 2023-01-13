package com.android.ondutytest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms
import com.android.ondutytest.util.SmsUtil

/**
 * @Description 闹钟广播接受类
 *
 * @Author GXD
 * @Date 2022.12.2
 */
class AlarmBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.getIntExtra("action", 0)) {
            0 -> {
                val bundle = intent.extras ?: return
                //发短信提醒值日
                val nameList = bundle.getStringArrayList("name")
                val numberList = bundle.getStringArrayList("number")
                if (numberList.isNullOrEmpty()) return
                for (i in numberList.indices) {
                    context?.let {
                        SmsUtil.sendMessage(context, numberList[i],
                            "亲爱的${nameList!![i]}，今天轮到您值日了，记得签到哦")
                    }
                }
            }
            1 -> {
                val bundle = intent.extras ?: return
                val number = bundle.getString("number")
                //判断关灯，发短信
                if (bundle.getBoolean("isSend")) {
                    context?.let {
                        number?.let { SmsUtil.sendMessage(context, number, "请注意，实验室没有关灯！") }
                    }
                }
            }
        }
    }
}