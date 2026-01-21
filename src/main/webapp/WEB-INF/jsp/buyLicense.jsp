<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.smn.restapigenerator.ui.UIController" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>License Expired</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
	<script src="https://www.paypal.com/sdk/js?client-id=${paypalClientId}&currency=USD&components=buttons&disable-funding=paylater"></script>
	<script type="text/javascript">

		let itemName = "REST API Generator License - 1 Week";
		let itemId   = "RAG-LIC-1W";

		function installOnClickHandler(buttonId, handlerFunction) {
			const button = document.getElementById(buttonId);
			if (button) {
				button.onclick = handlerFunction;
			}
		}

		function updateButtonLabel() {
			const buttonBuyLicense = document.getElementById("paypal-button-container");
			const buttonContactSales = document.getElementById("buttonContactSales");
			const licenseOptions = document.getElementById("licenseOptions");
			const selectedOption = licenseOptions.querySelector('input[name="licenseType"]:checked').value;
			switch (selectedOption) {
				case "oneDay":
					buttonBuyLicense.style.display = 'inline-block';
					buttonContactSales.style.display = 'none';
					itemName = "REST API Generator License - 1 Day";
					itemId   = "RAG-LIC-1D";
				break;
				case "oneWeek":
					buttonBuyLicense.style.display = 'inline-block';
					buttonContactSales.style.display = 'none';
					itemName = "REST API Generator License - 1 Week";
					itemId   = "RAG-LIC-1W";
				break;
				case "oneMonth":
					buttonBuyLicense.style.display = 'inline-block';
					buttonContactSales.style.display = 'none';
					itemName = "REST API Generator License - 1 Month";
					itemId   = "RAG-LIC-1M";
					break;
				case "longTerm":
					buttonBuyLicense.style.display = 'none';
					buttonContactSales.style.display = 'inline-block';
					break;
			}
		}

		function sendEmailViaClient(to, subject, body) {
			const mailtoLink = "mailto:" + to + "?subject=" + 	encodeURIComponent(subject) + "&body=" + encodeURIComponent(body);
			window.location.href = mailtoLink;
		}

		function contactSales(event) {
			event.preventDefault();
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
			return false;
		}

		window.onload = function() {
			installOnClickHandler("licenseDay", updateButtonLabel);
			installOnClickHandler("licenseWeek", updateButtonLabel);
			installOnClickHandler("licenseMonth", updateButtonLabel);
			installOnClickHandler("licenseLongTerm", updateButtonLabel);
			installOnClickHandler("buttonContactSales", contactSales);

			paypal.Buttons({

				async createOrder() {
					const t0 = performance.now();
					try {
						const res = await fetch("<c:url value='/api/paypal/orders'/>", {
							method: "POST",
							headers: { "Content-Type": "application/json" },
							body: JSON.stringify({ itemName, itemId })
						});

						// If backend sometimes returns HTML (login page, error page), res.json() will throw.
						const text = await res.text();
						let data;
						try { data = JSON.parse(text); } catch { data = { raw: text }; }

						if (!res.ok) {
							console.error("Create order failed:", res.status, data);
							throw new Error(data.error || `Create order failed (${res.status})`);
						}

						if (!data.id || typeof data.id !== "string") {
							console.error("Unexpected create-order response:", data);
							throw new Error("Create order did not return a valid id");
						}

						return data.id;

					} finally {
						console.log("createOrder() ms:", Math.round(performance.now() - t0));
					}
				},

				async onApprove(data) {
					const res = await fetch(
						"<c:url value='/api/paypal/orders'/>/" + data.orderID + "/capture",
						{ method: "POST" }
					);

					const text = await res.text();
					let capture;
					try { capture = JSON.parse(text); } catch { capture = { raw: text }; }

					if (!res.ok) {
						console.error("Capture failed:", res.status, capture);
						throw new Error(capture.error || "Capture failed (" + res.status + ")");
					}

					if (capture.status === "COMPLETED") {
						const licenseExpirationDate = document.getElementById("licenseExpirationDate");
						licenseExpirationDate.textContent = capture.newExpirationDate;

						const renewalSuccessPanel = document.getElementById("renewalSuccessPanel");
						renewalSuccessPanel.style.display = 'block';
					} else {
						const paymentStatus = document.getElementById("paymentStatus");
						paymentStatus.textContent = capture.status;

						const renewalFailedPanel = document.getElementById("renewalFailedPanel");
						renewalFailedPanel.style.display = 'block';
					}
				},

				onError(err) {
					console.error("PayPal error:", err);
				}

			}).render("#paypal-button-container");
		};

	</script>
</head>
<body>
	<div id="page">
		<p class="formHeader">License Expired</p>
		Your license has expired. To continue using the REST API Generator the following licenses are available:
		<p id="licenseOptions">
			<c:if test="${includeDebugLicense}">
			<input type="radio" id="licenseDay" name="licenseType" value="oneDay" checked="checked"/> One Day Access ($${UIController.LICENSE_COST_ONE_DAY})<br/>
			</c:if>
			<input type="radio" id="licenseWeek" name="licenseType" value="oneWeek" checked="checked"/> One Week Access ($${UIController.LICENSE_COST_ONE_WEEK})<br/>
			<input type="radio" id="licenseMonth" name="licenseType" value="oneMonth"/> One Month Access ($${UIController.LICENSE_COST_ONE_MONTH})<br/>
			<input type="radio" id="licenseLongTerm" name="licenseType" value="longTerm"/> Long-term Access (contact our sales team for pricing and options)
		</p>
		<div id="paypal-button-container"></div>
		<button id="buttonContactSales" style="display:none;">Contact Sales Team</button>
		<div id="renewalSuccessPanel" style="display:none;">
			<p>
				Thank you for your purchase!
			</p>
			<p>
				Your license has been successfully renewed.<br/>
				You can continue using the REST API Generator until <span id="licenseExpirationDate"></span>.<br/>
				<button onclick="window.location.href='${referrer}'">Continue</button>
			</p>
		</div>
		<div id="renewalFailedPanel" style="display:none;">
			<p>
				Your payment was not successful (status: <span id="paymentStatus"></span>).
			</p>
		</div>
		<p class="formHeader" id="apiTitle">Why Pay for a License?</p>
		<img id="licenseBenefitsImage" src="images/benefits.png" />
		<p>
			In short - to save your project money!  By using the REST API Generator, work<br/>that used to take weeks of manual effort
			to write the API's specification is now completed automatically in minutes — dramatically cutting costs and significantly
			accelerating delivery.  The REST API Generator also generates solid foundational implementation code, giving developers
			a major head start and reducing effort, risk, and time-to-market even further. Along the way you get higher-quality APIs
			that includes built-in best practices such as domain-driven URI naming, paged list queries, standardized search
			and error schemas, stronger decoupling between API developers and clients, and earlier client integration activities.
			All of these benefits add up to substantial cost savings for your project.
		</p>
		<p>
			Have questions? Contact our <a href="mailto:sales@api-excellence.com">sales team</a> for more information.
		</p>
	</div>
</body>
</html>