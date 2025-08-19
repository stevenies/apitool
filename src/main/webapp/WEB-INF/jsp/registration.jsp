<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<meta charset="ISO-8859-1">
<title>REST API Generator Tool</title>
<link rel="stylesheet" type="text/css" href="styles.css">
<script src="https://code.jquery.com/jquery-3.7.1.js"
	integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4="
	crossorigin="anonymous">
</script>
</head>
<script type="text/javascript">
	$(function() {
		$("#buttonLogin").click(function(event) {
			event.preventDefault(); // Prevent default form submission
			$("#formLogin").submit();
			return false;
		});
		$("#buttonRegister").click(function(event) {
			event.preventDefault(); // Prevent default form submission
			$("#formRegister").submit();
			return false;
		});
	});
</script>
<body>
	<div id="page">
		<p class="title">API Specification Generator</p>
		<form id="formLogin" action="/login" enctype="multipart/form-data" method="post">
			<table>
				<tr>
					<td colspan="3"><div class="formHeader">Registered Users</div></td>
				</tr>
				<tr>
				<tr>
					<td><label>Email<span class="required">*</span>:</label></td>
					<td><input id="email" name="email" type="text" value="${email}" /></td>
					<td class="formNote">Email address given when your account was registered.</td>
				</tr>
				<tr>
					<td><label>Access Token<span class="required">*</span>:</label></td>
					<td><input id="accessToken" name="accessToken" type="text" value="${accessToken}" /></td>
					<td class="formNote">Security credential authorizing use of the REST API Generator.</td>
				</tr>
			</table>
			<c:if test="${not empty errors}">
				<div class="errorPanel">
					<ul>
						<c:forEach var="error" items="${errors}">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</div>
			</c:if>
			<div class="actionPanel">
				<button id="buttonLogin">Login</button>
			</div>
		</form>
		<form id="formRegister" action="/register" enctype="multipart/form-data" method="post">
			<table>
				<tr>
					<td colspan="3"><div class="formHeader">New Users</div></td>
				</tr>
				<tr>
					<td><label>First Name<span class="required">*</span>:</label></td>
					<td><input id="nameFirst" name="nameFirst" type="text" value="${nameFirst}" /></td>
					<td class="formNote">Your first name.</td>
				</tr>
				<tr>
					<td><label>Last Name<span class="required">*</span>:</label></td>
					<td><input id="nameLast" name="nameLast" type="text" value="${nameLast}" /></td>
					<td class="formNote">Your last name.</td>
				</tr>
				<tr>
					<td><label>Company<span class="required">*</span>:</label></td>
					<td><input id="company" name="company" type="text" value="${company}" /></td>
					<td class="formNote">Name of your employer or "Self" if not employed.</td>
				</tr>
				<tr>
					<td><label>Email<span class="required">*</span>:</label></td>
					<td><input id="email" name="email" type="text" value="${email}" /></td>
					<td class="formNote">Your email address.</td>
				</tr>
				<tr>
					<td rowspan="3"><label>Access Plan<span class="required">*</span>:</label></td>
					<td><input type="radio" id="accessPlanFree" name="accessPlan" value="free">Trial (Free)</input></td>
					<td>Unlimited tool usage for a single 24 hour period.</td>
				</tr>
				<tr>
					<td><input type="radio" id="accessPlanFree" name="accessPlan" value="free">Week Access ($99)</input></td>
					<td>Unlimited tool usage during a period of one week.</td>
				</tr>
				<tr>
					<td><input type="radio" id="accessPlanFree" name="accessPlan" value="free">Month Access ($299)</input></td>
					<td>Unlimited tool usage during a period of one month.</td>
				</tr>
			</table>
			<c:if test="${not empty errors}">
				<div class="errorPanel">
					<ul>
						<c:forEach var="error" items="${errors}">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</div>
			</c:if>
			<div class="actionPanel">
				<button id="buttonRegister">Register</button>
			</div>
		</form>
	</div>
</body>
</html>