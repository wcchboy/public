package com.wcch.android.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.wcch.android.App;
import com.wcch.android.R;
import com.wcch.android.entity.CoordinateEntity;

import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttDisconnect;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import androidx.exifinterface.media.ExifInterface;

public class Utils {
    private static final String WIFISSID_UNKNOW = "<unknown ssid>";
    private static String hexString = "0123456789ABCDEF";
    private static Dialog sDialog;
    private static TextView sTipTextView;
    private static final int company = 4;
    public static int padType = 0;
    public static int play_mode = 0;
    public static long loginTime = 0;
    public static int loginType = 0;

    public static void output(Context context, String str) throws IOException
    {
        String format = new SimpleDateFormat(DateUtils.ymd).format(new Date());
        File file = new File("Bug" + format + ".txt");
        File file2 = new File(RouteInfo.ROOT_BUG_PATH);
        if (!file2.exists())
        {
            file2.mkdirs();
        }
        File file3 = new File(file2, file.getName());
        FileHelper.bugFileOutput(context, file3, "------@trace:" + str + "\r\n-----");
    }

    public static String getFileToStr(Context context, String str)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(str)));
            while (true)
            {
                String readLine = bufferedReader.readLine();
                if (readLine == null)
                {
                    break;
                }
                sb.append(readLine);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static boolean isOverSize(String str)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        if (((float) options.outWidth) > 2000.0f || ((float) options.outHeight) > 1125.0f)
        {
            return true;
        }
        return false;
    }

    public static String getFileMD5(File file)
    {
        if (!file.isFile())
        {
            return null;
        }
        byte[] bArr = new byte[1024];
        try
        {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            FileInputStream fileInputStream = new FileInputStream(file);
            while (true)
            {
                int read = fileInputStream.read(bArr, 0, 1024);
                if (read != -1)
                {
                    instance.update(bArr, 0, read);
                }
                else
                {
                    fileInputStream.close();
                    return bytesToHexString(instance.digest());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String bytesToHexString(byte[] bArr)
    {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0)
        {
            return null;
        }
        for (byte b : bArr)
        {
            String hexString2 = Integer.toHexString(b & 255);
            if (hexString2.length() < 2)
            {
                sb.append(0);
            }
            sb.append(hexString2);
        }
        return sb.toString();
    }

    public static int[] getPicWH(String str)
    {
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(str, options);
            if (options.outWidth <= 0 || options.outHeight <= 0)
            {
                return new int[]{0, 0};
            }
            return new int[]{options.outWidth, options.outHeight};
        }
        catch (Exception unused)
        {
            return new int[]{0, 0};
        }
    }

    public static void setEditSelection(EditText editText)
    {
        editText.setSelection(editText.getText().toString().length());
    }


    public static String getEncryPsd(String str)
    {
        try
        {
            return RSAUtils.encryptByPublicKey2(sha256(str), RSAUtils.SYNC_CW_KEY);
        }
        catch (Exception unused)
        {
            return str;
        }
    }

    public static String sha256(String str)
    {
        if (TextUtils.isEmpty(str))
        {
            return "";
        }
        try
        {
            String str2 = "";
            for (byte b : MessageDigest.getInstance("SHA-256").digest(str.getBytes()))
            {
                String hexString2 = Integer.toHexString(b & 255);
                if (hexString2.length() == 1)
                {
                    hexString2 = "0" + hexString2;
                }
                str2 = str2 + hexString2;
            }
            return str2;
        }
        catch (Exception unused)
        {
            return "";
        }
    }

    public static String md5(String str)
    {
        if (TextUtils.isEmpty(str))
        {
            return "";
        }
        try
        {
            String str2 = "";
            for (byte b : MessageDigest.getInstance("MD5").digest(str.getBytes()))
            {
                String hexString2 = Integer.toHexString(b & 255);
                if (hexString2.length() == 1)
                {
                    hexString2 = "0" + hexString2;
                }
                str2 = str2 + hexString2;
            }
            return str2;
        }
        catch (Exception unused)
        {
            return "";
        }
    }

    public static String str2HexStr(String str)
    {
        char[] charArray = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++)
        {
            sb.append(charArray[(bytes[i] & 240) >> 4]);
            sb.append(charArray[bytes[i] & 15]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static void showSoftInputFromWindow(AppCompatActivity appCompatActivity, EditText editText)
    {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        appCompatActivity.getWindow().setSoftInputMode(5);
    }

    public static String decode(String str)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(str.length() / 2);
        for (int i = 0; i < str.length(); i += 2)
        {
            byteArrayOutputStream.write((hexString.indexOf(str.charAt(i)) << 4) | hexString.indexOf(str.charAt(i + 1)));
        }
        return new String(byteArrayOutputStream.toByteArray());
    }

    public static String bytes2HexStr(byte[] bArr)
    {
        StringBuilder sb = new StringBuilder();
        if (bArr == null || bArr.length <= 0)
        {
            return "";
        }
        char[] cArr = new char[2];
        for (int i = 0; i < bArr.length; i++)
        {
            cArr[0] = Character.forDigit((bArr[i] >>> 4) & 15, 16);
            cArr[1] = Character.forDigit(bArr[i] & 15, 16);
            sb.append(cArr);
        }
        return sb.toString().toUpperCase();
    }

    public static boolean isNotificationEnabled(Context context)
    {
        try
        {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void goToSetNotification(Context context)
    {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26)
        {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        }
        else if (Build.VERSION.SDK_INT >= 21)
        {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }
        else
        {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static int dp2px(Context context, float f)
    {
        double d = (double) (f * context.getResources().getDisplayMetrics().density);
        Double.isNaN(d);
        return (int) (d + 0.5d);
    }

    public static boolean isIP(String str)
    {
        if (str.length() <= 0 || !str.contains(".") || str.split("\\.").length != 4 || str.startsWith(".") || str.endsWith("."))
        {
            return false;
        }
        boolean isNumeric = isNumeric(str.split("\\.")[0]);
        boolean isNumeric2 = isNumeric2(str.split("\\.")[1]);
        boolean isNumeric22 = isNumeric2(str.split("\\.")[2]);
        boolean isNumeric3 = isNumeric(str.split("\\.")[3]);
        if (!isNumeric || !isNumeric2 || !isNumeric22 || !isNumeric3)
        {
            return false;
        }
        return true;
    }

    public static boolean isIP2(String str)
    {
        if (str.length() <= 0 || !str.contains(".") || str.split("\\.").length != 4 || str.startsWith(".") || str.endsWith("."))
        {
            return false;
        }
        boolean isNumeric2 = isNumeric2(str.split("\\.")[0]);
        boolean isNumeric22 = isNumeric2(str.split("\\.")[1]);
        boolean isNumeric23 = isNumeric2(str.split("\\.")[2]);
        boolean isNumeric24 = isNumeric2(str.split("\\.")[3]);
        if (!isNumeric2 || !isNumeric22 || !isNumeric23 || !isNumeric24)
        {
            return false;
        }
        return true;
    }

    private static boolean isNumeric(String str)
    {
        try
        {
            int parseInt = Integer.parseInt(str);
            return parseInt < 256 && parseInt > 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isNumeric2(String str)
    {
        try
        {
            int parseInt = Integer.parseInt(str);
            return parseInt < 256 && parseInt >= 0;
        }
        catch (Exception unused)
        {
        }
        return false;
    }

    public static void netDely(final String str)
    {
        new Thread(new Runnable() {
            public void run()
            {
                try
                {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("ping -c 4 " + str).getInputStream()));
                    new String();
                    while (true)
                    {
                        String readLine = bufferedReader.readLine();
                        if (readLine != null)
                        {
                            if (readLine.contains("packet loss"))
                            {
                                readLine.substring(readLine.indexOf("received") + 10, readLine.indexOf("%") + 1);
                            }
                            if (readLine.contains("avg"))
                            {
                                int indexOf = readLine.indexOf(MqttTopic.TOPIC_LEVEL_SEPARATOR, 20);
                                String str = readLine.substring(indexOf + 1, readLine.indexOf(".", indexOf)) + "ms";
                            }
                        }
                        else
                        {
                            return;
                        }
                    }
                }
                catch (Exception unused)
                {
                }
            }
        }).start();
    }

    public static Bitmap DrawableToBitmap(Drawable drawable)
    {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        drawable.draw(new Canvas(createBitmap));
        return createBitmap;
    }

    public static String getStrFileSize(long j)
    {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        if (j > PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)
        {
            double d = (double) (((float) j) / 1024.0f);
            if (d > 1024.0d)
            {
                Double.isNaN(d);
                double d2 = d / 1024.0d;
                if (d2 > 1024.0d)
                {
                    return numberInstance.format(d2 / 1024.0d) + "GB";
                }
                return numberInstance.format(d2) + "MB";
            }
            return numberInstance.format(d) + "KB";
        }
        return numberInstance.format(j) + "B";
    }


    public static boolean isTime(String str)
    {
        try
        {
            String substring = str.substring(0, 2);
            String substring2 = str.substring(3, 5);
            String substring3 = str.substring(6);
            if (str.length() != 8 || !isInteger(substring) || !isInteger(substring2) || !isInteger(substring3) || str.charAt(2) != ':' || str.charAt(5) != ':')
            {
                return false;
            }
            return true;
        }
        catch (Exception unused)
        {
        }
        return false;
    }

    public static int getFontIndex(String str)
    {
        if (str.equals("1"))
        {
            return 1;
        }
        if (str.equals(ExifInterface.GPS_MEASUREMENT_2D))
        {
            return 2;
        }
        if (str.equals(ExifInterface.GPS_MEASUREMENT_3D))
        {
            return 3;
        }
        if (str.equals("4"))
        {
            return 4;
        }
        return str.equals("5") ? 5 : 0;
    }

    public static String getTextStyle(Context context, String str)
    {
        String string = context.getResources().getString(R.string.normal);
        if (company == 7)
        {
            if (str.equals("1"))
            {
                return context.getResources().getString(R.string.typeface11);
            }
            if (str.equals(ExifInterface.GPS_MEASUREMENT_2D))
            {
                return context.getResources().getString(R.string.typeface22);
            }
            if (str.equals(ExifInterface.GPS_MEASUREMENT_3D))
            {
                return context.getResources().getString(R.string.typeface33);
            }
            if (str.equals("4"))
            {
                return context.getResources().getString(R.string.typeface44);
            }
            return string;
        }
        else if (str.equals("1"))
        {
            return context.getResources().getString(R.string.typeface1);
        }
        else
        {
            if (str.equals(ExifInterface.GPS_MEASUREMENT_2D))
            {
                return context.getResources().getString(R.string.typeface2);
            }
            if (str.equals(ExifInterface.GPS_MEASUREMENT_3D))
            {
                return context.getResources().getString(R.string.typeface3);
            }
            if (str.equals("4"))
            {
                return context.getResources().getString(R.string.typeface4);
            }
            return str.equals("5") ? context.getResources().getString(R.string.typeface5) : string;
        }
    }

    public static Bitmap captureScreenBitmap(View view, int i, int i2)
    {
        if (view == null)
        {
            return null;
        }
        try
        {
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(false);
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap drawingCache = view.getDrawingCache();
            view.getWindowVisibleDisplayFrame(new Rect());
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(drawingCache, i, i2, false);
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(false);
            return createScaledBitmap;
        }
        catch (Exception unused)
        {
            return null;
        }
    }

    public static boolean isUseMediaType(String str)
    {
        return str.toLowerCase().endsWith("jpg") || str.toLowerCase().endsWith("bmp") || str.toLowerCase().endsWith("gif") || str.toLowerCase().endsWith("jpeg") || str.toLowerCase().endsWith("png") || str.toLowerCase().endsWith("mp4") || str.toLowerCase().endsWith("mov") || str.toLowerCase().endsWith("3gp") || str.toLowerCase().endsWith("mkv") || str.toLowerCase().endsWith("m4v") || str.toLowerCase().endsWith("avi") || str.toLowerCase().endsWith("wmv") || str.toLowerCase().endsWith("mpg") || str.toLowerCase().endsWith("mpeg");
    }


    public static String encryption(String str)
    {
        return md5(md5(str));
    }


    public static void hookWebView(Context context) throws IOException
    {
        Method method;
        int i = Build.VERSION.SDK_INT;
        try
        {
            Class<?> cls = Class.forName("android.webkit.WebViewFactory");
            Field declaredField = cls.getDeclaredField("sProviderInstance");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(null);
            if (obj == null)
            {
                if (i > 22)
                {
                    method = cls.getDeclaredMethod("getProviderClass", new Class[0]);
                }
                else if (i == 22)
                {
                    method = cls.getDeclaredMethod("getFactoryClass", new Class[0]);
                }
                else
                {
                    return;
                }
                method.setAccessible(true);
                Class cls2 = (Class) method.invoke(cls, new Object[0]);
                Class<?> cls3 = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> declaredConstructor = cls3.getDeclaredConstructor(new Class[0]);
                declaredConstructor.setAccessible(true);
                if (i < 26)
                {
                    Constructor constructor = cls2.getConstructor(cls3);
                    if (constructor != null)
                    {
                        constructor.setAccessible(true);
                        obj = constructor.newInstance(declaredConstructor.newInstance(new Object[0]));
                    }
                }
                else
                {
                    @SuppressLint("SoonBlockedPrivateApi") Field declaredField2 = cls.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                    declaredField2.setAccessible(true);
                    String str = (String) declaredField2.get(null);
                    if (str == null)
                    {
                        str = "create";
                    }
                    Method method2 = cls2.getMethod(str, cls3);
                    if (method2 != null)
                    {
                        obj = method2.invoke(null, declaredConstructor.newInstance(new Object[0]));
                    }
                }
                if (obj != null)
                {
                    declaredField.set("sProviderInstance", obj);
                }
            }
        }
        catch (Exception e)
        {
            MyException.outputException(context, e);
        }
    }

    public static boolean isPad(Context context)
    {
        if (((context.getResources().getConfiguration().screenLayout & 15) >= 3) || isPadPhone(context))
        {
            return true;
        }
        return false;
    }

    public static boolean isPadPhone(Context context)
    {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType() == 0;
    }

    private static boolean isPadSize(Context context)
    {
        Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        defaultDisplay.getWidth();
        defaultDisplay.getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return Math.sqrt(Math.pow((double) (((float) displayMetrics.widthPixels) / displayMetrics.xdpi), 2.0d) + Math.pow((double) (((float) displayMetrics.heightPixels) / displayMetrics.ydpi), 2.0d)) > 6.9d;
    }

    public static String[] getCurDateTime()
    {
        String format = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));
        return new String[]{format.substring(0, 4), format.substring(4, 6), format.substring(6)};
    }

    public static boolean isMobile(String str)
    {
        if (TextUtils.isEmpty(str))
        {
            return false;
        }
        return str.matches("[1][3456789]\\d{9}");
    }


    public static String getMonth(String str)
    {
        switch (Integer.parseInt(str))
        {
            case 2:
                return "Feb.";
            case 3:
                return "Mar.";
            case 4:
                return "Apr.";
            case 5:
                return "May.";
            case 6:
                return "Jun.";
            case 7:
                return "Jul.";
            case 8:
                return "Aug.";
            case 9:
                return "Sept.";
            case 10:
                return "Oct.";
            case 11:
                return "Nov.";
            case 12:
                return "Dec.";
            default:
                return "Jan.";
        }
    }

    public static String convertToRGB(int i)
    {
        String hexString2 = Integer.toHexString(Color.red(i));
        String hexString3 = Integer.toHexString(Color.green(i));
        String hexString4 = Integer.toHexString(Color.blue(i));
        if (hexString2.length() == 1)
        {
            hexString2 = "0" + hexString2;
        }
        if (hexString3.length() == 1)
        {
            hexString3 = "0" + hexString3;
        }
        if (hexString4.length() == 1)
        {
            hexString4 = "0" + hexString4;
        }
        return hexString2 + hexString3 + hexString4;
    }

    public static int convertToColorInt(String str) throws IllegalArgumentException
    {
        if (str.matches("[0-9a-fA-F]{1,6}"))
        {
            switch (str.length())
            {
                case 1:
                    return Color.parseColor("#00000" + str);
                case 2:
                    return Color.parseColor("#0000" + str);
                case 3:
                    char charAt = str.charAt(0);
                    char charAt2 = str.charAt(1);
                    char charAt3 = str.charAt(2);
                    return Color.parseColor(MqttTopic.MULTI_LEVEL_WILDCARD + charAt + charAt + charAt2 + charAt2 + charAt3 + charAt3);
                case 4:
                    return Color.parseColor("#00" + str);
                case 5:
                    return Color.parseColor("#0" + str);
                case 6:
                    return Color.parseColor(MqttTopic.MULTI_LEVEL_WILDCARD + str);
            }
        }
        throw new IllegalArgumentException(str + " is not a valid color.");
    }

    public static boolean isZhLanguage(Context context)
    {
        Locale locale;
        if (Build.VERSION.SDK_INT >= 24)
        {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        }
        else
        {
            locale = context.getResources().getConfiguration().locale;
        }
        return (locale.getLanguage() + "-" + locale.getCountry()).contains("zh");
    }


    public static boolean requestPermissions(Activity activity)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= 26)
            {
                int checkSelfPermission = activity.checkSelfPermission("android.permission.CAMERA");
                int checkSelfPermission2 = activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
                int checkSelfPermission3 = activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE");
                int checkSelfPermission4 = activity.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION");
                int checkSelfPermission5 = activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
                int checkSelfPermission6 = activity.checkSelfPermission("android.permission.ACCESS_WIFI_STATE");
                int checkSelfPermission7 = activity.checkSelfPermission("android.permission.BLUETOOTH");
                int checkSelfPermission8 = activity.checkSelfPermission("android.permission.RECORD_AUDIO");
                if (!(checkSelfPermission2 == 0 && checkSelfPermission3 == 0 && checkSelfPermission4 == 0 && checkSelfPermission5 == 0 && checkSelfPermission6 == 0 && checkSelfPermission == 0 && checkSelfPermission7 == 0 && checkSelfPermission8 == 0))
                {
                    activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_WIFI_STATE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.LOCATION_HARDWARE", "android.permission.READ_PHONE_STATE", "android.permission.WRITE_SETTINGS", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.REQUEST_INSTALL_PACKAGES", "android.permission.CAMERA", "android.permission.BLUETOOTH", "android.permission.RECORD_AUDIO", "android.permission.ACCESS_BACKGROUND_LOCATION"}, 16);
                    return true;
                }
            }
            else if (Build.VERSION.SDK_INT >= 23)
            {
                int checkSelfPermission9 = activity.checkSelfPermission("android.permission.CAMERA");
                int checkSelfPermission10 = activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
                int checkSelfPermission11 = activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE");
                int checkSelfPermission12 = activity.checkSelfPermission("android.permission.BLUETOOTH");
                if (!(checkSelfPermission10 == 0 && checkSelfPermission11 == 0 && checkSelfPermission9 == 0 && checkSelfPermission12 == 0))
                {
                    activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_WIFI_STATE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.LOCATION_HARDWARE", "android.permission.READ_PHONE_STATE", "android.permission.WRITE_SETTINGS", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.REQUEST_INSTALL_PACKAGES", "android.permission.BLUETOOTH", "android.permission.CAMERA"}, 16);
                    return true;
                }
            }
        }
        catch (Exception unused)
        {
        }
        return false;
    }

    public static boolean isHUAWEI()
    {
        return "huawei".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isMobileEnabled(Context context)
    {
        try
        {
            Method declaredMethod = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled", new Class[0]);
            declaredMethod.setAccessible(true);
            return ((Boolean) declaredMethod.invoke((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE), new Object[0])).booleanValue();
        }
        catch (Exception unused)
        {
            return false;
        }
    }

    public static boolean netStatu(Context context)
    {
        Network[] allNetworks;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < 21)
        {
            return connectivityManager.getNetworkInfo(0).isConnected();
        }
        HashMap hashMap = new HashMap();
        for (Network network : connectivityManager.getAllNetworks())
        {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            hashMap.put(networkInfo.getTypeName(), Boolean.valueOf(networkInfo.isConnected()));
        }
        if (hashMap.containsKey("MOBILE"))
        {
            return ((Boolean) hashMap.get("MOBILE")).booleanValue();
        }
        return false;
    }

    public static void RecursionDeleteFile(File file)
    {
        if (file.isFile())
        {
            file.delete();
        }
        else if (file.isDirectory())
        {
            File[] listFiles = file.listFiles();
            if (listFiles == null || listFiles.length == 0)
            {
                file.delete();
                return;
            }
            for (File file2 : listFiles)
            {
                RecursionDeleteFile(file2);
            }
            file.delete();
        }
    }


    public static String getStrColor(int i)
    {
        if (i == 0)
        {
            return "gggggg";
        }
        String hexString2 = Integer.toHexString(Color.red(i));
        if (hexString2.length() == 1)
        {
            hexString2 = "0" + hexString2;
        }
        String hexString3 = Integer.toHexString(Color.green(i));
        if (hexString3.length() == 1)
        {
            hexString3 = "0" + hexString3;
        }
        String hexString4 = Integer.toHexString(Color.blue(i));
        if (hexString4.length() == 1)
        {
            hexString4 = "0" + hexString4;
        }
        return hexString2 + hexString3 + hexString4;
    }

    public static int getIntColor(String str)
    {
        if (!Pattern.matches("[a-f0-9A-F]{6}", str))
        {
            return 0;
        }
        return Color.parseColor(MqttTopic.MULTI_LEVEL_WILDCARD + str);
    }

    public static String getWifiIP()
    {
        WifiManager wifiManager;
        int ipAddress;
        if (!isWifiContected() || (wifiManager = (WifiManager) App.Companion.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE)) == null || (ipAddress = wifiManager.getConnectionInfo().getIpAddress()) == 0)
        {
            return "";
        }
        return (ipAddress & 255) + "." + ((ipAddress >> 8) & 255) + "." + ((ipAddress >> 16) & 255) + "." + ((ipAddress >> 24) & 255);
    }

    public static String getWifiSSID(Context context)
    {
        NetworkInfo networkInfo;
        WifiInfo connectionInfo = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        String str = null;
        String ssid = connectionInfo != null ? connectionInfo.getSSID() : null;
        if (ssid != null)
        {
            str = ssid.trim();
        }
        if (!TextUtils.isEmpty(str) && str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"')
        {
            str = str.substring(1, str.length() - 1);
        }
        if ((TextUtils.isEmpty(str) || WIFISSID_UNKNOW.equalsIgnoreCase(str.trim())) && (networkInfo = getNetworkInfo(context)) != null && networkInfo.isConnected() && networkInfo.getExtraInfo() != null)
        {
            str = networkInfo.getExtraInfo().replace("\"", "");
        }
        if (TextUtils.isEmpty(str) || WIFISSID_UNKNOW.equalsIgnoreCase(str.trim()))
        {
            str = getSSIDByNetworkId(context);
        }
        return (str == null || str.length() <= 0 || str.charAt(0) != '\"' || str.charAt(str.length() - 1) != '\"') ? str : str.substring(1, str.length() - 1);
    }

    public static NetworkInfo getNetworkInfo(Context context)
    {
        try
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null)
            {
                return connectivityManager.getActiveNetworkInfo();
            }
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static String getSSIDByNetworkId(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null)
        {
            int networkId = wifiManager.getConnectionInfo().getNetworkId();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
            }
            for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks())
            {
                if (wifiConfiguration.networkId == networkId)
                {
                    return wifiConfiguration.SSID;
                }
            }
        }
        return WIFISSID_UNKNOW;
    }

    public static String[] getConnectWifiSsid() {
        if (((WifiManager) App.Companion.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE)) != null) {
            return new String[]{getWifiSSID(App.Companion.getInstance().getApplicationContext()), getWifiIP()};
        }
        return new String[]{"unknown ssid", ""};
    }

    public static boolean checkWifiIsEnable() {
        WifiManager wifiManager = (WifiManager) App.Companion.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    public static int getConnectWifiRssi() {
        WifiManager wifiManager = (WifiManager) App.Companion.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo().getRssi();
        }
        return 103;
    }

    public static String getWiFiAPConfig() {
        try {
            WifiManager wifiManager = (WifiManager) App.Companion.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration wifiConfiguration = (WifiConfiguration) wifiManager.getClass().getMethod("getWifiApConfiguration", new Class[0]).invoke(wifiManager, new Object[0]);
            if (wifiConfiguration == null) {
                return null;
            }
            return wifiConfiguration.SSID;
        } catch (Exception unused) {
            return null;
        }
    }

    public static float getFontWidth(Paint paint, String str) {
        return paint.measureText(str);
    }

    public static long getFileSize(File file) {
        if (!file.exists() || !file.isFile()) {
            return 0;
        }
        return file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    public static boolean isNetContected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
        if (connectivityManager == null || networkInfo == null || !networkInfo.isConnected()) {
            return false;
        }
        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null && (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    public static boolean isNetworkOnline() {
        try {
            new URL("https://www.baidu.com").openStream();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isWifiContected() {
        NetworkInfo networkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) App.Companion.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null || (networkInfo = connectivityManager.getNetworkInfo(1)) == null || !networkInfo.isConnected()) {
            return false;
        }
        return true;
    }

    public static void setWifiDormancy(Context context) {
        int i = Settings.System.getInt(context.getContentResolver(), "wifi_sleep_policy", 0);
        SharedPreferences.Editor edit = context.getSharedPreferences("wifi_sleep_policy", 0).edit();
        edit.putInt(Context.WIFI_SERVICE, i);
        edit.commit();
        if (2 != i) {
            Settings.System.putInt(context.getContentResolver(), "wifi_sleep_policy", 2);
        }
    }

    public static String getStrTextStyle(Context context, String str) {
        if (company == 7) {
            if (str.equals("1")) {
                return context.getResources().getString(R.string.typeface11);
            }
            if (str.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                return context.getResources().getString(R.string.typeface22);
            }
            if (str.equals(ExifInterface.GPS_MEASUREMENT_3D)) {
                return context.getResources().getString(R.string.typeface33);
            }
            if (str.equals("4")) {
                return context.getResources().getString(R.string.typeface44);
            }
        } else if (str.equals("1")) {
            return context.getResources().getString(R.string.typeface1);
        } else {
            if (str.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                return context.getResources().getString(R.string.typeface2);
            }
            if (str.equals(ExifInterface.GPS_MEASUREMENT_3D)) {
                return context.getResources().getString(R.string.typeface3);
            }
            if (str.equals("4")) {
                return context.getResources().getString(R.string.typeface4);
            }
            if (str.equals("5")) {
                return context.getResources().getString(R.string.typeface5);
            }
        }
        return context.getResources().getString(R.string.normal);
    }

    public static String getStrDisplay(Context context, int i) {
        if (i == 1) {
            return context.getResources().getString(R.string.display_over);
        }
        return context.getResources().getString(R.string.display_self);
    }

    public static String getStrMSTime(long j) {
        String str;
        String str2;
        long j2 = j / 60;
        long j3 = j % 60;
        if (j2 < 10) {
            str = "0" + j2;
        } else {
            str = "" + j2;
        }
        if (j3 < 10) {
            str2 = "0" + j3;
        } else {
            str2 = "" + j3;
        }
        return str + ":" + str2;
    }

    public static String getStrHMSTime(long j) {
        String str;
        String str2;
        String str3;
        long j2 = j / 60;
        long j3 = j2 / 60;
        long j4 = j2 % 60;
        long j5 = j % 60;
        if (j3 < 10) {
            str = "0" + j3;
        } else {
            str = "" + j3;
        }
        if (j4 < 10) {
            str2 = "0" + j4;
        } else {
            str2 = "" + j4;
        }
        if (j5 < 10) {
            str3 = "0" + j5;
        } else {
            str3 = "" + j5;
        }
        return str + ":" + str2 + ":" + str3;
    }

    public static String formatSize(long j) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        if (j > PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            float f = ((float) j) / 1024.0f;
            if (f > 1024.0f) {
                float f2 = f / 1024.0f;
                if (f2 > 1024.0f) {
                    return numberInstance.format((double) (f2 / 1024.0f)) + "G";
                }
                return numberInstance.format((double) f2) + "M";
            }
            return numberInstance.format((double) f) + "K";
        }
        return j + "B";
    }

    public static String getSpeed(Context context, String str) {
        String string = context.getResources().getString(R.string.speed_slow);
        if (str.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
            return context.getResources().getString(R.string.speed_fast);
        }
        return str.equals("1") ? context.getResources().getString(R.string.speed_normal) : string;
    }

    public static String getTextDir(Context context, int i) {
        String string = context.getResources().getString(R.string.direction_level);
        if (i == 1) {
            return context.getResources().getString(R.string.direction_vertical);
        }
        if (i == 2) {
            return context.getResources().getString(R.string.direction_circle);
        }
        if (i == 3) {
            return context.getResources().getString(R.string.direction_rect);
        }
        if (i == 4) {
            return context.getResources().getString(R.string.direction_trigon);
        }
        return i == 5 ? context.getResources().getString(R.string.direction_diamond) : string;
    }

    public static String getPicDir(Context context, int i) {
        String string = context.getResources().getString(R.string.direction_1);
        if (i == 1) {
            return context.getResources().getString(R.string.direction_2);
        }
        if (i == 2) {
            return context.getResources().getString(R.string.direction_3);
        }
        return i == 3 ? context.getResources().getString(R.string.direction_4) : string;
    }

    public static String getClockType(Context context, int i) {
        return i == 1 ? context.getResources().getString(R.string.direction_analog_clock) : context.getResources().getString(R.string.direction_digital_clock);
    }

    public static String getClockStyle(Context context, int i) {
        return i == 1 ? context.getResources().getString(R.string.clock_style_second) : context.getResources().getString(R.string.clock_style_first);
    }

    public static String getWed(Context context, int i) {
        String string = context.getResources().getString(R.string.mon);
        switch (i) {
            case 2:
                return context.getResources().getString(R.string.tue);
            case 3:
                return context.getResources().getString(R.string.wed);
            case 4:
                return context.getResources().getString(R.string.thu);
            case 5:
                return context.getResources().getString(R.string.fri);
            case 6:
                return context.getResources().getString(R.string.sat);
            case 7:
                return context.getResources().getString(R.string.sun);
            default:
                return string;
        }
    }



    public static String getNetMode(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(context.getResources().getString(R.string.net_mode_dhcp));
        arrayList.add(context.getResources().getString(R.string.net_mode_static));
        return (String) arrayList.get(i);
    }

    public static boolean isHotName(String str) {
        return str.startsWith("YD_") || str.startsWith("XTA_") || str.startsWith("Absen_") || str.startsWith("Nasrin_") || str.startsWith("MiiLan_") || str.startsWith("QL280_") || str.startsWith("QL250_") || str.startsWith("QL480_");
    }

    public static boolean isLegalDevice(String str) {
        if (str == null) {
            return false;
        }
        if (str.startsWith("YD_") || str.startsWith("Absen_") || str.startsWith("QL") || str.startsWith("XTA_") || str.startsWith("MiiLan_") || str.startsWith("Nasrin_")) {
            return true;
        }
        return false;
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    public void restoreWifiDormancy(Context context) {
        Settings.System.putInt(context.getContentResolver(), "wifi_sleep_policy", context.getSharedPreferences("wifi_sleep_policy", 0).getInt(Context.WIFI_SERVICE, 0));
    }

    public static String getStrCurTime(Context context) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEEE");
        if (!"zh".equals(context.getResources().getConfiguration().locale.getLanguage())) {
            return new SimpleDateFormat("MMM d, yyyy h:m:s aa", Locale.ENGLISH).format(date);
        }
        String format = simpleDateFormat.format(date);
        return format.substring(0, format.length() - 7).replace("时", ":").replace("分", "");
    }

    public static void captureScreen3(final Context context, final View view, final String str) {
        new Thread(new Runnable() {
            /* class com.pptouch.utils.Utils.AnonymousClass20 */

            public void run() {
                try {
                    if (view != null) {
                        view.destroyDrawingCache();
                        view.setDrawingCacheEnabled(false);
                        view.setDrawingCacheEnabled(true);
                        view.buildDrawingCache();
                        Bitmap drawingCache = view.getDrawingCache();
                        view.getWindowVisibleDisplayFrame(new Rect());
                        Bitmap createBitmap = Bitmap.createBitmap(drawingCache, 0, 0, drawingCache.getWidth(), drawingCache.getHeight(), (Matrix) null, false);
                        File file = new File(RouteInfo.ROOT_SCREENSHOT);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        String str = RouteInfo.ROOT_SCREENSHOT;
                        File file2 = new File(str, str + ".jpg");
                        if (file2.exists()) {
                            file2.delete();
                        }
                        try {
                            String str2 = RouteInfo.ROOT_SCREENSHOT;
                            File file3 = new File(str2, str + ".jpg");
                            FileOutputStream fileOutputStream = new FileOutputStream(file3);
                            createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file3)));
                            view.destroyDrawingCache();
                            view.setDrawingCacheEnabled(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception unused) {
                }
            }
        }).start();
    }


    public static boolean isInteger(String str) {
        return Pattern.compile("^[-\\+]?[\\d]*$").matcher(str).matches();
    }

    public static String formatTimeUnit(int i) {
        if (i >= 10) {
            return String.valueOf(i);
        }
        return "0" + String.valueOf(i);
    }

    public static String getFormtTime(long j) {
        StringBuilder sb = new StringBuilder();
        long j2 = j / 60;
        sb.append((j2 / 60) % 60);
        sb.append(":");
        sb.append(j2 % 60);
        sb.append(":");
        sb.append(j % 60);
        return sb.toString();
    }

    public static int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2dip(Context context, float f) {
        return (int) ((f / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static float[] getScreenConfig(Context context) {
        int daoHangHeight = getDaoHangHeight(context);
        int statusBarHeight = getStatusBarHeight(context);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        float width = (float) windowManager.getDefaultDisplay().getWidth();
        float height = (float) windowManager.getDefaultDisplay().getHeight();
        if (padType == 1) {
            if (width < height) {
                return new float[]{height, width + ((float) daoHangHeight) + ((float) statusBarHeight)};
            }
        } else if (width > height) {
            return new float[]{height, width + ((float) daoHangHeight) + ((float) statusBarHeight)};
        }
        return new float[]{width, height + ((float) daoHangHeight) + ((float) statusBarHeight)};
    }

    private static int getStatusBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static int getDaoHangHeight(Context context) {
        if (context.getResources().getIdentifier("config_showNavigationBar", "bool", "android") == 0) {
            return 0;
        }
        return context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("navigation_bar_height", "dimen", "android"));
    }

    public static boolean isOverlap(RectF rectF, RectF rectF2, long j, long j2, long j3, long j4) {
        RectF rectF3 = new RectF();
        rectF3.left = Math.max(rectF.left, rectF2.left);
        rectF3.right = Math.min(rectF.right, rectF2.right);
        rectF3.top = Math.max(rectF.top, rectF2.top);
        rectF3.bottom = Math.min(rectF.bottom, rectF2.bottom);
        if ((rectF3.left >= rectF3.right || ((j <= j3 || j4 < j) && (j3 <= j || j2 < j3))) && rectF3.isEmpty() && isOverLine(rectF, rectF2)) {
            return true;
        }
        return false;
    }

    public static boolean isOverLine(RectF rectF, RectF rectF2) {
        if (rectF.left == rectF.right) {
            if (rectF.left >= rectF2.right || rectF.left <= rectF2.left || rectF.top >= rectF2.bottom || rectF.bottom <= rectF2.top) {
                return true;
            }
            return false;
        } else if (rectF.top != rectF.bottom || rectF.top >= rectF2.bottom || rectF.top <= rectF2.top || rectF.left >= rectF2.right || rectF.right <= rectF2.left) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isOverlap(RectF rectF, RectF rectF2) {
        RectF rectF3 = new RectF();
        rectF3.left = Math.max(rectF.left, rectF2.left);
        rectF3.right = Math.min(rectF.right, rectF2.right);
        rectF3.top = Math.max(rectF.top, rectF2.top);
        rectF3.bottom = Math.min(rectF.bottom, rectF2.bottom);
        if (rectF3.width() < 2.0f) {
            return false;
        }
        return !rectF3.isEmpty();
    }

    public static Typeface getFontStyle(Context context, String str) {
        Typeface typeface = Typeface.DEFAULT;
        if (company == 7) {
            if (str.equals("1")) {
                return Typeface.createFromAsset(context.getResources().getAssets(), "fonts/方正萤雪简体.TTF");
            }
            if (str.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                return Typeface.createFromAsset(context.getResources().getAssets(), "fonts/方正兰亭纤黑简体.TTF");
            }
            if (str.equals(ExifInterface.GPS_MEASUREMENT_3D)) {
                return Typeface.createFromAsset(context.getResources().getAssets(), "fonts/方正彦辰雅黑简体.TTF");
            }
            if (str.equals("4")) {
                return Typeface.createFromAsset(context.getResources().getAssets(), "fonts/方正榜书行简体.TTF");
            }
            return typeface;
        } else if (str.equals("1")) {
            return Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Aa剑豪体.ttf");
        } else {
            if (str.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                return Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Leefont蒙黑体.ttf");
            }
            if (str.equals(ExifInterface.GPS_MEASUREMENT_3D)) {
                return Typeface.createFromAsset(context.getResources().getAssets(), "fonts/庞门正道细线体.ttf");
            }
            if (str.equals("4")) {
                return Typeface.createFromAsset(context.getResources().getAssets(), "fonts/优设好身体.ttf");
            }
            return str.equals("5") ? Typeface.createFromAsset(context.getResources().getAssets(), "fonts/余繁新语.ttf") : typeface;
        }
    }

    public static int getIndexTx(String str) {
        if (str == null) {
            str = "None";
        }
        if (str.equals("Window Shades") || str.equals("百叶窗")) {
            return 1;
        }
        if (str.equals("Ladder") || str.equals("阶梯")) {
            return 2;
        }
        if (str.equals("Checkerboard") || str.equals("棋盘")) {
            return 3;
        }
        if (str.equals("Random Line") || str.equals("随机线")) {
            return 4;
        }
        if (str.equals(MqttDisconnect.KEY) || str.equals("圆盘")) {
            return 5;
        }
        if (str.equals("Pinwheel") || str.equals("风车")) {
            return 6;
        }
        if (str.equals("Fade In") || str.equals("淡入")) {
            return 7;
        }
        return (str.equals("Random") || str.equals("随机")) ? 8 : 0;
    }

    public static String getTx(Context context, String str) {
        String string = context.getResources().getString(R.string.tx_wu);
        if (str == null) {
            return string;
        }
        if (str.equals("Window Shades") || str.equals("百叶窗")) {
            return context.getResources().getString(R.string.tx_byc);
        }
        if (str.equals("Ladder") || str.equals("阶梯")) {
            return context.getResources().getString(R.string.tx_jt);
        }
        if (str.equals("Checkerboard") || str.equals("棋盘")) {
            return context.getResources().getString(R.string.tx_qp);
        }
        if (str.equals("Random Line") || str.equals("随机线")) {
            return context.getResources().getString(R.string.tx_sjx);
        }
        if (str.equals(MqttDisconnect.KEY) || str.equals("圆盘")) {
            return context.getResources().getString(R.string.tx_yp);
        }
        if (str.equals("Pinwheel") || str.equals("风车")) {
            return context.getResources().getString(R.string.tx_fc);
        }
        if (str.equals("Fade In") || str.equals("淡入")) {
            return context.getResources().getString(R.string.tx_dr);
        }
        if (str.equals("Random") || str.equals("随机")) {
            return context.getResources().getString(R.string.tx_sj);
        }
        return string;
    }

    public static String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public static String getRandomCode() {
        return new String(UUID.randomUUID().toString());
    }


    public static void updateText(String str) {
        try {
            if (sTipTextView != null) {
                sTipTextView.setText(str);
            }
        } catch (Exception unused) {
        }
    }

    public static boolean isAppExist(Context context, String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            applicationInfo = null;
        }
        return applicationInfo != null;
    }

    public static void reQuest(final String str) {
        new Thread(new Runnable() {
            /* class com.pptouch.utils.Utils.AnonymousClass22 */

            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                    httpURLConnection.setConnectTimeout(1000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.getInputStream();
                } catch (Exception unused) {
                }
            }
        }).start();
    }

    public static String encode(String str) {
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 240) >> 4));
            sb.append(hexString.charAt((bytes[i] & 15) >> 0));
        }
        return sb.toString();
    }

    private static String readInfo(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            try {
                int read = inputStream.read(bArr);
                if (read != -1) {
                    byteArrayOutputStream.write(bArr, 0, read);
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    inputStream.close();
                    return new String(byteArray);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }



    public static Boolean fileChannelCopy(Context context, File file, File file2) throws Throwable
    {
        FileChannel fileChannel;
        FileOutputStream fileOutputStream;
        Throwable th;
        FileInputStream fileInputStream;
        FileChannel fileChannel2;
        IOException e;
        FileChannel fileChannel3;
        FileInputStream fileInputStream2 = null;
        FileChannel fileChannel4 = null;
        FileChannel fileChannel5 = null;
        try {
            fileInputStream = new FileInputStream(file);
            try {
                fileOutputStream = new FileOutputStream(file2);
                try {
                    fileChannel2 = fileInputStream.getChannel();
                    try {
                        fileChannel4 = fileOutputStream.getChannel();
                        fileChannel2.transferTo(0, fileChannel2.size(), fileChannel4);
                        try {
                            fileInputStream.close();
                            fileChannel2.close();
                            fileOutputStream.close();
                            fileChannel4.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        return true;
                    } catch (IOException e3) {
                        e = e3;
                        fileChannel3 = fileChannel4;
                        fileInputStream2 = fileInputStream;
                        try {
                            e.printStackTrace();
                            try {
                                fileInputStream2.close();
                                fileChannel2.close();
                                fileOutputStream.close();
                                fileChannel3.close();
                            } catch (IOException e4) {
                                e4.printStackTrace();
                            }
                            return false;
                        } catch (Throwable th2) {
                            th = th2;
                            fileInputStream = fileInputStream2;
                            fileChannel5 = fileChannel2;
                            fileChannel = fileChannel3;
                            try {
                                fileInputStream.close();
                                fileChannel5.close();
                                fileOutputStream.close();
                                fileChannel.close();
                            } catch (IOException e5) {
                                e5.printStackTrace();
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        fileChannel = fileChannel4;
                        fileChannel5 = fileChannel2;
                        fileInputStream.close();
                        fileChannel5.close();
                        fileOutputStream.close();
                        fileChannel.close();
                        throw th;
                    }
                } catch (IOException e6) {
                    e = e6;
                    fileChannel2 = null;
                    fileInputStream2 = fileInputStream;
                    fileChannel3 = fileChannel2;
                    e.printStackTrace();
                    fileInputStream2.close();
                    fileChannel2.close();
                    fileOutputStream.close();
                    fileChannel3.close();
                    return false;
                } catch (Throwable th4) {
                    th = th4;
                    fileChannel = null;
                    fileInputStream.close();
                    fileChannel5.close();
                    fileOutputStream.close();
                    fileChannel.close();
                    throw th;
                }
            } catch (IOException e7) {
                e = e7;
                fileOutputStream = null;
                fileChannel2 = null;
                fileInputStream2 = fileInputStream;
                fileChannel3 = fileChannel2;
                e.printStackTrace();
                fileInputStream2.close();
                fileChannel2.close();
                fileOutputStream.close();
                fileChannel3.close();
                return false;
            } catch (Throwable th5) {
                th = th5;
                fileOutputStream = null;
                fileChannel = null;
                fileInputStream.close();
                fileChannel5.close();
                fileOutputStream.close();
                fileChannel.close();
                throw th;
            }
        } catch (IOException e8) {
            e = e8;
            fileOutputStream = null;
            fileChannel2 = null;
            fileChannel3 = fileChannel2;
            e.printStackTrace();
            fileInputStream2.close();
            fileChannel2.close();
            fileOutputStream.close();
            fileChannel3.close();
            return false;
        } catch (Throwable th6) {
            th = th6;
            fileOutputStream = null;
            fileChannel = null;
            fileInputStream = null;
            fileInputStream.close();
            fileChannel5.close();
            fileOutputStream.close();
            fileChannel.close();
            throw th;
        }
    }

    public static BitmapDrawable getImageDrawable(String str) {
        try {
            File file = new File(str);
            if (!file.exists()) {
                return null;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[1024];
            FileInputStream fileInputStream = new FileInputStream(file);
            for (int read = fileInputStream.read(bArr); read != -1; read = fileInputStream.read(bArr)) {
                byteArrayOutputStream.write(bArr, 0, read);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return new BitmapDrawable(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        } catch (Exception unused) {
            return null;
        }
    }

    public static void hideSoft(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) App.Companion.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static void updateOptions(RequestOptions requestOptions, String str, long j) {
        if (str.length() > 0 && str.contains(".")) {
            try {
                requestOptions.signature(new MediaStoreSignature(MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.substring(str.lastIndexOf(".") + 1).toLowerCase()), j, 0));
            } catch (Exception unused) {
            }
        }
    }

    public static String getlocalip() {
        WifiManager wifiManager = (WifiManager) App.Companion.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return "";
        }
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        if (ipAddress == 0) {
            return null;
        }
        return (ipAddress & 255) + "." + ((ipAddress >> 8) & 255) + "." + ((ipAddress >> 16) & 255) + "." + ((ipAddress >> 24) & 255);
    }

    public static void copyToClipboard(Context context, String str) {
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("text", str));
    }

    public static String getVideoSort(String str) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(str);
        String extractMetadata = mediaMetadataRetriever.extractMetadata(19);
        String extractMetadata2 = mediaMetadataRetriever.extractMetadata(18);
        if (((float) Integer.parseInt(extractMetadata2)) / ((float) Integer.parseInt(extractMetadata)) == 1.7777778f) {
            return "16:9";
        }
        if (((float) Integer.parseInt(extractMetadata2)) / ((float) Integer.parseInt(extractMetadata)) == 1.25f) {
            return "5:4";
        }
        return extractMetadata2 + ":" + extractMetadata;
    }

    public static String getColorTitle(Context context, int i) {
        String string = context.getResources().getString(R.string.red);
        switch (i) {
            case 1:
                return context.getResources().getString(R.string.green);
            case 2:
                return context.getResources().getString(R.string.blue);
            case 3:
                return context.getResources().getString(R.string.white);
            case 4:
                return context.getResources().getString(R.string.yellow);
            case 5:
                return context.getResources().getString(R.string.cyan);
            case 6:
                return context.getResources().getString(R.string.purple);
            case 7:
                return context.getResources().getString(R.string.gradient_red);
            case 8:
                return context.getResources().getString(R.string.gradient_green);
            case 9:
                return context.getResources().getString(R.string.gradient_blue);
            case 10:
                return context.getResources().getString(R.string.gradient_white);
            case 11:
                return context.getResources().getString(R.string.gradient_yellow);
            case 12:
                return context.getResources().getString(R.string.gradient_cyan);
            case 13:
                return context.getResources().getString(R.string.gradient_purple);
            case 14:
                return context.getResources().getString(R.string.gradient_gray);
            default:
                return string;
        }
    }


    public static boolean isXiaomi() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("xiaomi");
    }

    public static boolean isVIVO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("vivo");
    }


    public static boolean ignoreBatteryOptimization(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        try {
            if (!((PowerManager) App.Companion.getInstance().getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(App.Companion.getInstance().getPackageName())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("ex", e.getMessage());
        }
        return false;
    }



    @SuppressLint("RestrictedApi")
    public static int getColor(int i) {
        switch (i) {
            case 1:
                return -16711936;
            case 2:
                return -16776961;
            case 3:
                return -1;
            case 4:
                return InputDeviceCompat.SOURCE_ANY;
            case 5:
                return -16711681;
            case 6:
                return Color.parseColor("#800080");
            default:
                return SupportMenu.CATEGORY_MASK;
        }
    }

    public static List<CoordinateEntity> changeList(List<CoordinateEntity> list, double d) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            double x1 = (double) list.get(i).getX1();
            Double.isNaN(x1);
            double y1 = (double) list.get(i).getY1();
            Double.isNaN(y1);
            double x2 = (double) list.get(i).getX2();
            Double.isNaN(x2);
            double y2 = (double) list.get(i).getY2();
            Double.isNaN(y2);
            arrayList.add(new CoordinateEntity((float) (x1 * d), (float) (y1 * d), (float) (x2 * d), (float) (y2 * d)));
        }
        return arrayList;
    }
}
