package kr.kro.hurdoo.jytchat.chat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NightBotClient {
    NightBotOAuth2 auth;
    public NightBotClient(NightBotOAuth2 auth) {
        assert auth != null;
        this.auth = auth;
    }

    public JsonObject sendRequest(ConnectionMethod method,String url,String var) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(method.toString());
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Authorization","Bearer " + auth.access_token);

        if(var != null) {
            conn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(var);
            out.flush();
            out.close();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode > 299) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            throw new IOException("response code is " + responseCode + "\n" +
                    new Gson().fromJson(new BufferedReader(new InputStreamReader(conn.getErrorStream())), JsonObject.class)
                            .get("message").getAsString());
        }
        return new Gson().fromJson(new InputStreamReader(conn.getInputStream()), JsonObject.class);
    }
    public enum ConnectionMethod {
        GET,
        POST,
        PUT;
    }
}
