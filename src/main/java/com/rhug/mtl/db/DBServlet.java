package com.rhug.mtl.db;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.stream.JsonWriter;

/**
 * Servlet implementation class DBServlet
 */
@WebServlet("/DBServlet")
public class DBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `countries` ( "+
					  								"`id` int(11) NOT NULL,"+
					  								"`city` varchar(50) NOT NULL,"+
					  								"`country` varchar(50) NOT NULL,"+
					  								" PRIMARY KEY (`id`)" +
					  								") ENGINE=MyISAM  DEFAULT CHARSET=utf8;";
					
	private static final String INSERT_SQL =    " INSERT IGNORE INTO `countries` (`id`, `city`, `country`) VALUES "+
												" (1, 'Montreal', 'Canada'), " +
												" (2, 'Toronto', 'Canada'), " +
												" (3, 'New York', 'USA'), "+
												" (4, 'Tokyo', 'Japan')," +
												" (5, 'Moscow', 'Russia'); ";
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DBServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		perform(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		perform(request,response);
	}
	
	private void perform(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException  {
		res.setContentType("application/json; charset=UTF-8");
		res.setCharacterEncoding("UTF-8");
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(res.getOutputStream(), "UTF-8"));
		Connection conn = null;
		try{
		    InitialContext ic = new InitialContext();
		    Context initialContext = (Context) ic.lookup("java:comp/env");
		    DataSource datasource = (DataSource) initialContext.lookup("jdbc/MySQLDS");
		    conn = datasource.getConnection();
	        Statement stmt = conn.createStatement();
	        stmt.executeUpdate(CREATE_TABLE_SQL);
	        stmt.close();
	        stmt = conn.createStatement();
	        stmt.executeUpdate(INSERT_SQL);
	        stmt.close();
	        stmt=conn.createStatement();
	        String query = "select * from Countries;";
	        ResultSet rs = stmt.executeQuery(query);
	        ResultSetMetaData rsmd= rs.getMetaData();
	        while(rs.next()) {
	        	 writer.beginObject();
	        	   for(int idx=1; idx<=rsmd.getColumnCount(); idx++) {
	        	     writer.name(rsmd.getColumnLabel(idx)); 
	        	     writer.value(rs.getString(idx));
	        	   }
	        	   writer.endObject();
	        	}
	        	
		}
		catch (NamingException nameExc)
		{
			throw new IOException("invalid datasource");
		}
		catch (SQLException sqlExc)
		{
			throw new IOException("invalid SQL");
		}
		finally
		{
			if(writer!=null)
			{
			   writer.close();
			}
			res.getOutputStream().flush();
		}
		
	}
}
