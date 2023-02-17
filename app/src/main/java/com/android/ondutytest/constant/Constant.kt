package com.android.ondutytest.constant

/**
 * @Description 全局常量
 *
 * @Author GXD
 * @Date 2022.11.24
 */
object Constant {
    //从U盘的此目录导入
    const val PATH_EXCEL = "duty"

    //权限请求码
    const val PERMISSION_REQUEST_CODE = 1

    //定时提醒值日闹钟请求码
    const val WARN_REQUEST_CODE = 0x102

    //定时判断关灯请求码
    const val TURNOFF_REQUEST_CODE = 0x104

    const val SP_LIGHT_THRESHOLD = "light_threshold"

    const val BREATHE_LAMP_GREEN = 0
    const val BREATHE_LAMP_RED = 1

    const val BLUETOOTH_DEVICE = "84:C2:E4:03:02:02"
}