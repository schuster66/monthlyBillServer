package com.schuster.monthlyBills.httpd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class postgressObject {
	
	private final String user = "monthly";
	private final String password = "twasTheNightBeforeChristmas";
	private final String host = "localhost";
	private final int port = 5432;
	private final String dbName = "monthlyBills";
	private final Properties props;
	
	private Connection conn;
	
	public postgressObject() {
		props = new Properties();
		props.setProperty("user",user);
		props.setProperty("password",password);
	}
	
	
	
	public Connection getConnection() {
		
		try {
			if (this.conn == null || !this.conn.isValid(2000)) {
				connect();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return conn;
	}
	
	private void connect() throws SQLException {
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
		conn = DriverManager.getConnection(url, props);
	}
	
	public ResultSet doSelect(String sql) throws SQLException {
		
		Connection conn = this.getConnection();
		Statement stmt = null;
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		
		//stmt.close();
		return rs;
	}
	
	public void doInsert(String sql) throws SQLException {
		Connection conn = this.getConnection();
		Statement stmt = null;
		stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}
	
	public void createCategories() {
		ResultSet rs;
		String sql = "select key, displayname, position, startdate, enddate, firsthalf, intotal from categories";
		try {
			rs = this.doSelect(sql);
			while (rs.next()) {
				String key = rs.getString(1);
				String displayName = rs.getString(2);
				int position = rs.getInt(3);
				String startDate = rs.getString(4);
				String endDate = rs.getString(5);
				boolean firstHalf = rs.getBoolean(6);
				boolean inTotal = rs.getBoolean(7);
				
				monthlyCategory.buildNewCategory(key, displayName, position, startDate, endDate, firstHalf, inTotal);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertOrUpdateBill(String key, String yearMonth, String payDate, double amount) {
		System.out.println("Updating key = " + key + " yearmonth = " + yearMonth);
		
		// Check to see if record exists for this key and yearmonth
		String checksql = "select * from entries where yearmonth = to_date('" + yearMonth + "', 'YYYY-MM') and category = '" + key + "'";
	    System.out.println("checksql = " + checksql);
	    
	    ResultSet rs = null;
	    try {
			rs = this.doSelect(checksql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    try {
			if (rs.next()) {
				String sql = "Update entries set paydate = to_date('" + payDate + 
								"', 'YYYY-MM-DD'), amount = " + amount + " where yearmonth = to_date('" + yearMonth + 
								"', 'YYYY-MM') and category = '" + key + "'";
				System.out.println("Update sql = " + sql);
				this.doInsert(sql);
			} else {
				String sql = "INSERT INTO entries (category, yearmonth, paydate, amount) VALUES ('" + 
						 key + "', to_date('" + yearMonth + "', 'YYYY-MM'), to_date('" + payDate + "', 'YYYY-MM-DD'), " + amount + ")";
				System.out.println("Insert SQL =" + sql);
				this.doInsert(sql);
				
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    /*
		String sql = "INSERT INTO entries (category, yearmonth, paydate, amount) VALUES ('" + 
					 key + "', to_date('" + yearMonth + "', 'YYYY-MM'), to_date('" + payDate + "', 'YYYY-MM-DD'), " + amount + ")";
		System.out.println("SQL =" + sql);
		try {
			this.doInsert(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//'MendozaMortgage',to_date('2017-11', 'YYYY-MM'), to_date('2017-11-26', 'YYYY-MM-DD'), 325.00
		
		
	}
	
	public String getBillsForMonth(int year, int month) {
		
		JSONObject allJSON = new JSONObject();
		JSONArray list = new JSONArray();
		
		HashMap<String, thisMonth> thisMonths = new HashMap<String,thisMonth>();
		HashMap<String, lastMonth> lastMonths = new HashMap<String,lastMonth>();
		String yearMonth = year + "-" + month;
		
		Date tryDate = null;
		SimpleDateFormat tryformat = new SimpleDateFormat("yyyy-MM");
		try {
			tryDate = tryformat.parse(yearMonth);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// First do sql and get a list of bills for this month
		ResultSet rs;
		String sql = "select category, paydate, amount from entries where " + 
					 "yearmonth = to_date('" + yearMonth + "', 'YYYY-MM')";
		try {
			rs = this.doSelect(sql);
			while (rs.next()) {
				String key = rs.getString(1);
				String payDate = rs.getString(2);
				String amount = rs.getString(3);
				thisMonth tm = new thisMonth(payDate,amount);
				thisMonths.put(key, tm);
				//System.out.println("key = " + key + " - paydate = " + payDate + " - amount = " + amount);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Now do sql to get last months bills
		
		ResultSet rs2;
		String sql2 = "select category, paydate, amount from entries where " + 
					 "yearmonth = to_date('" + yearMonth + "', 'YYYY-MM') - interval '1 month'";
		try {
			rs2 = this.doSelect(sql2);
			while (rs2.next()) {
				String key = rs2.getString(1);
				String payDate = rs2.getString(2);
				String amount = rs2.getString(3);
				lastMonth lm = new lastMonth(payDate,amount);
				lastMonths.put(key, lm);
				//System.out.println("key = " + key + " - paydate = " + payDate + " - amount = " + amount);
			}
			rs2.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Now loop through each category and print what you need.  print this months paydate and amount if available
		// print last months paydate and amount if available.
		
		for (monthlyCategory mc : monthlyCategory.getCategoriesInOrder()) {
			
			JSONObject monthObj = new JSONObject();
			// if it's not between the start date and end date then just go to the next one
		    // only worry about categories that are within the date given
			Date startDate = null;
			Date endDate = null;
			
			
			//System.out.println("start = " + mc.startDate() + " - end = " + mc.endDate());
			SimpleDateFormat dbformat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				startDate = dbformat.parse(mc.startDate());
				endDate = dbformat.parse(mc.endDate());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (tryDate.before(startDate) || tryDate.after(endDate)) {
				//System.out.println("Skipping this one " + mc.displayName());
				continue;
			}
			
	        monthObj.put("item", mc.displayName());
	        monthObj.put("key", mc.key());
	        monthObj.put("firsthalf", mc.firstHalf());
	        monthObj.put("intotal",mc.inTotal());
			//System.out.print(mc.displayName() );
			//System.out.print("|||");
			
	        String lastMonthString = "";
			if (lastMonths.containsKey(mc.key())) {
				lastMonthString = lastMonths.get(mc.key()).date + " - " + lastMonths.get(mc.key()).amount;
				//System.out.print(lastMonths.get(mc.key()).date + " " + lastMonths.get(mc.key()).amount);
			}
			monthObj.put("lastMonth", lastMonthString);
			//System.out.print("|||");
			
			String thisMonthDate = "";
			String thisMonthAmount = "";
			if (thisMonths.containsKey(mc.key())) {
				thisMonthDate = thisMonths.get(mc.key()).date;
				thisMonthAmount = thisMonths.get(mc.key()).amount;
				
				//System.out.print(thisMonths.get(mc.key()).date);
				//System.out.print("|||");
				//System.out.println(thisMonths.get(mc.key()).amount);
			} 
			thisMonthAmount = thisMonthAmount.replace("$", "");
			monthObj.put("payDate", thisMonthDate);
			monthObj.put("payAmount", thisMonthAmount);
			list.add(monthObj);
		}
		
		
		//System.out.println(list.toJSONString());
		return list.toJSONString();
		
	}
	
	public static void main(String[] args) {
		
		
		postgressObject po = new postgressObject();
		po.createCategories();
		
		System.out.println(po.getBillsForMonth(2017,12));
		System.out.println(po.getBillsForMonth(1999,6));
		
		po.insertOrUpdateBill("Wireless", "1999-05", "1999-06-12", 325.00);
		po.insertOrUpdateBill("Wireless", "1999-05", "1999-06-11", 625.00);
		po.insertOrUpdateBill("Wireless", "2017-09", "2017-08-24", 176.18);
		
		/*
		for (monthlyCategory mc : monthlyCategory.getCategoriesInOrder()) {
			System.out.println(mc.displayName() + "   " + mc.position());
			
		}
		*/
		
	}
	
	private class thisMonth {
		private String date = "";
		private String amount = "";
		
		private thisMonth(String date, String amount) {
			this.amount = amount;
			this.date = date;
		}
	}
	private class lastMonth {
		private String date = "";
		private String amount = "";
		
		private lastMonth(String date, String amount) {
			this.amount = amount;
			this.date = date;
		}
	}
}
