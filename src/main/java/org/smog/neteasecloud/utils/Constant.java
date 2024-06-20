package org.smog.neteasecloud.utils;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * 软件常用常量
 * @Desc Created by IntelliJ IDEA.
 * @Time 2023-02-22 4:21
 * @Author 花开富贵
 */
public  interface  Constant {

    /**
     *
     */
    Rectangle2D PRIMARY = Screen.getPrimary().getBounds();

    /**
     * 应用程序名字
     */
    String APP_NAME= "网易云音乐";

    /**
     * 窗口拖拽拉伸
     */
    boolean RESIZABLE = true;

    /**
     * 默认程序窗口宽度
     */
    double DEFAULT_WINDOW_WIDTH = PRIMARY.getWidth()/2+100;

    /**
     * 默认程序窗口高度
     */
    double DEFAULT_WINDOW_HIGH = PRIMARY.getHeight()/2+100;

}
