package com.android.ondutytest.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Description 人员信息数据结构
 *
 * @Author GXD
 * @Date 2022.11.16
 */
@Entity
data class PersonInfo(var name: String, var phoneNumber: String, var startDate: String,
                      var endDate: String, var isAdmin: Boolean) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
