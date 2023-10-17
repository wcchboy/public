package com.igrs.betotablet.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import com.igrs.betotablet.R
import com.igrs.betotablet.TabletApp
import com.igrs.betotablet.utils.AndroidUtil
import java.io.File

class UpdateDialog(context: Context) : Dialog(context), View.OnClickListener {

    private val STATUS_CHECKING = 0
    private val STATUS_NO_VERSION = 1
    private val STATUS_NEW_VERSION = 2
    val STATUS_DOWNLOADING = 3
    val STATUS_ERROR = 4
    private var CURRENT_STATUS = STATUS_CHECKING

    private var rlClose: RelativeLayout? = null
    private var llChecking: LinearLayout? = null
    private var llNoVersion: LinearLayout? = null
    private var llNewVersion: LinearLayout? = null
    private var llError: LinearLayout? = null
    private var llDownloading: LinearLayout? = null
    private var tvCurrentVersionNoVersion: TextView? = null
    private var tvCurrentVersionNewVersion: TextView? = null
    private var tvLatestVersion: TextView? = null
    private var tvUpdateDes: TextView? = null
    private var tvCurrentVersionChecking: TextView? = null
    private var buttonUpdate: TextView? = null
    private var buttonCancelUpdate: TextView? = null
    private var tvErrorDes: TextView? = null
    private var buttonSureError: TextView? = null
    private var tvDownloadProgress: TextView? = null
    private var buttonCancelDownload: TextView? = null
    private var buttonSureNoVersion: TextView? = null
    private var pb: ProgressBar? = null
    private var url: String? = null
     var latestVersion: String? = null
    var updateInfo: String? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setContentView(R.layout.layout_update_dialog)
        rlClose = findViewById(R.id.rl_close_dialog)
        llChecking = findViewById(R.id.ll_checking)
        llNoVersion = findViewById(R.id.ll_no_new_version)
        llNewVersion = findViewById(R.id.ll_new_version)
        llError = findViewById(R.id.ll_error)
        llDownloading = findViewById(R.id.ll_downloading)
        tvCurrentVersionNoVersion = findViewById(R.id.tv_current_version_no_version)
        tvCurrentVersionNewVersion = findViewById(R.id.tv_current_version_new_version)
        tvLatestVersion = findViewById(R.id.tv_latest_version)
        tvUpdateDes = findViewById(R.id.tv_update_des)
        tvUpdateDes?.movementMethod =ScrollingMovementMethod.getInstance()
        tvErrorDes = findViewById(R.id.tv_error_des)
        tvDownloadProgress = findViewById(R.id.tv_download_progress)
        tvCurrentVersionChecking = findViewById(R.id.tv_current_version_checking)
        buttonSureNoVersion = findViewById(R.id.button_sure_no_version)
        pb = findViewById(R.id.pb)
        buttonCancelDownload = findViewById(R.id.button_cancel_download)
        buttonSureError = findViewById(R.id.button_sure_error)
        buttonUpdate = findViewById(R.id.button_update)
        buttonCancelUpdate = findViewById(R.id.button_cancel_update)
        rlClose?.setOnClickListener(this)
        buttonCancelDownload?.setOnClickListener(this)
        buttonSureError?.setOnClickListener(this)
        buttonUpdate?.setOnClickListener(this)
        buttonCancelUpdate?.setOnClickListener(this)
        buttonSureNoVersion?.setOnClickListener(this)
        val window = this.window
        window!!.setGravity(Gravity.CENTER)
        window.setBackgroundDrawableResource(R.color.transparent)
        val m = window.windowManager
        val d = m.defaultDisplay
        val p = window.attributes
        p.width = 912
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window.attributes = p
    }

    fun show(status: Int) {
        show()
        setStatus(status)
    }

    private fun setStatus(status: Int) {
        if (status < 0 || status > 4) IllegalArgumentException("error status")
        CURRENT_STATUS = status
        changeUI()
    }

    fun setNewVersionInfo(url: String?, latestVersion: String?, updateInfo: String?) {
        this.latestVersion = latestVersion
        this.updateInfo = updateInfo
        this.url = url
    }

     fun setDownloadUrl( url:String) {
            this.url = url;
        }

    private fun changeUI() {
        llChecking!!.visibility = View.GONE
        llNoVersion!!.visibility = View.GONE
        llNewVersion!!.visibility = View.GONE
        llError!!.visibility = View.GONE
        llDownloading!!.visibility = View.GONE
        if (CURRENT_STATUS == STATUS_CHECKING) {
            llChecking!!.visibility = View.VISIBLE
            setCurrentVersion(tvCurrentVersionChecking)
        }
        if (CURRENT_STATUS == STATUS_NO_VERSION) {
            llNoVersion!!.visibility = View.VISIBLE
            setCurrentVersion(tvCurrentVersionNoVersion)
        }
        if (CURRENT_STATUS == STATUS_NEW_VERSION) {
            llNewVersion!!.visibility = View.VISIBLE
            setCurrentVersion(tvCurrentVersionNewVersion)
            val format: String = java.lang.String.format(
              //  App.getApplication().getString(R.string.found_new_version),
                latestVersion
            )
            tvLatestVersion!!.text = format
            if (!TextUtils.isEmpty(updateInfo)) {
                tvUpdateDes!!.text = updateInfo
            }
        }
        if (CURRENT_STATUS == STATUS_DOWNLOADING) {
            llDownloading!!.visibility = View.VISIBLE
        }
        if (CURRENT_STATUS == STATUS_ERROR) {
            llError!!.visibility = View.VISIBLE
        }
    }

    private fun setCurrentVersion(tv: TextView?) {
        val versionName: String? = AndroidUtil.getVersionName(TabletApp.application)
        val format: String = java.lang.String.format(TabletApp.application?.getString(R.string.current_version), versionName
        )
        tv!!.text = format
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_cancel_update, R.id.button_sure_error, R.id.rl_close_dialog, R.id.button_sure_no_version -> dismiss()
            R.id.button_cancel_download -> {
                //取消下载
            //    HttpUtils.getInstance().cancelDownload()
                dismiss()
            }
            R.id.button_update ->                 //开始下载
                startDownload()
        }
    }

    var handler = Handler(Looper.getMainLooper())
    private fun startDownload() {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(TabletApp.application, context.getString(R.string.url_error), Toast.LENGTH_SHORT).show()
            CURRENT_STATUS = STATUS_ERROR
            return
        }
//        HttpUtils.getInstance().downloadApk(url, object : DownloadCallback() {
//            fun onDownload() {
//                handler.post { setStatus(STATUS_DOWNLOADING) }
//            }
//
//            fun onDownloading(percent: Int) {
//                handler.post {
//                    setStatus(STATUS_DOWNLOADING)
//                    pb!!.progress = percent
//                    tvDownloadProgress!!.text = "$percent%"
//                }
//            }
//
//            fun onDownloadFinish() {
//                //一键安装
//              //  installApk(Constants.UPDATE_FILE_PATH)
//                dismiss()
//            }
//
//            fun onDownloadFail() {
//                //下载错误
//                handler.post { setStatus(STATUS_ERROR) }
//            }
//        }
    }

    interface OnButtonClicked {
        fun onClicked(cancel: Boolean)
    }

    private var onButtonClicked: OnButtonClicked? = null
    fun setOnButtonClicked(onButtonClicked: OnButtonClicked?) {
        this.onButtonClicked = onButtonClicked
    }


    companion object {
        const val STATUS_CHECKING = 0
        const val STATUS_NO_VERSION = 1
        const val STATUS_NEW_VERSION = 2
        const val STATUS_DOWNLOADING = 3
        const val STATUS_ERROR = 4

        fun installApk(downloadApk: String?) {
            val intent = Intent(Intent.ACTION_VIEW)
            val file = File(downloadApk)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri = FileProvider.getUriForFile(
                    TabletApp.application!!,
                    "com.lenovo.beto.fileProvider", file
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            } else {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val uri = Uri.fromFile(file)
                intent.setDataAndType(uri, "application/vnd.android.package-archive")
            }
            TabletApp.application?.startActivity(intent)
        }
    }
}