<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.smn.restapigenerator.ui.UIController" %>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>License Expired</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
	<script type="text/javascript">

		function installOnClickHandler(buttonId, handlerFunction) {
			const button = document.getElementById(buttonId);
			if (button) {
				button.onclick = handlerFunction;
			}
		}

		function updateButtonLabel() {
			const button = document.getElementById("buttonBuyLicense");
			const licenseOptions = document.getElementById("licenseOptions");
			const selectedOption = licenseOptions.querySelector('input[name="licenseType"]:checked').value;
			switch (selectedOption) {
				case "oneWeek":
					button.textContent = "Buy License";
					break;
				case "oneMonth":
					button.textContent = "Buy License";
					break;
				case "longTerm":
					button.textContent = "Contact Sales Team";
					break;
			}
		}

		function sendEmailViaClient(to, subject, body) {
			const mailtoLink = "mailto:" + to + "?subject=" + 	encodeURIComponent(subject) + "&body=" + encodeURIComponent(body);
			window.location.href = mailtoLink;
		}

		window.onload = function() {
			installOnClickHandler("buttonBuyLicense", function(event) {
				event.preventDefault();
				const licenseOptions = document.getElementById("licenseOptions");
				const selectedOption = licenseOptions.querySelector('input[name="licenseType"]:checked').value;
				switch (selectedOption) {
					case "oneWeek":
						alert("You selected One Week Access. Proceeding to payment gateway...");
						break;
					case "oneMonth":
						alert("You selected One Month Access. Proceeding to payment gateway...");
						break;
					case "longTerm":
						sendEmailViaClient(
							"sales@api-excellence.com",
							"Long-term License Inquiry",
							"Hi,\n\n"
							+ "I'm interested in purchasing a long-term license for the REST API Generator.\n"
							+ "Please provide more information about the licensing process.\n\n"
							+ "Sincerely,\n"
							+ "${user.nameFirst} ${user.nameLast}\n"
							+ "${user.email}"
						);
						break;
				}
				return false;
			});
			installOnClickHandler("licenseWeek", updateButtonLabel);
			installOnClickHandler("licenseMonth", updateButtonLabel);
			installOnClickHandler("licenseLongTerm", updateButtonLabel);
		};

	</script>
</head>
<body>
	<div id="page">
		<p class="formHeader">License Expired</p>
		<form>
			Your license has expired. To continue using the REST API Generator the following licenses are available:
			<p id="licenseOptions">
				<input type="radio" id="licenseWeek" name="licenseType" value="oneWeek" checked="checked"/> One Week Access ($${UIController.LICENSE_COST_ONE_WEEK})<br/>
				<input type="radio" id="licenseMonth" name="licenseType" value="oneMonth"/> One Month Access ($${UIController.LICENSE_COST_ONE_MONTH})<br/>
				<input type="radio" id="licenseLongTerm" name="licenseType" value="longTerm"/> Long-term Access (contact our sales team for pricing and options)<br/>
				<button id="buttonBuyLicense">Buy License</button>
			</p>
			<p class="formHeader" id="apiTitle">Why Pay for a License?</p>
			<img id="licenseBenefitsImage" src="images/benefits.png" />
			In short - to save your project money!  By using the REST API Generator, work<br/>that used to take weeks of manual effort
			to write the API's specification is now completed automatically in minutes — dramatically cutting costs and significantly
			accelerating delivery.  The REST API Generator also generates solid foundational implementation code, giving developers
			a major head start and reducing effort, risk, and time-to-market even further. Along the way you get higher-quality APIs
			that includes built-in best practices such as domain-driven URI naming, paged list queries, standardized search
			and error schemas, stronger decoupling between API developers and clients, and earlier client integration activities.
			All of these benefits add up to substantial cost savings for your project.
		</form>
	</div>
</body>
</html>