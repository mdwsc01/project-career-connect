# Career Connect
Web hồ sơ năng lực và kết nối tuyển dụng (Spring Boot 4, Java 17, Thymeleaf, MySQL 8, Flyway).

## Cách chạy
1. Cài Java 17 và MySQL 8, bật MySQL (XAMPP: bấm Start ở dòng MySQL).
2. Đặt biến môi trường theo file `.env.example` (DB_USERNAME, DB_PASSWORD). Nếu MySQL dùng `root` không mật khẩu thì bỏ qua bước này.
3. Chạy `mvnw.cmd spring-boot:run` (Windows) hoặc `./mvnw spring-boot:run`.
   Lần chạy đầu, Flyway tự tạo database `career_connect_db`, các bảng và dữ liệu mẫu.
4. Mở http://localhost:8080

## Quy ước làm việc
- Nhánh: `feature/<mã-task>-<tên-ngắn>`, ví dụ `feature/t4e795e-khoi-tao-du-an`
- Commit: `<loại>: <MÃ-TASK> mô tả`, loại gồm feat, fix, docs, refactor, test, chore
  - Ví dụ: `feat: T4E795E khoi tao du an` (nên viết không dấu)
- Không push thẳng vào `main`; mọi thay đổi qua Pull Request
- PR cần ít nhất 1 người review (Approve) trước khi merge
- Thay đổi DB: tạo file migration mới `V<số>__mo_ta.sql`, không sửa file đã merge

## Tài liệu
- Thiết kế CSDL và ERD: [docs/database-design.md](docs/database-design.md)
- User story: [docs/user-stories.md](docs/user-stories.md)
- Wireframe: [docs/wireframes.md](docs/wireframes.md)

## Tài khoản mẫu
Dữ liệu mẫu được Flyway tự tạo khi chạy ứng dụng lần đầu trên database trống (file `V2__seed_data.sql`). Tất cả là dữ liệu giả.
Mật khẩu chung cho mọi tài khoản: `Demo@1234`

| Vai trò | Email | Ghi chú |
| --- | --- | --- |
| Quản trị viên | admin@careerconnect.example.com | Duyệt hồ sơ, quản lý tài khoản |
| Nhà tuyển dụng | doitac1@example.com | Công ty TNHH Phần mềm Bình Minh (demo) |
| Nhà tuyển dụng | doitac2@example.com | Công ty CP Dữ liệu Sông Xanh (demo) |
| Nhà tuyển dụng | doitac3@example.com | Công ty TNHH Giải pháp Hải Đăng (demo) |
| Sinh viên | sv01@example.com ... sv15@example.com | sv01–sv11: đã duyệt, công khai · sv12–sv14: chờ duyệt · sv15: nháp |

> Các tài khoản này dùng được khi chức năng đăng nhập (đọc tài khoản từ bảng `users`) được hoàn thành.

Muốn tạo lại dữ liệu từ đầu: xoá database `career_connect_db` rồi chạy lại ứng dụng.