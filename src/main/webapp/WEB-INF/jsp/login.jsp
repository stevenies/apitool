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
			installOnClickHandler("buttonLogin", function(event) {
				event.preventDefault();
				submitForm("formLogin");
				return false;
			});
			installOnClickHandler("buttonRegister", function(event) {
				event.preventDefault();
				submitForm("formRegister");
				document.body.style.cursor = 'wait';
				return false;
			});
			document.body.style.cursor = 'default';
		};

	</script>
</head>
<body>
	<div id="page">
		<p class="title">Login</p>
		<form id="formLogin" action="login" enctype="multipart/form-data" method="post">
			<table>
				<tr>
					<td colspan="3"><div class="formHeader">Registered Users</div></td>
				</tr>
				<tr>
				<tr>
					<td><label>Email<span class="required">*</span>:</label></td>
					<td><input id="email" name="email" type="text" /></td>
					<td class="formNote">Email address given when your account was registered.</td>
				</tr>
				<tr>
					<td><label>Password<span class="required">*</span>:</label></td>
					<td><input id="password" name="password" type="text" /></td>
					<td class="formNote">Security credential authorizing use of the REST API Generator.</td>
				</tr>
			</table>
			<c:if test="${not empty loginErrors}">
				<div class="errorPanel">
					<ul>
						<c:forEach var="error" items="${loginErrors}">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</div>
			</c:if>
			<div class="actionPanel">
				<button id="buttonLogin">Login</button>
			</div>
			<input type="hidden" name="referrer" value="${referrer}" />
		</form>
		<form id="formRegister" action="register" enctype="multipart/form-data" method="post">
			<table>
				<tr>
					<td colspan="3"><div class="formHeader">New Users</div></td>
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
					<td><label>First Name<span class="required">*</span>:</label></td>
					<td><input id="nameFirst" name="nameFirst" value="${nameFirst}" type="text" /></td>
					<td class="formNote">Your first name.</td>
				</tr>
				<tr>
					<td><label>Last Name<span class="required">*</span>:</label></td>
					<td><input id="nameLast" name="nameLast" value="${nameLast}" type="text" /></td>
					<td class="formNote">Your last name.</td>
				</tr>
				<tr>
					<td><label>Company<span class="required">*</span>:</label></td>
					<td><input id="company" name="company" value="${company}" type="text" /></td>
					<td class="formNote">Name of your employer or "Self" if not employed.</td>
				</tr>
				<tr>
					<td><label>Email<span class="required">*</span>:</label></td>
					<td><input id="email" name="email" value="${email}" type="text" /></td>
					<td class="formNote">Your email address.</td>
				</tr>
				<tr>
					<td><label>Phone<span class="required">*</span>:</label></td>
					<td><input id="phone" name="phone" value="${phone}" type="text" /></td>
					<td class="formNote">Your phone number.</td>
				</tr>
			</table>
			<c:if test="${not empty registrationErrors}">
				<div class="errorPanel">
					<ul>
						<c:forEach var="error" items="${registrationErrors}">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</div>
			</c:if>
			<c:if test="${registrationSuccess}">
				<div class="successPanel">
					<p>
						Your account has been successfully registered.<br/>
						<strong>Please check your email</strong> for an email verification message and login instructions.<br/>
						Be sure to check your spam/junk folder if you don't see the email within a few minutes.
					</p>
					<p>
						<button id="buttonClose" onclick="window.close(); return false;">OK</button>
					</p>
				</div>
			</c:if>
			<c:if test="${not registrationSuccess}">
				<div class="actionPanel">
					<button id="buttonRegister">Register</button>
				</div>
			</c:if>
			<input type="hidden" name="referrer" value="${referrer}" />
		</form>
	</div>
</body>
</html>