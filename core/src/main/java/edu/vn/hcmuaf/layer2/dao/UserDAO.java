package edu.vn.hcmuaf.layer2.dao;

import edu.vn.hcmuaf.layer2.dao.bean.UserBean;
import edu.vn.hcmuaf.layer2.proto.Proto;
import org.jdbi.v3.core.Jdbi;

public class UserDAO extends PoolConnectDAO {
    public static UserBean getUserLogin(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        Jdbi jdbi = getJdbi();
        if (jdbi == null) {
            return null;
        }
        return jdbi.withHandle(h -> h.createQuery("select id,username,password,player_name,gender,sponsor,email,phone,active,tree,relogin_token,agency_level,isBot from " + "users" + " where username = :username")
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
}
