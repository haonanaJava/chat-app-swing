package com.heyutang.server;


import com.heyutang.common.TcpUtils;
import com.heyutang.dao.UserDao;
import com.heyutang.dao.UserDaoImpl;
import com.heyutang.entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.heyutang.common.Constants.*;


/**
 * @author heBao
 */
@Slf4j
public class ServerFrame extends JFrame {
    private JList<String> list;
    private JTextArea area;
    private DefaultListModel<String> lm;
    private UserDao userDao;

    private boolean loginThreadFlag = true;

    private ServerFrame() {
        userDao = new UserDaoImpl();
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
        final JMenuItem jmi1 = new JMenuItem("??????");
        // ???????????????Ctrl+'R'
        jmi1.setAccelerator(KeyStroke.getKeyStroke('R', KeyEvent.CTRL_MASK));
        jmi1.setActionCommand(SERVER_RUN);
        jm.add(jmi1);

        JMenuItem jmi2 = new JMenuItem("??????");
        // ???????????????Ctrl+'R'
        jmi2.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.CTRL_MASK));
        jmi2.setActionCommand(SERVER_STOP);
        jm.add(jmi2);

        // ??????
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
        // ?????????????????????
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

            new LoginThread(serverSocket).start();

            new ServerThread(serverSocket).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * ??????????????????????????????????????????Socket----???
     * ??????????????????????????????
     */
    private ConcurrentMap<String, Socket> usersMap = new ConcurrentHashMap<>();

    private class ServerThread extends Thread {
        private ServerSocket serverSocket;

        public ServerThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (this) {

                        Socket socketClient = serverSocket.accept();
                        System.out.println(socketClient);
                        try {
                            Scanner sc = new Scanner(socketClient.getInputStream());
                            System.out.println("?????????????????????");
                            if (sc.hasNextLine()) {
                                String firstMessage = sc.nextLine();

                                System.out.println("first message" + firstMessage);
                                String[] fMsg = firstMessage.split(DELIMITER);

                                if ("login".equals(fMsg[1])) {

                                    //???????????????????????????
                                    area.append("\r\nuser[" + fMsg[0] + "]login" + socketClient);

                                    //????????????????????????????????????????????????
                                    new ClientThread(socketClient).start();

                                    // ???????????????????????????
                                    usersMap.put(fMsg[0], socketClient);

                                    lm.addElement(fMsg[0]);

                                    // ???????????????????????????????????????????????????????????????????????????????????????
                                    TcpUtils.msgAll(fMsg[0], usersMap);

                                    // ????????????????????????????????????????????????????????????
                                    TcpUtils.msgSelf(socketClient, usersMap);

                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoginThread extends Thread {
        private ServerSocket serverSocket;

        public LoginThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (this) {
                        Socket socketClient = serverSocket.accept();
                        User user = null;
                        try {
                            while (loginThreadFlag) {
                                ObjectInputStream ois = new ObjectInputStream(socketClient.getInputStream());
                                PrintWriter pw = new PrintWriter(socketClient.getOutputStream(), true);
                                user = (User) ois.readObject();
                                User res = userDao.selectByPassword(user);
                                log.debug("???????????????????????????{}", user);
                                log.debug("?????????????????????{}", res);

                                if (res == null) {
                                    pw.println(LOGIN_FAILED);
                                    log.debug("???????????????????????????????????????");
                                    loginThreadFlag = false;
                                } else {
                                    pw.println(LOGIN_SUCCESS);
                                    log.debug("???????????????success");
                                    break;
                                }
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ???????????????????????????
     */
    private class ClientThread extends Thread {

        private Socket socketClient;

        public ClientThread(Socket socket) {
            this.socketClient = socket;
        }

        @Override
        public void run() {
            System.out.println("??????????????????????????????????????????????????????...");
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
                        //???????????????
                        area.append("\r\n??????[ " + msgs[3] + " ]?????????!" + usersMap.get(msgs[3]));

                        //???????????????????????????????????????
                        usersMap.remove(msgs[3]);

                        //?????????????????????????????????????????????
                        lm.removeElement(msgs[3]);

                        //??????????????????????????????????????????
                        TcpUtils.sendExitMsgToAll(msgs, usersMap);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
