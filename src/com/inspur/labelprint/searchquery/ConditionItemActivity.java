package com.inspur.labelprint.searchquery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;

import com.inspur.common.domain.RequestInfo;
import com.inspur.common.utils.NetUtil;
import com.inspur.common.utils.URLManager;
import com.inspur.common.utils.ViewUtils;
import com.inspur.common.view.MyEditText;
import com.inspur.common.view.XListView;
import com.inspur.common.view.XListView.IXListViewListener;
import com.inspur.labelprint.R;
import com.inspur.labelprint.searchquery.adapter.ConditionItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint("NewApi")
public class ConditionItemActivity extends Activity implements IXListViewListener{
	
	private XListView lv_condition;
	private Context cxt;
	
	private MyEditText search_met;
	private Button btn_confirm_con;
//	private List<String> valueList;
//	private List<String> textList;
	private List<JSONObject> list;
	
	private int page;
	private int pageSize;
	private boolean success;
	private String resclassname;
	private String attributeenname;
	private String conditionValue;
	private int item;
	private int totalPages;
	private Dialog dialog;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_condition_item);
		
//		bundle.putString("resclassname", s_resclassname);
//		bundle.putString("conditionName",s_conditionName);
		
		Intent intent=getIntent();
		Bundle bundle = intent.getExtras();
		resclassname = bundle.getString("resclassname");
		attributeenname = bundle.getString("attributeenname");
		item=bundle.getInt("item");
		//初始化
		init();	
		conditionValue="";
		lv_condition.setXListViewListener(this);
		//后台查询数据
		doQuery(page, pageSize, resclassname,attributeenname,conditionValue);
		//默认隐藏虚拟键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		//设置点击事件
		btn_confirm_con.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
//				valueList.clear();
//				textList.clear();
				list.clear();
				page=1;
				conditionValue=search_met.getText().toString();
				Log.e("conditionValue", conditionValue);
				doQuery(page, pageSize, resclassname, attributeenname, conditionValue);
			}
		});
	}
	private  Handler handler = new Handler() {	
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				
				dialog.hide();
				JSONObject jsonObject=null;
				try {
					String resultData=msg.getData().getString("data");
					Log.e("ConditionItemActivity of resultData", resultData);
					jsonObject=new JSONObject(resultData);
				} catch (JSONException e1) {
					e1.printStackTrace();
					ViewUtils.showToast(cxt, "json解析异常");
					return;
				}
				
				try {
					boolean success = jsonObject.getBoolean("success");
					if(!success){
						ViewUtils.showToast(cxt, "服务端查询异常");
						return;
					}
					totalPages=jsonObject.getInt("totalPages");
				} catch (JSONException e1) {
					e1.printStackTrace();
					ViewUtils.showToast(cxt, "服务端返回json格式错误");
					return;
				}
				//Toast.makeText(cxt, "数据："+msg.getData().getString("data"), Toast.LENGTH_LONG).show();  
				
				try {
					
					String jsonData=jsonObject.getString("data");	
					//数据解析
					parseData(jsonData);

					onLoad();
					if(page>=totalPages){
						lv_condition.setPullLoadEnable(false);
					} else {
						lv_condition.setPullLoadEnable(true);
					}
					
					ConditionItemAdapter adapter=new ConditionItemAdapter(list, cxt);
					
//					ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(cxt, android.R.layout.simple_list_item_1,textList);
//					//数据改变时更新Listview
//					arrayAdapter.notifyDataSetChanged();
//					//绑定Adapter
//					lv_condition.setAdapter(arrayAdapter);
					
					//数据改变时更新Listview
					adapter.notifyDataSetChanged();
					//绑定Adapter
					lv_condition.setAdapter(adapter);
					
					lv_condition.setPullLoadEnable(false);	
					//选择事件
					lv_condition.setOnItemClickListener(new AdapterView.OnItemClickListener() {  	  
			            @Override  
			            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
			                    long arg3) {  

			                Intent intent = new Intent();
			    			Bundle bundle = new Bundle();
							bundle.putString("itemData",list.get(arg2-1).toString());
							bundle.putInt("item", item);
							bundle.putString("attributeenname",attributeenname);
			    			intent.putExtras(bundle);	                
			                setResult(100,intent);
			                finish();
			            }  
			        }); 
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
			else{
				ViewUtils.showToast(cxt, "错误的返回类型"); 
			}
        }
	};
	/**
	 * 初始化
	 */
	public void init(){
		
		page=1;
		pageSize=10;
		success=false;
		totalPages=0;
		cxt=ConditionItemActivity.this;
//		search_met=(EditText) this.findViewById(R.id.search_met);
		search_met=(MyEditText) this.findViewById(R.id.search_met);
		lv_condition=(XListView) this.findViewById(R.id.lv_condition);
		btn_confirm_con=(Button) this.findViewById(R.id.btn_confirm_con);
//		valueList=new ArrayList<String>();
//		textList=new ArrayList<String>();
		list=new ArrayList<JSONObject>();
		conditionValue=search_met.getText().toString();
		dialog=ViewUtils.createDialog(cxt, "数据查询中");
	}
	/**
	 * 调用后台获取条件数据
	 */
	public void doQuery(
			int page,
			int pageSize,
			String resclassname,
			String attributeenname,
			String conditionValue){
		
		dialog.show();
		final RequestInfo reqInfo=new RequestInfo();
		reqInfo.context=cxt;
		reqInfo.requestUrl=URLManager.URL+URLManager.PRINT_GETSELECTVALUE;
		reqInfo.params="{page:"+page
				+",pageSize:"+pageSize
				+",resclass:'"+resclassname
				+"',attributeenname:'"+attributeenname
				+"',conditionValue:'"+conditionValue
				+"'}";
		
		new Thread() {
			@Override
			public void run() {

				String result=NetUtil.post(reqInfo);
				Log.e("result", result);
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
	/**
	 * 数据解析
	 * @param jsonData
	 * @throws JSONException
	 */
	public void parseData(String jsonData) throws JSONException{
		if(jsonData==null || jsonData.length()<=0){
			return;
		}
		JSONArray array=new JSONArray(jsonData);
		if(array!=null && array.length()>0){
			List<JSONObject> jz_list=new ArrayList<JSONObject>();
			for(int i=0;i<array.length();i++){
				JSONObject item=(JSONObject) array.get(i);	
				jz_list.add(item);
			}
			list.addAll(0, jz_list);
		}	
	}
	private void onLoad() {
		Log.e("onLoad", "onLoad");
		lv_condition.stopRefresh();
		lv_condition.stopLoadMore();
		lv_condition.setRefreshTime(sdf.format(new Date()));
	}
	@Override
	public void onRefresh() {	
		page++;
		Log.e("page", page+"");
		Log.e("onRefresh", "onRefresh");
		doQuery(page, pageSize, resclassname, attributeenname, conditionValue);
	}
	@Override
	public void onLoadMore() {
		page++;
		Log.e("page", page+"");
		Log.e("onLoadMore", "onLoadMore");
		doQuery(page, pageSize, resclassname, attributeenname, conditionValue);
	}

    @Override
    protected void onPause() {
        dialog.dismiss();
        super.onPause();
    }
}
