package rts.pptviewer;

import java.util.Timer;
import java.util.TimerTask;

public class Remainder {
    Timer timer;
    int seconds = 1;

    public Remainder() {

        timer = new Timer();
        timer.schedule(new RemindTask(), seconds * 1000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            System.out.println("Time's up!");
            timer.cancel(); //Terminate the timer thread
        }
    }
}
