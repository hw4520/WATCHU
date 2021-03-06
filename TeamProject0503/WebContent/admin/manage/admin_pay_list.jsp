<%@page import="java.util.List"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>왓츄 : WATCHU</title>
<script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
<link href="./css/default.css" rel="stylesheet" type="text/css">
<link href="./css/admin.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	var request = new XMLHttpRequest();
	function searchFunction() {
		request.open("Post", "./AdminPaySearch?searchValue="+encodeURIComponent(document.getElementById("searchValue").value)+"&select="+encodeURIComponent(document.getElementById("select").value), true);
		request.onreadystatechange = searchProcess;
		request.send(null);
	}
	function searchProcess() {
		var table = document.getElementById("ajaxTable");
		table.innerHTML = "";
		if (request.readyState == 4 && request.status == 200) {
			var object = eval('(' + request.responseText + ')');
			var result = object.result;
			for (var i = 0; i < result.length; i++) {
				var row = table.insertRow(0);
				for (var j = 0; j < result[i].length; j++) {
					var cell = row.insertCell(j);
					cell.innerHTML = result[i][j].value;
				}
			}
		}
	}
	window.onload = function() {
		searchFunction();
	}
</script>
</head>
<body>
	<jsp:include page="../../inc/header.jsp" />
	<jsp:include page="../../inc/admin_sub.jsp" />
	<section class="content_member">
	<div id="content_member">
		<h1 class="adminTitle">PAY LIST</h1>
		<div class="admin-search-container">
		<select id="select">
			<option value="p_id" selected="selected">ID</option>
			<option value="p_auto">결제방식</option>
			<option value="p_start_day">결제일</option>		
			<option value="p_end_day">만료일</option>	
		</select>
		<input type="text" onkeyup="searchFunction()" id="searchValue" placeholder="검색할 내용 입력해라.">
		<button type="button" onclick="searchFunction();"><img src="./images/search.png" width="20px" height="20px"></button>
		</div>
		<table class="db_list db_list2">
			<thead>
				<tr>
					<th class="th3">NO</th>
					<th class="th3">ID</th>
					<th class="th4">결제방식</th>
					<th class="th5">결제일</th>
					<th class="th5">쿠폰만료일</th>
				</tr>
			</thead>
			<tbody id="ajaxTable">
			</tbody>
		</table>
	</div>
	</section>
	<jsp:include page="../../inc/footer.jsp" />
</body>
</html>