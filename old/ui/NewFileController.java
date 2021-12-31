package old_java.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import kr.kro.hurdoo.jytchat.Jytchat;
import kr.kro.hurdoo.jytchat.ui.UIMain;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class NewFileController implements Initializable
{

    @FXML TextField dir;
    @FXML Button changeDir;
    @FXML TextField file;
    @FXML Button ok;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setChangeDir();
        setOk();
    }
    private void setChangeDir()
    {
        changeDir.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("파일 위치 선택");
            if(Jytchat.chatLog != null && Jytchat.chatLog.exists() && Jytchat.chatLog.isDirectory())
                chooser.setInitialDirectory(Jytchat.chatLog);
            File selected = chooser.showDialog(UIMain.mainStage);
            if(selected != null && selected.exists() && selected.isDirectory())
            {
                dir.setText(selected.getPath());
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("알 수 없는 폴더입니다.");
                if(selected == null) alert.setContentText("폴더를 찾을 수 없습니다.");
                else alert.setContentText("폴더를 찾을 수 없습니다:\n" + selected.getPath());
                alert.show();
            }
        });
        if(Jytchat.chatLog == null) Jytchat.chatLog = new File("/");
        dir.setText(Jytchat.chatLog.getPath());
        dir.setAlignment(Pos.CENTER_RIGHT);
    }

    private void setOk()
    {
        ok.setOnAction(event -> {
            File check = new File(dir.getText() + File.separator + file.getText());
            if(check.exists())
            {
                Alert alert = new Alert(Alert.AlertType.WARNING,"",ButtonType.OK, ButtonType.CANCEL);
                alert.setTitle("파일 덮어쓰기");
                alert.setHeaderText("파일이 이미 존재합니다.");
                alert.setContentText("덮어쓰기 하시겠습니까?\n" + check.getPath());
                alert.initOwner(NewFileStage.stage);
                alert.showAndWait();
                if(alert.getResult().equals(ButtonType.CANCEL)) return;
            }
            Jytchat.chatLog = check;

            // close request
            NewFileStage.stage.end();
        });
    }
}
