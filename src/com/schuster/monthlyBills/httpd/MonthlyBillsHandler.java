package com.schuster.monthlyBills.httpd;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class MonthlyBillsHandler extends AbstractHandler {

	@Override
	public void handle(String target, 
						Request baseRequest,
						HttpServletRequest request, 
						HttpServletResponse response)
			throws IOException, ServletException {
		
		 response.setContentType("text/html; charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);

	        PrintWriter out = response.getWriter();

	        out.println("<h1>" + "Hello Worrrrrld" + "</h1>");
	        out.println("<h2>" + "target = " + target + "</h2>");
	        out.println("<h2>" + baseRequest.toString() + "</h2>");
	        out.println("<h2>" + request.toString() + "</h2>");
	        out.println("<h2>" + "headers = ");
	        
	        	out.println(baseRequest.getHeaderNames());

	        out.println("/h2>");
	      

	        baseRequest.setHandled(true);
		
	}
	
}
