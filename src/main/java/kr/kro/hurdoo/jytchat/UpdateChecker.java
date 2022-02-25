package kr.kro.hurdoo.jytchat;

import com.github.mouse0w0.darculafx.DarculaFX;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private static final String VERSION = "1.11";
    public static void checkUpdate() {
        JsonObject object = new Gson().fromJson(receiveInfo(),JsonObject.class);
        String latest = object.get("version").getAsString();
        if(!latest.equals(VERSION)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("업데이트 가능");
            alert.setHeaderText("JytChat " + latest);
            alert.setContentText("최신 버전을 사용할 수 있습니다.\nhttps://github.com/HURDOO/jytchat에서 업데이트해 주세요.\n현재 버전: " + VERSION + " / 최신 버전: " + latest);

            Pane pane = alert.getDialogPane();
            DarculaFX.applyDarculaStyle(pane);
            pane.setStyle("-fx-font-family: \"NanumBarunGothic\"");

            alert.show();
        }
    }
    private static String receiveInfo() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("http://info.jytchat.kro.kr").openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String s;
            StringBuilder str = new StringBuilder();
            while((s = reader.readLine()) != null) {
                str.append(s);
            }
            return str.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
