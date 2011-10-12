package org.tseg.preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.tseg.Ulits.AnalyseType;
import org.tseg.Ulits.Separator;

public class Preprocessor {

	static private HashMap pageMap = new HashMap();
	static private HashMap categoryMap = new HashMap();

	static public boolean CheckUnicodeString(String value)
    {
		
		
    
		for (int i=0; i < value.length(); ++i) {
        if (value.charAt(i) > 0xFFFD)
        {
          // throw new Exception("Invalid Unicode");// 或者直接替换掉0x0
            
            return false;
        }
        else if (value.charAt(i)< 0x20 && value.charAt(i) != '\t' & value.charAt(i) != '\n' &value.charAt(i)!= '\r')
        {
            // throw new Exception("Invalid Xml Characters");// 或者直接替换掉0x													/
        
        	return false;
        }
        
       }
		return true;
    }

	static public String getPageName(String key) {

		if (key.contains(Separator.CATE_SEPARATOR)) {
			String[] keyArray = key.split(Separator.CATE_SEPARATOR);
			String s = "";
			for (int i = 0; i < keyArray.length - 1; i++) {
				s += getCateName(keyArray[i]) + Separator.CATE_SEPARATOR;
			}
			s += getPageName(keyArray[keyArray.length - 1]);
			return s;
		} else {
			String s = (String) pageMap.get(key);
			if (s == null) {
				// /////////针对页面变成目录分析的情况
				s = (String) categoryMap.get(key);
				if (s == null) {
					return key;
				} else {
					return s;
				}
			} else {

				return s;
			}
		}

	}

	static public String getCateName(String key) {

		String s = (String) categoryMap.get(key);
		if (s == null) {
			return key;
		} else {
			return s;
		}
	}

	/**
	 * 把Page替换成一级目录,作为算法的输入 则原来对页面的分析就变化成对目录的分析
	 * 
	 * @param strArray
	 */
	static public void tranPageToCate(String[] strArray) {

		strArray[4] = strArray[6];
		if (!strArray[12].contains("login")) {
			strArray[12] = strArray[13];
		}
		if (!strArray[17].contains("logout")) {
			strArray[17] = strArray[18];
		}

		strArray[6] = "$";
		strArray[7] = "$";
		strArray[8] = "$";
		strArray[9] = "$";
		strArray[13] = "$";
		strArray[14] = "$";
		strArray[15] = "$";
		strArray[16] = "$";
		strArray[18] = "$";
		strArray[19] = "$";
		strArray[20] = "$";
		strArray[21] = "$";

	}

	static public String[] run(String[] logArray, byte type) {

		String[] retArray = new String[logArray.length];
		try {
			Long.parseLong(logArray[0]);
		} catch (NumberFormatException e) {
			logArray[0] = "-1";
		}

		for (int i = 0; i < logArray.length; i++) {
			if (logArray[i].equals("")) {
				logArray[i] = "-1";
			}
			retArray[i] = logArray[i];
		}

		if (type == AnalyseType.NegCate) {

			retArray[6] = "$";
			retArray[7] = "$";
			retArray[8] = "$";
			retArray[9] = "$";
			retArray[13] = "$";
			retArray[14] = "$";
			retArray[15] = "$";
			retArray[16] = "$";
			retArray[18] = "$";
			retArray[19] = "$";
			retArray[20] = "$";
			retArray[21] = "$";
		}
		if (type == AnalyseType.PageToCate) {

			retArray[4] = retArray[6];
			if (!retArray[12].contains("login")) {
				retArray[12] = retArray[13];
			}
			if (!retArray[17].contains("logout")) {
				retArray[17] = retArray[18];
			}

			retArray[6] = "$";
			retArray[7] = "$";
			retArray[8] = "$";
			retArray[9] = "$";
			retArray[13] = "$";
			retArray[14] = "$";
			retArray[15] = "$";
			retArray[16] = "$";
			retArray[18] = "$";
			retArray[19] = "$";
			retArray[20] = "$";
			retArray[21] = "$";

		}
		return retArray;
	}

	static public void readMapFile(String fileName) throws IOException {

		InputStreamReader read = new InputStreamReader(new FileInputStream(
				new File(fileName + "/iread_pagename.txt")), "UTF-8");
		BufferedReader reader = new BufferedReader(read);

		String str;
		reader.readLine();
		while ((str = reader.readLine()) != null) {
			String[] strArray = str.split(Separator.DATA_SEPARATOR);
			pageMap.put(strArray[1], strArray[2]);
		}

		InputStreamReader read1 = new InputStreamReader(new FileInputStream(
				new File(fileName + "/iread_sanode.txt")), "UTF-8");
		BufferedReader reader1 = new BufferedReader(read1);

		String str1;
		reader1.readLine();
		while ((str1 = reader1.readLine()) != null) {
			String[] strArray = str1.split(Separator.DATA_SEPARATOR);
			for (int i = 0; i < strArray.length / 2; i++) {
				categoryMap.put(strArray[i * 2 + 1], strArray[i * 2]);
			}

		}
		categoryMap.put("$", "$");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	static public HashMap getPageMap() {
		return pageMap;
	}

	static public HashMap getCategoryMap() {
		return categoryMap;
	}

}
