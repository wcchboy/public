package com.igrs.sml.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

@SuppressWarnings("all")
public class Common {

	public static String replace(String mainString, String oldString,
			String newString) {
		if (mainString == null)
			return null;

		if (newString == null) {
			newString = "";
		}

		int i = mainString.lastIndexOf(oldString);
		if (i < 0)
			return mainString;
		StringBuffer mainSb = new StringBuffer(mainString);
		while (i >= 0) {
			mainSb.replace(i, i + oldString.length(), newString);
			i = mainString.lastIndexOf(oldString, i - 1);
		}
		return mainSb.toString();
	}

	public static int parseInt(String param) {
		int i = 0;
		try {
			i = Integer.parseInt(param);
		} catch (Exception e) {
			i = (int) parseFloat(param);
		}
		return i;
	}

	public static long parseLong(String param) {
		long l = 0;
		try {
			l = Long.parseLong(param);
		} catch (Exception e) {
			l = (long) parseDouble(param);
		}
		return l;
	}

	public static float parseFloat(String param) {
		float f = 0;
		try {
			f = Float.parseFloat(param);
		} catch (Exception e) {
			//
		}
		return f;
	}

	public static double parseDouble(String param) {
		double d = 0;
		try {
			d = Double.parseDouble(param);
		} catch (Exception e) {
			//
		}
		return d;
	}

	public static String randomStr(int length) {
		String[] strArray = { "2", "3", "4", "5", "6", "7", "8", "9", "a", "b",
				"c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q",
				"r", "s", "t", "u", "v", "w", "x", "y", "z" };
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < length; i++) {
			Random r = new Random();
			sb.append(strArray[r.nextInt(26)]);
		}
		return sb.toString();
	}

	public static List split(String str, String delim) {
		List<String> splitList = null;
		StringTokenizer st = null;

		if (str == null)
			return splitList;

		if (delim != null)
			st = new StringTokenizer(str, delim);
		else
			st = new StringTokenizer(str);

		if (st != null && st.hasMoreTokens()) {
			splitList = new ArrayList<String>();

			while (st.hasMoreTokens()) {
				System.out.println("Base64");
				splitList.add(st.nextToken());
			}
		}
		return splitList;
	}

	/**
	 * Escape HTML tags.
	 * 
	 * @param input
	 *            string to replace
	 * @return string
	 */
	public static String escapeHTMLTags(String input) {
		if (input == null || input.length() == 0)
			return input;
		StringBuffer buf = new StringBuffer();
		char ch = ' ';
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if (ch == '<')
				buf.append("&lt;");
			else if (ch == '>')
				buf.append("&gt;");
			else if (ch == '&')
				buf.append("&amp;");
			else if (ch == '"')
				buf.append("&quot;");
			else if (ch == '\'')
				buf.append("&apos;");
			else
				buf.append(ch);
		}
		return buf.toString();
	}

	public static String createBreaks(String input, int maxLength) {
		char chars[] = input.toCharArray();
		int len = chars.length;
		StringBuffer buf = new StringBuffer(len);
		int count = 0;
		int cur = 0;
		for (int i = 0; i < len; i++) {
			if (Character.isWhitespace(chars[i]))
				count = 0;
			if (count >= maxLength) {
				count = 0;
				buf.append(chars, cur, i - cur).append(" ");
				cur = i;
			}
			count++;
		}
		buf.append(chars, cur, len - cur);
		return buf.toString();
	}

	public static String getDateTime(String pattern, Date date) {
		String dt = null;
		if (date == null)
			return dt;

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		dt = sdf.format(date);
		return dt;
	}

	/**
	 * 计算指定日期的下一天
	 * 
	 * @param dateTime
	 *            日期，格式为：yyyy-MM-dd
	 * @return
	 */
	public static String getNextDay(String dateTime) {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = simpledate.parse(dateTime);
		} catch (ParseException ex) {
			System.out.println("日期格式不符合要求：" + ex.getMessage());
			return null;
		}
		now.setTime(date);
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);
		int day = now.get(Calendar.DAY_OF_MONTH) + 1;
		now.set(year, month, day);
		String time = simpledate.format(now.getTime());
		return time;
	}

	/**
	 * 计算指定日期的后几天
	 * 
	 * @param dateTime
	 *            日期，格式为：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getAfterDay(String dateTime, int n) {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat simpledate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = simpledate.parse(dateTime);
		} catch (ParseException ex) {
			System.out.println("日期格式不符合要求：" + ex.getMessage());
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, n);
		return simpledate.format(calendar.getTime());
	}

	/**
	 * 计算指定日期的后几天
	 * 
	 * @param dateTime
	 *            日期，格式为：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getAfterMinute(String dateTime, int n) {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat simpledate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = simpledate.parse(dateTime);
		} catch (ParseException ex) {
			System.out.println("日期格式不符合要求：" + ex.getMessage());
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, n);
		return simpledate.format(calendar.getTime());
	}

	/**
	 * 获取当前时间
	 * 
	 * @return Timestamp
	 */
	public static Timestamp getNowTime() {
		return new Timestamp(Calendar.getInstance().getTimeInMillis());
	}

	/**
	 * 获取系统时间 yyyy-MM-dd hh:mm:ss
	 * 
	 * @return String；
	 */
	public static String getSysDate() {
		System.out.println();
		return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date());
	}
	/**
	 * 获取系统时间 yyyy-MM-dd hh:mm:ss
	 *
	 * @return String；
	 */
	public static String dataTimeToString(long timestamp) {
		System.out.println();
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(timestamp));
	}
	/**
	 * 秒换算成 00:00:00格式;
	 * 
	 * @param second
	 *            秒
	 * @return 00:00:00；
	 */
	public static String convert(int second) {
		String hh, dd, ss;
		int h = 0, d = 0, s = 0;
		s = second % 60;
		second = second / 60;
		d = second % 60;
		h = second / 60;

		hh = h < 10 ? "0" + h : h + "";
		dd = d < 10 ? "0" + d : d + "";
		ss = s < 10 ? "0" + s : s + "";

		return hh + ":" + dd + ":" + ss;
	}

	/**
	 * 获取指定日期是星期几
	 * 
	 * @param thedate
	 *            格式：yyyy-MM-dd
	 * @return 星期
	 */
	public static String getWeek(String thedate) {
		String strReturn = "";
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStringToParse = thedate;
		try {
			Date date = bartDateFormat.parse(dateStringToParse);
			SimpleDateFormat bartDateFormat2 = new SimpleDateFormat("EEEE");
			strReturn = bartDateFormat2.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strReturn;
	}

	/**
	 * 获取指定日期是星期几
	 * 
	 * @param thedate
	 *            格式：yyyy-MM-dd
	 * @return 1
	 */
	public static String getDay(String thedate) {
		String strReturn = "";
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStringToParse = thedate;
		try {
			Date date = bartDateFormat.parse(dateStringToParse);
			strReturn = date.getDay() + "";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strReturn;
	}

	/**
	 * 是否在此时间段
	 * 
	 * @param startTime开始时间
	 * @param startTime开始时间
	 * @param endTime结束时间
	 * @return
	 */
	public static boolean timeIsBetween(Timestamp thisTime,
			Timestamp startTime, Timestamp endTime) {
		if (startTime.before(endTime)) {
			if (thisTime.before(endTime) && thisTime.after(startTime)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据生日获取年龄
	 * 
	 * @param birthday
	 * @return
	 */
	public static int getAgeByBirthday(String birthday) {
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = bartDateFormat.parse(birthday);
		} catch (ParseException e) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		if (cal.before(date)) {
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(date);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		int age = yearNow - yearBirth;
		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				age--;
			}
		}
		return age;
	}

	public static boolean isSafeStr(String s) {
		if (s.indexOf("%") != -1 || s.indexOf("\'") != -1
				|| s.indexOf("\"") != -1 || s.indexOf("&") != -1
				|| s.indexOf("#") != -1 || s.indexOf("*") != -1
				|| s.indexOf("/") != -1 || s.indexOf("\\") != -1
				|| s.indexOf("<") != -1 || s.indexOf(">") != -1
				|| s.indexOf("?") != -1 || s.indexOf("|") != -1)
			return false;
		return true;
	}

	/**
	 * 计算时间差
	 * 
	 * @param time
	 *            指定的时间，格式为：yyyy-MM-dd HH:mm:ss
	 * @return 当前时间和指定时间的时间差（秒）
	 */
	public static long getTimeDifference(String time) {
		long between = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String systemTime = sdf.format(new Date()).toString();

		Date end = null;
		Date begin = null;
		try {// 将截取到的时间字符串转化为时间格式的字符串
			end = sdf.parse(time);
			begin = sdf.parse(systemTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		between = Math.abs(end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒

		return between;
	}

	/**
	 * 计算时间差
	 * 
	 * @param time
	 *            指定的时间，格式为：yyyy-MM-dd HH:mm:ss
	 * @return 当前时间和指定时间的时间差（秒） 传入时间小于系统时间为负数
	 * 
	 */
	public static long getTimeDiff(String time) {
		long between = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String systemTime = sdf.format(new Date()).toString();
		Date end = null;
		Date begin = null;
		try {// 将截取到的时间字符串转化为时间格式的字符串
			end = sdf.parse(time);
			begin = sdf.parse(systemTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
		return between;
	}

	// 通过url和postdata获取返回值。该函数为主要函数可以copy使用返回值为一个Html页面的数据。
	public static String GetResponseDataByID(String url, String postData) {
		String data = null;
		try {
			URL dataUrl = new URL(url);
			HttpURLConnection con = (HttpURLConnection) dataUrl
					.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Proxy-Connection", "Keep-Alive");
			con.setDoOutput(true);
			con.setDoInput(true);

			OutputStream os = con.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.write(postData.getBytes());
			dos.flush();
			dos.close();

			InputStream is = con.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			byte d[] = new byte[dis.available()];
			dis.read(d);
			data = new String(d);
			// System.out.println(data);
			con.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

	public static String sendPost(String url, String parameters) {
		String result = "";// 返回的结果
		BufferedReader in = null;// 读取响应输入流
		PrintWriter out = null;
		StringBuffer sb = new StringBuffer();// 处理请求参数
		String params = parameters;// 编码之后的参数
		try {
			// // 编码请求参数
			// if(parameters.size() == 1){
			// for(PostValue p:parameters){
			// sb.append(p.getKey()).append("=").append(p.getValue());
			// }
			// params = sb.toString();
			// }else{
			// for(PostValue p:parameters){
			// sb.append(p.getKey()).append("=").append(p.getValue()).append("&");
			// }
			// String temp_params = sb.toString();
			// params = temp_params.substring(0, temp_params.length() - 1);
			// }
			// 创建URL对象
			java.net.URL connURL = new java.net.URL(url);
			// 打开URL连接
			java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL
					.openConnection();
			// 设置通用属性
			httpConn.setRequestProperty("Accept", "*/*");
			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
			httpConn.setRequestProperty("Accept-Charset", "gb2312");
			httpConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=gb2312");
			// 设置POST方式
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			// 获取HttpURLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.write(params);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应，设置编码方式
			in = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream(), "UTF-8"));
			String line;
			// 读取返回的内容
			while ((line = in.readLine()) != null) {
				result += line + "\r\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * MD5 加密
	 */
	public static String getCMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		String temp = "";
		String var = "";
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				if ((i + 1) % 4 == 0) {
					md5StrBuff.append("0"
							+ Integer.toHexString(0xFF & byteArray[i]) + temp);
					temp = "";
				} else {
					temp = "0" + Integer.toHexString(0xFF & byteArray[i])
							+ temp;
				}
			} else {
				if ((i + 1) % 4 == 0) {
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i])
							+ temp);
					temp = "";
				} else {
					temp = Integer.toHexString(0xFF & byteArray[i]) + temp;
				}
			}
		}
		return md5StrBuff.toString().toUpperCase();
	}

	/**
	 * 原MD5 加密
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		String temp = "";
		String var = "";
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				md5StrBuff.append("0"
						+ Integer.toHexString(0xFF & byteArray[i]));
			} else {
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
		return md5StrBuff.toString().toUpperCase();
	}

	public static String createFileName() {
		Date date = new Date();
		java.text.DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(date) + (int) (Math.random() * 10000);
	}

	public static String formatMoney(String num) {
		DecimalFormat df = new DecimalFormat();
		double data = Double.parseDouble(num);
		String style = "0.00";// 定义要显示的数字的格式
		df.applyPattern(style);// 将格式应用于格式化器
		return df.format(data);
	}

	/**
	 * 格式日期
	 * 
	 * @param dateStyle
	 *            yyyy-MM-dd HH:mm:ss
	 * @param dateValue
	 *            2012/6/26 14:56:53
	 * @return 2012-06-26 14:56:53
	 */
	public static String formatDate(String dateStyle, Object dateValue) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(dateStyle);
		return formatter.format(dateValue);
	}

	public static String strUtf8(String srcStr)
			throws UnsupportedEncodingException {
		return new String(srcStr.getBytes("ISO-8859-1"), "UTF-8");
	}


	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 *
	 * @param pxValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int pxtodip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 *
	 * @param dipValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int diptopx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}


	
	public static byte[] toBytes(int number){
		byte[] bytes = new byte[4];
		bytes[0] = (byte)number;
		bytes[1] = (byte) (number >> 8);
		bytes[2] = (byte) (number >> 16);
		bytes[3] = (byte) (number >> 24);
		return bytes;
	}
	public static int byteToint(byte b[], int offset) {
		int it[] = new int[4];
		byte tmpb = 0;
		String str="";
		for (int i = 0; i < 4; i++) {
			tmpb = b[i + offset];
            if (tmpb < 0)
                it[i] = 256 + tmpb;
            else
                it[i] = tmpb;

            str += tmpb + " ";
        }
        L.i(offset + " ->" + str);
        return it[0] + (it[1] << 8) + (it[2] << 16) + (it[3] << 24);
    }


    public static byte[] LongToBytes(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }

    public static long BytesToLong(byte[] buffer) {
        long values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8;
            values |= (buffer[i] & 0xff);
        }
        return values;
    }


    public static byte[] bitmap2RGB(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();  //返回可用于储存此位图像素的最小字节数

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //  使用allocate()静态方法创建字节缓冲区
        bitmap.copyPixelsToBuffer(buffer); // 将位图的像素复制到指定的缓冲区

        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];
		int count = rgba.length / 4;
		//Bitmap像素点的色彩通道排列顺序是RGBA
		for (int i = 0; i < count; i++) {
//            pixels[i * 3] = rgba[i * 4];        //R
//            pixels[i * 3 + 1] = rgba[i * 4 + 1];    //G
//            pixels[i * 3 + 2] = rgba[i * 4 + 2];       //B

			pixels[i * 3] = rgba[i * 4 + 2];        //R
			pixels[i * 3 + 1] = rgba[i * 4 + 1];    //G
			pixels[i * 3 + 2] = rgba[i * 4 ];       //B
		}
		buffer.clear();
		return pixels;
	}
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}
	/**
	 * byte数组中取int数值
	 *
	 * @param src
	 *            byte数组
	 * @return int数值
	 */
	public static int bytesToInt(byte[] src) {
		int value = (int) (((src[3] & 0xFF)<<24)
				| ((src[2] & 0xFF)<<16)
				| ((src[1] & 0xFF)<<8)
				| (src[0] & 0xFF));
		return value;
	}
	/**
	 * byte[]转int
	 *
	 * @param bytes 需要转换成int的数组
	 * @return int值
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (3 - i) * 8;
			value += (bytes[i] & 0xFF) << shift;
		}
		return value;
	}
	public static int toLittleEndian(int a) {
		return (((a & 0xFF) << 24) | (((a >> 8) & 0xFF) << 16) | (((a >> 16) & 0xFF) << 8) | ((a >> 24) & 0xFF));
	}

	public static byte[] YUV_420_888toNV21(Image image) {
		byte[] nv21;
		ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
		ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
		ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();
		int ySize = yBuffer.remaining();
		int uSize = uBuffer.remaining();
		int vSize = vBuffer.remaining();
		nv21 = new byte[ySize + uSize + vSize];
		//U and V are swapped
		yBuffer.get(nv21, 0, ySize);
		vBuffer.get(nv21, ySize, vSize);
		uBuffer.get(nv21, ySize + vSize, uSize);
		return nv21;
	}

	//scanRecords的格式转换
	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public final static short byteToShort(byte[] buf, boolean asc) {
		if (buf == null) {
			throw new IllegalArgumentException("byte array is null!");
		}
		if (buf.length > 2) {
			throw new IllegalArgumentException("byte array size > 2 !");
		}
		short r = 0;
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--) {
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		else
			for (int i = 0; i < buf.length; i++) {
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		return r;
	}

	/* @author suncat
	 * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
	 * @return
	 */
	public static final boolean ping(String ip) {
		String result = null;
		try {
			Process p = Runtime.getRuntime().exec("ping -c 1 -W 5 " + ip);// ping网址1次
			int status = p.waitFor();
			if (status == 0) {
				result = "success";
				return true;
			} else {
				result = "failed";
			}
		} catch (IOException e) {
			result = "IOException";
		} catch (InterruptedException e) {
			result = "InterruptedException";
		} finally {
			L.d("ping "+ip+" result = " + result);
		}
		return false;
	}



	public static void main1(String[] args) {
		System.out.println(Common.getMD5Str("790209"));
		// 39DC0AE1AB59BA4957E056BE3E880FF2
		// 39DC0AE1AB59BA4957E056BE3E880FF2

		// DB054CD9977ED47568F16A4ABEF457D2
	}
}
