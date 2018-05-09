package net.rating.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.admin.manage.db.MovieBean;

public class RatingDAO {
	//디비연결 메서드
	private Connection getConnection() throws Exception {
		Context init=new InitialContext();
		//자원의 이름 불러오기 자원 위치 java:comp/env 자원이름 jdbc/Mysql
		DataSource ds=(DataSource)init.lookup("java:comp/env/jdbc/Mysql");
		Connection con=ds.getConnection();
		
		return con;
	}
	
	public List randomMovieList(String ra_id){
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="";
		List movieList=new ArrayList();
		int[]randomNum;
		int randomNumCount = 0;
		int check=0;
		try {
			con=getConnection();

			sql="select mv_num from movie order by mv_num desc";
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			//영화 마지막 번호 들고오기
			int lastNum=0;
			if(rs.next()){
				lastNum=rs.getInt("mv_num");
			}
			
			//내가 평가하지 않은 영화가 몇개 남아 있는지,  체크
			//평가하지 않은 영화의 카운터를 세고, 
			//10개 이상인 경우, db확인 으로, 
			//10개 미만인경우, 카운트만큼 배열을 만든다. 

			sql="select count(mv_num) from movie where mv_num not in (select ra_p_num from rating where ra_id='wahchu' and ra_p_num is not null) order by mv_num desc";
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next()){
				if(rs.getInt("count(mv_num)")>=10){
					randomNumCount=10;
				}else{
					randomNumCount=rs.getInt("count(mv_num)");
				}
			}
			

			randomNum=new int[randomNumCount];
			
			for(int i=0; i<randomNum.length;i++){
				Random r= new Random();
				//randomNum[인덱스]=랜덤한 숫자 하나 넣을것.
				randomNum[i]=r.nextInt(lastNum);

				//인덱스에 저장된 넘버가 디비에 있는지 확인 and 평가하지 않은 영화 불러오기
				sql="select * from movie where mv_num=? and mv_num not in (select ra_p_num from rating where ra_id=? and ra_p_num is not null)";
				
				pstmt=con.prepareStatement(sql);
				pstmt.setInt(1, randomNum[i]);
				pstmt.setString(2, ra_id);
				rs=pstmt.executeQuery();
				if(rs.next()){
					//이러면 패스.
					//중복 제거
					for(int j=0;j<i; j++){
						if(randomNum[i]==randomNum[j]){
							check=-1;
						}
					}
				}else{
					check=-1;
					//다시 랜덤생성. 
				}
				
				//중복이 없을때, db에도 있는 번호일때, 되돌려줄 moviebean저장
				if(check!=-1){
					MovieBean moviebean= new MovieBean();
					moviebean.setMv_num(randomNum[i]);
					moviebean.setMv_eng_title(rs.getString("mv_eng_title"));
					moviebean.setMv_kor_title(rs.getString("mv_kor_title"));
					moviebean.setMv_genre(rs.getString("mv_genre"));
					moviebean.setMv_year(rs.getInt("mv_year"));
					movieList.add(moviebean);
				}else{
					check=0;
					i--;
				}
			}
//			무비리스트에 다 다른 주소가 저장되었는지 확인			
//			for (int j = 0; j < 10; j++) {
//				System.out.println(movieList.get(j));
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
			if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
			if(con!=null)try {con.close();} catch (SQLException e) {e.printStackTrace();}
		}
		return movieList;
	}
	
	
	public int ratingCheck(String ra_id, int ra_p_num){
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="";
		int check=0;
		try {
			con=getConnection();
			
			sql="select * from rating where ra_id=? and ra_p_num=? ";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, ra_id);
			pstmt.setInt(2, ra_p_num);
			rs=pstmt.executeQuery();
			
			if(rs.next()){
				check=-1;
			}else{
				check=1;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
			if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
			if(con!=null)try {con.close();} catch (SQLException e) {e.printStackTrace();}
		}
		return check;
	}
	
	
	public void insertRating(RatingBean ratingbean){
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="";
		try {
			con=getConnection();
			
			sql="insert into rating (ra_id,ra_rating,ra_p_num) values(?,?,?)";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, ratingbean.getRa_id());
			pstmt.setInt(2, ratingbean.getRa_rating());
			pstmt.setInt(3, ratingbean.getRa_p_num());
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
			if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
			if(con!=null)try {con.close();} catch (SQLException e) {e.printStackTrace();}
		}
		
	}
	
	public void updateRating(RatingBean ratingbean){
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="";
		try {
			con=getConnection();

			sql="update rating set ra_rating=? where ra_id=? and ra_p_num=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, ratingbean.getRa_rating());
			pstmt.setString(2, ratingbean.getRa_id());
			pstmt.setInt(3, ratingbean.getRa_p_num());
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
			if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
			if(con!=null)try {con.close();} catch (SQLException e) {e.printStackTrace();}
		}
		
	}
	
	
	
	
	
	
	
	
	
	//myrating에서 사용
	
	public int getRatingCount(String id){
		 Connection con=null;
		 String sql="";
		 PreparedStatement pstmt=null;
		 ResultSet rs=null;
		 int count = 0;
		 try{ //예외가 발생할 것 같은 명령, 	필수적으로 외부파일접근, 디비접근
				con = getConnection();
				sql="select count(*) as count from rating where ra_id=?";				 				 
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
						if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
						if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
						if(con!=null)try {con.close();} catch (SQLException e) {e.printStackTrace();}
					}
		 return count;
	 }//end count
	
	public Vector getRatingList(String id, int startRow, int pageSize){
		 List<RatingBean> ratinglist = new ArrayList<RatingBean>();
		 List<MovieBean> movielist = new ArrayList<MovieBean>();
		 Vector vector= new Vector();
		 Connection con=null;
		 String sql="";
		 PreparedStatement pstmt=null;
		 ResultSet rs=null;
		 try{ //예외가 발생할 것 같은 명령, 	필수적으로 외부파일접근, 디비접근
				con = getConnection();
				sql="select * from rating r join movie m on r.ra_p_num=m.mv_num where ra_id=? order by ra_num desc limit ?,?";				 				 
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, id);
				pstmt.setInt(2, startRow-1);
				pstmt.setInt(3, pageSize);
				rs = pstmt.executeQuery();
				
				while(rs.next()){			
					RatingBean rb = new RatingBean();
					rb.setRa_num(rs.getInt("ra_num"));
					rb.setRa_id(rs.getString("ra_id"));					
					rb.setRa_p_num(rs.getInt("ra_p_num"));
					rb.setRa_rating(rs.getInt("ra_rating"));
					ratinglist.add(rb);
					
					MovieBean moviebean= new MovieBean();
					moviebean.setMv_eng_title(rs.getString("mv_eng_title"));
					moviebean.setMv_genre(rs.getString("mv_genre"));
					moviebean.setMv_num(rs.getInt("mv_num"));
					movielist.add(moviebean);
					
				}
				vector.add(ratinglist);
				vector.add(movielist);
				
				//ra_num에 따른 m_num을 설정. 
				// m_num에 따른 무비 정보 저장. 
				//무비리스트에 넣고. 
				//두가지 리스트 합치기. 
				
			} catch(Exception e) {
					//예외 생기면 변수 e에 저장
					//예외를 잡아서 처리 -> 메시지 출력
					e.printStackTrace();
					}finally{
						//예외가 발생하든 말든 상관없이 마무리작업
						//객체 기억장소 마무리
						if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
						if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
						if(con!=null)try {con.close();} catch (SQLException e) {e.printStackTrace();}
					}
		 return vector;
	 }//end list
	
	public RatingBean getRating(String id, int mv_num){ 
	 Connection con=null;
	 String sql="";
	 PreparedStatement pstmt=null;
	 ResultSet rs=null;
	 RatingBean ratingBean = new RatingBean();
	 try{ //예외가 발생할 것 같은 명령, 	필수적으로 외부파일접근, 디비접근
			con = getConnection();
			sql="select * from rating where ra_id =? and ra_p_num=?";				 				 
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setInt(2, mv_num);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				ratingBean.setRa_id(rs.getString("ra_id"));
				ratingBean.setRa_rating(rs.getInt("ra_rating")); //
			}
			
		} catch(Exception e) {
				//예외 생기면 변수 e에 저장
				//예외를 잡아서 처리 -> 메시지 출력
				e.printStackTrace();
				}finally{
					//예외가 발생하든 말든 상관없이 마무리작업
					//객체 기억장소 마무리
					if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
					if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
					if(con!=null)try {con.close();} catch (SQLException e) {e.printStackTrace();}
				}
	 return ratingBean;
}
	public float avgRating(int mv_num){
		 Connection con=null;
		 String sql="";
		 PreparedStatement pstmt=null;
		 ResultSet rs=null;
		 float avg = 0;
		 try{ //예외가 발생할 것 같은 명령, 	필수적으로 외부파일접근, 디비접근
				con = getConnection();
				sql="select avg(ra_rating) as avg from rating where ra_p_num=?";				 				 
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, mv_num);
				rs = pstmt.executeQuery();
				
				if(rs.next()){
					avg=rs.getInt("avg");
				}
				
			} catch(Exception e) {
					//예외 생기면 변수 e에 저장
					//예외를 잡아서 처리 -> 메시지 출력
					e.printStackTrace();
					}finally{
						//예외가 발생하든 말든 상관없이 마무리작업
						//객체 기억장소 마무리
						if(rs!=null)try {rs.close();} catch (SQLException e) {e.printStackTrace();}
						if(pstmt!=null)try {pstmt.close();} catch (SQLException e) {e.printStackTrace();}
						if(con!=null)try {con.close();} catch (SQLException e) {e.printStackTrace();}
					}
		 return avg;
	}
	
}
