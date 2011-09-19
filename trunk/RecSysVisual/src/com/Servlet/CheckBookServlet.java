package com.Servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tseg.model.book.CheckBook;

import com.XMLFileReader;

public class CheckBookServlet extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		try {
			String webRootPath = getServletContext().getRealPath("/");
			System.out.print("webRootPath" + webRootPath);
			InputStream in = new FileInputStream(webRootPath
					+ "config/config.properties");
			Properties properties = new Properties();
			properties.load(in);
			String fileDir = "";
			fileDir = properties.getProperty("directory");
			
			String bookString = java.net.URLDecoder.decode(request
					.getParameter("bookString"), "UTF-8");
			String inputPath = fileDir+"/dim/dim_bookinfo.txt";  ///////////所有图书的信息
			String outputPath = fileDir+"/dim/checkBookResult.xml";      ///////////存放查询结果的文件
			
			CheckBook checkBook = new CheckBook();
			checkBook.setInputPath(inputPath);
			checkBook.setOutputPath(outputPath);
			checkBook.setBookStr(bookString);
			
			System.out.println("check book servlet.......\n"+bookString+"\n"+inputPath+"\n"+outputPath);
			checkBook.run();
			
			
			response.setContentType("text/xml;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			XMLFileReader xmlfr = new XMLFileReader();
			String str = "";
			str = xmlfr.readXMLToStr(outputPath);      
			out.println(str);
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
