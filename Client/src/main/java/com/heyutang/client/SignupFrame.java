package com.heyutang.client;

import com.heyutang.common.FrameCreators;
import com.heyutang.entiy.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.heyutang.common.Constants.*;

/**
 * @author heBao
 */
public class SignupFrame extends JFrame {

    private JTextField usernameField = null;

    private JTextField passwordField = null;

    private JButton signupBtn = null;

    private static SignupFrame instance = null;


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
            // 空校验
            if ("".equals(username.trim()) && "".equals(password.trim())) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空");
                return;
            }
            //todo 将对象发送给后端 将数据保存到数据库
            try (
                    Socket socket = new Socket("localhost", 8080);
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                pw.print(new User(username, password));
                String reply = bufferedReader.readLine();
                if ( SIGNUP_SUCCESS.equals(reply) ) {
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
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "连接服务器失败");
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
