<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>
<c:set var="pageTitle" value="Chi tiết đơn hàng"/><c:set var="activeNav" value="admin"/>
<%@ include file="../common/header.jspf" %>

<main id="main" class="shell page-main"><c:set var="adminTab" value="orders"/><%@ include file="../common/admin-nav.jspf" %><div class="page-heading"><div><p class="eyebrow">KHÔNG GIAN QUẢN TRỊ</p><h1>Chi tiết đơn hàng</h1></div><a class="text-link small" href="${pageContext.request.contextPath}/admin/orders">← Danh sách đơn</a></div><%@ include file="../common/order-content.jspf" %></main>
<%@ include file="../common/footer.jspf" %>