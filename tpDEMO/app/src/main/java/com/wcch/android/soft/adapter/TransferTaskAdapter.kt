package com.igrs.betotablet.soft.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.igrs.betotablet.TabletApp
import com.igrs.betotablet.soft.adapter.TransferTaskAdapter.TransferViewHolder
import com.igrs.betotablet.utils.FileUtil
import com.igrs.transferlib.FileTransferUtil
import com.igrs.transferlib.entity.FileTransferTask
import com.igrs.transferlib.enums.TaskState
import com.igrs.transferlib.enums.TaskType
import com.igrs.transferlib.utils.Logger

class TransferTaskAdapter : RecyclerView.Adapter<TransferViewHolder>() {
    private val taskList: MutableList<FileTransferTask> = ArrayList()

    private val normalSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        68f,
        TabletApp.application?.getApplication()?.resources?.displayMetrics
    ).toInt()
    private val maxSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        84f,
        TabletApp.application?.getApplication()?.resources?.displayMetrics
    ).toInt()

    /**
     * 添加传输任务
     */
    fun addTransferTask(task: FileTransferTask) {
        if (taskList.indexOfFirst { it.requestInfo.taskId == task.requestInfo.taskId } != -1) {
            return
        }
        //发送任务为0，接收任务为1
        if (task.taskType == TaskType.receiver.ordinal) {
            taskList.add(task)
            notifyItemInserted(taskList.size - 1)
        } else {
            val firstReceiveIndex = taskList.indexOfFirst { it.taskType == TaskType.receiver.ordinal }
            if (firstReceiveIndex == -1) {
                taskList.add(task)
                notifyItemInserted(taskList.size - 1)
            } else {
                taskList.add(firstReceiveIndex, task)
                notifyItemInserted(firstReceiveIndex)
                notifyItemRangeChanged(firstReceiveIndex + 1, taskList.size - 1)
            }
        }
    }

    fun updateTaskState(taskId: String, state: Int) {
        val findIndex = taskList.indexOfFirst { it. requestInfo.taskId == taskId}
        if (findIndex != -1) {
            if (state == TaskState.Transmitting.ordinal) {
                val f = FileTransferUtil.getInstance().getTask(taskId)
                Logger.e("updateTaskState", "f= $f")
                taskList[findIndex] = f
            } else {
                taskList[findIndex].taskState = state
            }

            notifyItemChanged(findIndex)
        }
    }

    fun updateProgress(taskId: String, progress: Int, transferLength: Long, transferRate: Long) {
        val findIndex = taskList.indexOfFirst { it. requestInfo.taskId == taskId}
        if (findIndex != -1) {
            taskList[findIndex].progress = progress
            taskList[findIndex].transferLength = transferLength
            taskList[findIndex].transferRate = transferRate
            notifyItemChanged(findIndex)
        }
    }

    fun clearTask() {
        val size = taskList.size
        taskList.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addTransferTaskArray(tasks: Array<FileTransferTask>) {
        val startIndex = taskList.size
        taskList.addAll(tasks)
        notifyItemRangeInserted(startIndex, tasks.size)
    }

    fun getTransferTasks(): List<FileTransferTask> {
        return taskList
    }

    fun hasTransferring(): Boolean {
        //todo 怎么判定为还有任务
        val findIndex = taskList.indexOfFirst {
            it.taskState == TaskState.Waiting.ordinal
            || it.taskState == TaskState.CheckLocalFileInfo.ordinal
            || it.taskState == TaskState.Confirming.ordinal
            || it.taskState == TaskState.Suspended.ordinal
            || it.taskState == TaskState.Transmitting.ordinal
        }

        return findIndex != -1
    }

    fun getTransferTask(position: Int): FileTransferTask? {
        if (position < 0 || position >= taskList.size)
            return null
        return taskList[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferViewHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_transfer_task, parent, false)
        val holder = TransferViewHolder(rootView)
        holder.cancelBtn.setOnClickListener {
            val transferTask = taskList[holder.adapterPosition]
            FileTransferUtil.getInstance().cancelFileTransfer(transferTask.requestInfo.taskId)
        }
        return holder
    }

    override fun onBindViewHolder(holder: TransferViewHolder, position: Int) {

        val fileTransferTask = taskList[position]

        //发送任务为0，接收任务为1
        if (fileTransferTask.taskType == TaskType.sender.ordinal) {
            holder.deviceNameTv.text = fileTransferTask.requestInfo.receiveDeviceName
            when(fileTransferTask.taskState) {
                TaskState.Waiting.ordinal,
                TaskState.CheckLocalFileInfo.ordinal,
                TaskState.Confirming.ordinal,
                TaskState.Suspended.ordinal,
                TaskState.Transmitting.ordinal,
                TaskState.TransferDone.ordinal -> {
                    holder.deviceImage(true)
                }
                TaskState.Reject.ordinal,
                TaskState.TransferCancel.ordinal,
                TaskState.Cancel.ordinal,
                TaskState.Oversized.ordinal -> {
                    holder.deviceImage(false)
                }
                else -> {
                    holder.deviceImage(false)
                }
            }
        } else {
            //缩略图
            if (fileTransferTask.requestInfo.thumbnail.isNotBlank()) {
                try {
                    val dataImg = Base64.decode(fileTransferTask.requestInfo.thumbnail.split(",")[1], Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(dataImg, 0, dataImg.size)
                    holder.deviceIv.setImageBitmap(bitmap)
                } catch (e: Exception) {
                }
            }

            holder.deviceNameTv.text = fileTransferTask.requestInfo.senderDeviceName
        }

        holder.rightIv.visibility = View.GONE
        holder.transferProgress.progress = fileTransferTask.progress
        holder.transferProgress.secondaryProgress = fileTransferTask.progress
        holder.transferState.text = when(fileTransferTask.taskState) {
            TaskState.Waiting.ordinal -> {
                holder.connectingWaitingState()
                if (fileTransferTask.taskType == TaskType.sender.ordinal)
                    holder.itemView.context.getString(R.string.waiting_other_receive)
                else
                    holder.itemView.context.getString(R.string.waiting_receive)
            }
            TaskState.CheckLocalFileInfo.ordinal -> {
                holder.connectingWaitingState()
                "文件校验中"
            }
            TaskState.Confirming.ordinal -> {
                holder.connectingWaitingState()
                "确认"
            }
            TaskState.Reject.ordinal -> {
                holder.cancelRejectOversizeEnd()
                holder.itemView.context.getString(R.string.reject)
            }
            TaskState.Suspended.ordinal -> {
                "暂停"
            }
            TaskState.Transmitting.ordinal -> {
                holder.transferring()
                var totalSize = 0L
                for (fileInfo in fileTransferTask.requestInfo.fileList) {
                    totalSize += fileInfo.size
                }
                holder.fileSizeTv.text = "${FileUtil.bit2M(fileTransferTask.transferLength)}M/${FileUtil.byte2M(totalSize)}M"
                holder.transferSpeed.text = FileUtil.bit2Speed(fileTransferTask.transferRate)
                "传输中"
            }
            TaskState.TransferCancel.ordinal -> {
                holder.cancelRejectOversizeEnd()
                holder.itemView.context.getString(R.string.other_cancelled)
            }
            TaskState.TransferDone.ordinal -> {
                holder.transferEnd()
                if (fileTransferTask.taskType == TaskType.sender.ordinal)
                    holder.itemView.context.getString(R.string.send)
                else
                    holder.itemView.context.getString(R.string.received)
            }
            TaskState.Cancel.ordinal -> {
                holder.cancelRejectOversizeEnd()
                holder.itemView.context.getString(R.string.cancelled)
            }
            TaskState.Oversized.ordinal -> {
                holder.cancelRejectOversizeEnd()
                "对方只接收XG以内文件"
            }
            else -> {
                holder.transferError()
                holder.transferProgress.progress = 0
                "异常"
            }
        }
        //设置高度
        when(fileTransferTask.taskState) {
            TaskState.Transmitting.ordinal,
            TaskState.InError.ordinal -> {
                holder.itemView.layoutParams.height = maxSize
                holder.itemView.requestLayout()
            }
            else -> {
                holder.itemView.layoutParams.height = normalSize
                holder.itemView.requestLayout()
            }
        }
    }
    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class TransferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceIv: ImageView = itemView.findViewById(R.id.device_iv)
        val deviceNameTv: TextView = itemView.findViewById(R.id.device_name_tv)
        val transferState: TextView = itemView.findViewById(R.id.transfer_state)
        val rightIv: ImageView = itemView.findViewById(R.id.right_iv)
        val cancelBtn: Button = itemView.findViewById(R.id.cancel_btn)
        val transferProgress: ProgressBar = itemView.findViewById(R.id.transfer_progress)
        val fileSizeTv: TextView = itemView.findViewById(R.id.file_size_tv)
        val transferSpeed: TextView = itemView.findViewById(R.id.transfer_speed)

        fun connectingWaitingState() {
            rightIv.visibility = View.GONE
            cancelBtn.visibility = View.VISIBLE
            transferProgress.visibility = View.GONE
            fileSizeTv.visibility = View.GONE
            transferSpeed.visibility = View.GONE

            deviceNameTv.setTextColor(Color.parseColor("#141414"))
            transferState.setTextColor(Color.parseColor("#A1A1A1"))
        }

        fun cancelRejectOversizeEnd() {
            transferState.visibility = View.VISIBLE
            rightIv.visibility = View.GONE
            cancelBtn.visibility = View.GONE
            transferProgress.visibility = View.GONE
            fileSizeTv.visibility = View.GONE
            transferSpeed.visibility = View.GONE
            deviceNameTv.setTextColor(Color.parseColor("#A1A1A1"))
            transferState.setTextColor(Color.parseColor("#FF2A21"))
        }

        fun transferEnd() {
            transferState.visibility = View.VISIBLE
            rightIv.visibility = View.VISIBLE
            cancelBtn.visibility = View.GONE
            transferProgress.visibility = View.GONE
            fileSizeTv.visibility = View.GONE
            transferSpeed.visibility = View.GONE
            deviceNameTv.setTextColor(Color.parseColor("#141414"))
            transferState.setTextColor(Color.parseColor("#1CBA2A"))
        }

        fun transferring() {
            transferState.visibility = View.GONE
            rightIv.visibility = View.GONE
            cancelBtn.visibility = View.VISIBLE
            transferProgress.visibility = View.VISIBLE
            fileSizeTv.visibility = View.VISIBLE
            transferSpeed.visibility = View.VISIBLE
            deviceNameTv.setTextColor(Color.parseColor("#141414"))
            cancelBtn.text = itemView.context.getString(R.string.cancel)
        }

        fun transferError() {
            transferState.visibility = View.VISIBLE
            rightIv.visibility = View.GONE
            cancelBtn.visibility = View.VISIBLE
            transferProgress.visibility = View.VISIBLE
            fileSizeTv.visibility = View.GONE
            transferSpeed.visibility = View.VISIBLE
            deviceNameTv.setTextColor(Color.parseColor("#141414"))
            transferState.setTextColor(Color.parseColor("#FF2A21"))
            cancelBtn.text = itemView.context.getString(R.string.retry)
        }

        /**
         * 是否灰色图标
         */
        fun deviceImage(isNormal: Boolean) {
            deviceIv.setImageResource(if (isNormal) R.drawable.ic_device_transfer_tv else R.drawable.ic_device_transfer_tv_disabled)
        }
    }
}