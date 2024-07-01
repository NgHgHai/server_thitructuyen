file init.sql la file  dùng để khởi tạo database khi lần đầu tiên khởi tạo docker,
sau đó dữ liệu sẽ được mount ra nơi khác vì vậy khi muốn khởi động lại database bằng file init
thì phải xóa volumes của db  trong docker để quá trình import file init.sql được chạy lại.

p/s: file init là file khởi tạc cả  databse nên cần phải có lệnh tạo database trước khi tạo bảng
vd: creat database if not exists db_name;
    ues db_name;