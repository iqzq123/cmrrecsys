package com.Servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tseg.analyse.*;

import com.XMLFileReader;

public class GetBookTundishServlet extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		response.setContentType("text/xml;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println("get book tundish servlet!");
		String bookInfoPath = java.net.URLDecoder.decode(request.getParameter("bookInfoPath"),"UTF-8");
		String chapterInfoPath = java.net.URLDecoder.decode(request.getParameter("chapterInfoPath"),"UTF-8");
		String readingInfoPath = java.net.URLDecoder.decode(request.getParameter("readingInfoPath"),"UTF-8");
		System.out.println("½øÈëservlet"+"\n"+bookInfoPath+"\n"+chapterInfoPath+"\n"+readingInfoPath);
		BookTundish bookTundish = new BookTundish();
		bookTundish.setBookInfoPath(bookInfoPath);
		bookTundish.setChapterInfoPath(chapterInfoPath);
		bookTundish.setInputPath(readingInfoPath);
		bookTundish.setOutputPath("E:/data/book/testout");
		bookTundish.setTundishPath("E:/data/book");		
		bookTundish.run();
		
		PrintWriter out = response.getWriter();
		XMLFileReader xmlfr = new XMLFileReader();
		String str = "";
		str = xmlfr.readXMLToStr("E:/data/book/tundish.xml");//"E:\\data\\pagevisit\\pv6.txt.out\\tundish.xml");//      
		out.println(str);
		out.flush();
		out.close();
	}

}
