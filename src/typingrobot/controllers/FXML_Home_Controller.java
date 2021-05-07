package typingrobot.controllers;

import java.awt.AWTException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.compress.utils.FileNameUtils;
import typingrobot.Utils.AlertUtils;
import typingrobot.tools.TypingUtility;

import typingrobot.models.InvoiceRow;
import typingrobot.tools.FileLoader;
import typingrobot.controllers.interfaces.CurrentRowObservable;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.tools.Preferences;

/**
 *
 * @author Miodrag Spasic
 */
public class FXML_Home_Controller implements Initializable, CurrentRowObservable, IErrorInterface {

    //<editor-fold desc="variables">
    private ObservableList<InvoiceRow> observableList;
    private TypingUtility typingUtility;
    private Preferences preferences;
            
    @FXML
    private Label dropField;
    @FXML
    private TableView<InvoiceRow> tableView;
    @FXML
    private TableColumn<?, ?> id;
    @FXML
    private TableColumn<?, ?> invoiceNumber;
    @FXML
    private TableColumn<?, ?> invoiceYear;
    @FXML
    private TableColumn<?, ?> invoiceAmount;
    @FXML
    private TextField delayTextField;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label countDownLabel;
    @FXML
    private Label startingPositionLabel;
    @FXML
    private Tooltip tooltip;
    @FXML
    private MenuItem menuItemClose;
    @FXML
    private MenuItem menuItemPreferences;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private CheckBox hasHeaders;
    @FXML
    private TableColumn<?, ?> paymentCode;
    @FXML
    private Label errorLabel;
 
     //</editor-fold>
    
    //entry point - initialize variables and set up starting UI components
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        observableList = FXCollections.observableArrayList();
        tableView.setItems(observableList);
        handleStartStopButtons(false, false);
        preferences = new Preferences();
        hasHeaders.setSelected(preferences.getBoolean("hasHeader", true));
        errorLabel.setVisible(false);
    }
    
    //Save user choice for hasHeader check box choice
    @FXML
    private void onHasHeaderChange(ActionEvent event) {
        CheckBox checkBox = ((CheckBox)event.getSource());
        preferences.putBoolean("hasHeader", checkBox.isSelected());
    }

    //listen drag event over Label dropField
    //accept drag only for files
    @FXML
    private void onDragOver(DragEvent event) throws FileNotFoundException, IOException {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }

    //accept drop events for files of type xls, xlsx, csv.
    //different loads must be applied for different file types
    //as implemented in FileLoader class.
    @FXML
    private void onDragDroped(DragEvent event) throws IOException, FileNotFoundException, AWTException {
        File droppedFile = event.getDragboard().getFiles().get(0);
        String fileExtension = FileNameUtils.getExtension(droppedFile.getName());

        //FileLoader needs a File and its extension.
        //FileLoader also need boolean hasHeadre from user choice
        //Returns ObservableList<InvoiceRow>
        observableList.addAll(new FileLoader(droppedFile, hasHeaders.isSelected(), this).getList(fileExtension));
        handleStartStopButtons(!observableList.isEmpty(), false);
        dropField.setText(droppedFile.getName());
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    //`Start typing` buuton click listener
    @FXML
    private void startTyping(ActionEvent event) throws AWTException {
        
        //Create TypingUtility object, set countDown label and set callback
        typingUtility = new TypingUtility(Integer.parseInt(delayTextField.getText()));
        typingUtility.setCountDownLabel(countDownLabel);
        typingUtility.setCurrentRowObservable(this);

        //This allows user to choose starting position in table.
        //Typing will start from selected row.
        //If nothing is selected, start from 0 (first row)
        int startPosition = getStartingPosition();

        typingUtility.startTyping(observableList, startPosition == -1 ? 0 : startPosition);
        handleStartStopButtons(false, true);
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    @FXML
    private void stopTyping(ActionEvent event) {
        typingUtility.stopTyping();
        handleStartStopButtons(true, false);
    }

    //calback from TypingUtility
    @Override
    public void onNextRow(int row) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tableView.getSelectionModel().select(row);
                tableView.scrollTo(row);
            }
        });
    }

    //calback from TypingUtility
    @Override
    public void onLastRowFinished(int row) {
        handleStartStopButtons(true, false);
    }

    /*Start position handling*/
    @FXML
    private void onTableMouseClicked(MouseEvent event) {
        startingPositionLabel.setText(String.valueOf(getStartingPosition() + 1));
    }

    private int getStartingPosition() {
        int startPosition = tableView.getSelectionModel().getSelectedIndex();
        return startPosition == -1 ? 0 : startPosition;
    }

    @FXML
    private void onStartPositionClick(MouseEvent event) {
        tooltip.show(startButton, event.getScreenX(), event.getScreenY());
        tooltip.setAutoHide(true);
    }
    /*END start pos. handling*/

    
    //Right click on dropField opens context menu for clearing observable list
    @FXML
    private void onDropFieldContextMenuRequest(ContextMenuEvent event) {
        ContextMenu menu = new ContextMenu();
        MenuItem item = new MenuItem("Clear table");

        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                observableList.clear();
                startingPositionLabel.setText("1");
                handleStartStopButtons(false, false);
                errorLabel.setText("");
            }
        });
        menu.getItems().add(item);
        menu.show(dropField.getScene().getWindow(), event.getScreenX(), event.getScreenY());
    }

    //method for invalidating buttons state
    private void handleStartStopButtons(boolean startEnabled, boolean stopEnabled) {
        startButton.setDisable(!startEnabled);
        stopButton.setDisable(!stopEnabled);
    }

    @FXML
    private void onMenuItemClose(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onMenuItemPreferences(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(FXML_Home_Controller.class.getResource("/typingrobot/fxml/FXML_Preferences.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        
        stage.show();
    }

    @FXML
    private void onMenuItemAbout(ActionEvent event) {
        AlertUtils.getSimpleAlert(
                Alert.AlertType.INFORMATION, "About AutoType", "Created by Miodrag Spasic", "version: 0.1\n\n2021")
                .show();
    }

    @Override
    public void onAnyError(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(!error.isEmpty());
    }

}
