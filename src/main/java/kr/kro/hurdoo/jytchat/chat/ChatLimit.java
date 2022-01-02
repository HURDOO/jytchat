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
    public static final HashMap<String,String> checkedSchoolID = new HashMap<>(); // youtube -> school

    /**
     *
     * @param item chatItem to check
     * @return if permission is NONE, always true / else if the chat author is checked
     */
    public static boolean check(ChatItem item) {
        if(!item.isAuthorOwner() && !item.isAuthorModerator() && !item.getAuthorType().contains(AuthorType.YOUTUBE)) {

            switch (permission) {
                case ADMIN:
                    YTChatSender.write(YTChatSendRequest.YTChatSendType.DELETE_CHAT, item);
                    return false;
                case CHECK:
                    if(!checkedSchoolID.containsKey(item.getAuthorChannelID())) {
                        YTChatSender.write(YTChatSendRequest.YTChatSendType.DELETE_CHAT,item);
                        YTChatSender.write(YTChatSendRequest.YTChatSendType.SEND_MESSAGE,
                                String.format("@%s !출석체크 후 채팅해주세요", item.getAuthorName()));
                    }
                    return false;
            }
        }
        return true;
    }
    public static void setPermission(ChatPermission permission) {
        ChatLimit.permission = permission;
    }
}

