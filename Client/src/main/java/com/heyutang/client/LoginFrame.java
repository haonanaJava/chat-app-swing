package com.heyutang.client;

import com.heyutang.common.FrameCreators;
import com.heyutang.entity.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

import static com.heyutang.common.Constants.*;


/**
 * @author heBao
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField = null;

    private JTextField passwordField = null;

    private JButton loginBtn = null;

    private JButton signupBtn = null;


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

    public ClientFrame handleLogin(Component parent) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String reply = null;
        // 空校验
        if ("".equals(username.trim()) && "".equals(password.trim())) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空");
            return null;
        }
        // todo 向服务端发送用户名和密码进行校验
        try {
            Socket socket = new Socket("localhost", PORT);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            oos.writeObject(new User(username, password));

            InputStream is = socket.getInputStream();
            Scanner sc = new Scanner(is);
            while (sc.hasNextLine()) {
                reply = sc.nextLine();
                if (LOGIN_SUCCESS.equals(reply) || LOGIN_FAILED.equals(reply)) {
                    break;
                }
            }
            System.out.println(reply);


        } catch (ConnectException ce) {
            ce.printStackTrace();
            JOptionPane.showMessageDialog(this, "连接服务器失败");
            return null;
        }

        if (LOGIN_SUCCESS.equals(reply)) {
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
