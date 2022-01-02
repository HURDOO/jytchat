package kr.kro.hurdoo.jytchat.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import kr.kro.hurdoo.jytchat.Jytchat;
import kr.kro.hurdoo.jytchat.chat.*;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable //NewFileCallable
{
    public static MainController instance;

    private final List<String> msgs = new LinkedList<>();

    @FXML ImageView thumbnail;
    @FXML TextField idField;
    @FXML ToggleSwitch toggleChat;
    @FXML ToggleSwitch toggleChatSave;
    @FXML ToggleSwitch toggleChatCheck;
    @FXML ToggleSwitch toggleChatBot;
    @FXML TextArea sendChatText;
    @FXML ChoiceBox<ChatPermission> chatPermission;
    @FXML Button sendChatButton;
    @FXML ScrollPane chatScroll;
    @FXML TextFlow chatBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Jytchat.videoId != null) idField.setText(Jytchat.videoId);
        setToggleChat();
        setToggleSave();
        setToggleCheck();
        setChatBot();
    }

    public void start()
    {
        Timeline chatTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), event1 ->
                {
                    while(!msgs.isEmpty())
                    {
                        Text text = new Text();
                        text.setText(msgs.get(0) + "\n");
                        text.setWrappingWidth(350);
                        chatBox.getChildren().add(text);
                        msgs.remove(0);
                        if(chatBox.getChildren().size() > 25)
                            chatBox.getChildren().remove(0);
                        Platform.runLater(() -> chatScroll.vvalueProperty().bind(chatBox.heightProperty()));
                    }
                }));
        chatTimer.setCycleCount(Timeline.INDEFINITE);
        chatTimer.play();
    }

    public void write(String msg)
    {
        msgs.add(msg);
    }

    private void setToggleChat()
    {
        toggleChat.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue) connect();
                else disconnect();
            } catch (Exception e)
            {
                toggleChat.setSelected(oldValue);
            }
        });
        toggleChat.setDisable(false);
    }
    private void connect()
    {
        Jytchat.videoId = idField.getText();
        if(!YTChat.start()) return;
        idField.setEditable(false);

        start();

        toggleChatSave.setDisable(false);
        toggleChatCheck.setDisable(false);
        toggleChatBot.setDisable(false);

        thumbnail.setImage(new Image("http://img.youtube.com/vi/" + Jytchat.videoId + "/mqdefault.jpg"));
    }
    private void disconnect()
    {
        Jytchat.videoId = null;
        idField.setEditable(true);

        YTChat.stop();

        stopSave();
        stopChatBot();
        stopCheck();

        chatBox.getChildren().clear();
    }

    private void setToggleSave()
    {
        toggleChatSave.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue) startSave();
                else stopSave();
            } catch (Exception e)
            {
                e.printStackTrace();
                toggleChat.setSelected(oldValue);
            }
        });

        toggleChatSave.setText("채팅 저장 켜기");
        toggleChatSave.setDisable(true);
    }
    private void startSave()
    {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("로그 파일 선택");
            chooser.setInitialDirectory(Jytchat.chatLog.getParentFile());
            chooser.setInitialFileName("output.txt");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("모든 파일","*.*"));
            File check = chooser.showSaveDialog(UIMain.mainStage);
            if(check == null) return;
            Jytchat.chatLog = check;

            FileSaver.start();
            toggleChatSave.setText("채팅 저장 끄기");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopSave()
    {
        FileSaver.stop();
        toggleChatSave.setText("채팅 저장 켜기");
    }

    private void setToggleCheck()
    {
        toggleChatCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(newValue) startCheck();
                else stopCheck();
            } catch (Exception e)
            {
                e.printStackTrace();
                toggleChatCheck.setSelected(oldValue);
            }
        });
        toggleChatCheck.setDisable(true);
    }
    private void startCheck()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("출석 데이터 파일 선택");
        chooser.setInitialDirectory(Jytchat.studentData.getParentFile());
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("모든 파일","*.*"));
        File check = chooser.showOpenDialog(UIMain.mainStage);
        if(check == null) return;
        Jytchat.studentData = check;

        Checker.start();
        toggleChatCheck.setText("출석체크 종료");
    }
    private void stopCheck()
    {
        Checker.stop();
        toggleChatCheck.setText("출석체크 시작");
    }

    private void setChatBot()
    {
        toggleChatBot.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue) startChatBot();
                else stopChatBot();
            } catch (Exception e)
            {
                e.printStackTrace();
                toggleChatBot.setSelected(oldValue);
            }
        });
        toggleChatBot.setDisable(true);
        chatPermission.setItems(FXCollections.observableArrayList(ChatPermission.NONE,ChatPermission.CHECK,ChatPermission.ADMIN));
        chatPermission.setValue(ChatPermission.NONE);
        sendChatButton.setOnAction(event -> {
            if(sendChatText.getText().equals("")) return;
            YTChat.sendChat(sendChatText.getText());
            sendChatText.setText("");
        });
        chatPermission.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case CHECK:

            }
        });
        reloadUsername();
    }
    private void startChatBot()
    {
        try {
            BorderPane pane = FXMLLoader.load((MainController.class.getResource("/fxml/ytcookie.fxml")));
            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.show();
            stage.setOnCloseRequest(event -> stage.close());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        toggleChatBot.setText("챗봇 해제");
        sendChatText.setDisable(false);
        sendChatButton.setDisable(false);
        chatPermission.setDisable(false);

        chatPermission.valueProperty().addListener((observable, oldValue, newValue) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(UIMain.mainStage);
            alert.setTitle("채팅 권한 변경");
            alert.setHeaderText(newValue.toString());
            switch (newValue) {
                case NONE:
                    alert.setContentText("누구나 채팅을 칠 수 있습니다.");
                    break;
                case CHECK:
                    alert.setContentText("!출석체크 명령어 이용 후 채팅이 가능합니다.");
                    break;
                case ADMIN:
                    alert.setContentText("채팅 관리자만 채팅이 가능합니다.\n관리자는 채널 설정에서 변경 가능합니다.");
                    break;
            }
            ButtonType result = alert.showAndWait().get();
            if(result == ButtonType.OK) {
                ChatLimit.setPermission(newValue);
            }
        });
    }
    public void stopChatBot()
    {
        //Jytchat.nbAuth = null;
        toggleChatBot.setText("챗봇 연동");
        sendChatText.setDisable(true);
        sendChatButton.setDisable(true);
        chatPermission.setDisable(true);
        chatPermission.setValue(ChatPermission.NONE);
    }
    public void reloadUsername() {
        String name = YTChat.getUsername();
        if(name != null) sendChatText.setPromptText(name + "으(로) 메세지 보내기");
        else sendChatText.setPromptText("메세지 보내기");
    }
}
