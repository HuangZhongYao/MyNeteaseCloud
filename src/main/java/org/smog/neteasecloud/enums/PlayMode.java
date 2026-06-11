package org.smog.neteasecloud.enums;

/**
 * 播放模式枚举
 *
 * @Desc 列表循环 / 单曲循环 / 随机播放
 * @Time 2024-06-21 10:31
 * @Author HuangZhongYao
 */
public enum PlayMode {

    /** 列表循环 */
    LIST_LOOP("列表循环"),

    /** 单曲循环 */
    SINGLE_LOOP("单曲循环"),

    /** 随机播放 */
    RANDOM("随机播放");

    private final String displayName;

    PlayMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 切换到下一个播放模式
     */
    public PlayMode next() {
        PlayMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
