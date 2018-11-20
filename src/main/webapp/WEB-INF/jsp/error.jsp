<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <body>
        <h1>Something went wrong! </h1>
        <c:if test="${not empty additionalErrorMessage}">
            <h2>Error: ${additionalErrorMessage}</h2>
        </c:if>
        <h2>We notified about this and working on ths problem now.</h2>
        <a href="/">Go Home</a>
    </body>
</html>