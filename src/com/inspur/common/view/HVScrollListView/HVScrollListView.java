package com.inspur.common.view.HVScrollListView;

import java.util.List;
import java.util.Map;

import com.inspur.common.utils.Constant;
import com.inspur.labelprint.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义控件，用于滚动过程中固定第一行及第一列
 * @author Administrator
 *
 */
public class HVScrollListView extends RelativeLayout {
	
	private Context context;
	private String[] arr_title;
	private String[] arr_key;
	private List<Map<String, Object>> listData;
	private RelativeLayout head;
	private ListView lv_data;
	
	
	private DataAdapter adapter;
	private boolean isShowSelect;
	private int i=0;//用来控制标题初始化
	
	public HVScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		csh(context);
	}

	public HVScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		csh(context);
	}

	public HVScrollListView(Context context) {
		super(context);
		csh(context);
	}
	
	private void csh(Context context){
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
		inflater.inflate(R.layout.hvscrolllistview_weight_main, this);  
		head=(RelativeLayout) findViewById(R.id.head);
		lv_data=(ListView) findViewById(R.id.lv_data);
		this.context=context;
		
//		RelativeLayout convertView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.activity_main, null);
//		this.head=(RelativeLayout) convertView.findViewById(R.id.head);
//		this.lv_data= (ListView) convertView.findViewById(R.id.lv_data);
//		this.context=context;
	}
	
	public void setContext(Context context){
		this.context=context;
	}
	/**
	 * 设置顶部标题
	 * @param arr_title
	 */
	public void setTitle(String[] arr_title){
		this.arr_title=arr_title;
	}
	/**
	 * 设置key
	 * @param arr_key
	 */
	public void setKey(String[] arr_key){
		this.arr_key=arr_key;
	}
	/**
	 * 设置要加载的数据
	 * @param listData
	 */
	public void setData(List<Map<String, Object>> listData){
		this.listData=listData;
	}
	/**
	 * 初始化控件
	 * @param context 
	 * @param isShowSelect 是否显示复选框（true 显示，false 隐藏）
	 */
	public void init(Context context,boolean isShowSelect){
		
		Log.i("HVListView", "init");
		
		this.isShowSelect=isShowSelect;
		
		head.setFocusable(true);
		head.setClickable(true);
		head.setBackgroundColor(Color.parseColor("#b2d235"));
		head.setOnTouchListener(new ListViewAndHeadViewTouchLinstener(head));
		
		lv_data.setOnTouchListener(new ListViewAndHeadViewTouchLinstener(head));
		
		//初始化title
		initTitle(head,isShowSelect);
		//创建适配器
		adapter=new DataAdapter(context, listData, arr_key, head,isShowSelect);
		//为ListView设置适配器
		lv_data.setAdapter(adapter);
	
	}
	/**
	 * 获得选中项对应的值
	 * @return
	 */
	public List<Map<String, Object>> getSelectItems(){
		
		
		if(isShowSelect){
			return adapter.getSelectItems();
		}
		return null;
	}
	
	/**
	 * 初始化title
	 */
	public void initTitle(RelativeLayout head,boolean isShowSelect){
		
		//i 用来控制标题初始化
		if(i==0){
			//初始化第一列
			LinearLayout ll_first_column =(LinearLayout) head.findViewById(R.id.ll_first_column);
			if(isShowSelect){
				CheckBox cb=(CheckBox) head.findViewById(R.id.cb_ischeck);
				cb.setVisibility(VISIBLE);
			}
			TextView tv_f_column=new TextView(context);
			tv_f_column.setLayoutParams(Constant.HVS_LPARAMS);
			tv_f_column.setText(arr_title[0]);
			tv_f_column.setTextSize(17);
			tv_f_column.setGravity(Gravity.CENTER_VERTICAL);
			ll_first_column.addView(tv_f_column);
			
			//创建title
			LinearLayout ll_contnet_head=(LinearLayout) head.findViewById(R.id.ll_contnet);	
			for(int i=1;i<arr_title.length;i++){
				TextView tv=new TextView(context);
				tv.setLayoutParams(Constant.HVS_LPARAMS);
				tv.setText(arr_title[i]);
				tv.setTextSize(17);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				ll_contnet_head.addView(tv);
			}		
		}
		i++;
	}
	
	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {
		
		private RelativeLayout head;

		public ListViewAndHeadViewTouchLinstener(RelativeLayout head) {
			super();
			this.head = head;
		}
		@Override
		public boolean onTouch(View arg0, MotionEvent e) {
			//当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
			HScrollView headSrcrollView =  (HScrollView) head.findViewById(R.id.horizontalScrollView1);
			headSrcrollView.onTouchEvent(e);			
			return false;
		}
	}


}
