package com.inspur.labelprint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import Jack.WewinPrinterHelper.Print;

public class MainActivity extends Activity {
    ImageView imageView1;

    Button btnPrint = null;
    Button btnClose = null;
    Bitmap bitmapQrcode;

    private Print p;

    String xml = "<Data><Print><LabelType>业务设备</LabelType><Code>11111111111</Code><Text>中国重庆北部新dghdfghj区"
            + "加州花园无线机个而他与i快活谷gsdgfgjgfjjc房可很快就会看见了据了解解决户口</Text><Text>线路名称：四川石油管理局通信公司重庆分公司转谈公司-安家嘴开源天然气公司塞德里克激发了手"
            + "机发送到附近</Text><Text>设备类型：协转</Text><Text>设备厂家：银驼铃</Text><Text>设备型号：GTT-FE1</Text><Text>业务标识：2308068308427</Text>"
            + "</Print></Data>";
//	String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><Data><Print><LabelType>JJ001</LabelType>"
//			+ "<Code>12345678900987654321</Code><Text>DDF机架名称：重庆伟杰建材01杰建材01F机房/DDF00F机房/DDF001</Text></Print></Data>";

    //	String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><Data><Print><LabelType>资源点</LabelType><Code>1212121211212</Code>"
//			+ "<Text>机房名称：合肥伟杰建材01F机房-合肥市卫生管理中心01F机房</Text><Text>所属班组：GYTA-24B</Text><Text>联系方式：15923581811</Text>>"
//			+ "<Text>机房名称：合肥伟杰建材01F机房-合肥市卫生管理中心01F机房</Text><Text>机房名称：合肥伟杰建材01F机房-合肥市卫生管理中心01F机房</Text></Print></Data>";
//	String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><Data><Print><LabelType>WY001</LabelType><Code>1212121211212</Code><Text>传输网元名称：重庆伟杰建材01F机"+
//			"伟杰建材01F机房/DDF001材房/DDF001材01F机房/DDF0</Text></Print><Print><LabelType>WY001</LabelType><Code>1212121211212</Code><Text>传输网元名称：重庆伟杰建材01F机伟杰建"+
//			"材01F机房/DDF001材房/DDF001材01F机房/DDF0</Text></Print></Data>";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.p = new Print();

        initView();

    }

    /**
     * 初始化组件
     */
    private void initView() {

        btnPrint = (Button) findViewById(R.id.btnPrint);
        bitmapQrcode = BitmapFactory.decodeResource(getResources(), R.drawable.hhh);
        btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
        // 当页面销毁和程序退出时，务必调用此方法
        p.close();
        finish();
            }
        });
        btnPrint.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /**
                 * 返回值：-6:打印服务安装失败,-5:打印失败,-4:黑度设置数据发送失败,-3:蓝牙未开启,-2:黑度设置异常,-1:
                 * 连接打印机失败,0:打印机未配对,1:打印机连接成功,2:黑度设置成功,3:打印成功
                 */
                Toast.makeText(getApplicationContext(), "正在调用打印机打印中...", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        int printInt = 0;

//							调用标签方法示例
                        printInt = p.TCMPrint(MainActivity.this,xml);

                        Log.e("info",printInt+"");
                        System.out.println("返回状态:" + printInt);
                    }
                }).start();
            }
        });
    }

    public void doPrint(){

    }

    @Override
    protected void onDestroy() {
        p.close();
        bitmapQrcode.recycle();
        super.onDestroy();
    }
}
