package kr.kro.hurdoo.jytchat.chat;

import com.github.kusaanko.youtubelivechat.ChatItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YTChatSender {

    private static final List<YTChatSendRequest> list = new ArrayList<>();

    private static void loop() {
        while (YTChat.connect) {
            while(!list.isEmpty()) {
                YTChatSendRequest req = list.get(0);
                list.remove(0);

                Object object = req.getObject();
                try {
                    switch (req.getType()) {
                        case SEND_MESSAGE:
                            YTChat.chat.sendMessage((String) object);
                            break;
                        case DELETE_CHAT:
                            ((ChatItem) object).delete();
                            break;
                        case TIMEOUT_USER:
                            ((ChatItem) object).timeoutAuthor();
                            break;
                        case BAN_USER:
                            ((ChatItem) object).banAuthor();
                            break;
                        case UNBAN_USER:
                            ((ChatItem) object).unbanAuthor();
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void start() {
        Thread thread = new Thread(YTChatSender::loop);
        thread.setDaemon(true);
        thread.start();
    }

    public static void write(YTChatSendRequest.YTChatSendType type,Object object) {
        list.add(new YTChatSendRequest(type,object));
    }
}
