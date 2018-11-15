<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<c:url value="../../css/search.css"/>"/>
        <meta charset="UTF-8"/>
        <title>Search</title>
    </head>
    <body>
        <div class="center-module">We can find what you want (probably)</div>
        <div class="center-module">
            <form id="search-form" action="search" method="GET">
                <input class="search-input" type="text" id="q" name="q" required>
                <input class="search-button" type="submit" value="Submit"/>
            </form>
        </div>

    </body>

</html>