package com.heyutang.dao;

import com.heyutang.Entiry.User;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author heBao
 */
public class UserDaoImpl implements UserDao {

    DataSource ds = new ComboPooledDataSource();

    @Override
    public boolean insertUser(User user) {
        try {
            Connection connection = ds.getConnection();

            PreparedStatement ps = connection.prepareStatement("insert into chat_user(username, password) values(?,?)");
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            return !ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User selectByPassword(User user) {
        try {
            Connection connection = ds.getConnection();
            PreparedStatement ps = connection.prepareStatement("select * from chat_user where username=? and password = ?;");
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ResultSet resultSet = ps.executeQuery();
            ArrayList<User> users = new ArrayList<>();
            while (resultSet.next()) {
                User res = new User();
                res.setUsername(resultSet.getString("username"));
                res.setPassword(resultSet.getString("password"));
                users.add(res);
            }
            return users.size() == 1 ? users.get(0) : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
