package com.inspur.common.utils;



import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.inspur.labelprint.R;

public class ViewUtils {

	public static Dialog createDialog(Context context, String msg) {

		View view = LayoutInflater.from(context).inflate(R.layout.loading_view,
				null);
		RelativeLayout layout = (RelativeLayout) view
				.findViewById(R.id.dialog_view);// 加载布局
		TextView tv_dialog_content = (TextView) view
				.findViewById(R.id.tv_dialog_content);// 提示文字
		/*
		 * // 加载动画 Animation hyperspaceJumpAnimation =
		 * AnimationUtils.loadAnimation( context, R.anim.load_animation); //
		 * 使用ImageView显示动画
		 * spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		 */
		tv_dialog_content.setText(msg);// 设置加载信息

		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		loadingDialog.setCancelable(false);// 返回键不取消
		loadingDialog.setContentView(layout, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));

		return loadingDialog;

	}

	/**
	 * 为后期做自定义toast做准备
	 * 
	 * @param context
	 * @param msg
	 *            显示内容
	 */
	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 设置总页数
	 * 
	 * @param tv_totalPage
	 *            总页数控件
	 * @param tv_pre
	 *            上一页
	 * @param tv_next
	 *            下一页
	 * @param totalPage
	 *            总页数
	 * @param currentPage
	 *            当前页
	 */
	public static void setContent(TextView tv_totalPage, TextView tv_pre,
			TextView tv_next, int totalPage, int currentPage) {
		if (totalPage == 1) {
			tv_totalPage.setText("第1/1页");
			tv_pre.setClickable(false);
			tv_next.setClickable(false);
		} else {
			tv_totalPage.setText("第" + (currentPage) + "/" + totalPage
					+ "页");
		}
	}


}
