<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Generate API Skeleton Code</title>
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

				const formData = new FormData();

				const jsonObject = {};
				jsonObject['action'] = 'generate';

				const apiCodeForm = document.getElementById('apiCodeForm');
				const formFields = new FormData(apiCodeForm);
				for (const [key, value] of formFields.entries()) {
					jsonObject[key] = value;
				}
				formData.append('formFields', JSON.stringify(jsonObject));

				fetch("doApiCodeForm", {
					method: "POST",
					body: formData
				})
				.then(response => {
					if (!response.ok) throw new Error("API code generation failed");
				})
				.then((result) => {
					window.location.href = "viewApiCodeForm?nocache=" + new Date().getTime();
				})
				.catch(error => {
					alert("Error: " + error);
				});

				return false;
			});

			installOnClickHandler("buttonDownload", function(event) {
				event.preventDefault(); // Prevent default form submission
				const url = "apiCodeZipFile";
				const filename = "apiCode.zip";
				downloadFile(url, filename);
				return false;
			});
		};
	</script>
</head>
<body>
	<div id="page">
		<p class="title">API Specification Generator</p>
		<form id="apiCodeForm">
			<table>
				<tr>
					<td colspan="3"><div class="formHeader">API Implementation Settings</div></td>
				</tr>
				<tr>
					<td><label>Language<span class="required">*</span>:</label></td>
					<td><input id="language" name="language" type="text" value="${apiCode.language}" /></td>
					<td class="formNote">Programming language for the API's implementation code (e.g., Java, Python).</td>
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
			<c:if test="${apiCode.valid}">
				<div class="successPanel">
					Generated code for API "${apiSpec.title}" on ${apiCode.dateGenerated}
				</div>
			</c:if>
			<div class="actionPanel">
				<c:if test="${not apiCode.valid}">
					<button id="buttonGenerate">Generate API Implementation Code</button>
				</c:if>
				<c:if test="${apiCode.valid}">
					<button id="buttonGenerate">Regenerate API Code</button>
					<button id="buttonDownload">Download API Code</button>
				</c:if>
			</div>
		</form>
	</div>
</body>
</html>