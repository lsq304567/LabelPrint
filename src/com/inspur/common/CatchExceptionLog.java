package com.inspur.common;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.Thread.UncaughtExceptionHandler;

public class CatchExceptionLog {

	
	/****
	 * 创建文件在应用的internal目录下 
	 * @param filename
	 * @param context
	 * @return
	 */
	public File makeFile(String filename, Context context) {
		return new File(context.getFilesDir(), filename);
	}

	
	/****
	 * 写文件到internal目录下
	 * @param filename
	 * @param content
	 * @param context
	 */
	public void writeFile(String filename, String content, Context context) {
		FileOutputStream outputStream;
		try {
			outputStream = context.openFileOutput(filename,	Context.MODE_PRIVATE);
			outputStream.write(content.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/****
	 * 缓存文件
	 * @param filename
	 * @param context
	 * @return
	 */
	public File getTempFile(String filename, Context context) {
		File file = null;
		try {
			file = File.createTempFile(filename, null, context.getCacheDir());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	
	
	/****
	 * 判断存储卡是否可读、写
	 * @return
	 */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	
	/****
	 * 判断存储卡是否可读
	 * @return
	 */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	
	/***
	 * 创建私有目录
	 * @param dirName
	 * @param context
	 * @return
	 */
	public static File makePrivateDir(String dirName, Context context) {
		File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),dirName);
		// 判断目录是否存在
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	
	/**
	 * 创建公共目录
	 * @param dirName
	 * @return
	 */
	public static File makePublicDir(String dirName) {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.MEDIA_NOFS),dirName);
		// 判断目录是否存在
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	
	/****
	 * 创建文件到SDCARD
	 * @param fileName
	 * @return
	 */
	public static File makeFile(String fileName) {
		String pathString = Environment.getExternalStorageDirectory().getPath()+ File.separator+"inspur_zsyw" +File.separator+ "exceptionlog";
		File dirFile = new File(pathString);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File logFile = new File(pathString + File.separator + fileName);
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logFile;
	}
	
	
	/****
	 * 捕获异常信息
	 */
	public static void catchException(final Context context){
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(){
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				Log.e("error", ex.getLocalizedMessage());
				try {
					if(isExternalStorageWritable()){
						//PrintWriter print=new PrintWriter(new FileWriter(makeFile("e.log"),true));
						
						FileOutputStream outstream = new FileOutputStream(makeFile("e.log"), true);
						OutputStreamWriter outStreamWriter = new OutputStreamWriter(outstream, "UTF-8");
						BufferedWriter writer = new BufferedWriter(outStreamWriter);
						
						XmlSerializer serializer = Xml.newSerializer();
						serializer.setOutput(writer);
						serializer.startDocument("UTF-8", true);
						
						//应用
						serializer.startTag("", "app");
						serializer.text("资源查查");//需要默认应用的名称使用的app_name
						serializer.endTag("", "app");
						//手机型号
						serializer.startTag("", "phonemodel");
						serializer.text(android.os.Build.MODEL);
						serializer.endTag("", "phonemodel");
						//安卓版本号
						serializer.startTag("", "version");
						serializer.text(android.os.Build.VERSION.RELEASE);
						serializer.endTag("", "version");
						//异常信息
						serializer.startTag("", "exception");
						serializer.text(ex.getLocalizedMessage());
						serializer.endTag("", "exception");
						
						serializer.endDocument();
						
						writer.write("\n");
						
						writer.flush();
						writer.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
