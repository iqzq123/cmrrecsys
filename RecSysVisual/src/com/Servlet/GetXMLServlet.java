package com.Servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.XMLFileReader;

public class GetXMLServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		response.setContentType("text/xml;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		
		String filePath = java.net.URLDecoder.decode(request.getParameter("filePath"),"UTF-8");
		
		System.out.println("filePath:"+filePath);
		
		PrintWriter out = response.getWriter();
		XMLFileReader xmlfr = new XMLFileReader();
		String str = "";
		str = xmlfr.readXMLToStr(filePath);//"E:\\data\\pagevisit\\pv6.txt.out\\tundish.xml");//      
		out.println(str);
		out.flush();
		out.close();
	}
}
