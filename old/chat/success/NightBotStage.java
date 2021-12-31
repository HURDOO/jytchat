package kr.kro.hurdoo.jytchat.ui;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import kr.kro.hurdoo.jytchat.Jytchat;

import java.io.IOException;

public class NightBotStage {

    private static Stage stage;

    public static void open()
    {
        stage = new Stage();
        stage.initOwner(UIMain.mainStage);
        stage.setResizable(false);
        stage.setTitle("NightBot 연동");

        FXMLLoader loader = new FXMLLoader(UIMain.class.getResource("/old/chat/success/NightBotOAuth2.fxml"));
        AnchorPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        DarculaFX.applyDarculaStyle(scene);
        NBOAuth2Controller.instance = loader.getController();

        stage.setOnHiding(event -> close());

        stage.show();
    }
    public static void close()
    {
        stage.close();
        if(Jytchat.nbAuth != null && Jytchat.nbAuth.access_token == null)
        {
            Jytchat.nbAuth.stop();
            ChatController.instance.stopNightBot();
        }
        if(Jytchat.nbAuth == null)
        {
            ChatController.instance.stopNightBot();
        }
    }
}
