package com.Servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tseg.Starter;
import org.tseg.analyse.FPAnalyser;



public class FPAAnalyseServlet extends HttpServlet {
	public String SEPARATOR = "@@@";
	private Hashtable<String, FPAnalyser> fpTable =new Hashtable<String, FPAnalyser>();	
	private Hashtable<String, Integer> preNumTable = new Hashtable<String, Integer>();
	private Hashtable<String, Integer> countTable = new Hashtable<String, Integer>();

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String action = java.net.URLDecoder.decode(request
					.getParameter("action"), "UTF-8");
			if (action.equals("start")) {
				String taskID=java.net.URLDecoder.decode(request.getParameter("taskID"),"UTF-8");

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
				
				System.out.println("taskID "+taskID);
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
				this.fpTable.put(taskID, fp);
				this.preNumTable.put(taskID, -2);
				this.countTable.put(taskID, 1);
				
				String params = siteDataPath + this.SEPARATOR + inputPath
						+ this.SEPARATOR + outputPath + this.SEPARATOR + type
						+ this.SEPARATOR + Closed + this.SEPARATOR
						+ maxFPLenght + this.SEPARATOR + MinRatio
						+ this.SEPARATOR + DecayRatio + this.SEPARATOR;

				System.out.println(params);
	
				fp.readParam(params);

				fp.run();

			} else if (action.equals("progress")) {
				response.setContentType("text/xml;charset=utf-8");
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				String taskID = java.net.URLDecoder.decode(request.getParameter("taskID"),"UTF-8");
				FPAnalyser fp = this.fpTable.get(taskID);
				System.out.println("progress taskID "+taskID);
				int currentLine = fp.getCurLineNum();
				int totalLine = fp.getLineAmount();
				String exceptionString = fp.getExceptionInfo();
				System.out.println("exceptionString is:"+exceptionString);
				if(exceptionString!=null){
					out.println("ERROR:\n"+exceptionString);
					out.flush();
					out.close();
				}
				out.println(currentLine+","+totalLine);
				out.flush();
				out.close();
			}
			else if(action.equals("getTaskInfo")){
				String taskInfo = "";
				if(fpTable.isEmpty()){
					taskInfo = "null";
				}
				else{
					Iterator<String> taskIDs = fpTable.keySet().iterator();
					while(taskIDs.hasNext()){
						String taskID = taskIDs.next();
						FPAnalyser fp = fpTable.get(taskID);
						String inputPath = fp.getInputPath();
						String outputPath = fp.getOutputPath();
						int currentLine = fp.getCurLineNum();
						int totalLine = fp.getLineAmount();
						int count = this.countTable.get(taskID);
						int preNum = this.preNumTable.get(taskID);
						if(preNum>currentLine){
							count++;
							this.countTable.remove(taskID);
							this.countTable.put(taskID, count);
						}
						preNum = currentLine;
						this.preNumTable.remove(taskID);
						this.preNumTable.put(taskID, preNum);
						taskInfo = taskInfo+taskID+","+inputPath+","+outputPath+","+currentLine+","+totalLine+","+count+";";
					}
				}
				response.setContentType("text/xml;charset=utf-8");
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				out.println(taskInfo);
				out.flush();
				out.close();			
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("ERROR:FPAAnalyseServlet");
			System.out.println(e.toString());
			response.setContentType("text/xml;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			out.println("ERROR:\n"+e.toString());
			out.flush();
			out.close();
		}

	}

}
