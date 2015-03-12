package com.inspur.common.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class CustomDialog extends AlertDialog {

	private int resourceId = -1;
	
	private View contentView = null;
	public CustomDialog(Context context, int theme, int resId) {
		super(context, theme);
		resourceId = resId;
	}
	
	public CustomDialog(Context context, int theme, View view) {
		super(context, theme);
		contentView = view;
	}
	
	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see android.app.AlertDialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(contentView);
		if(resourceId > 0){
			setContentView(resourceId);
		}
		if(contentView != null){
			setContentView(contentView);
		}
	}
}