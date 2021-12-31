package kr.kro.hurdoo.jytchat.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kr.kro.hurdoo.jytchat.web.api.Connect;
import kr.kro.hurdoo.jytchat.web.api.LocalHost;
import kr.kro.hurdoo.jytchat.web.api.ReturnType;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class GoogleAuth {
    public static GoogleAuth instance;
    public static final String CLIENT_ID = "770038829616-lsip9np936ipb73lmklrl7hf8ogsk51s.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "lK9c6FBFemZebV125z5RXczt";
    private static final String SCOPE = ("https://www.googleapis.com/auth/youtube.force-ssl "
            + "https://www.googleapis.com/auth/youtube.readonly").replace(" ","%20");

    private String code;
    public String access_token;
    public String refresh_token;
    public Long expires_in;

    private final String state;
    private final String code_verifier; // origin
    private final String code_challenge; // encoded

    public GoogleAuth()
    {
        instance = this;
        code_verifier = genCodeVerifier();
        code_challenge = base64_sha256_encode(code_verifier);
        state = "JYTCHAT" + (new Random().nextInt(899999)+100000);
        System.out.println(code_verifier);
        System.out.println(code_challenge);
    }

    public void startAuth() {
        LocalHost.start(ReturnType.GOOGLE_AUTH_USER);
    }
    public void startTokenRequest() {
        Connect.request(ReturnType.GOOGLE_AUTH_SERVER,"https://oauth2.googleapis.com/token", null, "application/x-www-form-urlencoded",
                "code=" + code
                + "&client_id=" + CLIENT_ID
                + "&client_secret=" + CLIENT_SECRET
                + "&redirect_uri=" + "http://localhost:" + LocalHost.PORT
                + "&code_verifier=" + code_verifier
                + "&grant_type=" + "authorization_code"
        );
    }

    public String getUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + "http://localhost:5678"
                + "&response_type=" + "code"
                //+ "&scope=" + "https://www.googleapis.com/auth/spreadsheets.readonly"
                + "&scope=" + SCOPE
                + "&code_challenge=" + code_challenge
                + "&code_challenge_method=" + "S256"
                + "&state=" + state;
    }
    private String genCodeVerifier()
    {
        int length = new Random().nextInt(85)+43;
        byte[] code = new byte[length];
        new SecureRandom().nextBytes(code);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }
    private String base64_sha256_encode(String code)
    {
        try {
            byte[] bytes = code.getBytes(StandardCharsets.US_ASCII);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes, 0, bytes.length);
            byte[] digest = messageDigest.digest();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        /*byte[] sha256 = DigestUtils.sha256Hex(code).getBytes(StandardCharsets.US_ASCII);
        String base64 = Base64.encodeBase64URLSafeString(sha256);
        return base64;*/
        return null;
    }

    /*
        http://127.0.0.1:5678/?error=access_denied&state=JYTCHAT501508
        http://127.0.0.1:5678/?state=JYTCHAT501508&code=4/0AX4XfWjUUwNvfRohdLKjoMO8CoE2kPWbZe0mJy61722HgPgcewURQY-Laia9ddtu3vSzrQ&scope=https://www.googleapis.com/auth/spreadsheets.readonly
    */
    public void parseCode(String url) {
        String argStr = url.replace("/?","");
        String[] args = argStr.split("&");
        JsonObject object = new JsonObject();
        for(String arg : args) {
            String[] ss = arg.split("=");
            object.addProperty(ss[0],ss[1]);
        }

        if(object.get("error") != null) {
            // @TODO: fail
            System.out.println("error");
            return;
        }
        String state = object.get("state").getAsString();
        if(!state.equals(this.state)) {
            // @TODO: security fail
            System.out.println("security");
            return;
        }
        String scope = object.get("scope").getAsString();
        if(!scope.equals(SCOPE)) {
            // @TODO: permission fail
            System.out.println("permission");
            return;
        }

        code = object.get("code").getAsString();
    }

    /*
        "access_token": "*",
        "expires_in": 3599,
        "refresh_token": "*",
        "scope": "https://www.googleapis.com/auth/spreadsheets.readonly",
        "token_type": "Bearer"
    */
    public void parseAccessToken(String response) {
        JsonObject object = new Gson().fromJson(response,JsonObject.class);
        if(object.get("error") != null) {
            System.out.println("error");
            // @TODO: error
        }

        String scope = object.get("scope").getAsString();
        if(!scope.equals(SCOPE)) {
            System.out.println("permission error");
            // @TODO: permission error
        }

        access_token = object.get("access_token").getAsString();
        refresh_token = object.get("refresh_token").getAsString();
        expires_in = object.get("expires_in").getAsLong();
    }
}
