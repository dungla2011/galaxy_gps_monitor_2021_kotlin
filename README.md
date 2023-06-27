# Galaxy Monitor GpS, Kotlin
Rảnh làm cái App chơi, mục đích theo dõi Bồ. Tình hình là có 1 em bồ, đang rất khả nghi, nên làm cái app cài vào máy để theo dõi.
 Hôm trước 20.10 đã tặng cái máy Samsung S20 làm quà rồi, ko thể cho đi linh tinh được :))
 Phần mềm online thì đầy ra nhưng ko như ý, lại bắt trả tiền thì sao ko làm cái app khi biết code ahaha

![theo dõi bồ](https://cdn-glx-7.galaxycloud.vn/tool/media/static.lib?sid=100&db68=1&type=mg&id=pp182424&media=image)

- Dành cho: các coder android

- Sử dụng để
  + Log vị trí GPS của máy android (check vị trí của Bồ, hoặc nghiêm túc hơn thì theo dõi vị trí của con cái đảm bảo an toàn...)
  + Phiên bản android hỗ trợ: >=5.0
  + Gửi vị trí GPS lên 1 server, qua URL sau đó có thể xem log GPS tại server, Có thể thay URL Server của bạn vào App (hàm pingFakeServer), đang để 3 phút gửi 1 lần, có thể đặt lại tham số này 
  + Cần cấp quyền GPS cho App, và bỏ Optimize Battery của riêng App, để background chạy không bị chặn
  + Đã build ok với Android studio 4.2
  
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
