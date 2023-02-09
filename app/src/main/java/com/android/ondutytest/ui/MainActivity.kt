package com.android.ondutytest.ui

import android.Manifest
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
import com.android.ondutytest.constant.Constant
import com.android.ondutytest.databinding.ActivityMainBinding
import com.android.ondutytest.presenter.MainPresenter
import com.android.ondutytest.presenter.RecorderManager
import com.android.ondutytest.ui.fragment.MainFragment
import com.android.ondutytest.ui.widget.CustomDialog
import com.android.ondutytest.util.*
import com.android.ondutytest.viewmodel.PersonInfoViewModel
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.*
import kotlin.collections.ArrayList

@RuntimePermissions
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
//        //找到fragment
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        lightThreshold = SPUtil.get(this, Constant.SP_LIGHT_THRESHOLD, 10) as Int

        initView()
        LogUtil.i(DeviceUtil.getLightSensitivity().toString())
        supportFragmentManager.beginTransaction().let {
            it.replace(R.id.fm_fg, MainFragment())
            it.commit()
        }
    }

    private fun initView() {
        binding.fab.setOnClickListener {
            DeviceUtil.changeAmbientLight(true, Constant.BREATHE_LAMP_GREEN)
            takeVideoWithPermissionCheck()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //设置关灯提醒
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
                    bundle.putString("number", viewModel.admin?.phoneNumber)
                    AlarmUtil.setAlarm(calendarThis, this, 1, bundle)
                }, hourOfDay, minute, true).show()
                true
            }
            //设置值日提醒
            R.id.action_settings_warn -> {
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
                    if (viewModel.personOnDuty.isNullOrEmpty()) {
                        LogUtil.i("值日生为空")
                        return@TimePickerDialog
                    }
                    val nameList = ArrayList<String>()
                    val numberList = ArrayList<String>()
                    for (person in viewModel.personOnDuty!!) {
                        numberList.add(person.phoneNumber)
                        nameList.add(person.name)
                    }
                    LogUtil.i(numberList.toString())
                    LogUtil.i(nameList.toString())
                    bundle.putStringArrayList("name", nameList)
                    bundle.putStringArrayList("number", numberList)
                    AlarmUtil.setAlarm(calendarThis, this, 0, bundle)
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
                    SPUtil.put(this, Constant.SP_LIGHT_THRESHOLD, lightThreshold)
                    ToastUtil.showShortToast(this, "阈值设置成功")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun takeVideo() {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}