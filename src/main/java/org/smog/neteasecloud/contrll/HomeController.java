package org.smog.neteasecloud.contrll;


import cn.hutool.json.JSONUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import org.smog.neteasecloud.utils.Constant;
import org.smog.neteasecloud.utils.Style;

/**
 * 主页控制器
 *
 * @Desc Created by IntelliJ IDEA.
 * @Time 2023-02-22 4:26
 * @Author 花开富贵
 */
public class HomeController {

    @FXML
    private BorderPane root;

    @FXML
    HBox top;

    @FXML
    private TextField search;


    public void initialize() {
        System.out.println("initialize = ");
    }

    public void setSelectedNavBackgroundColor(MouseEvent event){
        Object source = event.getSource();
        if (source instanceof Region){
            Region region = (Region) source;
            region.setStyle(Style.MOUSE_OVER_FONT_STYLE);
        }
    }

    /**
     * 设置选择菜单背景色
     */
    public void setSelectedBackgroundColor(MouseEvent event){
        Object source = event.getSource();
        if (source instanceof Region){
            Region region = (Region) source;
            region.setStyle(Style.MOUSE_OVER_STYLE);
        }
    }

    /**
     * 移除选择菜单背景色
     */
    public void removeBackgroundColor(MouseEvent event){
        Object source = event.getSource();
        if (source instanceof Region){
            Region region = (Region) source;
            region.setStyle(Style.REMOVE_BACKGROUND_COLOR);
        }
    }
}
