package com.heyutang.common;

import javax.swing.*;
import java.awt.*;

/**
 * @author heBao
 */
public class FrameCreators {

    /**
     * 登录 注册 界面的初始化
     * @param frame 框架
     */
    public static void init(JFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(Constants.WIDTH, Constants.HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        Container contentPane = frame.getContentPane();

        FlowLayout flowLayout = new FlowLayout();

        flowLayout.setVgap(100);
        flowLayout.setHgap(25);
        contentPane.setLayout(flowLayout);
    }


}
