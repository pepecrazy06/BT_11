<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>
<c:set var="pageTitle" value="Không thể mở trang"/><c:set var="activeNav" value=""/>
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<main id="main" class="shell"><section class="error-page"><p class="eyebrow">HUY BOOKS</p><div class="empty-symbol">${requestScope['jakarta.servlet.error.status_code']}</div><h1>${requestScope['jakarta.servlet.error.status_code'] == 403 ? 'Bạn chưa có quyền truy cập' : requestScope['jakarta.servlet.error.status_code'] == 404 ? 'Trang này chưa có trong tủ sách' : 'Một chút trục trặc nhỏ'}</h1><p class="muted">Vui lòng quay lại trang chủ hoặc thử lại sau. Nếu lỗi tiếp tục, hãy kiểm tra kết nối database và nhật ký máy chủ.</p><a class="button" href="${pageContext.request.contextPath}/home">Về trang chủ →</a></section></main>
<%@ include file="/WEB-INF/views/common/footer.jspf" %>