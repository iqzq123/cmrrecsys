package lc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LCRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(null != args && args.length == 1 && "-i".equals(args[0]))
		{
			LCInitialor.main(null);
		}
		else if(null != args && args.length == 1 && "-u".equals(args[0]))
		{
			LCUpdator.main(null);
		}
		else
		{
			System.out.println(
				"Usage: lc [-i] [-u]"+"\n"+
				"Options："+"\n"+
				"    "+"-i"+"\t"+"Initialize the Lifecircle System."+"\n"+
				"    "+"-u"+"\t"+"Update the Lifecircle System."+"\n"
			);
		}
		

	}

}
