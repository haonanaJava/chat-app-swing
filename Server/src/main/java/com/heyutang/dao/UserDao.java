package com.heyutang.dao;

import com.heyutang.entity.User;

/**
 * @author heBao
 */
public interface UserDao {

    /**
     * 新增用户
     *
     * @param user 用户实体
     * @return 插入成功与否
     */
    public boolean insertUser(User user);

    /**
     * 根据密码查询用户
     * @param user 用户实体
     * @return 查询返回对象
     */
    public User selectByPassword(User user);

}
