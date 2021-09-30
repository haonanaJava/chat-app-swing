package com.heyutang.client;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

import static com.heyutang.common.Constants.*;

/**
 * @author heBao
 */
public class ClientFrame extends JFrame implements ActionListener {

    private JTextField tfdUserName;
    private JList<String> list;
    private DefaultListModel<String> lm;
    private JTextArea allMsg;
    private JTextField tfdMsg;
    private JButton btnCon;
    private JButton btnExit;
    private JButton btnSend;

    /**
     * 主机ip地址
     */
    private static String HOST = "127.0.0.1";

    /**
     * 服务器的端口号
     */
    private static int PORT = 9090;

    private Socket clientSocket;
    private PrintWriter pw;

    public ClientFrame(String username) {
        super("chatting app");
        // 菜单条
        addJMenu();

        // 上面的面板
        JPanel p = new JPanel();
        JLabel jlb1 = new JLabel("用户名:");
        tfdUserName = new JTextField(10);
        tfdUserName.setEnabled(false);
        tfdUserName.setText(username);

        // 链接按钮
        URL linkIcon = ClassLoader.getSystemResource(EXIT_ICON);
        assert linkIcon != null;
        Icon icon = new ImageIcon(linkIcon);
        btnCon = new JButton("", icon);
        btnCon.setActionCommand(CONNECT_EVENT);
        btnCon.addActionListener(this);

        // 退出按钮
        URL exitIcon = ClassLoader.getSystemResource(LINK_ICON);
        assert exitIcon != null;
        icon = new ImageIcon(exitIcon);
        btnExit = new JButton("", icon);
        btnExit.setActionCommand(EXIT_EVENT);

        btnExit.addActionListener(this);
        btnExit.setEnabled(false);
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(jlb1);
        p.add(tfdUserName);
        p.add(btnCon);
        p.add(btnExit);
        getContentPane().add(p, BorderLayout.NORTH);

        // 中间的面板
        JPanel cenP = new JPanel(new BorderLayout());
        this.getContentPane().add(cenP, BorderLayout.CENTER);

        // 在线列表
        lm = new DefaultListModel<String>();
        list = new JList<String>(lm);
        lm.addElement("全部好友");

        // 设置默认显示
        list.setSelectedIndex(0);

        // 只能选中一行
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(2);
        JScrollPane js = new JScrollPane(list);
        Border border = new TitledBorder("在线");
        js.setBorder(border);
        Dimension preferredSize = new Dimension(100, cenP.getHeight());
        js.setPreferredSize(preferredSize);
        cenP.add(js, BorderLayout.EAST);

        // 聊天消息框
        allMsg = new JTextArea();
        allMsg.setFont(new Font("Fira code", Font.BOLD, 15));
        allMsg.setEditable(false);
        cenP.add(new JScrollPane(allMsg), BorderLayout.CENTER);

        // 消息发送面板
        JPanel p3 = new JPanel();
        JLabel jlb2 = new JLabel("消息:");
        p3.add(jlb2);
        tfdMsg = new JTextField(20);
        tfdMsg.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMsg();
                }
            }
        });
        p3.add(tfdMsg);
        btnSend = new JButton("发送");
        btnSend.setEnabled(false);
        btnSend.setActionCommand(SEND_EVENT);
        btnSend.addActionListener(this);
        p3.add(btnSend);
        this.getContentPane().add(p3, BorderLayout.SOUTH);

        // *************************************************
        // 右上角的X-关闭按钮-添加事件处理
        addWindowListener(new WindowAdapter() {
            //关闭窗口时，释放资源, 并向服务端发送离线消息
            @Override
            public void windowClosing(WindowEvent e) {
                if (pw == null) {
                    System.exit(0);
                }
                String msg = "exit" + DELIMITER + ALL + DELIMITER + "null" + DELIMITER + tfdUserName.getText();
                //关闭客户端时通知服务端
                pw.println(msg);
                pw.flush();
                System.exit(0);
            }
        });
        setBounds(new Rectangle(800, 600));
        //再小会出现组件隐藏
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * 上面的导航栏
     */
    private void addJMenu() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu menu = new JMenu("选项");
        menuBar.add(menu);

        JMenuItem menuItemSet = new JMenuItem("设置");
        JMenuItem menuItemHelp = new JMenuItem("帮助");
        menu.add(menuItemSet);
        menu.add(menuItemHelp);

        menuItemSet.addActionListener(e -> {
            final JDialog dlg = new JDialog(ClientFrame.this);// 弹出一个界面
            // 不能直接用this

            dlg.setBounds(ClientFrame.this.getX() + 20, ClientFrame.this.getY() + 30,
                    350, 150);
            dlg.setLayout(new FlowLayout());
            dlg.add(new JLabel("服务器IP和端口:"));

            final JTextField tfdHost = new JTextField(10);
            tfdHost.setText(ClientFrame.HOST);
            dlg.add(tfdHost);

            dlg.add(new JLabel(":"));

            final JTextField tfdPort = new JTextField(5);
            tfdPort.setText("" + ClientFrame.PORT);
            dlg.add(tfdPort);

            JButton btnSet = new JButton("设置");
            dlg.add(btnSet);
            btnSet.addActionListener(e12 -> {
                String ip = tfdHost.getText();//解析并判断ip是否合法
                String strs[] = ip.split("\\.");
                if (strs == null || strs.length != 4) {
                    JOptionPane.showMessageDialog(ClientFrame.this, IP_ERROR);
                    return;
                }
                try {
                    for (int i = 0; i < 4; i++) {
                        int num = Integer.parseInt(strs[i]);
                        if (num > 255 || num < 0) {
                            JOptionPane.showMessageDialog(ClientFrame.this, IP_ERROR);
                            return;
                        }
                    }
                } catch (NumberFormatException e2) {
                    JOptionPane.showMessageDialog(ClientFrame.this, IP_ERROR);
                    return;
                }

                ClientFrame.HOST = tfdHost.getText();//先解析并判断ip是否合法

                try {
                    int port = Integer.parseInt(tfdPort.getText());
                    if (port < 0 || port > 65535) {
                        JOptionPane.showMessageDialog(ClientFrame.this, PORT_ERROR);
                        return;
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(ClientFrame.this, PORT_ERROR);
                    return;
                }
                ClientFrame.PORT = Integer.parseInt(tfdPort.getText());
                dlg.dispose();//关闭这个界面
            });
            dlg.setVisible(true);//显示出来
        });

        menuItemHelp.addActionListener(e -> {
            JDialog dlg = new JDialog(ClientFrame.this);

            dlg.setBounds(ClientFrame.this.getX() + 30, ClientFrame.this.getY() + 30, 400, 100);
            dlg.setLayout(new FlowLayout());
            dlg.add(new JLabel("我的github:https://github.com/haonanaJava"));
            dlg.setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (CONNECT_EVENT.equals(e.getActionCommand())) {
            //过滤无效用户名
            if (tfdUserName.getText() == null
                    || tfdUserName.getText().trim().length() == 0) {
                JOptionPane.showMessageDialog(this, USERNAME_ERROR);
                return;
            }

            connecting();// 连接服务器的动作
            if (pw == null) {
                JOptionPane.showMessageDialog(this, SERVER_ERROR);
                return;
            }

            ((JButton) (e.getSource())).setEnabled(false);
            btnExit.setEnabled(true);
            btnSend.setEnabled(true);
            tfdUserName.setEditable(false);
        } else if (SEND_EVENT.equals(e.getActionCommand())) {
            sendMsg();
        } else if (EXIT_EVENT.equals(e.getActionCommand())) {
            //先把自己在线的菜单清空
            lm.removeAllElements();

            sendExitMsg();
            btnCon.setEnabled(true);
            btnExit.setEnabled(false);
            tfdUserName.setEditable(true);
        }

    }

    /**
     * 向服务器发送退出消息
     */
    private void sendExitMsg() {
        String msg = "exit" + DELIMITER + ALL + DELIMITER + "null" + DELIMITER + tfdUserName.getText();
        System.out.println("退出:" + msg);
        pw.println(msg);
        pw.flush();
    }

    /**
     * 处理消息发送
     */
    private void sendMsg() {
        if (tfdMsg.getText() == null
                || tfdMsg.getText().trim().length() == 0) {
            return;
        }
        String selectedUser = list.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this,
                    "请选择要发送消息的用户",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        String msg = "on" + DELIMITER + selectedUser + DELIMITER
                + tfdMsg.getText() + DELIMITER + tfdUserName.getText();
        pw.println(msg);
        pw.flush();

        // 将发送消息的文本设为空
        tfdMsg.setText("");
    }

    /**
     * 客户端连接服务端核心方法
     */
    private void connecting() {
        try {
            // 先根据用户名防范
            String userName = tfdUserName.getText();
            if (userName == null || userName.trim().length() == 0) {
                JOptionPane.showMessageDialog(this, "连接服务器失败!\r\n用户名有误，请重新输入！");
                return;
            }

            // 跟服务器握手
            clientSocket = new Socket(HOST, PORT);
            // 加上自动刷新
            pw = new PrintWriter(clientSocket.getOutputStream(), true);
            // 向服务器报上自己的用户名
            pw.println(userName);
            this.setTitle("用户[ " + userName + " ]上线...");

            // 线程 接受服务器发来的消息---一直开着的
            new ClientThread().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ClientThread extends Thread {

        @Override
        public void run() {
            try {
                Scanner sc = new Scanner(clientSocket.getInputStream());
                while (sc.hasNextLine()) {
                    String str = sc.nextLine();
                    String[] msgs = str.split(DELIMITER);
                    System.out.println(tfdUserName.getText() + ": " + str);
                    if ("msg".equals(msgs[0])) {
                        if ("server".equals(msgs[1])) {
                            // 服务器发送的官方消息
                            str = "[ 通知 ]:" + msgs[2];
                        } else {
                            // 服务器转发的聊天消息
                            str = "[ " + msgs[1] + " ]说: " + msgs[2];
                        }
                        allMsg.append("\r\n" + str);
                    }
                    if ("cmdAdd".equals(msgs[0])) {
                        boolean eq = false;
                        for (int i = 0; i < lm.getSize(); i++) {
                            System.out.println();
                            if (lm.getElementAt(i).equals(msgs[2])) {
                                eq = true;
                            }
                        }
                        if (!eq && !msgs[2].equals(tfdUserName.getText())) {
                            // 用户上线--添加
                            lm.addElement(msgs[2]);
                        }
                    }
                    if ("cmdRed".equals(msgs[0])) {
                        // 用户离线了--移除
                        lm.removeElement(msgs[2]);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
