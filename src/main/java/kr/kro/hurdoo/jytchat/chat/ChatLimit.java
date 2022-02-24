package kr.kro.hurdoo.jytchat.chat;

import com.github.kusaanko.youtubelivechat.AuthorType;
import com.github.kusaanko.youtubelivechat.ChatItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatLimit {
    private static Thread thread;
    private static ChatPermission permission = ChatPermission.NONE;
    private static TimeCount timeoutCount = TimeCount.NONE;
    private static TimeCount banCount = TimeCount.NONE;
    public static final HashMap<String,String> checkedSchoolID = new HashMap<>(); // youtube -> school
    public static final HashMap<String,Integer> count = new HashMap<>();

    /**
     *
     * @param item chatItem to check
     * @return if permission is NONE, always true / else if the chat author is checked
     */
    public static boolean check(ChatItem item) {
        if(!item.isAuthorOwner() && !item.isAuthorModerator() && !item.getAuthorType().contains(AuthorType.YOUTUBE)) {

            switch (permission) {
                case ADMIN:
                    addCount(item);
                    YTChatSender.write(YTChatSendRequest.YTChatSendType.DELETE_CHAT, item);
                    YTChatSender.write(YTChatSendRequest.YTChatSendType.SEND_MESSAGE,
                            String.format("@%s 지금은 관리자만 채팅할 수 있습니다", item.getAuthorName()));
                    return false;
                case CHECK:
                    if(!checkedSchoolID.containsKey(item.getAuthorChannelID())) {
                        addCount(item);
                        YTChatSender.write(YTChatSendRequest.YTChatSendType.DELETE_CHAT,item);
                        YTChatSender.write(YTChatSendRequest.YTChatSendType.SEND_MESSAGE,
                                String.format("@%s !출석체크 후 채팅해주세요", item.getAuthorName()));
                    }
                    return false;
            }
        }
        return true;
    }

    public static synchronized void addCount(ChatItem item) {
        String author = item.getAuthorChannelID();
        int cnt = count.getOrDefault(author,0) + 1;

        if(cnt >= timeoutCount.num) {
            YTChatSender.write(YTChatSendRequest.YTChatSendType.TIMEOUT_USER, item);
            System.out.printf("Timeout user %s\n", author);
        }
        else if(cnt >= banCount.num) {
            YTChatSender.write(YTChatSendRequest.YTChatSendType.BAN_USER, item);
            System.out.printf("Banned user %s\n", author);
        }
        else
            count.put(author, cnt);
    }

    public static void setPermission(ChatPermission permission) {
        ChatLimit.permission = permission;
        count.clear();
    }
    public static void setTimeoutCount(TimeCount timeoutCount) {
        ChatLimit.timeoutCount = timeoutCount;
    }
    public static void setBanCount(TimeCount banCount) {
        ChatLimit.banCount = banCount;
    }
}

