package com.heyutang.server;

import com.heyutang.common.TcpUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import static com.heyutang.common.Constants.*;

/**
 * @author heBao
 */
public class ServerFrame extends JFrame {
    private JList<String> list;
    private JTextArea area;
    private DefaultListModel<String> lm;

    private ServerFrame() {
        JPanel p = new JPanel(new BorderLayout());

        // right side user online list
        lm = new DefaultListModel<String>();
        list = new JList<String>(lm);
        JScrollPane js = new JScrollPane(list);
        TitledBorder titledBorder = new TitledBorder("online");
        js.setBorder(titledBorder);
        Dimension dimension = new Dimension(100, p.getHeight());
        js.setPreferredSize(dimension);
        p.add(js, BorderLayout.EAST);

        area = new JTextArea();
        // set to can't edit and modify
        area.setEditable(false);
        area.setEnabled(false);
        area.setBackground(Color.GRAY);
        //set font style and size
        area.setFont(new Font("Fira code", Font.BOLD, 20));
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        this.getContentPane().add(p);

        //top menu
        JMenuBar jb = new JMenuBar();
        this.setJMenuBar(jb);
        JMenu jm = new JMenu("control");
        jm.setMnemonic('C');
        jb.add(jm);
        final JMenuItem jmi1 = new JMenuItem("开启");
        // 设置快捷键Ctrl+'R'
        jmi1.setAccelerator(KeyStroke.getKeyStroke('R', KeyEvent.CTRL_MASK));
        jmi1.setActionCommand(SERVER_RUN);
        jm.add(jmi1);

        JMenuItem jmi2 = new JMenuItem("停止");
        // 设置快捷键Ctrl+'R'
        jmi2.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.CTRL_MASK));
        jmi2.setActionCommand(SERVER_STOP);
        jm.add(jmi2);

        // 监听
        jmi1.addActionListener(e -> {
            if (SERVER_RUN.equals(e.getActionCommand())) {
                startServer();
                jmi1.setEnabled(false);
            } else {
                System.exit(0);
            }
        });

        jmi2.addActionListener(e -> {

        });

        Toolkit tk = Toolkit.getDefaultToolkit();
        int width = (int) tk.getScreenSize().getWidth();
        int height = (int) tk.getScreenSize().getHeight();
        this.setBounds(width / 4, height / 4, width / 2, height / 2);
        // 关闭按钮器作用
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        setVisible(true);


    }

    public static ServerFrame getInstance() {
        return new ServerFrame();
    }

    private static final Integer PORT = 9090;

    private void startServer() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);

            area.append("server starting at:" + PORT);

            new ServerThread(serverSocket).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 用来保存所有在线用户的名字和Socket----池
     * 同时用来实现端口转发
     */
    private Map<String, Socket> usersMap = new HashMap<String, Socket>();

    private class ServerThread extends Thread {
        private ServerSocket serverSocket;

        public ServerThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Socket socketClient = serverSocket.accept();
                    Scanner sc = new Scanner(socketClient.getInputStream());
                    if (sc.hasNext()) {
                        String username = sc.nextLine();
                        area.append("\r\nuser[" + username + "]login" + socketClient);

                        //将用户名添加到右侧
                        lm.addElement(username);

                        //创建一个客户端线程，专为客户服务
                        new ClientThread(socketClient).start();

                        // 将用户放到用户池中
                        usersMap.put(username, socketClient);

                        // 把“当前用户登录的消息即用户名”通知给所有其他已经在线的人
                        TcpUtils.msgAll(username, usersMap);

                        // 通知当前登录的用户，有关其他在线人的信息
                        TcpUtils.msgSelf(socketClient, usersMap);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientThread extends Thread {

        private Socket socketClient;

        public ClientThread(Socket socket) {
            this.socketClient = socket;
        }

        @Override
        public void run() {
            System.out.println("一个与客户端通讯的线程启动并开始通讯...");
            try {
                Scanner sc = new Scanner(socketClient.getInputStream());
                while (sc.hasNext()) {
                    String msg = sc.nextLine();
                    System.out.println(msg);
                    String[] msgs = msg.split(DELIMITER);
                    if (msgs.length != 4) {
                        System.out.println("error");
                        continue;
                    }
                    if ("on".equals(msgs[0])) {
                        TcpUtils.sendMsgToSb(msgs, usersMap);
                    }
                    if ("exit".equals(msgs[0])) {
                        //服务器显示
                        area.append("\r\n用户[ " + msgs[3] + " ]已退出!" + usersMap.get(msgs[3]));

                        //从在线用户池中把该用户删除
                        usersMap.remove(msgs[3]);

                        //服务器的在线列表中把该用户删除
                        lm.removeElement(msgs[3]);

                        //通知其他用户，该用户已经退出
                        TcpUtils.sendExitMsgToAll(msgs, usersMap);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
