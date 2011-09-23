package com.Servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tseg.Starter;
import org.tseg.analyse.AnalyseRunner;

import com.XMLFileReader;

public class AnalyseStarterServlet extends HttpServlet {

	private AtomicInteger progress = new AtomicInteger(0);
	private Hashtable<String, Starter> analyseTable = new Hashtable<String, Starter>();

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		

		try {
			String action = java.net.URLDecoder.decode(request
					.getParameter("action"), "UTF-8");
			if (action.equals("start")) {
				Starter s = new Starter();
				String taskID = java.net.URLDecoder.decode(request
						.getParameter("taskID"), "UTF-8");
				String cmd = java.net.URLDecoder.decode(request
						.getParameter("cmd"), "UTF-8");
				String inputPath = java.net.URLDecoder.decode(request
						.getParameter("inputPath"), "UTF-8");
				String outputPath = java.net.URLDecoder.decode(request
						.getParameter("outputPath"), "UTF-8");
				this.analyseTable.put(taskID, s);

				System.out.println("taskID: "+taskID+"\n");
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
				s.setSiteDataPath(fileDir+"/dim");

				// s.setNegCate(true);

				s.start(cmd);// "PathFinderClass 2? |?100? |?login*;
				// |;������ʾҳ��");
				System.out.println("start run..............................");
				// System.out.println(s.getProgress());

			} else if (action.equals("get")) {
				response.setContentType("text/xml;charset=utf-8");
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				String taskID = java.net.URLDecoder.decode(request
						.getParameter("taskID"), "UTF-8");
				Starter starter = analyseTable.get(taskID);
				int currentLine = starter.getCurLineNum();
				int totalLine = starter.getLineAmout();
				out.println(currentLine+","+totalLine);
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
