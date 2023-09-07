# Galaxy Monitor GpS, Kotlin
- Dành cho: các coder android
- Sử dụng để
  + Log vị trí GPS của máy android
  + Phiên bản android hỗ trợ: >=5.0
  + Gửi vị trí GPS lên 1 server, qua URL sau đó có thể xem log GPS tại server, Có thể thay URL Server của bạn vào App (hàm pingFakeServer), đang để 3 phút gửi 1 lần, có thể đặt lại tham số này 
  + Cần cấp quyền GPS cho App, và bỏ Optimize Battery của riêng App, để background chạy không bị chặn
  + Đã build với Android studio 4.2
  
- Code tham khảo rất đơn giản:
  + Chạy background service, startup boot
https://robertohuertas.com/2019/06/29/android_foreground_services/
https://github.com/mmaterowski/endless-service
  + Update GPS:
https://stackoverflow.com/a/60927918/16621499
 
27.06.23
Thêm SMS tới 2 số Phone, để alert trạng thái, Nuôi sim ...

27.12.21
- Đã test chạy tốt trên Galaxy s10e (Android 11), máy cổ điển Galaxy S6 (Android 5)
- Add: Send log GPS to remote server ok
- Chạy cả khi khởi động lại 
  + Khi khởi động lại, Galaxy S6 (Android 5) có thể chạy service ngay mà không cần mở màn hình
  + Với Galaxy S10, cần mở màn hình lên để Boot Done, từ đó mới active service background, mất 5 phút là Boot xong, sau đó tắt màn hình thì service vẫn chạy ngầm

28.12.21
Đổi icon, name thành Email Service, faked name :)
