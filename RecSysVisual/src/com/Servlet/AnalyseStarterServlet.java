package com.Servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tseg.Starter;



import com.XMLFileReader;


  public class   AnalyseStarterServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		System.out.println("in start servlet1111111122222222222222222");

		String cmd = java.net.URLDecoder.decode(request.getParameter("cmd"),"UTF-8");
		String inputPath = java.net.URLDecoder.decode(request.getParameter("inputPath"),"UTF-8");
		String outputPath = java.net.URLDecoder.decode(request.getParameter("outputPath"),"UTF-8");

		System.out.println("servlet cmd: \n"+cmd);
		System.out.println("servlet inputPath: \n"+inputPath);
		System.out.println("servlet outputPath: \n"+outputPath);
		
		Starter s=new Starter();
		s.setInputPath(inputPath);
		s.setOutputPath(outputPath);
		String webRootPath=getServletContext().getRealPath("/");
		System.out.print("webRootPath"+webRootPath);
		InputStream in = new FileInputStream(webRootPath+"config/config.properties");
		Properties properties = new Properties();
		properties.load(in);
		String fileDir = "";
		fileDir = properties.getProperty("directory");
		s.setSiteDataPath(fileDir);
		
		//s.setNegCate(true);
		try{
			s.start(cmd);//"PathFinderClass	2?	|?100?	|?login*;	|;������ʾҳ��");
			System.out.println("start run..............................");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
