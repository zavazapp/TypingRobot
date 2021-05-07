package typingrobot.controllers.interfaces;

/**
 *
 * @author Miodrag Spasic
 * 
 * Typing goes row by row.
 * When current row typing is finished, and next row is started,
 * this callback is fired.
 */
public interface CurrentRowObservable {
    void onNextRow(int row);
    void onLastRowFinished(int row);
}
