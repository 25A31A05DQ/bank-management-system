<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>

<div class="stats">
    <div class="stat-card">
        <div class="stat-label">Total customers</div>
        <div class="stat-value"><fmt:formatNumber value="${totalCustomers}" pattern="#,##0"/></div>
    </div>
    <div class="stat-card">
        <div class="stat-label">Total bank balance</div>
        <div class="stat-value"><c:out value="${initParam.currencySymbol}"/><fmt:formatNumber value="${totalBalance}" pattern="#,##0.00"/></div>
    </div>
</div>

<div class="quick-actions">
    <a class="btn btn-primary" href="${ctx}/customers/add">Add customer</a>
    <a class="btn btn-secondary" href="${ctx}/transaction">Deposit / Withdraw</a>
    <a class="btn btn-secondary" href="${ctx}/customers">View customers</a>
</div>

<section class="panel">
    <div class="panel-head">
        <h2>Recent transactions</h2>
        <a class="link" href="${ctx}/history">View all</a>
    </div>
    <div class="table-wrap">
        <table class="table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Account number</th>
                <th>Customer</th>
                <th>Type</th>
                <th class="num">Amount</th>
                <th>Date</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="t" items="${recentTransactions}">
                <tr>
                    <td>#<c:out value="${t.id}"/></td>
                    <td><c:out value="${t.accountNumber}"/></td>
                    <td><c:out value="${t.customerName}"/></td>
                    <td><span class="badge ${t.type == 'DEPOSIT' ? 'badge-deposit' : 'badge-withdrawal'}">${t.type == 'DEPOSIT' ? 'Deposit' : 'Withdrawal'}</span></td>
                    <td class="num"><c:out value="${initParam.currencySymbol}"/><fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/></td>
                    <td><fmt:formatDate value="${t.transactionDate}" pattern="dd MMM yyyy, HH:mm"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty recentTransactions}">
                <tr><td colspan="6" class="empty">No transactions yet. Record a deposit to get started.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</section>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
