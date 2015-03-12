package com.inspur.common.domain;

import android.content.Context;

import java.io.File;
import java.util.Map;

/**
 * @author WangYanan
 * @Description: TODO 请求参数
 * @creation date 2014-5-10 下午5:51:37
 */
public class RequestInfo {
	public String requestUrl; //请求地址
	public Context context; //上下文
	public String params; //请求参数
	public static String method = "POST";
	public Map<String, File> fileMap;
}
