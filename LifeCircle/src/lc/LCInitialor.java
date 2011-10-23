package lc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.TimeZone;

import status.StatusType;

public class LCInitialor
{

	private String split = ",";

	public void initial(String initFile, String lcFile) throws Exception
	{
		// MSISDN,PV,VISIT_DAY,REGISTER_TIME,REAL_FEE,PAID_CHPTCNT,FREE_CHPTCNT
		FileReader fr = new FileReader(initFile);
		BufferedReader reader = new BufferedReader(fr);
		FileWriter fw = new FileWriter(lcFile);
		BufferedWriter writer = new BufferedWriter(fw);
		// FileWriter
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			String[] strArray = line.split(split);
			UserLC lc = new UserLC();
			lc.setId(Long.parseLong(strArray[0]));
			lc.pvAmout = Integer.parseInt(strArray[1]);
			lc.visitAmout = Integer.parseInt(strArray[2]);
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss", java.util.Locale.US);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
			lc.firstDate = sdf.parse(strArray[3]);
			lc.feeAomut = Integer.parseInt(strArray[4]);
			lc.paidChptAmout = Integer.parseInt(strArray[5]);
			lc.feeAomut = Integer.parseInt(strArray[6]);
			lc.setStatus(StatusType.INITIAl);
			lc.setStatusHis("0");
			writer.write(lc.toString() + "\n");

		}
		writer.flush();
		writer.close();
	}

	public void replace() throws Exception
	{
		FileReader fr = new FileReader(GlobalValue.rootDirectory + "update.txt");
		BufferedReader reader = new BufferedReader(fr);
		FileWriter fw = new FileWriter(GlobalValue.rootDirectory
				+ "update1.txt");
		BufferedWriter writer = new BufferedWriter(fw);
		// FileWriter
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			String line2 = line.replace(",", "|");
			writer.write(line2 + "\n");
		}
		writer.flush();
		writer.close();
	}

	public static void main(String[] args)
	{
		System.out.println("LCInitialor run！！！！！！！！！！！！！！！");
		LCInitialor lcInit = new LCInitialor();
		try
		{
			// lcInit.replace();
			for(int i = 1; i <= 4; i++)
			{
				lcInit.initial(
					GlobalValue.rootDirectory + "init"+ i + ".txt", 
					GlobalValue.rootDirectory + "lc"+ i + ".txt");
			}
			System.out.println("LCInitialor sucess");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getSplit()
	{
		return split;
	}

	public void setSplit(String split)
	{
		this.split = split;
	}

}
