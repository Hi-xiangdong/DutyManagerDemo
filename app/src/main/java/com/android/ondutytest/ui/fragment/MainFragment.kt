package com.android.ondutytest.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.ondutytest.DutyApplication
import com.android.ondutytest.R
import com.android.ondutytest.constant.Constant
import com.android.ondutytest.databinding.FragmentMainBinding
import com.android.ondutytest.model.database.PersonInfo
import com.android.ondutytest.presenter.MainPresenter
import com.android.ondutytest.ui.widget.CustomDialog
import com.android.ondutytest.util.*
import com.android.ondutytest.viewmodel.PersonInfoViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @Description 主Fragment
 *
 * @Author GXD
 * @Date 2023.1.11
 */
class MainFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!
    private var mDisposable: Disposable? = null
    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(PersonInfoViewModel::class.java) }
    private val presenter by lazy { MainPresenter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.Default) {
            viewModel.personList = presenter.loadPersonInfo() as ArrayList<PersonInfo>
            LogUtil.i(viewModel.personList.toString())
            viewModel.admin = presenter.getAdminFromList(viewModel.personList)
            //更新当前值日生信息
            viewModel.updatePersonList(viewModel.personList)
            withContext(Dispatchers.Main) {
                binding.tvPerson.text = presenter.getNameStringFromList(
                    presenter.judgeWhoIsOnDuty(viewModel.personList)
                )
            }
        }

        initView()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_import -> importData()
            R.id.bt_clean -> {
                lifecycleScope.launch(Dispatchers.Default) {
                    presenter.deleteAll()
                    viewModel.personList.clear()
                    viewModel.updatePersonList(viewModel.personList)
                    withContext(Dispatchers.Main) {
                        ToastUtil.showShortToast(requireContext(), "已清空")
                    }
                }
            }
        }
    }

    private fun importData() {
        if (DutyApplication.instance.usbList.isEmpty()) {
            LogUtil.i("U盘未插入")
            ToastUtil.showShortToast(requireContext(), "U盘未插入")
            return
        }
        val path = DutyApplication.instance.usbList[0].path + File.separator + Constant.PATH_EXCEL
        if (!File(path).exists()) {
            LogUtil.i("路径不存在")
            ToastUtil.showShortToast(requireContext(), "路径不存在")
            return
        }
        LogUtil.i("导入数据")
        val data = ExcelUtil.getExcelFileName(path, "xlsx")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.popupwindow_layout, null)
        //实例化自定义对话框
        val dialog = CustomDialog(requireContext(), layout = view)
        dialog.setCancelable(true)

        val listView = view.findViewById<ListView>(R.id.file_list)
        listView.adapter = ArrayAdapter(requireContext(), R.layout.listview_item, data)
        listView.setOnItemClickListener { _, _, position, _ ->
            LogUtil.i(data[position])
            lifecycleScope.launch(Dispatchers.Default) {
                val personListNew = ExcelUtil.readExcel(path + File.separator + data[position])
                LogUtil.i(personListNew.toString())
                for (person in personListNew) {
                    DutyApplication.instance.dataDao.insertPerson(person)
                    viewModel.personList.add(person)
                }
                viewModel.updatePersonList(viewModel.personList)
                withContext(Dispatchers.Main) {
                    ToastUtil.showShortToast(requireContext(), "导入完成")
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateTemAndHum() {
        binding.tvTem.text = "温度：${DeviceUtil.getTemperature()}"
        binding.tvHumidity.text = "湿度：${DeviceUtil.getHumidity()}"
    }

    private fun initView() {
        binding.btImport.setOnClickListener(this)
        binding.btClean.setOnClickListener(this)
        updateTemAndHum()
        //监听人员信息变化
        viewModel.personInfoLiveData.observe(viewLifecycleOwner) {
            viewModel.personOnDuty = presenter.judgeWhoIsOnDuty(it)
            binding.tvPerson.text = presenter.getNameStringFromList(viewModel.personOnDuty)
            DutyApplication.instance.personOnDuty = viewModel.personOnDuty
        }

        //设置监听温湿度变化的监听
        mDisposable = Observable.interval(0, 3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                updateTemAndHum()
            }
    }

    override fun onDestroyView() {
        mDisposable?.dispose()
        super.onDestroyView()
        _binding = null
    }
}