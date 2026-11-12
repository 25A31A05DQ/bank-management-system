<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>

<section class="panel">
    <div class="panel-head">
        <h2>All transactions</h2>
        <a class="btn btn-primary" href="${ctx}/transaction">New transaction</a>
    </div>
    <div class="table-wrap">
        <table class="table">
            <thead>
            <tr>
                <th>Transaction ID</th>
                <th>Account number</th>
                <th>Customer</th>
                <th>Type</th>
                <th class="num">Amount</th>
                <th>Date</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="t" items="${transactions}">
                <tr>
                    <td>#<c:out value="${t.id}"/></td>
                    <td><c:out value="${t.accountNumber}"/></td>
                    <td><c:out value="${t.customerName}"/></td>
                    <td><span class="badge ${t.type == 'DEPOSIT' ? 'badge-deposit' : 'badge-withdrawal'}">${t.type == 'DEPOSIT' ? 'Deposit' : 'Withdrawal'}</span></td>
                    <td class="num"><c:out value="${initParam.currencySymbol}"/><fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/></td>
                    <td><fmt:formatDate value="${t.transactionDate}" pattern="dd MMM yyyy, HH:mm"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty transactions}">
                <tr><td colspan="6" class="empty">No transactions yet. Record a deposit to get started.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
    <c:if test="${fn:length(transactions) >= maxRows}">
        <p class="hint table-note">Showing the latest <c:out value="${maxRows}"/> transactions.</p>
    </c:if>
</section>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
