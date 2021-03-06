package net.member.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.member.action.Sha256;

public class MemberDAO {
	
private Connection getConnection() throws Exception {
		
		Context init = new InitialContext();
		DataSource ds=(DataSource)init.lookup("java:comp/env/jdbc/Mysql");
		Connection con= ds.getConnection();
		return con;
		
}
public void  insertMember(MemberBean memberbean) {
	 Connection con = null;
	 String sql =null;
	 PreparedStatement pstmt =null;
	 int pic=(int)(Math.random()*10)+1;
	 try {
		con=getConnection();
		
		sql = "insert into member(m_id, m_pass, m_name,m_id_num1,m_id_num2,m_pay,m_grade, m_reg_date,m_pic,m_salt) values(?,?,?,?,?,?,?,?,?,?)";
		 pstmt = con.prepareStatement(sql);
		pstmt.setString(1, memberbean.getM_id());			
		pstmt.setString(2, memberbean.getM_pass());		
		pstmt.setString(3, memberbean.getM_name());	
		pstmt.setInt(4, memberbean.getM_id_num1());	
		pstmt.setInt(5, memberbean.getM_id_num2());	
		pstmt.setInt(6, 0);	//pay
		pstmt.setInt(7, 0); //grade	
		pstmt.setDate(8, memberbean.getM_reg_date());
		pstmt.setInt(9, pic);
		pstmt.setString(10, memberbean.getM_salt());
		pstmt.executeUpdate();
	}catch(Exception e) {
		//예외를 잡아서 처리 --> 메세지 출력 
		e.printStackTrace();
		
	}finally {
		//예외가 발생하든 말든 상관없이 마무리작업
		//객체 기억장소 마무리
		if(con!=null)try {con.close();}catch(SQLException ex) {};
		if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {};
	}
}//insertMember() 메서드

	public int idcheck(String m_id) {
		int check =0;
		Connection con=null;
		String sql =null;
		PreparedStatement pstmt =null;
		ResultSet rs =null;
		try {
			con=getConnection();
			
		sql="select * from member where m_id = ?";
		 pstmt= con.prepareStatement(sql);
		pstmt.setString(1, m_id);
		 rs= pstmt.executeQuery();
		if(rs.next()){
			if(m_id.equals(rs.getString("m_id"))){
				check=1;
			}else {
				check=0;
			}
		
		}

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {};
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {};
			if(con!=null)try {con.close();}catch(SQLException ex) {};
			
		}return check;
	}
	public int iddup(String m_id) {
		int check =0;
		Connection con=null;
		String sql =null;
		PreparedStatement pstmt =null;
		ResultSet rs =null;
		try {
			con=getConnection();
			
		sql="select m_id from member where m_id = ?";
		 pstmt= con.prepareStatement(sql);
		pstmt.setString(1, m_id);
		 rs= pstmt.executeQuery();
		if(rs.next()){
				check=1;
			}else {
				check=0;
			}
		

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {};
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {};
			if(con!=null)try {con.close();}catch(SQLException ex) {};
			
		}return check;
	}
	//중복체크 
	public int namedup(String m_name) {
		int check =0;
		Connection con=null;
		String sql =null;
		PreparedStatement pstmt =null;
		ResultSet rs =null;
		try {
			con=getConnection();
			
		sql="select m_name from member where m_name = ?";
		 pstmt= con.prepareStatement(sql);
		pstmt.setString(1, m_name);
		 rs= pstmt.executeQuery();
		if(rs.next()){
			check=1;
		}else {
			check=0;
		}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {};
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {};
			if(con!=null)try {con.close();}catch(SQLException ex) {};
			
		}return check;
	}
	
	public int userCheck(String m_id,String m_pass) {
		int check =-1;
		Connection con=null;
		String sql =null;
		PreparedStatement pstmt =null;
		ResultSet rs =null;
		try {
			con=getConnection();
			
			sql="select * from member where m_id=?";
			pstmt= con.prepareStatement(sql);
			pstmt.setString(1, m_id);
			rs= pstmt.executeQuery();

			if(rs.next()){
				//비밀번호 암호화
				String salt=rs.getString("m_salt");
				String passText=Sha256.encrypt(m_pass, salt);
				
				if(passText.equals(rs.getString("m_pass"))){
					check=1; //로그인성공
				}else {
					check=0; //비번틀릴경우
				}
			}else {
				check=-1; //아이디없을경우
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {};
			if(con!=null)try {con.close();}catch(SQLException ex) {};
			if(rs!=null)try {rs.close();}catch(SQLException ex) {};
		}return check;
	}
	
	public MemberBean getMember(String m_id) {
		MemberBean memberbean = null;
		String sql =null;
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		Connection con = null;
		try {
			con=getConnection();
			
			sql = "select m_id,m_pass,m_name,m_grade,lpad(m_id_num1,6,0) as m_id_num1,m_id_num2,m_reg_date,m_pay,m_pic from member where m_id=?"; 
			 pstmt = con.prepareStatement(sql);
			pstmt.setString(1, m_id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {		
				memberbean = new MemberBean();
				memberbean.setM_id(rs.getString("m_id"));  
				memberbean.setM_pass(rs.getString("m_pass"));  
				memberbean.setM_name(rs.getString("m_name"));  
				memberbean.setM_grade(rs.getInt("m_grade"));
				memberbean.setM_id_num1(rs.getInt("m_id_num1"));
				memberbean.setM_id_num2(rs.getInt("m_id_num2"));
				memberbean.setM_reg_date(rs.getDate("m_reg_date"));
				memberbean.setM_pay(rs.getInt("m_pay"));
				memberbean.setM_pic(rs.getInt("m_pic"));
				
			}else {
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			
		}finally {
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {};
			if(con!=null)try {con.close();}catch(SQLException ex) {};
			if(rs!=null)try {rs.close();}catch(SQLException ex) {};
		}
		return memberbean;
	}
	
	public int EmailChecked(String m_id){
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		int Echeck=0;
		String sql ="";
		Connection con = null;
		try {
			con=getConnection();
			sql = "SELECT m_grade FROM member WHERE m_id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, m_id);
			rs = pstmt.executeQuery();

			while(rs.next()) {
						if(rs.getInt("m_grade")==0) {
							Echeck=0;
						}else {
							Echeck=1;
						}
							
					
			}

		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//예외가 발생하든 말든 상관없이 마무리작업
			//객체 기억장소 마무리
		 if(rs!=null)try{rs.close();}catch(SQLException e){e.printStackTrace();}
		 if(pstmt!=null)try{pstmt.close();}catch(SQLException e){e.printStackTrace();}
		 if(con!=null)try{con.close();}catch(SQLException e){e.printStackTrace();}
		}
		return Echeck;
	}

	public void setUserEmailChecked(String m_id) {
		PreparedStatement pstmt = null;
		String sql =null;
		Connection con = null;
		try {
			con=getConnection();
			sql = "update member SET m_grade=? WHERE m_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "1");
			pstmt.setString(2, m_id);
			pstmt.executeUpdate();
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(con!=null)try {con.close();}catch(SQLException ex) {};
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {};
		}
	}

	public void passUpdateMember(String m_pass,String m_id){			
		Connection con=null;
		String sql="";
		PreparedStatement pstmt=null;
		try{
			//생성한 난수를 암호화, salt 재설정
			String salt = Sha256.getSalt();
			String passText=Sha256.encrypt(m_pass, salt);
		
		con = getConnection();	
		sql="update member set m_pass=?, m_salt=? where m_id=?";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1,passText);
		pstmt.setString(2,salt);
		pstmt.setString(3,m_id);
		pstmt.executeUpdate();			
		}catch(Exception e) {
			e.printStackTrace();
			}finally{
			 if(pstmt!=null){try{pstmt.close();}catch(SQLException e){e.printStackTrace();} }
			 if(con!=null){try{con.close();}catch(SQLException e){e.printStackTrace(); }}				
			}
	}//end  of passUpdateMember
	public String connectEmail(String m_id){
		String to1=m_id; // 
		String host="smtp.googlemail.com"; // 
		String subject="와츄 임시비밀번호 발급"; // 
		String fromName="관리자"; // 
		String from="wkdwodn22@gmail.com"; 
		String authNum=authNum(); // 
		String content="임시비밀번호 ["+authNum+"]"; //
		try{
			passUpdateMember(authNum,to1);
		Properties props=new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", host);
		props.setProperty
                       ("mail.smtp.socketFactory.class",
                        "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.port","587");
		props.put("mail.smtp.user",from);
		props.put("mail.smtp.auth","true");
		
		Session mailSession 
           = Session.getInstance(props,new javax.mail.Authenticator(){
			    protected PasswordAuthentication getPasswordAuthentication(){
				    return new PasswordAuthentication
                                        ("wkdwodn22","s8949005"); // naver�④쑴�젟
			}
		});
		
		Message msg = new MimeMessage(mailSession);
		InternetAddress []address1 = {new InternetAddress(to1)};
		msg.setFrom(new InternetAddress
                      (from, MimeUtility.encodeText(fromName,"utf-8","B")));
		msg.setRecipients(Message.RecipientType.TO, address1); // 獄쏆룆�뮉占쎄텢占쎌뿺 占쎄퐬占쎌젟
		msg.setSubject(subject); 
		msg.setSentDate(new java.util.Date());
		msg.setContent(content,"text/html; charset=utf-8"); // 占쎄땀占쎌뒠占쎄퐬占쎌젟
		
		Transport.send(msg); 
		}catch(MessagingException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return authNum;
	}

	public static String authNum(){
		StringBuffer buffer=new StringBuffer();
		for(int i=0;i<=4;i++){
			int num=(int)(Math.random()*9+1);
			buffer.append(num);
		}
		return buffer.toString();
	} //end of authNum()
	
	public boolean duplicateIdCheck(String m_name) {
	
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean x= false;
		String sql =null;
		try {
			
			sql="SELECT m_name FROM MEMBER WHERE m_name=?";
						
			conn = getConnection();
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, m_name);
			rs = pstm.executeQuery();
			
			if(rs.next())  x= true; 
			
			return x;
			
		} catch (Exception sqle) {
			throw new RuntimeException(sqle.getMessage());
		} finally {
			try{
				if ( pstm != null ){ pstm.close(); pstm=null; }
				if ( conn != null ){ conn.close(); conn=null;	}
			}catch(Exception e){
				throw new RuntimeException(e.getMessage());
			}
		}
	} // end duplicateIdCheck()
	
	public void updateMember(MemberBean mb){			
		Connection con=null;
		String sql="";
		PreparedStatement pstmt=null;
		try{con = getConnection();
		
		if(mb.getM_pass().equals("")){
			sql="update member set m_name=? where m_id =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mb.getM_name()); 
			pstmt.setString(2, mb.getM_id());
			pstmt.executeUpdate();
		}else{
			//생성한 난수를 암호화, salt 재설정
			String salt = Sha256.getSalt();
			String passText=Sha256.encrypt(mb.getM_pass(), salt);
			
			sql="update member set m_name=?, m_pass=?, m_salt=? where m_id =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mb.getM_name()); 
			pstmt.setString(2, passText);
			pstmt.setString(3, salt);				
			pstmt.setString(4, mb.getM_id());
			pstmt.executeUpdate();
		}
		
		}catch(Exception e) {
			//예외 생기면 변수 e에 저장
			//예외를 잡아서 처리 -> 메시지 출력
			e.printStackTrace();
			}finally{
				//예외가 발생하든 말든 상관없이 마무리작업 => 기억장소 정리
				//객체 기억장소 마무리
			 if(pstmt!=null){try{pstmt.close();}catch(SQLException e){e.printStackTrace();} }
			 if(con!=null){try{con.close();}catch(SQLException e){e.printStackTrace();}	}
			}		
	}//end updateMember
	
	public void deleteMember(String id){
		Connection con=null;
		String sql="";
		PreparedStatement pstmt=null;
		try{con = getConnection();
		//3단계 연결정보를  이용해서 sql 구문을 만들고 실행할 객체 생성
//			내장객체 Statement, PreparedStatement, CallableStatement
		//String sql="insert into member values('"+id+"','"+pass+"','"+name+"','"+reg_date+"')";
		sql="delete from member where m_id=?";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, id); // ? 첫번째 물음표, 아이디값
		pstmt.executeUpdate();
		}catch(Exception e) {
			//예외 생기면 변수 e에 저장
			//예외를 잡아서 처리 -> 메시지 출력
			e.printStackTrace();
			}finally{
				//예외가 발생하든 말든 상관없이 마무리작업 => 기억장소 정리
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
	}//end deleteMember
	
	public int checkChance(String id){
		int check=0;
		Connection con=null;
		String sql =null;
		PreparedStatement pstmt =null;
		ResultSet rs =null;
		try {
			con=getConnection();
			
		sql="select m_roulette from member where m_id=?";
		 pstmt= con.prepareStatement(sql);
		 pstmt.setString(1, id);
		 rs= pstmt.executeQuery();
		if(rs.next()){
			if(rs.getInt("m_roulette")>0){
				downChance(id);
				check=1;				
			}
		
		}

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {};
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {};
			if(con!=null)try {con.close();}catch(SQLException ex) {};
			
		}return check;
	}//end checkChance
	
	public void downChance(String id){
		Connection con=null;
		String sql="";
		PreparedStatement pstmt=null;
		try{con = getConnection();
	
		sql="update member set m_roulette=m_roulette-1 where m_id =?";
		pstmt = con.prepareStatement(sql);						
		pstmt.setString(1, id);
		pstmt.executeUpdate();
		
		}catch(Exception e) {
			//예외 생기면 변수 e에 저장
			//예외를 잡아서 처리 -> 메시지 출력
			e.printStackTrace();
			}finally{
				//예외가 발생하든 말든 상관없이 마무리작업 => 기억장소 정리
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
	}//end down
	
	public void m_rouletteReset(){
		Connection con=null;
		String sql =null;
		PreparedStatement pstmt =null;
		ResultSet rs =null;
		try {
			con=getConnection();
									
		sql="update member set m_roulette=3";
		pstmt= con.prepareStatement(sql);		
		pstmt.executeUpdate();
		
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(rs!=null)try {rs.close();}catch(SQLException ex) {};
			if(pstmt!=null)try {pstmt.close();}catch(SQLException ex) {};
			if(con!=null)try {con.close();}catch(SQLException ex) {};
			
		}
	}//end reset
		
	public void updateProfile(String id, int profile_num){			
		Connection con=null;
		String sql="";
		PreparedStatement pstmt=null;
		try{con = getConnection();
	
		sql="update member set m_pic=? where m_id =?";
		pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, profile_num);
		pstmt.setString(2, id); 
		
		pstmt.executeUpdate();
		
		}catch(Exception e) {
			//예외 생기면 변수 e에 저장
			//예외를 잡아서 처리 -> 메시지 출력
			e.printStackTrace();
			}finally{
				//예외가 발생하든 말든 상관없이 마무리작업 => 기억장소 정리
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
	}//end updateProfile
	
}


	
	
	

	
	
	
	
	

