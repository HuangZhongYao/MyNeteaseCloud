package org.smog.neteasecloud.utils;

/**
 * UI 样式常量
 *
 * @Desc 统一的 CSS 样式字符串，避免散落在各 Controller 中
 * @Author ZhongYao.Huang
 * @Time 2024-06-20 20:17
 */
public final class Style {

    private Style() {
        // 工具类，禁止实例化
    }

    /** 去除背景色 */
    public static final String REMOVE_BACKGROUND_COLOR = "-fx-background-color: transparent;";

    /** 鼠标悬停时左侧菜单背景色 */
    public static final String MOUSE_OVER_STYLE = "-fx-background-color: #e4e8ec;";

    /** 鼠标悬停时顶部导航字体颜色 */
    public static final String MOUSE_OVER_FONT_STYLE = "-fx-text-fill: #515151;";
}
