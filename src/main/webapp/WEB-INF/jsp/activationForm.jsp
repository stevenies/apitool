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
					<td colspan="3"><div class="formHeader">Account Activation</div></td>
				</tr>
<!--
				<tr>
					<td></td>
					<td colspan="2">
						<table id="subscriptionPlans">
							<tr>
								<td colspan="2" id="subscriptionPlansHeader">Subscription Plans:</td>
							</tr>
							<tr>
								<td class="subscriptionPlanLabel">Trial:</td>
								<td>Unlimited tool usage for a 24 hour period</td>
							</tr>
							<tr>
								<td class="subscriptionPlanLabel">One Week Access ($99):</td>
								<td>Unlimited tool usage for a 7 day period</td>
							</tr>
							<tr>
								<td class="subscriptionPlanLabel">One Month Access ($299):</td>
								<td>Unlimited tool usage for a 30 day period</td>
							</tr>
							<tr>
								<td colspan="2" id="subscriptionPlansFooter">
									<a href="whypay.html" target="WhyPay">Subscription Benefits</a></td>
								</td>
							</tr>
						</table>
					</td>
				</tr>
-->
				<tr>
					<td><label>Email<span class="required">*</span>:</label></td>
					<td>${email}</td>
				</tr>
				<tr>
					<td><label>Password<span class="required">*</span>:</label></td>
					<td><input type="text" id="accessToken" name="accessToken" value="${accessToken}" /></td>
					<td class="formNote">Password used to access your account.</td>
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
			<div class="actionPanel">
				<button id="buttonRegister">Activate</button>
			</div>
			<input type="hidden" name="email" value="${email}" />
		</form>
	</div>
</body>
</html>