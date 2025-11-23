<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<title>User Administration</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
	<script type="text/javascript">
    </script>
</head>
<body>
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>
    <div>
        <button onclick="">Export Users CSV</button>
    </div>
    <br/>
    <div>
        <table id="userTable">
            <thead>
                <tr style='background-color: #f2f2f2;'>
                    <th></th>
                    <th>Status</th>
                    <th>Company</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Expiration Date</th>
                </tr>
            </thead>
        <tbody>
${userTable}
        </tbody>
    </div>
</body>
</html>