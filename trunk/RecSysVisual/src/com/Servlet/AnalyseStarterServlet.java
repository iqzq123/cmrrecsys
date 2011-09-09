package com.Servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tseg.Starter;

import com.XMLFileReader;

public class AnalyseStarterServlet extends HttpServlet {

	private AtomicInteger progress = new AtomicInteger(0);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		

		try {
			String action = java.net.URLDecoder.decode(request
					.getParameter("action"), "UTF-8");
			if (action.equals("start")) {
				Starter s = new Starter();
				String cmd = java.net.URLDecoder.decode(request
						.getParameter("cmd"), "UTF-8");
				String inputPath = java.net.URLDecoder.decode(request
						.getParameter("inputPath"), "UTF-8");
				String outputPath = java.net.URLDecoder.decode(request
						.getParameter("outputPath"), "UTF-8");

				System.out.println("servlet cmd: \n" + cmd);
				System.out.println("servlet inputPath: \n" + inputPath);
				System.out.println("servlet outputPath: \n" + outputPath);
				s.getProgress(progress);
				s.setInputPath(inputPath);
				s.setOutputPath(outputPath);
				String webRootPath = getServletContext().getRealPath("/");
				System.out.print("webRootPath" + webRootPath);
				InputStream in = new FileInputStream(webRootPath
						+ "config/config.properties");
				Properties properties = new Properties();
				properties.load(in);
				String fileDir = "";
				fileDir = properties.getProperty("directory");
				s.setSiteDataPath(fileDir);

				// s.setNegCate(true);

				s.start(cmd);// "PathFinderClass 2? |?100? |?login*;
				// |;������ʾҳ��");
				System.out.println("start run..............................");
				// System.out.println(s.getProgress());

			} else if (action.equals("get")) {
				System.out.println("get's progress:" + progress);
				response.setContentType("text/xml;charset=utf-8");
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				out.println(progress);
				out.flush();
				out.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR:AnalyseStarterServlet");
			System.out.println(e.toString());
		}

	}

}
