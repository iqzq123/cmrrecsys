package com.Servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tseg.analyse.FPAnalyser;

public class FPAAnalyseServlet extends HttpServlet {
	public String SEPARATOR = "@@@";
	private AtomicInteger progress = new AtomicInteger(0);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("------------------进入FPAAnalyseServlet");
		try {
			String action = java.net.URLDecoder.decode(request
					.getParameter("action"), "UTF-8");
			if (action.equals("start")) {
				String inputPath = java.net.URLDecoder.decode(request
						.getParameter("inputPath"), "UTF-8");
				String outputPath = java.net.URLDecoder.decode(request
						.getParameter("outputPath"), "UTF-8");
				String type = java.net.URLDecoder.decode(request
						.getParameter("type"), "UTF-8");
				String Closed = java.net.URLDecoder.decode(request
						.getParameter("Closed"), "UTF-8");
				String maxFPLenght = java.net.URLDecoder.decode(request
						.getParameter("maxFPLenght"), "UTF-8");
				String MinRatio = java.net.URLDecoder.decode(request
						.getParameter("MinRatio"), "UTF-8");
				String DecayRatio = java.net.URLDecoder.decode(request
						.getParameter("DecayRatio"), "UTF-8");

				String webRootPath = getServletContext().getRealPath("/");
				System.out.print("webRootPath" + webRootPath);
				Properties properties = new Properties();
				InputStream in = new FileInputStream(webRootPath
						+ "config/config.properties");
				properties.load(in);
				String fileDir = "";
				fileDir = properties.getProperty("directory");
				System.out.println("fileDir is : " + fileDir);

				String siteDataPath = fileDir+"/dim";

				FPAnalyser fp = new FPAnalyser();
				fp.getProgress(progress);
				String params = siteDataPath + this.SEPARATOR + inputPath
						+ this.SEPARATOR + outputPath + this.SEPARATOR + type
						+ this.SEPARATOR + Closed + this.SEPARATOR
						+ maxFPLenght + this.SEPARATOR + MinRatio
						+ this.SEPARATOR + DecayRatio + this.SEPARATOR;

				System.out.println(params);
				fp.readParam(params);

				fp.run();

			} else if (action.equals("progress")) {
				System.out.println("get's progress:" + progress);
				response.setContentType("text/xml;charset=utf-8");
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				out.println(progress);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("ERROR:FPAAnalyseServlet");
			System.out.println(e.toString());
		}

	}

}
