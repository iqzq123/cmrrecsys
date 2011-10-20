package lc;
import java.util.Date;


public class UserDayInfo {
	Date visitDate=null;
	boolean isNew=false;
	int pvCnt=0;
	int fee=0;
	int paidChapterCnt=0;
	int freeChapterCnt=0;
	int downloadCnt=0;
	public UserDayInfo(String data) {
		//Msisdn| record_day| if_newuser| pv| real_fee| paidser_chpt_cnt+paidwhole_chpt_cnt|
		//freeser_chpt_cnt+freewhole_chpt_cnt| book_download_cnt
		super();
		// TODO Auto-generated constructor stub
		try{
			String[] s=data.split(GlobalValue.DATASEP);
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
					"yyyyMMdd", java.util.Locale.US);
			this.visitDate=sdf.parse(s[1]);
			//System.out.print(this.visitDate.toLocaleString()+"\n");
			this.isNew=Boolean.parseBoolean(s[2]);
			this.pvCnt=Integer.parseInt(s[3]);
			this.fee=Integer.parseInt(s[4]);
			this.paidChapterCnt=Integer.parseInt(s[5]);
			this.freeChapterCnt=Integer.parseInt(s[6]);
			this.downloadCnt=Integer.parseInt(s[7]);
		}catch(Exception e){
			System.out.println("UserDayInfo input Format eror");
		}
		
		
	}
	public Date getVisitDate() {
		return visitDate;
	}
	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public int getPvCnt() {
		return pvCnt;
	}
	public void setPvCnt(int pvCnt) {
		this.pvCnt = pvCnt;
	}
	public int getFee() {
		return fee;
	}
	public void setFee(int fee) {
		this.fee = fee;
	}
	public int getPaidChapterCnt() {
		return paidChapterCnt;
	}
	public void setPaidChapterCnt(int paidChapterCnt) {
		this.paidChapterCnt = paidChapterCnt;
	}
	public int getFreeChapterCnt() {
		return freeChapterCnt;
	}
	public void setFreeChapterCnt(int freeChapterCnt) {
		this.freeChapterCnt = freeChapterCnt;
	}
	public int getDownloadCnt() {
		return downloadCnt;
	}
	public void setDownloadCnt(int downloadCnt) {
		this.downloadCnt = downloadCnt;
	}

}
