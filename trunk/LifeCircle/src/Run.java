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
import lc.LCUpdator;


public class Run
{
	public static void main(String[] args) throws ParseException, IOException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse("20110830");
		String prefix = "d:\\杭州项目\\新数据\\";
//		String prefix = args[0];
		for(int i = 1; i <= 32; i++)
		{
			date.setDate(date.getDate() + 1);
			for(int j = 1; j <= 4; j++)
			{
				BufferedReader br = 
					new BufferedReader(new FileReader(prefix + sdf.format(date) + "\\update" + j +".txt"));
				String line = null;
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(GlobalValue.rootDirectory + "update" + j +".txt")));
				while((line = br.readLine()) != null)
				{
					out.println(line);
				}
				br.close();
				out.close();
				LCUpdator.main(new String[]{"" + j});
			}
		}
	}

}
