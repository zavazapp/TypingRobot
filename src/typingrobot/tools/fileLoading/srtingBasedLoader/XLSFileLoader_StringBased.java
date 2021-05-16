package typingrobot.tools.fileLoading.srtingBasedLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import javafx.collections.ObservableList;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.models.InvoiceRow;
import typingrobot.tools.fileLoading.FileLoadable;

/**
 *
 * @author Miodrag Spasic
 *
 * XLS Classes (Apache POI):
 *
 * HSSFWorkbook: Class representing the XLS file.
 *
 * HSSFSheet: Class representing the sheet in an XLS file.
 *
 * HSSFRow: Class representing a row in the sheet of XLS file.
 *
 * HSSFCell: Class representing a cell in a row of XLS file.
 *
 */
public class XLSFileLoader_StringBased extends AbstractStringBasedFileLoader implements FileLoadable {

    public XLSFileLoader_StringBased(File fileToLoad, boolean hasHeader, int firstTableRow, IErrorInterface errorCalback) {
        super(fileToLoad, hasHeader, firstTableRow, errorCalback);
    }

    @Override
    public ObservableList<InvoiceRow> getList(String fileExtension, String specialType) throws FileNotFoundException, IOException {
        System.out.println("loading..." + ".xls");

        FileInputStream inputStream1 = new FileInputStream(fileToLoad);

        HSSFWorkbook w1 = null;
        HSSFSheet s1 = null;
        int colCount = 0;
        ArrayList<String[]> table = new ArrayList<>();

        try {
            w1 = new HSSFWorkbook(inputStream1);
            s1 = w1.getSheetAt(0);

            ExcelExtractor extractor = new ExcelExtractor(w1);
            extractor.setIncludeCellComments(false);
            
            table = new TableParser_Invoices().getTableArray(extractor.getText(), specialType, firstTableRow);

            //count columns in table
            //if there are 5 columns, one of them is paymentCode
            //else, payment code is not included
            //Start generating rows from `firstTableRow`
            colCount = table.get(0).length;

        } catch (Exception e) {
            showLoadingError();
            inputStream1.close();
            return null;
        }

        //count columns in table
        //if there are 5 columns, one of them is paymentCode
        //else, payment code is not included
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
            inputStream1.close();
            return null;
        }

        //Release excel file
        inputStream1.close();

        return list;
    }

    @Override
    public double getTotalSum() {
        return totalSum;
    }

}
