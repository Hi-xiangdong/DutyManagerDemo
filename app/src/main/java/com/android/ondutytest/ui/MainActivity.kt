package com.android.ondutytest.ui

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.ondutytest.R
import com.android.ondutytest.databinding.ActivityMainBinding
import com.android.ondutytest.presenter.RecorderManager
import com.android.ondutytest.ui.widget.CustomDialog
import com.android.ondutytest.util.AlarmUtil
import com.android.ondutytest.util.DeviceUtil
import com.android.ondutytest.util.ToastUtil
import com.android.ondutytest.viewmodel.PersonInfoViewModel
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel by lazy { ViewModelProvider(this).get(PersonInfoViewModel::class.java) }
    private val recordManager by lazy { RecorderManager(this) }
    private var lightThreshold = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        //找到fragment
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        initView()
    }

    private fun initView() {
        binding.fab.setOnClickListener {
            DeviceUtil.changeAmbientLight()
            takeVideo()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings_off -> {
                val calendar = Calendar.getInstance()
                val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                TimePickerDialog(this, { _, hourThis, minuteThis ->
                    ToastUtil.showShortToast(this, "设置成功")
                    val calendarThis = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourThis)
                        set(Calendar.MINUTE, minuteThis)
                    }
                    val bundle = Bundle()
                    bundle.putBoolean("isSend", DeviceUtil.getLightSensitivity() > lightThreshold)
                    bundle.putString("number", viewModel.admin?.phoneNumber)
                    AlarmUtil.setAlarm(calendarThis, this, 1, bundle)
                }, hourOfDay, minute, true).show()
                true
            }
            R.id.action_settings_warn -> {
                val calendar = Calendar.getInstance()
                val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                TimePickerDialog(this, { _, hourThis, minuteThis ->
                    val calendarThis = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourThis)
                        set(Calendar.MINUTE, minuteThis)
                    }
                    val bundle = Bundle()
                    if (viewModel.personOnDuty.isNullOrEmpty()) {
                        return@TimePickerDialog
                    }
                    val nameList = ArrayList<String>()
                    val numberList = ArrayList<String>()
                    for (person in viewModel.personOnDuty!!) {
                        numberList.add(person.phoneNumber)
                        nameList.add(person.name)
                    }
                    bundle.putStringArrayList("name", nameList)
                    bundle.putStringArrayList("number", numberList)
                    AlarmUtil.setAlarm(calendarThis, this, 0, bundle)
                    ToastUtil.showShortToast(this, "设置成功")
                }, hourOfDay, minute, true).show()
                true
            }
            R.id.action_settings_threshold -> {
                //设置调整判断为未关灯的阈值
                val view = LayoutInflater.from(this).inflate(R.layout.dialog_adjust_threshold, null)
                val dialog = CustomDialog(this, layout = view)
                dialog.setCancelable(true)

                val seekBar = view.findViewById<SeekBar>(R.id.seekbar)
                seekBar.progress = lightThreshold
                val tvValue = view.findViewById<TextView>(R.id.tv_seekbar_value)
                tvValue.text = lightThreshold.toString()
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        tvValue.text = progress.toString()
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
                dialog.show()
                view.findViewById<Button>(R.id.bt_confirm).setOnClickListener {
                    dialog.dismiss()
                    lightThreshold = seekBar.progress
                    ToastUtil.showShortToast(this, "阈值设置成功")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun takeVideo() {
        val view = LayoutInflater.from(this).inflate(R.layout.recorder_layout, null)
        val params = ViewGroup.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.video_float_width),
            resources.getDimensionPixelSize(R.dimen.video_float_height)
        )
        view.layoutParams = params
        recordManager.startCamera(
            view.findViewById(R.id.view_finder),
            view.findViewById(R.id.camera_record_time)
        )
        EasyFloat.with(this)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RESULT_SIDE)
            .setImmersionStatusBar(true)
            .setGravity(Gravity.END)
            .setLayout(view) {
                it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
                    EasyFloat.dismiss()
                    recordManager.stopRecording()
                }
            }.show()
    }
}