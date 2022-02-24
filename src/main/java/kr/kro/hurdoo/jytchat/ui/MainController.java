package kr.kro.hurdoo.jytchat.ui;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
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
import kr.kro.hurdoo.jytchat.config.Config;
import org.controlsfx.control.ToggleSwitch;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    @FXML ChoiceBox<TimeCount> timeoutCount;
    @FXML ChoiceBox<TimeCount> banCount;
    @FXML ScrollPane chatScroll;
    @FXML TextFlow chatBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Jytchat.videoId != null) idField.setText(Jytchat.videoId);
        setToggleChat();
        setToggleSave();
        setToggleCheck();
        setChatBot();

        Platform.runLater(this::loadConfig);
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
                        if(chatBox.getChildren().size() > 200)
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

        if(toggleChatSave.isSelected()) toggleChatSave.fire();
        if(toggleChatCheck.isSelected()) toggleChatCheck.fire();
        if(toggleChatBot.isSelected()) toggleChatBot.fire();

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

        toggleChatSave.setDisable(true);
    }
    private void startSave()
    {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("채팅이 저장될 파일 선택");
            chooser.setInitialDirectory(Jytchat.chatLog.getParentFile());
            chooser.setInitialFileName(Jytchat.chatLog.isDirectory() ? "output.txt" : Jytchat.chatLog.getName());
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("모든 파일","*.*"));
            File check = chooser.showSaveDialog(UIMain.mainStage);
            if(check == null) {
                stopSave();
                toggleChatSave.setSelected(false);
                return;
            }
            Jytchat.chatLog = check;

            FileSaver.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopSave()
    {
        FileSaver.stop();
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
        chooser.setTitle("학셍 명단 파일 선택 (취소 시 출석체크 명령어와 학생 명단을 대조하지 않습니다)");
        chooser.setInitialDirectory(Jytchat.studentData.getParentFile());
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("모든 파일","*.*"));
        File check = chooser.showOpenDialog(UIMain.mainStage);
        if(check != null) {
            Jytchat.studentData = check;
        } // else no strict check
        Checker.start();
    }
    private void stopCheck()
    {
        Checker.stop();
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

        sendChatText.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!sendChatText.getText().contains("\n")) return;
            Platform.runLater(() -> sendChatText.setText(""));
            YTChatSender.write(YTChatSendRequest.YTChatSendType.SEND_MESSAGE, newValue);
        });

        setChatPermission();
        setCounts();
    }
    private void setChatPermission() {
        chatPermission.setItems(FXCollections.observableArrayList(ChatPermission.NONE,ChatPermission.CHECK,ChatPermission.ADMIN));
        chatPermission.setValue(ChatPermission.NONE);
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
            } else {
                chatPermission.setValue(oldValue);
            }
        });
    }
    private void setCounts() {
        timeoutCount.setItems(FXCollections.observableList(Arrays.asList(TimeCount.values())));
        timeoutCount.setValue(TimeCount.NONE);
        timeoutCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            ChatLimit.setTimeoutCount(newValue);
        });

        banCount.setItems(FXCollections.observableList(Arrays.asList(TimeCount.values())));
        banCount.setValue(TimeCount.NONE);
        banCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            ChatLimit.setBanCount(newValue);
        });
    }
    private void startChatBot()
    {
        YTCookieStage.show();
        sendChatText.setDisable(false);
        chatPermission.setDisable(false);
        chatPermission.setValue(ChatPermission.NONE);
        ChatLimit.setPermission(ChatPermission.NONE);
        timeoutCount.setDisable(false);
        banCount.setDisable(false);
    }
    public void stopChatBot()
    {
        YTChat.setUserData(null);
        sendChatText.setDisable(true);
        chatPermission.setDisable(true);
        chatPermission.setValue(ChatPermission.NONE);
        ChatLimit.setPermission(ChatPermission.NONE);
        timeoutCount.setValue(TimeCount.NONE);
        banCount.setValue(TimeCount.NONE);
        ChatLimit.setTimeoutCount(TimeCount.NONE);
        ChatLimit.setBanCount(TimeCount.NONE);
        timeoutCount.setDisable(true);
        banCount.setDisable(true);
    }

    private void loadConfig() {
        Config config;
        try {
            config = new Yaml(new AnnotationAwareConstructor(Config.class)).load(new FileInputStream("config.yml"));
        } catch (IOException ex) {
            config = new Yaml(new AnnotationAwareConstructor(Config.class)).load(MainController.class.getResourceAsStream("/config/config.yml"));
        }

        if(config.video_id != null)
        {
            idField.setText(config.video_id);
            if(config.enable_chat) toggleChat.fire();
        }
        if(config.save_file != null)
        {
            Jytchat.chatLog = config.save_file;
            if(config.enable_save) toggleChatSave.fire();
        }
        if(config.check_regex != null || config.checklist_file != null)
        {
            if(config.check_regex != null) Checker.setRegexPattern(config.check_regex);
            if(config.checklist_file != null) Jytchat.studentData = config.checklist_file;
            if(config.enable_check) toggleChatCheck.fire();
        }
        if(config.cookies != null)
        {
            YTChat.setUserData(config.cookies);
            if(config.enable_login) {
                toggleChatBot.fire();
                Platform.runLater(YTCookieStage::hide);
                if(config.chat_limit != null) chatPermission.setValue(config.chat_limit);
            }
        }
    }
}
