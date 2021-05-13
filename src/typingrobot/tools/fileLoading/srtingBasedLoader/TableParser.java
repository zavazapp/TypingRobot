package typingrobot.tools.fileLoading.srtingBasedLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 14.05.2022.
 *
 * @author Miodrag Spasic
 * This class handles different excel templates 
 *
 * This class converts excel string to table.
 */
public class TableParser {

    private Pattern pattern;
    private int rowId = 1;

    public ArrayList<String[]> getTableArray(String args, String specialType, int firstTableRow) {
        ArrayList<String[]> table = new ArrayList<>();

        System.out.println(args);
        String[] rows = args.split("\n");

        switch (specialType) {
            case "NEXE":
                pattern = Pattern.compile("[0-9]{2}[.][0-9]{2}[.][0-9]{2}.*");

                //Try to find biggining of a table
                for (int i = 0; i < rows.length; i++) {
                    String[] row = Arrays.stream(rows[i].split("\t"))
                            .filter(new Predicate<String>() {
                                @Override
                                public boolean test(String t) {
                                    return !t.trim().isEmpty();
                                }
                            }).toArray(String[]::new);

                    boolean firstCheck = false;
                    boolean secondCheck = false;
                    try {
                        firstCheck = pattern.matcher(row[0]).matches();
                        secondCheck = pattern.matcher(row[1]).matches();
                    } catch (Exception e) {
                        //Do nothing
                    }

                    if (row.length > 8 && firstCheck && secondCheck) {
                        firstTableRow = i;
                        break;
                    }
                }

                System.out.println("firstTableRow" + firstTableRow);
                //END try to find first row

                rowId = 1;
                for (int i = firstTableRow; i < rows.length; i++) {
                    String[] tempRow = Arrays.stream(rows[i].split("\t"))
                            .filter(new Predicate<String>() {
                                @Override
                                public boolean test(String t) {
                                    return !t.trim().isEmpty();
                                }
                            }).toArray(String[]::new);
                    if (tempRow.length > 8) {
                        String[] finalRow = new String[5];

                        //id
                        finalRow[0] = String.valueOf(rowId);
                        rowId++;

                        //payment code
                        finalRow[1] = "112";

                        //invoice number
                        finalRow[2] = String.valueOf(tempRow[5].trim());

                        //year
                        if (tempRow[0].trim().length() > 7) {
                            finalRow[3] = String.valueOf("20" + tempRow[0].trim().substring(6, 8));
                        } else {
                            finalRow[3] = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        }

                        //amount
                        finalRow[4] = String.valueOf(tempRow[9]);
                        table.add(finalRow);
                    }
                }
                break;

            case "AIK":

                //Try to find biggining of a table
                for (int i = 0; i < rows.length; i++) {
                    String[] row = Arrays.stream(rows[i].split("\t"))
                            .filter(new Predicate<String>() {
                                @Override
                                public boolean test(String t) {
                                    return !t.trim().isEmpty();
                                }
                            }).toArray(String[]::new);
                    if (row.length > 3 && row[0].equals("1")) {
                        firstTableRow = i;
                        break;
                    }
                }

                System.out.println("firstTableRow" + firstTableRow);
                //END try to find first row

                for (int i = firstTableRow; i < rows.length; i++) {
                    String[] tempRow = Arrays.stream(rows[i].split("\t"))
                            .filter(new Predicate<String>() {
                                @Override
                                public boolean test(String t) {
                                    return !t.trim().isEmpty();
                                }
                            }).toArray(String[]::new);
                    if (tempRow.length > 3) {
                        String[] finalRow = new String[5];

                        //id
                        finalRow[0] = tempRow[0];

                        //payment code
                        finalRow[1] = "112";

                        //invoice number
                        finalRow[2] = tempRow[1];

                        //year
                        finalRow[3] = tempRow[2].trim();

                        //amount
                        finalRow[4] = tempRow[4];
                        
                        table.add(finalRow);
                    }
                }
                break;

            default:
                //Try to find biggining of a table
                for (int i = 0; i < rows.length; i++) {
                    String[] row = Arrays.stream(rows[i].split("\t"))
                            .filter(new Predicate<String>() {
                                @Override
                                public boolean test(String t) {
                                    return !t.trim().isEmpty();
                                }
                            }).toArray(String[]::new);
                    if (row.length > 3 && row[0].equals("1")) {
                        firstTableRow = i;
                        break;
                    }
                }

                System.out.println("firstTableRow" + firstTableRow);
                //END try to find first row

                for (int i = firstTableRow; i < rows.length; i++) {
                    String[] row = Arrays.stream(rows[i].split("\t"))
                            .filter(new Predicate<String>() {
                                @Override
                                public boolean test(String t) {
                                    return !t.trim().isEmpty();
                                }
                            }).toArray(String[]::new);
                    if (row.length > 3) {
                        table.add(row);
                    }
                }
                break;
        }

        return table;
    }
}
