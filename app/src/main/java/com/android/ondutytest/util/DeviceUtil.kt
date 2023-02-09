package com.android.ondutytest.util

import com.hikvision.dmb.BreathingLampConfig
import com.hikvision.dmb.display.InfoDisplayApi
import com.hikvision.dmb.util.InfoUtilApi

/**
 * @Description 设备接口工具类
 *
 * @Author GXD
 * @Date 2023.1.12
 */
object DeviceUtil {
    //获取光感值
    fun getLightSensitivity(): Int {
        return InfoDisplayApi.getLtr303LuxValue()
    }

    //获取温度值
    fun getTemperature(): Float {
        return InfoUtilApi.getTemperatureHumdity()[0]
    }

    //获取湿度值
    fun getHumidity(): Float {
        return InfoUtilApi.getTemperatureHumdity()[1]
    }

    //改变氛围灯
    fun changeAmbientLight(isTurnOn: Boolean, color: Int) {
        val breathingLampConfig = BreathingLampConfig()
        //绿色
        breathingLampConfig.colour = color
        if (isTurnOn) {
            //打开呼吸灯(常亮)
            breathingLampConfig.mode = 1
        } else {
            //关闭
            breathingLampConfig.mode = 0
        }
        InfoUtilApi.setBlnControl(breathingLampConfig)
    }
}