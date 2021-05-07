package typingrobot;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author Miodrag Spasic
 */
public class TypingRobot extends Application {
    
    //thread safe switch for stoping other typing and timer threads
    public volatile static boolean WINDOW_CLOSED;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/FXML_Home.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                WINDOW_CLOSED = true;
            }
        });
        
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
