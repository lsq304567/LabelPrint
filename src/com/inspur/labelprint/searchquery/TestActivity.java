package com.inspur.labelprint.searchquery;

import java.util.ArrayList;
import java.util.List;

import com.inspur.common.view.hvlist.HvDynamicBean;
import com.inspur.common.view.hvlist.HvListAdapter;
import com.inspur.labelprint.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class TestActivity extends Activity {
	private ListView lv_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hvlist_view);
        String[] dd=    {"名称1","名称2","名称3","名称4","名称5","名称6","名称7","名称8","名称9","名称10","名称11"};
        String[] method={"getInt_id","getZh_label","getZh_label","getZh_label","getZh_label","getZh_label","getZh_label","getZh_label","getZh_label","getZh_label","getZh_label"};
        lv_content = (ListView) findViewById(R.id.lv_content);

        List<HvDynamicBean> mdata =new ArrayList<HvDynamicBean>();
        HvDynamicBean a1=new HvDynamicBean();
        a1.setInt_id("1111");
        a1.setZh_label("的费大幅度水电费水电费是东方闪电");
        HvDynamicBean a2=new HvDynamicBean();
        a2.setInt_id("3333");
        a2.setZh_label("dfdfdfd");

        mdata.add(a1);
        mdata.add(a2);

        HvListAdapter had=new HvListAdapter(TestActivity.this,mdata,dd,method);

        lv_content.setAdapter(had);
    }

}
