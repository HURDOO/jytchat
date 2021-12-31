package kr.kro.hurdoo.jytchat.chat;

public enum ChatPermission {
    NONE("채팅: 누구나 가능"),
    CHECK("채팅: 출석체크 전용"),
    ADMIN("채팅: 관리자 전용");

    public final String name;
    ChatPermission(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
