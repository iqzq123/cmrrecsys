package com.Servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tseg.analyse.*;

import com.XMLFileReader;

public class GetBookTundishServlet extends HttpServlet {
	private String tundishSuffix = "/图书漏斗文件.xml";
	private Hashtable<String, BookTundish> btTable = new Hashtable<String, BookTundish>();
	private String taskSep = "@";

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			response.setContentType("text/xml;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			request.setCharacterEncoding("UTF-8");
			String action = java.net.URLDecoder.decode(request
					.getParameter("action"), "UTF-8");
			if (action.equals("start")) {
				String taskID = java.net.URLDecoder.decode(request
						.getParameter("taskID"), "UTF-8");
				String bookString = java.net.URLDecoder.decode(request
						.getParameter("bookString"), "UTF-8");
				String readingInfoPath = java.net.URLDecoder.decode(request
						.getParameter("readingInfoPath"), "UTF-8");
				String tundishPath = java.net.URLDecoder.decode(request
						.getParameter("tundishPath"), "UTF-8");

				String currentTime = getCurrentTime();
				if (tundishPath.endsWith("xml")) {
					File tundishFile = new File(tundishPath);
					File parentFile = tundishFile.getParentFile();
					if (!parentFile.exists() || parentFile.isFile())
						parentFile.mkdir();
				} else {
					File tundishDir = new File(tundishPath);
					if (!tundishDir.exists())
						tundishDir.mkdir();
					else if (tundishDir.isFile())
						tundishDir.mkdir();
					tundishPath = tundishDir.getAbsolutePath() + tundishSuffix;
				}

				System.out.println("进入book tundish servlet" + "\n"
						+ readingInfoPath + "\n" + tundishPath);

				BookTundish bookTundish = new BookTundish();
				btTable.put(taskID, bookTundish);
				File f = new File(readingInfoPath);

				
				
				String webRootPath = getServletContext().getRealPath("/");
				System.out.print("webRootPath" + webRootPath);
				InputStream in = new FileInputStream(webRootPath
						+ "config/config.properties");
				Properties properties = new Properties();
				properties.load(in);
				String fileDir = "";
				fileDir = properties.getProperty("directory");
				String bookInfoPath = fileDir + "/dim/dim_bookinfo.txt";
				String chapterInfoPath = fileDir + "/dim/dim_chapterinfo.txt";
				bookTundish.setBookInfoPath(bookInfoPath);
				bookTundish.setChapterInfoPath(chapterInfoPath);
				bookTundish.setInputPath(readingInfoPath);
				bookTundish.setOutputPath(f.getParent() + "/tempbooks"
						+ currentTime);
				bookTundish.setTundishPath(tundishPath);
				bookTundish.setBookString(bookString);

				System.out.println("bookTundish servlet run.................");
				bookTundish.run();

			} else if (action.equals("progress")) {
				response.setContentType("text/xml;charset=utf-8");
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				String taskID = java.net.URLDecoder.decode(request
						.getParameter("taskID"), "UTF-8");
				BookTundish bookTundish = btTable.get(taskID);
				int chapterLine = bookTundish.getChapterLine();
				int readingInfoLine = bookTundish.getReadingInfoLine();
				String exceptionString = bookTundish.getException();
				System.out.println("exceptionString: "+exceptionString);
				if(!exceptionString.equals("nullnull")){
					out.println("ERROR:\n"+exceptionString);
					out.flush();
					out.close();
				}
				System.out.println("chapterLine is : "+chapterLine+"\nreadingInfoLine is:"+readingInfoLine);
				out.println(chapterLine+","+readingInfoLine);
				out.flush();
				out.close();
			}
			else if(action.equals("getTaskInfo")){
				String taskInfo = "";
				if(btTable.isEmpty()){
					taskInfo = "null";
				}
				else{
					Iterator<String> taskIDs = btTable.keySet().iterator();
					while(taskIDs.hasNext()){
						String taskID = taskIDs.next();
						BookTundish bookTundish = btTable.get(taskID);
						String inputPath = bookTundish.getInputPath();
						String outputPath = bookTundish.getTundishPath();
						String bookString = bookTundish.getBookString();
						int chapterLine = bookTundish.getChapterLine();
						int readingInfoLine = bookTundish.getReadingInfoLine();
						taskInfo = taskInfo+taskID+","+inputPath+","+outputPath+","+bookString+","+chapterLine+","+readingInfoLine;
						String exceptionString = bookTundish.getException();
						if(exceptionString.equals("nullnull")){
							taskInfo = taskInfo + taskSep;
						}
						else {
							taskInfo = taskInfo + "," + exceptionString + taskSep;
						}
					}
				}
				response.setContentType("text/xml;charset=utf-8");
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				out.println(taskInfo);
				out.flush();
				out.close();			
			}
		} catch (Exception e) {
			// TODO: handle exception
			
			response.setContentType("text/xml;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			out.println("ERROR:"+e.toString());
			out.flush();
			out.close();
			e.printStackTrace();
			System.out.println("ERROR:GetBookTundishServlet");
			System.out.println(e.toString());
		}

	}

	public String getCurrentTime() {
		Calendar ca = Calendar.getInstance();
		String year = Integer.toString(ca.get(Calendar.YEAR));
		String month = addZero(Integer.toString(ca.get(Calendar.MONTH) + 1));
		String day = addZero(Integer.toString(ca.get(Calendar.DATE)));
		String hour = addZero(Integer.toString(ca.get(Calendar.HOUR)));
		String minute = addZero(Integer.toString(ca.get(Calendar.MINUTE)));
		String second = addZero(Integer.toString(ca.get(Calendar.SECOND)));
		String mmsecond = addZero(Integer.toString(ca.get(Calendar.MILLISECOND)));
		// System.out.println(year+";"+month+";"+day+";"+hour+";"+minute+";"+second);
		return year + month + day + hour + minute + second + mmsecond;
	}

	public String addZero(String str) {
		String strNew;
		if (str.length() == 1) {
			strNew = "0" + str;
		} else {
			strNew = str;
		}
		return strNew;

	}

}
