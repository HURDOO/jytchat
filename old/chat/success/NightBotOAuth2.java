package kr.kro.hurdoo.jytchat.chat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.control.Alert;
import kr.kro.hurdoo.jytchat.Jytchat;
import kr.kro.hurdoo.jytchat.ui.ChatController;
import kr.kro.hurdoo.jytchat.ui.NBOAuth2Controller;
import kr.kro.hurdoo.jytchat.ui.NightBotStage;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NightBotOAuth2 {

    private String state = "JYTCHAT" + new Random().nextInt(999_999);

    public String access_token;
    public String refresh_token;
    public Integer expires_in;

    public String getClientAuthURL()
    {
        /*https://api.nightbot.tv/oauth2/authorize?response_type=code&client_id=d3cfa25e47c9c18e51220e4757d8e57a
        &redirect_uri=https%3A%2F%2Ftesting.com%2Fcallback&scope=commands%20timers*/
        return "https://api.nightbot.tv/oauth2/authorize" +
                "?response_type=code" +
                "&scope=regulars%20channel%20channel_send%20spam_protection" +
                "&redirect_uri=http://localhost:8080/callback" +
                "&client_id=" + Jytchat.nightBotClientId +
                "&state=" +  state;
    }

    private ServerSocket serverSocket;
    private Thread clientThread;

    public void auth() {
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        clientThread = new Thread(this::clientAuth);
        clientThread.start();
    }

    public void stop() {
        if(!clientThread.isAlive()) return;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clientAuth() {
        try {
            String url = "/favicon.ico";
            Socket socket = new Socket();
            while(url.equals("/favicon.ico"))
            {
                try {
                    socket = serverSocket.accept();
                } catch (SocketException e)
                {
                    return;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // GET /callback?code=d01ea7bcd50926a9d2fe61e15b1641eb3a056640 HTTP/1.1
                String get = reader.readLine();
                url = get.split(" ")[1];
            }

            // check state
            String state = url.split("=")[2];
            if(!state.equals(this.state)) throw new SecurityException("state has changed");
            String code = url.split("=")[1].split("&")[0];

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("Content-Type: text/html; charset=UTF-8\r\n\r\n");
            writer.write("<h4>인증이 완료되었습니다. 이 창을 닫으셔도 됩니다!</h4>" +
                    "<script>window.history.replaceState({}, document.title, '/');</script>");
            writer.flush();
            socket.close();
            serverSocket.close();

            /*{
                "access_token": "4fb1fed8889ec9d1c319d5b3c9a54b23",
                "refresh_token": "b98dbbc2e64789532de2c9e7c69b0f89",
                "token_type": "bearer",
                "expires_in": 2592000,
                "scope": "commands timers"
            }*/
            JsonObject object = serverAuth(code);

            access_token = object.get("access_token").getAsString();
            refresh_token = object.get("refresh_token").getAsString();
            expires_in = object.get("expires_in").getAsInt();

            NBOAuth2Controller.instance.done();
        } catch (IOException e)
        {
            e.printStackTrace();
            NBOAuth2Controller.instance.done();
        }
    }

    /*
        "https://api.nightbot.tv/oauth2/token" \
        -d "client_id=d3cfa25e47c9c18e51220e4757d8e57a" \
        -d "client_secret=50951bf21ec9639b210c7fda38665861" \
        -d "grant_type=authorization_code" \
        -d "redirect_uri=https%3A%2F%2Ftesting.com%2Fcallback" \
        -d "code=cfbdb83aaa4d5c2534c23329de35301a" e79892ce8712b57d2710c1edb7ff5670589d4b79
    */
    private JsonObject serverAuth(String code) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://api.nightbot.tv/oauth2/token").openConnection();
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent","Mozilla/5.0");

        conn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes("client_id=" + Jytchat.nightBotClientId +
                "&client_secret=" + Jytchat.nightBotClientSecret +
                "&grant_type=" + "authorization_code" +
                "&redirect_uri=" + "http://localhost:8080/callback" +
                "&code=" + code);
        out.flush();
        out.close();

        int responseCode = conn.getResponseCode();
        if(responseCode > 299)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            throw new IOException("response code is " + responseCode + "\n" +
                    new Gson().fromJson(new BufferedReader(new InputStreamReader(conn.getErrorStream())),JsonObject.class)
            .get("message").getAsString());
        }
        return new Gson().fromJson(new InputStreamReader(conn.getInputStream()), JsonObject.class);
    }
}

    /**
     *
     * @param responseUrl
     * @return token
     * @throws IllegalArgumentException when response url doesn't have its correct form
     * @throws IllegalAccessException when response contains error
     * @throws SecurityException when state does not match
     */
/*
    public String getToken(String responseUrl) throws IllegalArgumentException, IllegalAccessException, SecurityException {
        String[] split = responseUrl.split("#");
        if(split.length != 2) throw new IllegalArgumentException("response url does not contain '#' : " + responseUrl);
        String core = split[1].replace("#","");
        String[] params = core.split("&");
        HashMap<String,String> responseValue = new HashMap<>();
        for(String str : params)
        {
            String[] content = str.split("=");
            if(content.length != 2) throw new IllegalArgumentException("parameter in response url is illegal : " + str);
            responseValue.put(content[0],content[1]);
        }

        if(responseValue.containsKey("error")) throw new IllegalAccessException("returned error : " + responseValue.get("error"));
        if(!responseValue.containsKey("access_token") || !responseValue.containsKey("state")) throw new IllegalArgumentException(
                "no enough parameter; expected access_token and state, but got : " + core);
        String token = responseValue.get("access_token");
        String state = responseValue.get("state");
        if(!state.equals(state)) throw new SecurityException("state does not match; expected '" + state + "', but got: " + state
                + "\nProbably something changed (by hacker?) during connecting to server");
        return token;
    }
 */

/*
    public String authOnceURL()
    {
        return "https://api.nightbot.tv/oauth2/authorize"
                + "?client_id=" + Jytchat.nightBotClientId
                + "&redirect_uri=" + "http://localhost:8080/callback"
                + "&response_type=" + "token"
                + "&scope=" + "regulars%20channel%20channel_send"
                + "&state=" + state;
    }
 */
