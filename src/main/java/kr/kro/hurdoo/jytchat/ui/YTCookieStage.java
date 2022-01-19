package kr.kro.hurdoo.jytchat.ui;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class YTCookieStage {

    private static Stage stage = null;
    public static void show() {
        if(stage == null) load();
        stage.show();
    }
    public static void hide() {
        stage.hide();
    }
    private static void load() {
        stage = new Stage();

        stage.initOwner(UIMain.mainStage);
        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(event -> hide());

        try {
            FXMLLoader loader = new FXMLLoader(YTCookieStage.class.getResource("/fxml/ytcookie.fxml"));
            BorderPane pane = loader.load();
            YTCookieController.instance = loader.getController();
            stage.setScene(new Scene(pane));

            pane.setStyle("-fx-font-family: \"NanumBarunGothic\"");
            DarculaFX.applyDarculaStyle(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
