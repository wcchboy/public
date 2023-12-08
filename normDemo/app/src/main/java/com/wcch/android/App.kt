package com.wcch.android

import android.app.Application
import com.github.moduth.blockcanary.BlockCanary
import com.wcch.android.utils.AppBlockCanaryContext
import leakcanary.AppWatcher
import leakcanary.LeakCanary


/**
 * Created by Ryan on 2023/12/4.
 */
class App:Application() {

    override fun onCreate() {
        super.onCreate()
        //1.3
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)*/
        leakCanaryConfig()
        blockCanaryConfig()
    }

    /**
     * 配置LeakCanary 2.x
     */
    private fun leakCanaryConfig(){
        //App 处于前台时检测保留对象的阈值，默认是 5
        LeakCanary.config = LeakCanary.config.copy(retainedVisibleThreshold = 3)
        //自定义要检测的保留对象类型，默认监测 Activity，Fragment，FragmentViews 和 ViewModels
        AppWatcher.config= AppWatcher.config.copy(watchFragmentViews = false)
        //隐藏泄漏显示活动启动器图标，默认为 true
        LeakCanary.showLeakDisplayActivityLauncherIcon(false)

    }
    private fun blockCanaryConfig(){
        // 在主进程初始化调用哈
        BlockCanary.install(this, AppBlockCanaryContext()).start()
    }
    private fun test(s:String?){
        s?:return

    }
}