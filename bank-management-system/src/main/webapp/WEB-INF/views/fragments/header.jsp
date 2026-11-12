<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}"/> | Mini Bank</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<div class="layout">
    <aside class="sidebar" id="sidebar">
        <div class="brand">
            <span class="brand-mark">MB</span>
            <span class="brand-name">Mini Bank</span>
        </div>
        <nav class="nav" aria-label="Main navigation">
            <a class="nav-link ${activePage == 'dashboard' ? 'active' : ''}" href="${ctx}/dashboard">Dashboard</a>
            <a class="nav-link ${activePage == 'customers' ? 'active' : ''}" href="${ctx}/customers">Customers</a>
            <a class="nav-link ${activePage == 'add-customer' ? 'active' : ''}" href="${ctx}/customers/add">Add customer</a>
            <a class="nav-link ${activePage == 'transaction' ? 'active' : ''}" href="${ctx}/transaction">Deposit / Withdraw</a>
            <a class="nav-link ${activePage == 'history' ? 'active' : ''}" href="${ctx}/history">Transactions</a>
        </nav>
        <a class="nav-link nav-logout" href="${ctx}/logout">Log out</a>
    </aside>

    <div class="main">
        <header class="topbar">
            <button type="button" class="menu-toggle" id="menuToggle" aria-label="Toggle navigation">Menu</button>
            <h1 class="page-title"><c:out value="${pageTitle}"/></h1>
            <div class="user">Signed in as <strong><c:out value="${sessionScope.adminUser}"/></strong></div>
        </header>

        <main class="content">
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="alert alert-success" role="status"><c:out value="${sessionScope.flashSuccess}"/></div>
                <c:remove var="flashSuccess" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.flashError}">
                <div class="alert alert-error" role="alert"><c:out value="${sessionScope.flashError}"/></div>
                <c:remove var="flashError" scope="session"/>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success" role="status"><c:out value="${success}"/></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-error" role="alert"><c:out value="${error}"/></div>
            </c:if>
            <c:if test="${not empty errors}">
                <div class="alert alert-error" role="alert">
                    <ul>
                        <c:forEach var="e" items="${errors}"><li><c:out value="${e}"/></li></c:forEach>
                    </ul>
                </div>
            </c:if>
