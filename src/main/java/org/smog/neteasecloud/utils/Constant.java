package org.smog.neteasecloud.utils;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * 应用常量
 *
 * @Desc 窗口尺寸、场景路径等全局常量
 * @Author ZhongYao.Huang
 * @Time 2024-06-20 20:17
 */
public final class Constant {

    private Constant() {
        // 工具类，禁止实例化
    }

    /** 主屏幕尺寸 */
    public static final Rectangle2D PRIMARY = Screen.getPrimary().getBounds();

    /** 应用名称 */
    public static final String APP_NAME = "MyNeteaseCloud";

    /** 窗口是否可调整大小 */
    public static final boolean RESIZABLE = true;

    /** 默认窗口宽度 */
    public static final double DEFAULT_WINDOW_WIDTH = PRIMARY.getWidth() / 2 + 100;

    /** 默认窗口高度 */
    public static final double DEFAULT_WINDOW_HIGH = PRIMARY.getHeight() / 2 + 100;

    /** 404 默认场景路径 */
    public static final String DEFAULT_SCENE_PATH = "/fxml/404.fxml";

    // ==================== 场景路径 ====================

    public static final String RECOMMEND_SCENE_PATH = "/fxml/recommend.fxml";
    public static final String FEATURED_SCENE_PATH = "/fxml/featured.fxml";
    public static final String PODCAST_SCENE_PATH = "/fxml/podcast.fxml";
    public static final String PRIVATE_ROAMING_SCENE_PATH = "/fxml/privateRoaming.fxml";
    public static final String COMMUNITY_SCENE_PATH = "/fxml/community.fxml";
    public static final String I_LIKE_SCENE_PATH = "/fxml/iLike.fxml";
    public static final String MY_PODCAST_SCENE_PATH = "/fxml/myPodcast.fxml";
    public static final String MY_COLLECTION_SCENE_PATH = "/fxml/myCollection.fxml";
    public static final String DOWNLOAD_MANAGEMENT_SCENE_PATH = "/fxml/downloadManagement.fxml";
    public static final String FOLLOW_SCENE_PATH = "/fxml/follow.fxml";
    public static final String STORE_SCENE_PATH = "/fxml/store.fxml";
    public static final String MUSICIAN_SCENE_PATH = "/fxml/musicianCenter.fxml";
    public static final String CLOUD_PUSH_SCENE_PATH = "/fxml/cloudPush.fxml";
}
