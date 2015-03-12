package com.inspur.common.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class MyEditText extends EditText {
    private final String TAG = "查询";
    private Drawable dRight;
    private Drawable dLeft;
    private Rect rBounds;

   // 构造器
    public MyEditText(Context paramContext) {
            super(paramContext);
            initEditText();
    }

   public MyEditText(Context paramContext, AttributeSet paramAttributeSet) {
            super(paramContext, paramAttributeSet);
            initEditText();
    }

   public MyEditText(Context paramContext, AttributeSet paramAttributeSet,
                    int paramInt) {
            super(paramContext, paramAttributeSet, paramInt);
            initEditText();
    }

   // 初始化edittext 控件
    private void initEditText() {
            setEditTextDrawable();
           // setTextColor(0xcccccc);
            addTextChangedListener(new TextWatcher() {        // 对文本内容改变进行监听
                    public void afterTextChanged(Editable paramEditable) {
                    }

                   public void beforeTextChanged(CharSequence paramCharSequence,
                                    int paramInt1, int paramInt2, int paramInt3) {
                    }

                   public void onTextChanged(CharSequence paramCharSequence,
                                    int paramInt1, int paramInt2, int paramInt3) {
//                	      int color = MyEditText.this.getResources().getColor(R.color.ButtonBack);
                	       MyEditText.this.setTextColor(Color.BLACK);
                            MyEditText.this.setEditTextDrawable();
                  }
            });
    }

   // 控制图片的显示
    private void setEditTextDrawable() {
            if (getText().toString().length() == 0){
                    setCompoundDrawables(this.dLeft, null, null, null);
            } else {
                    setCompoundDrawables(this.dLeft, null, this.dRight, null);
            }
    }

   protected void finalize() throws Throwable {
            super.finalize();
            this.dRight = null;
            this.rBounds = null;
    }

   // 添加触摸事件
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
            if ((this.dRight != null) && (paramMotionEvent.getAction() == 1)) {
                    this.rBounds = this.dRight.getBounds();
                    int i = (int) paramMotionEvent.getX();
                    if (i > getRight() - 3 * this.rBounds.width()) {
                            setText("");
                            paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
                    }
            }
            return super.onTouchEvent(paramMotionEvent);
    }

   // 设置显示的图片资源
    public void setCompoundDrawables(Drawable paramDrawable1,
                    Drawable paramDrawable2, Drawable paramDrawable3,
                    Drawable paramDrawable4) {
    	    if (paramDrawable1 != null)
    	    	   this.dLeft = paramDrawable1;
            if (paramDrawable3 != null)
                    this.dRight = paramDrawable3;
            super.setCompoundDrawables(paramDrawable1, paramDrawable2,
                            paramDrawable3, paramDrawable4);
    }
}
