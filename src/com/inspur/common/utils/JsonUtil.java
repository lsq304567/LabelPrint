package com.inspur.common.utils;

import android.util.Log;

import com.inspur.common.UserInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonUtil {

	/**
	 * 将json 数组转换为Map 对象
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Map<String, Object> getMap(String jsonString) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 把json 转换为 ArrayList 形式
	 * 
	 * @return
	 */
	public static List<Map<String, Object>> getList(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			JSONObject jsonObject;
			// list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				list.add(getMap(jsonObject.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Android发出HTTP请求，解析返回的JSON
	 */
	public static String getJsonObject(String url) {
		HttpClient client = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder();
		JSONArray jsonArray = null;
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			Log.i("json_str", builder.toString());
			jsonArray = new JSONArray(builder.toString());
			for (int i = 0; i < 2; ++i) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.i("id", jsonObject.getInt("id") + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArray.toString();
	}


	public static Object fromJsonToModel(JSONObject json, Class pojo)
			throws Exception {
		Field[] fields = pojo.getDeclaredFields();
		Object obj = pojo.newInstance();
		for (Field field : fields) {
			field.setAccessible(true);
			String name = field.getName();
			try {
				json.get(name);
			} catch (Exception ex) {
				continue;
			}
			if (json.get(name) != null && !"".equals(json.getString(name))) {
				if (field.getType().equals(Long.class)
						|| field.getType().equals(long.class)) {
					field.set(obj, Long.parseLong(json.getString(name)));
				} else if (field.getType().equals(String.class)) {
					field.set(obj, json.getString(name));
				} else if (field.getType().equals(Double.class)
						|| field.getType().equals(double.class)) {
					field.set(obj, Double.parseDouble(json.getString(name)));
				} else if (field.getType().equals(Integer.class)
						|| field.getType().equals(int.class)) {
					field.set(obj, Integer.parseInt(json.getString(name)));
				} else if (field.getType().equals(java.util.Date.class)) {
					field.set(obj, Date.parse(json.getString(name)));
				} else {
					continue;
				}
			}
		}
		return obj;
	}

	public static void main() throws Exception {
		String str = "{'access_time_a':'','access_time_z':'','accessdate':'1,1,1,1,1,1,1','authoritytree':{},'compname':'ʡ����','createman':2,'createmanaccount':'admin','createtime':'2013-04-08 15:02:55','destroytime':'','empid':101521,'empname':'���','groupid':0,'grpmember':[],'listfuncs':[],'listres':[],'locked':0,'managerlistname':'','password':'3696441120a402f793a704766540e69e','remark':'','unitname':'ʡ����-�������','updatetime':'2013-04-08 15:02:55','useraccount':'inspur','userfunc':[],'userid':52515,'userrole':[]}";
		JSONObject jsonObj = new JSONObject(str);
		UserInfo userInfo = (UserInfo) JsonUtil.fromJsonToModel(jsonObj,
				UserInfo.class);
		System.out.println(userInfo.getUseraccount());

	}
}
