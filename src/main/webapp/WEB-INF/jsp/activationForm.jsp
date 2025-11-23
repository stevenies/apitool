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
			installOnClickHandler("buttonRegister", function(event) {
				event.preventDefault();
				submitForm("formActivation");
				document.body.style.cursor = 'wait';
				return false;
			});
			document.body.style.cursor = 'default';
		};

	</script>
</head>
<body>
	<div id="page">
		<p class="title">API Specification Generator</p>
		<form id="formActivation" action="activate" enctype="multipart/form-data" method="post">
			<table>
				<tr>
					<td colspan="3">
						You have been automatically enrolled in the Trial license plan.
						This plan allows unlimited tool usage for a 24 hour period.
						Once the trial period expires you can continue using the REST API Generator
						by purchasing a license:
						<ul>
							<li>One Week Access ($99): Unlimited tool usage for a 7 day period</li>
							<li>One Month Access ($249): Unlimited tool usage for a 30 day period</li>
						</ul>
						Click <a href="whypay.html" target="WhyPay">here</a> to review the benefits of purchasing a license.
					</td>
				</tr>
				<tr>
					<td colspan="3"><div class="formHeader">Account Activation</div></td>
				</tr>
				<tr>
					<td><label>Email<span class="required">*</span>:</label></td>
					<td>${email}</td>
				</tr>
				<tr>
					<td><label>Password<span class="required">*</span>:</label></td>
					<td><input type="text" id="password" name="password" value="${password}" /></td>
					<td class="formNote">Password to be used for account access.</td>
				</tr>
			</table>
			<c:if test="${not empty enrollmentErrors}">
				<div class="errorPanel">
					<ul>
						<c:forEach var="error" items="${enrollmentErrors}">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</div>
			</c:if>
			<c:if test="${emailVerified}">
				<div class="actionPanel">
					<button id="buttonRegister">Activate</button>
				</div>
			</c:if>
			<input type="hidden" name="email" value="${email}" />
		</form>
	</div>
</body>
</html>