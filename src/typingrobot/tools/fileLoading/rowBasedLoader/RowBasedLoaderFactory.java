package typingrobot.tools.fileLoading.rowBasedLoader;

import java.io.File;
import typingrobot.controllers.interfaces.IErrorInterface;
import typingrobot.tools.fileLoading.FileLoadable;


/**
 * 09.05.2021.
 *
 * @author Miodrag Spasic
 
 Depending on file type recognized in fileExtension, RowBasedLoaderFactory
 instantiates corresponding class with specific implementation of FileLoadable.
 */
@Deprecated
public class RowBasedLoaderFactory {

    public static FileLoadable get(String fileExtension, File fileToLoad, boolean hasHeaders, int startRow, IErrorInterface errorCallback) {

        switch (fileExtension) {
            case "xlsx":
                return new XLSXFileLoader_RowBased(fileToLoad, hasHeaders, startRow, errorCallback);
            case "xls":
                return new XLSFileLoader_RowBased(fileToLoad, hasHeaders, startRow, errorCallback);
            case "csv":
                return new CSVFileLoader_RowBased(fileToLoad, hasHeaders, startRow, errorCallback);
            default:
                return new CSVFileLoader_RowBased(fileToLoad, hasHeaders, startRow, errorCallback);

        }
    }

}
