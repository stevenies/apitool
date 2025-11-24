<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<title>User Administration</title>
	<link rel="stylesheet" type="text/css" href="styles.css">
	<script type="text/javascript">

        function editUser(userId) {
            const url = 'viewUsers?userId=' + userId + "&nocache=" + new Date().getTime();
            window.location.href = url;
            return false
        }

        function saveUser() {
            const form = document.getElementById('userAdminForm');
            form.submit();
            return false;
        }

        window.onload = function() {
            const targetElement = document.getElementById('saveButton');
            if (targetElement) {
                targetElement.scrollIntoView({behavior: 'auto', block: 'center'});
            }
        };

    </script>
</head>
<body>
    <c:if test="${not empty errors}">
        <div class="errorPanel">
            <ul>
                <c:forEach var="error" items="${errors}">
                    <li>${error}</li>
                </c:forEach>
            </ul>
        </div>
        <br/>
   </c:if>
    <!-- TODO
    <div>
        <button onclick="">Export Users CSV</button>
    </div>
    <br/>
    -->
    <div>
        <form id="userAdminForm" method="post" action="saveUser">
            <table id="userTable">
                <thead>
                    <tr id="userTableHeader">
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
            </table>
        </form>
    </div>
</body>
</html>