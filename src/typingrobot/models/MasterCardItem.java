package typingrobot.models;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author MiodragSpasic
 */
public class MasterCardItem {

    private SimpleStringProperty reference;
    private SimpleStringProperty orderingParty;
    private SimpleStringProperty paymentCode;

    public MasterCardItem() {
        this.reference = new SimpleStringProperty("");
        this.orderingParty = new SimpleStringProperty("");
        this.paymentCode = new SimpleStringProperty("");
    }

    public MasterCardItem(SimpleStringProperty reference, SimpleStringProperty orderingParty, SimpleStringProperty paymentCode) {
        this.reference = reference;
        this.orderingParty = orderingParty;
        this.paymentCode = paymentCode;
    }

    public String getReference() {
        return reference.get();
    }

    public void setReference(String reference) {
        this.reference.set(reference);
    }

    public String getOrderingParty() {
        return orderingParty.get();
    }

    public void setOrderingParty(String orderingParty) {
        this.orderingParty.set(orderingParty);
    }

    public String getPaymentCode() {
        return paymentCode.get();
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode.set(paymentCode);
    }

    
}
