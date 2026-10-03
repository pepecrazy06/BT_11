-- Huy Books / Thái Nhựt Huy / 24110227
-- Chạy sau 01_schema.sql. Giữ nguyên các đơn hàng hiện có.
USE webst9;
GO
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRANSACTION;
 IF EXISTS(SELECT 1 FROM dbo.purchase_orders WHERE status NOT IN
 ('PENDING','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED'))
  THROW 50001, N'Có mã trạng thái lạ. Kiểm tra SELECT DISTINCT status FROM purchase_orders trước khi chạy.', 1;
 IF NOT EXISTS(SELECT 1 FROM sys.check_constraints WHERE name='CK_huy_order_status' AND parent_object_id=OBJECT_ID('dbo.purchase_orders'))
  ALTER TABLE dbo.purchase_orders WITH CHECK ADD CONSTRAINT CK_huy_order_status
   CHECK(status IN ('PENDING','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED'));
 COMMIT;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO
SELECT code, label FROM (VALUES
 ('PENDING',N'Đơn hàng mới'),('CONFIRMED',N'Đã xác nhận'),
 ('PREPARING',N'Chuẩn bị hàng'),('SHIPPING',N'Vận chuyển'),
 ('DELIVERING',N'Giao hàng'),('DELIVERED',N'Đã giao'),
 ('CANCELLED',N'Đơn hàng hủy'),('RETURNED',N'Đơn hàng hoàn')
) AS statuses(code,label);
GO
