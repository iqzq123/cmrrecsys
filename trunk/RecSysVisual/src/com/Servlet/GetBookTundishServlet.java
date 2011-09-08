package com.Servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tseg.analyse.*;

import com.XMLFileReader;

public class GetBookTundishServlet extends HttpServlet{
	private String tundishSuffix = "/图书漏斗文件.xml";
	private   AtomicInteger progress =new AtomicInteger(0);
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		response.setContentType("text/xml;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println("get book tundish servlet!");
		request.setCharacterEncoding("UTF-8");
		String action = java.net.URLDecoder.decode(request.getParameter("action"),"UTF-8");
		System.out.println("action is:"+action);
		if(action.equals("start")){
			String bookInfoPath = java.net.URLDecoder.decode(request.getParameter("bookInfoPath"),"UTF-8");
			String chapterInfoPath = java.net.URLDecoder.decode(request.getParameter("chapterInfoPath"),"UTF-8");
			String readingInfoPath = java.net.URLDecoder.decode(request.getParameter("readingInfoPath"),"UTF-8");
			String tundishPath = java.net.URLDecoder.decode(request.getParameter("tundishPath"),"UTF-8");

			tundishPath = tundishPath + tundishSuffix;
			
			System.out.println("进入book tundish servlet"+"\n"+bookInfoPath+"\n"+chapterInfoPath+"\n"+readingInfoPath+"\n"+tundishPath);
			
			BookTundish bookTundish = new BookTundish();
			File f = new File(readingInfoPath);
			
			bookTundish.setBookInfoPath(bookInfoPath);
			bookTundish.setChapterInfoPath(chapterInfoPath);
			bookTundish.setInputPath(readingInfoPath);
			bookTundish.setOutputPath(f.getParent()+"/books");
			bookTundish.setTundishPath(tundishPath);
			
			bookTundish.getProgress(progress);
			bookTundish.run();
			
			
			PrintWriter out = response.getWriter();
			XMLFileReader xmlfr = new XMLFileReader();
			String str = "";
			str = xmlfr.readXMLToStr(tundishPath);//"E:\\data\\pagevisit\\pv6.txt.out\\tundish.xml");//      
			out.println(str);
			out.flush();
			out.close();
		}
		else if (action.equals("progress")){
			System.out.println("get's progress:"+progress);
			response.setContentType("text/xml;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			out.println(progress);
			out.flush();
			out.close();
		}
		
	}

}
