package com.schuster.monthlyBills.httpd;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class monthlyBillsServlet extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		String yearMonth = request.getParameter("yearmonth");
		String key = request.getParameter("key");
		String payDate = request.getParameter("paydate");
		String amount = request.getParameter("amount");
		
		System.out.println("key = " + key + " - yearmonth = " + yearMonth + " - paydate = " + payDate + " - amount = " + amount);
		
		testWebServer.po.insertOrUpdateBill(key, yearMonth, payDate, Double.parseDouble(amount));
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		String month = request.getParameter("month");
		String year = request.getParameter("year");
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		
		//response.getWriter().printf("you are here month = " + month + " - year = " + year);
		response.getWriter().printf(testWebServer.po.getBillsForMonth(Integer.parseInt(year), Integer.parseInt(month)));
			
	}

}
