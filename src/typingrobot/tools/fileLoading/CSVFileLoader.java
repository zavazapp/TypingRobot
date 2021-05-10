package typingrobot.tools.fileLoading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javafx.collections.ObservableList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.models.InvoiceRow;

/**
 *
 * @author Miodrag Spasic
 */
public class CSVFileLoader extends AbstractFileLoader implements FileLoadable{

    public CSVFileLoader(File fileToLoad, boolean hasHeader, int firstTableRow, IErrorInterface errorCalback) {
        super(fileToLoad, hasHeader, firstTableRow, errorCalback);
    }

    @Override
    public ObservableList<InvoiceRow> getList(String fileExtension) throws FileNotFoundException, IOException {
        System.out.println("loading..." + ".csv");

                BufferedReader fileReader = null;
                CSVParser csvParser = null;

                fileReader = new BufferedReader(new FileReader(fileToLoad));

                try {
                    csvParser = new CSVParser(
                            fileReader,
                            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                } catch (Exception e) {
                    showLoadingError();
                    csvParser.close();
                    return null;
                }

                Iterable<CSVRecord> csvRecords = csvParser.getRecords();

                for (CSVRecord csvRecord : csvRecords) {
                    InvoiceRow ir = new InvoiceRow();

                    int recCount = csvRecord.size();
//                    int rowId = 0;
//                    try {
//                        rowId = Integer.parseInt(csvRecord.get(0));
//                    } catch (NumberFormatException e) {
//                        showLoadingError();
//                        return null;
//                    }

                    //CSV record can not use abstract methods for validation
                    try {
                        //remove dots from id
                        String id = csvRecord.get(0);
                        if (id.contains(".")) {
                            id = id.split("[.]")[0].trim();
                        }
                        
                        

                        //remove dots from year
                        String year = csvRecord.get(recCount - 2);
                        if (year.contains(".")) {
                            year = year.split("[.]")[0].trim();
                        }

                        //make sure amount is in format nn.nn or nn
                        String amount = csvRecord.get(recCount - 1);

                        if (String.valueOf(amount.charAt(amount.length() - 3)).equals(",")) {
                            amount = amount.replace(",", ".");
                        }

                        if (String.valueOf(amount.charAt(amount.length() - 3)).equals(".")) {
                            if (amount.contains(",")) {
                                amount = amount.replace(",", "");
                            }
                        }
                        
                        

                        if (recCount == 4) {
                            if (csvRecord.getRecordNumber() >= firstTableRow - 1) {
                                ir.setId(id);
                                ir.setPaymentCode("112");
                                ir.setInvoiceNumber(csvRecord.get(1));
                                ir.setInvoiceYear(year);
                                ir.setInvoiceAmount(amount);

                                list.add(ir);
                            }
                        }

                        if (recCount == 5) {
                            if (csvRecord.getRecordNumber() >= firstTableRow - 1) {
                                ir.setId(id);
                                ir.setPaymentCode(csvRecord.get(1));
                                ir.setInvoiceNumber(csvRecord.get(2));
                                ir.setInvoiceYear(year);
                                ir.setInvoiceAmount(amount);

                                list.add(ir);
                            }
                        }

                    } catch (Exception exception) {
                        showLoadingError();
                        csvParser.close();
                        break;

                    }
                }

                //Release csv file
                csvParser.close();
                
                return list;
    }
    
}
