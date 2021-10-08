package com.heyutang.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

import static com.heyutang.common.Constants.ALL;
import static com.heyutang.common.Constants.DELIMITER;

/**
 * @author heBao
 */
public class TcpUtils {

    /**
     * 把“当前用户登录的消息即用户名”通知给所有其他已经在线的人
     * 思路:从池中依次把每个socket(代表每个在线用户)取出，向它发送userName
     *
     * @param userName 通知的用户名
     */
    public static void msgAll(String userName, Map<String, Socket> usersMap) {
        Iterator<Socket> it = usersMap.values().iterator();
        while (it.hasNext()) {
            Socket s = it.next();
            try {
                // 加true为自动刷新
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                // 通知客户端显示消息
                String msg = "message@#@#server@#@#用户[ " + userName + " ]已登录!";
                pw.println(msg);
                pw.flush();
                // 通知客户端在在线列表添加用户在线。
                msg = "cmdAdd@#@#server@#@#" + userName;
                pw.println(msg);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 服务器把客户端的聊天消息转发给相应的其他客户端
     *
     * @param msgs
     */
    public static void sendMsgToSb(String[] msgs, Map<String, Socket> usersMap) {
        try {
            if (ALL.equals(msgs[1])) {
                Iterator<String> userNames = usersMap.keySet().iterator();
                //遍历每一个在线用户，把聊天消息发给他
                while (userNames.hasNext()) {
                    String userName = userNames.next();
                    Socket s = usersMap.get(userName);
                    PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                    String str = "msg" + DELIMITER + msgs[3] + DELIMITER + msgs[2];
                    pw.println(str);
                    pw.flush();
                }
            } else {
                Socket s = usersMap.get(msgs[1]);
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                String str = "msg@#@#" + msgs[3] + "对你@#@#" + msgs[2];
                pw.println(str);
                pw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知其他用户。该用户已经退出
     *
     * @param msgs
     * @throws IOException
     */
    public static void sendExitMsgToAll(String[] msgs, Map<String, Socket> usersMap) throws IOException {
        Iterator<String> userNames = usersMap.keySet().iterator();

        while (userNames.hasNext()) {
            String userName = userNames.next();
            Socket s = usersMap.get(userName);
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            String str = "msg@#@#server@#@#用户[ " + msgs[3] + " ]已退出！";
            pw.println(str);
            pw.flush();

            str = "cmdRed@#@#server@#@#" + msgs[3];
            pw.println(str);
            pw.flush();
        }

    }

    /**
     * 通知当前登录的用户，有关其他在线人的信息
     * 把原先已经在线的那些用户的名字发给该登录用户，让他给自己界面中的lm添加相应的用户名
     *
     * @param socketClient
     */
    public static void msgSelf(Socket socketClient, Map<String, Socket> usersMap) {
        try {
            PrintWriter pw = new PrintWriter(socketClient.getOutputStream(), true);
            Iterator<String> it = usersMap.keySet().iterator();
            while (it.hasNext()) {
                String msg = "cmdAdd@#@#server@#@#" + it.next();
                pw.println(msg);
                pw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
