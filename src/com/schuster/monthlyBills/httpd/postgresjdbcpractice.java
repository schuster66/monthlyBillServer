package com.schuster.monthlyBills.httpd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class postgresjdbcpractice {
	
	public static void buildCategoriesInDB() {
	}

	public static void main(String[] args) {
		
		Statement stmt = null;
		Connection conn = null;
		
		String sql = "select * from test.items";
		
		String url = "jdbc:postgresql://localhost:5432/testdb";
		Properties props = new Properties();
		props.setProperty("user","postgres");
		props.setProperty("password","ou8one2");
		//props.setProperty("ssl","true");
		try {
			conn = DriverManager.getConnection(url, props);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int position = rs.getInt("position");
				System.out.println("Position = " + position);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//String url = "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
		//Connection conn = DriverManager.getConnection(url);
	}
}
