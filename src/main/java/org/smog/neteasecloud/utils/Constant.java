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

    /**
     * 404场景路径
     */
    String DEFAULT_SCENE_PATH = "/fxml/404.fxml";

    /**
     * 为我推荐场景页面
     */
    String RECOMMEND_SCENE_PATH = "/fxml/recommend.fxml";

    /**
     * 精选场景页面路径
     */
    String FEATURED_SCENE_PATH = "/fxml/featured.fxml";

    /**
     * 播客场景页面路径
     */
    String PODCAST_SCENE_PATH = "/fxml/podcast.fxml";

    /**
     * 私人漫游场景页面路径
     */
    String PRIVATE_ROAMING_SCENE_PATH = "/fxml/privateRoaming.fxml";

    /**
     * 社区场景页面路径
     */
    String COMMUNITY_SCENE_PATH = "/fxml/community.fxml";

    /**
     * 我喜欢的场景页面路径
     */
    String I_LIKE_SCENE_PATH = "/fxml/iLike.fxml";

    /**
     * 我的播客场景页面路径
     */
    String MY_PODCAST_SCENE_PATH = "/fxml/myPodcast.fxml";

    /**
     * 我的收藏场景页面路径
     */
    String MY_COLLECTION_SCENE_PATH = "/fxml/myCollection.fxml";

    /**
     * 下载管理场景页面路径
     */
    String DOWNLOAD_MANAGEMENT_SCENE_PATH = "/fxml/downloadManagement.fxml";
}
