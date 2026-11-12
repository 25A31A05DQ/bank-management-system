<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>

<section class="panel panel-form">
    <h2>Record a transaction</h2>

    <c:choose>
        <c:when test="${empty customers}">
            <p class="empty">There are no customer accounts yet.
                <a class="link" href="${ctx}/customers/add">Add a customer</a> first.</p>
        </c:when>
        <c:otherwise>
            <form method="post" action="${ctx}/transaction" class="form" id="transactionForm"
                  data-currency="<c:out value='${initParam.currencySymbol}'/>">
                <div class="field">
                    <label for="customerId">Customer account</label>
                    <select id="customerId" name="customerId" required>
                        <option value="">Select an account</option>
                        <c:forEach var="c" items="${customers}">
                            <option value="${c.id}" data-balance="${c.balance}" ${c.id == selectedId ? 'selected' : ''}>
                                <c:out value="${c.accountNumber} - ${c.name}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <p class="hint" id="balanceHint">Select an account to see its available balance.</p>
                </div>

                <fieldset class="field type-choice">
                    <legend>Transaction type</legend>
                    <label class="choice">
                        <input type="radio" name="type" value="DEPOSIT" ${selectedType == 'WITHDRAWAL' ? '' : 'checked'}>
                        <span>Deposit</span>
                    </label>
                    <label class="choice">
                        <input type="radio" name="type" value="WITHDRAWAL" ${selectedType == 'WITHDRAWAL' ? 'checked' : ''}>
                        <span>Withdrawal</span>
                    </label>
                </fieldset>

                <div class="field">
                    <label for="amount">Amount</label>
                    <input type="number" id="amount" name="amount" min="0.01" max="10000000" step="0.01"
                           inputmode="decimal" required value="<c:out value='${amount}'/>">
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Submit transaction</button>
                    <a class="btn btn-secondary" href="${ctx}/dashboard">Cancel</a>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</section>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
