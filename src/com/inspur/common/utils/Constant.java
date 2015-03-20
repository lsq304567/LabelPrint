package com.inspur.common.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.widget.RelativeLayout.LayoutParams;

public class Constant {

	public final static int SUCCESS = 200;// 请求网络数据成功，这个只是标识，和net code 200 没关系
	public final static int FAILED = 201;// 请求网络数据失败
    public final static int SERVICES_BIND_FAILURE = 20;
    public static boolean NEED_LOCATE_WARN = true;
    public static final int JSON_EXCEPTION = 2;// json解析异常
    public static final int JSON_NULL = 3;// json为空
    public static final int JSON_OK = 4;// json非空
    public final static String PACKAGE_NAME = "com.app.taskmanager";

    public final static int UNDO=0;  //资源未巡检
    public final static int CHECKED=1; //已巡检
    public final static int CHECKED_NORES=2;  //已巡检但未更改属性

    public final static int CAOGAO=0;
    public final static int DAISHOULI=1;
    public final static int DAIHECHA=2;
    public final static int HECHAZHONG=3;
    public final static int HECHAWANC=4;
    
    public final static LayoutParams HVS_LPARAMS=new LayoutParams(220,80);//HVScrollListView参数

    //字典转换器,统一配置在此处
    //工单状态
    public static final HashMap<Integer,String> HASH_ORDER_STATE= new HashMap<Integer,String>(){
        {
            put(CAOGAO,"草稿");
            put(DAISHOULI,"待受理");
            put(DAIHECHA,"待核查");
            put(HECHAZHONG,"核查中");
            put(HECHAWANC,"核查完成");
        }


    };
    public static ArrayList<?> RES_TYPE=new ArrayList<String[]>(){{
        add(new String[]{"无线与动力资源","1"});
        add(new String[]{"空间资源","2"});
        add(new String[]{"传输资源","3"});
        add(new String[]{"集团客户资源","4"});
    }};

    //定义查询资源的类型
    public static String RES_CLASSENNAME="BSC";

    //end


}
