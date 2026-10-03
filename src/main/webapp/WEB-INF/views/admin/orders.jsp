<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>
<c:set var="pageTitle" value="Quản lý đơn hàng"/><c:set var="activeNav" value="admin"/>
<%@ include file="../common/header.jspf" %>

<main id="main" class="shell page-main"><c:set var="adminTab" value="orders"/><%@ include file="../common/admin-nav.jspf" %><div class="page-heading"><div><p class="eyebrow">KHÔNG GIAN QUẢN TRỊ</p><h1>Đơn hàng COD</h1><p>Quản lý ${total} đơn hàng · Thu tiền khi giao thành công.</p></div></div><%@ include file="../common/order-filter.jspf" %><div class="table-scroll"><table><thead><tr><th>Mã đơn</th><th>Ngày đặt</th><th>Người nhận</th><th>Tổng tiền</th><th>Trạng thái</th><th></th></tr></thead><tbody><c:forEach items="${orders}" var="o"><tr><td><strong>#HB${o.id}</strong></td><td>${o.createdLabel}</td><td><c:out value="${o.recipient}"/><small class="muted" style="display:block">${o.paymentLabel}</small></td><td><strong><fmt:formatNumber value="${o.total}" maxFractionDigits="0"/> đ</strong></td><td><span class="status ${o.status}">${o.statusLabel}</span></td><td><a class="text-link" href="${pageContext.request.contextPath}${activeNav == 'admin' ? '/admin/orders?id=' : '/order-success?id='}${o.id}">Chi tiết →</a></td></tr></c:forEach><c:if test="${empty orders}"><tr><td colspan="6" style="text-align:center;padding:45px">Chưa có đơn hàng trong danh sách này.</td></tr></c:if></tbody></table></div><c:set var="paginationPath" value="/admin/orders"/><%@ include file="../common/pagination.jspf" %></main>
<%@ include file="../common/footer.jspf" %>