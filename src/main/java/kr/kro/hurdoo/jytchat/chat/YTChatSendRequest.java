package kr.kro.hurdoo.jytchat.chat;

public class YTChatSendRequest {
    private final YTChatSendType type;
    private final Object object;

    public YTChatSendRequest(YTChatSendType type, Object object) {
        this.type = type;
        this.object = object;
    }

    public YTChatSendType getType() {
        return type;
    }
    public Object getObject() {
        return object;
    }

    public enum YTChatSendType {
        SEND_MESSAGE,
        DELETE_CHAT,
        TIMEOUT_USER,
        BAN_USER,
        UNBAN_USER
    }
}
