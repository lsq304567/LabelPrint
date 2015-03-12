package com.inspur.common.utils;

public class URLManager {

//	public static final String URL = "http://10.18.1.16:8888";
//	public static final String URL = "http://10.18.11.39:8080";
//	public static String URL = "http://211.139.58.223:8001";
//	public static final String URL = "http://218.57.146.147:8200";
//	public static final String URL = "http://10.204.35.161:8080";
//	public static String URL = "http://211.137.3.168:7001";



    public static final String RESOURCEQUERY_URL = "/appservice/rest/app/resourceQuery/";
    public static final String RESOURCECHECK_URL = "/appservice/rest/app/resourceCheck/";

    //////////start
//	public static String URL = "http://172.28.158.6:8080";
    public static String URL = "http://211.139.58.223:8001";

    public static final String LABELPRINT_URL="/appservice/rest/app/labelPrint/";
    public static final String PRINT_QUERYRESOURCENOTE=LABELPRINT_URL+"queryResourceNote";//标签打印-获得资源节点
    public static final String PRINT_GETQUERYCONDITION=LABELPRINT_URL + "getQueryCondition";//标签打印-获得查询条件
    public static final String PRINT_GETSELECTVALUE=LABELPRINT_URL + "getSelectValue";//标签打印-获得条件值
    public static final String PRINT_SEARCHRESDATA=LABELPRINT_URL + "searchResData";//标签打印-查询网元数据
    public static final String PRINT_PRINTFORMAT=LABELPRINT_URL+"queryPrintFormat";//打印格式

    //////////end

    //查询资源属性列表-资源详情
    public static final String RES_QUERYOBJECT_BYID = RESOURCEQUERY_URL + "RES_QueryObject_ByID";
    //查询资源属性列表-资源详情
    public static final String RES_QUERYATTROBJECT_BYID = RESOURCEQUERY_URL + "RES_QueryAttrObject_ByID";
    //查询资源关联信息
    public static final String RES_QUERYRELATION_BYID = RESOURCEQUERY_URL + "RES_QueryRelation_ByID";
    //专业查询-分页数据查询
    public static final String RES_QUERYPAGE_BYNAME = RESOURCEQUERY_URL + "RES_QueryPage_ByName";
    //这初始化专业查询的查询条件
    public static final String RES_QUERYINITCONDITION = RESOURCEQUERY_URL + "RES_QueryInitCondition";
    public static final String RES_QUERYPHYSICVIEW = RESOURCEQUERY_URL + "RES_QueryPhysicView";
    public static final String RES_QUERYATTRTRANS = RESOURCEQUERY_URL + "RES_QueryAttrTrans";

    //错误上报
    public static final String CHECK_ERROR_REPORT = RESOURCECHECK_URL + "errorReport";
    //错误上报无附件
    public static final String CHECK_ERROR_REPORT_NOFILE = RESOURCECHECK_URL + "errorReportNoFile";
    //属性修改
    public static final String CHECK_ATTRIBUTE_MODIFY = RESOURCECHECK_URL + "attributeModify";
    //经伟度较准
    public static final String CHECK_LOCATION_MODIFY = RESOURCECHECK_URL + "locationModify";

    //根据用户ID获取待受理、待核查的工单
    //数据格式->{total:10,dataList:[{name:'123',pas:'22'},{name:'222',pas:'333'}]}
    public static final String GET_ORDERS_BYUSERID=URL+"getOrdersByUserid";
    //根据工单获取需核查的资源
    public static final String GET_ORDER_RES=URL+"getOrderRes";

}
