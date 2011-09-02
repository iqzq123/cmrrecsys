package com.Servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tseg.analyse.FPAnalyser;

public class FPAAnalyseServlet extends HttpServlet {
	public String SEPARATOR = "@@@"; 
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		System.out.println("------------------����FPAAnalyseServlet");
		String inputPath = java.net.URLDecoder.decode(request.getParameter("inputPath"), "UTF-8");
		String outputPath = java.net.URLDecoder.decode(request.getParameter("outputPath"), "UTF-8");
		String type = java.net.URLDecoder.decode(request.getParameter("type"), "UTF-8");
		String Closed = java.net.URLDecoder.decode(request.getParameter("Closed"), "UTF-8");
		String maxFPLenght = java.net.URLDecoder.decode(request.getParameter("maxFPLenght"), "UTF-8");
		String MinRatio = java.net.URLDecoder.decode(request.getParameter("MinRatio"), "UTF-8");
		String DecayRatio = java.net.URLDecoder.decode(request.getParameter("DecayRatio"), "UTF-8");
		
		
		String webRootPath=getServletContext().getRealPath("/");
		System.out.print("webRootPath"+webRootPath);
		Properties properties = new Properties();
		InputStream in = new FileInputStream(webRootPath+"config/config.properties");
		properties.load(in);
		String fileDir = "";
		fileDir = properties.getProperty("directory");		
		System.out.println("fileDir is : "+fileDir);
		
		String siteDataPath = fileDir;
		
		FPAnalyser fp = new FPAnalyser();
		String params = siteDataPath+this.SEPARATOR+
		inputPath+this.SEPARATOR+
		outputPath+this.SEPARATOR+
		type+this.SEPARATOR+
		Closed+this.SEPARATOR+
		maxFPLenght+this.SEPARATOR+
		MinRatio+this.SEPARATOR+
		DecayRatio+this.SEPARATOR;
		
		System.out.println(params);
		fp.readParam(params);
		
		try {
			fp.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
