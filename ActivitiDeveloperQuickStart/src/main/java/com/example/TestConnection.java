package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
		public static void main(String[] args) throws SQLException {
			new org.postgresql.Driver();
			Connection conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "12345"); 
			System.out.println(conn.getSchema());
			conn.close();
		}
}
