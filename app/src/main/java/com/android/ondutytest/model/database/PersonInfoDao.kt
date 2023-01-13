package com.android.ondutytest.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * @Description 数据库操作
 *
 * @Author GXD
 * @Date 2022.11.23
 */
@Dao
interface PersonInfoDao {
    @Insert
    fun insertPerson(person: PersonInfo)

    @Query("select * from PersonInfo")
    fun loadAllPersonInfo(): List<PersonInfo>

    @Delete
    fun deletePersonInfo(person: PersonInfo)

    @Query("delete from PersonInfo")
    fun deleteAll()
}