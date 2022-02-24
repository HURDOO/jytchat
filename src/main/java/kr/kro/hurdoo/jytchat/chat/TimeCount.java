package kr.kro.hurdoo.jytchat.chat;

public enum TimeCount {
    NONE(-1),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10)
    ;

    TimeCount(int num) {
        this.num = num;
        if(num == -1) str = "사용 안함";
        else str = num + "회 위반 시";
    }

    public final int num;
    public final String str;

    @Override
    public String toString() {
        return str;
    }
}
