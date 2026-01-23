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
				const form = document.getElementById("apiCodeForm");
				form.submit();
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
		<p class="title">API Code Generator</p>
		<form id="apiCodeForm" action="/doApiCodeForm" method="POST" enctype="multipart/form-data">
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
					<button id="buttonGenerate">Generate API Skeleton Code</button>
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