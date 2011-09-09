package com.Servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tseg.analyse.BookPackage;

import com.XMLFileReader;

public class BookPackageServlet extends HttpServlet{
	private String tundishSuffix = "/图书漏斗文件.xml";
	private String packageSuffix = "/图书打包文件.xml";
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		try {
			System.out.println("-------------------\n进入bookPackageServlet!!!!!!");
			response.setContentType("text/xml;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			
			String tundishPath = java.net.URLDecoder.decode(request.getParameter("tundishPath"), "UTF-8");
//			String packagePath = java.net.URLDecoder.decode(request.getParameter("packagePath"),"UTF-8");
			String bookID = java.net.URLDecoder.decode(request.getParameter("bookID"),"UTF-8");
			int n = Integer.parseInt(java.net.URLDecoder.decode(request.getParameter("n"), "UTF-8"));
			
			File temp = new File(tundishPath);
			String packagePath = temp.getParent()+packageSuffix;
//			tundishPath = tundishPath + tundishSuffix;
			BookPackage bookPackage = new BookPackage();
			
			bookPackage.setInputPath(tundishPath);
			bookPackage.setOutputPath(packagePath);
			bookPackage.setBookID(bookID);
			bookPackage.setN(n);
			
			bookPackage.run();
			
			
			PrintWriter out = response.getWriter();
			XMLFileReader xmlfr = new XMLFileReader();
			String str = "";
			str = xmlfr.readXMLToStr(packagePath);      
			out.println(str);
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("ERROR:BookPackageServlet");
			System.out.println(e.toString());
		}
		
		
	}

	

}
