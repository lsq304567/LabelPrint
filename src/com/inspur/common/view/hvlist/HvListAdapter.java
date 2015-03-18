package com.inspur.common.view.hvlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.TextSize;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.inspur.labelprint.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/12/18 0018.
 * 动态生成带表头和第一列为checkbox的可横向滑动的ListView
 */
@SuppressLint("ResourceAsColor")
public class HvListAdapter extends BaseAdapter{

    private static ListView.LayoutParams FILL_FILL_LAYOUTPARAMS =
            new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT,
                    ListView.LayoutParams.FILL_PARENT, 1);
    private static ListView.LayoutParams WAP_WAP_LAYOUTPARAMS =
            new ListView.LayoutParams(ListView.LayoutParams.WRAP_CONTENT,
                    ListView.LayoutParams.WRAP_CONTENT);
    private static LinearLayout ll ;
    private List<HvDynamicBean> mdata;   //数据源

    //titleData和methodData的数据需一一对应
    private String[] titleData ;  //为表头名称
    private String[] methodData ; //为对应的方法名

    private Map<Integer,Boolean> isCheckMap = new HashMap<Integer, Boolean>(); //维护选择项

    private Context ctx;

    public HvListAdapter(Context context,List<HvDynamicBean> data,String[] titleD,String[] methodD){
        this.ctx=context;
        this.mdata=data;
        this.titleData=titleD;
        this.methodData=methodD;
        initNoCheck(false);
    }


    /**
     * 默认所有项不选中
     */
    private void initNoCheck(boolean bool){
        isCheckMap.put(0,bool);
         for(int i=0;i< mdata.size();i++){
             isCheckMap.put(i+1,bool);
         }
    	/*for(int i=0;i< mdata.size();i++){
            isCheckMap.put(i,bool);
        }*/
    }

    @Override
    public int getCount() {
        return  mdata.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return mdata.get(i-1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        //动态生成view
        ll = new LinearLayout(ctx);
        ll.setLayoutParams(WAP_WAP_LAYOUTPARAMS);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.listview_item_pressed_style));
        
        ll.setGravity(Gravity.CENTER_VERTICAL);
        
        if(i>0){
        	if(i%2==0){
        		ll.setBackgroundColor(R.color.grey);
        	}
        }
        CheckBox cb =new CheckBox(ctx);
        cb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                 ,LinearLayout.LayoutParams.WRAP_CONTENT));
        cb.setButtonDrawable(R.drawable.checkbox_checked_style);
        cb.setChecked(isCheckMap.get(i));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(i==0){
                    if(isCheckMap.get(0)){
                        initNoCheck(false);
                    }else{
                        initNoCheck(true);
                    }
                    HvListAdapter.this.notifyDataSetChanged();
                }else{
                    isCheckMap.put(i,b);
                }

            }
        });


        ll.addView(cb);
        if(titleData!=null){
            int title_clu = 350;

            if(titleData.length==2){
                title_clu=450;
            }

            for(int k=0;k<titleData.length;k++){
                String hTitle =titleData[k];
                TextView tv=new TextView(ctx);
                tv.setLayoutParams(new LinearLayout.LayoutParams(300,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tv.setMaxLines(1);
                //tv.setGravity(View.al  View.TEXT_ALIGNMENT_CENTER);
                tv.setTextAppearance(ctx,R.style.text_title_style2);
                //名称列宽度放大
                if(k==1 || k==0){
                    tv.setLayoutParams(new LinearLayout.LayoutParams(title_clu,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                }
                
                tv.setGravity(Gravity.CENTER_VERTICAL);
            	tv.setHeight(100);
                if(i == 0){
                	tv.setTextSize(17);
                    tv.setText(hTitle);
//                    tv.setBackgroundColor(ctx.getResources().getColor(R.color.papayawhip));
                    ll.setBackgroundColor(R.color.yellow);
                }else{
                	tv.setSingleLine(false);  
                    tv.setTextSize(13);
                    tv.setMaxHeight(100);
                    tv.setEllipsize(TruncateAt.MARQUEE);
//                    tv.setMarqueeRepeatLimit(TextView.m);
                    tv.setText(reflectBean(mdata.get(i-1),methodData[k]));
                }


                ll.addView(tv);

            }
        }
        return ll;
    }
    public Map<Integer, Boolean> getCheckMap() {
        return this.isCheckMap;
    }

    /**
     * 获取选中的数据
     * @return
     */
    public List<HvDynamicBean> getSelectedData(){
    	
      //0 1 2 3 4 5 6
    	//0 1 2 3 4 5
        List<HvDynamicBean> result =new ArrayList<HvDynamicBean>();
        for(int i=1;i<mdata.size()+1;i++){
            if(isCheckMap.get(i)){
                result.add(mdata.get(i-1));
            }
        }
        return result;
    }
    public List<Integer> getSelectedItem(){
          List<Integer> result =new ArrayList<Integer>();
          for(int i=1;i<mdata.size()+1;i++){
              if(isCheckMap.get(i)){
                  result.add(i);
              }
          }
          return result;
      }
    
    

    public String reflectBean(HvDynamicBean item,String methodName){
        String result="";
        try {
            Class cls =  HvDynamicBean.class;
            Method method=cls.getMethod(methodName);
            result = (String)method.invoke(item);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
}
