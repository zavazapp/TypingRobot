package typingrobot.utils.typingUtils;

import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javafx.collections.ObservableList;
import typingrobot.TypingRobot;
import typingrobot.models.MasterCardItem;
import typingrobot.tools.CountDownTimer;
import static typingrobot.utils.typingUtils.TypingUtility.RUNNING;

/**
 * This class is extension of TypingUtility, and provides methods specific to
 * MasterCard object.
 *
 *
 * @author Miodrag Spasic
 */
public class TypingUtility_MasterCard extends TypingUtility {

    // AWT exception may occure if there is no active window.
    // For user side it is not likely to happen.
    // Robot class may use screen capture utility.
    // Since no such usage is implemented AWT exception is thrown.
    public TypingUtility_MasterCard(int delay) throws AWTException {
        super(delay);
    }

    /**
     *
     * @param data - ObservableList<MasterCardItem> formed from pasted string
     * @param startingPosition - user row selection on the table
     * @throws AWTException - no need to handle in this case
     *
     * Called from controller class on start button click.
     */
    public void startTyping(ObservableList<MasterCardItem> data, int startingPosition) throws AWTException {
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

    private void injectRow(MasterCardItem row) {

        //reference - broj naloga
        typeCharsequence(row.getReference());
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        //stavka u nalogu
        typeCharsequence("1");
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        //mb
        typeCharsequence(row.getOrderingParty());
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        //payment code
        typeCharsequence(row.getPaymentCode());
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        //F12
        robot.keyPress(KeyEvent.VK_F12);
        robot.keyRelease(KeyEvent.VK_F12);

        //arrow down
        robot.keyPress(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_DOWN);

        //enter
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        currentRowObservable.onNextRow(rowCount); //signal to UI
        rowCount++;
    }

}
