# Galaxy Monitor GpS, Kotlin
Rảnh làm cái App chơi

- Sử dụng để
  + Log vị trí GPS của máy android (check vị trí của Bồ vì có gì đó khả nghi, hoặc theo dõi vị trí của con cái đảm bảo an toàn...)
  + Mục đích riêng của tôi là check Bồ thôi, có lúc bồ nói ở chỗ này nhưng mình biết đang ở chỗ khác mà chưa dò ra được, nghi ngờ ko biết đang làm gì chưa bắt được quả tang :))

  + Phiên bản android hỗ trợ: >=5.0, đã test ok với Galaxy S10e
  + Gửi lên 1 server URL để xem log tại server
  + Cần cấp quyền GPS cho App, và bỏ Optimize Battery của riêng App, để background chạy không bị chặn
  + Có thể thay URL Server của bạn vào App (hàm pingFakeServer), vì hiện tại là gửi lên server của tôi, 3 phút gửi 1 lần, có thể đặt lại tham số này
  + Đã build ok với Android studio 4.2
  
- Code tham khảo rất đơn giản:
  + Chạy background service, startup boot
https://robertohuertas.com/2019/06/29/android_foreground_services/
https://github.com/mmaterowski/endless-service
  + Update GPS:
https://stackoverflow.com/a/60927918/16621499

27.12.21
- Tested ok on Galaxy s10e
- Add: Send log GPS to remote server ok
- Chạy cả khi khởi động lại 
(Khi khởi động lại, cần mở màn hình lên để Boot done, từ đó mới active service background, galaxy s10e mất 5 phút là Boot xong, sau đó tắt màn hình thì service vẫn chạy ngầm)

