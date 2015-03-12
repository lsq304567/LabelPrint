package com.inspur.labelprint.searchquery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.inspur.common.domain.RequestInfo;
import com.inspur.common.utils.DimenUtils;
import com.inspur.common.utils.NetUtil;
import com.inspur.common.utils.URLManager;
import com.inspur.common.utils.ViewUtils;
import com.inspur.labelprint.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SetConditionsActivity extends Activity{

    private static LayoutParams FILL_FILL_LAYOUTPARAMS =new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, 1);
    private static LayoutParams WAP_WAP_LAYOUTPARAMS =new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    private int[] left_images={R.drawable.left_finder,R.drawable.left_flag,R.drawable.left_list,R.drawable.left_name};

    public String resclassname;
    private Context cxt;
    private  Handler handler;
    private Map<String, Object> map;
    private Map<String, EditText> etMap;
    private String s_queryEnname;

    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_setconditions);

        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        resclassname = bundle.getString("resclassname");
        Log.e("resclassname", resclassname);

        //初始化
        init();
        //获取后台数据
        doQuery(resclassname);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {

                    dialog.hide();
                    JSONObject jsonObject=null;
                    try {
                        String resultData=msg.getData().getString("data");
                        Log.e("SetConditionsActivity of resultData", resultData);
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

                    try {

                        String jsonData=jsonObject.getString("data");

                        LinearLayout layout=creatLayout(jsonData);//获得布局
                        setContentView(layout);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    ViewUtils.showToast(cxt, "错误的返回类型");
                }
            }
        };
    }
    /**
     * 初始化
     */
    public void init(){

        cxt=SetConditionsActivity.this;
        map=new HashMap<String, Object>();
        etMap=new HashMap<String, EditText>();
        dialog=ViewUtils.createDialog(cxt, "数据加载中");
    }
    /**
     * 动态创建布局
     * @return
     * @throws JSONException
     */
    @SuppressLint({ "NewApi", "ResourceAsColor" })
    public LinearLayout creatLayout(String jsonData) throws JSONException{

        if(jsonData==null || jsonData.length()<=0){
            return new LinearLayout(cxt);
        }

        //{"queryCnname":"设备名称,机房名称,类型","queryEnname":"zh_label,equiproom_id,type","queryType":"input,select,select","queryWidth":".3,.3,.3"}

        //最外层的LinearLayout
        LinearLayout parent_layout = new LinearLayout(cxt);
        parent_layout.setOrientation(LinearLayout.VERTICAL);
        parent_layout.setLayoutParams(FILL_FILL_LAYOUTPARAMS);
        parent_layout.setBackgroundColor(cxt.getResources().getColor(R.color.aliceblue));
        int w=DimenUtils.dip2px(cxt, 10);
        parent_layout.setPadding(w, w, w, w);

        //第1个子LinearLayout
        LinearLayout child1_layout = new LinearLayout(cxt);
        child1_layout.setOrientation(LinearLayout.VERTICAL);
        child1_layout.setLayoutParams(new LinearLayout.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
        child1_layout.setBackgroundDrawable(cxt.getResources().getDrawable(R.drawable.search_item_bg));
        JSONObject obj=new JSONObject(jsonData);
        if(obj!=null){
            String queryCnname=obj.getString("queryCnname");
            String queryEnname=obj.getString("queryEnname");
            s_queryEnname=queryEnname;
            String queryType=obj.getString("queryType");

            String[] queryCnnameArr=queryCnname.split(",");
            String[] queryEnnameArr=queryEnname.split(",");
            String[] queryTypeArr=queryType.split(",");


            if(queryCnnameArr!=null && queryCnnameArr.length>0){
                for(int i=0;i<queryCnnameArr.length;i++){

                    LinearLayout c_child_layout = new LinearLayout(cxt);
                    c_child_layout.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams c_child_layout_layoutParams=new LinearLayout.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
                    c_child_layout_layoutParams.leftMargin=DimenUtils.dip2px(cxt, 8);
                    c_child_layout_layoutParams.topMargin=DimenUtils.dip2px(cxt, 8);
                    c_child_layout_layoutParams.bottomMargin=DimenUtils.dip2px(cxt, 8);
                    c_child_layout_layoutParams.rightMargin=DimenUtils.dip2px(cxt, 5);
                    c_child_layout.setLayoutParams(c_child_layout_layoutParams);

                    String type=queryTypeArr[i];

                    TextView tv=new TextView(cxt, null, R.style.search_item_text);
                    LayoutParams layoutParams=new LinearLayout.LayoutParams(ListView.LayoutParams.WRAP_CONTENT, ListView.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity=Gravity.CENTER_VERTICAL;
                    layoutParams.rightMargin=DimenUtils.dip2px(cxt, 5);
                    tv.setLayoutParams(layoutParams);
                    Drawable drawable=cxt.getResources().getDrawable(left_images[i]);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    tv.setCompoundDrawables(drawable, null, null, null);
                    tv.setText(queryCnnameArr[i]);
                    tv.setTextColor(R.color.gray);
                    c_child_layout.addView(tv);

                    if("input".equals(type)){

                        EditText et=new EditText(cxt);
                        et.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                        et.setTextColor(R.color.black);
                        c_child_layout.addView(et);
                        etMap.put(queryEnnameArr[i], et);
                    }
                    else if("select".equals(type)){

                        EditText et1=new EditText(cxt);
                        et1.setId(i);
                        et1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                        et1.setText("请选择"+queryCnnameArr[i]);
                        et1.setTextColor(R.color.black);
                        c_child_layout.addView(et1);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        final String s_resclassname=resclassname;
                        final String s_conditionName=queryEnnameArr[i];
                        final int item=i;

                        et1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent=new Intent(cxt,ConditionItemActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("resclassname", s_resclassname);
                                bundle.putString("attributeenname",s_conditionName);
                                bundle.putInt("item",item);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 100);
                            }
                        });
                    }

                    if(i!=0){
                        View view=new View(cxt);
                        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
                        view.setBackgroundColor(cxt.getResources().getColor(R.color.lightgray));
                        child1_layout.addView(view);
                    }

                    child1_layout.addView(c_child_layout);
                }
            }
        }

        //第2个子LinearLayout
        LinearLayout child2_layout = new LinearLayout(cxt);
        child2_layout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams child2_layout_layoutParams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        child2_layout_layoutParams.topMargin=DimenUtils.dip2px(cxt, 8);
        child2_layout.setLayoutParams(child2_layout_layoutParams);
        child2_layout.setGravity(Gravity.CENTER_VERTICAL);
        Button btn_confirm=new Button(cxt);
        btn_confirm.setText("确定");
        btn_confirm.setLayoutParams(new LayoutParams(ListView.LayoutParams.MATCH_PARENT, DimenUtils.dip2px(cxt, 45)));
        btn_confirm.setBackgroundDrawable(cxt.getResources().getDrawable(R.drawable.button_selector));
        btn_confirm.setTextColor(R.color.white);
        //按钮点击事件
        btn_confirm.setOnClickListener(new MyOnClickListener());

        child2_layout.addView(btn_confirm);

        parent_layout.addView(child1_layout);
        parent_layout.addView(child2_layout);

        return parent_layout;
    }
    //点击按钮跳转到下一个activity
    protected class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //跳转到下一个Activity
//			Intent intent=new Intent(cxt,ResListActivity.class);
            Intent intent=new Intent(cxt,RResListActivity.class);

            Bundle bundle = new Bundle();

            //迭代map，组装查询条件(获得select类型的值)
            StringBuffer conditionStr=new StringBuffer();
            Iterator iter_map = map.entrySet().iterator();
            while (iter_map.hasNext()) {
                Map.Entry entry = (Map.Entry) iter_map.next();
                Object obj_k = entry.getKey();
                Object obj_v = entry.getValue();
                if(obj_v!=null){
                    if("type".equals(obj_k.toString().trim())){
                        conditionStr.append(" and ");
                        conditionStr.append(obj_k+" like '%"+obj_v+"%'");
                    }else{
                        conditionStr.append(" and ");
                        conditionStr.append(obj_k+"="+obj_v);
                    }

                }
            }
            //迭代map，组装查询条件(获得input类型的值)
            Iterator iter_etMap = etMap.entrySet().iterator();
            while (iter_etMap.hasNext()) {
                Map.Entry entry = (Map.Entry) iter_etMap.next();
                Object obj_k = entry.getKey();
                EditText obj_v = (EditText) entry.getValue();
                Object value=obj_v.getText();
                if(value!=null){
                    String s_value=value.toString().trim();
                    if(s_value!=null && !"".equals(s_value.trim())){
                        conditionStr.append(" and ");
                        conditionStr.append(obj_k+" like '%"+s_value+"%'");
                    }
                }
            }

            Log.e("resclassname and conditionStr", resclassname+","+conditionStr.toString());
            bundle.putString("resclassname", resclassname);
            bundle.putString("queryEnname", s_queryEnname);
            bundle.putString("conditionStr", conditionStr.toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    /**
     * 调用后台获取条件数据
     */
    public void doQuery(String resclassname){

        dialog.show();
        final RequestInfo reqInfo=new RequestInfo();
        reqInfo.context=cxt;
        reqInfo.requestUrl=URLManager.URL+URLManager.PRINT_GETQUERYCONDITION;
        reqInfo.params="{resclassname:'"+resclassname+"'}";

        new Thread() {
            @Override
            public void run() {
                String result=NetUtil.post(reqInfo);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){

            if(data != null && data.getStringExtra("itemData") != null){
                String itemData=data.getStringExtra("itemData");
                int item=data.getIntExtra("item",0);
                String attributeenname=data.getStringExtra("attributeenname");
                Log.e("itemData", itemData);
                JSONObject obj;
                try {
                    obj = new JSONObject(itemData);
                    String value=obj.getString("value");
                    String text=obj.getString("text");
                    EditText et1=(EditText) findViewById(item);
                    et1.setText(text);
                    map.put(attributeenname, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
