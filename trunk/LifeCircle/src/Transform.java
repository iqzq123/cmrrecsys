import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lc.GlobalValue;


public class Transform
{
	public static void main(String[] args) throws IOException, ParseException
	{
		BufferedReader br = 
			new BufferedReader(new FileReader(GlobalValue.rootDirectory + "statis.txt"));
		PrintWriter out =
			new PrintWriter(new BufferedWriter(new FileWriter(GlobalValue.rootDirectory+"report.txt")));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse("20110829");
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd");
		
		String line = null;
		while((line = br.readLine()) != null)
		{
			date.setDate(date.getDate() + 1);
			String[] arr = line.split("\t");
			
			out.print(sdf1.format(date) + " ");
			out.print("认识 ");
			out.println(arr[0]);
			
			out.print(sdf1.format(date) + " ");
			out.print("有好感 ");
			out.println(arr[1]);
			
			out.print(sdf1.format(date) + " ");
			out.print("无好感 ");
			out.println(arr[2]);
			
			out.print(sdf1.format(date) + " ");
			out.print("徘徊 ");
			out.println(arr[3]);
			
			out.print(sdf1.format(date) + " ");
			out.print("确定关系 ");
			out.println(arr[4]);
			
			out.print(sdf1.format(date) + " ");
			out.print("稳定发展 ");
			out.println(arr[5]);
			
			out.print(sdf1.format(date) + " ");
			out.print("迷恋 ");
			out.println(arr[6]);
			
			out.print(sdf1.format(date) + " ");
			out.print("流失 ");
			out.println(arr[7]);
			
		}
		br.close();
		out.close();
	}
}
