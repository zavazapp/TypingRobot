package typingrobot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import typingrobot.tools.Preferences;

/**
 * FXML Controller class
 *
 * @author Miodrag Spasic
 */
public class FXML_Preferences_Controller implements Initializable {
    
    private final Preferences preferences = new Preferences();

    @FXML
    private CheckBox addEnterCheckBox;
    @FXML
    private CheckBox addDoubleEnterCheckBox;
    @FXML
    private CheckBox addArrowDownCheckBox;
    @FXML
    private CheckBox typeIdsCheckBox;
    @FXML
    private CheckBox typeInvoiceInDscCheckBox;
    @FXML
    private Button okButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addEnterCheckBox.setSelected(preferences.getBoolean("addEnterCheckBox", false));
        addDoubleEnterCheckBox.setSelected(preferences.getBoolean("addDoubleEnterCheckBox", false));
        addArrowDownCheckBox.setSelected(preferences.getBoolean("addArrowDownCheckBox", true));
        typeIdsCheckBox.setSelected(preferences.getBoolean("typeIdsCheckBox", false));
        typeInvoiceInDscCheckBox.setSelected(preferences.getBoolean("typeInvoiceInDscCheckBox", false));
    }    

    @FXML
    private void onCheckBoxAction(ActionEvent event) {
        CheckBox checkBox = (CheckBox)event.getTarget();
        String key = checkBox.getId();
        boolean value = checkBox.isSelected();
        
        preferences.putBoolean(key, value);
        
    }

    @FXML
    private void onOkClick(ActionEvent event) {
        ((Stage)okButton.getScene().getWindow()).close();
    }
    
}
