package net.category.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class RecChkDAO {
	//디비연결 메서드
	private Connection getConnection() throws Exception {
		Context init=new InitialContext();
		//자원의 이름 불러오기 자원 위치 java:comp/env 자원이름 jdbc/Mysql
		DataSource ds=(DataSource)init.lookup("java:comp/env/jdbc/Mysql");
		Connection con=ds.getConnection();
		
		return con;
	}
	
	public int reccomendCheck(int r_num, String id) {
		
		int check = 1;
		
		Connection con=null;
		String sql="";
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			con = getConnection();
			
			sql = "select * from rec_chk where re_num = ? && re_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, r_num);
			pstmt.setString(2, id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				check = 0;	// 추천 한번 했을때
			}else {
				sql = "insert into rec_chk(re_num,re_id) values(?,?)";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, r_num);
				pstmt.setString(2, id);
				pstmt.executeUpdate();
				
				check = 1;	// 추천 안했을때
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if(rs!=null)try{rs.close();}catch(SQLException e){};
			if(pstmt!=null)try{pstmt.close();}catch(SQLException e){};
			if(con!=null)try{con.close();}catch(SQLException e){};
		}
		return check;
	}

	
}
