-- Tài khoản minh họa quản trị cho môi trường bài tập.
-- Email: admin@huybooks.local | Mật khẩu: Huy@24110227
-- Không thay mật khẩu hoặc quyền của tài khoản đã tồn tại.
USE webst9;
GO
IF NOT EXISTS(SELECT 1 FROM dbo.users WHERE email='admin@huybooks.local')
 INSERT INTO dbo.users(email,fullname,phone,passwd,admin)
 VALUES('admin@huybooks.local',N'Thái Nhựt Huy','0901234567','pbkdf2$120000$50ZAy2v2XYVpjkSQ9yDtow==$o2vX5yDo3h/YENB35eV6ToGd6qRndSLf9ln7UjT32Ps=',1);
GO