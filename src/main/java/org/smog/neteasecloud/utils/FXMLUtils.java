package org.smog.neteasecloud.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;
import org.smog.neteasecloud.conf.AppContext;

import java.io.IOException;

/**
 * FXML 场景加载工具类
 *
 * @Desc 带缓存的场景加载，失败时降级到 404 页面
 * @Time 2024-06-21 10:50
 * @Author HuangZhongYao
 */
@Slf4j
public final class FXMLUtils {

    private FXMLUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 加载 FXML 场景（优先使用缓存）
     *
     * @param path FXML 文件路径
     * @return 加载后的场景节点
     */
    public static Parent loadScene(String path) {
        // 优先从缓存获取
        Parent parent = AppContext.getCachedScene(path);
        if (parent != null) {
            return parent;
        }

        try {
            // 加载 FXML 并放入缓存
            parent = FXMLLoader.load(FXMLUtils.class.getResource(path));
            AppContext.putCachedScene(path, parent);
            return parent;
        } catch (IOException e) {
            log.error("加载场景失败 [{}]: {}", path, e.getMessage());
        }

        // 降级：返回 404 页面
        try {
            return FXMLLoader.load(FXMLUtils.class.getResource(Constant.DEFAULT_SCENE_PATH));
        } catch (IOException e) {
            throw new RuntimeException("无法加载默认 404 页面", e);
        }
    }
}
