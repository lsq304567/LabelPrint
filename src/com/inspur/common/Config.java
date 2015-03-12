package com.inspur.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量类
 * @author Administrator
 *
 */
public class Config {
	/**
	 * 距离范围
	 */
	public static final int RANGE=1000;
	/**
	 * 起始页
	 */
	public static final int PAGE=1;
	/**
	 * 页容量
	 */
	public static final int PAGESIZE=10;
	/**
	 * 打印机返回状态值
	 */
	public static final Map<Integer,String> PRINTMESSAGE=new HashMap<Integer, String>(){
		/*-6:打印服务安装失败,-5:打印失败,-4:黑度设置数据发送失败,-3:蓝牙未开启,-2:黑度设置异常,-1:
			 * 连接打印机失败,0:打印机未配对,1:打印机连接成功,2:黑度设置成功,3:打印成功*/
		{
			put(-6,"打印服务安装失败");
	        put(-5,"打印失败");
	        put(-4,"黑度设置数据发送失败");
	        put(-3,"蓝牙未开启");
	        put(-2,"黑度设置异常");
	        put(-1,"连接打印机失败");
	        put(0,"打印成功");
	        put(1,"打印机连接成功");
	        put(2,"黑度设置成功");
	        put(3,"打印成功");
		}
	};
}
