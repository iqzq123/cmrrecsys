import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import status.StatusType;


public class A
{
	public static void main(String[] args) throws IOException
	{
//		BufferedReader reader = new BufferedReader(new FileReader("D:\\杭州项目\\10月25日\\life-cycle\\备份\\update1_1.txt"));
//		System.out.println(reader.readLine());
//		reader = new BufferedReader(new FileReader("D:\\杭州项目\\10月25日\\life-cycle\\备份\\update1_2.txt"));
//		System.out.println(reader.readLine());
//		reader = new BufferedReader(new FileReader("D:\\杭州项目\\10月25日\\life-cycle\\备份\\update1_3.txt"));
//		System.out.println(reader.readLine());
		System.out.println(new StringBuilder().append(StatusType.FAVOR).charAt(0));
	}
}
