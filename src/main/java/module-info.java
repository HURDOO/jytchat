module jytchat.main {
    requires YouTubeLiveChat;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires org.controlsfx.controls;
    requires darculafx;

    opens kr.kro.hurdoo.jytchat.ui to javafx.fxml;
    exports kr.kro.hurdoo.jytchat.ui to javafx.graphics,javafx.fxml;
}