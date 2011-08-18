package org.tseg.model;

import java.text.ParseException;
import java.util.TimeZone;

import org.tseg.preprocess.Preprocessor;

public class Visit {

	private int startTime;
	private int endTime;
	private String curPage;
	private String prePage;
	private String nextPage;

	public String toString() {

		long ltime = (long) startTime * 1000;
		java.util.Date dd = new java.util.Date(ltime);
		long ltime1 = (long) endTime * 1000;
		java.util.Date dd1 = new java.util.Date(ltime1);
//		String s = prePage + "->" + curPage + "->" + nextPage + "("
//				+ dd.toLocaleString() + "," + dd1.toLocaleString() + ")";
		this.curPage=Preprocessor.getPageName(this.curPage);
		String s=curPage+"("+dd.toLocaleString()+")";
		return s;
	}

	public Visit(String[] strArray) {
		String curPageName = "";
		if(!strArray[6].equals("$")){
			curPageName += strArray[6] + "/";
		}
		if (!strArray[7].equals("$")) {
			curPageName += strArray[7] + "/";
		}
		if (!strArray[8].equals("$")) {
			curPageName += strArray[8] + "/";
		}
		curPageName += strArray[4];
		setCurPage(curPageName);
		String prePageName = "";
		if(!strArray[13].equals("$")){
			prePageName += strArray[13] + "/";
		}
		if (!strArray[14].equals("$")) {
			prePageName += strArray[14] + "/";

		}
		if (!strArray[15].equals("$")) {
			prePageName += strArray[15]+"/";
		}
		prePageName += strArray[12];
		setPrePage(prePageName);
		String nextPage = "";
		if(!strArray[18].equals("$")){
			nextPage += strArray[18] + "/";
		}
		if (!strArray[19].equals("$")) {
			nextPage += strArray[19] + "/";

		}
		if (!strArray[20].equals("$")) {
			nextPage += strArray[20] + "/";
		}
		nextPage += strArray[17];
		setNextPage(nextPage);

		// //////////2011-06-04 06:35:50 时间格式
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss", java.util.Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
		try {
			java.util.Date d = sdf.parse(strArray[1]);
			setStartTime((int) (d.getTime() / 1000));
		
		} catch (ParseException e) {
			this.startTime = 0;
		
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		try {
			java.util.Date d2 = sdf.parse(strArray[22]);
			setEndTime((int) (d2.getTime() / 1000));
		} catch (ParseException e) {
			this.endTime = 0;
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
	
	public int getDruation(){
		
		if(this.startTime!=0&&this.endTime!=0&&this.endTime>this.startTime){
			return this.endTime-this.startTime;
		}
		return 0;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setCurPage(String curPage) {
		this.curPage = curPage;
	}

	public String getCurPage() {
		return curPage;
	}

	public void setPrePage(String prePage) {
		this.prePage = prePage;
	}

	public String getPrePage() {
		return prePage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}

	public String getNextPage() {
		return nextPage;
	}

}
