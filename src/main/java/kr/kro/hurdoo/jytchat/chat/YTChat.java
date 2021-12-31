package kr.kro.hurdoo.jytchat.chat;

import com.github.kusaanko.youtubelivechat.ChatItem;
import com.github.kusaanko.youtubelivechat.IdType;
import com.github.kusaanko.youtubelivechat.YouTubeLiveChat;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import kr.kro.hurdoo.jytchat.Jytchat;
import kr.kro.hurdoo.jytchat.ui.MainController;
import kr.kro.hurdoo.jytchat.ui.UIMain;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class YTChat {

    private static Thread thread = new Thread(YTChat::loop);
    private static YouTubeLiveChat chat;
    private static boolean connect;

    private static void loop()
    {
        while(connect)
        {
            try {
                chat.update();
                for(ChatItem item : chat.getChatItems())
                {
                    if(chat == null) break;
                    String msg = format.format(new Date(item.getTimestamp() / 1000))
                            + " " + item.getType()
                            + item.getAuthorType() + " "
                            + "[" + item.getAuthorName() + "]"
                            + item.getMessage();
                    MainController.instance.write(msg);
                    FileSaver.write(msg);
                    Checker.write(item);
                    ChatLimit.write(item);

                    //System.out.println(item);
                }

                Thread.sleep(1000);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                break;
            } catch (NullPointerException ignored) {}
        }
    }

    public static boolean start()
    {
        try {
            try {
                chat = new YouTubeLiveChat(Jytchat.videoId, false, IdType.VIDEO);
            } catch (IllegalArgumentException e)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("실시간 채팅에 접근할 수 없습니다.");
                alert.setContentText("입력한 영상 ID가 잘못되었거나, 스트리밍이 아닐 수 있습니다.\n" + Jytchat.videoId);
                alert.initOwner(UIMain.mainStage);
                alert.show();
                return false;
            }
            chat.setLocale(Locale.KOREA);
            connect = true;

            thread.start();
        } catch (IOException | IllegalThreadStateException e) {
            e.printStackTrace();
            return false;
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(new Pane(new ImageView("http://img.youtube.com/vi/" + chat.getVideoId() + "/mqdefault.jpg"))));
        // http://img.youtube.com/vi/<insert-youtube-video-id-here>/default.jpg
        stage.setResizable(false);
        stage.setTitle("스트리밍 연동됨");
        stage.initOwner(UIMain.mainStage);
        stage.setOnHiding(event -> stage.close());
        stage.show();
        return true;
    }

    public static void stop()
    {
        connect = false;
        chat = null;

        thread = new Thread(YTChat::loop);
    }

    public static void setUserData(String APISID, String HSID, String LOGIN_INFO, String SAPISID, String SID, String SSID) {
        chat.setUserData(SAPISID,HSID,SSID,APISID,SID,LOGIN_INFO);
    }

    public static void sendChat(String message) {
        try {
            chat.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUsername() {
        return chat == null ? null : chat.getSignedUserName();
    }

    public static void deleteChat(ChatItem item) throws IOException {
        chat.deleteChat(item);
    }


    /*
    * "ao4kVyLkoZrJ_FXo/AKDsju-fWWzD0mc5k","AuCye_9yqhIJ65KP1",
                "Axcg16I1u-2tvHAkJ","zg22udtZ7wPqXG_z/AmxgaD2M61qvnZkOx",
                "FAh7nko5m9mKav4zj1zkolq6yyaEuUOVobAM1I7qObKbwieV2fqBrkXycZDqrO4mGy-DYg.",
                "AFmmF2swRQIgVro3aSQ1FqkuTNzhMKneaFA0-cDKsiDFjpRVaGzVTF0CIQC7UTgbQVBIllsg4wM2v26zY0nD8YkTUqYv8lmG5PpnGQ:QUQ3MjNmd3NQQUpvUThaUE5DQWlTa0hqYVMzdGdDNlRYeG96dDBFUFFCdTdoeUxreXZtbFV4eDMxZXdpcDZnNktudmZuR2ZhdzhhRTlZNE5jYkdwR0Y3VHpZVXF6VEFMbG14anAydHpkZXNhNVVmT2d4NnhLRXUzWl9RN0ctdkk5MFNhWGZMemhDQUstNkUtaWFGMlZ0aWdFdW1NVk9LSUhpVkFFVHBGdmpuNDRublI2ZkpsbVRZR2tUZ29vcTR2WHFzMmp5WFc1N3FWcWhoUWJFTjJuX1puQlNFMTNvbEl3UQ=="*/

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
}
