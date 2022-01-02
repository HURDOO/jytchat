package kr.kro.hurdoo.jytchat.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import kr.kro.hurdoo.jytchat.chat.YTChat;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @FXML TextArea allCookies;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cookieHelp.setOnAction(event -> openBrowser("localhost"));
        chromeButton.setOnAction(event -> openBrowser("chrome://settings/cookies/detail?site=youtube.com"));
        youtubeButton.setOnAction(event -> openBrowser("https://www.youtube.com"));

        // @TODO: explorer opens / chat.update() stops

        save.setOnAction(event -> {
            if(!allCookies.getText().equals("")) {
                String all = ";" + allCookies.getText() + ";";
                String[] list = new String[]{"APISID","HSID","LOGIN_INFO","SAPISID","SID","SSID"};
                TextField[] uiList = new TextField[]{APISID,HSID,LOGIN_INFO,SAPISID,SID,SSID};

                for(int i=0;i<6;i++) {
                    Pattern pattern = Pattern.compile("[^\\w]" + list[i] + "=([^;]*);");
                    Matcher matcher = pattern.matcher(all);
                    if(matcher.find()) uiList[i].setText(matcher.group(1));
                    else System.out.println("Cannot find cookie " + list[i]);
                }
            }
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
