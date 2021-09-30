package com.heyutang.client;

import com.heyutang.Entiry.User;
import com.heyutang.common.FrameCreators;
import com.heyutang.dao.UserDao;
import com.heyutang.dao.UserDaoImpl;

import javax.swing.*;
import java.awt.*;


/**
 * @author heBao
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField = null;

    private JTextField passwordField = null;

    private JButton loginBtn = null;

    private JButton signupBtn = null;

    private UserDao userDao = new UserDaoImpl();

    public LoginFrame() {
        super("登录");

        FrameCreators.init(this);

        Container contentPane = getContentPane();

        initGraphic(contentPane);

        /**
         * 登录按钮的事件监听
         */
        loginBtn.addActionListener((event) -> {

        });

        /**
         * 注册按钮的事件监听
         */
        signupBtn.addActionListener(event -> {
            //singleton
            SignupFrame.getInstance();
        });

        setVisible(true);
    }

    public ClientFrame handleLogin(Component parent) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        User user = userDao.selectByPassword(new User(username, password));
        //登录成功
        if (user != null) {
            dispose();
            return new ClientFrame(username);
        } else {
            JOptionPane.showMessageDialog(parent,
                    "用户名或密码错误",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

    }

    /**
     * 登录界面组件初始化
     *
     * @param container 容器组件
     */
    private void initGraphic(Container container) {
        JLabel usernameLabel = new JLabel("用户名:");
        this.usernameField = new JTextField();
        usernameField.setColumns(15);
        container.add(usernameLabel);
        container.add(usernameField);

        JLabel passwordLabel = new JLabel("密码:");
        this.passwordField = new JTextField();
        passwordField.setColumns(15);
        container.add(passwordLabel);
        container.add(passwordField);


        this.loginBtn = new JButton("登录");
        this.signupBtn = new JButton("注册");

        container.add(loginBtn);
        container.add(signupBtn);


    }

}
