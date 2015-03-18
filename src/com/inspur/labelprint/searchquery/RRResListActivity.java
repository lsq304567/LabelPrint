package com.inspur.labelprint.searchquery;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inspur.common.Config;
import com.inspur.common.domain.RequestInfo;
import com.inspur.common.utils.JsonUtil;
import com.inspur.common.utils.NetUtil;
import com.inspur.common.utils.URLManager;
import com.inspur.common.utils.ViewUtils;
import com.inspur.common.view.hvlist.HvDynamicBean;
import com.inspur.common.view.hvlist.HvListAdapter;
import com.inspur.labelprint.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Jack.WewinPrinterHelper.Print;

/**
 * Created by Administrator on 2014/12/19 0019
 * 查询资源界面，具有带checkbox的横向LISTVIEW，和分页
 */
public class RRResListActivity extends Activity implements View.OnClickListener{
    private ListView listview;
    private ImageButton bakBtn;    //返回上一界面
    private ImageButton printEye;   //进入打印预览界面
    private HvListAdapter adapter;
    List<HvDynamicBean> mdata;
    private Context cxt;
    private String resclassname;
    private String conditionStr;
    private int page;
    private int pageSize;
    private  Handler handler;
    private int totalPages;
    private List<Map<String, Object>> list_map;
    private String s_etitle;
    private String[] etitle;
    private String[] ctitle;
    private Button btn_frontpage;
    private Button btn_afterpage;
    private TextView tv_currentpage;
    private String s_formats;
    private String queryEnname;

    private Dialog dialog;
    private Print p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reslist_view);
        
        ActionBar bar=getActionBar();
        bar.hide();
        
        p=new Print();

        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        resclassname = bundle.getString("resclassname");
        conditionStr = bundle.getString("conditionStr");
        queryEnname = bundle.getString("queryEnname");


        //初始化
        init();
        btn_frontpage.setOnClickListener(this);
        btn_afterpage.setOnClickListener(this);
        tv_currentpage.setText("第"+page+"页");

        //获取后台数据
        doQuery(resclassname, conditionStr, page, pageSize);
        //获得打印格式
//    	getPrintFormat();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                dialog.dismiss();
                if (msg.what == 1) {


                    JSONObject jsonObject=null;
                    try {
                        String resultData=msg.getData().getString("data");
                        Log.e("RResListActivity of resultData", resultData);
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
                        totalPages=jsonObject.getInt("totalPages");

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        ViewUtils.showToast(cxt, "服务端返回json格式错误");
                        return;
                    }

                    try {
                        String jsonData=jsonObject.getString("data");
                        //数据解析
                        parseData(jsonData);

                        HvDynamicBean bean=new HvDynamicBean();
                        if(list_map!=null && list_map.size()>0){
                            mdata.clear();
                            for(Map<String, Object> item:list_map){
                                if(item!=null){
                                    String[] s_obj=new String[etitle.length];
                                    for(int i=0;i<etitle.length;i++){
                                    	String key=etitle[i];
                                        s_obj[i]=item.containsKey(key)?(item.get(key)==null?"":item.get(key).toString()):"";
                                    }
                                    bean=new HvDynamicBean(s_obj);
                                    mdata.add(bean);
                                }
                            }
                        }

                        Log.e("mdata of size:", mdata.size()+"");

//						adapter=new RResListItemAdapter(list, ctitle,cxt);
//						adapter=new HvListAdapter(cxt,mdata,dd,method);

                        adapter=new HvListAdapter(cxt,mdata,ctitle,bean.getMethods());
                        adapter.notifyDataSetChanged();
                        listview.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(msg.what == 2){
                    String resultData=msg.getData().getString("data");
                    Log.e("getPrintFormat resultData", resultData);
                    if(resultData.contains("error")){
                        ViewUtils.showToast(cxt, "服务端异常");
                        return;
                    }
                    s_formats=resultData;
                }
                else if(msg.what == 3){
                    /**
                     * 返回值：-6:打印服务安装失败,-5:打印失败,-4:黑度设置数据发送失败,-3:蓝牙未开启,-2:黑度设置异常,-1:
                     * 连接打印机失败,0:打印机未配对,1:打印机连接成功,2:黑度设置成功,3:打印成功
                     */
                    int printInt=msg.getData().getInt("printInt");
                    Log.i("printInt", printInt+"");
                    if(printInt!=0){
                        ViewUtils.showToast(cxt, Config.PRINTMESSAGE.get(printInt));
                    }
                }
                else{
                    ViewUtils.showToast(cxt, "错误的返回类型");
                }
            }
        };

        //返回事件
        bakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //预览事件
        printEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//  				List<JSONArray> sData = adapter.getSelectedData() ;
//  				List<HvDynamicBean> sData =new ArrayList<HvDynamicBean>();

                List<Integer> items =adapter.getSelectedItem();

                Log.e("items of size:", items.size()+"");

                if(items==null || items.size()<=0){
                    ViewUtils.showToast(cxt,"请选择打印数据");
                }else{
                   
                	List<Map<String, Object>> printData=new ArrayList<Map<String,Object>>();
                	for(Integer item:items){
                		Log.i("item", item+"");
                		printData.add(list_map.get(item-1));
                	}
                	Log.e("printData of size:", printData.size()+"");
                	
                    doPrint(printData);
                }
            }
        });
    }

    /**
     * 初始化
     */
    protected void init() {
        bakBtn= (ImageButton)findViewById(R.id.ivTitleBtnLeft);
        printEye = (ImageButton)findViewById(R.id.ivTitleBtnRigh);
        listview =(ListView) findViewById(R.id.lv_content);
        btn_frontpage=(Button) this.findViewById(R.id.btn_frontpage);
        btn_afterpage=(Button) this.findViewById(R.id.btn_afterpage);
        tv_currentpage=(TextView) this.findViewById(R.id.tv_currentpage);
        mdata =new ArrayList<HvDynamicBean>();


        cxt=RRResListActivity.this;
        page=1;
        pageSize=20;
        totalPages=0;
//        list=new ArrayList<JSONArray>();
        list_map=new ArrayList<Map<String,Object>>();
        dialog=ViewUtils.createDialog(cxt, "数据查询中");
    }
    /**
     * 调用后台获取条件数据
     * @throws JSONException
     */
    public void doQuery(String resclassname,String conditionStr,int page,int pageSize){

        dialog.show();
        final RequestInfo reqInfo=new RequestInfo();
        reqInfo.context=cxt;
        reqInfo.requestUrl=URLManager.URL+URLManager.PRINT_SEARCHRESDATA;
        //设置参数
        JSONObject param=new JSONObject();
        try {
            param.put("username", "root");
            param.put("resclassenname", resclassname);
            param.put("queryCondition", conditionStr);
            param.put("page", page);
            param.put("pageSize", pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        reqInfo.params=param.toString();

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
    /**
     * 获得打印格式
     */
    public void getPrintFormat(){
        dialog.show();
        final RequestInfo reqInfo=new RequestInfo();
        reqInfo.context=cxt;
        reqInfo.requestUrl=URLManager.URL+URLManager.PRINT_PRINTFORMAT;
        reqInfo.method="GET";
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
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }
    /**
     * 数据解析
     * @param jsonData
     * @throws JSONException
     */
    public void parseData(String jsonData) throws JSONException{

        if(jsonData==null || jsonData.length()<=0){
            return;
        }

        JSONObject object=new JSONObject(jsonData);

        if(object!=null){
            s_formats=object.getString("formats");
            String dataArr=object.getString("fb_list");
            //英文字段标题
            s_etitle=object.getString("etitle");
            etitle=s_etitle.split(",");
            //中文字段标题
            String s_ctitle=object.getString("ctitle");
            ctitle=s_ctitle.split(",");

            if(dataArr!=null && dataArr.trim().length()>0){
            	list_map=JsonUtil.getList(dataArr);
            }
        }
    }

    @Override
    public void onClick(View v) {

        Log.e("onClick", "onClick");
        switch (v.getId()) {
            case R.id.btn_frontpage:
                if(page>1){
                    page--;
                    list_map.clear();
                    doQuery(resclassname, conditionStr, page, pageSize);
                    tv_currentpage.setText("第"+page+"页");
                }
                else{
                    Toast.makeText(cxt, "已经到第一页了....", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_afterpage:
                if(page<totalPages){
                    page++;
                    list_map.clear();
                    doQuery(resclassname, conditionStr, page, pageSize);
                    tv_currentpage.setText("第"+page+"页");
                }
                else{
                    Toast.makeText(cxt, "已经到最后页了....", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    /**
     * 打印
     */
    public void doPrint(final List<Map<String, Object>> printDataList){
        Log.e("doPrint", "printDataList size="+printDataList.size());
        ViewUtils.showToast(cxt, "正在调用打印机.....");
        final String xml=getXml(printDataList);
        Log.i("xml", xml);
        new Thread(new Runnable() {
            /**
             * 返回值：-6:打印服务安装失败,-5:打印失败,-4:黑度设置数据发送失败,-3:蓝牙未开启,-2:黑度设置异常,-1:
             * 连接打印机失败,0:打印机未配对,1:打印机连接成功,2:黑度设置成功,3:打印成功
             */
            @Override
            public void run() {
                int printInt = 0;

                try {
                    Log.i("print status", "start print");
                    printInt =p.TCMPrint(cxt,xml);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("exception", "调用打印机异常");
                }
                finally{
                    p.close();
                    Message msg = new Message();
                    msg.what = 3;
                    Bundle bundle=new Bundle();
                    bundle.putInt("printInt", printInt);
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    /**
     * to xml
     * @param printDataList
     * @return
     */
    public String getXml(List<Map<String, Object>> printDataList){

        //<Data><Print><LabelType>业务设备</LabelType><Code>11111111111</Code><Text>中国重庆北部新d></Text>
        StringBuffer sb=new StringBuffer();

        if(printDataList==null || printDataList.size()<0){
            return null;
        }
        sb.append("<Data>");
        String xml=parseToXml(s_formats, printDataList);
        sb.append(xml);
        sb.append("</Data>");
        return sb.toString();
    }
    /**
     * 获得每条打印项的详细信息
     * @param preview_cnlabel 中文标签
     * @param preview_enlabel 英文标签
     * @param bean 数据
     * @param label_type 顶部标题
     * @return
     */
    public String getString(String preview_cnlabel,String preview_enlabel,Map<String,Object> bean, String label_type){
    	
//    	Log.i("getString", "preview_cnlabel="+preview_cnlabel+";preview_enlabel="+preview_enlabel+";label_type="+label_type);
    	
    	StringBuffer sb=new StringBuffer();
        sb.append("<Print>").append("<LabelType>").append(label_type).append("</LabelType>");
        sb.append("<Code>").append(bean.get("ewm")).append("</Code>");
//        if(!("TAIL_FIBER_TAG".equals(label_type))){
//        	sb.append("<Code>").append(bean.get("ewm")).append("</Code>");
//        }       
        String[] arr_preview_cnlabel=preview_cnlabel.split(",");
        String[] arr_preview_enlabel=preview_enlabel.split(",");
        
        for(int i=0;i<arr_preview_enlabel.length;i++){
        	
        	String key=arr_preview_enlabel[i];
        	String value=bean.containsKey(key)?(bean.get(key)==null?"":bean.get(key).toString()):"";
        	sb.append("<Text>").append(arr_preview_cnlabel[i]+":"+value).append("</Text>");
        }
        
        sb.append("</Print>");
        return sb.toString();
    }

    public String parseToXml(String jsonStr,List<Map<String,Object>> printDataList){

    	/*
    	[
    		{"label_type":"123","preview_cnlabel":"22","preview_enlabel":"dd","resclassenname":"eee"},
    		{"label_type":"1sd3","preview_cnlabel":"2cc","preview_enlabel":"dvv","resclassenname":"eev"}
    	]
    	*/

        StringBuffer buffer=new StringBuffer();
        List<Map<String, Object>> list_map=JsonUtil.getList(jsonStr);
        if(list_map.isEmpty() || list_map.size()<0){
            return null;
        }
        if(printDataList.isEmpty() || printDataList.size()<0){
            return null;
        }
        //{\"label_type\":\"机架位置表\",\"preview_cnlabel\":\"资源管理单位,机架编号,所在行,所在列,所在面\",\"preview_enlabel\":\"manage_unit,zh_label,line,column_num,surface\",\"resclassenname\":\"RACK_POSITION\"}
        String s_type=resclassname;
        boolean flag=s_etitle.contains("type") && queryEnname.contains("type");
        for(Map<String,Object> bean:printDataList){    
        	if(flag){
                s_type=bean.get("type").toString();
            }
            Map<String,Object> map=getMap(list_map, s_type);
            String preview_cnlabel=map.get("preview_cnlabel").toString();
            String preview_enlabel=map.get("preview_enlabel").toString();
            String label_type=map.get("label_type").toString();
            String str=this.getString(preview_cnlabel,preview_enlabel,bean, label_type);
            buffer.append(str);
        }
        return buffer.toString();
    }
    /**
     * 查找匹配的数据
     * @param list_map
     * @param s_type
     * @return
     */
    public Map<String,Object> getMap(List<Map<String, Object>> list_map,String s_type){
        for(Map<String, Object> map:list_map){
            if(s_type.equals(map.get("resclassenname"))){
                return map;
            }
        }
        return null;
    }
    @Override
    protected void onDestroy() {
        p.close();
        super.onDestroy();
    }
}
