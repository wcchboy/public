package com.wcch.android.soft.util;

import android.content.Context;
import android.os.Environment;

import com.igrs.sml.util.L;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileUtils {
    private final static String TAG = FileUtils.class.getSimpleName();

    /**
     * 转换文件大小
     */
    public static String getFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

//    public static void deleteDownloadApk() {
//        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/temp";
//        File dir = new File(savePath);
//        if (dir.exists()) {
//            dir.delete();
//        }
//    }

    //删除文件夹和文件夹里面的文件
    public static void deleteDownloadApk() {
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/temp";
        File dir = new File(savePath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static String receiveFileSavePath() {
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LenovoProjection";
        File dir = new File(savePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return savePath;
    }

    /**
        获取 Android 当前应用内部私有根目录
     */
    public static String getInternalStoreFilesPath(Context context){
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 获取 Android 当前应用外部公共根目录
     * @param context
     * @return
     */
    public static String getExternalStoreFilesPath(Context context){
        String filesPath ;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            filesPath = context.getExternalFilesDir(null).getPath() ;
        }else {
            //外部存储不可用
            filesPath = context.getCacheDir().getPath() ;
        }
        return filesPath;
    }
    /**
     * 创建根目录
     *
     * @param path
     *            目录路径
     */
    public static void createDirFile(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    /**
     * 创建文件
     *
     * @param path
     *            文件路径
     * @return 创建的文件
     */
    public static File createNewFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return file;
    }

    /**
     * 删除文件夹
     *
     * @param folderPath
     *            文件夹的路径
     */
    public static void delFolder(String folderPath) {
        delAllFile(folderPath);
        String filePath = folderPath;
        filePath = filePath;
        File myFilePath = new File(filePath);
        myFilePath.delete();
    }

    /**
     * 删除文件
     *
     * @param path
     *            文件的路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
            }
        }
    }

    /**
     * 返回对象的唯一标识符
     * @param obj 需要取得唯一标识符的对象
     * @return className@hashcode 形式的唯一标识符。
     */
    public static String identityToString(Object obj){
        try{
            return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z");
        return sdf.format(new Date());
    }
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /*
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static boolean isFileExist(String filePath) {
        if (null == filePath) {
            return false;
        }

        File file = new File(filePath);
        if (file.isFile()) {
            return true;
        }
        return false;
    }

    public static String[] parseDirSturctor(String remotePathName) {
        ArrayList<String> dirList = new ArrayList<String>();
        String path = remotePathName;
        int index = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/' && i > 0) {
                dirList.add(path.substring(0, i));
                index = i;
            }
        }
        if (index < path.length()) {
            dirList.add(path.substring(0, path.length()));
        }
        String[] retStr = new String[dirList.size()];
        return (String[]) dirList.toArray(retStr);
    }

    public static boolean createFile(String fileName) {
        String[] dirStructor = parseDirSturctor(fileName);
        File file;
        for (int i = 0; i < dirStructor.length; i++) {
            file = new File(dirStructor[i]);
            if (i == dirStructor.length - 1) {
                if (!file.isFile()) {
                    return createNewFile(file);
                }
            } else {
                if (!file.exists()) {
                    file.mkdir();
                }
            }
        }
        return false;
    }

    public static boolean createNewFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            L.e(TAG, "文件不存在！" + "\n");
        }
    }

    public static void WriteText(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String ReadText(String fileName) {
        File _file = new File(fileName);
        return ReadText(_file);
    }

    public static String ReadText(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            int nLine = 0;
            while ((s = br.readLine()) != null) {
                //使用readLine方法，一次读一行
                if (nLine != 0) {
                    result.append("\n");
                }
                result.append(s);
                nLine++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    public static void deleteFile(String fileName) {
        File _file = new File(fileName);
        deleteFile(_file);
    }

    // 根据id删除目录中的对应文件
    public static void deleteFilesInDirByID(File file, String id) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    File temp = files[i];
                    if (temp.getName().startsWith(id + "_")) {
                        L.i(TAG, "deleteFilesInDirByID--->delete " + temp.getPath());
                        deleteFile(temp); // 把每个文件 用这个方法进行迭代
                    }
                }
            }
            // file.delete();
        } else {
            L.e(TAG, "文件不存在！" + "\n");
        }
    }

    public static String getFileName(String path) {
        if (path == null || path.length() <= 0) {
            L.e(TAG, "getFileName--->error!");
            return null;
        }

        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static boolean copyFile(String fromFile, String toFile) {
        File source = new File(fromFile);
        File dest = new File(toFile);
        deleteFile(dest);
        if (source.exists() == false)
            return false;

        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (Exception _ex) {

        }


        try {
            inputChannel.close();
        } catch (Exception _ex) {
        }
        try {
            outputChannel.close();
        } catch (Exception _ex) {
        }

        return false;
    }

    public static int copy(String fromFile, String toFile) {
        // 要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        // 如同判断SD卡是否存在或者文件是否存在
        // 如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        // 如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        // 目标目录
        File targetDir = new File(toFile);
        // 创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        // 遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory())// 如果当前项为子目录 进行递归
            {
                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

            } else
            // 如果当前项为文件则进行文件拷贝
            {
                CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return 0;
    }

    // 文件拷贝
    // 要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static int CopySdcardFile(String fromFile, String toFile) {
        L.d(TAG, "CopySdcardFile--->fromFile = " + fromFile + ", toFile = " + toFile);
        File tempfile = new File(toFile);
        if (!tempfile.exists()) {
            File tempdir = new File(tempfile.getParent());
            if (!tempdir.exists()) {
                tempdir.mkdir();
            }

            try {
                tempfile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            tempfile.delete();
        }

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static void MoveSdcardFile(String fromFile, String toFile) {
        L.d(TAG, "CopySdcardFile--->fromFile = " + fromFile + ", toFile = " + toFile);
        File tempfile = new File(toFile);
//        if (tempfile.exists())
//        {
//            tempfile.delete();
//        }
        File srcFile = new File(fromFile);
        boolean b = srcFile.renameTo(tempfile);
        L.e(TAG, "CopySdcardFile--->fromFile = " + b);

        return;
    }

    public static int mkdir(String _dir) {
        File tempfile = new File(_dir);
        if (tempfile.exists() == false) {
            tempfile.mkdir();
        }
        return 0;
    }

    public static String GetTempFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        try {
            File _temp = File.createTempFile("preview-", ".jpg");
            String _fileName = _temp.getName();
            return _fileName;
        } catch (Exception _ex) {

        }
        return null;
    }

    /**
     * 获取文件类型
     * （获取图片jpg、png等格式、文件word、xml等格式等）
     *
     * @param filePath 文件路径
     * @return
     */
    public static FileType getFileType(String filePath) {

        File file = new File(filePath);
        String name = file.getName();


        if (name.endsWith(".ppt") || name.endsWith(".pptx")) {
            return FileType.PPT;
        } else if (name.endsWith(".pdf")) {
            return FileType.PDF;
        } else if (name.endsWith(".xlsx") || name.endsWith(".xls")) {
            return FileType.XLSX;
        } else if (name.endsWith(".docx") || name.endsWith(".docx")) {//常见图片格式
            return FileType.DOCX;
        } else if (name.endsWith(".jpg")
                || name.endsWith(".png")
                || name.endsWith(".gif")
                || name.endsWith(".tif")
                || name.endsWith(".bmp")
                || name.endsWith(".svg")
                || name.endsWith(".tiff")
                || name.endsWith(".heic")) {
            return FileType.IMAGE;
        } else if (name.endsWith(".cda")
                || name.endsWith(".wav")
                || name.endsWith(".wma")
                || name.endsWith(".mp3")
                || name.endsWith(".mid")
                || name.endsWith(".ape")
                || name.endsWith(".flac")
                || name.endsWith(".aac")
                || name.endsWith(".ogg")) {//常见音频格式
            return FileType.AUDIO;
        } else if (name.endsWith(".asf")
                || name.endsWith(".avi")
                || name.endsWith(".dat")
                || name.endsWith(".mov")
                || name.endsWith(".mp4")
                || name.endsWith(".mpeg")
                || name.endsWith(".m4v")
                || name.endsWith(".qt")
                || name.endsWith(".flv")
                || name.endsWith(".wmv")
                || name.endsWith(".mkv")
                || name.endsWith(".rm")
                || name.endsWith(".rmvb")
                || name.endsWith(".vob")
                || name.endsWith(".ts")) {//常见音频格式
            return FileType.VIDEO;
        } else if (name.endsWith(".rar") || name.endsWith(".zip")) {
            return FileType.ZIP;
        } else if (name.endsWith(".txt")) {
            return FileType.TXT;
        } else if (name.endsWith(".lwf")) {
            return FileType.LWF;
        } else {
            return FileType.UN_KNOW_TYPE;
        }
    }

    public enum FileType {
        LWF,
        FOLDER,
        IMAGE,
        AUDIO,
        VIDEO,
        ZIP,
        PDF,
        DOCX,
        XLSX,
        PPT,
        TXT,
        DOC,
        PPTX,
        UN_KNOW_TYPE
    }
}
