package kr.kro.hurdoo.jytchat.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import kr.kro.hurdoo.jytchat.chat.YTChat;
import kr.kro.hurdoo.jytchat.config.CookieConverter;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YTCookieController implements Initializable {

    public static YTCookieController instance;

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
        cookieHelp.setOnAction(event -> openBrowser("https://github.com/HURDOO/jytchat/blob/master/cookie_guide/cookie_guide_ko.md"));
        chromeButton.setOnAction(event -> openBrowser("chrome://settings/cookies/detail?site=youtube.com"));
        youtubeButton.setOnAction(event -> openBrowser("https://www.youtube.com"));

        // @TODO: explorer opens / chat.update() stops

        save.setOnAction(event -> {
            Map<String,String> map;
            String[] list = new String[]{"APISID","HSID","LOGIN_INFO","SAPISID","SID","SSID"};
            TextField[] uiList = new TextField[]{APISID,HSID,LOGIN_INFO,SAPISID,SID,SSID};

            if(!allCookies.getText().equals("")) {
                map = new CookieConverter().convertToModel(allCookies.getText());
            }
            else {
                map = new HashMap<>();
                for(int i=0;i<6;i++) {
                    map.put(list[i],uiList[i].getText());
                }
            }

            YTChat.setUserData(map);
            YTCookieStage.hide();
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
