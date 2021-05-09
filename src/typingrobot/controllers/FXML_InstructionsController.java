package typingrobot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * FXML_Instructions.fxml Controller class
 * 
 * Shows new stage with a web view.
 *
 * @author Miodrag spasic
 */
public class FXML_InstructionsController implements Initializable {

    //url to web page with instructions
    private final String URL = "https://www.zavazapp.com/apps/typing-robot";

    @FXML
    private WebView webView;
    @FXML
    private ProgressBar progressBar;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WebEngine webEngine = webView.getEngine();
        webEngine.load(URL);
        
        progressBar.progressProperty().bind(webEngine.getLoadWorker().progressProperty());

        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                   
                
                if (newValue == Worker.State.SUCCEEDED) {
                    progressBar.setVisible(false);
                }
            }
        });
    }

}
