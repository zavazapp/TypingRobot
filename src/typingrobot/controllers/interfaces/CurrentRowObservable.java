package typingrobot.controllers.interfaces;

/**
 *
 * @author Miodrag Spasic
 * 
 * Typing goes row by row.
 * When current row typing is finished, or next row is started,
 * these callbacks are fired.
 */
public interface CurrentRowObservable {
    void onNextRow(int row);
    void onLastRowFinished(int row);
}
