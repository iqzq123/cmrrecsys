package com;
import java.io.IOException;
import java.io.PrintWriter;

import com.cn.helloworld;


import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class getString extends HttpServlet{

	
	public getString(){
		
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void processRequest(HttpServletRequest req,HttpServletResponse res) throws Exception 
	{
		System.out.println("save config path----------------");
		String action=req.getParameter("get");
		if(action.equals("get"))
			get(req,res);		
	}
	
	private void get(HttpServletRequest req, HttpServletResponse res) throws Exception
	{
//		Analyser b=new Analyser();
//		b.setInputPath("E:/data/pagevisit/test.txt");
//		b.setSiteDataPath("E:/data");		
//		b.run();
//		System.out.println("success");
//		helloworld hw = new helloworld();
//		PrintWriter out = res.getWriter();
//		out.println(hw.getStr());
	}

}
