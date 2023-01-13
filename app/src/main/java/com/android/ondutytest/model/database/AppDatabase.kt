package com.android.ondutytest.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * @Description 数据库
 *
 * @Author GXD
 * @Date 2022.11.23
 */
@Database(version = 1, entities = [PersonInfo::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun personInfoDao(): PersonInfoDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let { return it }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build().apply { instance = this }
        }
    }
}