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
 * 推荐页控制器
 *
 * @Desc 管理轮播图展示
 * @Time 2024-06-21 10:31
 * @Author HuangZhongYao
 */
public class RecommendController {

    private static final Logger log = LoggerFactory.getLogger(RecommendController.class);

    /** 轮播图片路径（延迟加载，首次访问时才加载 Image 对象） */
    private static final String[] CAROUSEL_IMAGE_PATHS = {
            "/image/carousel/1.jpg",
            "/image/carousel/2.jpg",
            "/image/carousel/3.jpg",
            "/image/carousel/4.jpg",
            "/image/carousel/5.jpg",
            "/image/carousel/6.jpg",
            "/image/carousel/7.jpg",
            "/image/carousel/8.jpg",
    };

    /** 延迟加载的图片缓存 */
    private static Image[] carouselImages;

    /** 轮播图组件 */
    @FXML
    public ImageView carousel;

    /**
     * 初始化（FXML 加载时自动调用）
     */
    public void initialize() {
        startBanner();
    }

    /**
     * 启动轮播
     */
    public void startBanner() {
        log.debug("推荐页 startBanner..");

        // 延迟加载图片
        loadImagesIfNeeded();

        final AtomicInteger index = new AtomicInteger(0);
        carousel.setImage(carouselImages[0]);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            log.debug("推荐页轮播切换图片");

            int next = index.incrementAndGet();
            if (next >= carouselImages.length) {
                next = 0;
            }
            index.set(next);
            carousel.setImage(carouselImages[index.get()]);
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * 延迟加载轮播图片（首次调用时才加载，减少应用启动时间）
     */
    private static void loadImagesIfNeeded() {
        if (carouselImages != null) {
            return;
        }
        synchronized (RecommendController.class) {
            if (carouselImages != null) {
                return;
            }
            carouselImages = new Image[CAROUSEL_IMAGE_PATHS.length];
            for (int i = 0; i < CAROUSEL_IMAGE_PATHS.length; i++) {
                carouselImages[i] = new Image(CAROUSEL_IMAGE_PATHS[i]);
            }
            log.debug("轮播图片加载完成，共 {} 张", carouselImages.length);
        }
    }
}
