package com.android.ondutytest.util

import com.android.ondutytest.model.database.PersonInfo
import org.apache.poi.openxml4j.exceptions.InvalidFormatException
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Description excel处理工具类
 *
 * @Author GXD
 * @Date 2022.11.19
 */
object ExcelUtil {
    /**
     * @Description:读对应文件路径下的excel文件
     *
     * @param filePath:文件路径
     * @return
     */
    fun readExcel(filePath: String): List<PersonInfo> {
        val list = ArrayList<PersonInfo>()
        try {
            val wb = WorkbookFactory.create(FileInputStream(File(filePath)))
            val sheet = wb.getSheetAt(0)
            for (row in sheet) {
                val value = ArrayList<String>()
                // excel的列数
                for (j in 0 until 5) {
                    val cell = row.getCell(j)
                    if (cell == null) {
                        value[j] = "-1"
                    }
                    when (cell.cellTypeEnum) {
                        CellType.STRING ->
                            value.add(cell.richStringCellValue.toString())
                        CellType.NUMERIC -> {
                            val format = cell.cellStyle.dataFormat
                            // m月d日:58 yyyy-MM-dd:14 yyyy年m月d日:31 yyyy年m月:57
                            if (format.toInt() == 14 || format.toInt() == 31 || format.toInt() == 57
                                || format.toInt() == 58) {
                                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                                val dateValue = cell.numericCellValue
                                val date = DateUtil.getJavaDate(dateValue)
                                value.add(simpleDateFormat.format(date))
                            } else {
                                // 不加toLong()默认转为double 0会转换成字符串”0.0“
                                value.add(cell.numericCellValue.toLong().toString())
                                LogUtil.i(cell.numericCellValue.toLong().toString())
                            }
                        }
                        else -> value.add("-1")
                    }
                    LogUtil.i(value[j])
                }
                val personInfo = PersonInfo(value[0], value[1], value[2], value[3], value[4] == "0")
                list.add(personInfo)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidFormatException) {
            e.printStackTrace()
        }
        return list
    }

    /**
     * @Description:得到目录下的所有传入类型的文件
     *
     * @param dirPath:目录名称 type:文件后缀
     * @return
     */
    fun getExcelFileName(dirPath: String, type: String): ArrayList<String> {
        val result = ArrayList<String>()
        val file = File(dirPath)
        val files = file.listFiles()
        if (!files.isNullOrEmpty()) {
            for (value in files) {
                if (!value.isDirectory) {
                    val fileName = value.name
                    if (fileName.trim { it <= ' ' }.lowercase().endsWith(type)) {
                        result.add(fileName)
                    }
                }
            }
        }
        return result
    }

}