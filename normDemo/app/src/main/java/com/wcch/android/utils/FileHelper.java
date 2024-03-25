package com.wcch.android.utils;

import android.content.Context;

import com.bumptech.glide.load.Key;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHelper {
    public static final String COMMAND_LINE_END = "\n";
    public static final String ENTER = "\r\n";
    public static boolean creatOrWriteInitFile(Context context, File file, String str) {
        if (file.exists()) {
            return write(context, file, str);
        }
        creatFile(context, file);
        return write(context, file, str);
    }

    public static boolean creatFile(Context context, File file) {
        try {
            file.createNewFile();
            file.setReadable(true);
            file.setWritable(true);
            return file.canRead() && file.canWrite();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleFile(Context context, File file) {
        return file.delete();
    }

    public static boolean write(Context context, File file, String str) {
        if (!file.exists() && !creatFile(context, file)) {
            return false;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                fileOutputStream.write(new String(str).getBytes(Key.STRING_CHARSET_NAME));
                try {
                    fileOutputStream.close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                try {
                    fileOutputStream.close();
                    return false;
                } catch (IOException unused) {
                    e2.printStackTrace();
                    return false;
                }
            }
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
            return false;
        }
    }

    public static String read(Context context, File file) {
        StringBuilder sb = new StringBuilder();
        if (!file.exists() || !file.canRead()) {
            return sb.toString();
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Key.STRING_CHARSET_NAME));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return sb.toString();
                }
                sb.append(readLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return sb.toString();
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
            return sb.toString();
        } catch (IOException e3) {
            e3.printStackTrace();
            return sb.toString();
        }
    }

    public static String convertCodeAndGetText(Context context, File file) {
        BufferedReader bufferedReader;
        String str = "";
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bufferedInputStream.mark(4);
            byte[] bArr = new byte[3];
            bufferedInputStream.read(bArr);
            bufferedInputStream.reset();
            if (bArr[0] == -17 && bArr[1] == -69 && bArr[2] == -65) {
                bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "utf-8"));
            } else if (bArr[0] == -1 && bArr[1] == -2) {
                bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "unicode"));
            } else if (bArr[0] == -2 && bArr[1] == -1) {
                bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "utf-16be"));
            } else if (bArr[0] == -1 && bArr[1] == -1) {
                bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "utf-16le"));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "GBK"));
            }
            for (String readLine = bufferedReader.readLine(); readLine != null; readLine = bufferedReader.readLine()) {
                str = str + readLine + COMMAND_LINE_END;
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return str;
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

    public static void fileCopy(Context context, File file, File file2) throws Throwable
    {
        Throwable th;
        BufferedInputStream bufferedInputStream;
        BufferedOutputStream bufferedOutputStream;
        Exception e;
        BufferedInputStream bufferedInputStream2 = null;
        BufferedOutputStream bufferedOutputStream2 = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            try {
                BufferedOutputStream bufferedOutputStream3 = new BufferedOutputStream(new FileOutputStream(file2));
                try {
                    byte[] bArr = new byte[2048];
                    while (true) {
                        int read = bufferedInputStream.read(bArr);
                        if (read != -1) {
                            bufferedOutputStream3.write(bArr, 0, read);
                        } else {
                            try {
                                bufferedInputStream.close();
                                bufferedOutputStream3.close();
                                return;
                            } catch (IOException e2) {
                                e2.printStackTrace();
                                return;
                            }
                        }
                    }
                } catch (Exception e3) {
                    bufferedOutputStream = bufferedOutputStream3;
                    e = e3;
                    bufferedInputStream2 = bufferedInputStream;
                    try {
                        e.printStackTrace();
                        bufferedInputStream2.close();
                        bufferedOutputStream.close();
                    } catch (Throwable th2) {
                        th = th2;
                        bufferedInputStream = bufferedInputStream2;
                        bufferedOutputStream2 = bufferedOutputStream;
                        try {
                            bufferedInputStream.close();
                            bufferedOutputStream2.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    bufferedOutputStream2 = bufferedOutputStream3;
                    bufferedInputStream.close();
                    bufferedOutputStream2.close();
                    throw th;
                }
            } catch (Exception e5) {
                e = e5;
                bufferedOutputStream = null;
                bufferedInputStream2 = bufferedInputStream;
                e.printStackTrace();
                bufferedInputStream2.close();
                bufferedOutputStream.close();
            } catch (Throwable th4) {
                th = th4;
                bufferedInputStream.close();
                bufferedOutputStream2.close();
                throw th;
            }
        } catch (Exception e6) {
            e = e6;
            bufferedOutputStream = null;
            e.printStackTrace();
            bufferedInputStream2.close();
            bufferedOutputStream.close();
        } catch (Throwable th5) {
            th = th5;
            bufferedInputStream = null;
            bufferedInputStream.close();
            bufferedOutputStream2.close();
            throw th;
        }
    }

    public static void bugFileOutput(Context context, File file, String str) throws IOException
    {
        Throwable th;
        FileNotFoundException e;
        String format = new SimpleDateFormat(DateUtils.ymdhms).format(new Date());
        BufferedOutputStream bufferedOutputStream = null;
        try {
            BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(new FileOutputStream(file, true));
            try {
                bufferedOutputStream2.write(("\r\n\r\n新的异常---时间" + format + "---").getBytes());
                bufferedOutputStream2.write(str.getBytes());
                bufferedOutputStream2.write(ENTER.getBytes());
            } catch (IOException e2) {
                try {
                    e2.printStackTrace();
                }
                catch (Throwable th3) {
                    th = th3;
                    bufferedOutputStream2.close();
                    throw th;
                }
            }
            try {
                bufferedOutputStream2.close();
            } catch (IOException e5) {
                MyException.outputException(context, e5);
            }
        } catch (Throwable e6) {
            MyException.outputException(context, (Exception) e6);
            bufferedOutputStream.close();
        }
    }
}
