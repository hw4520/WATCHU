package net.mypage.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.admin.manage.db.MovieBean;
import net.favorite.db.FavoriteBean;
import net.member.db.MemberBean;

public class AlarmDAO {
	//디비연결 메서드
	private Connection getConnection() throws Exception {
		Context init=new InitialContext();
		//자원의 이름 불러오기 자원 위치 java:comp/env 자원이름 jdbc/Mysql
		DataSource ds=(DataSource)init.lookup("java:comp/env/jdbc/Mysql");
		Connection con=ds.getConnection();
		
		return con;
	}
	
	public int getAlarmCount(String id){
		 Connection con=null;
		 String sql="";
		 PreparedStatement pstmt=null;
		 ResultSet rs=null;
		 int count = 0;
		 try{ //예외가 발생할 것 같은 명령, 	필수적으로 외부파일접근, 디비접근
				con = getConnection();
				sql="select count(*) as count from alarm where a_id=?";				 				 
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				
				if(rs.next()){
					count = rs.getInt("count");
				}
				
			} catch(Exception e) {
					//예외 생기면 변수 e에 저장
					//예외를 잡아서 처리 -> 메시지 출력
					e.printStackTrace();
					}finally{
						//예외가 발생하든 말든 상관없이 마무리작업
						//객체 기억장소 마무리
						
						if(rs!=null){
							try{rs.close();
							}catch(SQLException e){
								e.printStackTrace();
							 }
							}//end if
						if(pstmt!=null){
							try{pstmt.close();						
							}catch(SQLException e){
								e.printStackTrace();
							}
						 }//end if
							if(con!=null){
								try{con.close();
								}catch(SQLException e){
									e.printStackTrace();
								 }
								}//end if
					}
		 return count;
	 }//end count
	
	public int getCount(String id){ //확인 안 한 알람 갯수 구하는 메소드
		 Connection con=null;
		 String sql="";
		 PreparedStatement pstmt=null;
		 ResultSet rs=null;
		 int count = 0;
		 try{ //예외가 발생할 것 같은 명령, 	필수적으로 외부파일접근, 디비접근
				con = getConnection();
				sql="select count(*) as count from alarm where a_id=? and a_check=0";				 				 
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
				
				if(rs.next()){
					count = rs.getInt("count");
				}
				
			} catch(Exception e) {
					//예외 생기면 변수 e에 저장
					//예외를 잡아서 처리 -> 메시지 출력
					e.printStackTrace();
					}finally{
						//예외가 발생하든 말든 상관없이 마무리작업
						//객체 기억장소 마무리
						
						if(rs!=null){
							try{rs.close();
							}catch(SQLException e){
								e.printStackTrace();
							 }
							}//end if
						if(pstmt!=null){
							try{pstmt.close();						
							}catch(SQLException e){
								e.printStackTrace();
							}
						 }//end if
							if(con!=null){
								try{con.close();
								}catch(SQLException e){
									e.printStackTrace();
								 }
								}//end if
					}
		 return count;
	 }//end count
		
	public Vector getAlarmlist(String id,int startRow, int pageSize){
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;	
		ResultSet rs2=null;
		String sql="";
		Vector vector=new Vector();
		List alarmList=new ArrayList();
		List movieList=new ArrayList();
		List memberList=new ArrayList();
		List userList=new ArrayList();		
		try {
			con=getConnection();
			sql="select * from alarm where a_id=? order by a_num desc limit ?,?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setInt(2, startRow-1);
			pstmt.setInt(3, pageSize);
			rs=pstmt.executeQuery();			
			while(rs.next()){				
				AlarmBean ab=new AlarmBean();
				ab.setA_id(rs.getString("a_id"));
				ab.setA_num(rs.getInt("a_num"));					
				ab.setA_end_day(rs.getString("a_end_day"));
				ab.setA_start_day(rs.getString("a_start_day"));
				ab.setA_alarm_name(rs.getInt("a_alarm_name"));
				ab.setA_movie_name(rs.getString("a_movie_name"));
				ab.setA_check(rs.getInt("a_check"));
				ab.setA_follower(rs.getString("a_follower"));
				ab.setA_forAdmin(rs.getString("a_forAdmin"));
				alarmList.add(ab);
				sql="select * from movie where mv_kor_title=?";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, ab.getA_movie_name());
				rs2=pstmt.executeQuery();
				if(rs2.next()){
					MovieBean mb=new MovieBean();
					mb.setMv_num(rs2.getInt("mv_num"));					
					movieList.add(mb);
				}
				sql="select * from member where m_id=?";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, ab.getA_follower());
				rs2=pstmt.executeQuery();
					if(rs2.next()) {
						MemberBean memb=new MemberBean();
						memb.setM_name(rs2.getString("m_name"));
						memb.setM_id(rs2.getString("m_id"));
						memberList.add(memb);
					}else {
						MemberBean memb=new MemberBean();
						memb.setM_name("존재하지 않는 아이디");
						memberList.add(memb);
					}
				sql="select * from member where m_id=?";
					pstmt=con.prepareStatement(sql);					
					pstmt.setString(1,ab.getA_forAdmin());
					rs2=pstmt.executeQuery();
						if(rs2.next()) {
							MemberBean memb=new MemberBean();
							memb.setM_name(rs2.getString("m_name"));
							memb.setM_id(rs2.getString("m_id"));
							userList.add(memb);
						}else{							
							MemberBean memb=new MemberBean();
							memb.setM_name("존재하지 않는 아이디");							
							userList.add(memb);
						}

			}//while
			vector.add(alarmList);
			vector.add(movieList);
			vector.add(memberList);
			vector.add(userList);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs2!=null)try{rs2.close();}catch(SQLException ex){}
			if(rs!=null)try{rs.close();}catch(SQLException ex){}
			if(pstmt!=null)try{pstmt.close();}catch(SQLException ex){}
			if(con!=null)try{con.close();}catch(SQLException ex){}
		}
		return vector;
	}
	
	public List<AlarmBean> getAlarms(String id){
		 List<AlarmBean> alarmlist = new ArrayList<AlarmBean>();
		 Connection con=null;
		 String sql="";
		 PreparedStatement pstmt=null;
		 ResultSet rs=null;
		 try{ //예외가 발생할 것 같은 명령, 	필수적으로 외부파일접근, 디비접근
				con = getConnection();
				sql="select * from alarm where a_id=? order by a_num desc";				 				 
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, id);				
				rs = pstmt.executeQuery();
				
				while(rs.next()){					 
					AlarmBean ab = new AlarmBean();
					ab.setA_id(rs.getString("a_id"));
					ab.setA_num(rs.getInt("a_num"));					
					ab.setA_end_day(rs.getString("a_end_day"));
					ab.setA_alarm_name(rs.getInt("a_alarm_name"));
					ab.setA_movie_name(rs.getString("a_movie_name"));
					alarmlist.add(ab);
					}
				
			} catch(Exception e) {
					//예외 생기면 변수 e에 저장
					//예외를 잡아서 처리 -> 메시지 출력
					e.printStackTrace();
					}finally{
						//예외가 발생하든 말든 상관없이 마무리작업
						//객체 기억장소 마무리
						
						if(rs!=null){
							try{rs.close();
							}catch(SQLException e){
								e.printStackTrace();
							 }
							}//end if
						if(pstmt!=null){
							try{pstmt.close();						
							}catch(SQLException e){
								e.printStackTrace();
							}
						 }//end if
							if(con!=null){
								try{con.close();
								}catch(SQLException e){
									e.printStackTrace();
								 }
								}//end if
					}
		 return alarmlist;
	 }//end list
	
	public void insertAlarm(AlarmBean ab){
		Connection con=null;
		String sql="";
		PreparedStatement pstmt=null;
		try{ //예외가 발생할 것 같은 명령, 	필수적으로 외부파일접근, 디비접근
			 con = getConnection();
			 sql="insert into alarm (a_id,a_alarm_name,a_end_day,a_start_day,a_movie_name,a_follower,a_forAdmin) values(?,?,?,?,?,?,?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, ab.getA_id()); 
			pstmt.setInt(2, ab.getA_alarm_name()); 
			pstmt.setString(3, ab.getA_end_day()); 
			pstmt.setString(4, ab.getA_start_day());
			pstmt.setString(5, ab.getA_movie_name()); 
			pstmt.setString(6, ab.getA_follower());
			pstmt.setString(7, ab.getA_forAdmin());
			pstmt.executeUpdate();
		} catch(Exception e) {
				//예외 생기면 변수 e에 저장
				//예외를 잡아서 처리 -> 메시지 출력
				e.printStackTrace();
				}finally{
					//예외가 발생하든 말든 상관없이 마무리작업
					//객체 기억장소 마무리
					if(pstmt!=null){
						try{pstmt.close();						
						}catch(SQLException e){
							e.printStackTrace();
						}
					 }
						if(con!=null){
							try{con.close();
							}catch(SQLException e){
								e.printStackTrace();
							 }
							}
				}
		}//insertAlarm() 메서드
	
	public void deleteAlarm(int a_num){
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="";
		try {
			//1,2 디비연결
			con=getConnection();			
			sql="delete from alarm where a_num=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, a_num);
			//4 실행
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null)try{rs.close();}catch(SQLException ex){}
			if(pstmt!=null)try{pstmt.close();}catch(SQLException ex){}
			if(con!=null)try{con.close();}catch(SQLException ex){}
		}
	}//end deleteAlarm
	
	public void checkAlarm(String id){
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="";
		try {
			//1,2 디비연결
			con=getConnection();			
			sql="update alarm set a_check=1 where a_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, id);
			//4 실행
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null)try{rs.close();}catch(SQLException ex){}
			if(pstmt!=null)try{pstmt.close();}catch(SQLException ex){}
			if(con!=null)try{con.close();}catch(SQLException ex){}
		}
	}//check
	
}
