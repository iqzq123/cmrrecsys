package lc;
import java.util.Date;
import status.*;

public class UserLC {
	long id=0;
	Date firstDate = GlobalValue.initDate;
	Date preVisitDate = GlobalValue.initDate;
	int prePvCnt=0;
	int prePaidChptCnt=0;
	int preFreeChptCnt=0;
	Date preFeeDate = GlobalValue.initDate;
	int preFee=0;
	int preDLCnt=0;
	int visitAmout=0;
	int pvAmout=0;
	int paidChptAmout=0;
	int freeChptAmout=0;
	int feeAomut=0;
	int maxInterval=0;
	int interval1=0;
	int interval2=0;
	int interval3=0;
	int pvIncre1=0;
	int pvIncre2=0;
	int pvIncre3=0;
	int paidChptIncre1=0;
	int paidChptIncre2=0;
	int paidChptIncre3=0;
	int freeChptIncre1=0;
	int freeChptIncre2=0;
	int freeChptIncre3=0;
	byte status=0;
	byte preStatus1=0;
	byte preStatus2=0;
	byte preStatus3=0;
	boolean isChange=false;
	String statusHis="0";
	
	public UserLC(){
		
	}
	public UserLC(String data) {
		super();
		// TODO Auto-generated constructor stub
		try{
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyyMMdd");
		String []s=data.split(GlobalValue.DATASEP);
		this.id=Long.parseLong(s[0]);
		this.firstDate=sdf.parse(s[1]);
		this.preVisitDate=sdf.parse(s[2]);
		this.prePvCnt=Integer.parseInt(s[3]);
		this.prePaidChptCnt=Integer.parseInt(s[4]);
		this.preFreeChptCnt=Integer.parseInt(s[5]);
		this.preFeeDate=sdf.parse(s[6]);
		this.preFee=Integer.parseInt(s[7]);
		this.preDLCnt=Integer.parseInt(s[8]);
		this.visitAmout=Integer.parseInt(s[9]);
		this.pvAmout=Integer.parseInt(s[10]);
		this.paidChptAmout=Integer.parseInt(s[11]);
		this.freeChptAmout=Integer.parseInt(s[12]);
		this.feeAomut=Integer.parseInt(s[13]);
		this.maxInterval=Integer.parseInt(s[14]);
		this.interval1=Integer.parseInt(s[15]);
		this.interval2=Integer.parseInt(s[16]);
		this.interval3=Integer.parseInt(s[17]);
		this.pvIncre1=Integer.parseInt(s[18]);
		this.pvIncre2=Integer.parseInt(s[19]);
		this.pvIncre3=Integer.parseInt(s[20]);
		this.paidChptIncre1=Integer.parseInt(s[21]);
		this.paidChptIncre2=Integer.parseInt(s[22]);
		this.paidChptIncre3=Integer.parseInt(s[23]);
		this.freeChptIncre1=Integer.parseInt(s[24]);
		this.freeChptIncre2=Integer.parseInt(s[25]);
		this.freeChptIncre3=Integer.parseInt(s[26]);
		this.status=Byte.parseByte(s[27]);
		this.preStatus1=Byte.parseByte(s[28]);
		this.preStatus2=Byte.parseByte(s[29]);
		this.preStatus3=Byte.parseByte(s[30]);
		this.isChange=Boolean.parseBoolean(s[31]);
		this.statusHis=s[32];
		
		}catch( Exception e){
			id=-1;
		}
	}
	public String toString(){
		String retStr=new String();
		String s="|";
		java.text.SimpleDateFormat sdf4Ocl = new java.text.SimpleDateFormat(
				"yyyyMMdd", java.util.Locale.US);
		retStr=id+s+sdf4Ocl.format(firstDate)+s+sdf4Ocl.format(preVisitDate)+s+prePvCnt+s+prePaidChptCnt+s+preFreeChptCnt+s+
		sdf4Ocl.format(preFeeDate)+s+preFee+s+preDLCnt+s+visitAmout+s+pvAmout+s+paidChptAmout+s+
		freeChptAmout+s+feeAomut+s+maxInterval+s+interval1+s+interval2+s+interval3+s+pvIncre1+s+
		pvIncre2+s+pvIncre3+s+paidChptIncre1+s+paidChptIncre2+s+paidChptIncre3+s+freeChptIncre1+s+
		freeChptIncre2+s+freeChptIncre3+s+status+s+preStatus1+s+preStatus2+s+preStatus3+s+
		(isChange ? "1" : "0")+s+statusHis;
		return retStr;
	}
	
	
	public Date getPreFeeDate()
	{
		return preFeeDate;
	}
	public void setPreFeeDate(Date preFeeDate)
	{
		this.preFeeDate = preFeeDate;
	}
	public byte getPreStatus2()
	{
		return preStatus2;
	}
	public void setPreStatus2(byte preStatus2)
	{
		this.preStatus2 = preStatus2;
	}
	public byte getPreStatus3()
	{
		return preStatus3;
	}
	public void setPreStatus3(byte preStatus3)
	{
		this.preStatus3 = preStatus3;
	}
	public int getPre3AverPV(){
		int averPV=this.getPrePvCnt();
		int prePV1=this.getPrePvCnt()-this.getPvIncre1();
		int prePV2=prePV1-this.getPvIncre2();
		int prePV3=prePV2-this.getPvIncre3();
		averPV+=prePV1;
		averPV+=prePV2;
		averPV+=prePV3;
		averPV=averPV/4;
		return averPV;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getFirstDate() {
		return firstDate;
	}
	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
	}
	public Date getPreVisitDate() {
		return preVisitDate;
	}
	public void setPreVisitDate(Date preVisitDate) {
		this.preVisitDate = preVisitDate;
	}
	public int getPrePvCnt() {
		return prePvCnt;
	}
	public void setPrePvCnt(int prePvCnt) {
		this.prePvCnt = prePvCnt;
	}

	public int getPrePaidChptCnt() {
		return prePaidChptCnt;
	}
	public void setPrePaidChptCnt(int prePaidChptCnt) {
		this.prePaidChptCnt = prePaidChptCnt;
	}
	public int getPreFreeChptCnt() {
		return preFreeChptCnt;
	}
	public void setPreFreeChptCnt(int preFreeChptCnt) {
		this.preFreeChptCnt = preFreeChptCnt;
	}
	public int getPreFee() {
		return preFee;
	}
	public void setPreFee(int preFee) {
		this.preFee = preFee;
	}
	public int getPreDLCnt() {
		return preDLCnt;
	}
	public void setPreDLCnt(int preDLCnt) {
		this.preDLCnt = preDLCnt;
	}
	public int getVisitAmout() {
		return visitAmout;
	}
	public void setVisitAmout(int visitAmout) {
		this.visitAmout = visitAmout;
	}
	public int getPvAmout() {
		return pvAmout;
	}
	public void setPvAmout(int pvAmout) {
		this.pvAmout = pvAmout;
	}
	public int getPaidChptAmout() {
		return paidChptAmout;
	}
	public void setPaidChptAmout(int paidChptAmout) {
		this.paidChptAmout = paidChptAmout;
	}
	public int getFreeChptAmout() {
		return freeChptAmout;
	}
	public void setFreeChptAmout(int freeChptAmout) {
		this.freeChptAmout = freeChptAmout;
	}
	public int getFeeAomut() {
		return feeAomut;
	}
	public void setFeeAomut(int feeAomut) {
		this.feeAomut = feeAomut;
	}
	public int getMaxInterval() {
		return maxInterval;
	}
	public void setMaxInterval(int maxInterval) {
		this.maxInterval = maxInterval;
	}
	public int getInterval1() {
		return interval1;
	}
	public void setInterval1(int interval1) {
		this.interval1 = interval1;
	}
	public int getInterval2() {
		return interval2;
	}
	public void setInterval2(int interval2) {
		this.interval2 = interval2;
	}
	public int getInterval3() {
		return interval3;
	}
	public void setInterval3(int interval3) {
		this.interval3 = interval3;
	}
	public int getPvIncre1() {
		return pvIncre1;
	}
	public void setPvIncre1(int pvIncre1) {
		this.pvIncre1 = pvIncre1;
	}
	public int getPvIncre2() {
		return pvIncre2;
	}
	public void setPvIncre2(int pvIncre2) {
		this.pvIncre2 = pvIncre2;
	}
	public int getPvIncre3() {
		return pvIncre3;
	}
	public void setPvIncre3(int pvIncre3) {
		this.pvIncre3 = pvIncre3;
	}
	public int getPaidChptIncre1() {
		return paidChptIncre1;
	}
	public void setPaidChptIncre1(int paidChptIncre1) {
		this.paidChptIncre1 = paidChptIncre1;
	}
	public int getPaidChptIncre2() {
		return paidChptIncre2;
	}
	public void setPaidChptIncre2(int paidChptIncre2) {
		this.paidChptIncre2 = paidChptIncre2;
	}
	public int getPaidChptIncre3() {
		return paidChptIncre3;
	}
	public void setPaidChptIncre3(int paidChptIncre3) {
		this.paidChptIncre3 = paidChptIncre3;
	}
	public int getFreeChptIncre1() {
		return freeChptIncre1;
	}
	public void setFreeChptIncre1(int freeChptIncre1) {
		this.freeChptIncre1 = freeChptIncre1;
	}
	public int getFreeChptIncre2() {
		return freeChptIncre2;
	}
	public void setFreeChptIncre2(int freeChptIncre2) {
		this.freeChptIncre2 = freeChptIncre2;
	}
	public int getFreeChptIncre3() {
		return freeChptIncre3;
	}
	public void setFreeChptIncre3(int freeChptIncre3) {
		this.freeChptIncre3 = freeChptIncre3;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public byte getPreStatus1() {
		return preStatus1;
	}
	public void setPreStatus1(byte preStatus1) {
		this.preStatus1 = preStatus1;
	}
	public byte getPreStates2() {
		return preStatus2;
	}
	public void setPreStates2(byte preStates2) {
		this.preStatus2 = preStates2;
	}
	public byte getPreStates3() {
		return preStatus3;
	}
	public void setPreStates3(byte preStates3) {
		this.preStatus3 = preStates3;
	}
	public boolean isChange() {
		return isChange;
	}
	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}
	public String getStatusHis() {
		return statusHis;
	}
	public void setStatusHis(String statusHis) {
		this.statusHis = statusHis;
	}
	
	

}
