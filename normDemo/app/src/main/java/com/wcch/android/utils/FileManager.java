package com.wcch.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;

import com.wcch.android.entity.AppInfo;
import com.wcch.android.entity.FileBean;
import com.wcch.android.entity.ImgFolderBean;
import com.wcch.android.entity.Music;
import com.wcch.android.entity.Pic;
import com.wcch.android.entity.Video;
import com.wcch.android.entity.VideoPic;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileManager {
    private static ContentResolver mContentResolver;
    private static Context mContext;
    private static FileManager mInstance;
    private static Object mLock = new Object();
    private List<FileBean> mFiles = new ArrayList();

    public static FileManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new FileManager();
                    mContext = context;
                    mContentResolver = context.getContentResolver();
                }
            }
        }
        return mInstance;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0085, code lost:
        if (r1 != null) goto L_0x0090;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x008e, code lost:
        if (0 == 0) goto L_0x0093;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0090, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0093, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public List<Music> getMusics() {
        /*
        // Method dump skipped, instructions count: 156
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pptouch.utils.FileManager.getMusics():java.util.List");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0162, code lost:
        if (r2 != null) goto L_0x016d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x016b, code lost:
        if (0 == 0) goto L_0x0170;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x016d, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0170, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public List<Video> getVideos() {
        /*
        // Method dump skipped, instructions count: 377
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pptouch.utils.FileManager.getVideos():java.util.List");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0106, code lost:
        if (r2 != null) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x010f, code lost:
        if (0 == 0) goto L_0x0114;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0111, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0114, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public List<Pic> getPics() {
        /*
        // Method dump skipped, instructions count: 285
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pptouch.utils.FileManager.getPics():java.util.List");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0163, code lost:
        if (r2 != null) goto L_0x016e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x016c, code lost:
        if (0 == 0) goto L_0x0171;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x016e, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0171, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public List<VideoPic> getMediaVideos() {
        /*
        // Method dump skipped, instructions count: 378
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pptouch.utils.FileManager.getMediaVideos():java.util.List");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0107, code lost:
        if (r2 != null) goto L_0x0112;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0110, code lost:
        if (0 == 0) goto L_0x0115;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0112, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0115, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public List<VideoPic> getMediaPics() {
        /*
        // Method dump skipped, instructions count: 286
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pptouch.utils.FileManager.getMediaPics():java.util.List");
    }

    public Bitmap getVideoThumbnail(int i) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, (long) i, 3, options);
    }

    public Bitmap getPicThumbnail(int i) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return MediaStore.Images.Thumbnails.getThumbnail(mContentResolver, (long) i, 3, options);
    }

    public List<FileBean> getFilesByType2(int i) {
        this.mFiles.clear();
        readInfo(Environment.getExternalStorageDirectory(), i);
        if (this.mFiles.size() > 1) {
            Collections.sort(this.mFiles, new Comparator<FileBean>() {
                /* class com.pptouch.utils.FileManager.AnonymousClass1 */

                public int compare(FileBean fileBean, FileBean fileBean2) {
                    if (fileBean.date_modified < fileBean2.date_modified) {
                        return 1;
                    }
                    return fileBean.date_modified == fileBean2.date_modified ? 0 : -1;
                }
            });
        }
        return this.mFiles;
    }

    public void readInfo(File file, int i) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.isFile()) {
                    String absolutePath = file2.getAbsolutePath();
                    if (FileUtils.getFileType(absolutePath) == i) {
                        this.mFiles.add(new FileBean(absolutePath, file2.length(), FileUtils.getFileIconByPath(absolutePath), file2.lastModified() / 1000));
                    }
                } else if (file2.isDirectory()) {
                    readInfo(file2, i);
                }
            }
        }
    }

    public List<FileBean> getFilesByWeixin(int i) {
        this.mFiles.clear();
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.tencent.mm/MicroMsg/Download/");
        File file2 = new File(Environment.getExternalStorageDirectory().toString() + "/Android/data/com.tencent.wework/files/filecache/");
        if (file.exists()) {
            readInfo(file, i);
        }
        if (file2.exists()) {
            readInfo(file2, i);
        }
        if (this.mFiles.size() > 1) {
            Collections.sort(this.mFiles, new Comparator<FileBean>() {
                /* class com.pptouch.utils.FileManager.AnonymousClass2 */

                public int compare(FileBean fileBean, FileBean fileBean2) {
                    if (fileBean.date_modified < fileBean2.date_modified) {
                        return 1;
                    }
                    return fileBean.date_modified == fileBean2.date_modified ? 0 : -1;
                }
            });
        }
        return this.mFiles;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0070, code lost:
        if (r4 != null) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0079, code lost:
        if (0 == 0) goto L_0x007e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x007b, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0082, code lost:
        if (r3.size() != 0) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0088, code lost:
        return getFilesByType2(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x008d, code lost:
        if (r3.size() <= 1) goto L_0x0097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x008f, code lost:
        java.util.Collections.sort(r3, new com.pptouch.utils.FileManager.AnonymousClass3(r14));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0097, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public List<FileBean> getFilesByType(int r15) {
        /*
        // Method dump skipped, instructions count: 160
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pptouch.utils.FileManager.getFilesByType(int):java.util.List");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0072, code lost:
        if (r1 != null) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x007b, code lost:
        if (0 == 0) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x007d, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0080, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public List<ImgFolderBean> getImageFolders() {
        /*
        // Method dump skipped, instructions count: 137
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pptouch.utils.FileManager.getImageFolders():java.util.List");
    }

    public List<String> getImgListByDir(String str) {
        ArrayList arrayList = new ArrayList();
        File file = new File(str);
        if (!file.exists()) {
            return arrayList;
        }
        for (File file2 : file.listFiles()) {
            String absolutePath = file2.getAbsolutePath();
            if (FileUtils.isPicFile(absolutePath)) {
                arrayList.add(absolutePath);
            }
        }
        return arrayList;
    }

    public List<AppInfo> getAppInfos() {
        ArrayList arrayList = new ArrayList();
        PackageManager packageManager = mContext.getPackageManager();
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            AppInfo appInfo = new AppInfo();
            appInfo.setApplicationInfo(packageInfo.applicationInfo);
            appInfo.setVersionCode(packageInfo.versionCode);
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            String charSequence = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(charSequence);
            String str = packageInfo.packageName;
            appInfo.setApkPackageName(str);
            long length = new File(packageInfo.applicationInfo.sourceDir).length();
            appInfo.setApkSize(length);
            System.out.println("---------------------------");
            PrintStream printStream = System.out;
            printStream.println("程序的名字:" + charSequence);
            PrintStream printStream2 = System.out;
            printStream2.println("程序的包名:" + str);
            PrintStream printStream3 = System.out;
            printStream3.println("程序的大小:" + length);
            int i = packageInfo.applicationInfo.flags;
            if ((i & 1) != 0) {
                appInfo.setIsUserApp(false);
            } else {
                appInfo.setIsUserApp(true);
            }
            if ((i & 262144) != 0) {
                appInfo.setIsRom(false);
            } else {
                appInfo.setIsRom(true);
            }
            arrayList.add(appInfo);
        }
        return arrayList;
    }
}
