package kr.kro.hurdoo.jytchat.ui;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import kr.kro.hurdoo.jytchat.Jytchat;
import kr.kro.hurdoo.jytchat.chat.Checker;
import kr.kro.hurdoo.jytchat.chat.FileSaver;
import kr.kro.hurdoo.jytchat.chat.YTChat;

import java.util.Objects;

public class UIMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage mainStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(UIMain.class.getResource("/fxml/main.fxml"));
        BorderPane pane = loader.load();
        MainController.instance = loader.getController();
        Scene scene = new Scene(pane);
        DarculaFX.applyDarculaStyle(scene);
        Font font = Font.loadFont(UIMain.class.getResourceAsStream("/font/NanumBarunGothic.ttf"),10);
        System.out.println(font.getFamily());
        pane.setStyle("-fx-font-family: \"NanumBarunGothic\"");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Main.controller = loader.getController();
        //Main.controller.start();

        //NewFileStage.stage = new NewFileStage(primaryStage);

        primaryStage.setOnHidden(event -> {
            YTChat.stop();
            FileSaver.stop();
            Checker.stop();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Runtime.getRuntime().exit(0);
        });
    }
}
