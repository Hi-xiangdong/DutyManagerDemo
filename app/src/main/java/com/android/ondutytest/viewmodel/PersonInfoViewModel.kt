package com.android.ondutytest.viewmodel

import android.bluetooth.BluetoothClass.Device
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.ondutytest.model.database.PersonInfo
import com.android.ondutytest.util.DeviceUtil

/**
 * @Description 人员信息viewModel
 *
 * @Author GXD
 * @Date 2022.11.29
 */
class PersonInfoViewModel : ViewModel() {
    var admin:PersonInfo? = null
    var personOnDuty: List<PersonInfo>? = null
    var personList = ArrayList<PersonInfo>()
    val temLiveData = DeviceUtil.getTemperature()

    //viewModel中专用的数据列表
    val personInfoLiveData = MutableLiveData<List<PersonInfo>>()

    init {
        personInfoLiveData.postValue(personList)
    }

    fun updatePersonList(list: List<PersonInfo>) {
        personInfoLiveData.postValue(list)
    }

}