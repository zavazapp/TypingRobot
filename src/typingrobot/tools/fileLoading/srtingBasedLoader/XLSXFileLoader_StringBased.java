package typingrobot.tools.fileLoading.srtingBasedLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceDialog;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.models.InvoiceRow;
import typingrobot.tools.fileLoading.FileLoadable;

/**
 *
 * @author Miodrag Spasic
 *
 * XLSX Classes (Apache POI):
 *
 * XSSFWorkbook: It is a class representing the XLSX file.
 *
 * XSSFSheet: It is a class representing the sheet in an XLSX file.
 *
 * XSSFRow: It is a class representing a row in the sheet of XLSX file.
 *
 * XSSFCell: It is a class representing a cell in a row of XLSX file.
 *
 */
public class XLSXFileLoader_StringBased extends AbstractStringBasedFileLoader implements FileLoadable {

    public XLSXFileLoader_StringBased(
            File fileToLoad,
            boolean hasHeader,
            int firstTableRow,
            IErrorInterface errorCalback) {
        super(fileToLoad, hasHeader, firstTableRow, errorCalback);
    }

    @Override
    public ObservableList<InvoiceRow> getList(String fileExtension, String specialType) throws FileNotFoundException, IOException {
        System.out.println("loading..." + ".xlsx");

        FileInputStream inputStream = new FileInputStream(fileToLoad);

        XSSFWorkbook w = null;
        XSSFSheet s = null;
        int colCount = 0;
        ArrayList<String[]> table;

        try {
            w = new XSSFWorkbook(inputStream);
            s = w.getSheetAt(0);

            int sheetCount = w.getNumberOfSheets();

            String[] sheetNames = new String[sheetCount];
            for (int i = 0; i < sheetCount; i++) {
                sheetNames[i] = w.getSheetAt(i).getSheetName();
            }

            if (sheetCount > 1) {
                ChoiceDialog<String> choiceDialog = new ChoiceDialog(sheetNames[0], sheetNames);
                choiceDialog.setTitle("Multiple sheets found");
                choiceDialog.setHeaderText("Choose sheet..");
                choiceDialog.setContentText("My table is in sheet: ");
                choiceDialog.initOwner(stage);

                Optional<String> choice = choiceDialog.showAndWait();

                for (String sheetName : sheetNames) {
                    if (!sheetName.equals(choice.get())) {
                        w.removeSheetAt(w.getSheetIndex(sheetName));
                    }
                }
            }

            XSSFExcelExtractor extractor = new XSSFExcelExtractor(w);
            extractor.setIncludeCellComments(false);

            table = new TableParser_Invoices().getTableArray(extractor.getText(), specialType, firstTableRow);

            //count columns in table
            //if there are 5 columns, one of them is paymentCode
            //else, payment code is not included
            //Start generating rows from `firstTableRow`
            colCount = table.get(0).length;

        } catch (Exception e) {
            System.out.println("Ecxeption in line " + 69 + e.getMessage());
            showLoadingError();
            inputStream.close();
            return null;
        }

        try {

            for (String[] row : table) {

                InvoiceRow invoiceRow = getObjectFromStringArray(colCount, row);
                if (invoiceRow == null) {
                    showLoadingError();

                    break;
                }
                list.add(invoiceRow);

            }
        } catch (Exception e) {
            showLoadingError();
            inputStream.close();
            return null;
        }

        //Release excel file
        inputStream.close();

        return list;
    }

    @Override
    public double getTotalSum() {
        return totalSum;
    }

}
