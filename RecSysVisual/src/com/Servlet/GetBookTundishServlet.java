package com.Servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tseg.analyse.*;

import com.XMLFileReader;

public class GetBookTundishServlet extends HttpServlet{
	private String tundishSuffix = "/图书漏斗文件.xml";
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		response.setContentType("text/xml;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println("get book tundish servlet!");
		request.setCharacterEncoding("UTF-8");
		String bookInfoPath = java.net.URLDecoder.decode(request.getParameter("bookInfoPath"),"UTF-8");
		String chapterInfoPath = java.net.URLDecoder.decode(request.getParameter("chapterInfoPath"),"UTF-8");
		String readingInfoPath = java.net.URLDecoder.decode(request.getParameter("readingInfoPath"),"UTF-8");
		String tundishPath = java.net.URLDecoder.decode(request.getParameter("tundishPath"),"UTF-8");

		tundishPath = tundishPath + tundishSuffix;
		
		System.out.println("进入servlet"+"\n"+bookInfoPath+"\n"+chapterInfoPath+"\n"+readingInfoPath);
		
		BookTundish bookTundish = new BookTundish();
		File f = new File(readingInfoPath);
		
		bookTundish.setBookInfoPath(bookInfoPath);
		bookTundish.setChapterInfoPath(chapterInfoPath);
		bookTundish.setInputPath(readingInfoPath);
		bookTundish.setOutputPath(f.getParent()+"/books");
		bookTundish.setTundishPath(tundishPath);
		
		bookTundish.run();
		
		PrintWriter out = response.getWriter();
		XMLFileReader xmlfr = new XMLFileReader();
		String str = "";
		str = xmlfr.readXMLToStr(tundishPath);//"E:\\data\\pagevisit\\pv6.txt.out\\tundish.xml");//      
		out.println(str);
		out.flush();
		out.close();
	}

}
