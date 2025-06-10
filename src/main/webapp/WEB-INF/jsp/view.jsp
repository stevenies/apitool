<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta charset="ISO-8859-1">
<title>StarUML API Generation</title>
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
	<p>REST APIs</p>
	<p>A key characteristic of REST APIs is that they expose business
		data resources that can be created, read, updated, and/or deleted by
		the API. When developing a REST API an important design artifact is to
		create an information model identifying the data resources, resource
		attributes, and relationships between resources. An API information
		model is not the same as a database design model. An information model
		defines the interface between the API and its clients from a business
		perspective. In contrast a database design model describes how the
		data is persisted in a data store such as a database.</p>
	<p>Given an API information model expressed as a UML Class diagram,
		the following tool generates an REST API interface (expressed using
		the OpenAPI (Swagger) format) containing endpoints for accessing and
		manipulating the corresponding data resources.</p>
	<form id="formUploadInfoModel" action="/uploadInfoModel" enctype="multipart/form-data" method="post">
		<table>
			<tr>
				<td>Title:</td>
				<td><input id="title" name="title" type="text" /></td>
			</tr>
			<tr>
				<td>Description:</td>
				<td><input id="description" name="description" type="text" /></td>
			</tr>
			<tr>
				<td>Version:</td>
				<td><input id="version" name="version" type="text" /></td>
			</tr>
			<tr>
				<td>Information Model:</td>
				<td><input id="file" name="file" type="file" /></td>
			</tr>
			<tr>
				<td colspan="2">Settings</td>
			</tr>
			<tr>
				<td>Server Domain:</td>
				<td><input id="serverDomain" name="serverDomain" type="text" /></td>
			</tr>
			<tr>
				<td>API Context Root:</td>
				<td><input id="contextRoot" name="contextRoot" type="text" /></td>
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
	<p id="errors">${errors}</p>
</body>
</html>