package com.android.ondutytest.presenter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.Display
import android.view.Surface
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.android.ondutytest.DutyApplication
import com.android.ondutytest.util.LogUtil
import com.android.ondutytest.util.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.lang.IllegalStateException
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @Description 相机录像处理类
 *
 * @Author GXD
 * @Date 2022.11.24
 */
@SuppressLint("RestrictedApi")
class RecorderManager(private val context: Context) {
    private var videoCapture: VideoCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var camera: Camera? = null
    private lateinit var customLifecycle: CustomLifeCycle
    private lateinit var cameraExecutor: ExecutorService

    private var mDisposable: Disposable? = null
    private var mRecordSeconds = 0

    private val outputDirectory: String by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) "${Environment.DIRECTORY_DCIM}/CameraX/"
        else "${DutyApplication.instance.getExternalFilesDir(Environment.DIRECTORY_DCIM)}/CameraX"
    }

    fun startCamera(previewView: PreviewView, cameraRecordTime: TextView) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        customLifecycle = CustomLifeCycle()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context.applicationContext)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            cameraProvider?.let { customLifecycle.onResume() }
            //显示信息
            val metrics = DisplayMetrics().also {
                previewView.display.getRealMetrics(it)
            }
            //输出图片和预览的比例
            val aspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
            //显示角度
            val rotation = previewView.display.rotation
            val localCameraProvider = cameraProvider ?: throw IllegalStateException(
                "Camera initialization failed."
            )

            //相机预览配置
            preview = Preview.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .build()

            val videoCaptureConfig = VideoCapture.DEFAULT_CONFIG.config
            videoCapture = VideoCapture.Builder
                .fromConfig(videoCaptureConfig)
                .build()
            localCameraProvider.unbindAll()

            try {
                //用lifecycle绑定用例和相机
                camera = localCameraProvider.bindToLifecycle(
                    customLifecycle, CameraSelector.DEFAULT_BACK_CAMERA, preview, videoCapture
                )
                preview?.setSurfaceProvider(previewView.surfaceProvider)
                //调整到这里使预览打开的时候开始录像
                startRecording(cameraRecordTime)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context.applicationContext))

    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - 4 / 3) <= abs(previewRatio - 16 / 9)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun updateFloatWindowTime(view: TextView) {
        var updateTime = ""
        // 1s刷新一次
        mDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
            .map {
                mRecordSeconds++
                updateTime = TimeUtil.getFormatTime(mRecordSeconds)
                updateTime
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.text = updateTime
            }
    }

    //开始录像
    @SuppressLint("MissingPermission", "RestrictedApi")
    private fun startRecording(cameraRecordTime: TextView) {
        val localVideoCapture =
            videoCapture ?: throw IllegalStateException("Camera initialization failed.")

        //输出文件选项
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, TimeUtil.getNowDate())
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                put(MediaStore.MediaColumns.RELATIVE_PATH, outputDirectory)
            }

            DutyApplication.instance.contentResolver.run {
                val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

                VideoCapture.OutputFileOptions.Builder(this, contentUri, contentValues)
            }
        } else {
            File(outputDirectory).mkdir()
            val file = File("$outputDirectory/${TimeUtil.getNowDate()}.mp4")
            VideoCapture.OutputFileOptions.Builder(file)
        }.build()

        LogUtil.d("开始录像")
        updateFloatWindowTime(cameraRecordTime)
        localVideoCapture.startRecording(
            outputOptions,
            cameraExecutor,
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    LogUtil.d("文件已保存")
                }

                override fun onError(
                    videoCaptureError: Int,
                    message: String,
                    cause: Throwable?
                ) {
                    cause?.printStackTrace()
                }
            }
        )
    }

    //结束录像
    fun stopRecording() {
        LogUtil.d("停止录像")
        mRecordSeconds = 0
        mDisposable?.dispose()
        videoCapture?.stopRecording()
        customLifecycle.onStop()
    }

    inner class CustomLifeCycle : LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        fun onResume() {
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        }

        fun onStop() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }
}