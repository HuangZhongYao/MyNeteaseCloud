package org.smog.neteasecloud.contrll;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 为我推荐页控制器
 * @Desc
 * @Time 2024-06-21 10:31
 * @Author HuangZhongYao
 */
public class RecommendController {

    private static final Logger log = LoggerFactory.getLogger(RecommendController.class);
    /**
     * 轮播图
     */
    @FXML
    public ImageView carousel;

    /**
     * 轮播图片列表
     */
    private static Image[] carouselUrls = new Image[]{
            new Image("/image/carousel/1.jpg"),
            new Image("/image/carousel/2.jpg"),
            new Image("/image/carousel/3.jpg"),
            new Image("/image/carousel/4.jpg"),
            new Image("/image/carousel/5.jpg"),
            new Image("/image/carousel/6.jpg"),
            new Image("/image/carousel/7.jpg"),
            new Image("/image/carousel/8.jpg"),
    };

    /**
     * 初始化函数 将在FXML被加载时执行
     */
    public void initialize() {
        // 开启轮播
        this.startBanner();
    }

    /**
     * 轮播图片
     */
    public void startBanner() {

        log.debug("为我推荐页面 startBanner..");

        // 轮播图片下标
        final AtomicInteger index = new AtomicInteger(0);

        // 设置初始图片
        carousel.setImage(carouselUrls[index.get()]);

        // 每三秒切换图片
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {

            log.debug("为我推荐页面轮播切换图片.");

            // 获取下标
            int indexTemp = index.incrementAndGet();

            // 如果下标为最后一个重置为0
            indexTemp = indexTemp == carouselUrls.length ? 0 : indexTemp;

            // 设置下标
            index.set(indexTemp);

            // 设置图片
            carousel.setImage(carouselUrls[index.get()]);
        }));

        // 设置无限循环
        timeline.setCycleCount(Timeline.INDEFINITE);

        // 启动
        timeline.play();
    }
}
