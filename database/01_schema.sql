-- Huy Books · Thái Nhựt Huy · MSSV 24110227
-- SQL Server: chạy bằng SSMS. Không xóa dữ liệu hiện có.
IF DB_ID(N'webst9') IS NULL CREATE DATABASE webst9;
GO
USE webst9;
GO
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRANSACTION;
 IF OBJECT_ID(N'dbo.users',N'U') IS NULL
  CREATE TABLE dbo.users (id int IDENTITY PRIMARY KEY,email varchar(255) NOT NULL UNIQUE,fullname nvarchar(100),phone varchar(20),passwd varchar(255),admin bit NOT NULL DEFAULT 0);
 IF OBJECT_ID(N'dbo.author',N'U') IS NULL
  CREATE TABLE dbo.author (author_id int IDENTITY PRIMARY KEY,author_name nvarchar(200),date_of_birth date);
 IF OBJECT_ID(N'dbo.books',N'U') IS NULL
  CREATE TABLE dbo.books (bookid int IDENTITY PRIMARY KEY,isbn nvarchar(255),title nvarchar(200),publisher nvarchar(255),price decimal(19,2),description nvarchar(2000),publish_date date,cover_image nvarchar(255),quantity int);
 IF OBJECT_ID(N'dbo.book_author',N'U') IS NULL
  CREATE TABLE dbo.book_author (bookid int NOT NULL REFERENCES dbo.books(bookid),author_id int NOT NULL REFERENCES dbo.author(author_id),CONSTRAINT PK_book_author PRIMARY KEY(bookid,author_id));
 IF OBJECT_ID(N'dbo.rating',N'U') IS NULL
  CREATE TABLE dbo.rating (id int IDENTITY PRIMARY KEY,rating int,review_text nvarchar(1000),userid int REFERENCES dbo.users(id),bookid int REFERENCES dbo.books(bookid));
 IF OBJECT_ID(N'dbo.purchase_orders',N'U') IS NULL
  CREATE TABLE dbo.purchase_orders (
   id bigint IDENTITY PRIMARY KEY,userId int NOT NULL,recipient nvarchar(100) NOT NULL,phone varchar(20) NOT NULL,address nvarchar(500) NOT NULL,
   paymentMethod varchar(10) NOT NULL DEFAULT 'COD',status varchar(30) NOT NULL DEFAULT 'PENDING',paymentStatus varchar(30) NOT NULL DEFAULT 'UNPAID',
   createdAt datetime2 NOT NULL DEFAULT SYSDATETIME(),total decimal(19,2) NOT NULL);
 IF OBJECT_ID(N'dbo.purchase_order_items',N'U') IS NULL
  CREATE TABLE dbo.purchase_order_items (id bigint IDENTITY PRIMARY KEY,order_id bigint NOT NULL REFERENCES dbo.purchase_orders(id),bookId int NOT NULL,title nvarchar(200) NOT NULL,unitPrice decimal(19,2) NOT NULL,quantity int NOT NULL);
 -- Bổ sung cột cho database đã có phần đặt hàng.
 IF COL_LENGTH('dbo.purchase_orders','paymentStatus') IS NULL
  ALTER TABLE dbo.purchase_orders ADD paymentStatus varchar(30) NOT NULL CONSTRAINT DF_orders_paymentStatus DEFAULT 'UNPAID';
 IF COL_LENGTH('dbo.users','passwd') IS NOT NULL ALTER TABLE dbo.users ALTER COLUMN passwd varchar(255);
 -- Các trường tiếng Việt phải lưu Unicode.
 ALTER TABLE dbo.users ALTER COLUMN fullname nvarchar(100);
 ALTER TABLE dbo.author ALTER COLUMN author_name nvarchar(200);
 ALTER TABLE dbo.rating ALTER COLUMN review_text nvarchar(1000);
 IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_huy_orders_user_created' AND object_id=OBJECT_ID('dbo.purchase_orders'))
  CREATE INDEX IX_huy_orders_user_created ON dbo.purchase_orders(userId,createdAt DESC);
 IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_huy_orders_status' AND object_id=OBJECT_ID('dbo.purchase_orders'))
  CREATE INDEX IX_huy_orders_status ON dbo.purchase_orders(status);
 IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_huy_items_order' AND object_id=OBJECT_ID('dbo.purchase_order_items'))
  CREATE INDEX IX_huy_items_order ON dbo.purchase_order_items(order_id);
 COMMIT;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO
