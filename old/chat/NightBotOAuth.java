package old_java.chat;

import com.github.scribejava.core.builder.ScopeBuilder;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import kr.kro.hurdoo.jytchat.Jytchat;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class NightBotOAuth {

    private OAuth20Service service;
    private final String secretState = "Jytchat" + new Random().nextInt(999_999);

    public String getAccessTokenURL() // TODO: https://github.com/scribejava/scribejava
    {
        String API_KEY = "639c2b0bef61466190360c713014496e";
        String API_SECRET = "881380894996dd6794168377571339bc1c1503998542bdc167928df3fd02e0d3";
        service = new ServiceBuilder(API_KEY)
                .apiSecret(API_SECRET)
                .callback("https://localhost:8080/callback")
                .defaultScope(new ScopeBuilder().withScopes("channel","channel_send","regulars").build())
                .responseType("code")
                .build(NightBotApi20.instance());

        String url = service.getAuthorizationUrl(secretState);
        return url;
    }

    /**
     * @throws IllegalArgumentException when response url doesn't have its correct form
     * @throws IllegalAccessException when response contains error
     * @throws IOException by library
     * @throws ExecutionException by library
     * @throws InterruptedException by library
     */

    public String getTokenURL(String responseUrl) throws IllegalArgumentException, IllegalAccessException, IOException, ExecutionException, InterruptedException {
        String[] split = responseUrl.split("\\?");
        if(split.length != 2) throw new IllegalArgumentException("response url does not contain '?' : " + responseUrl);
        String core = split[1].replace("?","");
        String[] params = core.split("&");
        HashMap<String,String> responseValue = new HashMap<>();
        for(String str : params)
        {
            String[] content = str.split("=");
            if(content.length != 2) throw new IllegalArgumentException("parameter in response url is illegal : " + str);
            responseValue.put(content[0],content[1]);
        }

        if(responseValue.containsKey("error")) throw new IllegalAccessException("returned error : " + responseValue.get("error"));
        if(!responseValue.containsKey("code") || !responseValue.containsKey("state")) throw new IllegalArgumentException(
                "no enough parameter; expected code and state, but got : " + core);
        String code = responseValue.get("code");
        String state = responseValue.get("state");
        if(!state.equals(secretState)) throw new SecurityException("state does not match; expected '" + secretState + "', but got: " + state
                + "\nProbably something changed (by hacker?) during connecting to server");
        OAuth2AccessToken token = service.getAccessToken(code);
        OAuthRequest request = new OAuthRequest(Verb.POST,"https://api.nightbot.tv/oauth2/token");
        service.signRequest(token,request);

        try (Response response = service.execute(request)) {
            System.out.println("Got it! Lets see what we found...");
            System.out.println();
            System.out.println(response.getCode());
            System.out.println(response.getBody());
        }
        return "";
    }



    // NightBotOAuth2.java

    public void getToken3(String token) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://api.nightbot.tv/oauth2/token").openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type","application/json");

        JsonObject json = new JsonObject();
        json.addProperty("client_id",Jytchat.nightBotClientId);
        json.addProperty("client_secret",Jytchat.nightBotClientSecret);
        json.addProperty("code",token);
        json.addProperty("grant_type","authorization_code");
        json.addProperty("redirect_uri","https://localhost:8080/callback");

        conn.setDoOutput(true);
        OutputStream stream = conn.getOutputStream();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        stream.write(gson.toJson(json).getBytes(StandardCharsets.UTF_8));
        System.out.println(gson.toJson(json));
        stream.flush();
        stream.close();

        int responseCode = conn.getResponseCode();
        if(responseCode == 200)
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb);
        }
        else
        {
            System.out.println(responseCode + " " + conn.getResponseMessage());
        }
    }

    public String getToken(String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        JsonObject json = new JsonObject();
        json.addProperty("client_id",Jytchat.nightBotClientId);
        json.addProperty("client_secret",Jytchat.nightBotClientSecret);
        json.addProperty("code",token);
        json.addProperty("grant_type","authorization_code");
        json.addProperty("redirect_uri","https://localhost:8080/callback");

        Request request = new Request.Builder()
                .url("https://api.nightbot.tv/oauth2/token")
                .post(RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"))).build();
        //.addHeader("client_id",Jytchat.nightBotClientId)
        //.addHeader("client_secret",Jytchat.nightBotClientSecret)
        //.addHeader("code",token)
        //.addHeader("grant_type","authorization_code")
        //.addHeader("redirect_uri","https://localhost:8080/callback")
        //.build();
        okhttp3.Response response = client.newCall(request).execute();
        String result = response.body().string();
        System.out.println(result);

        JsonObject object = gson.fromJson(result,JsonObject.class);
        return object.get("access_token").toString();
    }

    public String getCode() throws IOException {
        LocalServerReceiver receiver = new LocalServerReceiver("localhost",8080,"/callback",
                null,null);
        System.out.println(receiver.getCallbackPath() + "\n" + receiver.getRedirectUri());
        return receiver.waitForCode();
    }

    public String getToken2(String token) throws IOException, ExecutionException, InterruptedException {
        OAuth20Service service = new ServiceBuilder(Jytchat.nightBotClientId)
                .apiSecret(Jytchat.nightBotClientSecret)
                .callback("http://localhost:8080/callback")
                .build(NightBotApi20.instance());
        service.getAccessToken(token);
        OAuthRequest request = new OAuthRequest(Verb.POST,"https://api.nightbot.tv/oauth2/token");
        service.signRequest(token,request);

        try (Response response = service.execute(request)) {
            System.out.println("Got it! Lets see what we found...");
            System.out.println();
            System.out.println(response.getCode());
            System.out.println(response.getBody());
        }
        return null;
    }

    public String createAccessTokenURL()
    {
        return "https://api.nightbot.tv/oauth2/authorize" +
                "?response_type=" + "code" +
                "&client_id=" + Jytchat.nightBotClientId +
                "&redirect_uri=" + "https%3A%2F%2Flocalhost%3A8080%2Fcallback" +
                "&scope=" + "regulars" + "%20channel" + "%20channel_send" +
                "&state=" + secretState;
    }
}
