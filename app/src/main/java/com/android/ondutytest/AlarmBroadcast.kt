package com.android.ondutytest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms
import com.android.ondutytest.constant.Constant
import com.android.ondutytest.util.*
import java.util.Calendar

/**
 * @Description 闹钟广播接受类
 *
 * @Author GXD
 * @Date 2022.12.2
 */
class AlarmBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        LogUtil.i("收到定时广播")
        when (intent?.getIntExtra("action", 0)) {
            0 -> {
                //氛围灯变成红色
                DeviceUtil.changeAmbientLight(true, Constant.BREATHE_LAMP_RED)
                val bundle = intent.extras ?: return
                //发短信提醒值日
                val nameList = bundle.getStringArrayList("name")
                val numberList = bundle.getStringArrayList("number")
                if (numberList.isNullOrEmpty()) return
                for (i in numberList.indices) {
                    context.let {
                        SmsUtil.sendMessage(context, numberList[i],
                            "亲爱的${nameList!![i]}，今天轮到您值日了，记得签到哦")
                    }
                }
            }
            1 -> {
                val bundle = intent.extras ?: return
                val number = bundle.getString("number")
                val lightThreshold = SPUtil.get(context, Constant.SP_LIGHT_THRESHOLD, 10) as Int
                //判断关灯，发短信
                if (DeviceUtil.getLightSensitivity() > lightThreshold) {
                    context.let {
                        number?.let { SmsUtil.sendMessage(context, number, "请注意，实验室没有关灯！") }
                        LogUtil.i("没有关灯")
                    }
                }
            }
        }
    }
}