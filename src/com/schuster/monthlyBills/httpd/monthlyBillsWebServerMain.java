package com.schuster.monthlyBills.httpd;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.Handler;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class monthlyBillsWebServerMain implements Runnable{
	
	public static postgressObject po;
	

	@Override
	public void run() {
		Server server = new Server(9595);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        //server.setHandler(context);
        
        ContextHandler context2 = new ContextHandler("/monthlyBills");
        MonthlyBillsHandler handler = new MonthlyBillsHandler();
        context2.setHandler(handler);
       // ServletHandler handler = new HelloHandler();
        //server.setHandler(handler);
     //   handler.addServletWithMapping(HelloServlet.class, "/echo*");
        
        ServletContextHandler context3 = new ServletContextHandler();
        context3.addServlet(monthlyBillsServlet.class, "/mbills");
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] {context3, context2, context});
        
        server.setHandler(contexts);
        //server.setHandler(handler);


        
        //URL urlStatics = Thread.currentThread().getContextClassLoader().getResource("index.html");
        // Objects.requireNonNull(urlStatics,"Unable to find index.html in classpath");
        //String urlBase = urlStatics.toExternalForm().replaceFirst("/[^/]*$","/");
        String urlBase = "/Users/tom/Desktop/adminstrap";
        System.out.println("Url base = " + urlBase);
        ServletHolder defHolder = new ServletHolder("default",new DefaultServlet());
        defHolder.setInitParameter("resourceBase",urlBase);
        defHolder.setInitParameter("dirAllowed","true");
        context.addServlet(defHolder,"/*");

        try
        {
            server.start();
            server.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		
	}
	
	public static void main(String[] args) { 
		
		// Build up the categories from the database
		postgressObject po = new postgressObject();
		po.createCategories();
		
		monthlyBillsWebServerMain.po = po;
		monthlyBillsWebServerMain mbws = new monthlyBillsWebServerMain();
		new Thread(mbws).start();
	}
	


}
