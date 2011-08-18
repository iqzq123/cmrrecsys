package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class XMLFileReader{
	public static void main(String args[]) 
    {
		XMLFileReader.readXMLToStr("D:\\tree.xml"); 
    }
	
	public static String readXMLToStr(String fileName){
		String result = "";
		System.out.println("fn:"+fileName);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					new File(fileName)), "UTF-8");
			BufferedReader reader = new BufferedReader(read);
			String str = "";
			while ((str = reader.readLine()) != null) {
				result += str;
			}
			reader.close();
			read.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("hello:::"+result);
		return result;
	}

}
