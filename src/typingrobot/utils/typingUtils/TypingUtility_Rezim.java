package typingrobot.utils.typingUtils;

import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javafx.collections.ObservableList;
import typingrobot.TypingRobot;
import typingrobot.models.RezimItem;
import typingrobot.tools.CountDownTimer;
import static typingrobot.utils.typingUtils.TypingUtility.RUNNING;

/**
 * This class is extension of TypingUtility, and provides methods specific to
 * Rezim object.
 *
 *
 * @author Miodrag Spasic
 */
public class TypingUtility_Rezim extends TypingUtility {

    // AWT exception may occure if there is no active window.
    // For user side it is not likely to happen.
    // Robot class may use screen capture utility.
    // Since no such usage is implemented AWT exception is thrown.
    public TypingUtility_Rezim(int delay) throws AWTException {
        super(delay);
    }

    /**
     *
     * @param data - ObservableList<RezimItem> formed from pasted string
     * @param startingPosition - user row selection on the table
     * @throws AWTException - no need to handle in this case
     *
     * Called from controller class on start button click.
     */
    public void startTyping(ObservableList<RezimItem> data, int startingPosition) throws AWTException {
        //set preferences variables
        setPreferences();

        timer = new Timer(true); //set as Daemon
        RUNNING = true;

        rowInjectorTimerTask = new TimerTask() {
            int tempCount = startingPosition;

            @Override
            public void run() {
                if (RUNNING) {

                    injectRow(data.get(tempCount));
                    tempCount++;

                    //if no more rows available
                    if (tempCount == data.size()) {
                        stopTyping();
                        currentRowObservable.onLastRowFinished(tempCount); //signal to UI
                    }

                    if (TypingRobot.WINDOW_CLOSED) {
                        stopTyping();
                    }

                }
            }
        };

        timer.scheduleAtFixedRate(rowInjectorTimerTask, COUNTDOWN_TIME, INJECT_PERIOD);
        setCountDownLabelVisible(true);

        countDownTimer = new CountDownTimer((int) COUNTDOWN_TIME / 1000, this);
        countDownTimer.startCountDown();
    }

    // Rows are incejted from RowInjectorTimerTask
    // sat a period of INJECT_PERIOD.
    //
    // Robot idle time between key events is set by user in controler class
    // and passd in constructor TypingUtility_Invoices(int delay);
    int rowCount = 1;

    private void injectRow(RezimItem item) {

        //reference - broj naloga
        typeCharsequence(item.getReference());
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        robot.delay(1000);

        //ALT+D
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_ALT);

        robot.delay(1000);
        
        //ALT+E
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_E);
        robot.keyRelease(KeyEvent.VK_E);
        robot.keyRelease(KeyEvent.VK_ALT);

        currentRowObservable.onNextRow(rowCount); //signal to UI
        rowCount++;
    }

}
