package com.wcch.android.soft.adapter

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.wcch.android.soft.adapter.TransferDeviceAdapter.DeviceViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import com.igrs.sml.util.L
import com.wcch.android.App
import com.wcch.android.R
import com.wcch.android.entity.Device

class TransferDeviceAdapter : RecyclerView.Adapter<DeviceViewHolder>() {
    private val devicesList = mutableListOf<Device>()
//    val selectDevices = mutableSetOf<String>()
    private val normalSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        60f,
       App.getInstance().applicationContext?.resources?.displayMetrics
    ).toInt()
    private val maxSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        64f,
        App.getInstance().applicationContext?.resources?.displayMetrics
    ).toInt()

    fun addDevice(o: Device) {
        val findIndex = devicesList.indexOfFirst { it.wlan_ip == o.wlan_ip }
        L.e("TransferDeviceAdapter", "findIndex== $findIndex,,o=$o")
        if (findIndex == -1) {
            devicesList.add(o)
            notifyItemInserted(devicesList.size - 1)
        }
    }

    fun clearDevice() {
        val size = devicesList.size
        devicesList.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun getDeviceNameByIp(ip: String): String {
        val findIndex = devicesList.indexOfFirst { it.wlan_ip == ip }
        if (findIndex != -1) {
            return devicesList[findIndex].room_name
        }
        return ""
    }

    var selectChange: ((device: Device, isSelect: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_recycler, parent, false)
        val holder = DeviceViewHolder(rootView)
        holder.castCodeGroup.visibility = View.GONE
        rootView.setOnClickListener {
            val device = devicesList[holder.adapterPosition]
//            val select = if (selectDevices.contains(device.wlan_ip)) {
//                selectDevices.remove(device.wlan_ip)
//                false
//            } else {
//                selectDevices.add(device.wlan_ip)
//                true
//            }
            notifyItemChanged(holder.adapterPosition, -1)
            selectChange?.invoke(device, true)
        }

        return holder
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val transferDevice = devicesList[position]
        holder.deviceNameTv.text = transferDevice.room_name
        //根据是否连接改变高度 显示
        if (transferDevice.connect_index == Device.STATE_CONNECT) {
            holder.deviceStateGroup.visibility = View.VISIBLE
            holder.deviceStateTv.text = "已连接"
            holder.rootView.layoutParams.height = maxSize
            holder.rootView.requestLayout()
        } else {
            holder.deviceStateGroup.visibility = View.GONE
            holder.rootView.layoutParams.height = normalSize
            holder.rootView.requestLayout()
        }

        when(transferDevice.device_type) {
            1 -> holder.deviceImg.setImageResource(R.drawable.ic_device_tv)
            2 -> holder.deviceImg.setImageResource(R.drawable.ic_device_pad)
            3 -> holder.deviceImg.setImageResource(R.drawable.ic_device_pc)
            4 -> holder.deviceImg.setImageResource(R.drawable.ic_device_pad)
        }

//        if (transferDevice.castCode.isNotEmpty()) {
//            holder.castCodeGroup.visibility = View.VISIBLE
//            holder.castCodeTv.text = transferDevice.castCode
//        } else {
//            holder.castCodeGroup.visibility = View.GONE
//        }

//        if (selectDevices.contains(transferDevice.wlan_ip)) {
//            holder.checkDevice.setImageResource(R.drawable.ic_select_y)
//        } else {
//            holder.checkDevice.setImageResource(R.drawable.ic_select_n)
//        }
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rootView: ConstraintLayout = itemView.findViewById(R.id.item_root)
        val checkDevice: ImageView = itemView.findViewById(R.id.check_device)
        val deviceImg: ImageView = itemView.findViewById(R.id.device_img)
        val deviceNameTv: TextView = itemView.findViewById(R.id.device_name_tv)
        val deviceStateTv: TextView = itemView.findViewById(R.id.device_state_tv)
        val castCodeTv: TextView = itemView.findViewById(R.id.cast_code_tv)
        val castCodeGroup: Group = itemView.findViewById(R.id.cast_code_group)
        val deviceStateGroup: Group = itemView.findViewById(R.id.device_state_group)
    }
}