package org.tseg.Ulits;

import java.util.HashMap;

public class Ulits {
	
	public static int getFileSize(String fileName){
		int amount=0;
		String[] nameArray=fileName.split(Separator.FILENAME_SEPARATOR);
		try{
			amount=Integer.parseInt(nameArray[nameArray.length-1])*10000;
		}catch(Exception e){
			amount=-1;
			System.out.print("fileName Format error");
		}
		return amount;
	}

	public static boolean isSameCategory(String page1, String page2) {

		// 分类专区/历史军事/战争幻想书库/军事历史阅读(279,71133) 分类专区/历史军事/战争幻想书库/军事历史阅读(279,71133)
		// 269.0
		// 从排行榜访问/手机阅读详情页(7928,114677) 从排行榜访问/手机阅读阅读页(168659,19403970) 2211.0

		String[] a = page1.split("/");
		String[] b = page2.split("/");
		int min = Math.min(a.length, b.length) - 1;
		int cnt = 0;
		for (int i = 0; i < min; i++) {
			if (a[i].equals(b[i])) {
				cnt++;
			}
		}
		if (cnt == min) {
			return true;
		}
		return false;
	}

	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}
	}
	public static Object [] sortTable(HashMap table){
		Object []array=table.keySet().toArray();
		boolean chFlag=false;
		for(int i=0;i<array.length-1;i++){
			for(int j=i+1;j<array.length;j++){
				int a=(Integer)table.get(array[i]);
				int b=(Integer)table.get(array[j]);
				if(a<b){
					Object tmp=array[i];
					array[i]=array[j];
					array[j]=tmp;
					chFlag=true;
				}
			}
			if(chFlag==false){
				break;
			}
		}
		return array;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "a/b/ai";
		String s1 = "a/c/ai";
		boolean flag = isSameCategory(s, s1);
		System.out.println(flag);

	}
}
