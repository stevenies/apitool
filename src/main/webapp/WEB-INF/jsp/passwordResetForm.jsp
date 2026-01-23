<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>REST API Generator Tool</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
	<script type="text/javascript">

		function installOnClickHandler(buttonId, handlerFunction) {
			const button = document.getElementById(buttonId);
			if (button) {
				button.onclick = handlerFunction;
			}
		}

		function submitForm(formId) {
			const form = document.getElementById(formId);
			if (form) {
				form.submit();
			}
		}
		
		window.onload = function() {
			installOnClickHandler("buttonResetPassword", function(event) {
				event.preventDefault();
				submitForm("formResetPassword");
				return false;
			});
		};

	</script>
</head>
<body>
	<div id="page">
		<p class="title">Reset Password</p>

		<form id="formResetPassword" action="resetPassword" enctype="multipart/form-data" method="post">
			<input type="hidden" name="key" value="${key}" />
			<table>
				<tr>
					<td><label>Email<span class="required">*</span>:</label></td>
					<td id="passwordResetEmail">${email}<input type="hidden" name="email" value="${email}" /></td>
					<td class="formNote">Email address given when your account was registered.</td>
				</tr>
				<tr>
					<td><label>New Password<span class="required">*</span>:</label></td>
					<td><input id="password" name="password" type="text" /></td>
					<td class="formNote">Security credential authorizing use of the REST API Generator.</td>
				</tr>
				<tr>
					<td><label>Confirm New Password<span class="required">*</span>:</label></td>
					<td><input id="confirmPassword" name="confirmPassword" type="text" /></td>
					<td class="formNote">Re-enter the new password for confirmation.</td>
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
			<c:if test="${success}">
				<div class="successPanel">
					<p>
						Your password was successfully reset.
					</p>
					<p>
						<button id="buttonClose" onclick="window.location.href='/'; return false;">OK</button>
					</p>
				</div>
			</c:if>
			<c:if test="${not success}">
				<div class="actionPanel">
					<button id="buttonResetPassword">Reset Password</button>
				</div>
			</c:if>
		</form>

	</div>
</body>
</html>