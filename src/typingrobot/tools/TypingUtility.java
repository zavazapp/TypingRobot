package typingrobot.tools;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.*;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import typingrobot.TypingRobot;
import typingrobot.tools.CountDownTimer;
import typingrobot.models.InvoiceRow;
import typingrobot.controllers.interfaces.CurrentRowObservable;
import typingrobot.controllers.interfaces.CountDownCallback;

/**
 * This class instantiate Robot and starts typing. Input window (that receives
 * typing events) must be in focus, otherwise typing events move to another
 * focused window.
 *
 * @author Miodrag Spasic
 */
public class TypingUtility implements CountDownCallback {

    //main swith for typing thread
    private volatile static boolean RUNNING = true;

    //Countdown before typing starts
    //Injected here for handling conveniance
    private Label countDownLabel;

    //separation of countDown timer for start typing delay
    private CountDownTimer countDownTimer;

    //Interface responsible for tracking typing progres.
    //Fires when row is finished to send info to UI controler for selecting currently typing row.
    //Fires when typing of last row is done to invalidate UI butons.
    //
    //Main use of CurrentRowObservable is for user experiance
    // void onNextRow(int row);
    // void onLastRowFinished(int row);
    private CurrentRowObservable currentRowObservable;

    //Native Java Robot.class responsible for typing.
    //Simulates real kybord and mouse events.
    private Robot robot;

    //Rows for typing are sent to robot on equal periods (INJECT_PERIOD)
    //TODO - use to setup deley time between rows (miliseconds).
    //It does not have efect on Robot typing speed.
    private TimerTask rowInjectorTimerTask;
    private Timer timer;
    private final long INJECT_PERIOD = 200; //milliseconds//TODO expose to user
    private final long COUNTDOWN_TIME = 5000; //milliseconds//stays hardcoded

    //Hook to preferences
    private final Preferences preferences = new Preferences();
    private boolean addEnterCheckBox,
            addDoubleEnterCheckBox,
            addArrowDownCheckBox,
            typeIdsCheckBox,
            typeInvoiceInDscCheckBox;

    // AWT exception may occure if there is no active window.
    // For user side it is not likely to happen.
    // Robot class may use screen capture utility.
    // Since no such usage is implemented AWT exception is thrown.
    public TypingUtility(int delay) throws AWTException {
        this.robot = new Robot();
        this.robot.setAutoDelay(delay);

    }

    //map values from preferences in private variables for typing schema
    private void setPreferences() {
        addEnterCheckBox = preferences.getBoolean("addEnterCheckBox", false);
        addDoubleEnterCheckBox = preferences.getBoolean("addDoubleEnterCheckBox", false);
        addArrowDownCheckBox = preferences.getBoolean("addArrowDownCheckBox", false);
        typeIdsCheckBox = preferences.getBoolean("typeIdsCheckBox", false);
        typeInvoiceInDscCheckBox = preferences.getBoolean("typeInvoiceInDscCheckBox", false);
    }

    //<editor-fold desc="Setters for reusable components">
    public void setCountDownLabel(Label countDownLabel) {
        this.countDownLabel = countDownLabel;
    }

    public void setCurrentRowObservable(CurrentRowObservable currentRowObservable) {
        this.currentRowObservable = currentRowObservable;
    }

    private void setCountDownLabelVisible(boolean visible) {
        if (countDownLabel != null) {
            countDownLabel.setVisible(visible);
        }
    }

    private void setCountDownLabelText(String text) {
        if (countDownLabel != null) {
            countDownLabel.setText(text);
        }
    }
    //</editor-fold>

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

                    currentRowObservable.onNextRow(tempCount); //signal to UI

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

    public void stopTyping() {
        RUNNING = false;
        rowInjectorTimerTask.cancel();
        timer.cancel();
        countDownTimer.cancel();
        countDownLabel.setVisible(false);
    }

    @Override
    public void onTimeChanged(int sec) {
        //Label text must be changed on main thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setCountDownLabelText(String.valueOf(sec));
                if (sec == 0) {
                    setCountDownLabelVisible(false);
                }
            }
        });

    }

    // Rows are incejted from RowInjectorTimerTask
    // sat a period of INJECT_PERIOD.
    //
    // Robot idle time between key events is set by user in controler class
    // and passd in constructor TypingUtility(int delay);
    private void injectRow(InvoiceRow invoiceRow) {

        if (typeIdsCheckBox) {
            typeCharsequence(invoiceRow.getId());
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        typeCharsequence(invoiceRow.getPaymentCode());
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        if (typeInvoiceInDscCheckBox) {
            typeCharsequence(invoiceRow.getInvoiceNumber());
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } else {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        if (invoiceRow.getPaymentCode().equals("112")) {
            typeCharsequence(invoiceRow.getInvoiceNumber());
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } else {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        if (invoiceRow.getPaymentCode().equals("112")) {
            typeCharsequence(invoiceRow.getInvoiceYear());
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } else {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }

        typeCharsequence(invoiceRow.getInvoiceAmount());
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

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
    }

    //Supose that each field in InvoiceRow consists of
    //more than one character. So, type each char separately.
    private void typeCharsequence(CharSequence values) {

        for (int i = 0; i < values.length(); i++) {
            typeChar(values.charAt(i));
        }
    }

    //Intermediate method for typing.
    //Method from key switch statement in typeChar(char character) method
    //Assumption is that it can contain more keys.
    private void doType(int... values) {
        doType(values, 0, values.length);
    }

    //Final method for typing.
    private void doType(int[] keyCodes, int offset, int length) {

        //Avoiding infinite loop
        if (length == 0) {
            return;
        }

        if (RUNNING) {
            robot.keyPress(keyCodes[offset]);

            //Recursion of doType
            //Infinite loop state controled by lenght parameter.
            doType(keyCodes, offset + 1, length - 1);

            robot.keyRelease(keyCodes[offset]);
        }

    }

    //Have no better idea for generating key events
    //based on char value. 
    //Note: char may be formed with two or more keys.
    //<editor-fold desc="chars">
    public void typeChar(char character) {
        switch (character) {
            case 'a':
                doType(VK_A);
                break;
            case 'b':
                doType(VK_B);
                break;
            case 'c':
                doType(VK_C);
                break;
            case 'd':
                doType(VK_D);
                break;
            case 'e':
                doType(VK_E);
                break;
            case 'f':
                doType(VK_F);
                break;
            case 'g':
                doType(VK_G);
                break;
            case 'h':
                doType(VK_H);
                break;
            case 'i':
                doType(VK_I);
                break;
            case 'j':
                doType(VK_J);
                break;
            case 'k':
                doType(VK_K);
                break;
            case 'l':
                doType(VK_L);
                break;
            case 'm':
                doType(VK_M);
                break;
            case 'n':
                doType(VK_N);
                break;
            case 'o':
                doType(VK_O);
                break;
            case 'p':
                doType(VK_P);
                break;
            case 'q':
                doType(VK_Q);
                break;
            case 'r':
                doType(VK_R);
                break;
            case 's':
                doType(VK_S);
                break;
            case 't':
                doType(VK_T);
                break;
            case 'u':
                doType(VK_U);
                break;
            case 'v':
                doType(VK_V);
                break;
            case 'w':
                doType(VK_W);
                break;
            case 'x':
                doType(VK_X);
                break;
            case 'y':
                doType(VK_Y);
                break;
            case 'z':
                doType(VK_Z);
                break;
            case 'A':
                doType(VK_SHIFT, VK_A);
                break;
            case 'B':
                doType(VK_SHIFT, VK_B);
                break;
            case 'C':
                doType(VK_SHIFT, VK_C);
                break;
            case 'D':
                doType(VK_SHIFT, VK_D);
                break;
            case 'E':
                doType(VK_SHIFT, VK_E);
                break;
            case 'F':
                doType(VK_SHIFT, VK_F);
                break;
            case 'G':
                doType(VK_SHIFT, VK_G);
                break;
            case 'H':
                doType(VK_SHIFT, VK_H);
                break;
            case 'I':
                doType(VK_SHIFT, VK_I);
                break;
            case 'J':
                doType(VK_SHIFT, VK_J);
                break;
            case 'K':
                doType(VK_SHIFT, VK_K);
                break;
            case 'L':
                doType(VK_SHIFT, VK_L);
                break;
            case 'M':
                doType(VK_SHIFT, VK_M);
                break;
            case 'N':
                doType(VK_SHIFT, VK_N);
                break;
            case 'O':
                doType(VK_SHIFT, VK_O);
                break;
            case 'P':
                doType(VK_SHIFT, VK_P);
                break;
            case 'Q':
                doType(VK_SHIFT, VK_Q);
                break;
            case 'R':
                doType(VK_SHIFT, VK_R);
                break;
            case 'S':
                doType(VK_SHIFT, VK_S);
                break;
            case 'T':
                doType(VK_SHIFT, VK_T);
                break;
            case 'U':
                doType(VK_SHIFT, VK_U);
                break;
            case 'V':
                doType(VK_SHIFT, VK_V);
                break;
            case 'W':
                doType(VK_SHIFT, VK_W);
                break;
            case 'X':
                doType(VK_SHIFT, VK_X);
                break;
            case 'Y':
                doType(VK_SHIFT, VK_Y);
                break;
            case 'Z':
                doType(VK_SHIFT, VK_Z);
                break;
            case '`':
                doType(VK_BACK_QUOTE);
                break;
            case '0':
                doType(VK_0);
                break;
            case '1':
                doType(VK_1);
                break;
            case '2':
                doType(VK_2);
                break;
            case '3':
                doType(VK_3);
                break;
            case '4':
                doType(VK_4);
                break;
            case '5':
                doType(VK_5);
                break;
            case '6':
                doType(VK_6);
                break;
            case '7':
                doType(VK_7);
                break;
            case '8':
                doType(VK_8);
                break;
            case '9':
                doType(VK_9);
                break;
            case '-':
                doType(VK_MINUS);
                break;
            case '=':
                doType(VK_EQUALS);
                break;
            case '~':
                doType(VK_SHIFT, VK_BACK_QUOTE);
                break;
            case '!':
                doType(VK_EXCLAMATION_MARK);
                break;
            case '@':
                doType(VK_AT);
                break;
            case '#':
                doType(VK_NUMBER_SIGN);
                break;
            case '$':
                doType(VK_DOLLAR);
                break;
            case '%':
                doType(VK_SHIFT, VK_5);
                break;
            case '^':
                doType(VK_CIRCUMFLEX);
                break;
            case '&':
                doType(VK_AMPERSAND);
                break;
            case '*':
                doType(VK_ASTERISK);
                break;
            case '(':
                doType(VK_LEFT_PARENTHESIS);
                break;
            case ')':
                doType(VK_RIGHT_PARENTHESIS);
                break;
            case '_':
                doType(VK_UNDERSCORE);
                break;
            case '+':
                doType(VK_PLUS);
                break;
            case '\t':
                doType(VK_TAB);
                break;
            case '\n':
                doType(VK_ENTER);
                break;
            case '[':
                doType(VK_OPEN_BRACKET);
                break;
            case ']':
                doType(VK_CLOSE_BRACKET);
                break;
            case '\\':
                doType(VK_BACK_SLASH);
                break;
            case '{':
                doType(VK_SHIFT, VK_OPEN_BRACKET);
                break;
            case '}':
                doType(VK_SHIFT, VK_CLOSE_BRACKET);
                break;
            case '|':
                doType(VK_SHIFT, VK_BACK_SLASH);
                break;
            case ';':
                doType(VK_SEMICOLON);
                break;
            case ':':
                doType(VK_COLON);
                break;
            case '\'':
                doType(VK_QUOTE);
                break;
            case '"':
                doType(VK_QUOTEDBL);
                break;
            case ',':
                doType(VK_COMMA);
                break;
            case '<':
                doType(VK_SHIFT, VK_COMMA);
                break;
            case '.':
                doType(VK_PERIOD);
                break;
            case '>':
                doType(VK_SHIFT, VK_PERIOD);
                break;
            case '/':
                doType(VK_SLASH);
                break;
            case '?':
                doType(VK_SHIFT, VK_SLASH);
                break;
            case ' ':
                doType(VK_SPACE);
                break;
            default:
                doType(VK_0);
        }
    }
    //</editor-fold>

}
