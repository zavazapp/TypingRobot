package typingrobot.models;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Miodrag Spasic
 */
public class InvoiceRow {
    private SimpleStringProperty  id;
    private SimpleStringProperty  paymentCode;
    private SimpleStringProperty  invoiceNumber;
    private SimpleStringProperty  invoiceYear;
    private SimpleStringProperty  invoiceAmount;

    public InvoiceRow() {
        this.id = new SimpleStringProperty("");
        this.paymentCode = new SimpleStringProperty("");
        this.invoiceNumber = new SimpleStringProperty("");
        this.invoiceYear = new SimpleStringProperty("");
        this.invoiceAmount = new SimpleStringProperty("");
    }

    
    public InvoiceRow(
            SimpleStringProperty id, 
            SimpleStringProperty paymentCode, 
            SimpleStringProperty invoiceNumber, 
            SimpleStringProperty invoiceYear, 
            SimpleStringProperty invoiceAmount) {
        
        this.id = id;
        this.paymentCode = paymentCode;
        this.invoiceNumber = invoiceNumber;
        this.invoiceYear = invoiceYear;
        this.invoiceAmount = invoiceAmount;
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getPaymentCode() {
        return paymentCode.get();
    }
    
    public void setPaymentCode(String paymentCode) {
        this.paymentCode.set(paymentCode);
    }

    public String getInvoiceNumber() {
        return invoiceNumber.get();
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber.set(invoiceNumber);
    }

    public String getInvoiceYear() {
        return invoiceYear.get();
    }

    public void setInvoiceYear(String invoiceYear) {
        this.invoiceYear.set(invoiceYear);
    }

    public String getInvoiceAmount() {
        return invoiceAmount.get();
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount.set(invoiceAmount);
    }

}
