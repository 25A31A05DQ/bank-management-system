<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Something went wrong | Mini Bank</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body class="login-page">
<div class="error-card">
    <h1>
        <c:choose>
            <c:when test="${pageContext.errorData.statusCode == 404}">Page not found</c:when>
            <c:when test="${pageContext.errorData.statusCode == 405}">Action not allowed</c:when>
            <c:otherwise>Something went wrong</c:otherwise>
        </c:choose>
    </h1>
    <p>
        <c:choose>
            <c:when test="${pageContext.errorData.statusCode == 404}">The page you asked for does not exist.</c:when>
            <c:when test="${pageContext.errorData.statusCode == 405}">This page cannot be opened that way.</c:when>
            <c:otherwise>The server could not complete your request. Try again, or check the Tomcat log for details.</c:otherwise>
        </c:choose>
    </p>
    <a class="btn btn-primary" href="${ctx}/dashboard">Back to dashboard</a>
</div>
</body>
</html>
