package kr.kro.hurdoo.jytchat.web.api;

import kr.kro.hurdoo.jytchat.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class LocalHost {
    public static final int PORT = 5678;
    private static ServerSocket serverSocket;

    static {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * localhost:5678 소켓을 활성화합니다.
     * 주로 계정 연동 과정에서 코드를 받아올 때 사용됩니다.
     * 브라우저에서 접속한 링크를 그대로 java.lang.String 형태로 반환합니다.
     * @param type
     */
    public static void start(ReturnType type) {
        assert serverSocket != null;

        Thread thread = new Thread(() -> startSocket(type));
        thread.setDaemon(true);
        thread.start();
    }

    private static void startSocket(ReturnType type)
    {
        // get url
        Socket socket;
        String url;
        while(true)
        {
            System.out.println("a");
            try {
                socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str = reader.readLine();
                url = str.split(" ")[1];
                if(!url.equals("/favicon.ico")) break; // favicon.ico 요청이 먼저 들어오는 경우가 있음
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // print out
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(BROWSER_MESSAGE);
            writer.flush();
            writer.close();
        } catch (IOException e)
        {
            Log.log(Level.WARNING, "Failed to send message to client");
            e.printStackTrace();
        }

        // return
        ReturnDoor.done(type,url);
    }

    private static final String BROWSER_MESSAGE = "HTTP/1.1 200 OK\r\n\r\n" +
            "Content-Type: text/html; charset=UTF-8\r\n\r\n" +
            "<h4>인증이 완료되었습니다. 이 창을 닫으셔도 됩니다!</h4>\r\n\r\n" +
            "<script>window.history.replaceState({}, document.title, '/'); window.close();</script>";
}
