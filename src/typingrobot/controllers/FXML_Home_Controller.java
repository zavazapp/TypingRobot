package typingrobot.controllers;

import java.awt.AWTException;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
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
import javafx.geometry.Bounds;
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
import javafx.stage.Window;
import org.apache.commons.compress.utils.FileNameUtils;
import typingrobot.utils.AlertUtils;
import typingrobot.tools.TypingUtility;

import typingrobot.models.InvoiceRow;
import typingrobot.controllers.interfaces.CurrentRowObservable;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.tools.fileLoading.FileLoadable;
import typingrobot.tools.Preferences;
import typingrobot.tools.fileLoading.AbstractFileLoader;
import typingrobot.tools.fileLoading.LoaderFactory;

/**
 *
 * @author Miodrag Spasic
 */
public class FXML_Home_Controller implements Initializable, CurrentRowObservable, IErrorInterface {

    //<editor-fold desc="variables">
    private ObservableList<InvoiceRow> observableList;
    private TypingUtility typingUtility;
    private Preferences preferences;
    public final static String URL = "https://www.zavazapp.com/apps/typing-robot";
    private final String VERSION = "version: 0.1.alpha\n\n2021"; //09 May 2021

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
    @FXML
    private TextField tableFirstRow; //user choice - first row of a table in excel file
    @FXML
    private MenuItem menuItemUserInstrusctions;
    @FXML
    private Label userInstructions;

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
        CheckBox checkBox = ((CheckBox) event.getSource());
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
        handleStartStopButtons(true, false);

        File droppedFile = event.getDragboard().getFiles().get(0);
        String fileExtension = FileNameUtils.getExtension(droppedFile.getName());

        //FileLoader needs a File and its extension.
        //FileLoader also need boolean hasHeadre from user choice
        //Returns ObservableList<InvoiceRow>
        int rowOffset;
        try {
            rowOffset = Integer.parseInt(tableFirstRow.getText());
            if (rowOffset < 1) {
                rowOffset = 1;
            }
        } catch (Exception e) {
            rowOffset = 1;
        }

        FileLoadable fileLoader = LoaderFactory.get(fileExtension, droppedFile, hasHeaders.isSelected(), rowOffset, this);
        
        //Inject Stage variable to FileLoadable instance for accesing windoe
        //Used to center secondary stages to center of app window
        ((AbstractFileLoader)fileLoader).setStage((Stage)dropField.getScene().getWindow());
        
        observableList.addAll(fileLoader.getList(fileExtension));

        dropField.setText(droppedFile.getName());
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    //`Start typing` buuton click listener
    @FXML
    private void startTyping(ActionEvent event) throws AWTException {
        int delay = 10;
        try {
            delay = Integer.parseInt(delayTextField.getText());
        } catch (Exception e) {
            delay = 10;
        }
        if (delay < 2 || delay > 2000) {
            delay = 15;
        }

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

                if (row > 0) {
                    tableView.getSelectionModel().select(row - 1 );
                    tableView.scrollTo(row - 1);
                    startingPositionLabel.setText(String.valueOf(row));
                }

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
    private void onTableRowClick(MouseEvent event) {
        startingPositionLabel.setText(String.valueOf(getStartingPosition() + 1));
    }

    private int getStartingPosition() {
        int startPosition = tableView.getSelectionModel().getSelectedIndex();
        return startPosition == -1 ? 0 : startPosition;
    }

    //Tooltip for start position Label
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

    //Menu items
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
        Window w = dropField.getScene().getWindow();
        
        stage.setX(w.getX() + 50);
        stage.setY(w.getY() + 50);

        stage.show();
    }

    @FXML
    private void onMenuItemUserInstructions(ActionEvent event) throws IOException {
        showWebViewStageInBrowser();
//        showWebViewStage();
    }

    @FXML
    private void onMenuItemAbout(ActionEvent event) {
        AlertUtils.getSimpleAlert(
                (Stage)dropField.getScene().getWindow(),
                Alert.AlertType.INFORMATION,
                "About Typing Robot",
                "Created by Miodrag Spasic",
                VERSION)
                .show();
    }
    //END Menu items

    @FXML
    private void onUserInstructionsClick(MouseEvent event) throws IOException {
        showWebViewStageInBrowser();
//        showWebViewStage();
    }

    //Callback received from AbstractFileLoader.
    //Sets text on a label, informing user about erors of successfuly loaded file.
    @Override
    public void onAnyError(String error, boolean critical) {
        errorLabel.setText(error);
        errorLabel.setVisible(!error.isEmpty());
        handleStartStopButtons(!critical, false);
    }

    private void showWebViewStage() throws IOException {
        Parent root = FXMLLoader.load(FXML_Home_Controller.class.getResource("/typingrobot/fxml/FXML_Instructions.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);

        stage.show();
    }

    private void showWebViewStageInBrowser() throws IOException {
        Desktop.getDesktop().browse(URI.create(URL));
    }

}
