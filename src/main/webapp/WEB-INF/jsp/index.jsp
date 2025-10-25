<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<meta charset="ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>REST API Generator</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
	<script src="../../downloadFile.js"></script>
	<script type="text/javascript">
		function openWindow(url, target) {
			window.open(url, target);
			return false;
		}
		function toggleDescription() {
			fetch("toggleDescription")
				.then(response => {
    				window.location.href = window.location.href + "?t=" + new Date().getTime();
				});
			return false;
		}
	</script>
</head>
<body>
	<div id="page">
		<c:if test="${not hideDescription}">
			<div id="logo"><img src="images/logo.png" alt="API Tool Logo" /></div>
		</c:if>
		<p>
			<div class="title" style="text-align: center;">REST API Generator</div>
			<div class="subtitle" style="text-align: center;">for Rapid Creation of REST API Specifications and Code</div>
		</p>
		<p>
		<c:if test="${hideDescription}">
			<div id="descriptionSwitch"><a onclick="return toggleDescription();">Show Description</a></div>
		</c:if>
		<c:if test="${not hideDescription}">
			<div id="descriptionSwitch"><a onclick="return toggleDescription();">Hide Description</a></div>
		</c:if>
		</p>
		<p id="executiveSummary">
			The REST API Generator is a toolset that automates the creation of REST API interface specifications and skeleton implementation code
			from a business domain model. It enables a contract-first approach for API development significantly reducing the time and cost
			required to create REST APIs.
		</p>
		<c:if test="${not hideDescription}">
			<p class="sectionHeader">API Development Challenges</p>
			<p>
				A best practice for <a onclick="return openWindow('overviewApi.html', 'API');">REST API</a> development
				is to use the <a href="https://www.openapis.org/" target="OpenAPI">OpenAPI</a> documentation standard
				to define the API's interface between a business system and its clients before any API implementation
				code is written. OpenAPI facilitates a contract-first design approach that not only improves communication
				and collaboration between API stakeholders - business product owners, designers, developers, and
				consumers - but also helps to identify potential issues and gaps in the API's design early in the
				development process. This proactive approach leads to higher quality APIs that better meet the needs
				of both the business and its clients.
			</p>
			<p>
				However creating an OpenAPI specification can be a complex and time-consuming process. Writing an OpenAPI
				specification manually is a laborious process typically taking several weeks or longer to complete.
				Specifications for non-trivial APIs can easily require thousands of lines of detailed JSON code
				for specifying the various API endpoints, request formats, response formats, success/error status codes,
				and domain model schemas.  For example, this <a onclick="return openWindow('apiExample.html', 'APIExample');">API</a>
				has a nominal number of API endpoints but requires 14000 lines of JSON code to implement its OpenAPI
				specification!  For an API of this size experience has shown that a developer typically needs three weeks
				to manually write the JSON code, test it, and resolve issues. Assuming that the developer can finish the
				work in three weeks, and given an average developer rate of $60 per hour, the cost to <u>manually</u>
				write the API's client specification is <span style="color:red;">$7,200!</span>
			</p>
			<p class="sectionHeader">How does the REST API Generator Save Time and Money?</p>
			<p>
				The REST API Generator provides two important capabilities to greatly reduce API development effort.
				First<br/>it automatically generates the API's OpenAPI specification from a business domain model
				expressed as a<br/>UML class diagram. A
				<a onclick="return openWindow('overviewDomainModel.html', 'DomainModel');">business domain model</a>
				is a conceptual blueprint of an organization's problem space identifying the key business concepts
				(entities), their attributes, and their relationships to other entities.  It provides a shared
				vocabulary for business stakeholders and technical teams to bridge the gap between business
				requirements and technical implementation.  The business domain model is a key design artifact
				for defining the API's client interface specification from a business perspective.
			</p>
			<p>
				Second, the REST API Generator reads the API's OpenAPI client interface specification to automatically
				generate code implementing the API's skeleton infrastructure. The skeleton implementation is a fully
				functional API that can receive requests from the client and return default responses but contains
				stubs for the business logic. The API skeleton code provides API developers with an initial "quick
				start" code base, thus significantly reducing the time and effort required to develop a REST API. 
			</p>
		</c:if>
		<img id="apiBuildSteps" src="images/apiBuildSteps.png" />
		<c:if test="${not hideDescription}">
			<p>
				By using the REST API Generator development activities that used to take weeks to perform manually
				can now be accomplished automatically in a matter of minutes.  Instead of developers spending time to manually
				create API specifications and write API boilerplate code they can now focus on implementing the API's business
				logic to more quickly realize the API's business value proposition and shorten time to market.
			</p>
			<p class="sectionHeader">API Development Tooling</p>
		</c:if>
		<p>
			The tools comprising the REST API Generator are:
			<ol>
				<li><a href="https://staruml.io/" target="_blank">StarUML Modeling Tool</a> -
					a COTS tool used by a Business Analyst to create the API's business domain model as a UML class diagram.</li>
				<li><a onclick="return openWindow('viewApiSpecForm', 'APISpecGenerator');">API Spec Generator</a> -
					generates an OpenAPI specification from the domain model's class diagram.</li>
				<li><a onclick="return openWindow('viewApiCodeForm', 'APICodeGenerator');">API Code Generator</a> -
					generates code implementing the API's skeleton implementation based on the<br/>API's OpenAPI
					specification.</li>
			</ol>
		</p>
		<c:if test="${not hideDescription}">
			<p class="sectionHeader">Try the REST API Generator Risk Free</p>
			<p>
				Would you like to experience how the REST API Generator can help your organization automatically generate REST API
				specifications and operational API code?  You can try the REST API Generator risk free by registering for a
				free trial license.  The trial license provides full access to all REST API Generator features for a
				period of 24 hours.  There is no obligation to continue using the tool after the trial period expires.
			</p>
			<p>
				To try the REST API Generator you'll need a business domain model created with the StarUML modeling
				tool.  You can either create your own domain model or download a 
				<a onclick="return downloadFile('images/insuranceExample.mdj', 'sampleDomainModel.mdj');">sample domain model</a>.
				Once you've obtained a business domain model
				<a onclick="return openWindow('viewApiSpecForm', 'APISpecGenerator');">register for a free trial license</a>.
			</p>
		</c:if>
		<p>
			Questions, comments, or suggestions?  Contact our support team at <a href="mailto:steveniesfl@gmail.com">steveniesfl@gmail.com</a>.
		</p>
	</div>
</body>
</html>