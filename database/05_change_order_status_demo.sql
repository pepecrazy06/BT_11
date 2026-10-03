-- Dùng để quan sát giao diện trên DATABASE THỬ NGHIỆM.
-- Đặt một đơn COD bằng tài khoản của bạn, rồi điền ID của đơn vào @OrderId.
-- Cho phép chuyển qua bất kỳ trạng thái nào để thử bộ lọc.
-- Có cập nhật tồn kho và trạng thái tiền; không dùng để sửa đơn thực tế.
USE webst9;
GO
SELECT TOP (30) id,userId,recipient,status,paymentStatus,total,createdAt
FROM dbo.purchase_orders ORDER BY id DESC;
GO
DECLARE @OrderId bigint = 0; -- Đổi 0 thành ID, ví dụ #HB12 => 12.
DECLARE @Status varchar(30) = 'PREPARING'; -- Xem bảng mã ở 04_order_statuses.sql.
DECLARE @OldStatus varchar(30), @Payment varchar(30);
DECLARE @OldReturned bit, @NewReturned bit;
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRANSACTION;
 IF @Status NOT IN ('PENDING','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED')
  THROW 50002,N'Mã trạng thái không hợp lệ.',1;
 SELECT @OldStatus=status,@Payment=paymentStatus
 FROM dbo.purchase_orders WITH (UPDLOCK,HOLDLOCK) WHERE id=@OrderId;
 IF @OldStatus IS NULL THROW 50003,N'Chưa chọn đúng @OrderId. Xem danh sách đơn ở kết quả SELECT.',1;
 SET @OldReturned=CASE WHEN @OldStatus IN ('CANCELLED','RETURNED') THEN 1 ELSE 0 END;
 SET @NewReturned=CASE WHEN @Status IN ('CANCELLED','RETURNED') THEN 1 ELSE 0 END;
 DECLARE @Items TABLE(bookId int PRIMARY KEY, quantity int);
 INSERT @Items SELECT bookId,SUM(quantity) FROM dbo.purchase_order_items WHERE order_id=@OrderId GROUP BY bookId;
 -- Khóa sách theo ID giống ứng dụng để tránh cập nhật đồng thời sai tồn kho.
 DECLARE @BookId int,@Qty int,@Stock int;
 DECLARE item_cursor CURSOR LOCAL FAST_FORWARD FOR SELECT bookId,quantity FROM @Items ORDER BY bookId;
 OPEN item_cursor;
 FETCH NEXT FROM item_cursor INTO @BookId,@Qty;
 WHILE @@FETCH_STATUS=0
 BEGIN
  SET @Stock=NULL;
  SELECT @Stock=quantity FROM dbo.books WITH(UPDLOCK,HOLDLOCK) WHERE bookid=@BookId;
  IF @OldReturned=0 AND @NewReturned=1
   UPDATE dbo.books SET quantity=COALESCE(quantity,0)+@Qty WHERE bookid=@BookId;
  IF @OldReturned=1 AND @NewReturned=0
  BEGIN
   IF @Stock IS NULL OR @Stock<@Qty THROW 50004,N'Không đủ tồn kho để đưa đơn hủy/hoàn trở lại trạng thái đang xử lý.',1;
   UPDATE dbo.books SET quantity=quantity-@Qty WHERE bookid=@BookId;
  END;
  FETCH NEXT FROM item_cursor INTO @BookId,@Qty;
 END;
 CLOSE item_cursor;
 DEALLOCATE item_cursor;
 UPDATE dbo.purchase_orders SET status=@Status,
 paymentStatus=CASE
  WHEN @Status='DELIVERED' THEN 'PAID'
  WHEN @Status='RETURNED' AND @Payment IN ('PAID','REFUND_PENDING') THEN 'REFUND_PENDING'
  ELSE 'UNPAID' END
 WHERE id=@OrderId;
 COMMIT;
 SELECT id,userId,status,paymentStatus,total FROM dbo.purchase_orders WHERE id=@OrderId;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO
-- Trở lại trình duyệt, F5 trang /orders hoặc /admin/orders và chọn bộ lọc.
