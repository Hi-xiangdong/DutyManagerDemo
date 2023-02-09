package com.android.ondutytest

import android.app.Application
import com.android.ondutytest.model.SDCardInfo
import com.android.ondutytest.model.database.AppDatabase
import com.android.ondutytest.model.database.PersonInfo
import com.android.ondutytest.model.database.PersonInfoDao
import com.android.ondutytest.util.SDCardUtil

/**
 * @Description 应用主类
 *
 * @Author GXD
 * @Date 2022.11.23
 */
class DutyApplication : Application() {
    //数据库操作对象
    lateinit var dataDao: PersonInfoDao
    //usb列表
    lateinit var usbList: ArrayList<SDCardInfo>
    var personOnDuty: List<PersonInfo>? = null

    companion object {
        lateinit var instance: DutyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        dataDao = AppDatabase.getDatabase(this).personInfoDao()
        usbList = SDCardUtil.getSDCards(this)
    }
}