package typingrobot.utils.typingUtils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.control.Label;
import typingrobot.controllers.interfaces.CountDownCallback;
import typingrobot.controllers.interfaces.CurrentRowObservable;
import typingrobot.tools.CountDownTimer;
import typingrobot.tools.Preferences;

/**
 *
 * @author Korisnik
 */
public abstract class TypingUtility implements CountDownCallback {

    //main swith for typing thread
    protected static volatile boolean RUNNING = true;
    //Countdown before typing starts
    //Injected here for handling conveniance
    protected Label countDownLabel;
    //separation of countDown timer for start typing delay
    protected CountDownTimer countDownTimer;
    //Interface responsible for tracking typing progres.
    //Fires when row is finished to send info to UI controler for selecting currently typing row.
    //Fires when typing of last row is done to invalidate UI butons.
    //
    //Main use of CurrentRowObservable is for user experiance
    // void onNextRow(int row);
    // void onLastRowFinished(int row);
    protected CurrentRowObservable currentRowObservable;
    //Native Java Robot.class responsible for typing.
    //Simulates real kybord and mouse events.
    protected Robot robot;
    //Rows for typing are sent to robot on equal periods (INJECT_PERIOD)
    //TODO - use to setup deley time between rows (miliseconds).
    //It does not have efect on Robot typing speed.
    protected TimerTask rowInjectorTimerTask;
    protected Timer timer;
    protected final long INJECT_PERIOD = 200; //milliseconds//TODO expose to user
    protected final long COUNTDOWN_TIME = 5000; //milliseconds//stays hardcoded
    //Hook to preferences
    protected final Preferences preferences = new Preferences();

    public boolean addEnterCheckBox,
            addDoubleEnterCheckBox,
            addArrowDownCheckBox,
            typeIdsCheckBox,
            typeInvoiceInDscCheckBox;

    public TypingUtility(int delay) throws AWTException {
        this.robot = new Robot();
        this.robot.setAutoDelay(delay);

    }

    //map values from preferences in private variables for typing schema
    protected void setPreferences() {
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

    protected void setCountDownLabelVisible(boolean visible) {
        if (countDownLabel != null) {
            countDownLabel.setVisible(visible);
        }
    }

    protected void setCountDownLabelText(String text) {
        if (countDownLabel != null) {
            countDownLabel.setText(text);
        }
    }
    //</editor-fold>

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

    //Supose that each field in InvoiceRow consists of
    //more than one character. So, type each char separately.
    protected void typeCharsequence(CharSequence values) {
        for (int i = 0; i < values.length(); i++) {
            typeChar(values.charAt(i));
        }
    }

    //Intermediate method for typing.
    //Method from key switch statement in typeChar(char character) method
    //Assumption is that it can contain more keys.
    protected void doType(int... values) {
        doType(values, 0, values.length);
    }

    //Final method for typing.
    protected void doType(int[] keyCodes, int offset, int length) {
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
                doType(KeyEvent.VK_A);
                break;
            case 'b':
                doType(KeyEvent.VK_B);
                break;
            case 'c':
                doType(KeyEvent.VK_C);
                break;
            case 'd':
                doType(KeyEvent.VK_D);
                break;
            case 'e':
                doType(KeyEvent.VK_E);
                break;
            case 'f':
                doType(KeyEvent.VK_F);
                break;
            case 'g':
                doType(KeyEvent.VK_G);
                break;
            case 'h':
                doType(KeyEvent.VK_H);
                break;
            case 'i':
                doType(KeyEvent.VK_I);
                break;
            case 'j':
                doType(KeyEvent.VK_J);
                break;
            case 'k':
                doType(KeyEvent.VK_K);
                break;
            case 'l':
                doType(KeyEvent.VK_L);
                break;
            case 'm':
                doType(KeyEvent.VK_M);
                break;
            case 'n':
                doType(KeyEvent.VK_N);
                break;
            case 'o':
                doType(KeyEvent.VK_O);
                break;
            case 'p':
                doType(KeyEvent.VK_P);
                break;
            case 'q':
                doType(KeyEvent.VK_Q);
                break;
            case 'r':
                doType(KeyEvent.VK_R);
                break;
            case 's':
                doType(KeyEvent.VK_S);
                break;
            case 't':
                doType(KeyEvent.VK_T);
                break;
            case 'u':
                doType(KeyEvent.VK_U);
                break;
            case 'v':
                doType(KeyEvent.VK_V);
                break;
            case 'w':
                doType(KeyEvent.VK_W);
                break;
            case 'x':
                doType(KeyEvent.VK_X);
                break;
            case 'y':
                doType(KeyEvent.VK_Y);
                break;
            case 'z':
                doType(KeyEvent.VK_Z);
                break;
            case 'A':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A);
                break;
            case 'B':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B);
                break;
            case 'C':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C);
                break;
            case 'D':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D);
                break;
            case 'E':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E);
                break;
            case 'F':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F);
                break;
            case 'G':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G);
                break;
            case 'H':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H);
                break;
            case 'I':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I);
                break;
            case 'J':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J);
                break;
            case 'K':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K);
                break;
            case 'L':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L);
                break;
            case 'M':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M);
                break;
            case 'N':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N);
                break;
            case 'O':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O);
                break;
            case 'P':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P);
                break;
            case 'Q':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q);
                break;
            case 'R':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R);
                break;
            case 'S':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S);
                break;
            case 'T':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T);
                break;
            case 'U':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U);
                break;
            case 'V':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V);
                break;
            case 'W':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W);
                break;
            case 'X':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X);
                break;
            case 'Y':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y);
                break;
            case 'Z':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z);
                break;
            case '`':
                doType(KeyEvent.VK_BACK_QUOTE);
                break;
            case '0':
                doType(KeyEvent.VK_0);
                break;
            case '1':
                doType(KeyEvent.VK_1);
                break;
            case '2':
                doType(KeyEvent.VK_2);
                break;
            case '3':
                doType(KeyEvent.VK_3);
                break;
            case '4':
                doType(KeyEvent.VK_4);
                break;
            case '5':
                doType(KeyEvent.VK_5);
                break;
            case '6':
                doType(KeyEvent.VK_6);
                break;
            case '7':
                doType(KeyEvent.VK_7);
                break;
            case '8':
                doType(KeyEvent.VK_8);
                break;
            case '9':
                doType(KeyEvent.VK_9);
                break;
            case '-':
                doType(KeyEvent.VK_MINUS);
                break;
            case '=':
                doType(KeyEvent.VK_EQUALS);
                break;
            case '~':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE);
                break;
            case '!':
                doType(KeyEvent.VK_EXCLAMATION_MARK);
                break;
            case '@':
                doType(KeyEvent.VK_AT);
                break;
            case '#':
                doType(KeyEvent.VK_NUMBER_SIGN);
                break;
            case '$':
                doType(KeyEvent.VK_DOLLAR);
                break;
            case '%':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5);
                break;
            case '^':
                doType(KeyEvent.VK_CIRCUMFLEX);
                break;
            case '&':
                doType(KeyEvent.VK_AMPERSAND);
                break;
            case '*':
                doType(KeyEvent.VK_ASTERISK);
                break;
            case '(':
                doType(KeyEvent.VK_LEFT_PARENTHESIS);
                break;
            case ')':
                doType(KeyEvent.VK_RIGHT_PARENTHESIS);
                break;
            case '_':
                doType(KeyEvent.VK_UNDERSCORE);
                break;
            case '+':
                doType(KeyEvent.VK_PLUS);
                break;
            case '\t':
                doType(KeyEvent.VK_TAB);
                break;
            case '\n':
                doType(KeyEvent.VK_ENTER);
                break;
            case '[':
                doType(KeyEvent.VK_OPEN_BRACKET);
                break;
            case ']':
                doType(KeyEvent.VK_CLOSE_BRACKET);
                break;
            case '\\':
                doType(KeyEvent.VK_BACK_SLASH);
                break;
            case '{':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET);
                break;
            case '}':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET);
                break;
            case '|':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH);
                break;
            case ';':
                doType(KeyEvent.VK_SEMICOLON);
                break;
            case ':':
                doType(KeyEvent.VK_COLON);
                break;
            case '\'':
                doType(KeyEvent.VK_QUOTE);
                break;
            case '"':
                doType(KeyEvent.VK_QUOTEDBL);
                break;
            case ',':
                doType(KeyEvent.VK_COMMA);
                break;
            case '<':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA);
                break;
            case '.':
                doType(KeyEvent.VK_PERIOD);
                break;
            case '>':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD);
                break;
            case '/':
                doType(KeyEvent.VK_SLASH);
                break;
            case '?':
                doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH);
                break;
            case ' ':
                doType(KeyEvent.VK_SPACE);
                break;
            default:
                doType(KeyEvent.VK_0);
        }
    }
    //</editor-fold>

}
