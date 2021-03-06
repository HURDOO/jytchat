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
import java.util.Map;

public class YTChat {

    private static Thread thread = new Thread(YTChat::loop);
    public static YouTubeLiveChat chat;
    public static boolean connect;
    private static Map<String,String> cookies;

    private static void loop()
    {
        long timestamp = 0L;
        while(connect)
        {
            try {
                chat.update();
                for(ChatItem item : chat.getChatItems())
                {
                    if(chat == null) break;
                    if(item.getTimestamp() < timestamp) break;
                    timestamp = item.getTimestamp();

                    Checker.check(item);
                    ChatLimit.check(item);

                    String msg = format.format(new Date(item.getTimestamp() / 1000))
                            + " " + item.getType()
                            + item.getAuthorType() + " "
                            + "[" + item.getAuthorName() + "]"
                            + item.getMessage();

                    FileSaver.write(msg);
                    MainController.instance.write(msg);
                }

                Thread.sleep(1000);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                break;
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean start()
    {
        try {
            try {
                chat = new YouTubeLiveChat(YouTubeLiveChat.getVideoIdFromURL(Jytchat.videoId), false, IdType.VIDEO);
            } catch (IllegalArgumentException e)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("????????? ????????? ????????? ??? ????????????.");
                alert.setContentText("????????? ?????? ID??? ??????????????????, ??????????????? ?????? ??? ????????????.\n" + Jytchat.videoId);
                alert.initOwner(UIMain.mainStage);
                alert.show();
                return false;
            }
            chat.setLocale(Locale.KOREA);
            connect = true;
            if(cookies != null) chat.setUserData(cookies);

            thread.start();
            YTChatSender.start();
        } catch (IOException | IllegalThreadStateException e) {
            e.printStackTrace();
            return false;
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(new Pane(new ImageView("http://img.youtube.com/vi/" + chat.getVideoId() + "/mqdefault.jpg"))));
        // http://img.youtube.com/vi/<insert-youtube-video-id-here>/default.jpg
        stage.setResizable(false);
        stage.setTitle("???????????? ?????????");
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

    public static void setUserData(Map<String,String> cookies) {
        YTChat.cookies = cookies;
        if(chat != null) {
            if(cookies == null) chat.setUserData(null,null,null,null,null,null);
            else chat.setUserData(cookies);
        }
    }

    public static void sendChat(String message) {
        try {
            chat.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    * "ao4kVyLkoZrJ_FXo/AKDsju-fWWzD0mc5k","AuCye_9yqhIJ65KP1",
                "Axcg16I1u-2tvHAkJ","zg22udtZ7wPqXG_z/AmxgaD2M61qvnZkOx",
                "FAh7nko5m9mKav4zj1zkolq6yyaEuUOVobAM1I7qObKbwieV2fqBrkXycZDqrO4mGy-DYg.",
                "AFmmF2swRQIgVro3aSQ1FqkuTNzhMKneaFA0-cDKsiDFjpRVaGzVTF0CIQC7UTgbQVBIllsg4wM2v26zY0nD8YkTUqYv8lmG5PpnGQ:QUQ3MjNmd3NQQUpvUThaUE5DQWlTa0hqYVMzdGdDNlRYeG96dDBFUFFCdTdoeUxreXZtbFV4eDMxZXdpcDZnNktudmZuR2ZhdzhhRTlZNE5jYkdwR0Y3VHpZVXF6VEFMbG14anAydHpkZXNhNVVmT2d4NnhLRXUzWl9RN0ctdkk5MFNhWGZMemhDQUstNkUtaWFGMlZ0aWdFdW1NVk9LSUhpVkFFVHBGdmpuNDRublI2ZkpsbVRZR2tUZ29vcTR2WHFzMmp5WFc1N3FWcWhoUWJFTjJuX1puQlNFMTNvbEl3UQ=="*/

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
}
