package org.smog.neteasecloud.utils;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public interface Constant {

    Rectangle2D PRIMARY = Screen.getPrimary().getBounds();
    String APP_NAME = "MyNeteaseCloud";

    boolean RESIZABLE = true;

    double DEFAULT_WINDOW_WIDTH = PRIMARY.getWidth() / 2 + 100;

    double DEFAULT_WINDOW_HIGH = PRIMARY.getHeight() / 2 + 100;

    String DEFAULT_SCENE_PATH = "/fxml/404.fxml";

    String RECOMMEND_SCENE_PATH = "/fxml/recommend.fxml";

    String FEATURED_SCENE_PATH = "/fxml/featured.fxml";

    String PODCAST_SCENE_PATH = "/fxml/podcast.fxml";

    String PRIVATE_ROAMING_SCENE_PATH = "/fxml/privateRoaming.fxml";

    String COMMUNITY_SCENE_PATH = "/fxml/community.fxml";

    String I_LIKE_SCENE_PATH = "/fxml/iLike.fxml";

    String MY_PODCAST_SCENE_PATH = "/fxml/myPodcast.fxml";

    String MY_COLLECTION_SCENE_PATH = "/fxml/myCollection.fxml";

    String DOWNLOAD_MANAGEMENT_SCENE_PATH = "/fxml/downloadManagement.fxml";

    String FOLLOW_SCENE_PATH = "/fxml/follow.fxml";

    String STORE_SCENE_PATH = "/fxml/store.fxml";

    String MUSICIAN_SCENE_PATH = "/fxml/musicianCenter.fxml";

    String CLOUD_PUSH_SCENE_PATH = "/fxml/cloudPush.fxml";
}