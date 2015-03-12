package com.inspur.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

public class UserInfo

implements Serializable, Cloneable {
	private static final long serialVersionUID = -785397946L;
	private int userid = 0;
	private int empid = 0;
	private String empname;
	private Integer companyid = null;
	private String compname;
	private String unitname;
	private String useraccount = null;
	private String password = null;
	private int groupid = 0;
	private String accessdate = null;
	private String access_time_a = null;
	private String access_time_z = null;
	private int locked = 0;
	private int createman = 0;
	private String createmanaccount;
	private String createtime = null;
	private String destroytime = null;
	private String remark = null;
	private String updatetime = null;
	private String managerlistname = null;
	private Object authoritytree;
	private String telephoneNumber;
	private String title;
	private String mail;
	private String mobile;
	private int systemflag;
	private int logid;
	private int unitid;
	private int locktype;
	private String locktime = "";

	public String toString() {
		Iterator iter;
		StringBuffer sbResult = new StringBuffer();

		sbResult.append("userid=" + this.userid + ";");
		sbResult.append("empid=" + this.empid + ";");

		sbResult.append("empname=" + this.empname + ";");
		sbResult.append("compname=" + this.compname + ";");
		sbResult.append("unitname=" + this.unitname + ";");

		sbResult.append("useraccount=" + this.useraccount + ";");
		sbResult.append("password=" + this.password + ";");
		sbResult.append("logid=" + this.logid + ";");
		sbResult.append("groupid=" + this.groupid + ";");
		sbResult.append("accessdate=" + this.accessdate + ";");
		sbResult.append("access_time_a=" + this.access_time_a + ";");
		sbResult.append("access_time_z=" + this.access_time_z + ";");
		sbResult.append("locked=" + this.locked + ";");
		sbResult.append("locktype=" + this.locktype + ";");
		sbResult.append("locktime=" + this.locktime + ";");
		sbResult.append("createman=" + this.createman + ";");
		sbResult.append("createmanaccount=" + this.createmanaccount + ";");
		sbResult.append("createtime=" + this.createtime + ";");
		sbResult.append("destroytime=" + this.destroytime + ";");
		sbResult.append("remark=" + this.remark + ";");
		sbResult.append("updatetime=" + this.updatetime + ";");
		sbResult.append("mobile=" + this.mobile + ";");
		sbResult.append("telephoneNumber=" + this.telephoneNumber + ";");
		sbResult.append("title=" + this.title + ";");
		sbResult.append("mail=" + this.mail + ";");
		sbResult.append("systemflag=" + this.systemflag + ";");

		sbResult.append("grpmember={");
		return sbResult.toString();
	}

	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
	}

	public int getUserid() {
		return this.userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getEmpid() {
		return this.empid;
	}

	public void setEmpid(int empid) {
		this.empid = empid;
	}

	public String getUseraccount() {
		return this.useraccount;
	}

	public void setUseraccount(String useraccount) {
		this.useraccount = useraccount;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getGroupid() {
		return this.groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public String getAccessdate() {
		return this.accessdate;
	}

	public void setAccessdate(String accessdate) {
		this.accessdate = accessdate;
	}

	public int getLocked() {
		return this.locked;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public int getCreateman() {
		return this.createman;
	}

	public void setCreateman(int createman) {
		this.createman = createman;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getManagerlistname() {
		return this.managerlistname;
	}

	public void setManagerlistname(String managerlistname) {
		this.managerlistname = managerlistname;
	}

	public String getEmpname() {
		return this.empname;
	}

	public void setEmpname(String empname) {
		this.empname = empname;
	}

	public String getCompname() {
		return this.compname;
	}

	public void setCompname(String compname) {
		this.compname = compname;
	}

	public String getUnitname() {
		return this.unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getCreatemanaccount() {
		return this.createmanaccount;
	}

	public void setCreatemanaccount(String createmanaccount) {
		this.createmanaccount = createmanaccount;
	}

	public Object getAuthoritytree() {
		return this.authoritytree;
	}

	public void setAuthoritytree(Object authoritytree) {
		this.authoritytree = authoritytree;
	}

	public Integer getCompanyid() {
		return this.companyid;
	}

	public String getMail() {
		return this.mail;
	}

	public String getTitle() {
		return this.title;
	}

	public String getTelephoneNumber() {
		return this.telephoneNumber;
	}

	public String getMobile() {
		return this.mobile;
	}

	public int getSystemflag() {
		return this.systemflag;
	}

	public int getLogid() {
		return this.logid;
	}

	public int getUnitid() {
		return this.unitid;
	}

	public int getLocktype() {
		return this.locktype;
	}

	public String getLocktime() {
		return this.locktime;
	}

	public void setCompanyid(Integer companyid) {
		this.companyid = companyid;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setSystemflag(int systemflag) {
		this.systemflag = systemflag;
	}

	public void setLogid(int logid) {
		this.logid = logid;
	}

	public void setUnitid(int unitid) {
		this.unitid = unitid;
	}

	public void setLocktype(int locktype) {
		this.locktype = locktype;
	}

	public void setLocktime(String locktime) {
		this.locktime = locktime;
	}

	public String getAccess_time_a() {
		return access_time_a;
	}

	public void setAccess_time_a(String access_time_a) {
		this.access_time_a = access_time_a;
	}

	public String getAccess_time_z() {
		return access_time_z;
	}

	public void setAccess_time_z(String access_time_z) {
		this.access_time_z = access_time_z;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getDestroytime() {
		return destroytime;
	}

	public void setDestroytime(String destroytime) {
		this.destroytime = destroytime;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	
	
}