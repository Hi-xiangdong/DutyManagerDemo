package com.android.ondutytest.presenter

import android.content.Context
import android.telephony.SmsManager
import androidx.core.content.ContextCompat.getSystemService
import com.android.ondutytest.DutyApplication
import com.android.ondutytest.model.database.PersonInfo
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Description 业务处理类
 *
 * @Author GXD
 * @Date 2022.11.29
 */
class MainPresenter(private val context: Context) {

    fun loadPersonInfo(): List<PersonInfo> {
        return DutyApplication.instance.dataDao.loadAllPersonInfo()
    }

    fun deleteAll() {
        DutyApplication.instance.dataDao.deleteAll()
    }

    /**
     * @Description:判断是谁值日
     *
     * @param list 所有人员列表
     * @return 值日生列表
     */
    fun judgeWhoIsOnDuty(list: List<PersonInfo>): List<PersonInfo>? {
        if (list.isEmpty()) return null
        val resList = ArrayList<PersonInfo>()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val nowDate = simpleDateFormat.format(Date())
        for (person in list) {
            if (nowDate >= person.startDate && nowDate <= person.endDate) {
                resList.add(person)
            }
        }
        return resList
    }

    /**
     * @Description:得到值日生字符串
     *
     * @param list 值日人员列表
     * @return 值日生字符串
     */
    fun getNameStringFromList(list: List<PersonInfo>?): String {
        if (list == null) return "值日生：暂无安排"
        var res = "值日生："
        for (person in list) {
            res = res + person.name + " "
        }
        return res
    }

    fun getAdminFormList(list: List<PersonInfo>): PersonInfo? {
        for (person in list) {
            if (person.isAdmin) {
                return person
            }
        }
        return null
    }
}