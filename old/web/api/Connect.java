package kr.kro.hurdoo.jytchat.web.api;

import kr.kro.hurdoo.jytchat.Log;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.logging.Level;

public class Connect {

    /**
     * 인터넷을 통해 특정 링크로 연결합니다.
     * @param type
     * @param url 연결할 링크립니다.
     * @param access_token (선택) 헤더의 Authorization: Bearer에 들어갈 토큰입니다. null이면 헤더를 추가하지 않습니다.
     * @param content_type (선택) 헤더의 Content-Type: 에 들어갈 내용입니다. null이면 헤더를 추가하지 않습니다.
     * @param body (선택) 서버에 보낼 내용입니다. null이면 보내지 않습니다.
     */
    public static void request(ReturnType type, String url, String access_token, String content_type, String body)
    {
        Thread thread = new Thread(() -> connect(type,url,access_token,content_type,body));
        thread.setDaemon(true);
        thread.start();
    }

    private static void connect(ReturnType type, String url, String access_token, String content_type, String body)
    {
        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            if(access_token != null) conn.setRequestProperty("Authorization","Bearer " + access_token);
            if(content_type != null) conn.setRequestProperty("Content-Type",content_type);
            if(body != null)
            {
                conn.setDoOutput(true);
                DataOutputStream stream = new DataOutputStream(conn.getOutputStream());
                stream.writeBytes(body);
                stream.flush();
                stream.close();
            }

            int responseCode = conn.getResponseCode();

            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } catch (IOException e)
            {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String str = "";
            StringBuilder msg = new StringBuilder("\n");
            while((str = reader.readLine()) != null)
            {
                msg.append(str);
            }
            if(responseCode > 299) {
                Log.log(Level.WARNING,"Connecting to " + url + " failed: response code is " + responseCode + msg);
            }
            else
            {
                ReturnDoor.done(type,msg.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum RequestType {
        POST,
        PUT,
        GET
    }
}
