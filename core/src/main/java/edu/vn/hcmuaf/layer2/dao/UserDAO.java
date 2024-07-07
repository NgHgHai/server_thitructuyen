package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.UserBean;
import edu.vn.hcmuaf.layer2.proto.Proto;
import org.jdbi.v3.core.Jdbi;

public class UserDAO extends PoolConnectDAO {
    public static UserBean getUserLogin(String username) {
        System.out.println("vo toi ham get user login");
        if (username == null || username.isEmpty()) {
            System.out.println("username is null");
            return null;
        }
        Jdbi jdbi = getJdbi();
        System.out.println(jdbi);
        if (jdbi == null) {
            return null;
        }
        System.out.println("select id,username,password,player");
        return jdbi.withHandle(h -> h.createQuery("select id,username,password,player_name,gender,email,phone,active,relogin_token from " + "users" + " where username = :username")
                .bind("username", username)
                .mapToBean(UserBean.class).stream().findFirst().orElse(null));
    }

    public static void removeToken(String username) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return;
        }
        jdbi.withHandle(h -> h.createUpdate("update users set relogin_token = null where username = :username")
                .bind("username", username)
                .execute());
    }

    public static boolean checkToken(String token, int userId) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return false;
        }
        int count = jdbi.withHandle(h -> h.createQuery("select * from users where relogin_token = :token and id = :userId")
                .bind("token", token)
                .bind("id", userId)
                .mapTo(Integer.class)
                .findOnly());
        return count > 0;
    }


    public static void updateReloginToken(int id, String reloginToken) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return;
        }
        jdbi.withHandle(h -> h.createUpdate("update users set relogin_token   = :reloginToken where id = :id")
                .bind("reloginToken", reloginToken)
                .bind("id", id)
                .execute());
    }

    public static int checkUserRegister(String username) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return 500; // Lỗi máy chủ cơ sở dữ liệu
        }

        int userCount = jdbi.withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM users WHERE username = :username")
                        .bind("username", username)
                        .mapTo(Integer.class)
                        .findOnly());

        if (userCount > 0) {
            return 400; // nguoi dung da ton tai trong csdl
        } else {
            return 200; // nguoi dung da ton tai trong csdl
        }
    }

    public static boolean checkEmailVerify(String email, String code) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return false;
        }

        int userCount = jdbi.withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM users WHERE email = :email and email_code = :code")
                        .bind("email", email)
                        .bind("code", code)
                        .mapTo(Integer.class)
                        .findOnly());

        if (userCount > 0) {
            return true;
        } else {
            return false;
        }
    }


    public static int insertRegisterUser(String username, String email, String pass, String randomSixDigits) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return 500;
        }

        int result = jdbi.withHandle(handle ->
                handle.createUpdate("INSERT INTO users (username, email, password,email_code) VALUES (:username, :email, :password,:randomSixDigits)")
                        .bind("username", username)
                        .bind("email", email)
                        .bind("password", pass)
                        .bind("randomSixDigits", randomSixDigits)
                        .execute());

        if (result == 1) {
            return 200; // dang ki nguoi dung thanh cong
        } else {
            return 500; // loi may chu csdl
        }
    }

    public static void updateUserInfo(Proto.User user) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return;
        }
        jdbi.withHandle(h -> h.createUpdate("update users set player_name = :playerName , gender = :gender where id = :id")
                .bind("playerName", user.getPlayerName())
                .bind("gender", user.getGender()));
    }

    public static void doVerifyEmail(String email) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return;
        }
        jdbi.withHandle(h -> h.createUpdate("update users set active = 1 ,email_code_time = " + System.currentTimeMillis() + " , is_email_verified = 1,email_code = '' where email = :email")
                .bind("email", email)
                .execute());
    }

    public static void changePassword(String username, String pass) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return;
        }
        jdbi.withHandle(h -> h.createUpdate("update users set password = :password where username = :username")
                .bind("password", pass)
                .bind("username", username)
                .execute());
    }

    public static void insertOTP(String email, String randomSixDigits) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return;
        }
        jdbi.withHandle(h -> h.createUpdate("update users set email_code = :randomSixDigits where email = :email")
                .bind("randomSixDigits", randomSixDigits)
                .bind("email", email)
                .execute());
    }

    public static int checkEmailRegister(String email) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return 500; // Lỗi máy chủ cơ sở dữ liệu
        }

        int userCount = jdbi.withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM users WHERE email = :email")
                        .bind("email", email)
                        .mapTo(Integer.class)
                        .findOnly());

        if (userCount > 0) {
            return 400; // nguoi dung da ton tai trong csdl
        } else {
            return 200; // nguoi dung chua ton tai trong csdl
        }
    }

    public static int checkOTP(String email, String otp) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return 500; // Lỗi máy chủ cơ sở dữ liệu
        }

        int userCount = jdbi.withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM users WHERE email = :email and email_code = :otp")
                        .bind("email", email)
                        .bind("otp", otp)
                        .mapTo(Integer.class)
                        .findOnly());

        if (userCount > 0) {
            return 200; // nguoi dung da ton tai trong csdl
        } else {
            return 400; // nguoi dung chua ton tai trong csdl
        }
    }

    public static UserBean getUserById(int hostId) {
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return null;
        }
        return jdbi.withHandle(h -> h.createQuery("select * from users where id = :id")
                .bind("id", hostId)
                .mapToBean(UserBean.class)
                .findFirst().orElse(null));
    }
}
