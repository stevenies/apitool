<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
		<p class="title">License Expired</p>
		<form>
			<table>
				<tr>
					<td></td>
					<td colspan="2">
						<table id="subscriptionPlans">
							<tr>
								<td colspan="2" id="subscriptionPlansHeader">License Plans<span class="required">*</span>:</td>
							</tr>
							<tr>
								<td class="subscriptionPlanLabel">One Week Access ($99):</td>
								<td><input type="radio" name="subscriptionPlan" value="oneWeek" disabled /> Unlimited tool usage for a 7 day period</td>
							</tr>
							<tr>
								<td class="subscriptionPlanLabel">One Month Access ($249):</td>
								<td><input type="radio" name="subscriptionPlan" value="oneMonth" disabled /> Unlimited tool usage for a 30 day period</td>
							</tr>
							<tr>
								<td colspan="2" id="subscriptionPlansFooter">
									<a href="whypay.html" target="WhyPay">License Benefits</a></td>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>