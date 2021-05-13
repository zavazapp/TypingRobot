package typingrobot.tools.fileLoading;

import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.collections.ObservableList;
import typingrobot.models.InvoiceRow;

/**
 *
 * @author Miodrag Spasic
 *
 * Implemented by file loaders
 */
public interface FileLoadable {

    ObservableList<InvoiceRow> getList(String fileExtension, String specialType) throws FileNotFoundException, IOException;

    double getTotalSum();
}
