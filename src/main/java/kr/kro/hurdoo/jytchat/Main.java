package kr.kro.hurdoo.jytchat;

import com.github.kusaanko.youtubelivechat.YouTubeLiveChat;
import kr.kro.hurdoo.jytchat.ui.UIMain;

import java.io.*;

public class Main {

    private static YouTubeLiveChat chat = null;
    public static void main(String[] args) throws IOException {

        /*GoogleAuth auth = new GoogleAuth();
        System.out.println(auth.getUrl());
        auth.startAuth();
        while(true) {
            try {
                Thread.sleep(1000);
                ReturnDoor.loop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        Thread thread = new Thread(() -> UIMain.main(args));
        thread.start();

        //YTChat.start();

        //YouTubeLiveChat chat;
        /*new Thread(() -> {
            try {
                chat = new YouTubeLiveChat("F4aby5WN1Rw", false, IdType.VIDEO);
                chat.setLocale(Locale.KOREA);
                System.out.println("completing");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            if(chat != null) break;
        }
        System.out.println("completed");
        while (true) {
            chat.update();
            for (ChatItem item : chat.getChatItems()) {
                System.out.println(item.getMessage());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
}
