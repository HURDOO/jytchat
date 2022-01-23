package kr.kro.hurdoo.jytchat.ui;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PopupStage {
    private static List<Stage> stageList = new ArrayList<>();
    public static void newStage(Pane pane,String name) {
        Stage stage = new Stage();
        stage.setTitle(name);
        stage.getIcons().add(new Image("/icon/yt_icon_mono_light.png"));
        stage.setScene(new Scene(pane));

        if(!stageList.isEmpty())
        {
            stage.initOwner(stageList.get(stageList.size()-1));
            stage.setResizable(false);
        }

        stage.setOnCloseRequest(event -> {
            stage.close();
            stageList.remove(stageList.size()-1);
            if(stageList.isEmpty()) Runtime.getRuntime().exit(0);
        });
    }

    // @TODO: make every popups as popup stage
}
