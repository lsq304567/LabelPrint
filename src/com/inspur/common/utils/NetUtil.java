package com.inspur.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.inspur.common.domain.RequestInfo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.inspur.labelprint.R;

public class NetUtil {
	
	public static String post(RequestInfo reqInfo) {
		DefaultHttpClient httpclient = null;
		try {

			httpclient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost(reqInfo.requestUrl);
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
			HttpConnectionParams.setSoTimeout(httpParams, 80000);
			httpost.setParams(httpParams);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("param", reqInfo.params));
			
			if(reqInfo.fileMap == null){
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				httpost.setEntity(entity);
				HttpResponse response = httpclient.execute(httpost);
				if (response.getStatusLine().getStatusCode() == 200) {
					String result = EntityUtils.toString(response.getEntity(),
							HTTP.UTF_8);
					if (entity != null) {
						entity.consumeContent();
					}
					return result;
				}
				
			} else {
				
				Set<String> set = reqInfo.fileMap.keySet();
				MultipartEntity entity = new MultipartEntity();
				for(String fileKey : set){
					entity.addPart(fileKey, new FileBody(reqInfo.fileMap.get(fileKey)));
				}
				entity.addPart("param", new StringBody(reqInfo.params, Charset.forName("UTF-8")));
				httpost.setEntity(entity);
				HttpResponse response = httpclient.execute(httpost);
				if (response.getStatusLine().getStatusCode() == 200) {
					String result = EntityUtils.toString(response.getEntity(),
							HTTP.UTF_8);
					if (entity != null) {
						entity.consumeContent();
					}
					return result;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != httpclient)
				httpclient.getConnectionManager().shutdown();
		}

		return null;
	}
	

	public static String get(RequestInfo reqInfo) {
		String params =reqInfo.params;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(reqInfo.requestUrl + params);
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 8000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		get.setParams(httpParams);
		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != client)
				client.getConnectionManager().shutdown();
		}
		return null;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean isConnect(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = con.getActiveNetworkInfo();
		if (workinfo == null || !workinfo.isAvailable()) {
			return false;
		}
		return true;
	}
}
