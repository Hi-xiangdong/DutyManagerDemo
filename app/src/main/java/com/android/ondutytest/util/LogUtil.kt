package com.android.ondutytest.util

import android.util.Log
import com.android.ondutytest.BuildConfig

/**
 * @Description 日志工具类
 *
 * @Author GXD
 * @Date 2022/4/22
 */
object LogUtil {
    //固定全局tag,方便搜索
    private const val TAG = "OnDutyTest"

    //系统日志的栈索引，5是有用的部分
    private const val STACK_INDEX = 5

    private fun getFileName() = Thread.currentThread().stackTrace[STACK_INDEX].fileName


    private fun getMethodName() = Thread.currentThread().stackTrace[STACK_INDEX].methodName

    private fun getLineNumber() = Thread.currentThread().stackTrace[STACK_INDEX].lineNumber.toString()


    private fun getHyperlinkLog() = ".(" + getFileName() + ":" + getLineNumber() + ")" + getMethodName()


    private fun getLogMessage(): String {
        return "File:" + getFileName() + "--->Method:" + getMethodName() +
                "--->Line:" + getLineNumber()
    }

    @JvmStatic
    fun v(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, msg + "--->" + getLogMessage())
        }
    }

    @JvmStatic
    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg + "--->" + getLogMessage())
        }
    }

    @JvmStatic
    fun i(msg: String) {
        Log.i(TAG, msg + "--->" + getHyperlinkLog())
    }

    @JvmStatic
    fun w(msg: String) {
        Log.w(TAG, msg + "--->" + getLogMessage())
    }

    @JvmStatic
    fun e(msg: String) {
        Log.e(TAG, msg + "--->" + getHyperlinkLog())
    }
}