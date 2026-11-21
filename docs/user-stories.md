# User story – Career Connect

> Task: **T4E795F** · Gồm 20 user story cho 3 vai trò, mỗi story có tiêu chí chấp nhận. Wireframe các màn hình xem tại [wireframes.md](wireframes.md).

## Vai trò

| Vai trò | Mã story | Ứng với `users.role` |
| --- | --- | --- |
| Ứng viên | UV | `CANDIDATE` |
| Nhà tuyển dụng | NTD | `EMPLOYER` |
| Quản trị viên (gồm người duyệt hồ sơ và người quản lý tài khoản) | QT | `MENTOR`, `ADMIN` |

Mức ưu tiên: **Cao** = bắt buộc cho bản demo đầu tiên, **TB** = nên có, **Thấp** = làm nếu còn thời gian.

## Tổng hợp

| Mã | Vai trò | Tóm tắt | Màn hình | Ưu tiên |
| --- | --- | --- | --- | --- |
| US01 | Ứng viên | Đăng ký tài khoản | Đăng ký | Cao |
| US02 | Mọi vai trò | Đăng nhập | Đăng nhập | Cao |
| US03 | Ứng viên | Cập nhật thông tin cơ bản của hồ sơ | Hồ sơ của tôi | Cao |
| US04 | Ứng viên | Quản lý học vấn | Hồ sơ của tôi | Cao |
| US05 | Ứng viên | Khai báo kỹ năng kèm mức 1–5 | Hồ sơ của tôi | Cao |
| US06 | Ứng viên | Thêm dự án và chứng chỉ | Hồ sơ của tôi | TB |
| US07 | Ứng viên | Gửi hồ sơ đi duyệt, xem kết quả | Hồ sơ của tôi | Cao |
| US08 | Ứng viên | Bật/tắt công khai hồ sơ, xem lượt xem | Hồ sơ của tôi | Thấp |
| US09 | Ứng viên | Phản hồi lời mời phỏng vấn | Yêu cầu phỏng vấn | Cao |
| US10 | Nhà tuyển dụng | Đăng ký kèm thông tin công ty | Đăng ký | Cao |
| US11 | Nhà tuyển dụng | Tìm ứng viên theo tiêu chí | Tìm ứng viên | Cao |
| US12 | Nhà tuyển dụng | Xem chi tiết ứng viên | Chi tiết ứng viên | Cao |
| US13 | Nhà tuyển dụng | Lưu ứng viên vào shortlist | Chi tiết ứng viên | TB |
| US14 | Nhà tuyển dụng | Gửi yêu cầu phỏng vấn | Chi tiết ứng viên | Cao |
| US15 | Nhà tuyển dụng | Theo dõi các yêu cầu đã gửi | Yêu cầu phỏng vấn | TB |
| US16 | Quản trị viên | Xem hàng chờ duyệt | Hàng chờ duyệt | Cao |
| US17 | Quản trị viên | Duyệt hồ sơ kèm nhận xét | Hàng chờ duyệt | Cao |
| US18 | Quản trị viên | Xem lịch sử duyệt của hồ sơ | Hàng chờ duyệt | TB |
| US19 | Quản trị viên | Tìm, lọc tài khoản | Quản trị tài khoản | TB |
| US20 | Quản trị viên | Khoá/mở khoá tài khoản | Quản trị tài khoản | Cao |

---

## Ứng viên

### US01 – Đăng ký tài khoản ứng viên
**Là** ứng viên, **tôi muốn** tự đăng ký tài khoản bằng email, **để** tạo hồ sơ năng lực của mình.

Tiêu chí chấp nhận:
- [ ] Form gồm họ tên, email, mật khẩu, nhập lại mật khẩu, chọn vai trò "Ứng viên".
- [ ] Email phải đúng định dạng và chưa được dùng; nếu trùng, báo "Email đã được sử dụng".
- [ ] Mật khẩu tối thiểu 8 ký tự; nhập lại phải khớp.
- [ ] Mật khẩu được lưu dạng mã hoá BCrypt, không lưu bản gốc.
- [ ] Đăng ký thành công thì tự đăng nhập và chuyển tới "Hồ sơ của tôi", đồng thời tạo sẵn một hồ sơ trạng thái Nháp.

### US02 – Đăng nhập
**Là** người dùng (ứng viên, nhà tuyển dụng hoặc quản trị viên), **tôi muốn** đăng nhập bằng email và mật khẩu, **để** sử dụng các chức năng dành cho vai trò của mình.

Tiêu chí chấp nhận:
- [ ] Sai email hoặc mật khẩu thì báo chung "Email hoặc mật khẩu không đúng" (không tiết lộ email có tồn tại hay không).
- [ ] Tài khoản bị khoá không đăng nhập được và hiện lý do khoá.
- [ ] Đăng nhập đúng thì chuyển trang theo vai trò: ứng viên → Hồ sơ của tôi, nhà tuyển dụng → Tìm ứng viên, quản trị viên → Hàng chờ duyệt.
- [ ] Có nút đăng xuất ở thanh menu trên mọi trang sau khi đăng nhập.

### US03 – Cập nhật thông tin cơ bản của hồ sơ
**Là** ứng viên, **tôi muốn** điền tiêu đề, giới thiệu, vị trí mong muốn, số năm kinh nghiệm, nơi ở và ảnh đại diện, **để** nhà tuyển dụng hiểu nhanh tôi là ai.

Tiêu chí chấp nhận:
- [ ] Tiêu đề và vị trí mong muốn là bắt buộc; giới thiệu tối đa 2000 ký tự.
- [ ] Ảnh đại diện chỉ nhận JPG/PNG, tối đa 2 MB.
- [ ] Lưu thành công thì hiện thông báo "Đã lưu"; lỗi nhập liệu hiện ngay dưới ô tương ứng.

### US04 – Quản lý học vấn
**Là** ứng viên, **tôi muốn** thêm, sửa, xoá các mục học vấn, **để** thể hiện quá trình học tập.

Tiêu chí chấp nhận:
- [ ] Mỗi mục gồm trường, bằng cấp, chuyên ngành, thời gian bắt đầu/kết thúc, GPA (không bắt buộc).
- [ ] Ngày kết thúc không được trước ngày bắt đầu; để trống nghĩa là "đang học".
- [ ] Danh sách hiển thị theo thời gian mới nhất trước.
- [ ] Xoá có hộp xác nhận.

### US05 – Khai báo kỹ năng
**Là** ứng viên, **tôi muốn** chọn kỹ năng từ danh mục và tự đánh giá mức 1–5, **để** nhà tuyển dụng tìm thấy tôi theo kỹ năng.

Tiêu chí chấp nhận:
- [ ] Kỹ năng chọn từ danh mục có sẵn (ô tìm kiếm gợi ý), không gõ tự do.
- [ ] Mức thành thạo bắt buộc, chỉ nhận 1 đến 5, hiển thị dạng 5 ô.
- [ ] Không thêm trùng một kỹ năng hai lần.
- [ ] Có thể đổi mức hoặc gỡ kỹ năng.

### US06 – Thêm dự án và chứng chỉ
**Là** ứng viên, **tôi muốn** liệt kê dự án đã làm và chứng chỉ đã đạt, **để** chứng minh năng lực thực tế.

Tiêu chí chấp nhận:
- [ ] Dự án gồm tên, vai trò, mô tả, công nghệ, đường dẫn, thời gian.
- [ ] Chứng chỉ gồm tên, đơn vị cấp, ngày cấp, ngày hết hạn (không bắt buộc), đường dẫn xác thực.
- [ ] Đường dẫn phải bắt đầu bằng `http://` hoặc `https://`.
- [ ] Chứng chỉ đã hết hạn hiển thị nhãn "Hết hạn".

### US07 – Gửi hồ sơ đi duyệt
**Là** ứng viên, **tôi muốn** gửi hồ sơ cho quản trị viên duyệt và xem kết quả, **để** hồ sơ được hiển thị với nhà tuyển dụng.

Tiêu chí chấp nhận:
- [ ] Chỉ gửi được khi đã có thông tin cơ bản, ít nhất 1 học vấn và 3 kỹ năng; nếu thiếu, liệt kê phần còn thiếu.
- [ ] Sau khi gửi, trạng thái chuyển "Chờ duyệt" và không gửi lại được cho đến khi có kết quả.
- [ ] Khi có kết quả, ứng viên nhận thông báo và thấy nhận xét của người duyệt.
- [ ] Nếu bị "Yêu cầu sửa", ứng viên sửa rồi gửi lại được.

### US08 – Công khai hồ sơ và xem lượt xem
**Là** ứng viên, **tôi muốn** bật/tắt công khai hồ sơ và biết có bao nhiêu lượt xem, **để** kiểm soát ai thấy hồ sơ và biết mức độ quan tâm.

Tiêu chí chấp nhận:
- [ ] Chỉ hồ sơ đã được duyệt mới bật công khai được.
- [ ] Hồ sơ tắt công khai không xuất hiện trong kết quả tìm kiếm của nhà tuyển dụng.
- [ ] Hiển thị số lượt xem trong 7 và 30 ngày gần nhất.

### US09 – Phản hồi lời mời phỏng vấn
**Là** ứng viên, **tôi muốn** xem và chấp nhận hoặc từ chối lời mời phỏng vấn, **để** sắp xếp lịch với nhà tuyển dụng.

Tiêu chí chấp nhận:
- [ ] Danh sách lời mời hiển thị công ty, vị trí, thời gian đề xuất, hình thức (online/offline), trạng thái.
- [ ] Lời mời đang chờ có nút "Chấp nhận" và "Từ chối"; từ chối có thể ghi lý do.
- [ ] Sau khi phản hồi, nhà tuyển dụng nhận thông báo và trạng thái không đổi lại được.
- [ ] Có thông báo khi có lời mời mới.

## Nhà tuyển dụng

### US10 – Đăng ký tài khoản nhà tuyển dụng
**Là** nhà tuyển dụng, **tôi muốn** đăng ký tài khoản kèm thông tin công ty, **để** bắt đầu tìm ứng viên.

Tiêu chí chấp nhận:
- [ ] Ngoài thông tin như US01, form có tên công ty, mã số thuế, website, lĩnh vực, quy mô.
- [ ] Nếu mã số thuế đã có trong hệ thống, tài khoản được gắn vào công ty đó thay vì tạo công ty mới.
- [ ] Đăng ký thành công thì chuyển tới màn hình Tìm ứng viên.

### US11 – Tìm ứng viên
**Là** nhà tuyển dụng, **tôi muốn** lọc ứng viên theo kỹ năng, mức tối thiểu, vị trí, số năm kinh nghiệm và địa điểm, **để** nhanh chóng tìm người phù hợp.

Tiêu chí chấp nhận:
- [ ] Chỉ hiện hồ sơ đã được duyệt và đang công khai.
- [ ] Lọc được nhiều kỹ năng cùng lúc, mỗi kỹ năng kèm mức tối thiểu.
- [ ] Kết quả hiển thị dạng thẻ: ảnh, tên, tiêu đề, 3 kỹ năng nổi bật, kinh nghiệm, địa điểm.
- [ ] Phân trang 10 kết quả/trang; không có kết quả thì hiện "Không tìm thấy ứng viên phù hợp".

### US12 – Xem chi tiết ứng viên
**Là** nhà tuyển dụng, **tôi muốn** xem đầy đủ hồ sơ của một ứng viên, **để** đánh giá trước khi liên hệ.

Tiêu chí chấp nhận:
- [ ] Hiển thị thông tin cơ bản, học vấn, kỹ năng kèm mức, dự án, chứng chỉ.
- [ ] Số điện thoại và email chỉ hiện sau khi ứng viên chấp nhận phỏng vấn.
- [ ] Mỗi lần mở trang được ghi nhận một lượt xem.

### US13 – Lưu shortlist
**Là** nhà tuyển dụng, **tôi muốn** lưu ứng viên vào danh sách rút gọn kèm ghi chú, **để** so sánh và quay lại sau.

Tiêu chí chấp nhận:
- [ ] Nút "Lưu" trên trang chi tiết và trên thẻ kết quả; đã lưu thì đổi thành "Bỏ lưu".
- [ ] Shortlist dùng chung cho mọi nhà tuyển dụng cùng công ty.
- [ ] Không lưu trùng một ứng viên hai lần trong cùng công ty.

### US14 – Gửi yêu cầu phỏng vấn
**Là** nhà tuyển dụng, **tôi muốn** gửi lời mời phỏng vấn cho ứng viên, **để** hẹn lịch trao đổi.

Tiêu chí chấp nhận:
- [ ] Form gồm vị trí, lời nhắn, thời gian đề xuất, hình thức (online/offline), địa điểm hoặc link họp.
- [ ] Thời gian đề xuất phải ở tương lai.
- [ ] Không gửi được lời mời mới khi đã có một lời mời đang chờ với cùng ứng viên.
- [ ] Gửi xong, ứng viên nhận thông báo; lời mời có trạng thái "Đang chờ".

### US15 – Theo dõi yêu cầu phỏng vấn
**Là** nhà tuyển dụng, **tôi muốn** xem tất cả lời mời đã gửi và trạng thái của chúng, **để** biết ứng viên nào đã đồng ý.

Tiêu chí chấp nhận:
- [ ] Lọc theo trạng thái: Đang chờ, Chấp nhận, Từ chối, Đã huỷ.
- [ ] Lời mời đang chờ có thể huỷ.
- [ ] Nhận thông báo khi ứng viên phản hồi.

## Quản trị viên

### US16 – Xem hàng chờ duyệt
**Là** quản trị viên, **tôi muốn** xem danh sách hồ sơ đang chờ duyệt, **để** xử lý lần lượt.

Tiêu chí chấp nhận:
- [ ] Hiển thị tên ứng viên, tiêu đề hồ sơ, ngày gửi, số lần đã gửi.
- [ ] Sắp xếp hồ sơ gửi sớm nhất lên đầu.
- [ ] Hiện tổng số hồ sơ đang chờ ở menu.

### US17 – Duyệt hồ sơ
**Là** quản trị viên, **tôi muốn** đánh giá hồ sơ là Đạt, Yêu cầu sửa hoặc Từ chối kèm nhận xét, **để** chỉ hồ sơ chất lượng mới đến tay nhà tuyển dụng.

Tiêu chí chấp nhận:
- [ ] Xem được toàn bộ nội dung hồ sơ ngay trên màn hình duyệt.
- [ ] "Yêu cầu sửa" và "Từ chối" bắt buộc nhập nhận xét.
- [ ] Mỗi lần duyệt lưu thành một bản ghi lịch sử mới, không ghi đè lần trước.
- [ ] Trạng thái hồ sơ cập nhật theo kết quả và ứng viên nhận thông báo.

### US18 – Xem lịch sử duyệt
**Là** quản trị viên, **tôi muốn** xem các lần duyệt trước của một hồ sơ, **để** đánh giá nhất quán và biết ứng viên đã sửa những gì.

Tiêu chí chấp nhận:
- [ ] Hiển thị theo thời gian: ngày, người duyệt, kết quả, nhận xét.
- [ ] Hiển thị ngay trên màn hình duyệt hồ sơ.

### US19 – Tìm và lọc tài khoản
**Là** quản trị viên, **tôi muốn** tìm tài khoản theo email hoặc tên và lọc theo vai trò, trạng thái, **để** quản lý người dùng.

Tiêu chí chấp nhận:
- [ ] Bảng gồm họ tên, email, vai trò, công ty (nếu có), trạng thái, ngày tạo.
- [ ] Lọc theo vai trò và trạng thái (Hoạt động / Bị khoá); phân trang 20 dòng/trang.

### US20 – Khoá và mở khoá tài khoản
**Là** quản trị viên, **tôi muốn** khoá tài khoản vi phạm kèm lý do và mở khoá khi cần, **để** bảo vệ hệ thống.

Tiêu chí chấp nhận:
- [ ] Khoá bắt buộc nhập lý do; có hộp xác nhận.
- [ ] Tài khoản bị khoá bị đăng xuất ở lần truy cập kế tiếp và không đăng nhập lại được.
- [ ] Hồ sơ của ứng viên bị khoá không xuất hiện trong tìm kiếm.
- [ ] Quản trị viên không thể tự khoá chính mình.