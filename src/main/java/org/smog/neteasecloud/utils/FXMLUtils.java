package org.smog.neteasecloud.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;

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
        try {
            return FXMLLoader.load(FXMLUtils.class.getResource(path));
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
