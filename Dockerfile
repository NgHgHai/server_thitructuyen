# Sử dụng image cơ sở là Apache Tomcat 10 trên OpenJDK 11
FROM tomcat:10-jdk11-openjdk-slim

# Thiết lập thư mục làm việc
WORKDIR /usr/local/tomcat
#CMD ["gradle", "build", "--no-daemon"]

# Sao chép tệp WAR của module Socket vào thư mục webapps của Tomcat
COPY socket/build/libs/socket.war webapps/ROOT.war

# Expose cổng mà Tomcat sẽ chạy
EXPOSE 8080

# Command để chạy Tomcat
CMD ["catalina.sh", "run"]

