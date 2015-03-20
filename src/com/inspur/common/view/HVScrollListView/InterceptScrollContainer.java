package com.inspur.common.view.HVScrollListView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * һ����ͼ�����ؼ�,��ֹ ���� ontouch�¼����ݸ����ӿؼ�
 * @author Administrator
 *
 */
public class InterceptScrollContainer extends LinearLayout {

	public InterceptScrollContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InterceptScrollContainer(Context context) {
		super(context);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		Log.i("InterceptScrollContainer","onInterceptTouchEvent");
		return true;
	}
}
