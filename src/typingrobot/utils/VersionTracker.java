package typingrobot.utils;

/**
 *
 * @author Miodrag Spasic
 */
public class VersionTracker {

    public static String getVersion() {
        //First alpha release
        //String VERSION = "version: 0.1.alpha\n\n2021"; //09 May 2021

        //String based loader added in hope to reduce dependancy on excel table format
        //String VERSION = "version: 0.2.alpha\n\n2021"; //12 May 2021
        //Added TablePaser for different templates of excel specification
        //String VERSION = "version: 0.3.alpha\n\n2021"; //14 May 2021
        //Added Paste function in addition to drag and drop
        //Added handler to pasted string in addition to pasted file
        //Added functionality for automated payment orders verification.
        //Added automated payment order statistic change for decade ourpose
        String VERSION = "version: 0.4.alpha\n\n2021"; //16 May 2021

        return VERSION;
    }
}
