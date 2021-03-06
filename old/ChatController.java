package kr.kro.hurdoo.jytchat.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import kr.kro.hurdoo.jytchat.Jytchat;
import kr.kro.hurdoo.jytchat.chat.Checker;
import kr.kro.hurdoo.jytchat.chat.FileSaver;
import kr.kro.hurdoo.jytchat.chat.YTChat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable //NewFileCallable
{
    public static ChatController instance;

    private final List<String> msgs = new LinkedList<>();

    private boolean useChat = false;
    private boolean useSave = false;
    private boolean useCheck = false;
    private boolean useChatBot = false;

    @FXML VBox chatBox;
    @FXML TextField idField;
    @FXML Button toggleChat;
    @FXML Button toggleSave;
    @FXML Button toggleCheck;
    @FXML Button chatBot;
    @FXML ChoiceBox<String> chat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Jytchat.videoId != null) idField.setText(Jytchat.videoId);
        setToggleChat();
        setToggleSave();
        setToggleCheck();
        setNightBot();
    }

    public void start()
    {
        Timeline chatTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), event1 ->
                {
                    while(!msgs.isEmpty())
                    {
                        chatBox.getChildren().add(new Text(msgs.get(0)));
                        msgs.remove(0);
                        if(chatBox.getChildren().size() > 25)
                        {
                            chatBox.getChildren().remove(0);
                        }
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
        toggleChat.setOnAction(event -> {
            if(!useChat) // == false
            {
                // setup
                connect();
            }
            else
            {
                // unload
                disconnect();
            }
        });
        toggleChat.setText("?????? ?????????");
        toggleChat.setDisable(false);
    }
    private void connect()
    {
        Jytchat.videoId = idField.getText();
        if(!YTChat.start()) return;
        idField.setDisable(true);

        useChat = true;
        toggleChat.setText("?????? ????????????");
        start();

        toggleSave.setDisable(false);
        toggleCheck.setDisable(false);
        chatBot.setDisable(false);
    }
    private void disconnect()
    {
        Jytchat.videoId = null;
        idField.setDisable(false);

        YTChat.stop();

        useChat = false;
        toggleChat.setText("?????? ?????????");

        stopSave();
        toggleSave.setDisable(true);

        stopCheck();
        toggleCheck.setDisable(true);

        stopChatBot();
        chatBot.setDisable(true);

        chatBox.getChildren().clear();
    }

    private void setToggleSave()
    {
        toggleSave.setOnAction(event -> {
            if(!useSave) // == false
            {
                // save
                //NewFileStage.stage.start(this);

                FileChooser chooser = new FileChooser();
                chooser.setTitle("?????? ?????? ??????");
                chooser.setInitialDirectory(Jytchat.chatLog.getParentFile());
                chooser.setInitialFileName("output.txt");
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("?????? ??????","*.*"));
                File check = chooser.showSaveDialog(UIMain.mainStage);
                if(check == null) return;
                Jytchat.chatLog = check;
                startSave();
            }
            else
            {
                stopSave();
            }
        });

        toggleSave.setText("?????? ?????? ??????");
        toggleSave.setDisable(true);
    }
    private void startSave()
    {
        try {
            FileSaver.start();
            toggleSave.setText("?????? ?????? ??????");
            useSave = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopSave()
    {
        FileSaver.stop();
        toggleSave.setText("?????? ?????? ??????");
        useSave = false;
    }

    private void setToggleCheck()
    {
        toggleCheck.setOnAction(event -> {
            if(!useCheck) // == false
            {
                // check
                startCheck();
            }
            else
            {
                // stop
                stopCheck();
            }
        });
        toggleCheck.setText("???????????? ??????");
        toggleCheck.setDisable(true);
    }
    private void startCheck()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("?????? ????????? ?????? ??????");
        chooser.setInitialDirectory(Jytchat.studentData.getParentFile());
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("?????? ??????","*.*"));
        File check = chooser.showOpenDialog(UIMain.mainStage);
        if(check == null) return;
        Jytchat.studentData = check;

        Checker.start();
        useCheck = true;
        toggleCheck.setText("???????????? ??????");
    }
    private void stopCheck()
    {
        Checker.stop();
        useCheck = false;
        toggleCheck.setText("???????????? ??????");
    }

    private void setNightBot()
    {
        chatBot.setOnAction(event -> {
            if(!useChatBot)
            {
                startChatBot();
            }
            else
            {
                stopChatBot();
            }
        });
        chatBot.setText("StreamLabs ??????");
        chatBot.setDisable(true);
    }
    private void startChatBot()
    {
        //NightBotStage.open();
        useChatBot = true;
        chatBot.setText("StreamLabs ??????");
    }
    public void stopChatBot()
    {
        //Jytchat.nbAuth = null;
        useChatBot = false;
        chatBot.setText("StreamLabs ??????");
    }

    private enum ChatLimit {
        NONE,
        CHECK,
        ADMIN;
    }

    /*@Override
    public void onNewFileChosen() {
        startSave();
    }*/
}
