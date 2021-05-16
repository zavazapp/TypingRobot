package typingrobot.tools.fileLoading.referenceStringLoaders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * 14.05.2022.
 *
 * @author Miodrag Spasic This class handles different excel templates
 *
 * This class converts excel string to table.
 */
public class TableParser_MasterCard {


    public ArrayList<String[]> getTableArray(String args, String paymentCode, String mb) {
        ArrayList<String[]> table = new ArrayList<>();

        String[] rows = args.split("\n");

        for (int i = 0; i < rows.length; i++) {
            String[] tempRow = Arrays.stream(rows[i].split("\n"))
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String t) {
                            return !t.trim().isEmpty();
                        }
                    }).toArray(String[]::new);
            if (tempRow.length > 0) {
                String[] finalRow = new String[3];

                //refference
                finalRow[0] = tempRow[0].trim();

                //ordering party
                finalRow[1] = mb.trim();

                //payment code
                finalRow[2] = paymentCode.trim();
                
                table.add(finalRow);

            }
            
        }

        return table;
    }
}
