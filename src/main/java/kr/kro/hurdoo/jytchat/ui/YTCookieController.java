package kr.kro.hurdoo.jytchat.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import kr.kro.hurdoo.jytchat.chat.YTChat;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class YTCookieController implements Initializable {

    @FXML Button cookieHelp;
    @FXML Button chromeButton;
    @FXML Button youtubeButton;
    @FXML Button save;

    @FXML TextField APISID;
    @FXML TextField HSID;
    @FXML TextField LOGIN_INFO;
    @FXML TextField SAPISID;
    @FXML TextField SID;
    @FXML TextField SSID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cookieHelp.setOnAction(event -> openBrowser("localhost"));
        chromeButton.setOnAction(event -> openBrowser("chrome://settings/cookies/detail?site=youtube.com"));
        youtubeButton.setOnAction(event -> openBrowser("https://www.youtube.com"));

        // @TODO: explorer opens / chat.update() stops

        save.setOnAction(event -> {
            YTChat.setUserData(
                    APISID.getText(),
                    HSID.getText(),
                    LOGIN_INFO.getText(),
                    SAPISID.getText(),
                    SID.getText(),
                    SSID.getText()
            );
            MainController.instance.reloadUsername();
        });
    }

    private void openBrowser(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
