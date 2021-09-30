package com.heyutang;

import com.heyutang.Entiry.User;
import com.heyutang.dao.UserDaoImpl;
import org.junit.Test;

public class Dao {

    @Test
    public void testInsert(){
        UserDaoImpl userDao = new UserDaoImpl();
        boolean res = userDao.insertUser(new User("hyt", "123456"));
        System.out.println(res);
    }

    @Test
    public void testSelect(){
        UserDaoImpl userDao = new UserDaoImpl();
        User user = userDao.selectByPassword(new User("hyt", "123456"));
        System.out.println(user);
    }
}
