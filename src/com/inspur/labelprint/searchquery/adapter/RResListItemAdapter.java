package com.inspur.labelprint.searchquery.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.inspur.common.utils.DimenUtils;
import com.inspur.common.view.hvlist.HvDynamicBean;
import com.inspur.common.view.hvlist.HvListAdapter;
import com.inspur.labelprint.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

public class RResListItemAdapter extends BaseAdapter {
	
	private Context cxt;
	private List<JSONArray> list;
	private String[] ctitle;
	
	private static LinearLayout ll ;
	private Map<Integer,Boolean> isCheckMap = new HashMap<Integer, Boolean>(); //维护选择项
	
	public RResListItemAdapter() {
		
	}
	public RResListItemAdapter(List<JSONArray> list,String[] ctitle, Context cxt) {
		super();
		this.list = list;
		this.cxt = cxt;
		this.ctitle=ctitle;
		initNoCheck(false);
	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		Log.e("position", position+"");
		if(convertView==null){
		
				//动态生成view
		    	ll = new LinearLayout(cxt);
		    	
//		    	LayoutParams ll_layoutParams=new LinearLayout.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT);
//		    	ll_layoutParams.leftMargin=DimenUtils.dip2px(cxt, 8);
//		    	ll_layoutParams.topMargin=DimenUtils.dip2px(cxt, 8);
//		    	ll_layoutParams.bottomMargin=DimenUtils.dip2px(cxt, 8);
//		    	ll_layoutParams.rightMargin=DimenUtils.dip2px(cxt, 5);	
//		    	ll.setLayoutParams(ll_layoutParams);
		    	
//		        ll.setLayoutParams(
//		        		new LinearLayout.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		        ll.setOrientation(LinearLayout.HORIZONTAL);
		        ll.setBackgroundDrawable(cxt.getResources().getDrawable(R.drawable.listview_item_pressed_style));
		     
		       
		        if(position==0){
		        	
		        	 for(int k=0;k<ctitle.length;k++){
			                String hTitle =ctitle[k];
			                TextView tv=new TextView(cxt);
			                tv.setLayoutParams(new LinearLayout.LayoutParams(150,
			                        LinearLayout.LayoutParams.WRAP_CONTENT));
			                tv.setMaxLines(1);
			                tv.setTextSize(16);
			                tv.setTextAppearance(cxt,R.style.text_title_style2);
			                tv.setText(hTitle);
		                    tv.setBackgroundColor(cxt.getResources().getColor(R.color.papayawhip));
		                    ll.addView(tv);
		        	 }
		        }
		        else{
		        	
		        	JSONArray array=list.get(position);
					if(array!=null && array.length()>0){
						//勾选按钮
						CheckBox cb =new CheckBox(cxt);
				        cb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
				                 ,LinearLayout.LayoutParams.WRAP_CONTENT));
				        cb.setButtonDrawable(R.drawable.checkbox_checked_style);
				        cb.setChecked(isCheckMap.get(position));
				        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				            @Override
				            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				                if(position==0){
				                    if(isCheckMap.get(0)){
				                        initNoCheck(false);
				                    }else{
				                        initNoCheck(true);
				                    }
				                    RResListItemAdapter.this.notifyDataSetChanged();
				                }else{
				                    isCheckMap.put(position,b);
				                }
		
				            }
				        });
				        ll.addView(cb); 
				        
				        for(int k=0;k<array.length();k++){
				        	//其他数据
		    		        TextView tv1=new TextView(cxt);
		    		    	LayoutParams tv1_layoutParams=new LinearLayout.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT);
		    		    	tv1_layoutParams.leftMargin=8;
		    		    	tv1_layoutParams.topMargin=8;
		    		    	tv1_layoutParams.bottomMargin=8;
		    		    	tv1_layoutParams.rightMargin=8;	
		    		    	tv1.setLayoutParams(tv1_layoutParams);
		    		        
//		    		        tv1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
//		    		        		,LinearLayout.LayoutParams.WRAP_CONTENT));
		    		        try {
		    		        	tv1.setText(list.get(position).getString(k));
		    				} catch (JSONException e) {
		    					e.printStackTrace();
		    				}
		    		        ll.addView(tv1);
				        }
					}

		        }
                convertView=ll;
					
		}
		return convertView;
        
    }
	/**
     * 默认所有项不选中
     */
    private void initNoCheck(boolean bool){
         for(int i=0;i< list.size();i++){
             isCheckMap.put(i,bool);
         }
    }
    /**
     * 获取选中的数据
     * @return
     */
    public List<JSONArray> getSelectedData(){
        List<JSONArray> result =new ArrayList<JSONArray>();
        for(int i=0;i<list.size();i++){
            if(isCheckMap.get(i)){
                result.add(list.get(i));
            }
        }
        return result;
    }
}
