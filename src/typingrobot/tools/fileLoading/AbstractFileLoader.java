package typingrobot.tools.fileLoading;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.ss.usermodel.Row;
import typingrobot.controllers.FXML_Home_Controller;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.models.InvoiceRow;

/**
 * 04.05.2021. - locked - stable
 *
 * 06.05.2021. - added user choice for header row
 *
 * 09.05.2021. - changed to abstract
 *
 * 10.05.2021. - Stage injection for secondary window positioning
 *
 * Loader classes extend FileLoader to share common methods. Each loader class
 * has its own implementation of FileLoadable interface methods depending on
 * file type input.
 *
 * @author Miodrag Spasic
 *
 * @FileLoader needs a File and its extension
 * @Return ObservableList<InvoiceRow>
 */
public abstract class AbstractFileLoader {

    public final File fileToLoad; //dropped file
    public final IErrorInterface errorCallback;
    public StringBuilder errorStringBuilder = new StringBuilder("Error!");
    public Calendar calendar;
    public int firstTableRow;
    public ObservableList<InvoiceRow> list = FXCollections.observableArrayList();
    public Stage stage;

    //Variable that holds user choice of table property `hasHeadres`
    public final boolean hasHeader;

    public AbstractFileLoader(
            File fileToLoad,
            boolean hasHeader,
            int firstTableRow,
            IErrorInterface errorCalback) {

        this.fileToLoad = fileToLoad;
        this.hasHeader = hasHeader;
        this.errorCallback = errorCalback;
        this.firstTableRow = firstTableRow;
        calendar = Calendar.getInstance();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    //Shows alert dialog if file fails to load, offering help 
    public void showLoadingError() {
        ButtonType close = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType showHelp = new ButtonType("Show help", ButtonBar.ButtonData.HELP);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Usualy, this error is show if excel or csv file does not contain well formed table.",
                showHelp, close);
        alert.setTitle("Error parsing file");

        if (stage != null) {
            alert.initOwner(stage);
        }

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get().equals(close)) {
            alert.close();
        } else {
            try {
                showWebViewStageInBrowser();
            } catch (IOException ex) {
                Logger.getLogger(AbstractFileLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        errorStringBuilder.append("Error parsing file");
        setErrorLabel("Error parsing file");
    }

    //Shows help about forming proper excel file
    public void showWebViewStage() throws IOException {
        Parent root = FXMLLoader.load(FXML_Home_Controller.class.getResource("/typingrobot/fxml/FXML_Instructions.fxml"));
        Stage webViewStage = new Stage();
        Scene scene = new Scene(root);
        webViewStage.setScene(scene);
        webViewStage.initStyle(StageStyle.DECORATED);

        if (stage != null) {
            webViewStage.initOwner(stage);
        }

        webViewStage.show();
    }

    private void showWebViewStageInBrowser() throws IOException {
        Desktop.getDesktop().browse(URI.create(FXML_Home_Controller.URL));
    }

    //Maps each row of excel table to java object (InvoiceRow)
    public InvoiceRow getObjectFromRow(short colCount, Row row) {
        InvoiceRow ir = new InvoiceRow();

        try {

            //remove dots from id
            String id = row.getCell(0).toString();
            if (id.contains(".")) {
                id = id.split("[.]")[0].trim();
            }

//            //remove dots from payment code
//            String paymentCode = row.getCell(0).toString();
//            if (id.contains(".")) {
//                id = id.split("[.]")[0].trim();
//            }
            //remove dots from year
            String year = row.getCell(colCount - 2).toString();
            if (year != null && !year.equals("") && year.contains(".")) {
                year = year.split("[.]")[0].trim();
            }

            //make sure amount is in format nn.nn or nn
            String amount = row.getCell(colCount - 1).toString();

            if (!amount.equals("") && amount.length() > 2) {
                if (String.valueOf(amount.charAt(amount.length() - 3)).equals(",")) {
                    amount = amount.replace(",", ".");
                }

                if (String.valueOf(amount.charAt(amount.length() - 3)).equals(".")) {
                    if (amount.contains(",")) {
                        amount = amount.replace(",", "");
                    }
                }
            }

            switch (colCount) {

                case 4:
                    //Excel file has 4 columns.
                    //That means there is no column named "Payment code"
                    //Add 112 for micing column value.
                    ir.setId(id);
                    ir.setPaymentCode("112");
                    ir.setInvoiceNumber(row.getCell(1).toString());
                    ir.setInvoiceYear(year);
                    ir.setInvoiceAmount(amount);
                    break;

                case 5:
                    //Excel file has 5 columns and one of them is "Payment code".
                    //If that is the case, read paymemt code from table.
                    ir.setId(id);
                    String paymentCode = row.getCell(1).toString();
                    if (paymentCode.contains(".")) {
                        paymentCode = paymentCode.split("[.]")[0];
                    }
                    ir.setPaymentCode(paymentCode);
                    ir.setInvoiceNumber(row.getCell(2).toString());
                    ir.setInvoiceYear(year);
                    ir.setInvoiceAmount(amount);
                    break;
            }

            validateValues(ir);

        } catch (Exception e) {
            System.out.println("Error in getObjectFromRow");
            System.out.println(e.toString());
            return null;
        }
        return ir;
    }

    private boolean critical = false; //defines if start button should be enabled

    public void validateValues(InvoiceRow ir) {
        int tempYear = 0;
        try {
            tempYear = Integer.parseInt(ir.getInvoiceYear());
        } catch (NumberFormatException e) {
            critical = true;
            errorStringBuilder
                    .append("\nInvoice year is invalid in row: ")
                    .append(ir.getId());
        }

        if (tempYear != 0 && tempYear > calendar.get(Calendar.YEAR)) {
            critical = true;
            errorStringBuilder
                    .append("\nInvoice year is greater than current in row: ")
                    .append(ir.getId());
        }

        if (tempYear == 0) {
            critical = true;
            errorStringBuilder
                    .append("\nIn001.0"
                            + "voice year is zero in row: ")
                    .append(ir.getId());
        }

        if (tempYear < calendar.get(Calendar.YEAR) - 5) {
            errorStringBuilder
                    .append("\nInvoice year does not have logical value in row: ")
                    .append(ir.getId());
        }

        if (ir.getId().isEmpty()
                || ir.getInvoiceNumber().isEmpty()
                || ir.getInvoiceYear().isEmpty()
                || ir.getInvoiceAmount().isEmpty()) {

            errorStringBuilder
                    .append("\nField is empty in row: ")
                    .append(ir.getId());
        }

        //crirical errors, so typing must be prevented
        if (ir.getInvoiceAmount().isEmpty()) {
            critical = true;
            errorStringBuilder
                    .append("\nInvoice ammount is missing in row: ")
                    .append(ir.getId());
        }
        if (ir.getPaymentCode().equals("112") && ir.getInvoiceNumber().isEmpty()) {
            critical = true;
            errorStringBuilder
                    .append("\nInvoice number is missing in row: ")
                    .append(ir.getId());
        }
        if (ir.getPaymentCode().equals("112") && ir.getInvoiceYear().isEmpty()) {
            critical = true;
            errorStringBuilder
                    .append("\nInvoice year is missing in row: ")
                    .append(ir.getId());
        }
        String regex = "[0-9.-]*$";
        if (!ir.getInvoiceAmount().matches(regex)) {
            critical = true;
            errorStringBuilder
                    .append("\nInvoice amount contains invalid characters in row: ")
                    .append(ir.getId());
        }

        if (!errorStringBuilder.toString().equals("Error!")) {
            errorCallback.onAnyError(errorStringBuilder.toString(), critical);
        }

    }

    //Sets text on error label informing user about
    //not acceptable values in table
    public void setErrorLabel(String error) {
        errorCallback.onAnyError(error, true);
    }
}
