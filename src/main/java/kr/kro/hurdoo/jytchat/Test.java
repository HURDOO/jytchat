package kr.kro.hurdoo.jytchat;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.apache.GoogleApacheHttpTransport;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test {
    public static void main(String accessToken,String refreshToken,long expiredSeconds) {
        try {

            YouTube yt = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), null)
                    .setApplicationName("JYTCHAT")
                    .build();
            //(GoogleNetHttpTransport.newTrustedTransport(), credential.getJsonFactory(), credential.getRequestInitializer());
            System.out.println(yt.liveBroadcasts().list(Collections.singletonList("snippet")).setMine(true).setAccessToken(accessToken).execute().getEtag());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
