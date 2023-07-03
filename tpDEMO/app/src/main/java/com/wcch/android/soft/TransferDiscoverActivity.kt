package com.wcch.android.soft

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.Settings
import android.text.InputType
import android.text.TextUtils
import android.text.method.DigitsKeyListener
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wcch.android.soft.adapter.TransferDeviceAdapter
import com.igrs.betotablet.soft.util.*
import com.igrs.sml.RuntimeInfo
import com.igrs.sml.util.BaseUtil
import com.igrs.sml.util.L
import com.igrs.transferlib.FileTransferUtil
import com.igrs.transferlib.enums.TransferType
import com.igrs.transferlib.utils.Logger
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.InjectResourceSource
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.style.BottomNavBarStyle
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.SelectMainStyle
import com.luck.picture.lib.style.TitleBarStyle
import com.wcch.android.App
import com.wcch.android.R
import com.wcch.android.entity.Device
import com.wcch.android.soft.statusBar.StatusBarUtils
import com.wcch.android.soft.util.*
import com.wcch.android.utils.AndroidUtil
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.io.File
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors


/**
 * 文件传输设备搜索页
 */
class TransferDiscoverActivity : AppCompatActivity() {
    private lateinit var stateGroup: Group
    private lateinit var imgLoading: ImageView
    private lateinit var menuBg: View
    private lateinit var pictureBtn: Button
    private lateinit var fileBtn: Button
    private lateinit var confirmBtn: Button
    private lateinit var deviceRecycler: RecyclerView
    private lateinit var deviceAdapter: TransferDeviceAdapter

    //1.4新增
    private lateinit var txtWifi: TextView
    private lateinit var deviceNameTv: TextView
    private lateinit var txtDevice: TextView
    private lateinit var layoutConnectedDevice: ConstraintLayout
    private lateinit var selectFileLl: ConstraintLayout
    private var selectedDevice: Device? = null
    private lateinit var searchStateTv: TextView
    private var alertDialog: AlertDialog? = null
    //1.4end

    private var animation: ObjectAnimator? = null

    private var actionId = R.id.select_picture_btn

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var mHandler: Handler? = null
    private val filePaths = mutableListOf<String>()
    //已ping通的ip
    private val unblockedWlanIps = CopyOnWriteArraySet<String>()
    //正在ping的ip
    private val pingWlanIps = CopyOnWriteArraySet<String>()
    //ping线程池
    private val pingTreadPool = Executors.newCachedThreadPool()
    //sendFile线程池
    private val sendFilePool = Executors.newCachedThreadPool()

    private lateinit var selectorStyle: PictureSelectorStyle

    private val scanCallback: ScanCallback = object : ScanCallback() {
        private val handler = Handler(object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                synchronized(this) {
                    val scanData = msg.obj as? ByteArray
                    if (scanData != null && scanData.size > 31) { //==62
                        val mac = BeToUtil.setDevice(scanData)
                        if (TextUtils.isEmpty(mac)) {
                            return false
                        }
                        try {
                            deviceAdapter.clearDevice()
                            for ((_, device) in BeToUtil.map_devices) {
                                if (!TextUtils.isEmpty(device.room_name) && device.room_name_isComplete
                                    && (!TextUtils.isEmpty(device.wlan_ip) && device.wlan_ip != "0.0.0.0" || !TextUtils.isEmpty(
                                        device.ap_ip
                                    ))
                                ) {

                                    if (device.room_name.endsWith("\n")) {
                                        device.room_name = device.room_name.substring(
                                            0,
                                            device.room_name.lastIndexOf("\n")
                                        )
                                    }
                                    if (device.room_name.contains("￥")) {
                                        device.room_name = device.room_name.replace("￥", "")
                                    }

                                    Log.e("zjx", " device name is  完整名称 且 device  有  ip （已解析完主包）" + Thread.currentThread().name)
                                    if (App.getInstance()?.current_device != null && App.getInstance().current_device?.device_mac == device.device_mac) {
                                        device.connect_index =
                                            Device.STATE_CONNECT
                                    } else {
                                        //不是当前连接设备 查询历史列表
                                        val hisDev =
                                            LitePal.where("device_mac = ?", device.device_mac)
                                                .find(
                                                    Device::class.java
                                                )
                                        if (hisDev != null && hisDev.size > 0) { //之前连接过的设备
                                            if (device != null && device.id == hisDev[0].id) { //上一次连接的设备
                                                L.i("scan current_device is null=" + (App.getInstance()?.current_device != null))
                                                device.connect_index =
                                                    Device.STATE_LAST
                                            }
                                        } else { //从没有连接的设备
                                            device.connect_index =
                                                Device.STATE_NONE
                                        }
                                    }
                                    if (!device.wlan_ip.isNullOrBlank() && device.connect_index !=
                                            Device.STATE_CONNECT) {
                                        if (unblockedWlanIps.contains(device.wlan_ip)) {
                                            //ip已ping通
                                            Log.e(TAG,"${device.room_name} 已ping")
                                            deviceAdapter.addDevice(device)
                                            if (deviceRecycler.visibility == View.GONE) {
                                                deviceRecycler.visibility = View.VISIBLE
                                                stateGroup.visibility = View.GONE
                                            }
                                        } else if (!pingWlanIps.contains(device.wlan_ip)) {
                                            //ip没有正在ping
                                            pingTreadPool.execute {
                                                pingWlanIps.add(device.wlan_ip)
                                                val result = NetWorkUtil.ping2(device.wlan_ip, 1, 3)
                                                pingWlanIps.remove(device.wlan_ip)
                                                Log.e(TAG,"${device.room_name} ping result=$result ${Thread.currentThread().name}")
                                                if (result) {
                                                    unblockedWlanIps.add(device.wlan_ip)
                                                    runOnUiThread {
                                                        deviceAdapter.addDevice(device)
                                                        if (deviceRecycler.visibility == View.GONE) {
                                                            deviceRecycler.visibility = View.VISIBLE
                                                            stateGroup.visibility = View.GONE
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    L.e("scan 数据不全  room_has_type1:" + device.room_has_type1 + "  room_name_s:" + device.room_name_s + "  room_name_e:" + device.room_name_e + "  room_name:" + device.room_name)
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            L.e("scan 加入列表失败 device err->e:$e")
                        } finally {
                            checkState()
                        }
                    }
                }
                return false
            }
        })

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            handler.sendMessage(handler.obtainMessage(0, result.scanRecord!!.bytes))
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            L.e("onBatchScanResults:$results")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            L.e("onScanFailed: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusTransparent_b(this)
        setContentView(R.layout.activity_transfer_discover)

        mHandler = Handler()
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null) {
            IgrsToast.getInstance().showToast(application, getString(R.string.bt_switch))
            finish()
            return
        }
        bluetoothLeScanner = mBluetoothAdapter!!.bluetoothLeScanner
        if (bluetoothLeScanner == null) {
            IgrsToast.getInstance().showToast(application, getString(R.string.bt_switch))
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, 123)
            finish()
            return
        }
        initView()
        hiddenBottomMenu()
        initListener()
        initSelectorStyle()
        requestPermission()

        scanLeDevice(true)

        if (intent.action == Intent.ACTION_SEND_MULTIPLE) {
            findViewById<LinearLayout>(R.id.tab_bar_ll).visibility = View.GONE
            val uris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
            Logger.e("外部分享uri", "uris= $uris")

            uris?.let {
                for (fileUri in uris) {
                    val path = GetFilePathFromUri.getPath(this, fileUri) ?: continue
                    try {
                        val file = File(path)
                        if (file.exists()) {
                            Log.e("TransferDiscoverActivity", "uri=${fileUri}\npath=$path\nfile=$file")
                            filePaths.add(path)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (intent.action == Intent.ACTION_SEND) {
            findViewById<LinearLayout>(R.id.tab_bar_ll).visibility = View.GONE
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            Logger.e("外部分享uri", "uri= $uri")
            uri?.let {
                val path = GetFilePathFromUri.getPath(this, uri) ?: return
                try {
                    val file = File(path)
                    if (file.exists()) {
                        Log.e("TransferDiscoverActivity", "uri=${uri}\npath=$path\nfile=$file")
                        filePaths.add(path)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkState()
        NetWorkUtil.getHostIPAddress(this)
        val ssid = NetWorkUtil.getSSID(this)
        if (!TextUtils.isEmpty(ssid) && "<unknown ssid>" != ssid) {
            txtWifi.text = ssid + if (RuntimeInfo.is5G) " 5G" else if (RuntimeInfo.is24G) " 2.4G" else ""
        } else {
            txtWifi.text = ""
        }
    }

    override fun onPause() {
        super.onPause()
        scanLeDevice(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        pingTreadPool.shutdownNow()
        sendFilePool.shutdownNow()
        unblockedWlanIps.clear()
        pingWlanIps.clear()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onBackPressed() {
        if (intent.action == Intent.ACTION_SEND_MULTIPLE || intent.action == Intent.ACTION_SEND) {
            super.onBackPressed()
        }
    }

    private fun initView() {
        stateGroup = findViewById(R.id.state_group)
        imgLoading = findViewById(R.id.img_loading)
        deviceRecycler = findViewById(R.id.device_recycler)

        menuBg = findViewById(R.id.menu_bg)
        pictureBtn = findViewById(R.id.select_picture_btn)
        fileBtn = findViewById(R.id.select_file_btn)
        confirmBtn = findViewById(R.id.confirm_btn)

        deviceAdapter = TransferDeviceAdapter()
        deviceAdapter.setHasStableIds(true)
        deviceRecycler.layoutManager = LinearLayoutManager(this)
        val dividerSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
                .toInt()
        val left =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44f, resources.displayMetrics)
        val right =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
        deviceRecycler.addItemDecoration(
            TransferDeviceDecoration(
                dividerSize,
                left,
                right,
                Color.parseColor("#e0e0e0")
            )
        )
        deviceRecycler.adapter = deviceAdapter

        txtWifi = findViewById(R.id.txt_wifi)
        deviceNameTv = findViewById(R.id.device_name_tv)
        txtDevice = findViewById(R.id.txt_device)
        layoutConnectedDevice = findViewById(R.id.layout_connected_device)
        selectFileLl = findViewById(R.id.select_file_ll)
        searchStateTv = findViewById(R.id.search_state_tv)

        val device = App.getInstance().current_device
        if (device != null) {
            var deviceName = device.room_name
            if (TextUtils.isEmpty(deviceName)) {
                deviceName = device.castCode
            }
            txtDevice.text = deviceName
            layoutConnectedDevice.visibility = View.VISIBLE
        } else {
            layoutConnectedDevice.visibility = View.GONE
        }
        val deviceName = AndroidUtil.getDeviceName()
        if (deviceName == null) {
            deviceNameTv.text = ""
        } else {
            deviceNameTv.text = deviceName
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            if (mBluetoothAdapter == null) {
                IgrsToast.getInstance().showToast(this, getString(R.string.bt_switch))
                return
            }
            bluetoothLeScanner = mBluetoothAdapter!!.bluetoothLeScanner
            if (bluetoothLeScanner == null) {
                IgrsToast.getInstance()
                    .showToast(this, getString(R.string.bt_switch))
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, 123)
                return
            }

            val permission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            L.e("ACCESS_FINE_LOCATION->state->$permission")
            if (PackageManager.PERMISSION_GRANTED != permission) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    requestLocalPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    val alertDialog = AlertDialog(this)
                    alertDialog.setTitle(getString(R.string.gps_switch))
                    alertDialog.setConfirmButton(getString(R.string.toset)) {
                        alertDialog.dismiss()
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    alertDialog.setCancelButton(getString(R.string.cancel), View.OnClickListener {
                        alertDialog.dismiss()
                        return@OnClickListener
                    })
                }
                return
            }
            if (!BaseUtil.gpsEnabled(this)) {
                alertDialog?.dismiss()
                alertDialog = AlertDialog(this).apply {
                    setTitle(getString(R.string.gps_switch))
                    setConfirmButton(
                        getString(R.string.toset)
                    ) {
                        dismiss()
                        val intent =
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    setCancelButton(
                        getString(R.string.cancel)
                    ) { dismiss() }
                }
                return
            }

            stateUndiscovered()
            setAnimation(true)
            BeToUtil.map_devices.clear()
            deviceAdapter.clearDevice()
//            deviceAdapter.selectDevices.clear()

            mHandler!!.postDelayed({ scanLeDevice(false) }, (60 * 1000).toLong())
            bluetoothLeScanner!!.startScan(scanCallback)
        } else {
            try {
                setAnimation(false)
                checkState()
                bluetoothLeScanner!!.stopScan(scanCallback)
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun checkState() {
        if (deviceAdapter.itemCount == 0) {
            stateUndiscovered()
        } else {
            stateSearchEnd()
        }
    }

    private fun stateUndiscovered() {
        deviceRecycler.visibility = View.GONE
        hiddenBottomMenu()
        stateGroup.visibility = View.VISIBLE
    }

    private fun stateSearchEnd() {
        deviceRecycler.visibility = View.VISIBLE
        stateGroup.visibility = View.GONE
    }

    private fun hiddenBottomMenu() {
        menuBg.visibility = View.GONE
        confirmBtn.visibility = View.GONE
    }

    private fun setAnimation(start: Boolean) {
        imgLoading.isEnabled = !start
        if (start) {
            searchStateTv.text = getString(R.string.device_searching)
            if (animation == null) {
                animation = ObjectAnimator.ofFloat(imgLoading, "rotation", 0f, 360f)
                animation?.duration = 1000
                animation?.repeatCount = ValueAnimator.INFINITE
                animation?.interpolator = LinearInterpolator()
            }
            imgLoading.setImageResource(R.drawable.ic_loading)
            animation?.start()
        } else {
            searchStateTv.text = getString(R.string.file_transfer_remind)
            imgLoading.setImageResource(R.drawable.ic_refresh)
            if (animation != null) animation?.end()
        }
    }

    private fun initListener() {
        layoutConnectedDevice.setOnClickListener {
            App.getInstance()?.current_device?.let {
                selectedDevice = it
                if (intent.action == Intent.ACTION_SEND_MULTIPLE ||
                    intent.action == Intent.ACTION_SEND) {
                    if (filePaths.isEmpty()) {
                        IgrsToast.getInstance().showToast(application, "文件路径解析错误")
                    } else {
                        sendFile(filePaths)
                    }
                } else {
                    if (selectFileLl.visibility != View.VISIBLE) {
                        selectFileLl.visibility = View.VISIBLE
                    }
                }
            }
        }
        deviceAdapter.selectChange = { device, isSelect ->
//            if (isSelect && deviceAdapter.selectDevices.size == 1) {
//                if (intent.action == Intent.ACTION_SEND_MULTIPLE ||
//                        intent.action == Intent.ACTION_SEND) {
//                    menuBg.visibility = View.VISIBLE
//                    pictureBtn.visibility = View.GONE
//                    fileBtn.visibility = View.GONE
//                    confirmBtn.visibility = View.VISIBLE
//                } else {
//                    menuBg.visibility = View.VISIBLE
//                    pictureBtn.visibility = View.VISIBLE
//                    fileBtn.visibility = View.VISIBLE
//                    confirmBtn.visibility = View.GONE
//                }
//            } else if (!isSelect && deviceAdapter.selectDevices.size == 0) {
//                hiddenBottomMenu()
//            }
            selectedDevice = device
            if (intent.action == Intent.ACTION_SEND_MULTIPLE ||
                intent.action == Intent.ACTION_SEND) {
                if (filePaths.isEmpty()) {
                    IgrsToast.getInstance().showToast(application, "文件路径解析错误")
                } else {
                    sendFile(filePaths)
                }
            } else {
                if (selectFileLl.visibility != View.VISIBLE) {
                    selectFileLl.visibility = View.VISIBLE
                }
            }
        }

        findViewById<ImageView>(R.id.back_iv).setOnClickListener {
            finish()
        }
        findViewById<View>(R.id.receive_file_view).setOnClickListener {
            toSavePath()
        }
        pictureBtn.setOnClickListener {
            actionId = it.id
            toFilePick()
        }
        fileBtn.setOnClickListener {
            actionId = it.id
            toFilePick()
        }
        confirmBtn.setOnClickListener {
            if (filePaths.isEmpty()) {
                IgrsToast.getInstance().showToast(application, "文件路径解析错误")
            } else {
                sendFile(filePaths)
            }
        }
        imgLoading.setOnClickListener {
            scanLeDevice(true)
        }
        findViewById<TextView>(R.id.title_tv).setOnClickListener {
            exitAfterMany()
        }
        findViewById<Button>(R.id.projection_btn).setOnClickListener {
            finish()
        }
        findViewById<ImageView>(R.id.close_img).setOnClickListener {
            selectFileLl.visibility = View.GONE
        }
    }

    private fun initSelectorStyle() {
        selectorStyle = PictureSelectorStyle()
        val whiteTitleBarStyle = TitleBarStyle().apply {
            titleBackgroundColor =
                ContextCompat.getColor(this@TransferDiscoverActivity, R.color.ps_color_white)
            isDisplayTitleBarLine = true
            titleCancelTextSize = 15
            titleBarHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44f,
                this@TransferDiscoverActivity.resources.displayMetrics)
                .toInt()
        }

        val whiteBottomNavBarStyle = BottomNavBarStyle().apply {
            bottomNarBarBackgroundColor =
                ContextCompat.getColor(this@TransferDiscoverActivity, R.color.ps_color_white)
            bottomSelectNumTextSize = 15
            isCompleteCountTips = true
            bottomNarBarHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47f,
                this@TransferDiscoverActivity.resources.displayMetrics)
                .toInt()
        }

        val selectMainStyle = SelectMainStyle().apply {
            statusBarColor =
                ContextCompat.getColor(this@TransferDiscoverActivity, R.color.ps_color_white)
            isDarkStatusBarBlack = true
            selectNormalTextColor = Color.parseColor("#ffa2a2a2")
            selectTextColor = Color.parseColor("#ff158cff")

            selectBackground = R.drawable.ps_checkbox_selector
            selectText = getString(R.string.done_front_num)
            selectNormalTextSize = 15
            selectTextSize = 15
            mainListBackgroundColor =
                ContextCompat.getColor(this@TransferDiscoverActivity, R.color.ps_color_white)
        }

        selectorStyle.titleBarStyle = whiteTitleBarStyle
        selectorStyle.bottomBarStyle = whiteBottomNavBarStyle
        selectorStyle.selectMainStyle = selectMainStyle
    }

    private fun requestPermission() {
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        L.e("ACCESS_FINE_LOCATION->state->$permission")
        if (PackageManager.PERMISSION_GRANTED != permission) {
            requestLocalPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun sendFile(filePaths: List<String>) {
        selectedDevice?.wlan_ip?.let { device ->

//        for (device in deviceAdapter.selectDevices) {
            sendFilePool.execute {
                val taskId = FileTransferUtil.getInstance().RequestFileTransfer(device, TransferType.File,
                    filePaths.toTypedArray())
                Logger.e(TAG, "taskId==>$taskId")
                if (taskId.isNotEmpty()) {
                    FileTransferUtil.getInstance().getTask(taskId)?.let {
                        if (it.requestInfo.receiveDeviceName.isEmpty()) {
                            it.requestInfo.receiveDeviceName = deviceAdapter.getDeviceNameByIp(device)
                        }
                        if (App.getInstance()?.currentActivity()?.javaClass?.simpleName
                            != TransferActivity::class.java.simpleName) {
                            startActivity(Intent(this, TransferActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                putExtra(TransferActivity.TASKS, arrayOf(it))
                            })
                        } else {
                            EventBus.getDefault().post(it)
                        }
                    }
                } else {
                    runOnUiThread {
                        IgrsToast.getInstance().showToast(this, getString(R.string.transfer_fail_version_tips))
                    }
                }
            }
//        }
        }
    }

    private fun toSavePath() {
        val uri =
            Uri.parse("content://com.android.externalstorage.documents/document/primary:Download")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        }
        startActivity(intent)
    }

    private fun toFilePick() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // 安卓11，判断有没有“所有文件访问权限”权限
//            if (Environment.isExternalStorageManager()) {
//                requestStorePermission()
//            } else {
//                try {
//                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
//                    permission.launch(intent)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                    intent.data = Uri.fromParts("package",packageName, null)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    permission.launch(intent)
//                }
//            }
//        } else {
            requestStorePermission()
//        }
    }

    /**
     * 存储权限请求
     */
    private fun requestStorePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )== PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            when(actionId) {
                R.id.select_picture_btn -> {
                    selectPicture()
                }
                R.id.select_file_btn -> {
                    selectFile()
                }
            }
        } else {
            storePermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    private val pictureSelector = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            try {
                val selectList = PictureSelector.obtainSelectorList(it.data)
                val pathList = mutableListOf<String>()
                for (media in selectList) {
                    val realPath = media.realPath
                    Log.d(TAG, "realPath= $realPath")
                    pathList.add(realPath)
                }
                if (pathList.isNotEmpty()) {
                    sendFile(pathList)
                } else {
                    IgrsToast.getInstance().showToast(this, "文件路径解析错误")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val selector =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                try {
                    val pathList = mutableListOf<String>()
                    val clipData = it.data?.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri ?: continue
                            val path = GetFilePathFromUri.getPath(this, uri) ?: continue
                            try {
                                val file = File(path)
                                if (file.exists()) {
                                    Log.e("TransferDiscoverActivity", "uri=${uri}\npath=$path\nfile=$file")
                                    pathList.add(path)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        val path = GetFilePathFromUri.getPath(this, uri)
                        if (path != null) {
                            try {
                                val file = File(path)
                                if (file.exists()) {
                                    Log.e("TransferDiscoverActivity 单个", "uri=${uri}\npath=$path\nfile=$file")
                                    pathList.add(path)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    if (pathList.isNotEmpty()) {
                        sendFile(pathList)
                    } else {
                        IgrsToast.getInstance().showToast(this, "文件路径解析错误")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private val requestLocalPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result: Boolean ->
        if (result && !BaseUtil.gpsEnabled(this)) {
            alertDialog?.dismiss()
            alertDialog = AlertDialog(this).apply {
                setTitle(getString(R.string.gps_switch))
                setConfirmButton(
                    getString(R.string.toset)
                ) {
                    dismiss()
                    val intent =
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                setCancelButton(
                    getString(R.string.cancel)
                ) { dismiss() }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private val permission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Environment.isExternalStorageManager()) {
                requestStorePermission()
            } else {
                IgrsToast.getInstance().showToast(this, "存储权限获取失败")
            }
        }

    private val storePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it?.let {
                if (it[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                    && it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                    when(actionId) {
                        R.id.select_picture_btn -> {
                            selectPicture()
                        }
                        R.id.select_file_btn -> {
                            selectFile()
                        }
                    }
                } else {
                    IgrsToast.getInstance().showToast(this, "存储权限获取失败")
                }
            }
        }

    private fun selectPicture() {

        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofAll())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .setSelectorUIStyle(selectorStyle)
            .isWithSelectVideoImage(true)
            .isPreviewAudio(false)
            .isPreviewImage(false)
            .isPreviewVideo(false)
            .isPreviewFullScreenMode(false)
            .isPreviewZoomEffect(false)
            .isDisplayCamera(false)
            .isGif(true)
            .setMaxSelectNum(Int.MAX_VALUE)
            .setMaxVideoSelectNum(Int.MAX_VALUE)
            .setInjectLayoutResourceListener { context, resourceSource ->
                when(resourceSource) {
                    InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE -> {
                        R.layout.ps_custom_fragment_selector
                    }
                    else -> 0
                }
            }
            .forResult(pictureSelector)
        if (selectFileLl.visibility == View.VISIBLE) {
            selectFileLl.visibility = View.GONE
        }
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("content://com.android.externalstorage.documents/document/primary:"))
        }
        selector.launch(intent)
        if (selectFileLl.visibility == View.VISIBLE) {
            selectFileLl.visibility = View.GONE
        }
    }


    private var time: Long = 0
    private var count = 1

    private fun exitAfterMany(): Boolean {
        val timeNew = Date().time
        if (timeNew - time < 1000) { //连续点击间隔
            count += 1
        } else {
            count = 1
        }
        time = timeNew
        if (count >= 10) {  //点击次数
            count = 1
            val et = EditText(this)
            et.isFocusable = true
            et.isFocusableInTouchMode = true
            et.requestFocus()
            window.setFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            )
            et.inputType = InputType.TYPE_CLASS_NUMBER
            val digits = "0123456789."
            et.keyListener = DigitsKeyListener.getInstance(digits)

            // et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            et.setTextColor(Color.BLACK)
            //  et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
            val builder = android.app.AlertDialog.Builder(
                this,
                android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
            )
                .setTitle("Input IP")
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.confirm)
                ) { dialog, which ->
                    val input = et.text.toString()
                    if (input == "" || !BaseUtil.isCorrectIp(input)) {
                        IgrsToast.getInstance()
                            .showToast_error(this, "IP not correct!", 3000)
                    } else {
                        val ip = et.text.toString()
                        val d1 = Device()
                        d1.room_name = ip
                        d1.wlan_ip = ip
                        d1.device_type = 1
                        d1.device_mac = "ignore"
                        val imm =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
                        deviceAdapter.addDevice(d1)
                        setAnimation(false)
                        checkState()
                    }
                }.setNegativeButton(
                    getString(R.string.cancel)
                ) { dialog, which ->
                    val imm =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(
                        0,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            val alertDialog = builder.create()
            //alertDialog.setTitle("请输入检查数量（单位:" + node.baseunit + "）");
            // 解决无法弹出软键盘的bug
            alertDialog.setView(et)
            alertDialog.show()
        }
        return true
    }

    companion object {
        private const val TAG = "TransferDiscoverAct"
    }
}