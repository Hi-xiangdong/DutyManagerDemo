package com.android.ondutytest.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import com.android.ondutytest.model.SDCardInfo
import java.io.File
import java.lang.reflect.InvocationTargetException

/**
 * @Description 获取外部存储分区信息工具类
 *
 * @Author GXD
 * @Date 2022/8/6
 */
object SDCardUtil {

    fun getSDCards(context: Context): ArrayList<SDCardInfo> {
        val sdCards = ArrayList<SDCardInfo>()
        // 存储管理器
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
            ?: return sdCards

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val volumes = storageManager.storageVolumes
                val getPath = StorageVolume::class.java.getMethod("getPath")
                val getUserLabel = StorageVolume::class.java.getMethod("getUserLabel")
                for (volume in volumes) {
                    if (volume.state != Environment.MEDIA_REMOVED) {
                        val info = SDCardInfo()
                        info.path = getPath.invoke(volume) as String
                        info.label = getUserLabel.invoke(volume) as? String
                            ?: volume.getDescription(context) ?: File(info.path).name
                        LogUtil.i(info.path + "  " + info.label)
                        if (!sdCards.contains(info)) {
                            sdCards.add(info)
                        }
                    }
                }
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        } else {
            val storageVolumes: Array<StorageVolume>?
            try {
                val getVolumePath = storageManager.javaClass.getMethod("getVolumeList")
                storageVolumes = getVolumePath.invoke(storageManager) as? Array<StorageVolume>
                val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
                val getPath = storageVolumeClazz.getMethod("getPath")
                val getUserLabel = storageVolumeClazz.getMethod("getUserLabel")
                val getDescription =
                    storageVolumeClazz.getMethod("getDescription", Context::class.java)
                if (storageVolumes !== null && storageVolumes.isNotEmpty()) {
                    for (i in storageVolumes.indices) {
                        val volume = storageVolumes[i]
                        if (volume.state != Environment.MEDIA_REMOVED) {
                            val info = SDCardInfo()
                            info.path = getPath.invoke(volume) as String
                            info.label = getUserLabel.invoke(volume) as? String
                                ?: getDescription.invoke(volume, context) as String
                            LogUtil.i(info.path + "  " + info.label)
                            if (!sdCards.contains(info)) {
                                sdCards.add(info)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        sdCards.removeAt(0)
        return sdCards
    }
}