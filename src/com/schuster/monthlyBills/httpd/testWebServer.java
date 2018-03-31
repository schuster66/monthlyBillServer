package com.schuster.monthlyBills.httpd;

import java.io.File;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

public class testWebServer implements Runnable{
	
	public static postgressObject po;
	
	public testWebServer(postgressObject po) {
		this.po = po;
	}
	

	@Override
	public void run() {
		Server server = new Server(9595);

		 //This context basically just serves html in whatever directory you set in dir0
		 ResourceHandler rh0 = new ResourceHandler();
		 
		 // This line allows you to save the file after getting it in Windows.  Took
		 // a lot of searching.  Probably can comment this out when going to production, but
		 // I'm not sure it has any value on linux machines anyway.
		 rh0.setMinMemoryMappedContentLength(-1);
		 
	     ContextHandler context0 = new ContextHandler();
	     context0.setContextPath("/");
	     File dir0 = new File("/Users/tom/Desktop/adminstrap");
	     context0.setBaseResource(Resource.newResource(dir0));
	     //context0.setInitParameter( "org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
	     context0.setHandler(rh0);
	     
	     ServletContextHandler context3 = new ServletContextHandler();
	     context3.addServlet(monthlyBillsServlet.class, "/mbills");
 
	     ContextHandlerCollection contexts = new ContextHandlerCollection();
	     contexts.setHandlers(new Handler[] { context0, context3});

	     server.setHandler(contexts);

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
		
		System.out.println("here we are");
		
		// Build up the categories from the database
		postgressObject po = new postgressObject();
		po.createCategories();
		
		//monthlyBillsWebServerMain.po = po;
		testWebServer mbws = new testWebServer(po);
		new Thread(mbws).start();
	}
	


}
