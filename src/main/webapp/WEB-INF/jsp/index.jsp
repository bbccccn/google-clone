<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<c:url value="../../css/index.css"/>"/>
        <meta charset="UTF-8" />
        <title>Index page</title>
    </head>
    <body>
        <div class="center-module">
            <form:form id="indexform" action="index" method="POST">

                Enter url to index (f.e. https://www.wikipedia.org/): <input class="index-input" id="q" type="text" name="q" required>
                <br>
                Enter depth of index (default value - 2): <input class="index-depth-input" id="depth" type="text" name="depth">

                <input class="index-button" type="submit" value="Submit" />
            </form:form>
        </div>
        <div class="center-module">
            <c:if test="${not empty executionTime}">
                <p>Execution time: ${executionTime} seconds</p>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <p class="error-message">Error encountered: ${errorMessage}.</p>
            </c:if>
        </div>
    </body>

</html>