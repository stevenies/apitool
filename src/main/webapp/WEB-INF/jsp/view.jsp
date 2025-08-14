<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta charset="ISO-8859-1">
<title>REST API Generator</title>
<style>
	body {
		margin: 20px;
		font-family: Verdana, sans-serif;
		background-color: #c8c8c8;
	}
	#page {
		background-color: #ffffff;
		max-width: 800px;
		margin: auto;
		padding: 20px;
		border-radius: 8px;
		box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
		line-height: 1.4;
	}
	.title {
		font-family: Arial, sans-serif;
		font-size: 24px;
		font-weight: bold;
		text-align: center;
		margin-bottom: 20px;
	}
	.sectionHeader {
		font-family: Arial, sans-serif;
		font-size: 20px;
		font-weight: bold;
		margin: 20px 0px 12px 0px;
	}
	p {
		margin: 0px 0px 12px 0px;
	}
	a {
		color: #007BFF;
		text-decoration: none;
	}
	a:hover {
		text-decoration: underline;
	}
</style>
</head>
<body>
	<div id="page">
		<p class="title">REST API Generator</p>
		<p class="sectionHeader">What is a REST API?</p>
		<p>
			REST APIs are web-based <a href="apiOverview.html" target="_blank">Application Programming Interfaces</a>
			(API) providing clients with an easy means to access and update data sourced by a data provider.  A key
			characteristic of REST APIs is that they expose business data resources (aka business data objects) that
			can be created, read, updated, and/or deleted by the API. When developing a REST API an important first
			step is to create a <a href="domainModelOverview.html" target="_blank">business domain model</a> identifying
			the data resources, resource attributes, and relationships between resources. The business domain model
			is the main design artifact defining the API's interface between the data provider and its clients from
			a business perspective.
		</p>
		<p class="sectionHeader">What does the REST API Generator tool do?</p>
		<p>
			The REST API Generator provides two key capabilities to greatly reduce API development effort - 1) it
			transforms a business domain model expressed as a UML class diagram into an API interface specification
			documented using the OpenAPI standard, and 2) it transforms an API's OpenAPI specification into an API skeleton
			implementation.  The API skeleton implementation provides API developers with an API project's initial
			"quick start" code base, thus significantly reducing the time and effort required to develop a REST API.
			By using this tool development activities that used to take weeks to perform manually can now be accomplished
			automatically in a matter of minutes.
		</p>
		<p class="sectionHeader">What are the steps to create a new REST API?</p>
		<p>
			The process of creating a new REST API using the REST API Generator consists of the following six steps:
			<ol>
				<li>Obtain an access token enabling you to utilize the REST API Generator.</li>
				<li>Create a business domain model expressed as a UML class diagram.</li>
				<li>Generate the API's OpenAPI interface specification.</li>
				<li>Tailor the API's specification to remove unwanted endpoints.</li>
				<li>Generate the API's skeleton implementation.</li>
				<li>Implement the API's business logic.</li>
			</ol>
		</p>
		<p class="sectionHeader"><img src="" />Obtain an access token</p>
		<p>
		</p>
		<p class="sectionHeader"><img src="" />Create the business domain model</p>
		<p>
			Use the <a href="https://staruml.io/" target="_blank">StarUML</a> modeling tool to create a UML class diagram
			representing the API's business domain model. The business domain model should identify the data resources
			exposed by the API, resource attributes, and relationships between resources.  Once the business domain model
			has been created save it to your local computer using StarUML's native <code>.mdj</code> file format.
		</p>
		<p class="sectionHeader"><img src="" />Generate the API's OpenAPI interface specification</p>
		<p>
			Open the <a href="openapitool">REST API Generator tool</a>, upload the StarUML <code>.mdj</code> file containing the
			API's business domain model, and then press the Generate API button. In response the tool will generate the
			API's specification document and download it to your local computer.
		</p>
		<p class="sectionHeader"><img src="" />Tailor the API's specification to remove unwanted endpoints</p>
		<p>	
			After the API interface specification has been generated and downloaded it can then be tailored to remove unwanted
			endpoints. For example, if the business domain model does not require a DELETE endpoint for a particular resource
			then the DELETE endpoint can be removed from the API specification.
		</p>
		<p class="sectionHeader"><img src="" />Generate the API's skeleton implementation</p>
		<p>
			After the API's specification has been tailored it can then be used to generate code comprising the API's skeleton
			implementation. The skeleton implementation is a fully functional API that can receive requests from the client and
			return default responses.  The API is considered a skeleton implementation in that it contains stubs for the
			business logic. The intent is to serve as a starting point for API developers to implement the business logic.
		</p>
		<p>
			To generate the API's skeleton implementation code open the <a href="tool" target="_blank">API Generation Tool</a>,
			upload the OpenAPI specification file, and then press the Implement API button. In response the tool will generate
			the API's skeleton implementation files, package them into a ZIP file, and download the ZIP file to your local computer
		</p>
		<p class="sectionHeader"><img src="" />Implement the API's business logic</p>
		<p>
			Once the API's skeleton implementation is generated it is then ready to be used as a starting point for API developers
			to implement the business logic.  The skeleton implementation provides a quick-start code base that significantly
			reduces the time and effort required to develop a REST API.
		</p>
	</div>
</body>
</html>