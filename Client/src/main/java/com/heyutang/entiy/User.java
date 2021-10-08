package com.heyutang.entiy;

import lombok.Data;

/**
 * @author heBao
 */
@Data
public class User {

    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;

    }

}
