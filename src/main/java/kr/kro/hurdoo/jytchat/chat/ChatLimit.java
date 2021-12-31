package kr.kro.hurdoo.jytchat.chat;

import com.github.kusaanko.youtubelivechat.AuthorType;
import com.github.kusaanko.youtubelivechat.ChatItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatLimit {
    private static Thread thread;
    private static ChatPermission permission;
    public static final HashMap<String,String> checkedSchoolID = new HashMap<>(); // youtube -> school
    private static final List<ChatItem> items = new ArrayList<>();

    private static void loop() {
        while(permission != ChatPermission.NONE) {
            while(!items.isEmpty()) {
                ChatItem item = items.get(0);
                items.remove(0);

                if(!item.isAuthorOwner() && !item.isAuthorModerator() && !item.getAuthorType().contains(AuthorType.YOUTUBE)) {

                    switch (permission) {
                        case ADMIN:
                            try {
                                YTChat.deleteChat(item);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case CHECK:
                            if(!checkedSchoolID.containsKey(item.getAuthorChannelID())) {
                                try {
                                    YTChat.deleteChat(item);
                                    YTChat.sendChat(String.format("@%s !출석체크 후 채팅해주세요", item.getAuthorName()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void write(ChatItem item) {
        items.add(item);
    }

    public static void start() {
        items.clear();
        thread = new Thread(ChatLimit::loop);
        thread.start();
    }

    public static void stop() {
        permission = ChatPermission.NONE;
    }

    public static void setPermission(ChatPermission permission) {
        ChatLimit.permission = permission;
    }
}

