package org.tseg.analyse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class BookPackage {

	/**
	 * @param args
	 */
	private ArrayList<Double> RORattr = new ArrayList<Double>();  //流失率
	private String inputPath;
	private String outputPath;
	private String bookID; //需要打包的图书ID
	private int n;  //划分的册数，由界面传递参数
	private ArrayList<ArrayList<Chapter>> volumn = new ArrayList<ArrayList<Chapter>>();
	private ArrayList<Chapter> chapterArrayList = new ArrayList<Chapter>();
				
	public void readXML() {
		File file = new File(inputPath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document doc = null;
		try {
			doc = db.parse(file);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element tundish = doc.getDocumentElement();
		NodeList bookNodeList = tundish.getElementsByTagName("book");
		Element bookElement = null ;
		for (int i=0;i<bookNodeList.getLength();i++) {
			Element book = (Element) bookNodeList.item(i);
			System.out.println(book.getAttribute("bookID"));
			if(book.getAttribute("bookID").equals(this.bookID)) {
				bookElement = book;
				break;
			}
		}
		
		NodeList chaptNodeList = bookElement.getChildNodes();
		for (int i=0;i<chaptNodeList.getLength();i++) {
			Element chaptEmt = (Element) chaptNodeList.item(i);
			Chapter chapter = new Chapter();
			String name = chaptEmt.getAttribute("chaptName");
			int num = Integer.parseInt(chaptEmt.getAttribute("userNum"));
			int fee = Integer.parseInt(chaptEmt.getAttribute("chapterFee"));
			double ror = Double.parseDouble(chaptEmt.getAttribute("runOffRatio"));
			
			chapter.setName(name);
			chapter.setuserNum(num);
			chapter.setFee(fee);
			chapter.setROR(ror);

			this.RORattr.add(ror);
			
			this.chapterArrayList.add(chapter);
			
			
		}
	}
	
	
	
	public double getThreshold () {
		ArrayList<Double> tempROR = new ArrayList<Double>();
		for(int i=0;i<this.RORattr.size();i++) {
			double ror = this.RORattr.get(i);
			tempROR.add(ror);
		}
		Collections.sort(tempROR);
		for(int i=0;i<tempROR.size();i++) {
			System.out.println(tempROR.get(i));
		}
		return tempROR.get(this.n-1);
	}
	
	public void packaging() {
		double threshold = getThreshold();
		System.out.println(threshold);
		ArrayList<Chapter> chapters = new ArrayList<Chapter>();
		chapters.add(this.chapterArrayList.get(0));
		for(int i=1;i<this.RORattr.size();i++) {			
			/*
			 * 第一章的流失率为0.0 ，如果阈值大于0，而第一章不能作为一个划分点，所以划分点要包含该阈值
			 * if与else代码段的区别在于<=threshold和<threshold
			 */
			if(threshold>0) {                              
				if( this.RORattr.get(i) <= threshold ) {   //该章节的流失率小于阈值，就要在此分册
//					System.out.println("  if");
					ArrayList<Chapter> chapterClone = new ArrayList<Chapter>();
					chapterClone = (ArrayList<Chapter>) chapters.clone();
					this.volumn.add(chapterClone);
//					showvolumn();
					chapters.clear();
					Chapter chapter = this.chapterArrayList.get(i);
					chapters.add(chapter);
				}
				else {
//					System.out.println("  else");
					Chapter chapter = this.chapterArrayList.get(i);
					chapters.add(chapter);
//					test(chapters);
				}
			}
			else {
				if( this.RORattr.get(i) < threshold ) {   //该章节的流失率小于阈值，就要在此分册
//					System.out.println("  if");
					ArrayList<Chapter> chapterClone = new ArrayList<Chapter>();
					chapterClone = (ArrayList<Chapter>) chapters.clone();
					this.volumn.add(chapterClone);
//					showvolumn();
					chapters.clear();
					Chapter chapter = this.chapterArrayList.get(i);
					chapters.add(chapter);
				}
				else {
//					System.out.println("  else");
					Chapter chapter = this.chapterArrayList.get(i);
					chapters.add(chapter);
//					test(chapters);
				}
			}
				
		
			
		}
		this.volumn.add(chapters);
	}
	
	public void test(ArrayList<Chapter> chapters) {
		for (int i=0;i<chapters.size();i++) {
			Chapter chapter = chapters.get(i);
			System.out.println("          "+chapter.getName());
		}
	}
	
	public void showvolumn() {
		for(int i=0;i<this.volumn.size();i++) {
			ArrayList<Chapter> cArrayList = new ArrayList<Chapter>();
			cArrayList = this.volumn.get(i);
			System.out.println("第"+(i+1)+"册");
			for(int j=0;j<cArrayList.size();j++) {
				Chapter chapter = cArrayList.get(j);
				System.out.println("  "+chapter.getName());
			}
		}
	}
	
	public void output() {
		
		File f = new File(this.outputPath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (Exception pce) {
			System.err.println(pce);
		}
		Document doc = db.newDocument();
		Element root = doc.createElement("book");
		doc.appendChild(root);
		
		for(int i=0;i<this.volumn.size();i++) {
			Element volumn = doc.createElement("volumn");
			volumn.setAttribute("name", "第"+(i+1)+"册");
			root.appendChild(volumn);
			ArrayList<Chapter> chaptArrayList = this.volumn.get(i);
			for(int j=0;j<chaptArrayList.size();j++) {
				Chapter chapter = chaptArrayList.get(j);
				Element chapt = doc.createElement("chapter");
				chapt.setAttribute("chaptName", chapter.getName());
				chapt.setAttribute("userNum", String.valueOf(chapter.getuserNum()));
				chapt.setAttribute("chapterFee", String.valueOf(chapter.getFee()));
				chapt.setAttribute("runOffRatio", String.valueOf(chapter.getROR()));
				volumn.appendChild(chapt);
			}
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
	
	public void run() {
		//RORattr
		readXML();
		packaging();
		output();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BookPackage bPackage = new BookPackage();
		bPackage.setInputPath("E:/data/book/tundish.xml");
		bPackage.setOutputPath("E:/data/book/volumn.xml");
		bPackage.setBookID("355044051");
		bPackage.setN(13);
		bPackage.run();
		bPackage.showvolumn();


	}
	
	public void setInputPath(String str) {
		this.inputPath = str;
	}
	
	public String getInputPath() {
		return this.inputPath;
	}
	
	public void setOutputPath(String str) {
		this.outputPath = str ;
	}
	
	public String getOutputPath() {
		return this.outputPath;
	}
	
	public void setBookID(String str) {
		this.bookID = str;
	}
	
	public String getBookID() {
		return this.bookID;
	}
	
	public void setN(int n) {
		this.n = n;
	}
	
	public int getN() {
		return this.n;
	}
	
	
	public class Chapter {
		private String name;
		private int fee;
		private int userNum;
		private double ror;
		
		public void setName(String str) {
			name = str;
		}
		public String getName() {
			return name;
		}
		
		public void setFee(int f) {
			fee = f;
		}
		public int getFee() {
			return fee;
		}
		
		public void setuserNum(int n) {
			userNum = n;
		}
		public int getuserNum() {
			return userNum;
		}
		
		public void setROR (double r) {
			ror = r;
		}
		public double getROR () {
			return ror;
		}
	}

}


