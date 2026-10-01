-- Dữ liệu minh họa cho bài làm 24110227. Giá, số lượng và mã DEMO là dữ liệu mẫu.
USE webst9;
GO
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRANSACTION;
 DECLARE @catalog TABLE(code nvarchar(30),title nvarchar(200),author_name nvarchar(200),price decimal(19,2),cover nvarchar(255),description nvarchar(2000));
 INSERT INTO @catalog VALUES
 (N'DEMO-24110227-01',N'Đắc nhân tâm',N'Dale Carnegie',86000,N'assets/images/dac-nhan-tam.jpg',N'Những góc nhìn gần gũi về giao tiếp, lắng nghe và cách xây dựng các mối quan hệ. Một điểm bắt đầu cho hành trình thấu hiểu bản thân và mọi người.'),
 (N'DEMO-24110227-02',N'Nhà giả kim',N'Paulo Coelho',79000,N'assets/images/nha-gia-kim.jpg',N'Một câu chuyện về ước mơ, sự kiên trì và hành trình tìm kiếm ý nghĩa. Đồng hành cùng nhân vật qua những lựa chọn để lắng nghe điều trái tim muốn nói.'),
 (N'DEMO-24110227-03',N'Tuổi trẻ đáng giá bao nhiêu?',N'Rosie Nguyễn',90000,N'assets/images/tuoi-tre-dang-gia-bao-nhieu.jpg',N'Gợi mở về học tập, trải nghiệm và trưởng thành. Dành cho những bạn đọc muốn chủ động tạo nên những năm tháng tuổi trẻ đáng nhớ.'),
 (N'DEMO-24110227-04',N'Mắt biếc',N'Nguyễn Nhật Ánh',110000,N'assets/images/mat-biec.jpg',N'Một câu chuyện dịu dàng về tuổi thơ và những rung động đầu đời. Những trang viết gợi nhiều suy ngẫm về tình cảm, ký ức và sự trưởng thành.'),
 (N'DEMO-24110227-05',N'Hoàng tử bé',N'Antoine de Saint-Exupéry',68000,N'assets/images/hoang-tu-be.jpg',N'Cuộc gặp gỡ nhỏ mở ra những câu hỏi lớn về tình bạn, tình yêu và cách người lớn nhìn thế giới. Một cuốn sách có thể đọc lại ở nhiều thời điểm khác nhau.'),
 (N'DEMO-24110227-06',N'Dế Mèn phiêu lưu ký',N'Tô Hoài',65000,N'assets/images/de-men.jpg',N'Hành trình khám phá thế giới của Dế Mèn với những bài học về lòng dũng cảm, tình bạn và trách nhiệm. Câu chuyện thân quen dành cho nhiều thế hệ bạn đọc.'),
 (N'DEMO-24110227-07',N'Tôi tài giỏi, bạn cũng thế!',N'Adam Khoo',125000,N'assets/images/toi-tai-gioi.jpg',N'Những gợi ý giúp bạn đọc xây dựng thói quen học tập, xác định mục tiêu và phát triển sự tự tin. Phù hợp với hành trình khám phá tiềm năng bản thân.'),
 (N'DEMO-24110227-08',N'Quẳng gánh lo đi và vui sống',N'Dale Carnegie',92000,N'assets/images/quang-ganh-lo.jpg',N'Các góc nhìn về nỗi lo và cách sống chủ động hơn trong những điều thường ngày. Một lựa chọn dành cho bạn đọc muốn dành nhiều sự chú ý hơn cho hiện tại.'),
 (N'DEMO-24110227-09',N'Không gia đình',N'Hector Malot',135000,N'assets/images/khong-gia-dinh.jpg',N'Cuộc hành trình nhiều thử thách gợi lên lòng nhân ái, niềm hy vọng và ý nghĩa của gia đình. Câu chuyện dành cho những bạn đọc yêu tác phẩm giàu cảm xúc.'),
 (N'DEMO-24110227-10',N'7 thói quen hiệu quả',N'Stephen R. Covey',145000,N'assets/images/7-thoi-quen.jpg',N'Gợi mở cách tổ chức bản thân, xây dựng mục tiêu và nuôi dưỡng những thói quen có ý nghĩa. Một cuốn sách để đọc chậm và suy ngẫm.');
 INSERT INTO dbo.author(author_name) SELECT DISTINCT c.author_name FROM @catalog c WHERE NOT EXISTS(SELECT 1 FROM dbo.author a WHERE a.author_name=c.author_name);
 -- Không tạo bản trùng khi database đã có sách cùng tên.
 INSERT INTO dbo.books(isbn,title,publisher,price,description,cover_image,quantity)
 SELECT code,title,N'Dữ liệu mẫu Huy Books',price,description,cover,30 FROM @catalog c
 WHERE NOT EXISTS(SELECT 1 FROM dbo.books b WHERE b.title=c.title OR b.isbn=c.code);
 -- Chỉ điền trường thiếu cho các sách đã có; không ghi đè giá/tồn kho hiện hữu.
 UPDATE b SET cover_image=CASE WHEN NULLIF(b.cover_image,'') IS NULL THEN c.cover ELSE b.cover_image END,
 description=CASE WHEN NULLIF(b.description,'') IS NULL THEN c.description ELSE b.description END,
 price=COALESCE(b.price,c.price),quantity=COALESCE(b.quantity,30)
 FROM dbo.books b JOIN @catalog c ON b.title=c.title;
 INSERT INTO dbo.book_author(bookid,author_id)
 SELECT b.bookid,a.author_id FROM @catalog c JOIN dbo.books b ON b.title=c.title JOIN dbo.author a ON a.author_name=c.author_name
 WHERE NOT EXISTS(SELECT 1 FROM dbo.book_author x WHERE x.bookid=b.bookid AND x.author_id=a.author_id);
 COMMIT;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO
SELECT COUNT(*) AS total_books FROM dbo.books;
SELECT COUNT(*) AS total_authors FROM dbo.author;
