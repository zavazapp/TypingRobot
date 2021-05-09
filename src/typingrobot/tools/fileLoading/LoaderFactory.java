package typingrobot.tools.fileLoading;

import java.io.File;
import typingrobot.controllers.interfaces.IErrorInterface;


/**
 * 09.05.2021.
 *
 * @author Miodrag Spasic
 * 
 * Depending on file type recognized in fileExtension, LoaderFactory
 * instantiates corresponding class with specific implementation of FileLoadable.
 */
public class LoaderFactory {

    public static FileLoadable get(String fileExtension, File fileToLoad, boolean hasHeaders, int startRow, IErrorInterface errorCallback) {

        switch (fileExtension) {
            case "xlsx":
                return new XLSXFileLoader(fileToLoad, hasHeaders, startRow, errorCallback);
            case "xls":
                return new XLSFileLoader(fileToLoad, hasHeaders, startRow, errorCallback);
            case "csv":
                return new CSVFileLoader(fileToLoad, hasHeaders, startRow, errorCallback);
            default:
                return new CSVFileLoader(fileToLoad, hasHeaders, startRow, errorCallback);

        }
    }

}
