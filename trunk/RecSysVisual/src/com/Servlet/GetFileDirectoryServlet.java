package com.Servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.FileDirectoryBuilder;
import java.util.Properties;

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
		PrintWriter out = response.getWriter();
		File curDir = new File(".");
		FileDirectoryBuilder getFileDirectory = new FileDirectoryBuilder();
		String str = "";
		Properties properties = new Properties();
		String webRootPath=getServletContext().getRealPath("/");
		System.out.print("webRootPath"+webRootPath);
		InputStream in = new FileInputStream(webRootPath+"config/config.properties");
		properties.load(in);
		String fileDir = "";
		fileDir = properties.getProperty("directory");
		System.out.println(fileDir);
		if ( fileDir != null ){
			str = getFileDirectory.getFileDirXMLStr(fileDir);     
		}  
		out.println(str);
		out.flush();
		out.close();
	}
}
