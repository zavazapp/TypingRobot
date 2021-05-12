package typingrobot.tools.fileLoading.rowBasedLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.collections.ObservableList;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
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
@Deprecated
public class XLSFileLoader_RowBased extends AbstractRowBasedFileLoader implements FileLoadable {

    public XLSFileLoader_RowBased(File fileToLoad, boolean hasHeader, int firstTableRow, IErrorInterface errorCalback) {
        super(fileToLoad, hasHeader, firstTableRow, errorCalback);
    }

    @Override
    public ObservableList<InvoiceRow> getList(String fileExtension) throws FileNotFoundException, IOException {
        System.out.println("loading..." + ".xls");

        FileInputStream inputStream1 = new FileInputStream(fileToLoad);

        HSSFWorkbook w1 = null;
        HSSFSheet s1 = null;
        try {
            w1 = new HSSFWorkbook(inputStream1);
            s1 = w1.getSheetAt(0);
        } catch (Exception e) {
            showLoadingError();
            inputStream1.close();
            return null;
        }

        //count columns in table
        //if there are 5 columns, one of them is paymentCode
        //else, payment code is not included
//        short colCount1 = 4;
        short colCount1 = s1.getRow(firstTableRow).getLastCellNum();

        try {
            if (hasHeader) {
                firstTableRow += 1;
            }

            for (Row row : s1) {
                if (row.getRowNum() >= firstTableRow - 1) {
                    InvoiceRow invoiceRow = getObjectFromRow(colCount1, row);
                    
                    if (invoiceRow == null) {
                        showLoadingError();
                        
                        break;
                    }
                    
                    list.add(invoiceRow);
                }
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
    public long getTotalSum() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
