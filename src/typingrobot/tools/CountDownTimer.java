package typingrobot.tools;

import java.util.Timer;
import java.util.TimerTask;
import typingrobot.TypingRobot;
import typingrobot.controllers.interfaces.CountDownCallback;

/**
 * Count down before typing starts. 
 * Gives time for user to set focus on window where typing is to be done.
 * 
 * Callback method TimeExpirable.onTimeElapsed is called after each count
 * to inform UI on current timer value. Callback is received by TypingUtility class
 * which invoke startTyping if countdown is 0;
 * 
 * @author Miodrag Spasic
 */
public class CountDownTimer extends Timer {

    private CountDownCallback countDownCallback;
    private volatile int count;

    public CountDownTimer(boolean bln) {
        super(true);
    }

    public CountDownTimer(int count, CountDownCallback timeExpirable) {
        this.countDownCallback = timeExpirable;
        this.count = count;
    }

    public void startCountDown() {
        scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                countDownCallback.onTimeChanged(count);
                count --;
                
                //cancel thread on time elapsed
                if (count == -1) {
                    cancel();
                }
                
                //stop thread on app exit even if stopTyping in not pressed
                // or time did not elapsed before closing the app
                if (TypingRobot.WINDOW_CLOSED) {
                    cancel();
                }
            }
        }, 0, 1000);
    }

    @Override
    public void cancel() {
        super.cancel();
        count = 5;
    }
    
    

}
