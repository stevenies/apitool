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
		$("#buttonSubmit").click(function(event) {
			event.preventDefault(); // Prevent default form submission
			$("#errors").html("");
			$("#formUploadInfoModel").submit();
			return false;
		});
	});
</script>
<body>
	<div id="page">
		<p class="title">API Specification Generator</p>
		<form id="formUploadInfoModel" action="/uploadDomainModel" enctype="multipart/form-data" method="post">
			<table>
				<tr>
					<td colspan="3"><div class="formHeader">User Information</div></td>
				</tr>
				<tr>
					<td><label>Email<span class="required">*</span>:</label></td>
					<td><input id="email" name="email" type="text" value="${email}" /></td>
					<td class="formNote">Email address given when your account was registered.</td>
				</tr>
				<tr>
					<td><label>Access Token<span class="required">*</span>:</label></td>
					<td><input id="accessToken" name="accessToken" type="text" value="${accessToken}" /></td>
					<td class="formNote">User access credential authorizing use of the REST API Generator tool.</td>
				</tr>
				<tr>
					<td colspan="3"><div class="formHeader">API Information</div></td>
				</tr>
				<tr>
					<td><label>API Title<span class="required">*</span>:</label></td>
					<td><input id="title" name="title" type="text" value="${title}" /></td>
					<td class="formNote">Title developers use to refer to the API.</td>
				</tr>
				<tr>
					<td><label>API Description:</label></td>
					<td><textarea id="description" name="description" rows="4" cols="30">${description}</textarea></td>
					<td class="formNote">Brief description of the API's purpose and functionality.</td>
				</tr>
				<tr>
					<td><label>API Version:</label></td>
					<td><input id="version" name="version" type="text" value="${version}" /></td>
					<td class="formNote">API version (e.g., 1.0).</td>
				</tr>
				<tr>
					<td><label>Business Domain Model<span class="required">*</span>:</label></td>
					<td><input id="file" name="file" type="file" /></td>
					<td class="formNote">StarUML file containing the API's business domain model.</td>
				</tr>
				<tr>
					<td colspan="3"><div class="formHeader">API Functionality</div></td>
				</tr>
				<tr>
					<td><label>Generate SEARCH endpoints:</label></td>
					<td><input id="makeSEARCH" name="makeSEARCH" type="checkbox" value="true" /></td>
					<td class="formNote">Generate endpoints for searching for resources based on example values.</td>
				</tr>
				<tr>
					<td><label>Generate GET endpoints:</label></td>
					<td><input id="makeGET" name="makeGET" type="checkbox" value="true" /></td>
					<td class="formNote">Generate endpoints for retrieval of resources.</td>
				</tr>
				<tr>
					<td><label>Generate POST endpoints:</label></td>
					<td><input id="makePOST" name="makePOST" type="checkbox" /></td>
					<td class="formNote">Generate endpoints for creation of new resource instances.</td>
				</tr>
				<tr>
					<td><label>Generate PUT endpoints:</label></td>
					<td><input id="makePUT" name="makePUT" type="checkbox" value="true" /></td>
					<td class="formNote">Generate endpoints for full updates of existing resources.</td>
				</tr>
				<tr>
					<td><label>Generate PATCH endpoints:</label></td>
					<td><input id="makePATCH" name="makePATCH" type="checkbox" value="true" /></td>
					<td class="formNote">Generate endpoints for partial updates of existing resources.</td>
				</tr>
				<tr>
					<td><label>Generate DELETE endpoints:</label></td>
					<td><input id="makeDELETE" name="makeDELETE" type="checkbox" value="true" /></td>
					<td class="formNote">Generate endpoints for resource deletion.</td>
				</tr>
				<tr>
					<td colspan="3"><div class="formHeader">API Deployment Settings</div></td>
				</tr>
				<tr>
					<td><label>API Server Domain<span class="required">*</span>:</label></td>
					<td><input id="serverDomain" name="serverDomain" type="text" value="${serverDomain}" /></td>
					<td class="formNote">Domain where the API will be hosted<br>(e.g., api.company.com).</td>
				</tr>
				<tr>
					<td><label>API Context Root<span class="required">*</span>:</label></td>
					<td><input id="contextRoot" name="contextRoot" type="text" value="${contextRoot}" /></td>
					<td class="formNote">Context path prefix for the API's various endpoint URIs (e.g., /businessApi/...).</td>
				</tr>
				<tr>
					<td><label>API Port:</label></td>
					<td><input id="port" name="port" type="text" value="${port}" /></td>
					<td class="formNote">Port on which the API server will run<br>(e.g., 443).</td>
				</tr>
			</table>
			<div class="errorPanel">
				<c:if test="${not empty errors}">
					<ul>
						<c:forEach var="error" items="${errors}">
							<li>${error}</li>
						</c:forEach>
					</ul>
				</c:if>
			</div>
			<div class="actionPanel">
				<button id="buttonSubmit">Generate API Specification</button>
			</div>
		</form>
	</div>
</body>
</html>