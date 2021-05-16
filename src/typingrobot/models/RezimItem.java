package typingrobot.models;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author MiodragSpasic
 */
public class RezimItem {

    private SimpleStringProperty reference;

    public RezimItem() {
        this.reference = new SimpleStringProperty("");
    }

    public RezimItem(SimpleStringProperty reference) {
        this.reference = reference;
    }

    public String getReference() {
        return reference.get();
    }

    public void setReference(String reference) {
        this.reference.set(reference);
    }

}
