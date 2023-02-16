package com.android.ondutytest

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.android.ondutytest.constant.Constant
import com.android.ondutytest.util.LogUtil
import com.android.ondutytest.util.SmsUtil

/**
 * @Description TODO
 *
 * @Author GXD
 * @Date 2023.2.16
 */
class BluetoothBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val str = device?.name + "|" + device?.address
                //如果是对应设备则发送短信
                if (str == Constant.BLUETOOTH_DEVICE)
                    SmsUtil.sendMessage(
                        context,
                        DutyApplication.instance.admin!!.phoneNumber,
                        "请注意，设备被挪动！"
                    )
                LogUtil.i("发现新的设备")
            }
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                Toast.makeText(context, "正在扫描", Toast.LENGTH_SHORT).show()
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                Toast.makeText(context, "扫描完成", Toast.LENGTH_SHORT).show()
            }
        }
    }
}