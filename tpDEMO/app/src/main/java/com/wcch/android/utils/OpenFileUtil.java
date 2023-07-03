package com.wcch.android.utils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;

import androidx.core.content.FileProvider;

import com.wcch.android.R;
import com.wcch.android.soft.util.RSAUtils;
import com.wcch.android.view.ToastUtils;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class OpenFileUtil {

    public static Intent openFile(Context context,String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            return getAudioFileIntent(context,filePath);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getVideoFileIntent(context,filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(context,filePath);
        } else if (end.equals("apk")) {
            return getApkFileIntent(context,filePath);
        } else if (end.equals("ppt") || end.equals("pptx")) {
            return getPptFileIntent(context,filePath);
        } else if (end.equals("xls") || end.equals("xlsx")) {
            return getExcelFileIntent(context,filePath);
        } else if (end.equals("doc") || end.equals("doc")) {
            return getWordFileIntent(context,filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(context,filePath);
        } else if (end.equals("chm")) {
            return getChmFileIntent(context,filePath);
        } else if (end.equals("txt")) {
            return getTextFileIntent(context,filePath, false);
        } else {
            return getAllIntent(context,filePath);
        }
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(Context context,String param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
        }
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(Context context,String param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
//        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;

    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(Context context,String param) {


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(param)), "video/*");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        return intent;
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.putExtra("oneshot", 0);
//        intent.putExtra("configchange", 0);
//        Uri uri = Uri.fromFile(new File(param));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
//        }
//        intent.setDataAndType(uri, "video/*");
//        return intent;
    }
    //android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent2(Context context,String Path)
    {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
            intent.setDataAndType(fileUri, "video/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "video/*");
        }
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(Context context,String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
//        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }


    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(Context context,String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(Context context,String param) {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        Uri uri = Uri.fromFile(new File(param));
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
////        }
//        intent.setDataAndType(uri, "image/*");
//        return intent;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(param)), "image/*");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        return intent;
    }
    //android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent2(Context context,String Path)
    {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
            intent.setDataAndType(fileUri, "image/jpeg");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "image/jpeg");
        }
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
//    public static Intent getPptFileIntent(Context context,String param) {
//
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(new File(param));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
//        }
//        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//        return intent;
//    }

//    // Android获取一个用于打开Excel文件的intent
//    public static Intent getExcelFileIntent(Context context,String param) {
//
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(new File(param));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
//        }
//        intent.setDataAndType(uri, "application/vnd.ms-excel");
//        return intent;
//    }
//
//    // Android获取一个用于打开Word文件的intent
//    public static Intent getWordFileIntent(Context context,String param) {
//
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(new File(param));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
//        }
//        intent.setDataAndType(uri, "application/msword");
//        return intent;
//    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(Context context,String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
        }
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(Context context,String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        if (paramBoolean) {
//            Uri uri1 = Uri.parse(param);
//            intent.setDataAndType(uri1, "text/plain");
//        } else {
//            Uri uri2 = Uri.fromFile(new File(param));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                uri2 = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(param));
//            }
//            Uri uri2 = Uri.fromFile(new File(param));
//            intent.setDataAndType(uri2, "text/plain");
//        }
        Uri uri2 = Uri.fromFile(new File(param));
        intent.setDataAndType(uri2, "text/plain");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        return intent;
    }


    public static String  getAuthor() {
        String author = new SimpleDateFormat("yyyymmdd").format(new Date()) + "igrs" + "Projection";
        try {
            return RSAUtils
                    .getRSAPublicString(Base64Utils.decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDegLmF7tIxTcqlogaH4Skl3V499rSQZynN0BMlnocAb/rkSzScyHn3dcfjMjXYtxRYx1csweemi0J2hpx6yP3UHbUZyTzfS887oR9QS3s1GjFPYLEH7gThVWhNxtoo+K1l8iXltyjH0+o/TgT8xv2MVrYYO0FV5x/8vhm/ruVtjwIDAQAB"),author);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void openAssignFolder(Context context,String path) {
        int i = path.lastIndexOf("/");
        String substring = path.substring(0, i);
        File file = new File(substring);
        if (null == file || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * igrs文件管理器打开指定目录
     * @param context con
     * @param path 文件路径
     */
    public static void openIgrsFileManager(Context context,String path) {
        try {
            int i = path.lastIndexOf("/");
            String substring = path.substring(0, i);
            File file = new File(substring);
            if (null == file || !file.exists()) {
                return;
            }
            Intent intent = new Intent("com.igrs.action.OPEN_PATH");
            intent.setData(Uri.parse("igrsfile://" + file.getAbsolutePath() + File.separator));
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            openAssignFolder(context, path);
        }

    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(Context context,String Path)
    {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        if (isInstall(context, "cn.wps.moffice_eng")) {
            intent.setClassName("cn.wps.moffice_eng",
                    "cn.wps.moffice.documentmanager.PreStartActivity2");
        } else {
            intent.putExtra("hasWPF",false);
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,context.getString(R.string.no_wps));
            intent.addCategory("android.intent.category.DEFAULT");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
//            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
//            intent.setDataAndType(fileUri, "application/vnd.ms-powerpoint");
//        } else {
//            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-powerpoint");
//        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-powerpoint");
        return intent;
    }


    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(Context context,String Path)
    {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        if (isInstall(context, "cn.wps.moffice_eng")) {
            intent.setClassName("cn.wps.moffice_eng",
                    "cn.wps.moffice.documentmanager.PreStartActivity2");
        } else {
            intent.putExtra("hasWPF",false);
//            Toast.makeText(context,"未检测到WPS,请优先安装WPS软件！",Toast.LENGTH_LONG).show();
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,context.getString(R.string.no_wps));
            intent.addCategory("android.intent.category.DEFAULT");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
//            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
//            intent.setDataAndType(fileUri, "application/msword");
//        } else {
//            intent.setDataAndType(Uri.fromFile(file), "application/msword");
//        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        intent.setDataAndType(Uri.fromFile(file), "application/msword");
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(Context context,String Path)
    {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        if (isInstall(context, "cn.wps.moffice_eng")) {
            intent.setClassName("cn.wps.moffice_eng",
                    "cn.wps.moffice.documentmanager.PreStartActivity2");
        } else {
            intent.putExtra("hasWPF",false);
//            Toast.makeText(context,"未检测到WPS,请优先安装WPS软件！",Toast.LENGTH_LONG).show();
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,context.getString(R.string.no_wps));
            intent.addCategory("android.intent.category.DEFAULT");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
//            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
//            intent.setDataAndType(fileUri, "application/vnd.ms-excel");
//        } else {
//            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
//        }
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        return intent;
    }
    /**
     * 检查是否安装wps
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstall(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }



    //android获取一个用于打开HTML文件的intent  
    public static Intent getHtmlFileIntent(String Path)  
    {  
        File file = new File(Path);  
        Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();  
        Intent intent = new Intent("android.intent.action.VIEW");  
        intent.setDataAndType(uri, "text/html");  
        return intent;  
    }  


    //android获取一个用于打开PDF文件的intent
    public static Intent getChrome(Context context)
    {
        Uri uri = Uri.parse("https://www.baidu.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClassName("com.android.chrome","com.google.android.apps.chrome.Main");
        return intent;
    }


    //android获取一个用于打开PDF文件的intent  
    public static Intent getPdfFileIntent(Context context,String Path)
    {
        File file = new File(Path);  
        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (isInstall(context,"com.adobe.reader")){
            intent.setPackage("com.adobe.reader");
        }else{
//            Toast.makeText(context, context.getString(R.string.toast_adobe_unable),Toast.LENGTH_LONG).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
            intent.setDataAndType(fileUri, "application/pdf");

        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        }
        return intent;
    }  
    //android获取一个用于打开文本文件的intent  
    public static Intent getTextFileIntent(Context context,String Path)
    {  
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        if (isInstall(context, "cn.wps.moffice_eng")) {
            intent.setClassName("cn.wps.moffice_eng",
                    "cn.wps.moffice.documentmanager.PreStartActivity2");
        } else {
            intent.addCategory("android.intent.category.DEFAULT");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
            intent.setDataAndType(fileUri, "text/plain");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "text/plain");
        }
        return intent;
    }  


    //android获取一个用于打开PPT文件的intent  
    public static Intent getPPTFileIntent(Context context,String Path)
    {  
        File file = new File(Path);  
        Intent intent = new Intent("android.intent.action.VIEW");
        if (isInstall(context, "cn.wps.moffice_eng")) {
            intent.setClassName("cn.wps.moffice_eng",
                    "cn.wps.moffice.documentmanager.PreStartActivity2");
        } else {
//            Toast.makeText(context, WBApplication.getInstance().getString(R.string.toast_wps_unable),Toast.LENGTH_LONG).show();
            intent.addCategory("android.intent.category.DEFAULT");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
            Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
            intent.setDataAndType(fileUri, "application/vnd.ms-powerpoint");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-powerpoint");
        }
        return intent;
    }  
    //android获取一个用于打开apk文件的intent  
    public static Intent getApkFileIntent(String Path)  
    {  
        File file = new File(Path);  
        Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");  
        return intent;  
    }
}  