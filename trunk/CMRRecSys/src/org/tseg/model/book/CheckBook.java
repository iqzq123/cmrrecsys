package org.tseg.model.book;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class CheckBook {

	/**
	 * @param args
	 */
	private String[] bookStrings;
	private String inputPath;
	private String outputPath;
	private String bookStr;
	private String sep = ";";
	private String fileSep = "\\|";
	private ArrayList<String> targetBooks = new ArrayList<String>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CheckBook checkBook = new CheckBook();
		checkBook.setInputPath("E:/data/book/dim_bookinfo.txt");
		checkBook.setOutputPath("E:/data/book/20110918.xml");
		checkBook.setBookStr("一不小心;41143062; ");
		checkBook.run();

	}
	
	public void run(){
		initial();
		find();
		outPut();
	}
	
	public void initial(){
		bookStrings = bookStr.trim().split(sep);		
	}
	
	/////////模糊匹配，看bookStrings中是否有元素与指定book相匹配的
	public Boolean inBookStrings(String bookID,String bookName){  
		Boolean findBoolean = false;
		for(int i=0;i<bookStrings.length;i++){
			if(bookID.contains(bookStrings[i])||bookName.contains(bookStrings[i])){
				findBoolean = true;
				break;
			}
		}
		return findBoolean;
	}
	
	public void find(){
		try {
			Boolean find = false;
			InputStreamReader read;
			read = new InputStreamReader(new FileInputStream(
					new File(inputPath)),"UTF-8");
			BufferedReader br = new BufferedReader(read);
			String str = "";
			int line = 0;
			while((str=br.readLine())!=null){
				line++;
				String bookID = str.split(fileSep)[0];
				String bookName = str.split(fileSep)[1];
				if(inBookStrings(bookID,bookName)){
					targetBooks.add(str);
					System.out.println(line+" "+str);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void outPut(){
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			db = dbf.newDocumentBuilder();
			doc = db.newDocument();
			Element root = doc.createElement("foundBooks");
			doc.appendChild(root);
			String[] books;
			for(int i=0;i<targetBooks.size();i++){
				books = targetBooks.get(i).split(fileSep);
				Element bookEmt = doc.createElement("book");
				bookEmt.setAttribute("bookID", books[0]);
				bookEmt.setAttribute("bookName", books[1]);
				root.appendChild(bookEmt);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(this.outputPath);
			XMLSerializer xmlSerilizer = new XMLSerializer(os, outformat);
			xmlSerilizer.serialize(doc);

		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}
	}
	public void setBookStr(String bookString){
		this.bookStr = bookString;
	}
	public String getBookStr(){
		return this.bookStr;
	}
	public void setInputPath(String path){
		this.inputPath = path;
	}
	public String getInputPath(){
		return this.inputPath;
	}
	
	public void setOutputPath(String path){
		this.outputPath = path;
	}
	public String getOutputPath(){
		return this.outputPath;
	}

}
