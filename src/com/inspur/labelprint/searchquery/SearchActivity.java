package com.inspur.labelprint.searchquery;

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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.inspur.common.UserInfo;
import com.inspur.common.app.LabelPrintApplication;
import com.inspur.common.domain.RequestInfo;
import com.inspur.common.utils.Constant;
import com.inspur.common.utils.JsonUtil;
import com.inspur.common.utils.NetUtil;
import com.inspur.common.utils.URLManager;
import com.inspur.common.utils.ViewUtils;
import com.inspur.labelprint.R;
import com.inspur.zsyw.platform.Platform;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class SearchActivity extends Activity  {


    // 登陆人信息
    private UserInfo userInfo;

    private LabelPrintApplication application;

	private Button search_button;
	private Spinner profess_spinner;
	private Spinner netype_spinner;
	private Context cxt;
	
	private List<JSONObject> list;
	private List professionList;
	private List<String[]> resourceList;
	private List<String[]> resclassnameList;
	private int f_item=0;
	private Dialog dialog;
	private  Handler handler;

    private Platform platform;

    private String packageName = "com.app.taskmanager";
	
    //test git
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		cxt = this;
        dialog=ViewUtils.createDialog(cxt, "数据加载中");
		//初始化
		init();	
		//后台查询数据
		//doQuery();
		handler = new Handler() {	
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					
					dialog.hide();
					
					JSONObject jsonObject=null;
					try {
						String resultData=msg.getData().getString("data");
						Log.e("SearchActivity of resultData", resultData);
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
					} catch (JSONException e1) {
						e1.printStackTrace();
						ViewUtils.showToast(cxt, "服务端返回json格式错误");
						return;
					}
					
					//Toast.makeText(cxt, "数据："+msg.getData().getString("data"), Toast.LENGTH_LONG).show();  	
					try {
						String jsonData=jsonObject.getString("data");
						//数据解析
						initData(jsonData);
						//设置专业分类
//						String[] cities={"成都","重庆","北京"};
						ArrayAdapter<String> p_adapter=new ArrayAdapter<String>(cxt, android.R.layout.simple_spinner_item,professionList);
						p_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						profess_spinner.setAdapter(p_adapter);	
						//下拉框事件
						profess_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0, View arg1,
									int arg2, long arg3) {
								f_item=arg2;
								setSpinnerValue(netype_spinner, f_item);
								//Toast.makeText(cxt, "你选择的是："+professionList.get(arg2), Toast.LENGTH_SHORT).show();
							}
							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub			
							}
						});
						
						if(f_item==0){
							setSpinnerValue(netype_spinner, f_item);
						}	
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
				else{
					ViewUtils.showToast(cxt, "错误的返回类型"); 
				}
	        }
		};
		
		//按钮事件
		search_button.setOnClickListener(searchClickListener);
		
		 
	}
	/**
	 * 设置Spinner的值
	 * @param spinner
	 * @param f_item
	 */
	public void setSpinnerValue(Spinner spinner,final int f_item){
		//设置资源类型
		ArrayAdapter<String> r_adapter=new ArrayAdapter(cxt, android.R.layout.simple_spinner_item,resourceList.get(f_item));
		r_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(r_adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//Toast.makeText(cxt, "你选择的是："+resourceList.get(f_item)[arg2], Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub		
			}
		});
	}


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 初始化,设置回调接口
        platform = Platform.getInstance(SearchActivity.this, callback);
        // 绑定服务
        platform.bindService();

        if(application.getUserInfo() != null){
            userInfo = application.getUserInfo();
            doQuery();
        }

    }

	//初始化
	public void init(){
        application = (LabelPrintApplication)this.getApplication();
		search_button = (Button) findViewById(R.id.search_button);
		profess_spinner = (Spinner) findViewById(R.id.profess_spinner);
		netype_spinner = (Spinner) findViewById(R.id.res_spinner);
		cxt=SearchActivity.this;


		list=new ArrayList();
		professionList=new ArrayList();
		resourceList=new ArrayList();
		resclassnameList=new ArrayList();
	}
	
	OnClickListener searchClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			int professchosseid = profess_spinner.getSelectedItemPosition();
			int netypechooseid = netype_spinner.getSelectedItemPosition();
			
//			Toast.makeText(cxt, "professchosseid:"+professchosseid+",netypechooseid:"+netypechooseid, Toast.LENGTH_SHORT).show();
			
			if(professchosseid < 0){
				Toast.makeText(cxt, "专业分类为空，无法查询！", Toast.LENGTH_SHORT).show();
			} else if(netypechooseid < 0){
				Toast.makeText(cxt, "资源类型为空，无法查询！", Toast.LENGTH_SHORT).show();
			} 
			else {
//				String professchosse_int_id = list.get(professchosseid).get("id").toString();
//				String netypechoose_int_id = list.get(professchosseid).get("id").toString();
//				
				
				String resclassname=resclassnameList.get(professchosseid)[netypechooseid];
				
				Intent intent = new Intent(cxt, SetConditionsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("resclassname", resclassname);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	};
	
	/**
	 * 调用后台获取条件数据
	 */
	public void doQuery(){
		
		dialog.show();
		final RequestInfo reqInfo=new RequestInfo();
		reqInfo.context=cxt;
		reqInfo.requestUrl=URLManager.URL+URLManager.PRINT_QUERYRESOURCENOTE;
		
		new Thread() {
			@Override
			public void run() {

				String result=NetUtil.post(reqInfo);
				//Log.e("result", result);
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
	 * 数据初始化
	 * @throws Exception 
	 */
	public void initData(String jsonData) throws Exception{
		
		if(jsonData==null || jsonData.length()<=0){
			return;
		}
		
		try {
			JSONArray jsonArray=new JSONArray(jsonData);
			
			if(jsonArray!=null){
				
				for(int i=0;i<jsonArray.length();i++){
					JSONObject jsonObj=(JSONObject) jsonArray.get(i);
					if(jsonObj!=null){
						list.add(jsonObj);
						professionList.add(jsonObj.get("text"));
						JSONArray jArray=jsonObj.getJSONArray("tagNotes");
						if(jArray!=null && jArray.length()>0){
							String[] item=new String[jArray.length()];
							String[] item1=new String[jArray.length()];
							for(int j=0;j<jArray.length();j++){
								JSONObject obj=(JSONObject) jArray.get(j);
								if(obj!=null){
									item[j]=obj.get("text").toString();
									item1[j]=obj.get("resclassname").toString();
								}
							}
							resourceList.add(item);
							resclassnameList.add(item1);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new Exception("数据解析异常");
		}
	}
    enum Action {
        USER, URL
    }
    private Action action;

    Platform.PlatformCallback callback = new Platform.PlatformCallback() {

        @Override
        public void onSuccess(String response) {
            if(action == Action.USER){
                Log.e("userinfo", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    userInfo = (UserInfo) JsonUtil.fromJsonToModel(jsonObject, UserInfo.class);
                    application.setUserInfo(userInfo);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                action = Action.URL;
                platform.getBaseUrl(packageName);
            } else if (action == Action.URL) {
                //如果URl最后带有“/”符号。则去除该符号
                if(response.lastIndexOf("/") == response.length() - 1){
                    response = response.substring(0, response.length() - 1);
                }
                //去除最后的上下文，获取外网地址
                URLManager.URL = response.substring(0, response.lastIndexOf("/"));
                application.setServiceUrl(URLManager.URL);
                Log.e("外网地址", URLManager.URL);



                doQuery();
            }
        }

        @Override
        public void onFailure(String arg0) {
            // TODO Auto-generated method stub
            handler_plat.sendEmptyMessage(Constant.SERVICES_BIND_FAILURE);
        }

        @Override
        public void onServiceConnected() {
            // TODO Auto-generated method stub
            action = Action.USER;
            platform.getUserInfo(packageName);
        }
    };
    private Handler handler_plat = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Constant.SERVICES_BIND_FAILURE:
                    ViewUtils.showToast(SearchActivity.this,"鉴权失败,需先登录掌上运维平台!");
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if (null != platform)
            platform.unbindService();// 注销服务

        dialog.dismiss();
        super.onPause();
    }

}
