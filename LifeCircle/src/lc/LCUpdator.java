package lc;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import status.StatusType;
import status.Transfer;


public class LCUpdator {
	
	private String LCTableFile=null;
	private String inputFile=null;
	private String outputFile=null;
	private Hashtable UDITable=new Hashtable();
	private Date updateDate=new Date();
	

	public UserLC update(UserLC lc,UserDayInfo info){
		long interval=0;
		try{
			interval=(info.getVisitDate().getTime()-lc.getPreVisitDate().getTime())/(24*3600000);
		}catch(Exception e){
			interval=0;
		}
		if(interval<0){
			interval=0;
		}
		lc.interval3=lc.interval2;
		lc.interval2=lc.interval1;
		lc.interval1=(int)interval;
		if(interval>lc.maxInterval){
			lc.maxInterval=(int)interval;
		}
		if(info.isNew()){
			lc.firstDate=info.getVisitDate();
		}
		lc.preVisitDate=info.getVisitDate();
		lc.visitAmout++;
		//update pv
		int pvIncr=info.getPvCnt()-lc.getPrePvCnt();
		lc.pvIncre3=lc.pvIncre2;
		lc.pvIncre2=lc.pvIncre1;
		lc.pvIncre1=pvIncr;
		lc.pvAmout+=info.getPvCnt();
		lc.prePvCnt=info.getPvCnt();
		//update fee
		if(info.getFee()>0){
			lc.feeAomut+=info.getFee();
			lc.preFee=info.getFee();
			lc.preFeeDate=info.getVisitDate();
		}
		//update read paid chapter
		{
			int chapIncre=info.getPaidChapterCnt()-lc.getPrePaidChptCnt();
			lc.paidChptIncre3=lc.paidChptIncre2;
			lc.paidChptIncre2=lc.paidChptIncre1;
			lc.paidChptIncre1=chapIncre;
			lc.prePaidChptCnt=info.getPaidChapterCnt();
			lc.paidChptAmout+=info.getPaidChapterCnt();
			
		}
		//update read free chapter
		{
			int chapIncre=info.getFreeChapterCnt()-lc.getPreFreeChptCnt();
			lc.freeChptIncre3=lc.freeChptIncre2;
			lc.freeChptIncre2=lc.freeChptIncre1;
			lc.freeChptIncre1=chapIncre;
			lc.preFreeChptCnt=info.getFreeChapterCnt();
			lc.freeChptAmout+=info.getFreeChapterCnt();
			
		}
		//update download
		
		lc.preDLCnt=info.getDownloadCnt();
		//update status
		byte preStatus=lc.getStatus();
		byte curStatus=Transfer.getStatus(lc);
		if(preStatus!=curStatus){
			
			lc.setChange(true);
			lc.preStatus3=lc.preStatus2;
			lc.preStatus2=lc.preStatus1;
			lc.preStatus1=lc.status;
			lc.setStatus(curStatus);
			if(lc.statusHis.length()<200){
				lc.statusHis=lc.status+GlobalValue.STATUSEP+lc.statusHis;
			}else{
				lc.statusHis=lc.statusHis.substring(0, 197);
			}
			
		}
		lc.setChange(false);
		return lc;
		
	}
	public void checkLapsed(UserLC lc){
		long interval=(this.updateDate.getTime()-lc.getPreVisitDate().getTime())/(24*3600000);
		if(interval>GlobalValue.lapsedInterval){
			lc.setStatus(StatusType.LAPSED);
		}	
	}
	//test the whole life
	public void test(){
		
		Transfer.initial();
		String lcStr="15988723544|2011-10-9 10:20:32|2011-10-17 14:50:14|0|0|0|2011-10-17 14:50:14|0|0|1|4|2|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|false|0";
		UserLC lc=new UserLC(lcStr);
		
		UserDayInfo info=new UserDayInfo("15988723544|20111019|F|20|0|0|5|0");
		update(lc,info);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info1=new UserDayInfo("15988723544|20111023|F|100|10|5|0|0");
		update(lc,info1);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info2=new UserDayInfo("15988723544|20111026|F|20|0|0|0|0");
		update(lc,info2);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info3=new UserDayInfo("15988723544|20111026|F|20|10|3|0|0");
		update(lc,info3);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info4=new UserDayInfo("15988723544|20111026|F|20|10|6|0|0");
		update(lc,info4);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info5=new UserDayInfo("15988723544|20111026|F|20|10|9|0|0");
		update(lc,info5);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info6=new UserDayInfo("15988723544|20111026|F|20|10|6|0|0");
		update(lc,info6);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info7=new UserDayInfo("15988723544|20111026|F|20|10|3|0|0");
		update(lc,info7);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info8=new UserDayInfo("15988723544|20111029|F|6|0|0|0|0");
		update(lc,info8);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info9=new UserDayInfo("15988723544|20111105|F|3|0|0|0|0");
		update(lc,info9);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info10=new UserDayInfo("15988723544|20111115|F|1|0|0|0|0");
		update(lc,info10);
		System.out.println(lc.getStatusHis());
		
		UserDayInfo info11=new UserDayInfo("15988723544|20111115|F|100|0|0|0|0");
		update(lc,info11);
		System.out.println(lc.getStatusHis());
	}
	public void run()throws Exception{
		
		
		Transfer.initial();
		FileReader fr=new FileReader(this.inputFile);
		BufferedReader reader=new BufferedReader(fr);
		String line=null;
		while((line=reader.readLine())!=null){
			UserDayInfo u=new UserDayInfo(line);
			this.UDITable.put(Long.parseLong(line.split(GlobalValue.DATASEP)[0]), u);
		}
		
		FileReader fr2=new FileReader(this.LCTableFile);
		BufferedReader reader2=new BufferedReader(fr2);
		FileWriter fw=new FileWriter(this.outputFile);
		BufferedWriter writer=new BufferedWriter(fw);
		String line2=null;
		//
		while((line2=reader2.readLine())!=null){
			UserLC lc=new UserLC(line2);
			UserDayInfo u=(UserDayInfo)this.UDITable.get(lc.getId());
			if(u!=null){
				this.update(lc, u);
			}else{
				this.checkLapsed(lc);
			}
			//this.UDITable.put(lc.getId(), lc);
			writer.write(lc.toString()+"\n");
		}
		writer.flush();
		writer.close();
	
		/*FileOutputStream fOut = new FileOutputStream(this.LCTableFile);
		fOut.close();
		System.out.println("LCUpadator run！！！！！！！！！！！！！！！");*/
	}
	public static void main(String[] args) {
		
		

		System.out.println("LCUpadator run！！！！！！！！！！！！！！！");
		
		
		

		LCUpdator lc=new LCUpdator();
		lc.test();
//		try{
//			lc.setLCTableFile("E:/data/datas/lc.txt");
//			lc.setInputFile("E:/data/datas/update2.txt");
//			lc.setOutputFile("E:/data/datas/lcout.txt");
//			lc.run();
//		}catch(Exception e){
//			e.printStackTrace();
//		}

		
		// //////////2011-06-04 06:35:50 时间格式
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss", java.util.Locale.US);
		//sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
		try {
			java.util.Date d = sdf.parse("2011-06-04 6:35:50");
			java.util.Date d2 = sdf.parse("2011-06-05 6:35:50");
			//System.out.print((d2.getTime()-d.getTime())/(24*3600000));
			Date d1=new Date();
			System.out.print(d1.toLocaleString());
		
		} catch (ParseException e) {
			

		}
		
	}
	public String getLCTableFile() {
		return LCTableFile;
	}
	public void setLCTableFile(String tableFile) {
		LCTableFile = tableFile;
	}
	public String getInputFile() {
		return inputFile;
	}
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	public String getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}



}
