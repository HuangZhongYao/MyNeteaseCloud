package org.smog.neteasecloud.conf;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 应用上下文（全局单例状态）
 *
 * @Desc 持有主舞台、主场景、配置信息及场景缓存
 * @Time 2024-06-21 10:47
 * @Author HuangZhongYao
 */
public final class AppContext {

    private AppContext() {
        // 工具类，禁止实例化
    }

    /** 主场景 */
    private static Scene mainScene;

    /** 主舞台 */
    private static Stage primaryStage;

    /** 应用配置信息 */
    private static Properties appProperties;

    /** 场景缓存（FXML 路径 → 已加载的 Parent） */
    private static final Map<String, Parent> CACHE_SCENE = new HashMap<>();

    // ==================== 主场景 ====================

    public static Scene getMainScene() {
        return mainScene;
    }

    public static void setMainScene(Scene mainScene) {
        if (AppContext.mainScene != null) {
            throw new IllegalStateException("AppContext.mainScene 已被赋值，不可重复设置");
        }
        AppContext.mainScene = mainScene;
    }

    // ==================== 主舞台 ====================

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        if (AppContext.primaryStage != null) {
            throw new IllegalStateException("AppContext.primaryStage 已被赋值，不可重复设置");
        }
        AppContext.primaryStage = primaryStage;
    }

    // ==================== 应用配置 ====================

    public static Properties getAppProperties() {
        return appProperties;
    }

    public static void setAppProperties(Properties properties) {
        if (AppContext.appProperties != null) {
            throw new IllegalStateException("AppContext.appProperties 已被赋值，不可重复设置");
        }
        AppContext.appProperties = properties;
    }

    // ==================== 场景缓存 ====================

    /**
     * 获取缓存的场景
     *
     * @param path FXML 文件路径
     * @return 缓存的 Parent，不存在返回 null
     */
    public static Parent getCachedScene(String path) {
        return CACHE_SCENE.get(path);
    }

    /**
     * 缓存场景
     *
     * @param path   FXML 文件路径
     * @param parent 加载后的场景节点
     */
    public static void putCachedScene(String path, Parent parent) {
        CACHE_SCENE.put(path, parent);
    }

    /**
     * 获取场景缓存的只读视图
     */
    public static Map<String, Parent> getSceneCacheView() {
        return Collections.unmodifiableMap(CACHE_SCENE);
    }

    /**
     * 清空场景缓存
     */
    public static void clearSceneCache() {
        CACHE_SCENE.clear();
    }
}
