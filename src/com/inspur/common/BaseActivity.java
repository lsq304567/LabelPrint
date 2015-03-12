package com.inspur.common;


import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.inspur.common.app.LabelPrintApplication;
import com.inspur.common.domain.RequestInfo;
import com.inspur.common.utils.Constant;
import com.inspur.common.utils.NetUtil;
import com.inspur.common.utils.ViewUtils;
import com.inspur.common.view.TipsToast;
import com.inspur.labelprint.MainActivity;
import com.inspur.labelprint.R;

import java.lang.reflect.Field;


public abstract class BaseActivity extends FragmentActivity  {
    public LabelPrintApplication application;
	protected ActionBar actionBar;
	private static TipsToast tipsToast;
	private Dialog dialog;
	protected boolean isBackHome;//是否返回home界面
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        application = (LabelPrintApplication)getApplication();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            actionBar = getActionBar();
            if(actionBar!=null){
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true); //4.x以上响应点击图标
                actionBar.setDisplayHomeAsUpEnabled(true);//设置显示箭头图标
            }
        }

		
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
		    if(menuKeyField != null) {
		        menuKeyField.setAccessible(true);
		        menuKeyField.setBoolean(config, false);
		    }
		} catch (Exception ex) {
		    // Ignore
		}
		
		init();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case android.R.id.home:
			if(isBackHome)
				backHome();//返回主界面
			else
				gotoLast();//返回上一层
			
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	// 初始化
	private void init() {
		loadViewLayout();
		findViewById();
		setListener();
		processLogic();
	}

	/**
	 * @author WangYanan
	 * @Description: TODO 返回主界面，可由子类覆写
	 */
	protected void backHome(){
		Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
        startActivity(intent);  
	}
	
	/**
	 * @author WangYanan
	 * @Description: TODO 返回上一个界面，可由子类覆写
	 */
	protected void gotoLast(){
		finish();
	}
	
	/**
	 * @author WangYanan
	 * @Description: TODO 设置布局
	 */
	protected abstract void loadViewLayout();

	/**
	 * @author WangYanan
	 * @Description: TODO 看名字也不用多说
	 */
	protected abstract void findViewById();

	/**
	 * @author WangYanan
	 * @Description: TODO 监听事件
	 */
	protected abstract void setListener();

    /**
     * @author johnny_kuang
     * @Des 初始化界面的页头元素
     */
   // protected abstract  void initMainTitle();


	/**
	 * @author WangYanan
	 * @Description: TODO 需要自动向后台请求数据,做查询功能的话可以使用
	 */
	protected abstract void processLogic();

	/**
	 * 自定义Toast WangYanan 2014-6-17下午2:48:07
	 * 
	 * @param iconResId
	 *            显示图标
	 * @param txtinfo
	 *            显示文本
	 */
	public void showTips(int iconResId, String txtinfo) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(getApplication().getBaseContext(),
					txtinfo, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(txtinfo);
	}

	/**
	 * 显示toast WangYanan 2014-6-17下午2:48:32
	 * 
	 * @param txtinfo
	 *            显示文本
	 */
	public void showTips(String txtinfo) {
		showTips(R.drawable.ic_launcher, txtinfo);
	}

	/**
	 * 
	 * @author WangYanan
	 * @Description: TODO 根据网络数据进行回调处理
	 * 
	 */
	public abstract interface DataCallback<T> {
		public abstract void processData(T paramObject); //请求成功，针对数据进行处理

		public abstract void getDataFailed();// 网络请求失败
	}

	/**
	 * @author WangYanan
	 * @Description: TODO 不用多说，耗时操作的最佳选择
	 */
	class BaseTask implements Runnable {
		private Context context;
		private RequestInfo reqInfo;
		private Handler handler;

		public BaseTask(Context context, RequestInfo reqInfo, Handler handler) {
			this.context = context;
			this.reqInfo = reqInfo;
			this.handler = handler;
		}

		public void run() {
			String result = null;
			Message msg = new Message();
			if (NetUtil.hasNetwork(context)) {
				if(RequestInfo.method.equalsIgnoreCase("GET"))
					result = NetUtil.get(reqInfo);// 真正和后台服务器交互的方法
				else	
					result = NetUtil.post(reqInfo);// 真正和后台服务器交互的方法
				msg.what = Constant.SUCCESS;
				msg.obj = result;
				handler.sendMessage(msg);
			} else {
				msg.what = Constant.FAILED;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}

	}

	/**
	 * 
	 * @author WangYanan
	 * @Description: TODO 配合TASK，轻松加愉快
	 */
	class BaseHandler extends Handler {
		private Context context;
		private DataCallback callBack;
		private RequestInfo reqInfo;

		public BaseHandler(Context context, DataCallback callBack,
				RequestInfo reqInfo) {
			this.context = context;
			this.callBack = callBack;
			this.reqInfo = reqInfo;
		}

		public void handleMessage(Message msg) {
			closeProgressDialog();
			if (msg.what == Constant.SUCCESS) {
				if (msg.obj == null) {
					Toast.makeText(context, R.string.data_null, Toast.LENGTH_SHORT).show();
//					showTips(R.drawable.ic_launcher,
//							getResources().getString(R.string.data_null));//返回数据为空。请求成功的情况下都会有数据返回，如果返回数据为空，多是人为导致
					callBack.getDataFailed();
				} else {
					callBack.processData(msg.obj);
				}
			} else if (msg.what == Constant.FAILED) {
				showTips(R.drawable.ic_launcher,
						getResources().getString(R.string.net_error));
				callBack.getDataFailed();
			}
		}
	}

	/**
	 * @author WangYanan
	 * @Description: TODO 网络请求
	 * 
	 * @param reqInfo
	 *            请求参数
	 * @param callBack
	 *            回调解析
	 * @param context
	 *            上下文，这里是为了防止dialog导致窗体泄露才传递，其实并没用到，由于有过先例，该参数先保留
	 */
	protected void getDataFromServer(RequestInfo reqInfo,
			DataCallback callBack, Context context) {
		
		BaseHandler handler = new BaseHandler(this, callBack, reqInfo);
		BaseTask task = new BaseTask(this, reqInfo, handler);
		showProgressDialog(reqInfo.context);
		new Thread(task).start();

	}

	/**
	 * @author WangYanan
	 * @Description: TODO show Dialog
	 */
	protected void showProgressDialog(Context context) {
		if (dialog == null)
			dialog = ViewUtils.createDialog(context,
					getString(R.string.LoadContent));

		dialog.show();

	}

	/**
	 * @author WangYanan
	 * @Description: TODO close Dialog
	 */
	protected void closeProgressDialog() {
		if (dialog != null && this.dialog.isShowing())
			this.dialog.dismiss();
		dialog = null;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

	}


}
