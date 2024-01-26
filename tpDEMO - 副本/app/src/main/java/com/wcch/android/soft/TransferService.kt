package com.igrs.betotablet.soft

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.view.WindowManager
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.TypedValue
import android.graphics.PixelFormat
import android.os.*
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.endo.common.utilcode.util.AppUtils.isAppForeground
import com.igrs.betotablet.soft.adapter.TransferTaskAdapter
import com.igrs.betotablet.soft.entity.SwitchApp
import com.igrs.betotablet.soft.util.SidesTextItemDecoration
import com.igrs.sml.util.L
import com.igrs.transferlib.FileTransferEventListener
import com.igrs.transferlib.FileTransferUtil
import com.igrs.transferlib.entity.*
import com.igrs.transferlib.enums.CoverStrategy
import com.igrs.transferlib.enums.ResponseType
import com.igrs.transferlib.enums.TaskType
import com.igrs.transferlib.utils.Logger
import com.wcch.android.App
import com.wcch.android.R
import com.wcch.android.soft.TransferActivity
import com.wcch.android.utils.AndroidUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference

class TransferService : Service(), FileTransferEventListener {
    private var wm: WindowManager? = null
    private var lp: WindowManager.LayoutParams? = null
    private var notifyWindowHeight = 0
    private var notifyWindowY = 0
    private var notifyWindowBottomGap = 0
    private val notifyWindowList = mutableListOf<View>()
    private var transferWindowView: View? = null
    private var transferTaskAdapter: TransferTaskAdapter? = null

    private var fileTransferUtil: FileTransferUtil? = null

    private val handler = ServerHandler(this)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground()
        }
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        lp = WindowManager.LayoutParams()

        notifyWindowHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            notifyWindowHeightDp,
            resources.displayMetrics
        ).toInt()
        notifyWindowY = (resources.displayMetrics.heightPixels -
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    notifyWindowYDp,
                    resources.displayMetrics
                )).toInt()
        notifyWindowBottomGap = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            notifyWindowBottomGapDp,
            resources.displayMetrics
        ).toInt()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        savePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        fileTransferUtil = FileTransferUtil.getInstance()
        fileTransferUtil?.init(
            controlPort, dataPort, bufferSize, AndroidUtil.getAndroidID(this),
            AndroidUtil.getDeviceName(), this
        )

        L.e(
            TAG,
            "android ID ${AndroidUtil.getAndroidID(this)}\n deviceName ${AndroidUtil.getDeviceName()}"
        )
        showTransferWindow()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        fileTransferUtil?.unInit()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        handler.removeCallbacksAndMessages(null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForeground() {
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            "transferService",
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
        val notification = Notification.Builder(
            applicationContext, "transferService"
        ).build()
        startForeground(2, notification)
    }

    @Subscribe
    fun switchApp(data: SwitchApp) {
        //进入前台
        if (data.isForeground && hasTransferTask) {
            val tasks = transferTaskAdapter?.getTransferTasks()?.toList()
            setTransferWindowVisibility(false)
            launchActivity(tasks)
            hasTransferTask = false
        }
    }

    /**
     * 传输window
     */
    private fun showTransferWindow() {
        transferWindowView = LayoutInflater.from(this).inflate(R.layout.layout_transfer_task, null)
        transferWindowView!!.findViewById<TextView>(R.id.transferring_remind_tv).visibility = View.GONE
        transferWindowView!!.findViewById<TextView>(R.id.done_btn).setOnClickListener {
            hasTransferTask = false
            setTransferWindowVisibility(false)
        }
        transferWindowView!!.findViewById<Button>(R.id.cancel_all).setOnClickListener {
            FileTransferUtil.getInstance().stopAll()
            hasTransferTask = false
            setTransferWindowVisibility(false)
        }
        transferWindowView!!.findViewById<TextView>(R.id.background_receive_btn).setOnClickListener {
            setTransferWindowVisibility(false, clearAdapter = false)
        }
        val rv = transferWindowView!!.findViewById<RecyclerView>(R.id.file_transfer_recycler)
        rv.layoutManager = LinearLayoutManager(this)
        transferTaskAdapter = TransferTaskAdapter()
        transferTaskAdapter?.setHasStableIds(true)
        rv.adapter = transferTaskAdapter
        val itemDecoration = SidesTextItemDecoration.Builder(object : SidesTextItemDecoration.ItemDecorationListener {
            override fun getGroupName(position: Int): String {
                return if (transferTaskAdapter?.getTransferTask(position)?.taskType == TaskType.sender.ordinal) {
                    "0"
                } else {
                    "1"
                }
            }

            override fun getLeftText(position: Int): String {
                return if (transferTaskAdapter?.getTransferTask(position)?.taskType == TaskType.sender.ordinal) {
                    "文件发送中"
                } else {
                    "文件接收中"
                }
            }

            override fun getRightText(position: Int): String {
                return ""
            }
        }).apply {
            setShowType(SidesTextItemDecoration.ShowType.LEFT)
            setGroupColor(Color.WHITE)
            setGroupHeight(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    40f,
                    resources.displayMetrics
                ).toInt()
            )
            setDividerColor(Color.WHITE)
            setDividerHeight(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    6f,
                    resources.displayMetrics
                ).toInt()
            )
            setLeftTextColor(Color.parseColor("#ff141414"))
            setLeftTextSize(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    13f,
                    resources.displayMetrics
                )
            )
            setLeftTextPadding(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10f,
                    resources.displayMetrics
                )
            )
        }.itemDecoration
        rv.addItemDecoration(itemDecoration)

        lp?.apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            format = PixelFormat.TRANSPARENT
            width = 1
            height = 1
            dimAmount = 0.0f
        }
        wm?.addView(transferWindowView, lp)
    }

    private fun setTransferWindowVisibility(visible: Boolean, clearAdapter: Boolean = true) {
        if (visible && (lp?.width == 1 || lp?.height == 1)) {
            lp?.run {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
                dimAmount = 0.5f
            }
            wm?.updateViewLayout(transferWindowView, lp)
        } else if (!visible && (lp?.width == WindowManager.LayoutParams.MATCH_PARENT
                    || lp?.height == WindowManager.LayoutParams.MATCH_PARENT)) {
            lp?.run {
                width = 1
                height = 1
                dimAmount = 0.0f
            }
            wm?.updateViewLayout(transferWindowView, lp)
            if (clearAdapter) {
                transferTaskAdapter?.clearTask()
            }
        }
    }

    /**
     * 文件接收通知window
     */
    @Synchronized
    private fun showTransferNotifyWindow(fileTransferRequestInfo: FileTransferRequestInfo) {
        val notifyView =
            LayoutInflater.from(this).inflate(R.layout.layout_receive_file_notify, null)

        val thumbnailImage = notifyView.findViewById<ImageView>(R.id.thumbnail_iv)
        val title = notifyView.findViewById<TextView>(R.id.transfer_title_tv)
        val transferSize = notifyView.findViewById<TextView>(R.id.transfer_size_tv)
        val rejectBtn = notifyView.findViewById<Button>(R.id.reject_btn)
        val acceptBtn = notifyView.findViewById<Button>(R.id.accept_btn)

        rejectBtn.setOnClickListener {
            wm!!.removeView(notifyView)
            notifyWindowList.remove(notifyView)
            updateNotifyWindowMarginByRemove()
            //拒绝
            fileTransferUtil?.FileTransferResponse(
                fileTransferRequestInfo.taskId,
                ResponseType.reject, savePath, CoverStrategy.ReWrite
            )
        }
        acceptBtn.setOnClickListener {
            wm!!.removeView(notifyView)
            notifyWindowList.remove(notifyView)
            updateNotifyWindowMarginByRemove()
            //暂时ResponseType.reject是反的
            fileTransferUtil?.FileTransferResponse(
                fileTransferRequestInfo.taskId,
                ResponseType.confirming, savePath, CoverStrategy.ReWrite
            )
            val transferTask = fileTransferUtil?.getTask(fileTransferRequestInfo.taskId)
            transferTask?.let {
                addTransferTask(it)
            }
        }

        var fileSize = "0M"
        var fileName = ""
        when {
            fileTransferRequestInfo.fileList.size == 1 -> {
                val fileInfo = fileTransferRequestInfo.fileList[0];
                fileSize = "${fileInfo.size / 1024 / 1024}M"
                fileName = "${fileInfo.name}.${fileInfo.extensionName}"
            }

            fileTransferRequestInfo.fileList.size > 1 -> {

                var size = 0L
                for (fileInfo in fileTransferRequestInfo.fileList) {
                    size += fileInfo.size
                }

                fileSize = "${size / 1024 / 1024}M"
                fileName = "${fileTransferRequestInfo.fileList.size}个文件"
            }
        }
        title.text = "${fileTransferRequestInfo.senderDeviceName}发送的$fileName"
        transferSize.text = fileSize


        val lp = WindowManager.LayoutParams().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                type = WindowManager.LayoutParams.TYPE_PHONE
            }
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            format = PixelFormat.TRANSPARENT
            gravity = Gravity.TOP or Gravity.LEFT
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = notifyWindowHeight
            y = notifyWindowY - if (notifyWindowList.size < 2)
                notifyWindowList.size * notifyWindowBottomGap
            else
                2 * notifyWindowBottomGap
        }

        wm!!.addView(notifyView, lp)
        notifyWindowList.add(notifyView)

        when (notifyWindowList.size) {
            2 -> {
                val marginHorizontal = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    15f,
                    resources.displayMetrics
                )
                updateNotifyWindowMargin(notifyWindowList[0], marginHorizontal.toInt())
            }
            3 -> {
                val marginHorizontal0 = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    22f,
                    resources.displayMetrics
                )
                updateNotifyWindowMargin(notifyWindowList[0], marginHorizontal0.toInt())

                val marginHorizontal = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    15f,
                    resources.displayMetrics
                )
                updateNotifyWindowMargin(notifyWindowList[1], marginHorizontal.toInt())
            }
        }

    }

    private fun launchActivity(transferTask: List<FileTransferTask>? = null) {
        if (App.getInstance()?.currentActivity()?.javaClass?.simpleName
            != TransferActivity::class.java.simpleName
        ) {
            startActivity(Intent(this, TransferActivity::class.java).apply {
                transferTask?.let {
                    putExtra(TransferActivity.TASKS, transferTask.toTypedArray())
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
        } else {
            transferTask?.let {
                for (task in transferTask) {
                    EventBus.getDefault().post(task)
                }
            }
        }
    }

    /**
     * 移除后更新接收文件通知window
     */
    private fun updateNotifyWindowMarginByRemove() {
        when (notifyWindowList.size) {
            2 -> {
                val marginHorizontal = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    15f,
                    resources.displayMetrics
                )
                updateNotifyWindowMargin(notifyWindowList[0], marginHorizontal.toInt())
            }

            1 -> {
                val marginHorizontal = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    8f,
                    resources.displayMetrics
                )
                updateNotifyWindowMargin(notifyWindowList[0], marginHorizontal.toInt())
            }
        }
    }

    private fun updateNotifyWindowMargin(notifyView: View, margin: Int) {
        val root = notifyView.findViewById<ConstraintLayout>(R.id.border_layout)
        root.post {
            val layoutParams: FrameLayout.LayoutParams =
                root.layoutParams as FrameLayout.LayoutParams
            layoutParams.setMargins(margin, 0, margin, 0)
            root.requestLayout()
        }
    }

    /**
     * 添加传输任务
     */
    private fun addTransferTask(transferTask: FileTransferTask) {
        hasTransferTask = true
        if (App.getInstance()?.isAppForeground == true) {
            launchActivity(listOf(transferTask))
        } else {
            if (transferWindowView?.windowVisibility != View.VISIBLE) {
                showTransferWindow()
            }
            transferTaskAdapter?.addTransferTask(transferTask)
            transferTaskViewState()
            setTransferWindowVisibility(true)
            EventBus.getDefault().post(transferTask)
        }
    }

    /**
     * 传输任务状态变化
     */
    private fun transferTaskStateChange(transferState: FileTransferState) {
        if (transferWindowView?.windowVisibility == View.VISIBLE) {
            transferTaskAdapter?.updateTaskState(transferState.taskId, transferState.stat)
            transferTaskViewState()
        }
        EventBus.getDefault().post(transferState)
        Logger.d(TAG, "post transferTaskStateChange")
    }

    private fun transferProgressChange(transferProgress: FileTransferProgress) {
        if (transferWindowView?.windowVisibility == View.VISIBLE) {
            transferTaskAdapter?.updateProgress(transferProgress.taskId, transferProgress.progress,
                transferProgress.transferLength, transferProgress.transferRate)
//            transferTaskViewState()
        }
        EventBus.getDefault().post(transferProgress)
        Logger.d(TAG, "post transferProgressChange")
    }

    /**
     * 传输界面view状态变化
     */
    private fun transferTaskViewState() {
        val doneBtn = transferWindowView?.findViewById<TextView>(R.id.done_btn)
        val backgroundReceiveBtn = transferWindowView?.findViewById<TextView>(R.id.background_receive_btn)
        val cancelAll = transferWindowView?.findViewById<Button>(R.id.cancel_all)
        if (transferTaskAdapter?.hasTransferring() == true) {
            doneBtn?.visibility = View.GONE
            backgroundReceiveBtn?.visibility = View.VISIBLE
        } else {
            doneBtn?.visibility = View.VISIBLE
            backgroundReceiveBtn?.visibility = View.GONE
        }
        if ((transferTaskAdapter?.itemCount ?: 0) > 1 &&
            transferTaskAdapter?.hasTransferring() == true
        ) {
            cancelAll?.visibility = View.VISIBLE
        } else {
            cancelAll?.visibility = View.GONE
        }
    }

    companion object {
        private const val notifyWindowHeightDp = 120f
        private const val notifyWindowYDp = notifyWindowHeightDp + 24
        private const val notifyWindowBottomGapDp = 6f

        private const val controlPort = 11320
        private const val dataPort = 11321
        private const val bufferSize = 1024 * 1024

        private const val TRANSFER_REQUEST_EVENT = 1
        private const val TRANSFER_PROGRESS_CHANGE = 2
        private const val TRANSFER_STATE_CHANGE = 3
        private const val TRANSFER_ERROR = 4
        private const val TAG = "TransferService"

        var hasTransferTask = false
        var savePath = ""
    }

    override fun transferRequestEvent(transferRequestInfo: FileTransferRequestInfo?) {
        transferRequestInfo?.let {
            Logger.e(TAG, "transferRequestEvent==>$transferRequestInfo")
            handler.sendMessage(handler.obtainMessage(TRANSFER_REQUEST_EVENT, transferRequestInfo))
        }
    }

    override fun taskErrorEvent(transferError: FileTransferError?) {
        transferError?.let {
            Logger.e(TAG, "taskErrorEvent==>$transferError")
            handler.sendMessage(handler.obtainMessage(TRANSFER_ERROR, transferError))
        }
    }

    override fun progressChangeEvent(transferProgress: FileTransferProgress?) {
        transferProgress?.let {
            Logger.e(TAG, "progressChangeEvent==>$transferProgress")
            handler.sendMessage(handler.obtainMessage(TRANSFER_PROGRESS_CHANGE, transferProgress))
        }
    }

    override fun taskStateChangeEvent(transferState: FileTransferState?) {
        transferState?.let {
            Logger.e(TAG, "taskStateChangeEvent==>$transferState")
            handler.sendMessage(handler.obtainMessage(TRANSFER_STATE_CHANGE, transferState))
        }
    }

    private class ServerHandler(server: TransferService) : Handler(Looper.getMainLooper()) {
        val weakServer = WeakReference(server)
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                TRANSFER_REQUEST_EVENT -> {
                    val fileTransferRequestInfo = msg.obj as? FileTransferRequestInfo
                    fileTransferRequestInfo?.let {
                        weakServer.get()?.run {
                            showTransferNotifyWindow(it)
                        }
                    }
                }
                TRANSFER_PROGRESS_CHANGE -> {
                    val fileTransferProgress = msg.obj as? FileTransferProgress
                    fileTransferProgress?.let {
                        weakServer.get()?.run {
                            transferProgressChange(it)
                        }
                    }
                }
                TRANSFER_STATE_CHANGE -> {
                    val transferState = msg.obj as? FileTransferState
                    transferState?.let {
                        weakServer.get()?.run {
                            transferTaskStateChange(it)
                        }
                    }
                }
                TRANSFER_ERROR -> {

                }
            }
        }
    }
}