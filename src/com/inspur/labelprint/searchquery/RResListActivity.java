package com.inspur.labelprint.searchquery;

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
import android.widget.TableLayout;
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
public class RResListActivity extends Activity implements View.OnClickListener{
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
    private List<JSONArray> list;
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
                        if(list!=null && list.size()>0){
                            mdata.clear();
                            for(JSONArray item:list){
                                if(item!=null){
                                    String[] s_obj=new String[item.length()];
                                    for(int i=0;i<item.length();i++){
                                        s_obj[i]=item.getString(i);
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

                List<HvDynamicBean> sData =adapter.getSelectedData();

                Log.e("sData of size:", sData.size()+"");

                if(sData==null || sData.size()<=0){
                    ViewUtils.showToast(cxt,"请选择打印数据");
                }else{
                    /*Intent intent=new Intent(cxt,PrintViewActivity.class);
                    intent.putExtra("arrayList",(Serializable)sData);
                    intent.putExtra("ctitle", (Serializable)etitle);
                    intent.putExtra("title", (Serializable)ctitle);
                    intent.putExtra("resclassname", resclassname);
                    startActivity(intent);*/
                    doPrint(sData);
                }
            }
        });
    }

    /**
     * 初始化
     */
    protected void init() {
    	
    	this.getActionBar().hide();
        bakBtn= (ImageButton)findViewById(R.id.ivTitleBtnLeft);
        printEye = (ImageButton)findViewById(R.id.ivTitleBtnRigh);
        listview =(ListView) findViewById(R.id.lv_content);
        btn_frontpage=(Button) this.findViewById(R.id.btn_frontpage);
        btn_afterpage=(Button) this.findViewById(R.id.btn_afterpage);
        tv_currentpage=(TextView) this.findViewById(R.id.tv_currentpage);
        mdata =new ArrayList<HvDynamicBean>();


        cxt=RResListActivity.this;
        page=1;
        pageSize=20;
        totalPages=0;
        list=new ArrayList<JSONArray>();
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
            String dataArr=object.getString("list");
            //英文字段标题
            s_etitle=object.getString("etitle");
            etitle=s_etitle.split(",");
            //中文字段标题
            String s_ctitle=object.getString("ctitle");
            ctitle=s_ctitle.split(",");

            if(dataArr!=null && dataArr.trim().length()>0){
                JSONArray array=new JSONArray(dataArr);
//				List<JSONArray> jz_list=new ArrayList<JSONArray>();
                list=new ArrayList<JSONArray>();
                for(int i=0;i<array.length();i++){
                    JSONArray item=(JSONArray) array.get(i);
                    list.add(item);
                }
//				list.add(0,new JSONArray(Arrays.deepToString(ctitle)));

//				list.addAll(0, jz_list);
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
                    list.clear();
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
                    list.clear();
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
    public void doPrint(final List<HvDynamicBean> printDataList){
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
    public String getXml(List<HvDynamicBean> printDataList){

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
     * @param ctitle
     * @return
     */
    public String getString(String[] ctitle,HvDynamicBean bean,String label_type){
        StringBuffer sb=new StringBuffer();
        sb.append("<Print>").append("<LabelType>").append(label_type).append("</LabelType>");
        HvDynamicBean dynamicBean=bean;
        switch (ctitle.length) {
            case 1:
                sb.append("<Code>").append(dynamicBean.getData1()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                break;
            case 2:
                sb.append("<Code>").append(dynamicBean.getData2()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                String date2_1=dynamicBean.getData1();
                if(date2_1!=null && !"null".equals(date2_1) && date2_1.trim().length()>0){
                    sb.append("<Text>").append(ctitle[0]+":"+date2_1).append("</Text>");
                }
                break;
            case 3:
                sb.append("<Code>").append(dynamicBean.getData3()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                String date3_1=dynamicBean.getData1();
                if(date3_1!=null && !"null".equals(date3_1) && date3_1.trim().length()>0){
                    sb.append("<Text>").append(ctitle[0]+":"+date3_1).append("</Text>");
                }
                String date3_2=dynamicBean.getData2();
                if(date3_2!=null && !"null".equals(date3_2) && date3_2.trim().length()>0){
                    sb.append("<Text>").append(ctitle[1]+":"+date3_2).append("</Text>");
                }
                break;
            case 4:
                sb.append("<Code>").append(dynamicBean.getData4()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                String date4_1=dynamicBean.getData1();
                if(date4_1!=null && !"null".equals(date4_1) && date4_1.trim().length()>0){
                    sb.append("<Text>").append(ctitle[0]+":"+date4_1).append("</Text>");
                }
                String date4_2=dynamicBean.getData2();
                if(date4_2!=null && !"null".equals(date4_2) && date4_2.trim().length()>0){
                    sb.append("<Text>").append(ctitle[1]+":"+date4_2).append("</Text>");
                }
                String date4_3=dynamicBean.getData3();
                if(date4_3!=null && !"null".equals(date4_3) && date4_3.trim().length()>0){
                    sb.append("<Text>").append(ctitle[2]+":"+date4_3).append("</Text>");
                }
                break;
            case 5:
                sb.append("<Code>").append(dynamicBean.getData5()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                String date5_1=dynamicBean.getData1();
                if(date5_1!=null && !"null".equals(date5_1) && date5_1.trim().length()>0){
                    sb.append("<Text>").append(ctitle[0]+":"+date5_1).append("</Text>");
                }
                String date5_2=dynamicBean.getData2();
                if(date5_2!=null && !"null".equals(date5_2) && date5_2.trim().length()>0){
                    sb.append("<Text>").append(ctitle[1]+":"+date5_2).append("</Text>");
                }
                String date5_3=dynamicBean.getData3();
                if(date5_3!=null && !"null".equals(date5_3) && date5_3.trim().length()>0){
                    sb.append("<Text>").append(ctitle[2]+":"+date5_3).append("</Text>");
                }
                String date5_4=dynamicBean.getData4();
                if(date5_4!=null && !"null".equals(date5_4) && date5_4.trim().length()>0){
                    sb.append("<Text>").append(ctitle[3]+":"+date5_4).append("</Text>");
                }
                break;
            case 6:
                sb.append("<Code>").append(dynamicBean.getData6()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                String date6_1=dynamicBean.getData1();
                if(date6_1!=null && !"null".equals(date6_1) && date6_1.trim().length()>0){
                    sb.append("<Text>").append(ctitle[0]+":"+date6_1).append("</Text>");
                }
                String date6_2=dynamicBean.getData2();
                if(date6_2!=null && !"null".equals(date6_2) && date6_2.trim().length()>0){
                    sb.append("<Text>").append(ctitle[1]+":"+date6_2).append("</Text>");
                }
                String date6_3=dynamicBean.getData3();
                if(date6_3!=null && !"null".equals(date6_3) && date6_3.trim().length()>0){
                    sb.append("<Text>").append(ctitle[2]+":"+date6_3).append("</Text>");
                }
                String date6_4=dynamicBean.getData4();
                if(date6_4!=null && !"null".equals(date6_4) && date6_4.trim().length()>0){
                    sb.append("<Text>").append(ctitle[3]+":"+date6_4).append("</Text>");
                }
                String date6_5=dynamicBean.getData5();
                if(date6_5!=null && !"null".equals(date6_5) && date6_5.trim().length()>0){
                    sb.append("<Text>").append(ctitle[4]+":"+date6_5).append("</Text>");
                }
                break;
            case 7:
                sb.append("<Code>").append(dynamicBean.getData7()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                String date7_1=dynamicBean.getData1();
                if(date7_1!=null && !"null".equals(date7_1) && date7_1.trim().length()>0){
                    sb.append("<Text>").append(ctitle[0]+":"+date7_1).append("</Text>");
                }
                String date7_2=dynamicBean.getData2();
                if(date7_2!=null && !"null".equals(date7_2) && date7_2.trim().length()>0){
                    sb.append("<Text>").append(ctitle[1]+":"+date7_2).append("</Text>");
                }
                String date7_3=dynamicBean.getData3();
                if(date7_3!=null && !"null".equals(date7_3) && date7_3.trim().length()>0){
                    sb.append("<Text>").append(ctitle[2]+":"+date7_3).append("</Text>");
                }
                String date7_4=dynamicBean.getData4();
                if(date7_4!=null && !"null".equals(date7_4) && date7_4.trim().length()>0){
                    sb.append("<Text>").append(ctitle[3]+":"+date7_4).append("</Text>");
                }
                String date7_5=dynamicBean.getData5();
                if(date7_5!=null && !"null".equals(date7_5) && date7_5.trim().length()>0){
                    sb.append("<Text>").append(ctitle[4]+":"+date7_5).append("</Text>");
                }
                String date7_6=dynamicBean.getData6();
                if(date7_6!=null && !"null".equals(date7_6) && date7_6.trim().length()>0){
                    sb.append("<Text>").append(ctitle[5]+":"+date7_6).append("</Text>");
                }
                break;
            case 8:
                sb.append("<Code>").append(dynamicBean.getData8()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                String date8_1=dynamicBean.getData1();
                if(date8_1!=null && !"null".equals(date8_1) && date8_1.trim().length()>0){
                    sb.append("<Text>").append(ctitle[0]+":"+date8_1).append("</Text>");
                }
                String date8_2=dynamicBean.getData2();
                if(date8_2!=null && !"null".equals(date8_2) && date8_2.trim().length()>0){
                    sb.append("<Text>").append(ctitle[1]+":"+date8_2).append("</Text>");
                }
                String date8_3=dynamicBean.getData3();
                if(date8_3!=null && !"null".equals(date8_3) && date8_3.trim().length()>0){
                    sb.append("<Text>").append(ctitle[2]+":"+date8_3).append("</Text>");
                }
                String date8_4=dynamicBean.getData4();
                if(date8_4!=null && !"null".equals(date8_4) && date8_4.trim().length()>0){
                    sb.append("<Text>").append(ctitle[3]+":"+date8_4).append("</Text>");
                }
                String date8_5=dynamicBean.getData5();
                if(date8_5!=null && !"null".equals(date8_5) && date8_5.trim().length()>0){
                    sb.append("<Text>").append(ctitle[4]+":"+date8_5).append("</Text>");
                }
                String date8_6=dynamicBean.getData6();
                if(date8_6!=null && !"null".equals(date8_6) && date8_6.trim().length()>0){
                    sb.append("<Text>").append(ctitle[5]+":"+date8_6).append("</Text>");
                }
                String date8_7=dynamicBean.getData7();
                if(date8_7!=null && !"null".equals(date8_7) && date8_7.trim().length()>0){
                    sb.append("<Text>").append(ctitle[6]+":"+date8_7).append("</Text>");
                }
                break;
            case 9:
                sb.append("<Code>").append(dynamicBean.getData9()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                String date9_1=dynamicBean.getData1();
                if(date9_1!=null && !"null".equals(date9_1) && date9_1.trim().length()>0){
                    sb.append("<Text>").append(ctitle[0]+":"+date9_1).append("</Text>");
                }
                String date9_2=dynamicBean.getData2();
                if(date9_2!=null && !"null".equals(date9_2) && date9_2.trim().length()>0){
                    sb.append("<Text>").append(ctitle[1]+":"+date9_2).append("</Text>");
                }
                String date9_3=dynamicBean.getData3();
                if(date9_3!=null && !"null".equals(date9_3) && date9_3.trim().length()>0){
                    sb.append("<Text>").append(ctitle[2]+":"+date9_3).append("</Text>");
                }
                String date9_4=dynamicBean.getData4();
                if(date9_4!=null && !"null".equals(date9_4) && date9_4.trim().length()>0){
                    sb.append("<Text>").append(ctitle[3]+":"+date9_4).append("</Text>");
                }
                String date9_5=dynamicBean.getData5();
                if(date9_5!=null && !"null".equals(date9_5) && date9_5.trim().length()>0){
                    sb.append("<Text>").append(ctitle[4]+":"+date9_5).append("</Text>");
                }
                String date9_6=dynamicBean.getData6();
                if(date9_6!=null && !"null".equals(date9_6) && date9_6.trim().length()>0){
                    sb.append("<Text>").append(ctitle[5]+":"+date9_6).append("</Text>");
                }
                String date9_7=dynamicBean.getData7();
                if(date9_7!=null && !"null".equals(date9_7) && date9_7.trim().length()>0){
                    sb.append("<Text>").append(ctitle[6]+":"+date9_7).append("</Text>");
                }
                String date9_8=dynamicBean.getData8();
                if(date9_8!=null && !"null".equals(date9_8) && date9_8.trim().length()>0){
                    sb.append("<Text>").append(ctitle[7]+":"+date9_8).append("</Text>");
                }
                break;
            case 10:
                sb.append("<Code>").append(dynamicBean.getData10()).append("</Code>");
//				sb.append("<Text>").append(label_type).append("</Text>");
                String date10_1=dynamicBean.getData1();
                if(date10_1!=null && !"null".equals(date10_1) && date10_1.trim().length()>0){
                    sb.append("<Text>").append(ctitle[0]+":"+date10_1).append("</Text>");
                }
                String date10_2=dynamicBean.getData2();
                if(date10_2!=null && !"null".equals(date10_2) && date10_2.trim().length()>0){
                    sb.append("<Text>").append(ctitle[1]+":"+date10_2).append("</Text>");
                }
                String date10_3=dynamicBean.getData3();
                if(date10_3!=null && !"null".equals(date10_3) && date10_3.trim().length()>0){
                    sb.append("<Text>").append(ctitle[2]+":"+date10_3).append("</Text>");
                }
                String date10_4=dynamicBean.getData4();
                if(date10_4!=null && !"null".equals(date10_4) && date10_4.trim().length()>0){
                    sb.append("<Text>").append(ctitle[3]+":"+date10_4).append("</Text>");
                }
                String date10_5=dynamicBean.getData5();
                if(date10_5!=null && !"null".equals(date10_5) && date10_5.trim().length()>0){
                    sb.append("<Text>").append(ctitle[4]+":"+date10_5).append("</Text>");
                }
                String date10_6=dynamicBean.getData6();
                if(date10_6!=null && !"null".equals(date10_6) && date10_6.trim().length()>0){
                    sb.append("<Text>").append(ctitle[5]+":"+date10_6).append("</Text>");
                }
                String date10_7=dynamicBean.getData7();
                if(date10_7!=null && !"null".equals(date10_7) && date10_7.trim().length()>0){
                    sb.append("<Text>").append(ctitle[6]+":"+date10_7).append("</Text>");
                }
                String date10_8=dynamicBean.getData8();
                if(date10_8!=null && !"null".equals(date10_8) && date10_8.trim().length()>0){
                    sb.append("<Text>").append(ctitle[7]+":"+date10_8).append("</Text>");
                }
                String date10_9=dynamicBean.getData9();
                if(date10_9!=null && !"null".equals(date10_9) && date10_9.trim().length()>0){
                    sb.append("<Text>").append(ctitle[8]+":"+date10_9).append("</Text>");
                }
                break;
            default:return null;
        }
        sb.append("</Print>");
        return sb.toString();
    }

    public String parseToXml(String jsonStr,List<HvDynamicBean> printDataList){

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

        String s_type=resclassname;
        boolean flag=s_etitle.contains("type") && queryEnname.contains("type");
        for(HvDynamicBean bean:printDataList){
            if(flag){
                s_type=bean.getData1();
            }
            Map<String,Object> map=getMap(list_map, s_type);
            String label_type=map.get("label_type").toString();
            String str=this.getString(ctitle, bean, label_type);
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
