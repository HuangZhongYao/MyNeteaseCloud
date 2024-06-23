package org.smog.neteasecloud.contrll;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.smog.neteasecloud.utils.Constant;
import org.smog.neteasecloud.utils.FXMLUtils;
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

    @FXML
    private VBox leftMenu;

    @FXML
    private Hyperlink featuredMenu;

    @FXML
    private Hyperlink recommendMenu;


    public void initialize() {
        // 默认推荐页面
        this.recommendMenu.setStyle(Style.MOUSE_OVER_STYLE);
        root.setCenter(FXMLUtils.loadScene(Constant.RECOMMEND_SCENE_PATH));
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


    /**
     * 设置所选菜单背景颜色
     * @param event 事件
     */
    public void setTheSelectedMenuBackgroundColor(ActionEvent event){

        // 移除其它菜单背景色
        ObservableList<Node> children = leftMenu.getChildren();
        children.forEach(menu->menu.setStyle(Style.REMOVE_BACKGROUND_COLOR));

        // 设置当前选中菜单颜色
        Region source = (Region) event.getSource();
        source.setStyle(Style.MOUSE_OVER_STYLE);
    }
    /**
     * 中心区域显示推荐场景页面
     * @param event 事件
     */
    public void toRecommend(ActionEvent event)  {
        this.setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.RECOMMEND_SCENE_PATH));
    }

    /**
     * 中心区域显示精选场景页面
     * @param event 事件
     */
    public void toFeatured(ActionEvent event)  {
        this.setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.FEATURED_SCENE_PATH));
    }


    public void toPodcast(ActionEvent event)  {
        this.setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.PODCAST_SCENE_PATH));
    }

    public void toPrivateRoaming(ActionEvent event)  {
        this.setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.PRIVATE_ROAMING_SCENE_PATH));
    }

    public void toCommunity(ActionEvent event)  {
        this.setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.COMMUNITY_SCENE_PATH));
    }

    public void toILike(ActionEvent event)  {
        this.setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.I_LIKE_SCENE_PATH));
    }

    public void toMyPodcast(ActionEvent event)  {
        this.setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.MY_PODCAST_SCENE_PATH));
    }

    public void toMyCollection(ActionEvent event)  {
        this.setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.MY_COLLECTION_SCENE_PATH));
    }

    public void toDownloadManagement(ActionEvent event)  {
        this.setTheSelectedMenuBackgroundColor(event);
        root.setCenter(FXMLUtils.loadScene(Constant.DOWNLOAD_MANAGEMENT_SCENE_PATH));
    }
}
