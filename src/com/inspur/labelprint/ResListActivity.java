package com.inspur.labelprint;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.inspur.common.BaseActivity;
import com.inspur.common.domain.RequestInfo;
import com.inspur.common.utils.URLManager;
import com.inspur.common.utils.ViewUtils;
import com.inspur.common.view.hvlist.HvDynamicBean;
import com.inspur.common.view.hvlist.HvListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/12/19 0019
 * 查询资源界面，具有带checkbox的横向LISTVIEW，和分页
 */
public class ResListActivity extends BaseActivity
        implements 
        AbsListView.OnScrollListener,
        View.OnClickListener{

    private ListView listview;
    private ImageButton bakBtn;    //返回上一界面
    private ImageButton printEye;   //进入打印预览界面

    private int beginNum=0; //从第几条记录开始查询
    private int pageSize=10; //默认每页数据数
    private int totalCount=-1;  //记录总数
    private int countPage=-1;  //总页数

    private View footer;

    private HvListAdapter adapter;
    List<HvDynamicBean> mdata =new ArrayList<HvDynamicBean>();
    private String[] titles;
    private String[] methods;

    private String search_param="";  //查询页面传过来的查询参数

    private boolean finish = true;// 是否加载完成

    @Override
    protected void loadViewLayout() {
        setContentView(R.layout.reslist_view);

    }

    @Override
    protected void findViewById() {
        bakBtn= (ImageButton)findViewById(R.id.ivTitleBtnLeft);
        printEye = (ImageButton)findViewById(R.id.ivTitleBtnRigh);
        listview =(ListView) findViewById(R.id.lv_content);
        footer = View.inflate(this, R.layout.footer, null);

        adapter=new HvListAdapter(ResListActivity.this,mdata,titles,methods);

        listview.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        // 在增加listview的页脚之前，需要提前设置一次
        listview.addFooterView(footer);
        listview.setAdapter(adapter);
        // 然后再次将页脚删除掉
        listview.removeFooterView(footer);
        // 滚动监听事件
        listview.setOnScrollListener(this);
    }
    private void parseData(String param){
        finish = true;
        Gson g=new Gson();
        try {
            JSONObject jsonObj = new JSONObject(param);
            if (jsonObj.getString("success").equals("true")) {
                JSONArray array = new JSONArray(jsonObj
                        .getString("dataList"));
                for (int i = 0; i < array.length(); i++) {
                    HvDynamicBean item =
                            g.fromJson(array.get(i).toString(),HvDynamicBean.class);
                    mdata.add(item);
                }
                totalCount = Integer.parseInt(jsonObj
                        .getString("totalcount"));
            }

            adapter.notifyDataSetChanged();
            // 将页脚删除掉
            if (listview.getFooterViewsCount() > 0) {
                listview.removeFooterView(footer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void searchDataByPage(int begionCount){
        RequestInfo req = new RequestInfo();
        req.context = ResListActivity.this;
        req.requestUrl = URLManager.URL + "";

        JSONObject condition = new JSONObject();
        req.params = condition.toString();

        req.params="{beginNum:"+beginNum+",pageSize:"+pageSize+"}";
        super.getDataFromServer(req, new DataCallback<String>() {

            @Override
            public void processData(String paramObject) {
                Log.e("paramObject:", paramObject);
                parseData(paramObject);
            }

            @Override
            public void getDataFailed() {
                // 这里仅作演示，实际请求失败后怎么操作根据实际情况来看

            }

        }, ResListActivity.this);
    }
    @Override
    protected void processLogic() {
        searchDataByPage(0);
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
//                case SUCCESS_GET_DATA:
//                    @SuppressWarnings("unchecked")
//                    ArrayList<String> result = ((ArrayList<String>) msg.obj);
//                    data.addAll(result);
//                    // 让listview自动刷新
//                    adapter.notifyDataSetChanged();
//                    finish = true;
//                    // 将页脚删除掉
//                    if (listview.getFooterViewsCount() > 0) {
//                        listview.removeFooterView(footer);
//                    }
//                    break;
//
//                default:
//                    break;
            }
        };
    };

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        //总页数，每页计数从1开始
        if(totalCount%pageSize ==0 ){
            countPage = totalCount / pageSize;
        }else{
            countPage = totalCount / pageSize +1;
        }

        // 最下面的条目数
        final int totalCount = firstVisibleItem + visibleItemCount;
        int currentPage = totalCount / pageSize;// 当前页
        int nextPage = currentPage + 1;// 下一页
        // 当翻到最后一条数据时
        if (totalCount == totalItemCount && nextPage <= countPage && finish) {
            // 已经移动到了listview的最后
            finish = false;
            // 添加页脚
            listview.addFooterView(footer);
            searchDataByPage(totalCount+1);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivTitleBtnLeft:
                finish();
                break;
            case R.id.ivTitleBtnRigh:
                List<HvDynamicBean> sData = adapter.getSelectedData() ;
                if(sData==null&&sData.size()==0){
                    ViewUtils.showToast(ResListActivity.this,"请选择打印数据");
                }else{
                    Intent itent=new Intent(ResListActivity.this,PrintViewActivity.class);
                    itent.putExtra("arrayList",(Serializable)sData);
                    startActivity(itent);
                }

                break;
        }
    }
}
