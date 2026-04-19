<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Generate API Specification</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
	<script src="downloadFile.js"></script>
	<script type="text/javascript">

		function installOnClickHandler(buttonId, handlerFunction) {
			const button = document.getElementById(buttonId);
			if (button) {
				button.onclick = handlerFunction;
			}
		}

		window.onload = function() {
			installOnClickHandler("buttonGenerate", function(event) {
				event.preventDefault(); // Prevent default form submission
				const form = document.getElementById("apiSpecForm");
				form.submit();
				return false;
			});

			installOnClickHandler("buttonDownload", function(event) {
				event.preventDefault(); // Prevent default form submission
				const url = "apiSpecFile";
				const filename = "apiSpec.json";
				downloadFile(url, filename);
				return false;
			});

			installOnClickHandler("buttonTailor", function(event) {
				event.preventDefault(); // Prevent default form submission
				window.open("swaggerEditor.html", 'SwaggerEditor');
				return false;
			});
		};
	</script>
</head>
<body>
	<div id="page">
		<img id="specGeneration" src="images/specGeneration.png" alt="API Specification Generation" />
		<p class="title">API Specification Generator</p>
		<form id="apiSpecForm" action="doApiSpecForm" method="POST" enctype="multipart/form-data">
			<table>
				<tr>
					<td colspan="3"><div class="formHeader">API Information</div></td>
				</tr>
				<tr>
					<td><label>API Title<span class="required">*</span>:</label></td>
					<td><input id="title" name="title" type="text" value="${apiSpec.title}" /></td>
					<td class="formNote">Title developers use to refer to the API.</td>
				</tr>
				<tr>
					<td><label>API Description:</label></td>
					<td><textarea id="description" name="description" rows="4" cols="30">${apiSpec.description}</textarea></td>
					<td class="formNote">Brief description of the API's purpose and functionality.</td>
				</tr>
				<tr>
					<td><label>API Version<span class="required">*</span>:</label></td>
					<td><input id="version" name="version" type="text" value="${apiSpec.version}" /></td>
					<td class="formNote">API version (e.g., 1.0).</td>
				</tr>
				<tr>
					<td><label>Business Domain Model<span class="required">*</span>:</label></td>
					<td><input id="domainModel" name="domainModel" type="file" /></td>
					<td class="formNote">StarUML '.mdj' file.</td>
				</tr>
				<tr>
					<td colspan="3"><div class="formHeader">API Functionality</div></td>
				</tr>
				<tr>
					<td colspan="3">
						<p>Generate API endpoints for the following capabilities:</p>
						<div id="apiCapabilities">
							<input id="makeSEARCH" name="makeSEARCH" type="checkbox" value="true" <c:if test="${apiSpec.makeSEARCH}">checked</c:if> /> Search for data matching the specified criteria<br/>
							<input id="makePOST" name="makePOST" type="checkbox" value="true" <c:if test="${apiSpec.makePOST}">checked</c:if> /> Create new data<br/>
							<input id="makeGET" name="makeGET" type="checkbox" value="true" <c:if test="${apiSpec.makeGET}">checked</c:if> /> Query existing data<br/>
							<input id="makePUT" name="makePUT" type="checkbox" value="true" <c:if test="${apiSpec.makePUT}">checked</c:if> /> Update data values<br/>
							<input id="makeDELETE" name="makeDELETE" type="checkbox" value="true" <c:if test="${apiSpec.makeDELETE}">checked</c:if> /> Delete data
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="3"><div class="formHeader">API Deployment Settings</div></td>
				</tr>
				<tr>
					<td><label>API Server Domain<span class="required">*</span>:</label></td>
					<td><input id="serverDomain" name="serverDomain" type="text" value="${apiSpec.serverDomain}" /></td>
					<td class="formNote">Domain where the API will be hosted<br>(e.g., mycompany.com).</td>
				</tr>
				<tr>
					<td><label>API Context Root<span class="required">*</span>:</label></td>
					<td><input id="contextRoot" name="contextRoot" type="text" value="${apiSpec.contextRoot}" /></td>
					<td class="formNote">Context path prefix for the API's various endpoint URIs (e.g., api).</td>
				</tr>
				<tr>
					<td><label>API Port:</label></td>
					<td><input id="port" name="port" type="text" value="${apiSpec.port}" /></td>
					<td class="formNote">Port on which the API server will run<br>(e.g., 443).</td>
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
			<c:if test="${apiSpec.valid}">
				<div class="successPanel">
					Generated specification for API "${apiSpec.title}" on ${apiSpec.dateGenerated}
				</div>
			</c:if>
			<div class="actionPanel">
				<c:if test="${not apiSpec.valid}">
					<button id="buttonGenerate">Generate API Specification</button>
				</c:if>
				<c:if test="${apiSpec.valid}">
					<button id="buttonGenerate">Regenerate API Specification</button>
					<button id="buttonDownload">Download API Specification</button>
					<button id="buttonTailor">Tailor API Endpoints</button>
				</c:if>
			</div>
		</form>
	</div>
</body>
</html>