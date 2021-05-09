package typingrobot.tools.fileLoading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.models.InvoiceRow;

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

public class XLSXFileLoader extends AbstractFileLoader implements FileLoadable {

    public XLSXFileLoader(File fileToLoad, boolean hasHeader, int firstTableRow, IErrorInterface errorCalback) {
        super(fileToLoad, hasHeader, firstTableRow, errorCalback);
    }

    @Override
    public ObservableList<InvoiceRow> getList(String fileExtension) throws FileNotFoundException, IOException {
        System.out.println("loading..." + ".xlsx");

        FileInputStream inputStream = new FileInputStream(fileToLoad);

        XSSFWorkbook w = null;
        XSSFSheet s = null;
        short colCount = 0;

        try {
            w = new XSSFWorkbook(inputStream);
            s = w.getSheetAt(0);

            //count columns in table
            //if there are 5 columns, one of them is paymentCode
            //else, payment code is not included
            //Start generating rows from `firstTableRow`
            colCount = s.getRow(firstTableRow).getLastCellNum();//TODO inv.terget exception

        } catch (Exception e) {
            System.out.println("Ecxeption in line " + 57);
            showLoadingError();
            inputStream.close();
            return null;
        }

        try {
            if (hasHeader) {
                    firstTableRow += 1;
                }
            
            for (Row row : s) {
                
                if (row.getRowNum() >= firstTableRow - 1) {

                    InvoiceRow invoiceRow = getObjectFromRow(colCount, row);
                    if (invoiceRow == null) {
                        showLoadingError();
                        
                        break;
                    }
                    list.add(getObjectFromRow(colCount, row));

                }
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

}
