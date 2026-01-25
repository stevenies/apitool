<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<meta charset="ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>REST API Generator</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
	<script src="downloadFile.js"></script>
	<script src="popupPanel.js"></script>
	<script type="text/javascript">
		function openWindow(url, target) {
			window.open(url, target);
			return false;
		}
	</script>
</head>
<body>
	<div id="page">
		<div id="logo"><img src="images/logo.png" alt="API Tool Logo" /></div>
		<p>
			<div class="title" style="text-align: center;">REST API Generator</div>
			<div class="subtitle" style="text-align: center;">for Rapid Creation of REST API Specifications and Code</div>
		</p>
		<p>
		</p>
		<p id="executiveSummary">
			The REST API Generator is a toolset that automates the creation of REST API interface specifications and skeleton implementation code
			from a business domain model. It enables a contract-first approach for API development significantly reducing the time and cost
			required to create REST APIs.
		</p>
		<p class="sectionHeader">API Development Challenges</p>
		<p>
			Given today's AI-based “vibe coding” tools APIs have never been easier to implement.  However creating<br/>
			well-structured and robust API client <i>interfaces</i> still requires thoughtful engineering.  When APIs are designed well
			they serve as your architecture's key system interfaces hiding implementation details behind well-defined
			contracts.  The results are modular system architectures that can easily evolve as business requirements
			change.  In contrast coding APIs without first defining client interface contracts can result in brittle
			ad-hoc system architectures and API interfaces that are hard to evolve without adversely impacting API clients.
		</p>
		<p>
			A best practice for <a onclick="return openWindow('overviewApi.html', 'API');">API</a> development
			is to first define the API's interface between a business system and its clients before any API implementation
			code is written. However creating an client interface specification<br/>using the
			<a href="https://www.openapis.org/" target="OpenAPI">OpenAPI</a>
			documentation standard can be a complex and time-consuming process.
			Writing an<br/>API specification manually is a laborious activity typically taking several weeks or longer to complete.
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
			First it automatically generates the API's OpenAPI specification from a business domain model
			expressed as a UML class diagram. Second it reads the API's OpenAPI client interface specification to automatically
			generate code implementing the API's skeleton infrastructure.
		</p>
		<p>
			The following figure illustrates the main steps for quickly designing and implementing robust APIs using the REST API Generator.
			Click each step for a description of that step's development activities.
			<div style="display:inline-block">
				<img id="apiBuildSteps" src="images/apiBuildSteps.png" usemap="#apiBuildStepsHotspots" />
				<map name="apiBuildStepsHotspots" id="apiBuildStepsHotspots">
					<area shape="rect" coords="38,19,382,460" href="#" onclick="return showPopup(event);" data-key="regionA" alt="Region A" />
					<area shape="rect" coords="400,70,614,460" href="#" onclick="return showPopup(event);" data-key="regionB" alt="Region B" />
					<area shape="rect" coords="626,70,771,460" href="#" onclick="return showPopup(event);" data-key="regionC" alt="Region C" />
				</map>
			</div>
			<div id="popup" class="popup" role="dialog" aria-hidden="true"></div>
		</p>
		<p>
			By using the REST API Generator development activities that used to take weeks to perform manually
			can now be accomplished automatically in a matter of minutes.  Instead of developers spending time to manually
			create API specifications and write API foundational code they can now focus on implementing the API's business
			logic to more quickly realize the API's business value proposition and shorten time to market.
		</p>
		<p class="sectionHeader">Try the REST API Generator Risk Free</p>
		<p>
			Would you like to experience how the REST API Generator can help your organization automatically generate REST API
			specifications and operational API code?  You can try the REST API Generator risk free by registering for a
			free trial license.  The trial license provides full access to all REST API Generator features for a
			period of 24 hours.  There is no obligation to continue using the tool after the trial period expires.
		</p>
		<p>
			To try the REST API Generator you'll need a business domain model created with the StarUML modeling tool.
			You can either create your own domain model or download a 
			<a onclick="return downloadFile('images/insuranceExample.mdj', 'sampleDomainModel.mdj');">sample domain model</a>.
			Once you've obtained a business domain model
			<a onclick="return openWindow('viewApiSpecForm', 'APISpecGenerator');">register for a free trial license</a>.
		</p>
		<p>
			Questions, comments, or suggestions?  Contact our sales team at <a href="mailto:sales@api-excellence.com">sales@api-excellence.com</a>.
		</p>
		<p class="sectionHeader">Tools Comprising the REST API Generator Toolset</p>
		<table id="toolsTable">
			<tr>
				<td class="icon">
					<img src="images/Modeller.png" onclick="return openWindow('https://staruml.io/', 'StarUML');"/>
				</td>
				<td>
					<a onclick="return openWindow('https://staruml.io/', 'StarUML');">StarUML Modeling Tool</a> - a COTS tool
					used by your Business Analyst to create the API's<br/>business domain model.  Not sure where to begin? We offer
					consulting services to help you<br/>ideate your API's business value proposition and model your business domain
					resources.<br/>Contact <a href="mailto:sales@api-excellence.com">sales@api-excellence.com</a> for more information.
				</td>
			</tr>
			<tr>
				<td class="icon">
					<img src="images/SpecGenerator.png" onclick="return openWindow('viewApiSpecForm', 'APISpecGenerator');"/>
				</td>
				<td>
					<a onclick="return openWindow('viewApiSpecForm', 'APISpecGenerator');">API Specification Generator</a> -
					generates an OpenAPI client interface specification from the<br/>domain model's resource diagram.
				</td>
			</tr>
			<tr>
				<td class="icon">
					<img src="images/CodeGenerator.png" onclick="return openWindow('viewApiCodeForm', 'APICodeGenerator');"/>
				</td>
				<td>
					<a onclick="return openWindow('viewApiCodeForm', 'APICodeGenerator');">API Code Generator</a> - generates
					the API's skeleton implementation code (API controllers and<br/>model schema DTOs) from the API's client
					interface specification.
				</td>
			</tr>
		</table>
	</div>
</body>
</html>