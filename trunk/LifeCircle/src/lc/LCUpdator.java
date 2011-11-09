package lc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import status.StatusType;
import status.Transfer;

public class LCUpdator
{

	private String LCTableFile = null;
	private String inputFile = null;
	private String outputFile = null;
	private Hashtable UDITable = new Hashtable();
	private Date updateDate = null;
	private ArrayList<UserLC> newUserList = new ArrayList();

	public UserLC update(UserLC lc, UserDayInfo info)
	{
		long interval = 0;
		try
		{
			interval = (info.getVisitDate().getTime() - lc.getPreVisitDate()
					.getTime())
					/ (24 * 3600000);
		} catch (Exception e)
		{
			interval = 0;
		}
		if (interval < 0)
		{
			interval = 0;
		}
		lc.interval3 = lc.interval2;
		lc.interval2 = lc.interval1;
		lc.interval1 = (int) interval;
		if (interval > lc.maxInterval)
		{
			lc.maxInterval = (int) interval;
		}
		if (info.isNew())
		{
			lc.firstDate = info.getVisitDate();
		}
		lc.preVisitDate = info.getVisitDate();
		lc.visitAmout++;
		// update pv
		int pvIncr = info.getPvCnt() - lc.getPrePvCnt();
		lc.pvIncre3 = lc.pvIncre2;
		lc.pvIncre2 = lc.pvIncre1;
		lc.pvIncre1 = pvIncr;
		lc.pvAmout += info.getPvCnt();
		lc.prePvCnt = info.getPvCnt();
		// update fee
		if (info.getFee() > 0)
		{
			lc.feeAomut += info.getFee();
			lc.preFee = info.getFee();
			lc.preFeeDate = info.getVisitDate();
		}
		// update read paid chapter
		{
			int chapIncre = info.getPaidChapterCnt() - lc.getPrePaidChptCnt();
			lc.paidChptIncre3 = lc.paidChptIncre2;
			lc.paidChptIncre2 = lc.paidChptIncre1;
			lc.paidChptIncre1 = chapIncre;
			lc.prePaidChptCnt = info.getPaidChapterCnt();
			lc.paidChptAmout += info.getPaidChapterCnt();

		}
		// update read free chapter
		{
			int chapIncre = info.getFreeChapterCnt() - lc.getPreFreeChptCnt();
			lc.freeChptIncre3 = lc.freeChptIncre2;
			lc.freeChptIncre2 = lc.freeChptIncre1;
			lc.freeChptIncre1 = chapIncre;
			lc.preFreeChptCnt = info.getFreeChapterCnt();
			lc.freeChptAmout += info.getFreeChapterCnt();

		}
		// update download

		lc.preDLCnt = info.getDownloadCnt();
		// update status
		byte preStatus = lc.getStatus();
		byte curStatus = Transfer.getStatus(lc);
		if (preStatus != curStatus)
		{

			lc.setChange(true);
			lc.preStatus3 = lc.preStatus2;
			lc.preStatus2 = lc.preStatus1;
			lc.preStatus1 = lc.status;
			lc.setStatus(curStatus);
		}
		else
			lc.setChange(false);
		if (lc.statusHis.length() >= 200)
		{
			lc.statusHis = lc.statusHis.substring(0, 197);				
		}
		lc.statusHis = lc.status + GlobalValue.STATUSEP + lc.statusHis;
		return lc;

	}

	public void checkLapsed(UserLC lc)
	{
		long interval = (this.updateDate.getTime() - lc.getPreVisitDate()
				.getTime())
				/ (24 * 3600000);
		if (interval > GlobalValue.lapsedInterval)
		{
			lc.setStatus(StatusType.LAPSED);
		}
		if (lc.statusHis.length() >= 200)
		{
			lc.statusHis = lc.statusHis.substring(0, 197);				
		}
		lc.statusHis = "" + GlobalValue.STATUSEP + lc.statusHis;
	}

	// test the whole life
	public void test()
	{

		Transfer.initial();
		String lcStr = "15988723544|2011-10-9 10:20:32|2011-10-17 14:50:14|0|0|0|2011-10-17 14:50:14|0|0|1|4|2|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|false|0";
		UserLC lc = new UserLC(lcStr);

		UserDayInfo info = new UserDayInfo("15988723544|20111019|F|20|0|0|5|0");
		update(lc, info);
		System.out.println(lc.getStatusHis());

		UserDayInfo info1 = new UserDayInfo(
				"15988723544|20111023|F|100|10|5|0|0");
		update(lc, info1);
		System.out.println(lc.getStatusHis());

		UserDayInfo info2 = new UserDayInfo("15988723544|20111026|F|20|0|0|0|0");
		update(lc, info2);
		System.out.println(lc.getStatusHis());

		UserDayInfo info3 = new UserDayInfo(
				"15988723544|20111026|F|20|10|3|0|0");
		update(lc, info3);
		System.out.println(lc.getStatusHis());

		UserDayInfo info4 = new UserDayInfo(
				"15988723544|20111026|F|20|10|6|0|0");
		update(lc, info4);
		System.out.println(lc.getStatusHis());

		UserDayInfo info5 = new UserDayInfo(
				"15988723544|20111026|F|20|10|9|0|0");
		update(lc, info5);
		System.out.println(lc.getStatusHis());

		UserDayInfo info6 = new UserDayInfo(
				"15988723544|20111026|F|20|10|6|0|0");
		update(lc, info6);
		System.out.println(lc.getStatusHis());

		UserDayInfo info7 = new UserDayInfo(
				"15988723544|20111026|F|20|10|3|0|0");
		update(lc, info7);
		System.out.println(lc.getStatusHis());

		UserDayInfo info8 = new UserDayInfo("15988723544|20111029|F|6|0|0|0|0");
		update(lc, info8);
		System.out.println(lc.getStatusHis());

		UserDayInfo info9 = new UserDayInfo("15988723544|20111105|F|3|0|0|0|0");
		update(lc, info9);
		System.out.println(lc.getStatusHis());

		UserDayInfo info10 = new UserDayInfo("15988723544|20111115|F|1|0|0|0|0");
		update(lc, info10);
		System.out.println(lc.getStatusHis());

		UserDayInfo info11 = new UserDayInfo(
				"15988723544|20111115|F|100|0|0|0|0");
		update(lc, info11);
		System.out.println(lc.getStatusHis());
	}

	public void run() throws Exception
	{

		Transfer.initial();
		FileReader fr = new FileReader(this.inputFile);
		BufferedReader reader = new BufferedReader(fr);
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			UserDayInfo u = new UserDayInfo(line);
			// A new user should be processed specially.
			if ("T".equals(line.split(GlobalValue.DATASEP)[2]))
				processNewUser(line);
			else
			{
				this.UDITable.put(Long.parseLong(line
						.split(GlobalValue.DATASEP)[0]), u);
			}
		}
		//None of the old users has visit record  in one day is too rare to happen.Thus we 
		//neglect the probability of NullPoint Exception... 
		updateDate = ((UserDayInfo)(UDITable.values().toArray()[0])).getVisitDate();
		
		FileReader fr2 = new FileReader(this.LCTableFile);
		BufferedReader reader2 = new BufferedReader(fr2);
		FileWriter fw = new FileWriter(this.outputFile);
		BufferedWriter writer = new BufferedWriter(fw);
		String line2 = null;
		//
		while ((line2 = reader2.readLine()) != null)
		{
			UserLC lc = new UserLC(line2);
			UserDayInfo u = (UserDayInfo) this.UDITable.get(lc.getId());
			if (u != null)
			{
				this.update(lc, u);
			} else
			{
				this.checkLapsed(lc);
			}
			// this.UDITable.put(lc.getId(), lc);
			writer.write(lc.toString() + "\n");
		}
		for(UserLC lc : newUserList)
		{
			writer.write(lc.toString() + "\n");
		}
		writer.flush();
		writer.close();
		fr.close();
		fr2.close();

		/*
		 * FileOutputStream fOut = new FileOutputStream(this.LCTableFile);
		 * fOut.close(); System.out.println("LCUpadator run！！！！！！！！！！！！！！！");
		 */
	}

	/**
	 * To form an new LC record when come across a new user.The record will be
	 * appended to the LCTable afterwards.
	 * 
	 * @param line represents one line in update.txt
	 */
	public void processNewUser(String line)
	{
		UserLC newUser = new UserLC();
		try
		{
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
					"yyyyMMdd", java.util.Locale.US);
			String[] s = line.split(GlobalValue.DATASEP);
			long msisdn = Long.parseLong(s[0]);
			Date record_day = sdf.parse(s[1]);
			int pv = Integer.parseInt(s[3]);
			int real_fee = Integer.parseInt(s[4]);
			int paid_cnt = Integer.parseInt(s[5]);
			int free_cnt = Integer.parseInt(s[6]);
			int download_cnt = Integer.parseInt(s[7]);
			
			newUser.setId(msisdn);
			newUser.setFirstDate(record_day);
			newUser.setPreVisitDate(record_day);
			newUser.setPrePvCnt(pv);
			newUser.setPrePaidChptCnt(paid_cnt);
			newUser.setPreFreeChptCnt(free_cnt);
			newUser.setPreFeeDate(record_day);
			newUser.setPreFee(real_fee);
			newUser.setPreDLCnt(download_cnt);
			newUser.setVisitAmout(1);
			newUser.setPvAmout(pv);
			newUser.setPaidChptAmout(paid_cnt);
			newUser.setFreeChptAmout(free_cnt);
			newUser.setFeeAomut(real_fee);
			newUser.setStatus(StatusType.INITIAl);
			newUser.setStatusHis("0");
			
//			lc.setId(Long.parseLong(strArray[0]));
//			lc.pvAmout=Integer.parseInt(strArray[1]);
//			lc.visitAmout=Integer.parseInt(strArray[2]);
//			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
//					"yyyy-MM-dd hh:mm:ss", java.util.Locale.US);
//			sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
//			lc.firstDate=sdf.parse(strArray[3]);
//			lc.feeAomut=Integer.parseInt(strArray[4]);
//			lc.paidChptAmout=Integer.parseInt(strArray[5]);
//			lc.feeAomut=Integer.parseInt(strArray[6]);
//			lc.setStatus(StatusType.INITIAl);
//			lc.setStatusHis("0");
			
		} catch (Exception e)
		{
			e.printStackTrace();
			newUser.setId(-1);
		}
		newUserList.add(newUser);
	}

	public static void main(String[] args)
	{
		int num = Integer.parseInt(args[0]);
		System.out.println("LCUpadator NO." + num + " starts!");
		LCUpdator lc = new LCUpdator();
		try
		{
			String lcFile = GlobalValue.rootDirectory + "lc" + num + ".txt";
			String inputFile = GlobalValue.rootDirectory + "update" + num + ".txt";
			String outputFile = GlobalValue.rootDirectory + "lc" + num + ".tmp";
			lc.setLCTableFile(lcFile);
			lc.setInputFile(inputFile);
			lc.setOutputFile(outputFile);
			lc.run();
			if(new File(lcFile).exists())
				new File(lcFile).delete();
			new File(outputFile).renameTo(new File(lcFile));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("LCUpadator NO." + num + " ends!");
	}

	public String getLCTableFile()
	{
		return LCTableFile;
	}

	public void setLCTableFile(String tableFile)
	{
		LCTableFile = tableFile;
	}

	public String getInputFile()
	{
		return inputFile;
	}

	public void setInputFile(String inputFile)
	{
		this.inputFile = inputFile;
	}

	public String getOutputFile()
	{
		return outputFile;
	}

	public void setOutputFile(String outputFile)
	{
		this.outputFile = outputFile;
	}

}
