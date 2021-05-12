package typingrobot.utils;

import java.text.DecimalFormat;

/**
 *
 * @author Miodrag Spasic
 */
public class NumberFormatUtils {

    public static String getFormatedNumber(long numberToFormat) {
        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");

        return String.valueOf(new Float(df.format(numberToFormat)));

    }
}
