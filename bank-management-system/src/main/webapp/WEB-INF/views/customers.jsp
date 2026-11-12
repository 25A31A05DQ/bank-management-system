<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>

<section class="panel">
    <div class="panel-head">
        <form class="search" method="get" action="${ctx}/customers">
            <input type="search" name="q" maxlength="100" aria-label="Search customers"
                   placeholder="Search by name or account number" value="<c:out value='${q}'/>">
            <button type="submit" class="btn btn-primary">Search</button>
            <c:if test="${not empty q}">
                <a class="btn btn-secondary" href="${ctx}/customers">Clear</a>
            </c:if>
        </form>
        <a class="btn btn-primary" href="${ctx}/customers/add">Add customer</a>
    </div>

    <div class="table-wrap">
        <table class="table">
            <thead>
            <tr>
                <th>Account number</th>
                <th>Name</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Address</th>
                <th class="num">Balance</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="c" items="${customers}">
                <tr>
                    <td><c:out value="${c.accountNumber}"/></td>
                    <td><c:out value="${c.name}"/></td>
                    <td><c:out value="${c.phone}"/></td>
                    <td><c:out value="${c.email}"/></td>
                    <td><c:out value="${c.address}"/></td>
                    <td class="num"><c:out value="${initParam.currencySymbol}"/><fmt:formatNumber value="${c.balance}" pattern="#,##0.00"/></td>
                    <td><a class="link" href="${ctx}/transaction?account=<c:out value='${c.accountNumber}'/>">Deposit / Withdraw</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty customers}">
                <tr>
                    <td colspan="7" class="empty">
                        <c:choose>
                            <c:when test="${not empty q}">No customers match your search.</c:when>
                            <c:otherwise>No customers yet. Add your first customer to open an account.</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</section>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
