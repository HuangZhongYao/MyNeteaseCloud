package org.smog.neteasecloud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.smog.neteasecloud.utils.Constant;

/**
 * 应用启动入口
 *
 * @Desc Created by IntelliJ IDEA.
 * @Time 2023-02-22 4:15
 * @Author 花开富贵
 */
@Slf4j
public class NeteasecCloudStartApplication extends Application {

    /**
     * 启动入口
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("默认窗口尺寸：{}，{}",Constant.DEFAULT_WINDOW_HIGH,Constant.DEFAULT_WINDOW_WIDTH);
        // 加载主页
        Parent root = FXMLLoader.load(this.getClass().getResource("/fxml/home.fxml"));
        // 设置主场景、窗口宽高
        Scene homeScene = new Scene(root, Constant.DEFAULT_WINDOW_WIDTH,Constant.DEFAULT_WINDOW_HIGH);
        primaryStage.setScene(homeScene);
        // 设置窗口是否可变
        primaryStage.setResizable(Constant.RESIZABLE);
        // 设置窗口标题
        primaryStage.setTitle(Constant.APP_NAME);
        // 设置任务栏图标
        primaryStage.getIcons().addAll(new Image("/image/icon/icon.png"));
        // 显示窗口
        primaryStage.show();

    }
}
