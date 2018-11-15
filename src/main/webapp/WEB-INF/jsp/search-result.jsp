<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="<c:url value="../../css/search-result.css"/>"/>
        <meta charset="UTF-8"/>
        <title>${query} :: Search Results</title>
    </head>
    <body>
        <div class="search-form">
            <form id="search-form" action="search" method="GET">
                <input class="search-field" type="text" id="q" name="q" value="${query}" required>
                <input class="search-button" type="submit" value="Submit" onclick="handleClick()"/>
            </form>
        </div>
        <div class="search-results">
            <c:if test="${!(not empty searchResults)}">No results for request ${query} has been found.</c:if>
            <c:forEach items="${searchResults}" var="res">
                <div class="search-item">
                    <a class="site-name" href="${res.url}"><c:out value="${res.title}"/></a>
                    <br>
                    <a class="site-url" href="${res.url}"><c:out value="${res.url}"/></a>
                    <br>
                    <p>${res.hitBlock}</p>
                    <br>
                </div>
            </c:forEach>
        </div>
        <div>
            <c:set var="req" value="${pageContext.request}" />
            <c:set var="url">${req.requestURL}</c:set>
            <c:set var="base" value="${fn:substring(url, 0, fn:length(url) - fn:length(req.requestURI))}${req.contextPath}/" />

            <c:forEach var="i" begin="1" end="${amountOfPages}">
                    <c:if test="${i == currentPage}">
                        <a href="${base}search?q=${query}&page=${i}"><b>${i}</b></a>
                    </c:if>
                    <c:if test="${i != currentPage}">
                        <a href="${base}search?q=${query}&page=${i}">${i}</a>
                    </c:if>
            </c:forEach>
        </div>
    </body>

    <script type="text/javascript">
        function handleClick() {
            document.getElementById('search-form').action = "search?q=" + document.getElementById("q").value;

        }
    </script>

</html>