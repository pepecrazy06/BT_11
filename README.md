# \# 📚 Huy Books

# 

# \*\*Sinh viên:\*\* Thái Nhựt Huy  

# \*\*MSSV:\*\* 24110227  

# \*\*Nội dung cập nhật:\*\* Hoàn thành yêu cầu bài tập 11, update giao diện và cập nhật them thông tin sản phẩm.
# \*\*Tài khoản test:\*\* admin@huybooks.local; Huy@24110227



# Ứng dụng quản lý và bán sách được xây dựng bằng \*\*Java Servlet/JSP\*\*, sử dụng \*\*JDK 21\*\*, \*\*Apache Tomcat 11\*\* và \*\*Microsoft SQL Server\*\*.

# 

# \---

# 

# \## 🛠 Công nghệ sử dụng

# 

# | Thành phần | Công nghệ |

# |---|---|

# | Ngôn ngữ | Java 21 |

# | Backend | Jakarta Servlet |

# | Frontend | JSP, HTML, CSS, JavaScript |

# | ORM | JPA / Hibernate |

# | Database | Microsoft SQL Server |

# | Application Server | Apache Tomcat 11 |

# | Build Tool | Maven |

# | Xác thực mật khẩu | PBKDF2 |

# | Email | SMTP |

# | Encoding | UTF-8 |

# 

# \---

# 

# \# ✨ Chức năng hệ thống

# 

# \## 1. Trang chủ

# 

# Trang chủ hỗ trợ:

# 

# \- Giới thiệu nhà sách.

# \- Hiển thị danh sách sách.

# \- Tìm kiếm theo:

# &#x20; - Tên sách.

# &#x20; - Tác giả.

# &#x20; - ISBN.

# \- Lọc sách còn hàng.

# \- Sắp xếp theo:

# &#x20; - Giá.

# &#x20; - Tên.

# &#x20; - Sách mới nhất.

# \- Phân trang danh sách sách.

# 

# \---

# 

# \## 2. Chi tiết sách

# 

# Trang chi tiết hiển thị:

# 

# \- Ảnh bìa.

# \- Tên sách.

# \- Giá.

# \- Tác giả.

# \- Nhà xuất bản.

# \- Ngày xuất bản.

# \- Số lượng tồn kho.

# \- Nội dung giới thiệu.

# \- Chọn số lượng trước khi thêm vào giỏ hàng.

# \- Đánh giá sách từ \*\*1 đến 5 sao\*\*.

# 

# \---

# 

# \## 3. Giỏ hàng

# 

# Hệ thống hỗ trợ:

# 

# \- Thêm sách vào giỏ hàng.

# \- Tự động gộp khi thêm cùng một sách nhiều lần.

# \- Tăng hoặc giảm số lượng.

# \- Cập nhật số lượng.

# \- Xóa từng sản phẩm.

# \- Xóa toàn bộ giỏ hàng.

# 

# Số lượng tối đa cho mỗi sách được giới hạn theo:

# 

# ```text

# min(99, số lượng tồn kho)

# ```

# 

# Giỏ hàng được lưu trong \*\*HTTP Session\*\*.

# 

# > Khi phiên làm việc kết thúc hoặc người dùng đăng xuất, dữ liệu giỏ hàng sẽ bị xóa.

# 

# \---

# 

# \## 4. Thanh toán COD

# 

# Người dùng có thể đặt hàng bằng hình thức \*\*Cash On Delivery – COD\*\*.

# 

# Thông tin thanh toán gồm:

# 

# \- Người nhận.

# \- Số điện thoại.

# \- Địa chỉ giao hàng.

# 

# Người dùng phải đăng nhập trước khi đặt hàng.

# 

# Khi đặt hàng thành công:

# 

# 1\. Đơn hàng được tạo.

# 2\. Chi tiết đơn hàng được lưu.

# 3\. Tồn kho được trừ.

# 4\. Toàn bộ quá trình được thực hiện trong một transaction.

# 5\. Trang xác nhận đơn hàng được hiển thị.

# 

# \---

# 

# \## 5. Đơn hàng của tôi

# 

# Người dùng có thể:

# 

# \- Xem lịch sử đặt hàng.

# \- Lọc đơn theo trạng thái.

# \- Xem chi tiết từng đơn.

# \- Hủy đơn khi trạng thái còn là:

# 

# ```text

# PENDING

# ```

# 

# Khi hủy:

# 

# \- Tồn kho được hoàn lại.

# \- Việc hủy và hoàn kho được thực hiện trong cùng một transaction.

# \- Một đơn hàng không thể được hoàn kho hai lần.

# 

# \---

# 

# \# 🔐 Tài khoản và bảo mật

# 

# \## Đăng nhập / đăng ký

# 

# Hệ thống hỗ trợ:

# 

# \- Đăng nhập.

# \- Đăng ký.

# \- Kiểm tra dữ liệu người dùng.

# \- Xác thực email bằng OTP.

# 

# \---

# 

# \## OTP

# 

# OTP có:

# 

# \- Thời hạn: \*\*5 phút\*\*.

# \- Tối đa: \*\*5 lần thử\*\*.

# 

# \---

# 

# \# 👨‍💼 Quản trị hệ thống

# 

# Quản trị viên có bảng điều khiển riêng.

# 

# \## Dashboard

# 

# Hiển thị:

# 

# \- Doanh thu COD đã thu.

# \- Số đơn hàng mới.

# \- Các sách có tồn kho thấp.

# \- Thông tin tổng quan hệ thống.

# 

# \---

# 

# \## Quản lý sách

# 

# Quản trị viên có thể:

# 

# \- Thêm sách.

# \- Sửa sách.

# \- Xóa sách.

# \- Chọn nhiều tác giả cho một sách.

# \- Quản lý ảnh bìa.

# \- Quản lý ngày xuất bản.

# \- Quản lý giá.

# \- Quản lý tồn kho.

# 

# \---

# 

# \## Quản lý tác giả

# 

# Hỗ trợ:

# 

# \- Thêm tác giả.

# \- Sửa tác giả.

# \- Xóa tác giả.

# 

# \---

# 

# \# 🎨 Giao diện

# 

# Giao diện sử dụng chung cho toàn bộ hệ thống và hỗ trợ:

# 

# \- Tiếng Việt UTF-8.

# \- Responsive.

# \- Menu dành cho thiết bị di động.

# \- Ảnh bìa dự phòng khi sách không có ảnh.

# \- JSP include dùng để tái sử dụng các thành phần giao diện.

# 

# \*\*SiteMesh đã được gỡ bỏ\*\* do không tương thích với luồng `forward` hiện tại trên Tomcat 11.

# 

# \---

# 

# \# 🗄 Database

# 

# \# ✉️ Email OTP

# 

# Thêm các biến sau trước khi test

# ```text

# SMTP\_USERNAME

# SMTP\_PASSWORD

# SMTP\_HOST

# SMTP\_PORT

# ```

# 

# \## Ý nghĩa

# 

# \### SMTP\_USERNAME

# 

# Địa chỉ email dùng để gửi OTP.

# 

# Ví dụ:

# 

# ```text

# SMTP\_USERNAME=example@gmail.com

# ```

# 

# \### SMTP\_PASSWORD

# 

# Mật khẩu ứng dụng SMTP.

# 

# ```text

# SMTP\_PASSWORD=xxxxxxxxxxxxxxxx

# ```

# 

# \### SMTP\_HOST

# 

# Mặc định:

# 

# ```text

# SMTP\_HOST=smtp.gmail.com

# ```

# \---

# 

# \---

# 

# \---

# 

# \# 👤 Tác giả

# 

# \*\*Thái Nhựt Huy\*\*  

# \*\*MSSV:\*\* 24110227

# 

# Ứng dụng \*\*Huy Books\*\* — Servlet/JSP Book Management \& COD Ordering System.

