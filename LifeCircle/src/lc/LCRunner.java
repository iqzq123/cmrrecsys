package lc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LCRunner
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (null != args && args.length == 2 && ("-i".equals(args[0]) || "-u".equals(args[0])) )
		{
			boolean flag = true;
			try
			{
				int i = Integer.parseInt(args[1]);
				if(i <= 0)
					flag = false;
			} catch (Exception e)
			{
				flag = false;
			}
			if(flag)
			{
				if("-i".equals(args[0]))
					LCInitialor.main(new String[]{args[1]});
				else
					LCUpdator.main(new String[]{args[1]});
			}
			else
				System.out.println("Invalid argument.");	
		} 
		else
		{
			System.out.println("Invalid argument.");
			
//			moved to shell or bat
//			System.out.println("Usage: lc [-i] [-u]" + "\n" + "Options：" + "\n"
//					+ "    " + "-i" + "\t"
//					+ "Initialize the Lifecircle System." + "\n" + "    "
//					+ "-u" + "\t" + "Update the Lifecircle System." + "\n");
		}

	}

}
