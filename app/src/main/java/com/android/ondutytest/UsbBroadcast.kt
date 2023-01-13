package com.android.ondutytest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.widget.Toast
import com.android.ondutytest.model.SDCardInfo
import com.android.ondutytest.util.LogUtil

/**
 * @Description U盘插入广播接收
 *
 * @Author GXD
 * @Date 2022.11.24
 */
class UsbBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val path = intent.data?.path
        if (path.isNullOrEmpty()) {
            return
        }
        val storage = getStorageVolume(context, intent)
        val getUserLabel = StorageVolume::class.java.getMethod("getUserLabel")
        var label = ""
        storage?.let {
            label = getUserLabel.invoke(storage) as? String ?: ""
        }
        val info = SDCardInfo(label, path)
        when (intent.action) {
            Intent.ACTION_MEDIA_MOUNTED -> {
                Toast.makeText(context, "发现外部存储", Toast.LENGTH_SHORT).show()
                if (!DutyApplication.instance.usbList.contains(info)) {
                    DutyApplication.instance.usbList.add(info)
                }
                LogUtil.d(DutyApplication.instance.usbList.toString())
            }
            //Android11
            Intent.ACTION_MEDIA_EJECT -> {
                if (DutyApplication.instance.usbList.contains(info)) {
                    DutyApplication.instance.usbList.remove(info)
                }
                LogUtil.d(DutyApplication.instance.usbList.toString())
            }
        }
    }

    private fun getStorageVolume(context: Context, intent: Intent) = intent.data?.path?.run {
        val storageVolume = intent.extras?.getParcelable<StorageVolume>("storage_volume")
        LogUtil.i(storageVolume.toString())
        storageVolume ?: getStorageVolumeCompat(context, this)
    }

    private fun getStorageVolumeCompat(context: Context, path: String): StorageVolume? {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        storageManager.storageVolumes.forEach {
            val privatePathMethod = it::class.java.getDeclaredMethod("getPath")
            val internalPath = privatePathMethod.invoke(it) as String
            if (internalPath == path) {
                return it
            }
        }
        return null
    }
}