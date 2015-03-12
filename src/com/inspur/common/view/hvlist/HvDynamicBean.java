package com.inspur.common.view.hvlist;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/18 0018.
 * 作为横向LISTVIEW的专用bean元素
 */
public class HvDynamicBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public final static String[] METHOD={"getData1","getData2","getData3","getData4","getData5","getData6","getData7","getData8","getData9","getData10"};
	private String int_id;
    private String zh_label;
    private String city;
    private String county;

    private String data1;
    private String data2;
    private String data3;
    private String data4;
    private String data5;
    private String data6;
    private String data7;
    private String data8;
    private String data9;
    private String data10;
    
    private String[] methods;

    public HvDynamicBean() {
		super();
	}
    
    public HvDynamicBean(String[] array) {
		super();
		//[\"BTS\",\"巫山聚合ZG1\",null,null,null,null,null,\"116424030220000440293\"]
		int length=array.length;
		this.methods=new String[length];
		for(int i=0;i<length;i++){
			this.methods[i]=METHOD[i];
		}
		switch (length) {
		case 1:
			this.data1=array[0];
			
			break;
		case 2:
			this.data1=array[0];
			this.data2=array[1];
			break;
		case 3:
			this.data1=array[0];
			this.data2=array[1];
			this.data3=array[2];
			break;
		case 4:
			this.data1=array[0];
			this.data2=array[1];
			this.data3=array[2];
			this.data4=array[3];
			break;
		case 5:
			this.data1=array[0];
			this.data2=array[1];
			this.data3=array[2];
			this.data4=array[3];
			this.data5=array[4];
			break;
		case 6:
			this.data1=array[0];
			this.data2=array[1];
			this.data3=array[2];
			this.data4=array[3];
			this.data5=array[4];
			this.data6=array[5];
			break;
		case 7:
			this.data1=array[0];
			this.data2=array[1];
			this.data3=array[2];
			this.data4=array[3];
			this.data5=array[4];
			this.data6=array[5];
			this.data7=array[6];
			break;
		case 8:
			this.data1=array[0];
			this.data2=array[1];
			this.data3=array[2];
			this.data4=array[3];
			this.data5=array[4];
			this.data6=array[5];
			this.data7=array[6];
			this.data8=array[7];
		break;
		case 9:
			this.data1=array[0];
			this.data2=array[1];
			this.data3=array[2];
			this.data4=array[3];
			this.data5=array[4];
			this.data6=array[5];
			this.data7=array[6];
			this.data8=array[7];
			this.data9=array[8];
			break;
		case 10:
			this.data1=array[0];
			this.data2=array[1];
			this.data3=array[2];
			this.data4=array[3];
			this.data5=array[4];
			this.data6=array[5];
			this.data7=array[6];
			this.data8=array[7];
			this.data10=array[9];
			break;	
		default:
			break;
		}
		
	}

	public String getInt_id() {
        return int_id;
    }

    public void setInt_id(String int_id) {
        this.int_id = int_id;
    }

    public String getZh_label() {
        return zh_label;
    }

    public void setZh_label(String zh_label) {
        this.zh_label = zh_label;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public String getData5() {
        return data5;
    }

    public void setData5(String data5) {
        this.data5 = data5;
    }

	public String[] getMethods() {
		return methods;
	}

	public void setMethods(String[] methods) {
		this.methods = methods;
	}

	public String getData6() {
		return data6;
	}

	public void setData6(String data6) {
		this.data6 = data6;
	}

	public String getData7() {
		return data7;
	}

	public void setData7(String data7) {
		this.data7 = data7;
	}

	public String getData8() {
		return data8;
	}

	public void setData8(String data8) {
		this.data8 = data8;
	}

	public String getData9() {
		return data9;
	}

	public void setData9(String data9) {
		this.data9 = data9;
	}

	public String getData10() {
		return data10;
	}

	public void setData10(String data10) {
		this.data10 = data10;
	}
    
}
