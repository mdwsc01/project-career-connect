# Career Connect
Web hồ sơ năng lực và kết nối tuyển dụng (Spring Boot 3, Java 17, Thymeleaf, MySQL).

## Cách chạy
1. Cài Java 17 và MySQL 8.
2. Đặt biến môi trường theo file `.env.example` (DB_USERNAME, DB_PASSWORD).
3. Chạy `mvnw.cmd spring-boot:run` (Windows) hoặc `./mvnw spring-boot:run`.
4. Mở http://localhost:8080

## Quy ước làm việc
- Nhánh: `feature/<mã-task>-<tên-ngắn>`, ví dụ `feature/t4e795e-khoi-tao-du-an`
- Commit: `<loại>: <MÃ-TASK> mô tả`, loại gồm feat, fix, docs, refactor, test, chore
    - Ví dụ: `feat: T4E795E khởi tạo dự án`
- Không push thẳng vào `main`; mọi thay đổi qua Pull Request
- PR cần ít nhất 1 người review (Approve) trước khi merge
- Thay đổi DB: tạo file migration mới `V<số>__mo_ta.sql`, không sửa file cũ