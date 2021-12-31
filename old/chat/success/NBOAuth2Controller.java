package kr.kro.hurdoo.jytchat.ui;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;
import kr.kro.hurdoo.jytchat.Jytchat;
import kr.kro.hurdoo.jytchat.chat.NightBotClient;
import kr.kro.hurdoo.jytchat.chat.NightBotOAuth2;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

public class NBOAuth2Controller implements Initializable {

    public static NBOAuth2Controller instance;

    @FXML TextField url;
    @FXML Button browser;
    @FXML Button copy;
    @FXML Text text;

    private volatile boolean ing = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setWeb();
    }

    Timeline timeline;
    public void setWeb() {
        NightBotOAuth2 nb = Jytchat.nbAuth = new NightBotOAuth2();
        String link = nb.getClientAuthURL();
        nb.auth();
        url.setText(link);
        browser.setOnAction(event -> {
            openBrowser(link);
        });
        copy.setOnAction(event -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(link),null);
        });
        openBrowser(link);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1),event -> {
            if(!ing)
            {
                ing = true;
                if(nb.access_token == null || nb.refresh_token == null || nb.expires_in == null)
                {
                    Jytchat.nbAuth = null;
                    alertError("NightBot 연동 중 오류가 발생하였습니다.");
                }

                text.setText("채널 정보 불러오는 중...");
                browser.setDisable(true);
                copy.setDisable(true);

                // get channel
                NightBotClient client = new NightBotClient(nb);
                try {
                    JsonObject object = client.sendRequest(NightBotClient.ConnectionMethod.GET,
                            "https://api.nightbot.tv/1/channel",null);
                    boolean joined = object.get("channel").getAsJsonObject().get("joined").getAsBoolean();
                    if(!joined)
                    {
                        try {
                            JsonObject object1 = client.sendRequest(NightBotClient.ConnectionMethod.POST,
                                    "https://api.nightbot.tv/1/channel/join", null);
                            int status = object.get("status").getAsInt();
                            if (status != 200) throw new IOException("status is " + status);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                            alertError("채널에 접속하던 중 오류가 발생하였습니다.\n" + e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    alertError("채널 정보를 불러오는 중 오류가 발생했습니다.\n" + e.getMessage());
                    return;
                }

                text.setText("스팸 방지 필터 설정하는 중...");

                boolean enabled;
                String blacklist;
                String exemptUserLevel;
                String message;

                try {
                    JsonObject object1 = client.sendRequest(NightBotClient.ConnectionMethod.GET,
                            "https://api.nightbot.tv/1/spam_protection/blacklist",null);
                    JsonObject object = object1.get("filter").getAsJsonObject();
                    enabled = object.get("enabled").getAsBoolean();
                    blacklist = object.get("blacklist").getAsString();
                    exemptUserLevel = object.get("exemptUserLevel").getAsString();
                    message = object.get("message").getAsString();
                } catch (IOException | NullPointerException e)
                {
                    e.printStackTrace();
                    alertError("스팸 방지 필터를 불러오는 중 오류가 발생하였습니다.\n" + e.getMessage());
                    return;
                }

                try {
                    String var = "";
                    if(!enabled) var += "&enabled=true";
                    if(!blacklist.equals("*")) var += "&blacklist=*";
                    if(!(exemptUserLevel.equals("moderator") || exemptUserLevel.equals("regular")
                    || exemptUserLevel.equals("everyone")))
                    {
                        var += "&exemptUserLevel=everyone";
                        exemptUserLevel = "everyone";
                    }
                    if(message.equals("")) var += "&message=!출석체크 학번 이름 - 명령어 입력 후 사용해주세요!";
                    if(!var.equals(""))
                    {
                        var = var.substring(1, var.length() - 1);
                        System.out.println("changed: " + var);
                        JsonObject object = client.sendRequest(NightBotClient.ConnectionMethod.PUT,
                                "https://api.nightbot.tv/1/spam_protection/blacklist", var);
                        int status = object.get("status").getAsInt();
                        if (status != 200)
                            throw new IOException("status is " + status);
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                    alertError("스팸 방지 필터를 설정하는 중 오류가 발생하였습니다.\n" + e.getMessage());
                } finally {
                    switch (exemptUserLevel)
                    {
                        case "everyone":
                            Jytchat.nbChat = Jytchat.NightBotChat.EVERYONE;
                            break;
                        case "regular":
                            Jytchat.nbChat = Jytchat.NightBotChat.REGULAR;
                            break;
                        case "moderator":
                            Jytchat.nbChat = Jytchat.NightBotChat.MODERATOR;
                            break;
                        default:
                            throw new IllegalArgumentException("exemptUserLevel is " + exemptUserLevel);
                    }
                }

                NightBotStage.close();
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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
    public void done()
    {
        ing = false;
    }
    private void alertError(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
        ChatController.instance.stopNightBot();
        NightBotStage.close();
    }
}

