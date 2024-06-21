package org.smog.neteasecloud.conf;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Properties;

/**
 * @Desc 应用上下文
 * @Time 2024-06-21 10:47
 * @Author HuangZhongYao
 */
public final class AppContext {

    /**
     * 主场景
     */
    private static Scene MAIN_SCENE = null;

    /**
     * 主舞台
     */
    private static Stage PRIMARY_STAGE = null;

    /**
     * 应用配置信息
     */
    private static Properties APP_PROPERTIES = null;

    public static Scene getMainScene() {
        return MAIN_SCENE;
    }

    public static void setMainScene(Scene mainScene) {
        if (MAIN_SCENE != null){
            throw new RuntimeException("AppContext.MAIN_SCENE has already been assigned and cannot be assigned again.");
        }
        AppContext.MAIN_SCENE = mainScene;
    }

    public static Stage getPrimaryStage() {
        return PRIMARY_STAGE;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        if (PRIMARY_STAGE != null){
            throw new RuntimeException("AppContext.PRIMARY_STAGE has already been assigned and cannot be assigned again.");
        }
        AppContext.PRIMARY_STAGE = primaryStage;
    }

    public static Properties getAppProperties() {
        return APP_PROPERTIES;
    }

    public static void setAppProperties(Properties properties) {
        if (APP_PROPERTIES != null){
            throw new RuntimeException("AppContext.APP_PROPERTIES has already been assigned and cannot be assigned again.");
        }
        AppContext.APP_PROPERTIES = properties;
    }


}
