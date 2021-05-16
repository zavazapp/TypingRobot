package typingrobot.controllers;

import java.awt.AWTException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.models.MasterCardItem;
import typingrobot.models.RezimItem;
import typingrobot.utils.typingUtils.TypingUtility_Invoices;
import typingrobot.utils.typingUtils.TypingUtility_MasterCard;
import typingrobot.tools.fileLoading.referenceStringLoaders.ReferenceStringLoader_MasterCard;
import typingrobot.tools.fileLoading.referenceStringLoaders.ReferenceStringLoader_Rezim;
import typingrobot.utils.typingUtils.TypingUtility_Rezim;

/**
 * FXML_Dekada_MasterCard class
 *
 * @author Miodrag Spasic
 */
public class FXML_Rezim_Controller extends FXML_Home_Controller implements Initializable, IErrorInterface {

    private ObservableList<RezimItem> observableList;
    private TypingUtility_Rezim typingUtility;

    @FXML
    private MenuItem menuItemClose;
    @FXML
    private Label dropField;
    @FXML
    private Label countDownLabel;
    @FXML
    private Label userInstructions;
    @FXML
    private TextField delayTextField;
    @FXML
    private Label startingPositionLabel;
    @FXML
    private Tooltip tooltip;
    @FXML
    private CheckBox hasHeaders;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private TableView<RezimItem> tableView;
    @FXML
    private TableColumn<?, ?> paymentCode;
    @FXML
    private MenuItem menuItemInvoiceTyping;
    @FXML
    private MenuItem menuItemMastercardCorrections;
    @FXML
    private TextField paymentCodeField;
    @FXML
    private TextField mbField;
    @FXML
    private TableColumn<?, ?> reference;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        observableList = FXCollections.observableArrayList();
        tableView.setItems(observableList);
        handleStartStopButtons(false, false);
    }


    @FXML
    private void onDropFieldContextMenuRequest(ContextMenuEvent event) {
        ContextMenu menu = new ContextMenu();

        //Clear
        MenuItem itemClear = new MenuItem("Clear table");
        itemClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                observableList.clear();
                startingPositionLabel.setText("1");
                handleStartStopButtons(false, false);
                dropField.setText("Paste references here");
            }
        });

        //Paste
        MenuItem itemPaste = new MenuItem("Paste");
        itemPaste.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                observableList.clear();
                startingPositionLabel.setText("1");
                handleStartStopButtons(false, false);

                Clipboard clipboard = Clipboard.getSystemClipboard();

                if (clipboard.hasString()) {

                    handleStartStopButtons(true, false);

                    try {
                        initFileLoader(clipboard.getString());
                    } catch (IOException ex) {
                        Logger.getLogger(FXML_Rezim_Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        });

        menu.getItems().add(itemClear);
        menu.getItems().add(itemPaste);
        menu.setAutoHide(true);
        menu.show(dropField.getScene().getWindow(), event.getScreenX(), event.getScreenY());
    }

    @FXML
    private void onDragOver(DragEvent event) {
    }

    @FXML
    private void onDragDroped(DragEvent event) {
    }

    @FXML
    private void onUserInstructionsClick(MouseEvent event) {
    }

    @FXML
    private void onStartPositionClick(MouseEvent event) {
    }

    @FXML
    private void onHasHeaderChange(ActionEvent event) {
    }

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

        //Create TypingUtility_Invoices object, set countDown label and set callback
        typingUtility = new TypingUtility_Rezim(Integer.parseInt(delayTextField.getText()));
        typingUtility.setCountDownLabel(countDownLabel);
        typingUtility.setCurrentRowObservable(this);

        //This allows user to choose starting position in table.
        //Typing will start from selected row.
        //If nothing is selected, start from 0 (first row)
        int startPosition = getStartingPosition();

        typingUtility.startTyping(observableList, startPosition == -1 ? 0 : startPosition);
        handleStartStopButtons(false, true);
    }

    @FXML
    private void stopTyping(ActionEvent event) {
        typingUtility.stopTyping();
        handleStartStopButtons(true, false);
    }


    @FXML
    private void onMenuItemInvoiceTyping(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(FXML_Home_Controller.class.getResource("/typingrobot/fxml/FXML_Home.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        Window w = dropField.getScene().getWindow();

        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/typingrobot/resources/icon_v3.png")));
        stage.setX(w.getX() + 50);
        stage.setY(w.getY() + 50);
        stage.setTitle("Invoice Typing");
        stage.show();
        w.hide();
    }

    //method for invalidating buttons state
    private void handleStartStopButtons(boolean startEnabled, boolean stopEnabled) {
        startButton.setDisable(!startEnabled);
        stopButton.setDisable(!stopEnabled);
    }

    private void initFileLoader(String args) throws IOException {

        ReferenceStringLoader_Rezim loader = new ReferenceStringLoader_Rezim();

        //specialType is user choice for conviniance of different table formats
        observableList.addAll(loader.getList(args));

        for (RezimItem rezimItem : observableList) {
            System.out.println(rezimItem.getReference());
        }

    }

    @Override
    public void onAnyError(String error, boolean critical) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int getStartingPosition() {
        int startPosition = tableView.getSelectionModel().getSelectedIndex();
        return startPosition == -1 ? 0 : startPosition;
    }
}
