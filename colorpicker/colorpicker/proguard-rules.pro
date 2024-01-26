#================ Proguard关键字 ================#
# 关键字                      描述
# keep                        保留类和类中的成员，防止被混淆或移除
# keepclassmembers            只保留类中的成员，防止被混淆或移除
# keepclasseswithmembers      保留类和类中的成员，防止被混淆或移除，保留指明的成员
# keepnames                   保留类和类中的成员，防止被混淆，成员没有被引用会被移除
# keepclassmembernames        只保留类中的成员，防止被混淆，成员没有引用会被移除
# keepclasseswithmembernames  保留类和类中的成员，防止被混淆，保留指明的成员，成员没有引用会被移除
#-assumenosideeffects         假设调用不产生任何影响，在proguard代码优化时会将该调用remove掉。如system.out.println和Log.v等等
#-dontwarn [class_filter]     不提示warnning
#================ Proguard通配符 ================#
# 通配符      描述
# <field>     匹配类中的所有字段
# <method>    匹配类中所有的方法
# <init>      匹配类中所有的构造函数
# *           匹配任意长度字符，不包含包名分隔符(.)
# **          匹配任意长度字符，包含包名分隔符(.)
# ***         匹配任意参数类型
# !           放在文件名前面表示将某文件排除在外

#================ ANDROID默认的混淆配置(proguard-android.txt) ================#
# http://proguard.sourceforge.net/index.html#manual/usage.html
#包名不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
#混淆时是否记录日志(有了verbose这句话，混淆后就会生成映射文件)
#我注释了也会输出mapping.txt等文件,默认输出在../outputs/mapping/xxx/release/下
-verbose
#不优化输入的类文件(建议注释掉,后面的-optimizations优化算法才有效,以及删除日志)
#-dontoptimize
#不做预校验，preverify是proguard的4个步骤之一，android不需要做预校验，去除这一步可以加快混淆速度
-dontpreverify
#保护注解,泛型,内部类,反射
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
#
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.google.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick.
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Preserve annotated Javascript interface methods.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

#================ ANDROID常用的混淆配置 ================#

# 代码混淆压缩比，在0~7之间，默认为5,一般不下需要修改
-optimizationpasses 7
# 谷歌推荐的混淆时所采用的算法(-dontoptimize需要注释掉)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 出现 dex 优化错误,可以尝试下面的优化算法
#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
#如果引用了v4或者v7包
-dontwarn android.support.**
#忽略警告
-ignorewarning

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留自定义控件(继承自View)不能被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
    *** get* ();
}

#================ 记录生成的日志数据 ================#
#默认输出在../outputs/mapping/xxx/release/下

#移除log,必须注释掉-dontoptimize,否则不生效
#-assumenosideeffects class android.util.Log {
#    public static boolean isLoggable(java.lang.String, int);
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#}

#================ 混淆拓展,增加反编译阅读难度 ================#
#其中所有有效字词都用作混淆字段和方法名称
-obfuscationdictionary proguard_xilayu.txt
#其中所有有效词都用作混淆类名
-classobfuscationdictionary proguard_keywords.txt
#其中所有有效词都用作混淆包名称
#-packageobfuscationdictionary proguard_keywords.txt
#深度重载混淆。这个选项会使代码更小（且不易理解）
#这个选项可能会使参数和返回类型不同的方法和属性混淆后获得同样的名字
-overloadaggressively
#指定接口可以合并，即使实现类没实现所有的方法
-mergeinterfacesaggressively

#================ 添加自己的混淆规则 ================#
#解决The same input jar is specified twice问题:注释libraryjars
#-libraryjars libs/tff_sdk_api.jar

#忽略警告
-dontwarn com.android.toofifi.**

#保持以下类不被混淆
#keep tff jar begin

#okhttp-3.2.0.jar
-keep class okhttp3.**{*;}
#okio-1.8.0.jar
-keep class okio.**{*;}
#ThinDownloadManager-1.2.5.jar!
-keep class com.thin.downloadmanager.**{*;}

-keep class org.apache.commons.fileupload.**{*;}
#commons-io-2.5.jar
-keep class org.apache.commons.io.**{*;}
#org.apache.http.legacy.jar
-keep class android.net.compatibility.**{*;}
-keep class android.net.http.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.commons.**{*;}
-keep class org.apache.http.**{*;}









