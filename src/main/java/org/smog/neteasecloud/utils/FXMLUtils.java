package org.smog.neteasecloud.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;
import org.smog.neteasecloud.conf.AppContext;

import java.io.IOException;

/**
 * @Desc fxml 工具类
 * @Time 2024-06-21 10:50
 * @Author HuangZhongYao
 */
@Slf4j
public final class FXMLUtils {

    /**
     * 获取fxml场景
     * @param path fxml文件路径
     * @return Parent 场景
     */
    public static Parent loadScene(String path){

        // 获取缓存
        Parent parent = AppContext.CACHE_SCENE.get(path);

        try {

            // 有缓存则使用缓存
            if (null != parent){
                return parent;
            }

            // 加载fxml
            parent = FXMLLoader.load(FXMLUtils.class.getResource(path));
            // 放入缓存
            AppContext.CACHE_SCENE.put(path,parent);

            return parent;
        } catch (IOException e) {
            log.error("加载场景错误. {}",e.getMessage());
        }

        // 返回404场景
        try {
            return FXMLLoader.load(FXMLUtils.class.getResource(Constant.DEFAULT_SCENE_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
