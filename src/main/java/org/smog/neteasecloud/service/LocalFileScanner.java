package org.smog.neteasecloud.service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 本地音乐文件扫描器
 *
 * @Desc 负责扫描本地音乐目录、过滤支持的音频格式、搜索过滤
 * @Time 2024-06-21 10:31
 * @Author HuangZhongYao
 */
@Slf4j
public class LocalFileScanner {

    /** 默认音乐目录 */
    private static final String DEFAULT_MUSIC_DIR = "music";

    /** 支持的音频文件扩展名 */
    private static final List<String> SUPPORTED_AUDIO_EXTENSIONS =
            Arrays.asList(".mp3", ".wav", ".m4a", ".aac");

    /** 扫描到的歌曲文件列表 */
    private final List<File> songFiles = new ArrayList<>();

    /** 当前过滤后的索引列表（与搜索关键词匹配的原始索引） */
    private final List<Integer> filteredIndices = new ArrayList<>();

    /**
     * 扫描本地音乐目录
     *
     * @return 扫描到的歌曲文件数量
     */
    public int scan() {
        songFiles.clear();
        filteredIndices.clear();

        Path musicDir = resolveMusicDirectory();
        try {
            if (!Files.exists(musicDir)) {
                Files.createDirectories(musicDir);
                log.info("音乐目录不存在，已自动创建: {}", musicDir.toAbsolutePath());
                return 0;
            }

            try (Stream<Path> stream = Files.list(musicDir)) {
                List<File> files = stream
                        .filter(Files::isRegularFile)
                        .filter(path -> hasSupportedExtension(path.getFileName().toString()))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase(Locale.ROOT)))
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                songFiles.addAll(files);
            }

            // 初始时不过滤，显示全部
            for (int i = 0; i < songFiles.size(); i++) {
                filteredIndices.add(i);
            }

            log.info("扫描完成，共发现 {} 首歌曲，目录: {}", songFiles.size(), musicDir.toAbsolutePath());
        } catch (IOException e) {
            log.error("扫描本地音乐目录失败: {}", musicDir, e);
            throw new RuntimeException("扫描音乐目录失败: " + musicDir, e);
        }

        return songFiles.size();
    }

    /**
     * 根据关键词过滤歌曲列表
     *
     * @param keyword 搜索关键词（为空则显示全部）
     * @return 过滤后的歌曲名称列表（带序号）
     */
    public List<String> filter(String keyword) {
        filteredIndices.clear();

        String kw = (keyword == null || keyword.trim().isEmpty())
                ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        List<String> songNames = new ArrayList<>();
        for (int i = 0; i < songFiles.size(); i++) {
            String fileName = songFiles.get(i).getName();
            if (!kw.isEmpty() && !fileName.toLowerCase(Locale.ROOT).contains(kw)) {
                continue;
            }
            filteredIndices.add(i);
            songNames.add((i + 1) + ". " + fileName);
        }

        return songNames;
    }

    /**
     * 将过滤索引映射回原始歌曲索引
     *
     * @param displayIndex 列表中的显示位置
     * @return 原始歌曲索引，无效时返回 -1
     */
    public int toOriginalIndex(int displayIndex) {
        if (displayIndex < 0 || displayIndex >= filteredIndices.size()) {
            return -1;
        }
        return filteredIndices.get(displayIndex);
    }

    /**
     * 获取歌曲总数量
     */
    public int getTotalCount() {
        return songFiles.size();
    }

    /**
     * 获取过滤后的数量
     */
    public int getFilteredCount() {
        return filteredIndices.size();
    }

    /**
     * 获取过滤后的索引列表（只读）
     */
    public List<Integer> getFilteredIndices() {
        return Collections.unmodifiableList(filteredIndices);
    }

    /**
     * 获取歌曲文件列表（只读）
     */
    public List<File> getSongFiles() {
        return Collections.unmodifiableList(songFiles);
    }

    /**
     * 判断是否有歌曲
     */
    public boolean isEmpty() {
        return songFiles.isEmpty();
    }

    // ==================== 私有方法 ====================

    /**
     * 解析音乐目录路径
     */
    private Path resolveMusicDirectory() {
        String configuredPath = readMusicDirectoryFromProperties();
        Path path = Paths.get(configuredPath);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir")).resolve(path).normalize();
        }
        return path;
    }

    /**
     * 从配置文件读取音乐目录路径
     */
    private String readMusicDirectoryFromProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            log.warn("读取 application.properties 失败，使用默认目录", e);
        }

        String musicDir = properties.getProperty("music.local.dir", DEFAULT_MUSIC_DIR);
        return (musicDir == null || musicDir.trim().isEmpty())
                ? DEFAULT_MUSIC_DIR : musicDir.trim();
    }

    /**
     * 检查文件扩展名是否为支持的音频格式
     */
    private boolean hasSupportedExtension(String fileName) {
        String lowerFileName = fileName.toLowerCase(Locale.ROOT);
        return SUPPORTED_AUDIO_EXTENSIONS.stream().anyMatch(lowerFileName::endsWith);
    }
}
