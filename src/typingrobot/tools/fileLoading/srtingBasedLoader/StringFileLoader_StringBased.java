package typingrobot.tools.fileLoading.srtingBasedLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.models.InvoiceRow;
import typingrobot.tools.fileLoading.FileLoadable;

/**
 *
 * @author Miodrag Spasic
 *
 * 16.05.2021. - This class is added to support paste option for
 * plain text, tab delimited.
 * 
 * Used to select table in excel and copy-paste in robot.
 */
public class StringFileLoader_StringBased extends AbstractStringBasedFileLoader implements FileLoadable {

    public StringFileLoader_StringBased(
            File fileToLoad,
            boolean hasHeader,
            int firstTableRow,
            IErrorInterface errorCalback) {
        super(fileToLoad, hasHeader, firstTableRow, errorCalback);
    }
    
    
    @Override
    public ObservableList<InvoiceRow> getList(String args, String specialType) throws FileNotFoundException, IOException {
        System.out.println("loading..." + " plain text");

        int colCount = 0;
        ArrayList<String[]> table;

        try {

            table = new TableParser_Invoices().getTableArray(args, specialType, firstTableRow);

            //count columns in table
            //if there are 5 columns, one of them is paymentCode
            //else, payment code is not included
            //Start generating rows from `firstTableRow`
            colCount = table.get(0).length;

        } catch (Exception e) {
            System.out.println("Ecxeption in line " + 69 + e.getMessage());
            showLoadingError();
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
            return null;
        }

        return list;
    }

    @Override
    public double getTotalSum() {
        return totalSum;
    }

}
