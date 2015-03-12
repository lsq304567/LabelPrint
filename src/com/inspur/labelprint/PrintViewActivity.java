package com.inspur.labelprint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.inspur.common.utils.BitMapUtil;
import com.inspur.common.utils.ViewUtils;
import com.inspur.common.view.hvlist.HvDynamicBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打印类，资源查询封装需打印的数据传过来，进行打印
 */
public class PrintViewActivity extends Activity implements View.OnClickListener{
    Button button;
   // Print print = new Print();
    ImageView imageView;
    private ProgressDialog pd;
    //需要打印的数据，arraylist如大于1，则连续打印多张
    //都是相同类型的资源，打印格式一致，所以BITMAP默认展现第一条数据
    private ArrayList<HashMap<String, String>> dataList;
    private List<HvDynamicBean> printDataList;
    private String[] ctitle;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.print_preview);
        printDataList =   (List<HvDynamicBean>) getIntent().getSerializableExtra("arrayList");
        //dataList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("arrayList");
        ctitle=(String[]) getIntent().getSerializableExtra("ctitle");
        checkBluetooth();
        initComponent();
    }
    //检查蓝牙是否打开，并且挂载了相应的打印机
    private void checkBluetooth(){
        boolean isBlueOk = this.bluetoothAdapter.isEnabled();
        if(!isBlueOk){
            ViewUtils.showToast(this,"请打开手机蓝牙,并确保打印机蓝牙在线，然后挂载相应的标签打印机");
            Intent enableBtIntent = new Intent(
                    Settings.ACTION_BLUETOOTH_SETTINGS);
            this.startActivity(enableBtIntent);
        }

    }
    //在界面上预览要打印标签的样子
    public void showPrintView(){
        /*//test
        if(dataList == null){
            HashMap<String,String> test = new HashMap<String, String>();
            test.put("class","ddd");
            dataList= new ArrayList<HashMap<String, String>>();
            dataList.add(test);
        }*/
    	
    	reBuildData(printDataList);

        if(dataList.size()>0){
            HashMap<String,String> firstItem=dataList.get(0);
            //其中key=class，为资源类型
            String className=firstItem.get("class");
            //构建底图
            Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cm);
            Matrix matrix_root=new Matrix();
            matrix_root.preScale(1.5f,1.5f);   //放大1.5倍
            rawBitmap = Bitmap.createBitmap(rawBitmap,0,0,rawBitmap.getWidth(),rawBitmap.getHeight(),matrix_root,true);


            List<String> list = new ArrayList<String>();
            /*list.add("合肥市卫生管理中心01F机房");
            list.add("机房编码：HF06708-1");*/
            
            list.add(firstItem.get("zh_label"));

            Bitmap bm = BitMapUtil.creatContentBitmap(list, 100 * 8,
                    35 * 8, 50f, 50f, 10.0f, 0);
            Bitmap root = Bitmap.createBitmap(rawBitmap.getWidth(),rawBitmap.getHeight(),rawBitmap.getConfig());
            Canvas cv=new Canvas(root);

            cv.drawBitmap(rawBitmap,0,0,null);
            //draw fg into
            //写入图片处理
            Matrix matrix=new Matrix();
            matrix.preRotate(90);
            Bitmap seBm=Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);
            cv.drawBitmap(seBm, 0, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
            imageView.setImageBitmap(root);
        }
    }

    public void doPrint(){
    	
                //调用蓝牙不能在子线程中进行
   
                /*HashMap<String, String> map = new HashMap<String,
                        String>();
                map.put("NAME", "合肥市卫生管理中心01F机房");
                map.put("NUM", "机房编码：HF06708-1");
                map.put("QRCODE", "123456123456123456123456");
                map.put("PRINTCOUNT", "1");
                String result = "";
                try {
                    result = print.PrintResources(ResourcesType.JZJF, map);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                pd.dismiss();
                ViewUtils.showToast(PrintViewActivity.this,result);*/
    	
    	
    	if(dataList!=null && dataList.size()>0){
    		int i=0;
			for(Map map:dataList){
				String result = "";
	            try {
	            	i++;
	            	HashMap<String, String> print_map = new HashMap<String, String>();
	            	print_map.put("NUM",String.valueOf(map.get("zh_label")));
	            	print_map.put("QRCODE",String.valueOf(map.get("qrcode")));
	            	print_map.put("PRINTCOUNT", String.valueOf(1));
					Log.e("start", "print start"+i);
					//print.PrintResources(ResourcesType.JJ, print_map);
					Log.e("end", "print end"+i);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            pd.dismiss();
	        	ViewUtils.showToast(PrintViewActivity.this,result);
			}
		}
    }
    public void initComponent() {
        button = (Button) findViewById(R.id.btn_print);
        imageView = (ImageView) findViewById(R.id.showImage);

        showPrintView();

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
            pd = ProgressDialog.show(PrintViewActivity.this,"处理中","正在打印...",true,true);

            doPrint();
            pd.dismiss();
//                    /**
//                     * 基站机房标签
//                     */
//                    List<String> list = new ArrayList<String>();
//                    list.add("合肥市卫生管理中心01F机房");
//                    list.add("机房编码：HF06708-1");
//                    Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cm);
//                    Bitmap root = Bitmap.createBitmap(rawBitmap.getWidth(),rawBitmap.getHeight(),rawBitmap.getConfig());
//                    Canvas cv=new Canvas(root);
//
//                    //draw bg into
//                    cv.drawBitmap(rawBitmap, 0, 0, null);//在 0，0坐标开始画入bg
//
//                    Bitmap bm1 = BitMapUtil.creatContentBitmap(list, 100 * 8,
//                            35 * 8, 50f, 50f, 10.0f, 0);
//                    Bitmap bm2 = QrCodeUtil.createQRCode(
//                            "123456123456123456123", 240, 240, 0, 0);
//                    Bitmap bm = BitMapUtil.combineBitmap(bm1, bm2,
//                            BitMapUtil.HORIZONTAL, 0, 20, 16, 90);
//                    //draw fg into
//                    cv.drawBitmap(bm, 0, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
//
//                    imageView.setImageBitmap(rawBitmap);
//
//                    HashMap<String, String> map = new HashMap<String,
//                            String>();
//                    map.put("NAME", "合肥市卫生管理中心01F机房");
//                    map.put("NUM", "机房编码：HF06708-1");
//                    map.put("QRCODE", "123456123456123456123456");
//                    map.put("PRINTCOUNT", "1");
//                    String result=print.PrintResources(ResourcesType.JZJF, map);
//                    System.out.println(result);
//                    Log.d("WewinPrint", result);
            /**
             * 机架标签
             */
//                    List<String> list = new ArrayList<String>();
//                    list.add("01列01架");
//                    Bitmap bm1 = BitMapUtil.creatContentBitmap(list, 290,
//                            30 * 8, 40f, 130f, 5.0f, 0);
//                    Bitmap bm2 = QrCodeUtil.createQRCode(
//                            "123456123456123456123456", 200, 200, 0, 0);
//                    Bitmap bm = BitMapUtil.combineBitmap(bm1, bm2,
//                            BitMapUtil.HORIZONTAL, 0, 25, 0, 90);
//                    imageView.setImageBitmap(bm);
//
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put("NUM", "01列01架");
//                    map.put("QRCODE", "123456789");
//                    map.put("PRINTCOUNT", "1");
//                    print.PrintResources(ResourcesType.JJ, map);

            /**
             * 设备网元标签
             */
            // List<String> list = new ArrayList<String>();
            // list.add("ssas合肥多久多久啊肯德基啊空间空间");
            // Bitmap bm1 = BitMapUtil.creatContentBitmap(list, 25 * 8,
            // 15 * 8, 28f, 30f, 5.0f, 0);
            // Bitmap bm2 = QrCodeUtil.createQRCode("123456", 120, 120,
            // 0,
            // 0);
            // Bitmap bm = BitMapUtil.combineBitmap(bm1, bm2,
            // BitMapUtil.VERTICAL, 40, 0, 10, 90);
            //
            // Bitmap bm11 = BitMapUtil.creatContentBitmap(list, 25 * 8,
            // 15 * 8, 28f, 30f, 5.0f, 0);
            // Bitmap bm21 = QrCodeUtil.createQRCode(
            // "123456123456123456123456", 120, 120, 0, 0);
            // Bitmap bm111 = BitMapUtil.combineBitmap(bm11, bm21,
            // BitMapUtil.VERTICAL, 40, 0, 10, 90);
            // Bitmap bmZ = BitMapUtil.combineBitmap(bm, bm111,
            // BitMapUtil.VERTICAL, 0, 0, 45, 0);
            // imageView.setImageBitmap(bmZ);
            //
            // HashMap<String, String> map = new HashMap<String,
            // String>();
            // map.put("NAME", "合肥市卫生管理中心01F机房");
            // map.put("QRCODE", "123456123456123456123456");
            // map.put("PRINTCOUNT", "1");
            // print.PrintResources(ResourcesType.WY, map);

            /**
             * ODF子框、综合架-空开
             */
            // List<String> list = new ArrayList<String>();
            // list.add("总开关（主）");
            // Bitmap bm1 = BitMapUtil.creatContentBitmap(list, 13 * 8,
            // 18 * 8, 28f, 30f, 5.0f, 0);
            // Bitmap bm11 = BitMapUtil.creatContentBitmap(list, 13 * 8,
            // 18 * 8, 28f, 30f, 5.0f, 0);
            // Bitmap bmZ = BitMapUtil.combineBitmap(bm1, bm11,
            // BitMapUtil.HORIZONTAL, 0, 0, 35, 0);
            // imageView.setImageBitmap(bmZ);
            //
            // HashMap<String, String> map = new HashMap<String,
            // String>();
            // map.put("NAME", "总开关（主）");
            // map.put("PRINTCOUNT", "1");
            // print.PrintResources(ResourcesType.ODF, map);

            /**
             * 传输专业尾纤
             */
            // List<String> list = new ArrayList<String>();
            // list.add("NAME:你的风度的火箭队呵呵");
            // list.add("BUSINESS:insisn却悄悄的死去的地位");
            // Bitmap bm1 = BitMapUtil.creatContentBitmap(list, 35 * 8,
            // 12 * 8, 20f, 30f, 5.0f, 0);
            //
            // list.clear();
            // list.add("FROM:ssssssssqsqsqsqsqs");
            // list.add("TO:sssannvmvmmvmvmvmvm");
            // Bitmap bm11 = BitMapUtil.creatContentBitmap(list, 35 * 8,
            // 12 * 8, 20f, 30f, 5.0f, 180);
            // Bitmap bmZ = BitMapUtil.combineBitmap(bm1, bm11,
            // BitMapUtil.VERTICAL, 0, 0, 10, 270);
            // imageView.setImageBitmap(bmZ);
            //
            // HashMap<String, String> map = new HashMap<String,
            // String>();
            // map.put("NAME", "NAME:合肥市卫生管理中心01F机房ewwewewew");
            // map.put("BUSINESS", "BUSINESS:合肥市卫生管理中心01F机房wewewewew");
            // map.put("FROM", "FROM:合肥市卫生管理中心01F机房");
            // map.put("TO", "TO:合肥市卫生管理中心01F机房");
            // map.put("PRINTCOUNT", "1");
            // print.PrintResources(ResourcesType.CSWQ, map);

            /**
             * 传输专业2M线
             */
            // List<String> list = new ArrayList<String>();
            // list.add("NAME:你的风度的火箭队呵呵");
            // Bitmap bm1 = BitMapUtil.creatContentBitmap(list, 35 * 8,
            // 12 * 8, 28f, 30f, 5.0f, 0);
            //
            // list.clear();
            // list.add("BUSINESS:insisn却悄悄的死去的地位");
            // Bitmap bm11 = BitMapUtil.creatContentBitmap(list, 35 * 8,
            // 12 * 8, 28f, 30f, 5.0f, 180);
            // Bitmap bmZ = BitMapUtil.combineBitmap(bm1, bm11,
            // BitMapUtil.VERTICAL, 0, 0, 10, 270);
            // imageView.setImageBitmap(bmZ);
            //
            // HashMap<String, String> map = new HashMap<String,
            // String>();
            // map.put("NAME", "NAME:合肥市卫生管理中心01F机房ggrggrgggg");
            // map.put("POSITION", "POSITION:合肥市卫生管理中心01F机房ggrggrggg");
            // map.put("PRINTCOUNT", "1");
            // print.PrintResources(ResourcesType.CSWQ2M, map);

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            ViewUtils.showToast(PrintViewActivity.this,msg.obj.toString());
        }
    };
    
    /**
     * 数据转换
     * @param printDataList
     */
    public void reBuildData(List<HvDynamicBean> printDataList){
    	
    	if(dataList == null || dataList.size()<=0){
        	dataList= new ArrayList<HashMap<String, String>>();
        	
            for(HvDynamicBean obj:printDataList){
            	HashMap<String,String> item = new HashMap<String, String>();
            	
            	switch (ctitle.length) {
					case 1:
						Object oj1_1=obj.getData1();
						if(oj1_1!=null){
							item.put(ctitle[0], oj1_1.toString());
						}
						break;
					case 2:
						Object oj2_1=obj.getData1();
						if(oj2_1!=null){
							item.put(ctitle[0], oj2_1.toString());
						}
						Object oj2_2=obj.getData2();
						if(oj2_2!=null){
							item.put(ctitle[1], oj2_2.toString());
						}
						break;	
					case 3:
						Object oj3_1=obj.getData1();
						if(oj3_1!=null){
							item.put(ctitle[0], oj3_1.toString());
						}
						Object oj3_2=obj.getData2();
						if(oj3_2!=null){
							item.put(ctitle[1], oj3_2.toString());
						}
						Object oj3_3=obj.getData3();
						if(oj3_3!=null){
							item.put(ctitle[2], oj3_3.toString());
						}
						break;	
					case 4:
						Object oj4_1=obj.getData1();
						if(oj4_1!=null){
							item.put(ctitle[0], oj4_1.toString());
						}
						Object oj4_2=obj.getData2();
						if(oj4_2!=null){
							item.put(ctitle[1], oj4_2.toString());
						}
						Object oj4_3=obj.getData3();
						if(oj4_3!=null){
							item.put(ctitle[2], oj4_3.toString());
						}
						Object oj4_4=obj.getData4();
						if(oj4_4!=null){
							item.put(ctitle[3], oj4_4.toString());
						}
						break;	
					case 5:
						Object oj5_1=obj.getData1();
						if(oj5_1!=null){
							item.put(ctitle[0], oj5_1.toString());
						}
						Object oj5_2=obj.getData2();
						if(oj5_2!=null){
							item.put(ctitle[1], oj5_2.toString());
						}
						Object oj5_3=obj.getData3();
						if(oj5_3!=null){
							item.put(ctitle[2], oj5_3.toString());
						}
						Object oj5_4=obj.getData4();
						if(oj5_4!=null){
							item.put(ctitle[3], oj5_4.toString());
						}
						Object oj5_5=obj.getData5();
						if(oj5_5!=null){
							item.put(ctitle[4], oj5_5.toString());
						}
						break;
					case 6:
						Object oj6_1=obj.getData1();
						if(oj6_1!=null){
							item.put(ctitle[0], oj6_1.toString());
						}
						Object oj6_2=obj.getData2();
						if(oj6_2!=null){
							item.put(ctitle[1], oj6_2.toString());
						}
						Object oj6_3=obj.getData3();
						if(oj6_3!=null){
							item.put(ctitle[2], oj6_3.toString());
						}
						Object oj6_4=obj.getData4();
						if(oj6_4!=null){
							item.put(ctitle[3], oj6_4.toString());
						}
						Object oj6_5=obj.getData5();
						if(oj6_5!=null){
							item.put(ctitle[4], oj6_5.toString());
						}
						Object oj6_6=obj.getData6();
						if(oj6_6!=null){
							item.put(ctitle[5], oj6_6.toString());
						}
						break;
					case 7:
						Object oj7_1=obj.getData1();
						if(oj7_1!=null){
							item.put(ctitle[0], oj7_1.toString());
						}
						Object oj7_2=obj.getData2();
						if(oj7_2!=null){
							item.put(ctitle[1], oj7_2.toString());
						}
						Object oj7_3=obj.getData3();
						if(oj7_3!=null){
							item.put(ctitle[2], oj7_3.toString());
						}
						Object oj7_4=obj.getData4();
						if(oj7_4!=null){
							item.put(ctitle[3], oj7_4.toString());
						}
						Object oj7_5=obj.getData5();
						if(oj7_5!=null){
							item.put(ctitle[4], oj7_5.toString());
						}
						Object oj7_6=obj.getData6();
						if(oj7_6!=null){
							item.put(ctitle[5], oj7_6.toString());
						}
						Object oj7_7=obj.getData7();
						if(oj7_7!=null){
							item.put(ctitle[6], oj7_7.toString());
						}
						break;
					case 8:
						Object oj8_1=obj.getData1();
						if(oj8_1!=null){
							item.put(ctitle[0], oj8_1.toString());
						}
						Object oj8_2=obj.getData2();
						if(oj8_2!=null){
							item.put(ctitle[1], oj8_2.toString());
						}
						Object oj8_3=obj.getData3();
						if(oj8_3!=null){
							item.put(ctitle[2], oj8_3.toString());
						}
						Object oj8_4=obj.getData4();
						if(oj8_4!=null){
							item.put(ctitle[3], oj8_4.toString());
						}
						Object oj8_5=obj.getData5();
						if(oj8_5!=null){
							item.put(ctitle[4], oj8_5.toString());
						}
						Object oj8_6=obj.getData6();
						if(oj8_6!=null){
							item.put(ctitle[5], oj8_6.toString());
						}
						Object oj8_7=obj.getData7();
						if(oj8_7!=null){
							item.put(ctitle[6], oj8_7.toString());
						}
						Object oj8_8=obj.getData8();
						if(oj8_8!=null){
							item.put(ctitle[7], oj8_8.toString());
						}
						break;
					case 9:
						Object oj9_1=obj.getData1();
						if(oj9_1!=null){
							item.put(ctitle[0], oj9_1.toString());
						}
						Object oj9_2=obj.getData2();
						if(oj9_2!=null){
							item.put(ctitle[1], oj9_2.toString());
						}
						Object oj9_3=obj.getData3();
						if(oj9_3!=null){
							item.put(ctitle[2], oj9_3.toString());
						}
						Object oj9_4=obj.getData4();
						if(oj9_4!=null){
							item.put(ctitle[3], oj9_4.toString());
						}
						Object oj9_5=obj.getData5();
						if(oj9_5!=null){
							item.put(ctitle[4], oj9_5.toString());
						}
						Object oj9_6=obj.getData6();
						if(oj9_6!=null){
							item.put(ctitle[5], oj9_6.toString());
						}
						Object oj9_7=obj.getData7();
						if(oj9_7!=null){
							item.put(ctitle[6], oj9_7.toString());
						}
						Object oj9_8=obj.getData8();
						if(oj9_8!=null){
							item.put(ctitle[7], oj9_8.toString());
						}
						Object oj9_9=obj.getData9();
						if(oj9_9!=null){
							item.put(ctitle[8], oj9_9.toString());
						}
						break;
					case 10:
						Object oj10_1=obj.getData1();
						if(oj10_1!=null){
							item.put(ctitle[0], oj10_1.toString());
						}
						Object oj10_2=obj.getData2();
						if(oj10_2!=null){
							item.put(ctitle[1], oj10_2.toString());
						}
						Object oj10_3=obj.getData3();
						if(oj10_3!=null){
							item.put(ctitle[2], oj10_3.toString());
						}
						Object oj10_4=obj.getData4();
						if(oj10_4!=null){
							item.put(ctitle[3], oj10_4.toString());
						}
						Object oj10_5=obj.getData5();
						if(oj10_5!=null){
							item.put(ctitle[4], oj10_5.toString());
						}
						Object oj10_6=obj.getData6();
						if(oj10_6!=null){
							item.put(ctitle[5], oj10_6.toString());
						}
						Object oj10_7=obj.getData7();
						if(oj10_7!=null){
							item.put(ctitle[6], oj10_7.toString());
						}
						Object oj10_8=obj.getData8();
						if(oj10_8!=null){
							item.put(ctitle[7], oj10_8.toString());
						}
						Object oj10_9=obj.getData9();
						if(oj10_9!=null){
							item.put(ctitle[8], oj10_9.toString());
						}
						Object oj10_10=obj.getData10();
						if(oj10_10!=null){
							item.put(ctitle[9], oj10_10.toString());
						}
						break;
					default:
						break;
				}
                dataList.add(item);        	
            }    
        }
    }
}
