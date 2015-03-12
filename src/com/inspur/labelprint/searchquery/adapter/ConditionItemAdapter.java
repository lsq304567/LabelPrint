package com.inspur.labelprint.searchquery.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.inspur.labelprint.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConditionItemAdapter extends BaseAdapter {
	
	private List<JSONObject> list;
	private Context cxt;
	
	public ConditionItemAdapter() {
		
	}
	public ConditionItemAdapter(List<JSONObject> list, Context cxt) {
		super();
		this.list = list;
		this.cxt = cxt;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView==null){
			
			convertView = LayoutInflater.from(cxt).inflate(R.layout.adapter_item_test, null);
			
			TextView tv_label=(TextView) convertView.findViewById(R.id.tv_label);
			
			try {
				tv_label.setText(list.get(position).getString("text"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}

		return convertView;
	}

}
