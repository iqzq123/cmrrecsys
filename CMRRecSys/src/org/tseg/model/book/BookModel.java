package org.tseg.model.book;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.sun.corba.se.spi.orb.StringPair;

public class BookModel {
	private String bookInfoPath = "";
	private String chapterInfoPath = "";
	private String inputPath = "";
	private String outputPath = "";

	private HashMap<Integer,Book> bookMap = new HashMap<Integer, Book>();
	private HashMap<String, Integer> bookNameMap = new HashMap<String, Integer>();
	//private HashMap<String, ArrayList<Book>> userReadingMap = new HashMap<String, ArrayList<Book>>();
	private ArrayList<Book> targetBooksAL = new ArrayList<Book>();
	private final int BOOK_ATTRI_NUM = 15;
	private final int CHAPTER_ATTRI_NUM = 7;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BookModel b = new BookModel();
		b.setBookInfoPath("E:\\data\\book\\bookinfo.txt");
		b.setChapterInfoPath("E:\\data\\book\\chapterinfo.txt");
		b.setInputPath("E:\\data\\book\\msisdn_chapterid.txt");
		b.onInitial();
		b.run();
		b.saveBookXML();
		System.out.println("done");

	}
	
	public void onInitial() {
		System.out.println("onInitial");
		this.readBookInfo();
		this.readChapterInfo();
		System.out.println("onInitial END");
	}
	
	public void run() {
		System.out.println("run");
		this.readReadingInfo();
		System.out.println("run END");
	}
	
	public void readBookInfo() {
		try {
			System.out.println("readBookInfo");
			InputStreamReader read;
			read = new InputStreamReader(new FileInputStream(
					new File(bookInfoPath)));
			BufferedReader reader = new BufferedReader(read);
			String str;
			reader.readLine();
			while ((str = reader.readLine()) != null) {
				String[] strArray = str.split(",");
				
				Book book = new Book();
				book.setId(Integer.parseInt(strArray[0]));
				book.setName(strArray[1]);
				
				this.bookMap.put(book.getId(), book);
				this.bookNameMap.put(book.getName(), book.getId());
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("readBookInfo END");
	}
	
	public void readChapterInfo() {
		try {
			System.out.println("readChapterInfo");
			InputStreamReader read;
			read = new InputStreamReader(new FileInputStream(
					new File(chapterInfoPath)));
			BufferedReader reader = new BufferedReader(read);
			String str;
			reader.readLine();
			while ((str = reader.readLine()) != null) {
				String[] strArray = str.split(",");
				if ( strArray.length == this.CHAPTER_ATTRI_NUM ){
					Chapter chapter = new Chapter(Integer.parseInt(strArray[0]),
												Integer.parseInt(strArray[1]),
												strArray[2],
												Integer.parseInt(strArray[3]),
												Integer.parseInt(strArray[4]),
												Integer.parseInt(strArray[5]),
												Integer.parseInt(strArray[6]));
					Book book = this.bookMap.get(chapter.getBookId());
					book.addChapter(chapter);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("readChapterInfo End");
	}
	
	public void readReadingInfo() {
		
		try {
			System.out.println("readReadingInfo");
			InputStreamReader read;
			read = new InputStreamReader(new FileInputStream(
					new File(inputPath)));
			System.out.println(this.inputPath);
			BufferedReader reader = new BufferedReader(read);
			String str;
			reader.readLine();
			Book book = null;
			while ((str = reader.readLine()) != null) {
				String[] strArray = str.split(",");
				
				if ( strArray.length < 2 )
					continue;
				
				if ( book == null ){
					book = getBookByChapterId(Integer.parseInt(strArray[1]));
					if ( book != null )
						this.targetBooksAL.add(book);
				}
				
				if ( book != null )
					countChapterUserNumber(strArray, book);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("readReadingInfo END");
	}
	
	private void countChapterUserNumber(String[] strArray, Book book){
		if ( strArray.length < 2 )
			return ;
		int chapterId = Integer.parseInt(strArray[1]);
		HashMap<Integer, Chapter> chapterMap = book.getChapterMap();
		Chapter chapter = chapterMap.get(chapterId);
		int userNum = chapter.getUserNum();
		chapter.setUserNum(userNum+1);
	}
	
	public void saveBookXML() {
		System.out.println("savaBookXML");
		for ( Book book : this.targetBooksAL ){
			Iterator<Integer> iterator = book.getChapterMap().keySet().iterator();
			while ( iterator.hasNext() ) {
				Chapter chapter = book.getChapterMap().get(iterator.next());
				System.out.println(chapter.getId()+","+chapter.getUserNum());
				
			}
		}
		System.out.println("savaBookXML END");
	}
	private Book getBookByChapterId(int chapterId) {	
		Iterator<Integer> iter = this.bookMap.keySet().iterator();
		while ( iter.hasNext() ){
			Book tempBook = this.bookMap.get(iter.next());
			if ( tempBook.hasChapter(chapterId) )
				return tempBook;
		}
		return null;
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

}
