package org.tseg.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tseg.model.book.BookModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.java_cup.internal.internal_error;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class BookTundish {

	/**
	 * @param args
	 */
	
	private String bookInfoPath = "";
	private String chapterInfoPath = "";
	private String inputPath = "";
	private String outputPath = "";
	private String tundishPath = "";
	private BookModel bookModel = new BookModel();
	
	public void run(){
		bookModel.setBookInfoPath(bookInfoPath);
		bookModel.setChapterInfoPath(chapterInfoPath);
		bookModel.setInputPath(inputPath);
		bookModel.setOutputPath(outputPath);
		bookModel.onInitial();
		bookModel.run();
		bookModel.saveBookXML();
		saveTundishXML();
	}
	
	/*
	 * 读取BookModel保存的文本文件，生成xml文件
	 * 读取数据各字段：chaptID,userNum,chaptName,bookName,chaptFee
	 * 生成xml文件各属性：chapterID,chapterName,userNum,propotion,runOffRatio,chapterFee
	 */
	public void saveTundishXML(){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();
		Element root = doc.createElement("Tundish");
		doc.appendChild(root);
		
		String str = "";
		String [] sStrings;
		ArrayList<String[]> aRecord = new ArrayList<String[]>();		
		try {
			File inFile = new File(outputPath);
			File [] bookFiles = inFile.listFiles();

			for(int i=0;i<bookFiles.length;i++)
			{
				aRecord.clear();
				BufferedReader br = new BufferedReader(new FileReader(bookFiles[i]));
				while((str=br.readLine())!=null){
					sStrings = str.split(",");
					aRecord.add(sStrings);
				}
				Element bookEmt = doc.createElement("book");
				bookEmt.setAttribute("bookID", bookFiles[i].getName());
				bookEmt.setAttribute("bookName", (aRecord.get(0))[0]);
				bookEmt.setAttribute("serialize", aRecord.get(0)[1]);
				root.appendChild(bookEmt);
				int baseUserNum = Integer.parseInt((aRecord.get(0))[3]);
				System.out.println("baseUserNum : "+baseUserNum);
				for(int j=0;j<aRecord.size();j++)
				{
					String [] ss = aRecord.get(j);
					
					String chapterID = ss[2];
					int userNum = Integer.parseInt(ss[3]);
					double propotion = (double)userNum/baseUserNum;
					String chaptName = ss[4];
					int chapterFee = Integer.parseInt(ss[5]);
					
					Element chaptEmt = doc.createElement("chaptEmt");
					chaptEmt.setAttribute("chapterID", chapterID);			
					chaptEmt.setAttribute("chaptName", chaptName);
					chaptEmt.setAttribute("userNum", String.valueOf(userNum));
					chaptEmt.setAttribute("propotion", String.valueOf(propotion));
					chaptEmt.setAttribute("chapterFee", String.valueOf(chapterFee));
					
					if(j==0){
						chaptEmt.setAttribute("runOffRatio", "0");						
					}
					else {
						int lastUserNum = Integer.parseInt((aRecord.get(j-1))[3]);
						double runOffRatio = (double) (lastUserNum-userNum)/lastUserNum;
						chaptEmt.setAttribute("runOffRatio", String.valueOf(runOffRatio));
					}
					
					bookEmt.appendChild(chaptEmt);
				}
			}
				
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		try {
			// 用xmlserializer把document的内容进行串化
			FileOutputStream os = null;
			OutputFormat outformat = new OutputFormat(doc);
			os = new FileOutputStream(this.tundishPath);
			XMLSerializer xmlSerilizer = new XMLSerializer(os, outformat);
			xmlSerilizer.serialize(doc);

		} catch (IOException ioexp) {
			ioexp.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BookTundish bt = new BookTundish();
		bt.setBookInfoPath("E:/data/book/新图书信息.txt");
		bt.setChapterInfoPath("E:/data/book/新章节信息.txt");
		bt.setInputPath("E:/data/book/新数据文件.txt");
		bt.setOutputPath("E:/data/book/test");
		bt.setTundishPath("E:/data/book/图书漏斗文件.xml");
		bt.run();

	}
	
	public String getBookInfoPath() {
		return bookInfoPath;
	}

	public void setBookInfoPath(String bookInfoPath) {
		this.bookInfoPath = bookInfoPath;
	}

	public String getChapterInfoPath() {
		return chapterInfoPath;
	}

	public void setChapterInfoPath(String chapterInfoPath) {
		this.chapterInfoPath = chapterInfoPath;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
	public void setTundishPath(String tundishPath) {
		this.tundishPath = tundishPath;
	}
	
	public String getTundishPath(){
		return this.tundishPath;
	}
	
	public void getProgress(AtomicInteger progress){
		this.bookModel.getProgress(progress);
	}

}
