package typingrobot.tools.fileLoading.referenceStringLoaders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import typingrobot.models.MasterCardItem;
import typingrobot.models.RezimItem;

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
public class ReferenceStringLoader_Rezim {

    public ObservableList<RezimItem> list = FXCollections.observableArrayList();

    public ReferenceStringLoader_Rezim() {
    }

    public ObservableList<RezimItem> getList(String args) throws FileNotFoundException, IOException {
        System.out.println("loading..." + " Rezzim items");

        ArrayList<String[]> table;

        table = new TableParser_Rezim().getTableArray(args);

        for (String[] row : table) {

            RezimItem item = new RezimItem();
            item.setReference(row[0]);
            
            list.add(item);
        }
        return list;

    }
}