package com.wcch.android.soft

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.igrs.betotablet.soft.TransferService
import com.igrs.betotablet.soft.adapter.TransferTaskAdapter
import com.igrs.betotablet.soft.util.SidesTextItemDecoration
import com.igrs.transferlib.FileTransferUtil
import com.igrs.transferlib.entity.*
import com.igrs.transferlib.enums.TaskType
import com.wcch.android.R
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TransferActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "TransferActivity"
        const val TASKS = "transferTaskIds"
    }
    private lateinit var rv: RecyclerView
    private lateinit var taskAdapter: TransferTaskAdapter
    private lateinit var doneBtn: TextView
    private lateinit var cancelAllBtn: Button
    private lateinit var remindTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_transfer_task)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
        initData()
    }

    private fun initView() {
        rv = findViewById(R.id.file_transfer_recycler)
        rv.layoutManager = LinearLayoutManager(this)
        taskAdapter = TransferTaskAdapter()
        taskAdapter.setHasStableIds(true)
        rv.adapter = taskAdapter
        val itemDecoration = SidesTextItemDecoration.Builder(object :
            SidesTextItemDecoration.ItemDecorationListener {
            override fun getGroupName(position: Int): String {
                return if (taskAdapter.getTransferTask(position)?.taskType == TaskType.sender.ordinal) {
                    "0"
                } else {
                    "1"
                }
            }

            override fun getLeftText(position: Int): String {
                return if (taskAdapter.getTransferTask(position)?.taskType == TaskType.sender.ordinal) {
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

        doneBtn = findViewById<TextView>(R.id.done_btn).apply {
            setOnClickListener {
                TransferService.hasTransferTask = false
                finish()
            }
        }
        cancelAllBtn = findViewById<Button>(R.id.cancel_all).apply {
            setOnClickListener {
                FileTransferUtil.getInstance().stopAll()
                TransferService.hasTransferTask = false
                finish()
            }
        }
        remindTv = findViewById(R.id.transferring_remind_tv)
    }

    private fun initData() {
//        taskAdapter.addTransferTaskArray(FileTransferUtil.getInstance().GetSenderTaskList(123))
//        taskAdapter.addTransferTaskArray(FileTransferUtil.getInstance().GetReceiveTaskList(123))

        getTaskFromIntent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            setIntent(it)
            getTaskFromIntent()
        }
    }

    /**
     * 从Intent中获取传输任务
     */
    private fun getTaskFromIntent() {
        val transferTasks = intent.getParcelableArrayExtra(TASKS)
        transferTasks?.let {
            for (transferTask in transferTasks) {
                if (transferTask is FileTransferTask) {
//                    val fileTransferTask = FileTransferUtil.getInstance().GetTask(transferTask.requestInfo.taskId) ?: transferTask
                    taskAdapter.addTransferTask(transferTask)
                }
            }
        }
        transferTaskViewState()
    }

    /**
     * 传输界面view状态变化
     */
    private fun transferTaskViewState() {
        if (taskAdapter.hasTransferring()) {
            doneBtn.visibility = View.GONE
            remindTv.visibility = View.VISIBLE
        } else {
            doneBtn.visibility = View.VISIBLE
            remindTv.visibility = View.GONE
        }

        if (taskAdapter.itemCount > 1 && taskAdapter.hasTransferring()) {
            cancelAllBtn.visibility = View.VISIBLE
        } else {
            cancelAllBtn.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addTransferTask(transferTask: FileTransferTask) {
        taskAdapter.addTransferTask(transferTask)
        transferTaskViewState()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun taskErrorEvent(transferError: FileTransferError) {

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun progressChangeEvent(transferProgress: FileTransferProgress) {
        taskAdapter.updateProgress(transferProgress.taskId, transferProgress.progress,
            transferProgress.transferLength, transferProgress.transferRate)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun taskStateChangeEvent(transferState: FileTransferState) {
        taskAdapter.updateTaskState(transferState.taskId, transferState.stat)
        transferTaskViewState()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onBackPressed() {
    }
}