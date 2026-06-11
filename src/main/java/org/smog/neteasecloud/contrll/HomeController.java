package org.smog.neteasecloud.contrll;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.smog.neteasecloud.service.LocalFileScanner;
import org.smog.neteasecloud.service.PlayerService;
import org.smog.neteasecloud.utils.Constant;
import org.smog.neteasecloud.utils.FXMLUtils;
import org.smog.neteasecloud.utils.Style;

import java.util.Arrays;
import java.util.List;

/**
 * 主页面控制器
 *
 * @Desc 负责页面导航（带动画过渡）、搜索过滤、播放列表抽屉
 * @Time 2024-06-21 10:31
 * @Author HuangZhongYao
 */
@Slf4j
public class HomeController {

    // ==================== 动画时长常量 ====================

    /** 内容区切换动画时长 */
    private static final Duration CONTENT_TRANSITION_DURATION = Duration.millis(280);

    /** 抽屉动画时长 */
    private static final Duration DRAWER_DURATION = Duration.millis(220);

    /** 菜单 hover 缩放比例 */
    private static final double MENU_HOVER_SCALE = 1.03;

    // ==================== FXML 注入：布局 ====================

    @FXML
    private BorderPane root;
    @FXML
    private TextField search;
    @FXML
    private VBox leftMenu;

    // ==================== FXML 注入：顶部导航 ====================

    @FXML
    private Hyperlink discoverMenu;
    @FXML
    private Hyperlink followMenu;
    @FXML
    private Hyperlink storeMenu;
    @FXML
    private Hyperlink musicianMenu;
    @FXML
    private Hyperlink cloudPushMenu;

    // ==================== FXML 注入：左侧菜单 ====================

    @FXML
    private Hyperlink featuredMenu;
    @FXML
    private Hyperlink recommendMenu;

    // ==================== FXML 注入：播放控制按钮 ====================

    @FXML
    private Button previousButton;
    @FXML
    private Button playPauseButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button playModeButton;
    @FXML
    private Button playlistToggleButton;

    // ==================== FXML 注入：标签 ====================

    @FXML
    private Label currentSongNameLabel;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private Label playlistInfoLabel;

    // ==================== FXML 注入：滑块 ====================

    @FXML
    private Slider progressSlider;
    @FXML
    private Slider volumeSlider;

    // ==================== FXML 注入：播放列表 ====================

    @FXML
    private ListView<String> songListView;
    @FXML
    private VBox playlistDrawer;

    // ==================== 服务层 ====================

    private final LocalFileScanner fileScanner = new LocalFileScanner();
    private final PlayerService playerService = new PlayerService();

    // ==================== UI 状态 ====================

    private Hyperlink currentTopMenu;
    private Hyperlink currentLeftMenu;
    private boolean playlistDrawerOpen;
    private boolean pageTransitioning;

    // ==================== 初始化 ====================

    /**
     * FXML 加载后自动调用
     */
    public void initialize() {
        // 默认选中左侧菜单
        applyLeftMenuActiveStyle(recommendMenu);
        navigateToPage(Constant.RECOMMEND_SCENE_PATH);
        currentLeftMenu = recommendMenu;

        // 默认选中顶部导航
        currentTopMenu = discoverMenu;
        if (discoverMenu != null) {
            applyTopNavActiveStyle(discoverMenu);
        }

        // 绑定播放器 UI 控件
        playerService.bindUi(
                currentSongNameLabel, currentTimeLabel, totalTimeLabel, playlistInfoLabel,
                progressSlider, volumeSlider,
                playPauseButton, playModeButton,
                previousButton, nextButton, playlistToggleButton,
                songListView
        );
        playerService.initUi();
        playerService.setOnPlayError(this::onPlayError);

        // 加载本地歌曲
        reloadSongs();

        // 搜索框实时过滤
        search.textProperty().addListener((observable, oldValue, newValue) -> onSearchFilter(newValue));
    }

    // ==================== 页面切换动画 ====================

    /**
     * 带淡入过渡动画的页面切换
     *
     * @param fxmlPath FXML 场景路径
     */
    private void navigateToPage(String fxmlPath) {
        if (pageTransitioning) {
            return;
        }

        Parent newContent = FXMLUtils.loadScene(fxmlPath);
        if (newContent == null) {
            return;
        }

        Node currentContent = root.getCenter();
        if (currentContent == newContent) {
            return;
        }

        pageTransitioning = true;

        // 新内容初始透明
        newContent.setOpacity(0);
        newContent.setScaleX(0.97);
        newContent.setScaleY(0.97);

        // 设置新内容
        root.setCenter(newContent);

        // 构建组合动画：淡入 + 微缩放恢复
        FadeTransition fadeIn = new FadeTransition(CONTENT_TRANSITION_DURATION, newContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scaleIn = new ScaleTransition(CONTENT_TRANSITION_DURATION, newContent);
        scaleIn.setFromX(0.97);
        scaleIn.setFromY(0.97);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition transition = new ParallelTransition(fadeIn, scaleIn);
        transition.setOnFinished(e -> pageTransitioning = false);
        transition.play();
    }

    // ==================== 顶部导航（发现/关注/商城/音乐人/云推歌） ====================

    /**
     * 鼠标悬停 — 添加视觉效果
     */
    public void setTopNavHover(MouseEvent event) {
        Hyperlink source = (Hyperlink) event.getSource();
        if (source != currentTopMenu) {
            source.setStyle(Style.MOUSE_OVER_FONT_STYLE);
        }
    }

    /**
     * 鼠标离开 — 移除视觉效果
     */
    public void removeTopNavHover(MouseEvent event) {
        Hyperlink source = (Hyperlink) event.getSource();
        if (source != currentTopMenu) {
            source.setStyle(Style.REMOVE_BACKGROUND_COLOR);
        }
    }

    private void switchTopMenu(Hyperlink newMenu) {
        if (currentTopMenu != null) {
            currentTopMenu.setStyle(Style.REMOVE_BACKGROUND_COLOR);
        }
        currentTopMenu = newMenu;
        applyTopNavActiveStyle(newMenu);
    }

    private void applyTopNavActiveStyle(Hyperlink menu) {
        menu.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; "
                + "-fx-background-color: rgba(255,255,255,0.20); "
                + "-fx-background-radius: 20; -fx-underline: false; -fx-border-width: 0;");
    }

    @FXML
    public void toDiscover(ActionEvent event) {
        switchTopMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.RECOMMEND_SCENE_PATH);
    }

    @FXML
    public void toFollow(ActionEvent event) {
        switchTopMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.FOLLOW_SCENE_PATH);
    }

    @FXML
    public void toStore(ActionEvent event) {
        switchTopMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.STORE_SCENE_PATH);
    }

    @FXML
    public void toMusician(ActionEvent event) {
        switchTopMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.MUSICIAN_SCENE_PATH);
    }

    @FXML
    public void toCloudPush(ActionEvent event) {
        switchTopMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.CLOUD_PUSH_SCENE_PATH);
    }

    // ==================== 左侧菜单导航 ====================

    /**
     * 鼠标进入 — 菜单 hover 效果
     */
    public void setSelectedBackgroundColor(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Region && source != currentLeftMenu) {
            Region region = (Region) source;
            region.setStyle(Style.MOUSE_OVER_STYLE);
            animateScale(region, MENU_HOVER_SCALE);
        }
    }

    /**
     * 鼠标离开 — 恢复样式
     */
    public void removeBackgroundColor(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Region && source != currentLeftMenu) {
            Region region = (Region) source;
            region.setStyle(Style.REMOVE_BACKGROUND_COLOR);
            animateScale(region, 1.0);
        }
    }

    private void switchLeftMenu(Hyperlink newMenu) {
        if (currentLeftMenu != null) {
            currentLeftMenu.setStyle(Style.REMOVE_BACKGROUND_COLOR);
        }
        currentLeftMenu = newMenu;
        applyLeftMenuActiveStyle(newMenu);
    }

    private void applyLeftMenuActiveStyle(Hyperlink menu) {
        menu.setStyle("-fx-background-color: #dce0e8; -fx-text-fill: #ec4141; "
                + "-fx-font-weight: bold; -fx-underline: false; -fx-border-width: 0;");
    }

    public void toRecommend(ActionEvent event) {
        switchLeftMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.RECOMMEND_SCENE_PATH);
    }

    public void toFeatured(ActionEvent event) {
        switchLeftMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.FEATURED_SCENE_PATH);
    }

    public void toPodcast(ActionEvent event) {
        switchLeftMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.PODCAST_SCENE_PATH);
    }

    public void toPrivateRoaming(ActionEvent event) {
        switchLeftMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.PRIVATE_ROAMING_SCENE_PATH);
    }

    public void toCommunity(ActionEvent event) {
        switchLeftMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.COMMUNITY_SCENE_PATH);
    }

    public void toILike(ActionEvent event) {
        switchLeftMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.I_LIKE_SCENE_PATH);
    }

    public void toMyPodcast(ActionEvent event) {
        switchLeftMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.MY_PODCAST_SCENE_PATH);
    }

    public void toMyCollection(ActionEvent event) {
        switchLeftMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.MY_COLLECTION_SCENE_PATH);
    }

    public void toDownloadManagement(ActionEvent event) {
        switchLeftMenu((Hyperlink) event.getSource());
        navigateToPage(Constant.DOWNLOAD_MANAGEMENT_SCENE_PATH);
    }

    // ==================== 播放控制（委托给 PlayerService） ====================

    @FXML
    public void togglePlayPause(ActionEvent event) {
        playerService.togglePlayPause();
        // 按钮弹跳微动效
        animateButtonPress(playPauseButton);
    }

    @FXML
    public void playPreviousSong(ActionEvent event) {
        playerService.playPrevious();
        animateButtonPress(previousButton);
    }

    @FXML
    public void playNextSong(ActionEvent event) {
        playerService.playNext(true);
        animateButtonPress(nextButton);
    }

    @FXML
    public void switchPlayMode(ActionEvent event) {
        playerService.switchPlayMode();
        animateButtonPress(playModeButton);
    }

    @FXML
    public void onProgressPressed(MouseEvent event) {
        playerService.onProgressPressed();
    }

    @FXML
    public void onProgressReleased(MouseEvent event) {
        playerService.onProgressReleased();
    }

    @FXML
    public void onSongListClicked(MouseEvent event) {
        if (event.getClickCount() < 2) {
            return;
        }
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            playerService.onSongListDoubleClicked(selectedIndex);
        }
    }

    // ==================== 播放列表抽屉（带动画） ====================

    @FXML
    public void togglePlaylistDrawer(ActionEvent event) {
        if (playlistDrawerOpen) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    private void openDrawer() {
        if (playlistDrawerOpen) {
            return;
        }
        playlistDrawerOpen = true;
        playlistToggleButton.setText("收起列表");
        playlistDrawer.setManaged(true);
        playlistDrawer.setVisible(true);
        playlistDrawer.setOpacity(0);

        TranslateTransition slideUp = new TranslateTransition(DRAWER_DURATION, playlistDrawer);
        slideUp.setFromY(100);
        slideUp.setToY(0);
        slideUp.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeIn = new FadeTransition(DRAWER_DURATION, playlistDrawer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition transition = new ParallelTransition(slideUp, fadeIn);
        transition.play();
    }

    private void closeDrawer() {
        if (!playlistDrawerOpen) {
            return;
        }
        playlistDrawerOpen = false;
        playlistToggleButton.setText("播放列表");

        TranslateTransition slideDown = new TranslateTransition(DRAWER_DURATION, playlistDrawer);
        slideDown.setFromY(playlistDrawer.getTranslateY());
        slideDown.setToY(100);
        slideDown.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fadeOut = new FadeTransition(DRAWER_DURATION, playlistDrawer);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setInterpolator(Interpolator.EASE_IN);

        ParallelTransition transition = new ParallelTransition(slideDown, fadeOut);
        transition.setOnFinished(evt -> {
            playlistDrawer.setVisible(false);
            playlistDrawer.setManaged(false);
        });
        transition.play();
    }

    private void closeDrawerImmediately() {
        playlistDrawerOpen = false;
        playlistToggleButton.setText("播放列表");
        playlistDrawer.setTranslateY(100);
        playlistDrawer.setOpacity(0);
        playlistDrawer.setVisible(false);
        playlistDrawer.setManaged(false);
    }

    // ==================== 歌曲加载与搜索 ====================

    @FXML
    public void reloadLocalSongs(ActionEvent event) {
        reloadSongs();
    }

    /**
     * 加载 / 重新加载本地歌曲
     */
    private void reloadSongs() {
        playerService.reset();

        try {
            int count = fileScanner.scan();
            playerService.setSongLists(fileScanner.getSongFiles(), fileScanner.getFilteredIndices());

            if (count == 0) {
                currentSongNameLabel.setText("未找到可播放歌曲");
                playlistInfoLabel.setText("本地歌曲：0");
                playPauseButton.setText("播放");
                closeDrawerImmediately();
                playerService.setControlButtonsEnabled(false);
                songListView.setItems(FXCollections.observableArrayList());
                return;
            }

            playerService.setControlButtonsEnabled(true);

            // 刷新列表显示
            List<String> songNames = fileScanner.filter(search.getText());
            songListView.setItems(FXCollections.observableArrayList(songNames));
            playerService.updatePlaylistInfo(
                    fileScanner.getTotalCount(), fileScanner.getFilteredCount(), isSearching());

            // 断点续播
            int startIndex = playerService.resolveSavedTrackIndex();
            java.io.File startFile = fileScanner.getSongFiles().get(startIndex);
            javafx.util.Duration resumeTime = playerService.resolveSavedTrackDuration(startFile);
            playerService.play(startIndex, false, resumeTime);

        } catch (RuntimeException e) {
            log.error("加载本地歌曲失败", e);
            currentSongNameLabel.setText("扫描失败");
            playlistInfoLabel.setText("本地目录读取失败");
            songListView.setItems(FXCollections.observableArrayList());
            closeDrawerImmediately();
            playerService.setControlButtonsEnabled(false);
        }
    }

    /**
     * 搜索过滤
     */
    private void onSearchFilter(String keyword) {
        List<String> songNames = fileScanner.filter(keyword);
        songListView.setItems(FXCollections.observableArrayList(songNames));
        playerService.setSongLists(fileScanner.getSongFiles(), fileScanner.getFilteredIndices());
        playerService.selectCurrentSongInListView();
        playerService.updatePlaylistInfo(
                fileScanner.getTotalCount(), fileScanner.getFilteredCount(), isSearching());
    }

    private boolean isSearching() {
        return search.getText() != null && !search.getText().trim().isEmpty();
    }

    /**
     * 播放出错回调
     */
    private void onPlayError() {
        playerService.playNext(false);
    }

    // ==================== 动画辅助方法 ====================

    /**
     * 按钮点击弹跳微动效
     */
    private void animateButtonPress(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(120), button);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.92);
        st.setToY(0.92);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.setInterpolator(Interpolator.EASE_BOTH);
        st.play();
    }

    /**
     * 节点缩放动画
     */
    private void animateScale(Node node, double targetScale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(160), node);
        st.setToX(targetScale);
        st.setToY(targetScale);
        st.setInterpolator(Interpolator.EASE_OUT);
        st.play();
    }

    // ==================== 辅助方法 ====================

    private List<Hyperlink> getTopMenus() {
        return Arrays.asList(discoverMenu, followMenu, storeMenu, musicianMenu, cloudPushMenu);
    }
}
