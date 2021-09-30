package com.heyutang.client;

import com.heyutang.Entiry.User;
import com.heyutang.common.FrameCreators;
import com.heyutang.dao.UserDaoImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.heyutang.common.Constants.SIGNUP_FAILED;
import static com.heyutang.common.Constants.SIGNUP_SUCCESS;

/**
 * @author heBao
 */
public class SignupFrame extends JFrame {

    private JTextField usernameField = null;

    private JTextField passwordField = null;

    private JButton signupBtn = null;

    private static SignupFrame instance = null;

    UserDaoImpl userDao = new UserDaoImpl();

    private SignupFrame() {
        super("注册");
        FrameCreators.init(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        initGraphic(contentPane);

        /**
         * 注册事件
         */
        signupBtn.addActionListener(e -> {
            String username = this.usernameField.getText();
            String password = this.passwordField.getText();
            //将数据保存到数据库
            boolean b = userDao.insertUser(new User(username, password));
            if (b) {
                JOptionPane.showMessageDialog(contentPane,
                        SIGNUP_SUCCESS,
                        "消息",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(contentPane,
                        SIGNUP_FAILED,
                        "消息",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        /**
         * 关闭窗口后将单例对象值为null
         */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                instance = null;
            }
        });

        setVisible(true);
    }

    public static SignupFrame getInstance() {
        if (instance == null) {
            instance = new SignupFrame();
        }
        return instance;
    }

    /**
     * 注册界面组件初始化
     *
     * @param container 容器组件
     */
    public void initGraphic(Container container) {
        this.usernameField = new JTextField();
        usernameField.setColumns(15);
        container.add(new JLabel("用户名:"));
        container.add(usernameField);

        this.passwordField = new JTextField();
        passwordField.setColumns(15);
        container.add(new JLabel("密码:"));
        container.add(passwordField);


        this.signupBtn = new JButton("注册");
        container.add(signupBtn);
    }

}
