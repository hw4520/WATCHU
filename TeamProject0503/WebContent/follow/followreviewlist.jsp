<%@page import="net.category.db.ReviewBean"%>
<%@page import="java.util.List"%>
<%@page import="net.admin.manage.db.MovieBean"%>
<%@page import="net.member.db.MemberBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="./images/watchu_logo22.ico" type="image/x-icon" >
<title>왓츄 : WATCHU</title>

<!-- CSS -->
<link href="./css/default.css" rel="stylesheet" type="text/css">
<link href="./css/followReviews.css" rel="stylesheet" type="text/css">

<!-- 웹 폰트 : 나눔고딕 -->
<link href="https://fonts.googleapis.com/css?family=Nanum+Gothic" rel="stylesheet">

<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
</head>

<body>
<%
List<ReviewBean> followreviewlist=(List)request.getAttribute("followreviewlist");
List<MovieBean> followmovielist=(List)request.getAttribute("followmovielist");

MemberBean getmember=(MemberBean)request.getAttribute("memberbean");

int followercount=((Integer)request.getAttribute("followercount"));
int followingcount= ((Integer)request.getAttribute("followingcount"));
int reviewcount= ((Integer)request.getAttribute("reviewcount"));
MovieBean favorite = (MovieBean)request.getAttribute("moviebean");
String grade="";
switch(getmember.getM_grade()){
case 1 : grade = "정회원"; break;
case 2 : grade = "VIP회원"; break;
}

String genre="";
if(favorite!=null){
switch(favorite.getMv_genre()){
case "animation" : genre="애니메이션"; break;
case "comedy" : genre="코미디"; break;
case "indie" : genre="독립영화"; break;
case "sf" : genre="SF"; break;
case "action" : genre="액션"; break;
case "thriller" : genre="스릴러"; break;
case "romance" : genre="로맨스"; break;
case "horror" : genre="공포"; break;
case "drama" : genre="드라마"; break;
}
}
%>
<!-- 헤더 영역 -->
<jsp:include page="../inc/header.jsp"/>
<!-- 헤더 영역 -->

<article class="all">
	<section class="sec myInfo">
		<div id="profile">
			<img src="./images/proflie_img/proflie<%=getmember.getM_pic()%>.png" width="100px" height="100px">
			<p><a href="./FollowMyHome.fo?m_id=<%=getmember.getM_id()%>"><span><%=getmember.getM_name()%></span></a></p> <!-- 이름, 등급 가져오기 -->
		</div><!-- profile -->
		<div id="info">
			<p><span id="span1">Following</span><span><td data-toggle="modal" data-target="#following"><%=followingcount%></td></span>
			<span id="span1">Follower</span><span><td data-toggle="modal" data-target="#follower"><%=followercount%></td></span></p>
			<p><span id="span1">리뷰 수</span><span><%=reviewcount%></span></p>
			<p><span id="span1">선호장르</span>
			<span>
				<%
				if(favorite==null ){
					%>
					<%="아직 선호장르가 없습니다"%>
					<%
				}else{
					%>
					<%=genre%>
					<%
				}
				%> 
			</span></p>
		</div><!-- info -->
	</section><!-- myInfo -->
	
	<section class="sec myReviewList">
		<%
		 if(followreviewlist.size()==0){
			 %>
			 <div id="rv"> 
				<div class="noReviewContents">
					<p><%="아직 리뷰가 없습니다"%></p>
					<p><%="영화를 본 후 내 감상을 리뷰로 남겨주세요!"%></p>
				</div>
			</div>
			 <%
		 }else{	
			
			for(int i=0;i<followreviewlist.size();i++){
			ReviewBean reviewbean=followreviewlist.get(i);
			MovieBean moviebean=followmovielist.get(i);
			%>
			<div id="rv"> 
				<p><span id="reviewTitle"><a href="./CategoryMovie.ca?mv_num=<%=moviebean.getMv_num()%>"><%=moviebean.getMv_kor_title()%></a></span>
				<span id="reviewDate"><%=reviewbean.getR_date()%></span>
				<span id="reviewRecommand"><img src="./images/thumbsUp.png" width="20px" height="20px" style="position:relative;top:5px"><%=reviewbean.getR_recommand()%></span>
				<p class="rvList">
				<%=reviewbean.getR_content().replaceAll("\r\n", "<br>")%></p>
			</div> 
			<%}
		}%>
	</section>
	<p class="up"><a href="#">▲<br>▲</a></p>

</article>

<!-- 푸터 영역 -->
<jsp:include page="../inc/footer.jsp"/>
<!-- 푸터 영역 -->

</body>
</html>