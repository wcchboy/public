package com.wcch.android.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @fileName FileUtils.java
 * @description 文件工具类
 */
public class FileUtils {
	private static final String TAG = "FileUtils";
	/** 获取文件大小的单位B,KB,MB,GB */
	public static final int SIZE_TYPE_B = 1;
	public static final int SIZE_TYPE_KB = 2;
	public static final int SIZE_TYPE_MB = 3;
	public static final int SIZE_TYPE_GB = 4;
	private String SDPATH;
	/** The Constant BUFFER_SIZE. */
	private static final int BUFFER_SIZE = 4096;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		// 得到当前外部存储设备的目录
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * 在SD卡上创建文件
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	/**
	 * 判断SD上的文件是否存在
	 * 
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 在SD卡上创建目录
	 */
	public File getFilePath(String filePath, String fileName) {
		File file = null;
		mackRootDirectory(filePath);
		try {
			file = new File(filePath + fileName);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return file;
	}

	public void mackRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 将一个InputStream里面的数据写入到SD中
	 * 
	 * 例如把文件从FTP下载到本程序的/data/data/wlb/files/下面 次方法可以下载files文件夹下面指定文件的名称
	 * inputStream一旦输出就为空了。所以不要通过日志打印
	 * 
	 */

	public File write2SDFromInput(String path, String fileName,
			InputStream inputStream) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createNewFile(path + fileName);
			// long dow_start =
			// System.currentTimeMillis();//System.currentTimeMillis()返回以毫秒为单位的当前时间。
			output = new FileOutputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE];
			int length;
			while ((length = (inputStream.read(buffer))) > 0) {
				output.write(buffer, 0, length);
			}
			/*
			 * byte buffer[] = new byte[4*1024];
			 * while((inputStream.read(buffer))!=-1) { output.write(buffer); }
			 */

			/*
			 * int buffer =0; while((buffer=inputStream.read())!=-1) {
			 * output.write(buffer); }
			 */
			output.flush();

			/*
			 * InputStream is =new FileInputStream(file);
			 * 
			 * FileOutputStream fos = new FileOutputStream(path+fileName);
			 * byte[] buffers = new byte[8192]; int count = 0;
			 * 
			 * // 开始复制文件
			 * 
			 * while ((count = is.read(buffers)) > 0)
			 * 
			 * { fos.write(buffers, 0, count); } fos.close(); is.close();
			 */

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return file;
	}

	public void writeResponseData(String path, String fileName,
			InputStream inputStream) {
		if (inputStream == null) {
			return;
		}
		createSDDir(path);
		File file = createNewFile(path + fileName);
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(file);
			if (inputStream != null) {
				byte[] tmp = new byte[BUFFER_SIZE];
				int l, count = 0;
				while ((l = inputStream.read(tmp)) != -1
						&& !Thread.currentThread().isInterrupted()) {
					count += l;
					outStream.write(tmp, 0, l);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// 发送失败消息
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outStream != null) {
					outStream.flush();
					outStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 判断SD是否可以
	 * 
	 * @return
	 */
	public static boolean isSdcardExist() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
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
	 * 获取文件的Uri
	 * 
	 * @param path
	 *            文件的路径
	 * @return
	 */
	public static Uri getUriFromFile(String path) {
		File file = new File(path);
		return Uri.fromFile(file);
	}

	/**
	 * 换算文件大小
	 * 
	 * @param size
	 * @return
	 */
	public static String formatFileSize(long size) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "未知大小";
		if (size < 1024) {
			fileSizeString = df.format((double) size) + "B";
		} else if (size < 1048576) {
			fileSizeString = df.format((double) size / 1024) + "K";
		} else if (size < 1073741824) {
			fileSizeString = df.format((double) size / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) size / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 复制文件到其他目录
	 * 
	 * @param fileDir
	 * @param fileName
	 * @param newFilePath
	 */
	public void copyNewDatabase(String fileDir, String fileName,
			String newFilePath) {
		try {
			// 更新数据库
			// 获得file文件的绝对路径
			String filename = fileDir + fileName;
			File dir = new File(newFilePath);

			if (!dir.exists()) {
				dir.mkdir();
			}

			// 获得封装favoritedb.db文件的InputStream对象
			InputStream is = new FileInputStream(newFilePath + fileName);
			FileOutputStream fos = new FileOutputStream(filename);
			byte[] buffer = new byte[8192];
			int count = 0;
			// 开始复制favoritedb.db文件
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
			// 更新数据库成功
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * string -->inputStream
	 */
	public InputStream String2InputStream(String str) {
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		return stream;
	}

	/**
	 * InputStream-->String
	 * 
	 * @throws IOException
	 */
	public String InputStream2String(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	/**
	 * 慢但是节约资源
	 * 
	 * @param is
	 * @return
	 */
	public String InputStream2String2(InputStream is) {
		String all_content = null;
		try {
			all_content = "";
			InputStream ins = is;
			ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
			byte[] str_b = new byte[1024];
			int i = 1;
			while ((i = ins.read(str_b)) > 0) {
				outputstream.write(str_b, 0, i);
			}
			all_content = outputstream.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return all_content;
	}

	/**
	 * file-->InputStream
	 */
	public InputStream file2InputStream(File file) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return in;
	}

	/**
	 * file-->InputStream meizuo
	 */
	public InputStream file2InputStream(String path, File file) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return in;
	}

	/**
	 * InputStream-->file
	 */
	public File inputstream2File(InputStream inputStream) {
		File file = null;
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				os.close();
				inputStream.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * file-->InputStream data/data/name/files
	 */
	public String file2StringByData(Context context, String file) {
		String res = "";
		FileInputStream fileInputStream = null;
		try {
			// fileInputStream=context.openFileInput("name.xml");//只针对这个路径
			// data/data/name/files

			File fileName = new File(file);
			if (fileName.exists()) {
				fileInputStream = new FileInputStream(fileName);
				if (fileInputStream != null) {
					int length = fileInputStream.available();
					byte[] buffer = new byte[length];
					fileInputStream.read(buffer);
					res = EncodingUtils.getString(buffer, "UTF-8");
				}
				fileInputStream.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		return res;
	}

	/**
	 * file-->InputStream data/data/name/file
	 */
	public void writefile2StringByData(Context context, String fileName,
			String writestr) throws IOException {
		try {
			FileOutputStream fout = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			byte[] bytes = writestr.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
	}

	/**
	 * file-->InputStream
	 * 
	 */
	public String file2String(String file) {
		String res = "";
		FileInputStream fileInputStream = null;
		try {
			File fileName = new File(file);
			if (!fileName.isDirectory()) {
				fileInputStream = new FileInputStream(file);
				if (fileInputStream != null) {
					InputStreamReader inputStreamReader = new InputStreamReader(
							fileInputStream);
					BufferedReader buffreader = new BufferedReader(
							inputStreamReader);
					String line;
					while ((line = buffreader.readLine()) != null) {
						res += line + "\n";
					}
					fileInputStream.close();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		return res;
	}

	/**
	 * file-->InputStream data/data/name/files
	 */
	public void isFilePathExists(String file) {
		File filePath = null;
		try {
			filePath = new File(file);
			if (!filePath.exists()) {
				filePath.mkdir();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
	}

	/**
	 * 版本比较
	 * 
	 * @param service_version
	 * @param local_version
	 * @return
	 */

	public static boolean isNewVersion(String service_version,
			String local_version) {
		boolean flag = false;
		try {
			int mservice_version = Integer
					.parseInt(rmoveAllSpace(service_version));
			int mlocal_version = Integer.parseInt(rmoveAllSpace(local_version));
			if (mservice_version > mlocal_version) {

				flag = true;
				return flag;
			} else {
				// return flag;
				return flag;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("ExceptionLog", "(VersionInformation :ifNewVersion error");
			// return flag;
			return false;
		}
	}

	public static String rmoveAllSpace(String str) {
		String tmpstr = null;
		try {
			tmpstr = str.replace(".", "");
			// String tmpstr=str.replaceAll("[\\d]","");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmpstr;

	}

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @throws Exception
	 */
	public int unZipFile(File zipFile, String folderPath, String folderName)
			throws IOException {
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = zList.nextElement();
			if (ze.isDirectory()) {
				String dirstr = folderPath + folderName;
				dirstr = new String(dirstr.getBytes("8859_1"), StandardCharsets.UTF_8);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getRealFileName(folderPath, folderName)));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		Log.d("unZipFile", "unZip successful.");
		return 0;
	}

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @throws Exception
	 */
	public int unZipFile(File zipFile, String folderPath) throws
			IOException {
		mackRootDirectory(folderPath);
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = zList.nextElement();
			if (ze.isDirectory()) {
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), StandardCharsets.UTF_8);
				// Log.d("unZipFile", "str = "+dirstr);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			FileOutputStream outputStream = new FileOutputStream(folderPath
					+ ze.getName());
			OutputStream os = new BufferedOutputStream(outputStream);
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		Log.d("unZipFile", "unZip successful.");
		return 0;
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					// substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ret = new File(ret, substr);

			}
			Log.d("unZipFile", "1ret = " + ret);
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				// substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d("unZipFile", "substr = " + substr);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ret = new File(ret, substr);
			Log.d("unZipFile", "2ret = " + ret);
			return ret;
		}
		return ret;
	}

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @throws Exception
	 */
	public void unZipFileByZip(File zipFile, String folderPath)
			throws IOException {
		mackRootDirectory(folderPath);
		ZipFile mzipFile = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> en = mzipFile.entries();
		InputStream inputStream = null;
		while (en.hasMoreElements()) {
			ZipEntry zipEntry = en.nextElement();
			if (!zipEntry.isDirectory()) {
				inputStream = mzipFile.getInputStream(zipEntry);
				File f = new File(folderPath + zipEntry.getName());
				File file = f.getParentFile();
				file.mkdir();

				FileOutputStream outputStream = new FileOutputStream(folderPath
						+ zipEntry.getName());
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
				}
				outputStream.close();
			}
		}
	}

	public static boolean exists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	public static boolean removeFile(String filePath) {
		return new File(filePath).delete();
	}

	public static boolean createFile(String filePath) throws IOException {
		return create(new File(filePath));
	}

	/**
	 * 创建文件，包括必要的父目录的创建，如果未创建
	 * 
	 * @param file
	 *            待创建的文件
	 * @return 返回操作结果
	 * @throws IOException
	 *             创建失败，将抛出该异常
	 */
	public static boolean create(File file) throws IOException {
		if (file.exists()) {
			return true;
		}

		File parent = file.getParentFile();
		if (parent == null || !parent.exists()) {
			boolean flag = parent.mkdirs();
			if (!flag) {

			}
		}
		return file.createNewFile();
	}

	public static String fileUriToFilePath(Context context, Uri uri) {
		if (context == null || uri == null) {
			return null;
		}

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
			return getPath(context, uri);
		} else {
			return getRealPathFromURI(context, uri);
		}
	}

	private static String getRealPathFromURI(Context context, Uri contentUri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj,
				null, null, null);
		if (null != cursor && cursor.moveToFirst()) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
			cursor.close();
		}
		return res;
	}

	private static List<String> getExtSDCardPath() {
		List<String> result = new ArrayList<String>();
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("mnt")) {
					String[] arr = line.split(" ");
					String path = arr[1];
					File file = new File(path);
					if (file.isDirectory()) {
						result.add(path);
					}
				}
			}
			isr.close();
		} catch (Exception e) {

		}
		return result;
	}

	@SuppressLint("NewApi")
	private static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/" + split[1];
				} else {
					if (externalMemoryAvailable()) {
						return "/storage/" + split[0] + "/" + split[1];
					}
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	private static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * 通过url获取文件名
	 * 
	 * @param url
	 * @return 文件字符串
	 */
	public static String generateFileNameByUrl(String url) {
		byte[] md5 = getMD5(url.getBytes());
		BigInteger bi = new BigInteger(md5).abs();
		return bi.toString(36);
	}

	private static byte[] getMD5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
		}
		return hash;
	}

	/**
	 * 删除文件或者文件夹
	 * 
	 * @File file
	 * 
	 */
	public static void delAllFile(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		String path = file.getAbsolutePath();
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
	 * 获取文件长度
	 * 
	 * @param filePath
	 * @return
	 */
	public long getFileOrFilesSize(String filePath) {
		return getFileOrFilesSize(new File(filePath));
	}

	/**
	 * 获取文件长度，指定单位
	 * 
	 * @param filePath
	 * @param sizeType
	 *            SIZE_TYPE_B,SIZE_TYPE_KB,SIZE_TYPE_MB,SIZE_TYPE_GB
	 * @return
	 */
	public static double getFileOrFilesSize(String filePath, int sizeType) {
		return formatSize(getFileOrFilesSize(new File(filePath)), sizeType);
	}

	/**
	 * 获取文件长度，指定单位
	 * 
	 * @param file
	 * @param sizeType
	 *            SIZE_TYPE_B,SIZE_TYPE_KB,SIZE_TYPE_MB,SIZE_TYPE_GB
	 * @return
	 */
	public static double getFileOrFilesSize(File file, int sizeType) {
		return formatSize(getFileOrFilesSize(file), sizeType);
	}

	/**
	 * 获取文件大小
	 * 
	 * @param file
	 * @return
	 */
	public static long getFileOrFilesSize(File file) {
		long size = 0;
		if (!file.exists()) {
			return size;
		}
		try {
			if (file.isFile()) {
				size = getFileSize(file);
			}
			if (file.isDirectory()) {
				size = getFilesSize(file);
			}
		} catch (Exception e) {
			Log.e("getFileOrFilesSize", "faile!");
		}
		return size;
	}

	/**
	 * 获取单个文件大小
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			Log.e("getFileSize", "faile! not exists!");
		}
		return size;
	}

	/**
	 * 获取文件夹的大小
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static long getFilesSize(File file) throws Exception {
		long size = 0;
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				size += getFileSize(f);
			} else if (f.isDirectory()) {
				size += getFilesSize(f);
			}
		}
		return size;
	}

	/**
	 * 格式化size
	 * 
	 * @param size
	 * @param sizeType
	 *            SIZE_TYPE_B,SIZE_TYPE_KB,SIZE_TYPE_MB,SIZE_TYPE_GB
	 * @return 格式后的大小
	 */
	public static double formatSize(long size, int sizeType) {

		DecimalFormat df = new DecimalFormat("#.00");
		double sizeDouble = 0;
		switch (sizeType) {
		case SIZE_TYPE_B:
			sizeDouble = Double.valueOf(df.format((double) size));
			break;
		case SIZE_TYPE_KB:
			sizeDouble = Double.valueOf(df.format(((double) size) / 1024));
			break;
		case SIZE_TYPE_MB:
			sizeDouble = Double.valueOf(df
					.format(((double) size) / 1024 / 1024));
			break;
		case SIZE_TYPE_GB:
			sizeDouble = Double.valueOf(df
					.format(((double) size) / 1024 / 1024 / 1024));
			break;
		default:
			break;
		}
		return sizeDouble;
	}

	/**
	 * sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean externalMemoryAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 内部存储可用空间
	 * 
	 * @return
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getAbsolutePath());
		long availableBlocks = stat.getAvailableBlocks();
		long blockSize = stat.getBlockSize();
		return availableBlocks * blockSize;
	}

	/**
	 * 内部存储总空间
	 * 
	 * @return
	 */
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getAbsolutePath());
		long blockCount = stat.getBlockCount();
		long blockSize = stat.getBlockSize();
		return blockCount * blockSize;
	}

	/**
	 * 外部存储总空间
	 * 
	 * @return
	 */
	public static long getTotalSDCardSize() {
		if (externalMemoryAvailable()) {
			StatFs statFs = new StatFs(Environment
					.getExternalStorageDirectory().getAbsolutePath());
			int blockCount = statFs.getBlockCount();
			int blockSize = statFs.getBlockSize();
			return blockCount * blockSize;
		} else {
			return 0;
		}
	}

	/**
	 * 外部存储可用空间
	 * 
	 * @return
	 */
	public static long getAvailableSDCardSize() {
		if (externalMemoryAvailable()) {
			StatFs statFs = new StatFs(Environment
					.getExternalStorageDirectory().getAbsolutePath());
			int availableBlocks = statFs.getAvailableBlocks();
			int blockSize = statFs.getBlockSize();
			return availableBlocks * blockSize;
		} else {
			return 0;
		}
	}

	/**
	 * 从Uri获取本地路径
	 * @param context
	 * @param contentUri
	 * @return
	 */
	public static String getAbsoluteImagePath(Context context, Uri contentUri) {
		System.out.println(contentUri);
		String[] projection = {MediaStore.Images.Media.DATA};
		String urlpath;
		//如果是 content:// 路径
		CursorLoader loader = new CursorLoader(context, contentUri, projection, null, null, null);
		Cursor cursor = loader.loadInBackground();
		try {
			int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			urlpath = cursor.getString(column_index);
			return urlpath;			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		//如果是 file://
		urlpath = contentUri.getPath();
		return urlpath;
	}

	/**
	 * 改变文件类型，如.jpg -> .png
	 * @param srcPath	源文件路径
	 * @param distPath	目标类型文件路径
	 * @return	改变后的文件路径
	 */
	public static String changeFileType(String srcPath, String distPath) {
		String srcType = null, distType = null;
		String reg = "\\.\\w+$";
		Pattern pat = Pattern.compile(reg);
		Matcher mat1 = pat.matcher(srcPath);
		if (mat1.find()) {
			srcType = srcPath.substring(mat1.start(), mat1.end());
		}
		Matcher mat2 = pat.matcher(distPath);
		if (mat2.find()) {
			distType = distPath.substring(mat2.start(), mat2.end());
		}
		if (srcType != null && srcType == distType) {
			return srcPath;
		} else {
			return mat1.replaceFirst(distType);
		}
	}

	public static void unZipTest(){

		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/upgrade.zip";
		decompressFile(path);
	}

	public static void decompressFile(String source) {
		final String MAC_IGNORE = "__MACOSX/";
		String target = Environment.getExternalStorageDirectory().getAbsolutePath();
		try {
			File file = new File(source);
			if(!file.exists()) {
				return;
			}
			ZipFile zipFile = new ZipFile(file);
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
			ZipEntry zipEntry = null;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				String fileName = zipEntry.getName();
				if(fileName != null && fileName.contains(MAC_IGNORE)) {
					continue;
				}
				File temp = new File(target + File.separator + fileName);
				if(zipEntry.isDirectory()) {
					File dir = new File(target + File.separator + fileName);
					dir.mkdirs();
					continue;
				}
				if (temp.getParentFile() != null && !temp.getParentFile().exists()) {
					temp.getParentFile().mkdirs();
				}
				byte[] buffer = new byte[1024];
				OutputStream os = new FileOutputStream(temp);
				// 通过ZipFile的getInputStream方法拿到具体的ZipEntry的输入流
				InputStream is = zipFile.getInputStream(zipEntry);
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					os.write(buffer, 0, len);
				}
				os.close();
				is.close();
			}
			zipInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
