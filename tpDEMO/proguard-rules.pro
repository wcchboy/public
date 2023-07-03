# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#需要序列化和反序列化的类不能被混淆（注：Java反射用到的类也不能被混淆）
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable { #保护实现接口Serializable的类中，指定规则的类成员不被混淆
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    void writeObject(java.io.ObjectOutputStream);
    void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#################################
#              泛型
#################################
-keepattributes Signature
#################################
#              webview
#################################
-keepattributes *JavascriptInterface*
-keep class **.Webview2JsInterface { *; }
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void *(android.webkit.WebView,java.lang.String);
}
#################################
#              v4
#################################
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
#################################
#              butterknife
#################################
-keep public class * implements butterknife.Unbinder {
    public <init>(...);
}
-keep class butterknife.*
-dontwarn butterknife.internal.**
-keep class **$$ViewBinding{*;}
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#################################
#              tencent
#################################
#-libraryjars libs/libammsdk.jar
-keep class com.tencent.mm.opensdk.**{*;}
-keep class com.tencent.wxop.**{*;}
-keep class com.tencent.mm.sdk.**{*;}
#################################
#              httpclient
#################################
-keep class org.apache.http.** {*;}
-keep class org.apache.james.mime4j.** {*;}
-dontwarn org.apache.http.**
-dontwarn org.apache.james.mime4j.**
#################################
#              gson
#################################
-keep class sun.misc.Unsafe{*;}
-keep class com.google.gson.**{*;}
#################################
#              imageloader
#################################
-keep class com.nostra13.universalimageloader.**{*;}
#################################
#              eventbus
#################################
-dontwarn org.greenrobot.eventbus.**
-keep  class  org.greenrobot.eventbus.** { *; }

-dontwarn org.androidannotations.**
-keep class org.androidannotations.** {*;}
-dontwarn org.apache.commons.**


######################################################
######################################################
#library
#-ignorewarnings
#-libraryjars libs/android-support-v4.jar
#-libraryjars /libs/ar.jar
#-libraryjars /libs/dd-plist.jar
#-libraryjars  libs/android-support-v4.jar
#-libraryjars libs/eventbus-3.0.0.jar





-keep  class  com.ab.** { *; }



-keep class com.mosquitto.**{*;}

-keep  class  org.apache.james.mime4j.** { *; }
-keep  class  com.dd.plist.** { *; }
-keep  class  com.samsung.android.sdk.** { *; }
-keep  class  com.samsung.android.sdk.pass.** { *; }


-keep public class * extends android.app.Activity #所有activity的子类不要去混淆
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keep class com.chad.library.adapter.base.** {*;}
-keep class com.chad.library.adapter.base.animation.** {*;}
-keep class com.chad.library.adapter.base.callback.** {*;}
-keep class com.chad.library.adapter.base.entity.** {*;}
-keep class com.chad.library.adapter.base.listener.** {*;}
-keep class com.chad.library.adapter.base.loadmore.** {*;}
-keep class com.chad.library.adapter.base.util.** {*;}
-keep class com.artifex.mupdf.** {*;}
-keep class com.artifex.mupdf.fitz.android.** {*;}
-keep class com.artifex.mupdf.fitz.** {*;}
-keep class com.artifex.mupdf.mini.** {*;}

-keep class com.wcch.android.bean.** {*;}
-keep class com.wcch.android.dialog.** {*;}#自定义类不被混淆
-keep class com.wcch.android.controls.** {*;}#自定义类不被混淆
-keep class com.google.zxing.** {*;}#qrcode
-keep class com.qrcode.zxing.** {*;}#qrcode
-dontwarn com.google.zxing.**
-keep class okhttp3.** {*;}#okhttp
-keep interface okhttp3.** {*;}
-dontwarn okhttp3.**
-keep class okio.** {*;}#okio
-dontwarn okio.**


# 打包时删除log代码 ----start----
# 需要启动代码优化才生效(gradle.build文件中 minifyEnable true)
# 使用proguard-android-optimize.txt
# proguard文件中 -dontoptimize 要注释掉
#-assumenosideeffects class android.util.Log{
#    public static boolean isLoggable(java.lang.String, int);
#    public static int d(...);
#    public static int w(...);
#    public static int v(...);
#    public static int i(...);
#    public static int e(...);
#}
## 打包时删除打印语句
#-assumenosideeffects class java.io.PrintStream{
#    public *** print(...);
#    public *** println(...);
#}
# 打包时删除log代码 ----end----
-dontwarn com.qrcode.zxing.**
#com.dd.plist
#-keep class com.dd.plist.** {*;}
-dontwarn com.dd.plist.**

-dontwarn com.samsung.android.sdk.**
-dontwarn com.samsung.android.sdk.pass.**
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}

-keepclassmembernames class com.cgv.cn.movie.common.bean.** { *; }  #转换JSON的JavaBean，类成员名称保护，使其不被混淆
#E add by ryanwang 2015-10-20