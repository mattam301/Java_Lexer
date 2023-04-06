# DFA-transition-table-based Lexical Scanner

## Môi trường

- Java 11
- Gradle 7.4.2

## Hướng dẫn sử dụng

### Đầu vào

- Đặt file `dfa.dat` và `sample.c` trong folder [resources](app/src/main/resources)
- Cấu trúc file `dfa.dat`:
  - Tổng số lượng các trạng thái
  - Trạng thái ban đầu
  - Các trạng thái kết thúc (là các số nguyên cách nhau bằng khoảng trắng)
  - Tên các loại từ tố tương ứng với các trạng thái kết thúc
  - Các trạng thái có trạng thái tiếp theo
  - Tên các từ khóa trong VC
  - `n` trạng thái có trạng thái kế tiếp (là các số nguyên cách nhau bằng khoảng trắng)
  - `m` đầu vào chuyển trạng thái dưới dạng java regex (cách nhau bằng khoảng trắng)
  - Bảng chuyển trạng thái gồm `n` dòng tương ứng với `n` trạng thái có trạng thái kế tiếp, mỗi dòng là gồm `m` số nguyên dương đại diện cho trạng thái kế tiếp của trạng thái ứng với dòng đó với đầu vào khớp pattern tương ứng hoặc -1 ứng với đầu vào không có trạng thái chuyển tiếp

### Thực thi

Chạy lệnh sau tại thư mục gốc của project:

```bash
gradle run
```

### Đầu ra

- Đầu ra chuẩn
- File `output.vctok` trong folder `app/build/classes/java/main/com/lewwcom/lexicalscanner`
