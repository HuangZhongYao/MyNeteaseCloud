package org.smog.neteasecloud.contrll;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

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
        System.out.println("initialize = " );
    }

}
