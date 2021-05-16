package typingrobot.tools.fileLoading.referenceStringLoaders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import typingrobot.models.MasterCardItem;

/**
 *
 * @author Miodrag Spasic
 *
 * 16.05.2021. - This class is added to support paste option for plain text, tab
 * delimited.
 *
 * Used to select references in excel and copy-paste in robot. Usage: "Podaci za
 * NBS", "Rezim"
 */
public class ReferenceStringLoader_MasterCard {

    public ObservableList<MasterCardItem> list = FXCollections.observableArrayList();

    public ReferenceStringLoader_MasterCard() {
    }

    public ObservableList<MasterCardItem> getList(String args, String paymentCode, String mb) throws FileNotFoundException, IOException {
        System.out.println("loading..." + " references");

        int colCount = 0;
        ArrayList<String[]> table;

        table = new TableParser_MasterCard().getTableArray(args, paymentCode, mb);

        colCount = table.get(0).length;

        for (String[] row : table) {

            MasterCardItem item = new MasterCardItem();
            item.setReference(row[0]);
            item.setOrderingParty(row[1]);
            item.setPaymentCode(row[2]);
            
            list.add(item);
        }
        return list;

    }
}