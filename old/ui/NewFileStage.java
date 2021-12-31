package old_java.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class NewFileStage extends Stage {

    private NewFileCallable currentClass = null;

    public static NewFileStage stage;

    public NewFileStage(Stage parent) throws IOException {
        super();
        initOwner(parent);
        setResizable(false);
        FXMLLoader loader = new FXMLLoader(NewFileStage.class.getResource("/fxml/NewFile.fxml"));
        AnchorPane pane = loader.load();
        NewFileController cont = loader.getController();
        setScene(new Scene(pane));

    }
    public void start(NewFileCallable currentClass)
    {
        this.currentClass = currentClass;
        show();
    }
    public void end()
    {
        close();
        currentClass.onNewFileChosen();
        currentClass = null;
    }
}
