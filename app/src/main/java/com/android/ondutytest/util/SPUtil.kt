package com.android.ondutytest.util

import android.content.Context

/**
 * @Description TODO
 *
 * @Author GXD
 * @Date 2023.2.8
 */
object SPUtil {
    private const val FILE_NAME = "duty_sp"

    fun put(context: Context, key: String, value: Any) {
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> editor.putString(key, value.toString())
        }
        editor.apply()
    }

    fun get(context: Context, key: String, defaultValue: Any): Any? {
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return when(defaultValue) {
            is String -> sp.getString(key, defaultValue)
            is Int -> sp.getInt(key, defaultValue)
            is Boolean -> sp.getBoolean(key, defaultValue)
            else -> sp.getString(key, defaultValue.toString())
        }
    }
}