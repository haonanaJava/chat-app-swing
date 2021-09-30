package com.heyutang;

import com.heyutang.client.ClientFrame;
import com.heyutang.client.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author heBao
 */
public class ClientApplication {

    public static void main(String[] args) {

        LoginFrame loginFrame = new LoginFrame();

        /**
         * 客户端聊天界面对象
         */
        AtomicReference<ClientFrame> clientFrame = new AtomicReference<>();

        Container loginFrameContentPane = loginFrame.getContentPane();
        JButton loginBtn = (JButton) loginFrameContentPane.getComponent(4);
        loginBtn.addActionListener(e -> {
            clientFrame.set(loginFrame.handleLogin(loginFrameContentPane));
        });


    }


}
