<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin login | Mini Bank</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body class="login-page">
<div class="login-shell">
    <section class="login-intro">
        <span class="brand-mark">MB</span>
        <h1>Mini Bank</h1>
        <p>Manage customer accounts, deposits and withdrawals from one place.</p>
    </section>

    <section class="login-card">
        <h2>Admin sign in</h2>

        <c:if test="${not empty success}">
            <div class="alert alert-success" role="status"><c:out value="${success}"/></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error" role="alert"><c:out value="${error}"/></div>
        </c:if>

        <form method="post" action="${ctx}/login" class="form">
            <div class="field">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" maxlength="50" required autofocus
                       autocomplete="username" value="<c:out value='${username}'/>">
            </div>
            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" maxlength="100" required
                       autocomplete="current-password">
            </div>
            <button type="submit" class="btn btn-primary btn-block">Sign in</button>
        </form>
    </section>
</div>
</body>
</html>
