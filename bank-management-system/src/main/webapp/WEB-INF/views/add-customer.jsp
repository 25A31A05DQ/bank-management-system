<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/fragments/header.jsp"/>

<section class="panel panel-form">
    <h2>New customer</h2>
    <p class="hint">An account number is generated automatically and the opening balance is zero.</p>

    <form method="post" action="${ctx}/customers/add" class="form">
        <div class="field">
            <label for="name">Full name</label>
            <input type="text" id="name" name="name" maxlength="100" required value="<c:out value='${form.name}'/>">
        </div>
        <div class="field-row">
            <div class="field">
                <label for="phone">Phone number</label>
                <input type="tel" id="phone" name="phone" maxlength="16" required
                       pattern="\+?[0-9]{10,15}" title="10-15 digits, optional leading +"
                       value="<c:out value='${form.phone}'/>">
            </div>
            <div class="field">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" maxlength="100" required value="<c:out value='${form.email}'/>">
            </div>
        </div>
        <div class="field">
            <label for="address">Address</label>
            <textarea id="address" name="address" rows="3" minlength="5" maxlength="255" required><c:out value="${form.address}"/></textarea>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Save customer</button>
            <a class="btn btn-secondary" href="${ctx}/customers">Cancel</a>
        </div>
    </form>
</section>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>
