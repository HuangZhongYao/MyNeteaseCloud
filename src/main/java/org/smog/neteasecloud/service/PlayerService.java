package org.smog.neteasecloud.service;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.smog.neteasecloud.enums.PlayMode;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;

/**
 * 音乐播放服务
 *
 * @Desc 管理 MediaPlayer 生命周期、播放控制、进度记忆、播放模式切换
 * @Time 2024-06-21 10:31
 * @Author HuangZhongYao
 */
@Slf4j
public class PlayerService {

    // ==================== Preference 键常量 ====================

    private static final String PREF_LAST_TRACK_PATH = "player.last.track.path";
    private static final String PREF_LAST_TRACK_SECONDS = "player.last.track.seconds";
    private static final String PREF_VOLUME = "player.volume";

    // ==================== 默认值 ====================

    private static final double DEFAULT_VOLUME = 70;

    // ==================== UI 控件 ====================

    private Label currentSongNameLabel;
    private Label currentTimeLabel;
    private Label totalTimeLabel;
    private Label playlistInfoLabel;
    private Slider progressSlider;
    private Slider volumeSlider;
    private Button playPauseButton;
    private Button playModeButton;
    private Button previousButton;
    private Button nextButton;
    private Button playlistToggleButton;
    private ListView<String> songListView;

    // ==================== 播放状态 ====================

    private MediaPlayer mediaPlayer;
    private int currentSongIndex = -1;
    private boolean draggingProgressSlider;
    private int lastSavedProgressSecond = -1;
    private PlayMode currentPlayMode = PlayMode.LIST_LOOP;
    private final Random random = new Random();
    private final Preferences preferences;

    // ==================== 数据引用 ====================

    private List<File> songFiles;
    private List<Integer> filteredIndices;

    // ==================== 回调 ====================

    /** 播放出错时的回调（通常跳到下一首） */
    private Runnable onPlayError;

    // ==================== 构造与初始化 ====================

    public PlayerService() {
        this.preferences = Preferences.userNodeForPackage(PlayerService.class);
    }

    /**
     * 设置 UI 控件引用（在 FXML 注入后调用）
     */
    public void bindUi(
            Label currentSongNameLabel,
            Label currentTimeLabel,
            Label totalTimeLabel,
            Label playlistInfoLabel,
            Slider progressSlider,
            Slider volumeSlider,
            Button playPauseButton,
            Button playModeButton,
            Button previousButton,
            Button nextButton,
            Button playlistToggleButton,
            ListView<String> songListView) {

        this.currentSongNameLabel = currentSongNameLabel;
        this.currentTimeLabel = currentTimeLabel;
        this.totalTimeLabel = totalTimeLabel;
        this.playlistInfoLabel = playlistInfoLabel;
        this.progressSlider = progressSlider;
        this.volumeSlider = volumeSlider;
        this.playPauseButton = playPauseButton;
        this.playModeButton = playModeButton;
        this.previousButton = previousButton;
        this.nextButton = nextButton;
        this.playlistToggleButton = playlistToggleButton;
        this.songListView = songListView;
    }

    /**
     * 设置播放错误回调
     */
    public void setOnPlayError(Runnable onPlayError) {
        this.onPlayError = onPlayError;
    }

    /**
     * 初始化播放器 UI 状态
     */
    public void initUi() {
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
        currentSongNameLabel.setText("暂无歌曲");
        playlistInfoLabel.setText("本地歌曲：0");
        playPauseButton.setText("播放");
        playModeButton.setText(currentPlayMode.getDisplayName());
        playlistToggleButton.setText("播放列表");

        // 音量变化监听
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            preferences.putDouble(PREF_VOLUME, newValue.doubleValue());
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
            }
        });
    }

    // ==================== 歌曲列表管理 ====================

    /**
     * 更新歌曲列表引用
     */
    public void setSongLists(List<File> songFiles, List<Integer> filteredIndices) {
        this.songFiles = songFiles;
        this.filteredIndices = filteredIndices;
    }

    /**
     * 重置播放状态（切换歌曲列表时调用）
     */
    public void reset() {
        releaseCurrentPlayer();
        currentSongIndex = -1;
        progressSlider.setValue(0);
        progressSlider.setMax(0);
        currentTimeLabel.setText("00:00");
        totalTimeLabel.setText("00:00");
    }

    // ==================== 播放控制 ====================

    /**
     * 播放/暂停切换
     */
    public void togglePlayPause() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText("播放");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("暂停");
        }
    }

    /**
     * 播放上一首
     */
    public void playPrevious() {
        if (songFiles == null || songFiles.isEmpty()) {
            return;
        }

        int previousIndex;
        if (currentPlayMode == PlayMode.RANDOM) {
            previousIndex = resolveRandomIndex();
        } else {
            previousIndex = currentSongIndex <= 0 ? songFiles.size() - 1 : currentSongIndex - 1;
        }

        play(previousIndex, true, Duration.ZERO);
    }

    /**
     * 播放下一首
     */
    public void playNext(boolean manualSwitch) {
        if (songFiles == null || songFiles.isEmpty()) {
            return;
        }

        int nextIndex;
        if (!manualSwitch && currentPlayMode == PlayMode.SINGLE_LOOP) {
            nextIndex = currentSongIndex;
        } else if (currentPlayMode == PlayMode.RANDOM) {
            nextIndex = resolveRandomIndex();
        } else {
            nextIndex = (currentSongIndex + 1) % songFiles.size();
        }

        play(nextIndex, true, Duration.ZERO);
    }

    /**
     * 播放指定索引的歌曲
     *
     * @param index    歌曲在 songFiles 中的索引
     * @param autoPlay 是否自动播放
     * @param startAt  从指定位置开始播放
     */
    public void play(int index, boolean autoPlay, Duration startAt) {
        if (songFiles == null || index < 0 || index >= songFiles.size()) {
            return;
        }

        releaseCurrentPlayer();

        File songFile = songFiles.get(index);
        currentSongIndex = index;
        lastSavedProgressSecond = -1;
        currentSongNameLabel.setText(songFile.getName());
        playPauseButton.setText("播放");
        selectCurrentSongInListView();

        mediaPlayer = new MediaPlayer(new Media(songFile.toURI().toString()));
        mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
        preferences.put(PREF_LAST_TRACK_PATH, songFile.getAbsolutePath());

        // 媒体就绪回调
        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = mediaPlayer.getTotalDuration();
            progressSlider.setMax(totalDuration.toSeconds());
            progressSlider.setValue(0);
            currentTimeLabel.setText("00:00");
            totalTimeLabel.setText(formatDuration(totalDuration));

            if (startAt != null && !startAt.isUnknown()
                    && startAt.greaterThan(Duration.ZERO)
                    && startAt.lessThan(totalDuration)) {
                mediaPlayer.seek(startAt);
                progressSlider.setValue(startAt.toSeconds());
                currentTimeLabel.setText(formatDuration(startAt));
            }

            if (autoPlay) {
                mediaPlayer.play();
                playPauseButton.setText("暂停");
            }
        });

        // 播放进度监听
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

        // 播放结束 → 下一首
        mediaPlayer.setOnEndOfMedia(() -> playNext(false));

        // 播放出错
        mediaPlayer.setOnError(() -> {
            log.error("音乐播放失败: {}", songFile.getAbsolutePath(),
                    mediaPlayer.getError() != null ? mediaPlayer.getError().getMessage() : "未知错误");
            if (onPlayError != null) {
                onPlayError.run();
            } else {
                playNext(false);
            }
        });
    }

    /**
     * 切换到指定进度
     */
    public void seek(Duration seekDuration) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(seekDuration);
            savePlaybackProgress(currentSongIndex, seekDuration);
        }
    }

    // ==================== 播放模式 ====================

    /**
     * 切换到下一个播放模式
     */
    public void switchPlayMode() {
        currentPlayMode = currentPlayMode.next();
        playModeButton.setText(currentPlayMode.getDisplayName());
    }

    /**
     * 获取当前播放模式
     */
    public PlayMode getCurrentPlayMode() {
        return currentPlayMode;
    }

    // ==================== 进度条拖拽 ====================

    public void onProgressPressed() {
        draggingProgressSlider = true;
    }

    public void onProgressReleased() {
        draggingProgressSlider = false;
        if (mediaPlayer != null) {
            seek(Duration.seconds(progressSlider.getValue()));
        }
    }

    // ==================== 列表点击 ====================

    /**
     * 处理播放列表双击事件
     *
     * @param displayIndex 列表中的显示位置
     */
    public void onSongListDoubleClicked(int displayIndex) {
        if (filteredIndices == null || displayIndex < 0 || displayIndex >= filteredIndices.size()) {
            return;
        }
        int originalIndex = filteredIndices.get(displayIndex);
        play(originalIndex, true, Duration.ZERO);
    }

    // ==================== 断点续播 ====================

    /**
     * 尝试恢复上次的播放进度
     *
     * @return 建议的起始播放索引
     */
    public int resolveSavedTrackIndex() {
        if (songFiles == null || songFiles.isEmpty()) {
            return 0;
        }
        String savedTrackPath = preferences.get(PREF_LAST_TRACK_PATH, "");
        for (int i = 0; i < songFiles.size(); i++) {
            if (songFiles.get(i).getAbsolutePath().equals(savedTrackPath)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 解析上次保存的播放时间点
     */
    public Duration resolveSavedTrackDuration(File file) {
        String savedTrackPath = preferences.get(PREF_LAST_TRACK_PATH, "");
        if (!file.getAbsolutePath().equals(savedTrackPath)) {
            return Duration.ZERO;
        }
        double seconds = preferences.getDouble(PREF_LAST_TRACK_SECONDS, 0);
        return seconds <= 0 ? Duration.ZERO : Duration.seconds(seconds);
    }

    // ==================== 控件状态 ====================

    /**
     * 设置播放控制按钮的启用/禁用状态
     */
    public void setControlButtonsEnabled(boolean enabled) {
        previousButton.setDisable(!enabled);
        playPauseButton.setDisable(!enabled);
        nextButton.setDisable(!enabled);
        playModeButton.setDisable(!enabled);
        playlistToggleButton.setDisable(!enabled);
        songListView.setDisable(!enabled);
    }

    /**
     * 在 ListView 中选中当前播放的歌曲
     */
    public void selectCurrentSongInListView() {
        if (songListView == null || filteredIndices == null) {
            return;
        }
        int displayIndex = filteredIndices.indexOf(currentSongIndex);
        if (displayIndex >= 0) {
            songListView.getSelectionModel().select(displayIndex);
            songListView.scrollTo(displayIndex);
        } else {
            songListView.getSelectionModel().clearSelection();
        }
    }

    // ==================== 播放列表信息 ====================

    /**
     * 更新播放列表信息标签
     */
    public void updatePlaylistInfo(int totalCount, int filteredCount, boolean isSearching) {
        if (isSearching) {
            playlistInfoLabel.setText("本地歌曲：" + totalCount + "，匹配：" + filteredCount);
        } else {
            playlistInfoLabel.setText("本地歌曲：" + totalCount);
        }
    }

    // ==================== 资源释放 ====================

    /**
     * 释放当前播放器资源
     */
    public void release() {
        releaseCurrentPlayer();
    }

    // ==================== 公开查询方法 ====================

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    // ==================== 私有辅助方法 ====================

    private void releaseCurrentPlayer() {
        if (mediaPlayer != null) {
            savePlaybackProgress(currentSongIndex, mediaPlayer.getCurrentTime());
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    private void savePlaybackProgress(int index, Duration currentTime) {
        if (songFiles == null || index < 0 || index >= songFiles.size()
                || currentTime == null || currentTime.isUnknown()) {
            return;
        }
        preferences.put(PREF_LAST_TRACK_PATH, songFiles.get(index).getAbsolutePath());
        preferences.putDouble(PREF_LAST_TRACK_SECONDS, Math.max(0, currentTime.toSeconds()));
    }

    private int resolveRandomIndex() {
        if (songFiles == null || songFiles.size() <= 1) {
            return 0;
        }
        int nextIndex = currentSongIndex;
        while (nextIndex == currentSongIndex) {
            nextIndex = random.nextInt(songFiles.size());
        }
        return nextIndex;
    }

    // ==================== 静态工具方法 ====================

    /**
     * 格式化播放时间为 mm:ss 或 hh:mm:ss
     */
    public static String formatDuration(Duration duration) {
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
}
