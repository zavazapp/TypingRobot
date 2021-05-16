package typingrobot.utils.typingUtils;

import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javafx.collections.ObservableList;
import typingrobot.TypingRobot;
import typingrobot.models.InvoiceRow;
import typingrobot.tools.CountDownTimer;

/**
 * This class instantiate Robot and starts typing. Input window (that receives
 * typing events) must be in focus, otherwise typing events move to another
 * focused window.
 *
 * @author Miodrag Spasic
 */
public class TypingUtility_Invoices extends TypingUtility {


    // AWT exception may occure if there is no active window.
    // For user side it is not likely to happen.
    // Robot class may use screen capture utility.
    // Since no such usage is implemented AWT exception is thrown.

    public TypingUtility_Invoices(int delay) throws AWTException {
        super(delay);

    }

    /**
     *
     * @param data - ObservableList<InvoiceRow> formed from imported file
     * @param startingPosition - user row selection on the table
     * @throws AWTException - no need to handle in this case
     *
     * Called from controller class on start button click.
     */
    public void startTyping(ObservableList<InvoiceRow> data, int startingPosition) throws AWTException {
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
    private void injectRow(InvoiceRow invoiceRow) {

        //id
        if (typeIdsCheckBox) {
            typeCharsequence(invoiceRow.getId());
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        //payment code
        typeCharsequence(invoiceRow.getPaymentCode());
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        //description
        if (typeInvoiceInDscCheckBox || !invoiceRow.getPaymentCode().contains("112")) {
            typeCharsequence(invoiceRow.getInvoiceNumber());
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } else {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        //invoice
        if (invoiceRow.getPaymentCode().contains("112")) {
            typeCharsequence(invoiceRow.getInvoiceNumber());
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } else {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        //year
        if (invoiceRow.getPaymentCode().contains("112")) {
            typeCharsequence(invoiceRow.getInvoiceYear());
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } else {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        //amount
        typeCharsequence(invoiceRow.getInvoiceAmount());
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        //after row ends
        if (addEnterCheckBox) {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }
        
        if (addDoubleEnterCheckBox) {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }
        
        if (addArrowDownCheckBox) {
            robot.keyPress(KeyEvent.VK_DOWN);
            robot.keyRelease(KeyEvent.VK_DOWN);
        }
        
        int currentRow;
        try {
            currentRow = Integer.parseInt(invoiceRow.getId());
        } catch (Exception e) {
            currentRow = 0;
            
        }
        currentRowObservable.onNextRow(currentRow); //signal to UI
    }


}
