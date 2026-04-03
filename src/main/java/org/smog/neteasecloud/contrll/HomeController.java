package org.smog.neteasecloud.contrll;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.smog.neteasecloud.utils.Constant;
import org.smog.neteasecloud.utils.FXMLUtils;
import org.smog.neteasecloud.utils.Style;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class HomeController {

    private static final String DEFAULT_MUSIC_DIR = "music";
    private static final List<String> SUPPORTED_AUDIO_EXTENSIONS =
            Arrays.asList(".mp3", ".wav", ".m4a", ".aac");

    private static final String PREF_LAST_TRACK_PATH = "player.last.track.path";
    private static final String PREF_LAST_TRACK_SECONDS = "player.last.track.seconds";
    private static final String PREF_VOLUME = "player.volume";
    private static final double DEFAULT_VOLUME = 70;
    private static final String TEXT_PLAY = "播放";
    private static final String TEXT_PAUSE = "暂停";
    private static final String TEXT_NO_SONG = "暂无歌曲";
    private static final String TEXT_LOCAL_TRACKS_ZERO = "本地歌曲：0";
    private static final String TEXT_SCAN_FAILED = "扫描失败";
    private static final String TEXT_NO_TRACKS_HINT = "未找到可播放歌曲";
    private static final String TEXT_DRAWER_OPEN = "收起列表";
    private static final String TEXT_DRAWER_CLOSE = "播放列表";

    private enum PlayMode {
        LIST_LOOP("列表循环"),
        SINGLE_LOOP("单曲循环"),
        RANDOM("随机播放");

        private final String displayName;

        PlayMode(String displayName) {
            this.displayName = displayName;
        }
    }

    @FXML
    private BorderPane root;
    @FXML
    private HBox top;
    @FXML
    private TextField search;
    @FXML
    private VBox leftMenu;

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

    @FXML
    private Hyperlink featuredMenu;
    @FXML
    private Hyperlink recommendMenu;

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

    @FXML
    private Label currentSongNameLabel;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private Label playlistInfoLabel;

    @FXML
    private Slider progressSlider;
    @FXML
    private Slider volumeSlider;

    @FXML
    private ListView<String> songListView;
    @FXML
    private VBox playlistDrawer;

    private final List<File> localSongFiles = new ArrayList<>();
    private final Random random = new Random();
    private final Preferences preferences = Preferences.userNodeForPackage(HomeController.class);

    private MediaPlayer mediaPlayer;
    private int currentSongIndex = -1;
    private boolean draggingProgressSlider = false;
    private int lastSavedProgressSecond = -1;
    private PlayMode currentPlayMode = PlayMode.LIST_LOOP;
    private Hyperlink currentTopMenu;
    private boolean playlistDrawerOpen = false;

    public void initialize() {
        recommendMenu.setStyle(Style.MOUSE_OVER_STYLE);
        root.setCenter(FXMLUtils.loadScene(Constant.RECOMMEND_SCENE_PATH));

        currentTopMenu = discoverMenu;
        if (discoverMenu != null) {
            discoverMenu.setStyle(Style.MOUSE_OVER_FONT_STYLE);
        }

        initializePlayerUi();
        loadLocalSongs();
    }

    public void setTopNavHover(MouseEvent event) {
        Hyperlink source = (Hyperlink) event.getSource();
        if (source != currentTopMenu) {
            source.setStyle(Style.MOUSE_OVER_FONT_STYLE);
        }
    }

    public void removeTopNavHover(MouseEvent event) {
        Hyperlink source = (Hyperlink) event.getSource();
        if (source != currentTopMenu) {
            source.setStyle(Style.REMOVE_BACKGROUND_COLOR);
        }
    }

    public void setSelectedBackgroundColor(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Region) {
            ((Region) source).setStyle(Style.MOUSE_OVER_STYLE);
        }
    }

    public void removeBackgroundColor(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Region) {
            ((Region) source).setStyle(Style.REMOVE_BACKGROUND_COLOR);
        }
    }

    private void setSelectedTopMenu(ActionEvent event) {
        getTopMenus().forEach(menu -> menu.setStyle(Style.REMOVE_BACKGROUND_COLOR));
        currentTopMenu = (Hyperlink) event.getSource();
        currentTopMenu.setStyle(Style.MOUSE_OVER_FONT_STYLE);
    }

    public void setTheSelectedMenuBackgroundColor(ActionEvent event) {
        ObservableList<Node> children = leftMenu.getChildren();
        children.forEach(menu -> menu.setStyle(Style.REMOVE_BACKGROUND_COLOR));
        ((Region) event.getSource()).setStyle(Style.MOUSE_OVER_STYLE);
    }

    @FXML
    public void toDiscover(ActionEvent event) {
        setSelectedTopMenu(event);
        root.setCenter(FXMLUtils.loadScene(Constant.RECOMMEND_SCENE_PATH));
    }

    @FXML
    public void toFollow(ActionEvent event) {
        setSelectedTopMenu(event);
        root.setCenter(FXMLUtils.loadScene(Constant.FOLLOW_SCENE_PATH));
    }

    @FXML
    public void toStore(ActionEvent event) {
        setSelectedTopMenu(event);
        root.setCenter(FXMLUtils.loadScene(Constant.STORE_SCENE_PATH));
    }

    @FXML
    public void toMusician(ActionEvent event) {
        setSelectedTopMenu(event);
        root.setCenter(FXMLUtils.loadScene(Constant.MUSICIAN_SCENE_PATH));
    }

    @FXML
    public void toCloudPush(ActionEvent event) {
        setSelectedTopMenu(event);
        root.setCenter(FXMLUtils.loadScene(Constant.CLOUD_PUSH_SCENE_PATH));
    }

    public void toRecommend(ActionEvent event) {
        setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.RECOMMEND_SCENE_PATH));
    }

    public void toFeatured(ActionEvent event) {
        setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.FEATURED_SCENE_PATH));
    }

    public void toPodcast(ActionEvent event) {
        setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.PODCAST_SCENE_PATH));
    }

    public void toPrivateRoaming(ActionEvent event) {
        setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.PRIVATE_ROAMING_SCENE_PATH));
    }

    public void toCommunity(ActionEvent event) {
        setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.COMMUNITY_SCENE_PATH));
    }

    public void toILike(ActionEvent event) {
        setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.I_LIKE_SCENE_PATH));
    }

    public void toMyPodcast(ActionEvent event) {
        setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.MY_PODCAST_SCENE_PATH));
    }

    public void toMyCollection(ActionEvent event) {
        setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.MY_COLLECTION_SCENE_PATH));
    }

    public void toDownloadManagement(ActionEvent event) {
        setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.DOWNLOAD_MANAGEMENT_SCENE_PATH));
    }

    @FXML
    public void togglePlayPause(ActionEvent event) {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText(TEXT_PLAY);
        } else {
            mediaPlayer.play();
            playPauseButton.setText(TEXT_PAUSE);
        }
    }

    @FXML
    public void togglePlaylistDrawer(ActionEvent event) {
        if (playlistDrawerOpen) {
            closePlaylistDrawer();
        } else {
            openPlaylistDrawer();
        }
    }

    @FXML
    public void playPreviousSong(ActionEvent event) {
        if (localSongFiles.isEmpty()) {
            return;
        }
        int previousIndex = currentSongIndex <= 0 ? localSongFiles.size() - 1 : currentSongIndex - 1;
        if (currentPlayMode == PlayMode.RANDOM) {
            previousIndex = resolveRandomIndex();
        }
        playSongAt(previousIndex, true, Duration.ZERO);
    }

    @FXML
    public void playNextSong(ActionEvent event) {
        playNextSongInternal(true);
    }

    @FXML
    public void reloadLocalSongs(ActionEvent event) {
        loadLocalSongs();
    }

    @FXML
    public void switchPlayMode(ActionEvent event) {
        PlayMode[] values = PlayMode.values();
        currentPlayMode = values[(currentPlayMode.ordinal() + 1) % values.length];
        playModeButton.setText(currentPlayMode.displayName);
    }

    @FXML
    public void onProgressPressed(MouseEvent event) {
        draggingProgressSlider = true;
    }

    @FXML
    public void onProgressReleased(MouseEvent event) {
        draggingProgressSlider = false;
        if (mediaPlayer == null) {
            return;
        }
        Duration seekDuration = Duration.seconds(progressSlider.getValue());
        mediaPlayer.seek(seekDuration);
        savePlaybackProgress(currentSongIndex, seekDuration);
    }

    @FXML
    public void onSongListClicked(MouseEvent event) {
        if (event.getClickCount() < 2) {
            return;
        }
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < localSongFiles.size()) {
            playSongAt(selectedIndex, true, Duration.ZERO);
        }
    }

    private void initializePlayerUi() {
        progressSlider.setMin(0);
        progressSlider.setMax(0);
        progressSlider.setValue(0);

        double savedVolume = preferences.getDouble(PREF_VOLUME, DEFAULT_VOLUME);
        if (savedVolume < 0 || savedVolume > 100) {
            savedVolume = DEFAULT_VOLUME;
        }
        volumeSlider.setMin(0);
        volumeSlider.setMax(100);
        volumeSlider.setValue(savedVolume);

        currentTimeLabel.setText("00:00");
        totalTimeLabel.setText("00:00");
        currentSongNameLabel.setText(TEXT_NO_SONG);
        playlistInfoLabel.setText(TEXT_LOCAL_TRACKS_ZERO);
        playPauseButton.setText(TEXT_PLAY);
        playModeButton.setText(currentPlayMode.displayName);
        playlistToggleButton.setText(TEXT_DRAWER_CLOSE);

        playlistDrawer.setVisible(false);
        playlistDrawer.setManaged(false);
        playlistDrawer.setTranslateY(180);

        setPlaybackButtonsState(false);

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            preferences.putDouble(PREF_VOLUME, newValue.doubleValue());
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
            }
        });
    }

    private void loadLocalSongs() {
        releaseCurrentPlayer();

        localSongFiles.clear();
        currentSongIndex = -1;
        progressSlider.setValue(0);
        progressSlider.setMax(0);
        currentTimeLabel.setText("00:00");
        totalTimeLabel.setText("00:00");

        Path musicDir = resolveMusicDirectory();
        try {
            if (!Files.exists(musicDir)) {
                Files.createDirectories(musicDir);
            }
            try (Stream<Path> stream = Files.list(musicDir)) {
                localSongFiles.addAll(stream
                        .filter(Files::isRegularFile)
                        .filter(path -> hasSupportedExtension(path.getFileName().toString()))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
                        .map(Path::toFile)
                        .collect(Collectors.toList()));
            }
        } catch (IOException e) {           log.error("扫描本地音乐目录失败: {}", musicDir, e);           currentSongNameLabel.setText(TEXT_SCAN_FAILED);
            playlistInfoLabel.setText("本地目录读取失败：" + musicDir);
            refreshSongListView();
            closePlaylistDrawerImmediately();
            setPlaybackButtonsState(false);
            return;
        }

        refreshSongListView();
        playlistInfoLabel.setText("本地歌曲：" + localSongFiles.size() );
        if (localSongFiles.isEmpty()) {
            currentSongNameLabel.setText(TEXT_NO_TRACKS_HINT);
            playPauseButton.setText(TEXT_PLAY);
            closePlaylistDrawerImmediately();
            setPlaybackButtonsState(false);
            return;
        }

        setPlaybackButtonsState(true);
        int startIndex = resolveSavedTrackIndex();
        Duration resumeTime = resolveSavedTrackDuration(localSongFiles.get(startIndex));
        playSongAt(startIndex, false, resumeTime);
    }

    private void refreshSongListView() {
        List<String> songNames = new ArrayList<>();
        for (int i = 0; i < localSongFiles.size(); i++) {
            songNames.add((i + 1) + ". " + localSongFiles.get(i).getName());
        }
        songListView.setItems(FXCollections.observableArrayList(songNames));
    }

    private void openPlaylistDrawer() {
        playlistDrawerOpen = true;
        playlistToggleButton.setText(TEXT_DRAWER_OPEN);
        playlistDrawer.setManaged(true);
        playlistDrawer.setVisible(true);

        TranslateTransition transition = new TranslateTransition(Duration.millis(180), playlistDrawer);
        transition.setFromY(180);
        transition.setToY(0);
        transition.play();
    }

    private void closePlaylistDrawer() {
        playlistDrawerOpen = false;
        playlistToggleButton.setText(TEXT_DRAWER_CLOSE);

        TranslateTransition transition = new TranslateTransition(Duration.millis(180), playlistDrawer);
        transition.setFromY(playlistDrawer.getTranslateY());
        transition.setToY(180);
        transition.setOnFinished(evt -> {
            playlistDrawer.setVisible(false);
            playlistDrawer.setManaged(false);
        });
        transition.play();
    }

    private void closePlaylistDrawerImmediately() {
        playlistDrawerOpen = false;
        playlistToggleButton.setText(TEXT_DRAWER_CLOSE);
        playlistDrawer.setTranslateY(180);
        playlistDrawer.setVisible(false);
        playlistDrawer.setManaged(false);
    }

    private void playNextSongInternal(boolean manualSwitch) {
        if (localSongFiles.isEmpty()) {
            return;
        }

        int nextIndex;
        if (!manualSwitch && currentPlayMode == PlayMode.SINGLE_LOOP) {
            nextIndex = currentSongIndex;
        } else if (currentPlayMode == PlayMode.RANDOM) {
            nextIndex = resolveRandomIndex();
        } else {
            nextIndex = (currentSongIndex + 1) % localSongFiles.size();
        }

        playSongAt(nextIndex, true, Duration.ZERO);
    }

    private int resolveRandomIndex() {
        if (localSongFiles.size() <= 1) {
            return 0;
        }
        int nextIndex = currentSongIndex;
        while (nextIndex == currentSongIndex) {
            nextIndex = random.nextInt(localSongFiles.size());
        }
        return nextIndex;
    }

    private void playSongAt(int index, boolean autoPlay, Duration startAt) {
        if (index < 0 || index >= localSongFiles.size()) {
            return;
        }

        releaseCurrentPlayer();

        File songFile = localSongFiles.get(index);
        currentSongIndex = index;
        lastSavedProgressSecond = -1;
        currentSongNameLabel.setText(songFile.getName());
        playPauseButton.setText(TEXT_PLAY);
        songListView.getSelectionModel().select(index);
        songListView.scrollTo(index);

        mediaPlayer = new MediaPlayer(new Media(songFile.toURI().toString()));
        mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
        preferences.put(PREF_LAST_TRACK_PATH, songFile.getAbsolutePath());

        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = mediaPlayer.getTotalDuration();
            progressSlider.setMax(totalDuration.toSeconds());
            progressSlider.setValue(0);
            currentTimeLabel.setText("00:00");
            totalTimeLabel.setText(formatDuration(totalDuration));

            if (startAt != null && !startAt.isUnknown() && startAt.greaterThan(Duration.ZERO) && startAt.lessThan(totalDuration)) {
                mediaPlayer.seek(startAt);
                progressSlider.setValue(startAt.toSeconds());
                currentTimeLabel.setText(formatDuration(startAt));
            }

            if (autoPlay) {
                mediaPlayer.play();
                playPauseButton.setText(TEXT_PAUSE);
            }
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!draggingProgressSlider) {
                progressSlider.setValue(newValue.toSeconds());
            }
            currentTimeLabel.setText(formatDuration(newValue));

            int seconds = (int) Math.floor(newValue.toSeconds());
            if (seconds != lastSavedProgressSecond) {
                lastSavedProgressSecond = seconds;
                savePlaybackProgress(currentSongIndex, newValue);
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> playNextSongInternal(false));
        mediaPlayer.setOnError(() -> {
            log.error("音乐播放失败: {}", songFile.getAbsolutePath(), mediaPlayer.getError());
            playNextSongInternal(false);
        });
    }

    private void releaseCurrentPlayer() {
        if (mediaPlayer != null) {
            savePlaybackProgress(currentSongIndex, mediaPlayer.getCurrentTime());
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    private void savePlaybackProgress(int index, Duration currentTime) {
        if (index < 0 || index >= localSongFiles.size() || currentTime == null || currentTime.isUnknown()) {
            return;
        }
        preferences.put(PREF_LAST_TRACK_PATH, localSongFiles.get(index).getAbsolutePath());
        preferences.putDouble(PREF_LAST_TRACK_SECONDS, Math.max(0, currentTime.toSeconds()));
    }

    private int resolveSavedTrackIndex() {
        String savedTrackPath = preferences.get(PREF_LAST_TRACK_PATH, "");
        for (int i = 0; i < localSongFiles.size(); i++) {
            if (localSongFiles.get(i).getAbsolutePath().equals(savedTrackPath)) {
                return i;
            }
        }
        return 0;
    }

    private Duration resolveSavedTrackDuration(File file) {
        String savedTrackPath = preferences.get(PREF_LAST_TRACK_PATH, "");
        if (!file.getAbsolutePath().equals(savedTrackPath)) {
            return Duration.ZERO;
        }
        double seconds = preferences.getDouble(PREF_LAST_TRACK_SECONDS, 0);
        return seconds <= 0 ? Duration.ZERO : Duration.seconds(seconds);
    }

    private void setPlaybackButtonsState(boolean enabled) {
        previousButton.setDisable(!enabled);
        playPauseButton.setDisable(!enabled);
        nextButton.setDisable(!enabled);
        playModeButton.setDisable(!enabled);
        playlistToggleButton.setDisable(!enabled);
        songListView.setDisable(!enabled);
    }

    private Path resolveMusicDirectory() {
        String configuredPath = readMusicDirectoryFromProperties();
        Path path = Paths.get(configuredPath);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir")).resolve(path).normalize();
        }
        return path;
    }

    private String readMusicDirectoryFromProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            log.warn("read application.properties failed", e);
        }

        String musicDir = properties.getProperty("music.local.dir", DEFAULT_MUSIC_DIR);
        return (musicDir == null || musicDir.trim().isEmpty()) ? DEFAULT_MUSIC_DIR : musicDir.trim();
    }

    private boolean hasSupportedExtension(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        return SUPPORTED_AUDIO_EXTENSIONS.stream().anyMatch(lowerFileName::endsWith);
    }

    private String formatDuration(Duration duration) {
        if (duration == null || duration.isUnknown() || duration.lessThanOrEqualTo(Duration.ZERO)) {
            return "00:00";
        }
        long totalSeconds = (long) Math.floor(duration.toSeconds());
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    private List<Hyperlink> getTopMenus() {
        return Arrays.asList(discoverMenu, followMenu, storeMenu, musicianMenu, cloudPushMenu);
    }
}
