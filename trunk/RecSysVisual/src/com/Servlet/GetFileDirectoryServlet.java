package com.Servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.FileDirectoryBuilder;


public class GetFileDirectoryServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		response.setContentType("text/xml;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println("ok");
		PrintWriter out = response.getWriter();
		File dir = new File(".");
		FileDirectoryBuilder getFileDirectory = new FileDirectoryBuilder();
		String str = "";
		str = getFileDirectory.getFileDirXMLStr("E:\\data\\");        
		out.println(str);
		out.flush();
		out.close();
	}
}
