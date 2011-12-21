import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import lc.GlobalValue;


public class Statistics
{
	static long[][] count = new long[50][9];
	public static void main(String[] args) throws IOException
	{
		
		for(int i = 1; i <= 4; i++)
		{
			BufferedReader br = 
				new BufferedReader(new FileReader(GlobalValue.rootDirectory + "lc" + i + ".txt"));
			String line = null;
			while((line = br.readLine()) != null)
			{
				String arr[] = line.split("\\|");
				String[] his = arr[32].split(",");
				for(int j = 0; j < his.length; j++)
				{
					count[j][Integer.parseInt(his[j]) % 10]++;
				}
			}
			br.close();
		}
		PrintWriter out =
			new PrintWriter(new BufferedWriter(new FileWriter(GlobalValue.rootDirectory+"statis.txt")));
		for(int i = 0; i < 32; i++)
		{
			for(int j = 0; j < 8; j++)
				out.print(count[i][j] + " ");
			out.println();
		}
		out.close();
				
	}

}
