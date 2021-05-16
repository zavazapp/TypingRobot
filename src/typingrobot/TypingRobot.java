package typingrobot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author Miodrag Spasic
 * 
 * Application entry point with main method.
 */
public class TypingRobot extends Application {
    
    //thread safe switch for stoping typing and timer threads
    public volatile static boolean WINDOW_CLOSED;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Task<Parent> createMainScene = new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                Parent root = FXMLLoader.load(getClass().getResource("fxml/FXML_Home.fxml"));
                Thread.sleep(2000);
                return root;
            }
        }; 

        //splash screen so that user see loading action
        ProgressBar pBar = new ProgressBar();
        pBar.progressProperty().bind(createMainScene.progressProperty());

        Label statusLabel = new Label("Loading Typing Robot...");
        Image image = new Image(TypingRobot.class.getResourceAsStream("resources/icon_v3.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

        HBox root = new HBox(55, imageView, statusLabel, pBar);

        Stage loadingStage = new Stage();
        loadingStage.setTitle("Loading...");
        loadingStage.initStyle(StageStyle.DECORATED);
        loadingStage.setScene(new Scene(root));
        loadingStage.show();
        //END splash screen
        

        //listener for main stage loading
        createMainScene.setOnSucceeded(e -> {
            stage.setScene(new Scene(createMainScene.getValue()));
            
            //set taskbar icon
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("resources/icon_v3.png")));
            stage.setTitle("Invoice Typing");
            stage.show();
            stage.setOnCloseRequest((WindowEvent e1Event) -> {
                Platform.exit();
            });

            loadingStage.hide();
        });
        

        new Thread(createMainScene).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
