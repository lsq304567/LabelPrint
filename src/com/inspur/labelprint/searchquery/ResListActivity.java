package com.inspur.labelprint.searchquery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.inspur.common.domain.RequestInfo;
import com.inspur.common.utils.NetUtil;
import com.inspur.common.utils.URLManager;
import com.inspur.common.view.XListView;
import com.inspur.common.view.XListView.IXListViewListener;
import com.inspur.labelprint.R;
import com.inspur.labelprint.searchquery.adapter.ConditionItemAdapter;
import com.inspur.labelprint.searchquery.adapter.ResListItemAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class ResListActivity extends Activity implements IXListViewListener{
	
	private Context cxt;
	private  Handler handler;
	private XListView lv_reslist;
	private boolean success;
	private int totalPages;
	private List<JSONArray> list;
	private String resclassname;
	private String conditionStr;
	
	private int page;
	private int pageSize;
	private SimpleDateFormat sdf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_res_list);
		
		Intent intent=getIntent();
		Bundle bundle = intent.getExtras();
		resclassname = bundle.getString("resclassname");
		conditionStr = bundle.getString("conditionStr");
		
		//初始化
		init();
		lv_reslist.setXListViewListener(this);
		//获取后台数据
		doQuery(resclassname, conditionStr, page, pageSize);

		handler = new Handler() {	
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					String jsonData=msg.getData().getString("data");	
					
					Log.e("ResListActivity of jsonData", jsonData);
					
					try {
						//数据解析
						parseData(jsonData);
						if(!success){
							Toast.makeText(cxt, "服务端异常", Toast.LENGTH_SHORT).show();
							finish();
							return;
						}
						onLoad();
						if(page>=totalPages){
							lv_reslist.setPullLoadEnable(false);
						} else {
							lv_reslist.setPullLoadEnable(true);
						}
						
						ResListItemAdapter adapter=new ResListItemAdapter(list, cxt);
						//数据改变时更新Listview
						adapter.notifyDataSetChanged();
						//绑定Adapter
						lv_reslist.setAdapter(adapter);
						
						lv_reslist.setPullLoadEnable(false);	
						
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
				else{
					Toast.makeText(cxt, "没有请求到数据", Toast.LENGTH_SHORT).show();  
				}
	        }

			
		};
	}
	/**
	 * 数据解析
	 * @param jsonData
	 * @throws JSONException
	 */
	public void parseData(String jsonData) throws JSONException{
		
		JSONObject obj=new JSONObject(jsonData);
		if(obj!=null){
			success=obj.getBoolean("success");
			if(success){
				String dataStr=obj.getString("data");
				totalPages=obj.getInt("totalPages");
				if(dataStr!=null && dataStr.length()>0){
//					JSONArray array=new JSONArray(dataStr);
//					
//					List<JSONObject> jz_list=new ArrayList<JSONObject>();
//					for(int i=0;i<array.length();i++){
//						JSONObject item=(JSONObject) array.get(i);				
//						jz_list.add(item);
					
					JSONObject object=new JSONObject(dataStr);
					if(object!=null){
						String dataArr=object.getString("list");
						if(dataArr!=null && dataArr.trim().length()>0){
							JSONArray array=new JSONArray(dataArr);
							List<JSONArray> jz_list=new ArrayList<JSONArray>();
							for(int i=0;i<array.length();i++){
								JSONArray item=(JSONArray) array.get(i);				
								jz_list.add(item);
							}
							list.addAll(0, jz_list);
						}	
					}
				}
			}
		}
	}
	private void onLoad() {
		Log.e("onLoad", "onLoad");
		lv_reslist.stopRefresh();
		lv_reslist.stopLoadMore();
		lv_reslist.setRefreshTime(sdf.format(new Date()));
	}
	//初始化
	public void init(){
		
		cxt=ResListActivity.this;
		page=1;
		pageSize=10;
		lv_reslist=(XListView) findViewById(R.id.lv_reslist);
		success=false;
		totalPages=0;
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		list=new ArrayList<JSONArray>();
	}
	
	/**
	 * 调用后台获取条件数据
	 * @throws JSONException 
	 */
	public void doQuery(String resclassname,String conditionStr,int page,int pageSize){

		final RequestInfo reqInfo=new RequestInfo();
		reqInfo.context=cxt;
		reqInfo.requestUrl=URLManager.URL+URLManager.PRINT_SEARCHRESDATA;
		//设置参数
		JSONObject param=new JSONObject();
		try {
			param.put("username", "root");
			param.put("resclassenname", resclassname);
			param.put("queryCondition", conditionStr);
			param.put("page", page);
			param.put("pageSize", pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		reqInfo.params=param.toString();
		
		new Thread() {
			@Override
			public void run() {
				String result=NetUtil.post(reqInfo);
				if (!TextUtils.isEmpty(result)) {
					// 发送数据的消息
		            Message msg = new Message();
		            Bundle data = new Bundle();
		            data.putString("data", result);
		            msg.setData(data);
		            msg.what = 1;
		            handler.sendMessage(msg);
				}
			}
		}.start();
	}
	@Override
	public void onRefresh() {
		page++;
		Log.e("page", page+"");
		Log.e("onRefresh", "onRefresh");
		doQuery(resclassname, conditionStr, page, pageSize);
	}
	@Override
	public void onLoadMore() {
		page++;
		Log.e("page", page+"");
		Log.e("onLoadMore", "onLoadMore");
		doQuery(resclassname, conditionStr, page, pageSize);
	}
}
