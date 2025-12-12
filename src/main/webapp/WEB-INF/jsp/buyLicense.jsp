<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>License Expired</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
	<script type="text/javascript">
	</script>
</head>
<body>
	<div id="page">
		<p class="formHeader">License Expired</p>
		<form>
			Your license has expired. To continue using the REST API Generator the following licenses are available:
			<ul>
				<li>
					One Week Access ($${UIController.LICENSE_COST_ONE_WEEK}): Unlimited tool usage for a 7 day period
					<form action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post" style="display: inline;">
						<input type="hidden" name="cmd" value="_xclick">
						<input type="hidden" name="business" value="sales@api-excellence.com">
						<input type="hidden" name="item_name" value="REST API Generator - One Week License">
						<input type="hidden" name="amount" value="${UIController.LICENSE_COST_ONE_WEEK}">
						<input type="hidden" name="currency_code" value="USD">
						<input type="hidden" name="return" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/paypal-success">
						<input type="hidden" name="cancel_return" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/paypal-cancel">
						<input type="hidden" name="notify_url" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/paypal-ipn">
						<input type="hidden" name="custom" value="${sessionScope.user.email}">
						<input type="submit" name="submit" value="Buy" style="background-color: #0070ba; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; font-size: 14px;">
					</form>
				</li>
				<li>
					One Month Access ($${UIController.LICENSE_COST_ONE_MONTH}): Unlimited tool usage for a 30 day period
					<form action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post" style="display: inline;">
						<input type="hidden" name="cmd" value="_xclick">
						<input type="hidden" name="business" value="sales@api-excellence.com">
						<input type="hidden" name="item_name" value="REST API Generator - One Month License">
						<input type="hidden" name="amount" value="${UIController.LICENSE_COST_ONE_MONTH}">
						<input type="hidden" name="currency_code" value="USD">
						<input type="hidden" name="return" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/paypal-success">
						<input type="hidden" name="cancel_return" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/paypal-cancel">
						<input type="hidden" name="notify_url" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/paypal-ipn">
						<input type="hidden" name="custom" value="${sessionScope.user.email}">
						<input type="submit" name="submit" value="Buy" style="background-color: #0070ba; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; font-size: 14px;">
					</form>
				</li>
				<li>Long-term Access: Contact <a href="mailto:sales@api-excellence.com">sales@api-excellence.com</a> for pricing and options</li>
			</ul>

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