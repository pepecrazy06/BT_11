<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>
<c:set var="pageTitle" value="Chi tiết đơn hàng"/><c:set var="activeNav" value="orders"/>
<%@ include file="common/header.jspf" %>
<main id="main" class="shell page-main">
<div class="success-banner">
<div class="success-mark">${order.status == 'CANCELLED' ? '−' : (order.status == 'RETURNED' ? '↩' : '✓')}</div>
<p class="eyebrow">HÀNH TRÌNH CUỐN SÁCH CỦA BẠN</p>
<h1>${order.status == 'CANCELLED' ? 'Đơn hàng đã được hủy' : (order.status == 'RETURNED' ? 'Đơn hàng đã được hoàn' : 'Cảm ơn bạn đã chọn Huy Books')}</h1>
<p>Theo dõi trạng thái và thông tin nhận sách của bạn tại đây.</p></div>
<%@ include file="common/order-content.jspf" %>
<div style="text-align:center;margin-top:28px"><a class="button button-outline" href="${pageContext.request.contextPath}/orders">Đơn hàng của tôi</a> <a class="button" href="${pageContext.request.contextPath}/home">Tiếp tục khám phá →</a></div></main>
<%@ include file="common/footer.jspf" %>
