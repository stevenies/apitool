<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Upload API Specification</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
	<div id="page">
		<form id="formUploadApiSpec" action="doUploadSpecForm" method="POST" enctype="multipart/form-data">
			<c:if test="${not uploaded}">
			<table>
				<tr>
					<td><label>OpenAPI File<span class="required">*</span>:</label></td>
					<td><input id="apiSpec" name="apiSpec" type="file" /></td>
				</tr>
			</table>
			</c:if>
			<c:if test="${not empty errors}">
				<div class="errorPanel">
					<c:forEach var="error" items="${errors}">
						${error}
					</c:forEach>
				</div>
			</c:if>
			<c:if test="${uploaded}">
				<div class="successPanel">
					Uploaded specification for API "${apiSpec.title}" on ${apiSpec.dateGenerated}
				</div>
			</c:if>
			<div class="actionPanel">
				<c:if test="${not uploaded}">
					<button id="buttonUpload">Upload</button>
				</c:if>
				<c:if test="${uploaded}">
					<button id="buttonClose">Close</button>
				</c:if>
			</div>
		</form>
	</div>
</body>
<script type="text/javascript">

	// NOTE: These functions must be defined here instead of using window.onload or jQuery's $(document).ready()
	// because the dialog content is loaded dynamically and may not be present when those events fire. By defining
	// these functions directly in the script, we ensure they are available when the dialog is displayed and the
	// buttons are rendered.

	function installOnClickHandler(buttonId, handlerFunction) {
		const button = document.getElementById(buttonId);
		if (button) {
			button.onclick = handlerFunction;
		}
	}

	installOnClickHandler("buttonUpload", function(event) {
		event.preventDefault(); // Prevent default form submission
		const form = document.getElementById("formUploadApiSpec");
		form.submit();
		return false;
	});

	installOnClickHandler("buttonClose", function(event) {
		event.preventDefault(); // Prevent default form submission
		$("#uploadSpecDialog").dialog("close");
		return false;
	});

</script>
</html>