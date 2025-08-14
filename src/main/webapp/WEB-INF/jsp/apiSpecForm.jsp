<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta charset="ISO-8859-1">
<title>REST API Generation Tool</title>
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
	<form id="formUploadInfoModel" action="/uploadDomainModel" enctype="multipart/form-data" method="post">
		<table>
			<tr>
				<td colspan="2">API Information</td>
			</tr>
			<tr>
				<td>API Title:</td>
				<td><input id="title" name="title" type="text" /></td>
			</tr>
			<tr>
				<td>API Description:</td>
				<td><input id="description" name="description" type="text" /></td>
			</tr>
			<tr>
				<td>API Version:</td>
				<td><input id="version" name="version" type="text" /></td>
			</tr>
			<tr>
				<td colspan="2">Upload API Domain Model</td>
			</tr>
			<tr>
				<td>Domain Model:</td>
				<td><input id="file" name="file" type="file" /></td>
			</tr>
			<tr>
				<td colspan="2">API Deployment Settings</td>
			</tr>
			<tr>
				<td>API Server Domain:</td>
				<td><input id="serverDomain" name="serverDomain" type="text" /></td>
			</tr>
			<tr>
				<td>API Context Root:</td>
				<td><input id="contextRoot" name="contextRoot" type="text" /></td>
			</tr>
			<tr>
				<td>API Port:</td>
				<td><input id="port" name="port" type="text" /></td>
			</tr>
			<tr>
				<td colspan="2">API Functionality</td>
			</tr>
			<tr>
				<td>Generate SEARCH (query by example) endpoints:</td>
				<td><input id="makeSEARCH" name="makeSEARCH" type="checkbox" value="true" /></td>
			</tr>
			<tr>
				<td>Generate GET (resource query) endpoints:</td>
				<td><input id="makeGET" name="makeGET" type="checkbox" value="true" /></td>
			</tr>
			<tr>
				<td>Generate POST (resource creation) endpoints:</td>
				<td><input id="makePOST" name="makePOST" type="checkbox" /></td>
			</tr>
			<tr>
				<td>Generate PUT (full resource update) endpoints:</td>
				<td><input id="makePUT" name="makePUT" type="checkbox" value="true" /></td>
			</tr>
			<tr>
				<td>Generate PATCH (partial resource update) endpoints:</td>
				<td><input id="makePATCH" name="makePATCH" type="checkbox" value="true" /></td>
			</tr>
			<tr>
				<td>Generate DELETE (resource deletion) endpoints:</td>
				<td><input id="makeDELETE" name="makeDELETE" type="checkbox" value="true" /></td>
			</tr>
			<tr>
				<td colspan="2"><button id="buttonSubmit">Generate API Interface</button></td>
			</tr>
		</table>
	</form>
	<div id="errors">${errors}</div>
</body>
</html>