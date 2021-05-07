package typingrobot.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.models.InvoiceRow;

/**
 * 04.05.2021. - locked - stable 06.05.2021. - added user choice for header row
 *
 * @author Miodrag Spasic
 *
 * @FileLoader needs a File and its extension
 * @Return ObservableList<InvoiceRow>
 */
public class FileLoader {

    private final File fileToLoad;
    private final IErrorInterface errorCalback;
    private StringBuilder errorStringBuilder = new StringBuilder("Error!");
    private Calendar calendar;

    //Variable that holds user choice of table property `hasHeadres`
    private final boolean hasHeader;

    public FileLoader(File fileToLoad, boolean hasHeader, IErrorInterface errorCalback) {
        this.fileToLoad = fileToLoad;
        this.hasHeader = hasHeader;
        this.errorCalback = errorCalback;
        calendar = Calendar.getInstance();
    }

    /**
     * XLSX Classes : XSSFWorkbook: It is a class representing the XLSX file.
     * XSSFSheet: It is a class representing the sheet in an XLSX file. XSSFRow:
     * It is a class representing a row in the sheet of XLSX file. XSSFCell: It
     * is a class representing a cell in a row of XLSX file.
     *
     * XLS Classes : HSSFWorkbook: It is a class representing the XLS file.
     * HSSFSheet: It is a class representing the sheet in an XLS file. HSSFRow:
     * It is a class representing a row in the sheet of XLS file. HSSFCell: It
     * is a class representing a cell in a row of XLS file.
     *
     */
    public ObservableList<InvoiceRow> getList(String fileExtension) throws FileNotFoundException, IOException {
        ObservableList<InvoiceRow> list = FXCollections.observableArrayList();

        switch (fileExtension) {

            case "xlsx":
                System.out.println("loading..." + ".xlsx");

                FileInputStream inputStream = new FileInputStream(fileToLoad);

                XSSFWorkbook w = null;
                XSSFSheet s = null;

                try {
                    w = new XSSFWorkbook(inputStream);
                    s = w.getSheetAt(0);
                } catch (Exception e) {
                    showTableError();
                    break;
                }

                //count columns in table
                //if there are 5 columns, one of them is paymentCode
                //else, payment code is not included
                short colCount = s.getRow(0).getLastCellNum();

                try {
                    for (Row row : s) {
                        if (row.getRowNum() != 0 || !hasHeader) {
                            list.add(getObjectFromRow(colCount, row));

                        }
                    }
                } catch (Exception e) {
                    showTableError();
                }

                //Release excel file
                inputStream.close();
                break;

            case "xls":
                System.out.println("loading..." + ".xls");

                FileInputStream inputStream1 = new FileInputStream(fileToLoad);

                HSSFWorkbook w1 = null;
                HSSFSheet s1 = null;
                try {
                    w1 = new HSSFWorkbook(inputStream1);
                    s1 = w1.getSheetAt(0);
                } catch (Exception e) {
                    showTableError();
                    break;
                }

                //count columns in table
                //if there are 5 columns, one of them is paymentCode
                //else, payment code is not included
                short colCount1 = s1.getRow(0).getLastCellNum();

                try {
                    for (Row row : s1) {
                        if (row.getRowNum() != 0 || !hasHeader) {
                            list.add(getObjectFromRow(colCount1, row));
                        }
                    }
                } catch (Exception e) {
                    showTableError();
                    break;
                }

                //Release excel file
                inputStream1.close();
                break;

            //any ComaSeparatedValues with header row
            default:
                System.out.println("loading..." + ".csv");

                BufferedReader fileReader = null;
                CSVParser csvParser = null;

                fileReader = new BufferedReader(new FileReader(fileToLoad));

                try {
                    csvParser = new CSVParser(
                            fileReader,
                            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                } catch (Exception e) {
                    showTableError();
                    break;
                }

                Iterable<CSVRecord> csvRecords = csvParser.getRecords();

                for (CSVRecord csvRecord : csvRecords) {
                    InvoiceRow ir = new InvoiceRow();

                    int recCount = csvRecord.size();

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
                            if (csvRecord.getRecordNumber() != 0 || !hasHeader) {
                                ir.setId(id);
                                ir.setPaymentCode("112");
                                ir.setInvoiceNumber(csvRecord.get(1));
                                ir.setInvoiceYear(year);
                                ir.setInvoiceAmount(amount);

                                list.add(ir);
                            }
                        }

                        if (recCount == 5) {
                            if (csvRecord.getRecordNumber() != 0 || !hasHeader) {
                                ir.setId(id);
                                ir.setPaymentCode(csvRecord.get(1));
                                ir.setInvoiceNumber(csvRecord.get(2));
                                ir.setInvoiceYear(year);
                                ir.setInvoiceAmount(amount);

                                list.add(ir);
                            }
                        }

                    } catch (Exception exception) {
                        showTableError();
                        break;

                    }
                }

                //Release csv file
                csvParser.close();
                break;
        }

        return list;
    }

    private void showTableError() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Usualy, this error is show if excel or csv file does not contain well formed table.");
        alert.setTitle("Error parsing file");
        errorStringBuilder.append("Error parsing file");
        setErrorLabel("Error parsing file");
        alert.show();
    }

    private InvoiceRow getObjectFromRow(short colCount, Row row) {
        InvoiceRow ir = new InvoiceRow();

        //remove dots from id
        String id = row.getCell(0).toString();
        if (id.contains(".")) {
            id = id.split("[.]")[0].trim();
        }

        //remove dots from year
        String year = row.getCell(colCount - 2).toString();
        if (year.contains(".")) {
            year = year.split("[.]")[0].trim();
        }

        //make sure amount is in format nn.nn or nn
        String amount = row.getCell(colCount - 1).toString();

        if (String.valueOf(amount.charAt(amount.length() - 3)).equals(",")) {
            amount = amount.replace(",", ".");
        }

        if (String.valueOf(amount.charAt(amount.length() - 3)).equals(".")) {
            if (amount.contains(",")) {
                amount = amount.replace(",", "");
            }
        }

        switch (colCount) {

            case 4:
                //Excel file has 4 columns.
                //That means there is no column named "Patment code"
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
                ir.setPaymentCode(row.getCell(1).toString());
                ir.setInvoiceNumber(row.getCell(2).toString());
                ir.setInvoiceYear(year);
                ir.setInvoiceAmount(amount);

                break;
        }
        validateValues(ir);
        return ir;
    }

    private void setErrorLabel(String error) {
        errorCalback.onAnyError(error);
    }

    private void validateValues(InvoiceRow ir) {
        if (ir.getInvoiceAmount().isEmpty()) {
            errorStringBuilder
                    .append("\nInvoice ammount is missing in row: ")
                    .append(ir.getId());
        }
        if (ir.getPaymentCode().equals("112") && ir.getInvoiceNumber().isEmpty()) {
            errorStringBuilder
                    .append("\nInvoice number is missing in row: ")
                    .append(ir.getId());
        }
        if (ir.getPaymentCode().equals("112") && ir.getInvoiceYear().isEmpty()) {
            errorStringBuilder
                    .append("\nInvoice year is missing in row: ")
                    .append(ir.getId());
        }
        String regex = "[0-9.]*$";
        if (!ir.getInvoiceAmount().matches(regex)) {
            errorStringBuilder
                    .append("\nInvoice number contains invalid characters in row: ")
                    .append(ir.getId());
        }

        if (Integer.parseInt(ir.getInvoiceYear()) > calendar.get(Calendar.YEAR)) {
            errorStringBuilder
                    .append("\nInvoice year is greater than current in row: ")
                    .append(ir.getId());
        }

        if (Integer.parseInt(ir.getInvoiceYear()) < calendar.get(Calendar.YEAR) - 5 ) {
            errorStringBuilder
                    .append("\nInvoice year does not have logical value in row: ")
                    .append(ir.getId());
        }

        if (!errorStringBuilder.toString().equals("Error!")) {
            errorCalback.onAnyError(errorStringBuilder.toString());
        }
    }
}
