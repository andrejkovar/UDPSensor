package rassus.lab2;

import java.util.Random;

/**
 *
 * @author Aleksandar
 */
public class EmulatedSystemClock {
    
    private long startTime;
    private double jitter; //jitter per second,  percentage of deviation per 1 second

    public EmulatedSystemClock() {
        startTime = System.currentTimeMillis();
        Random r = new Random();

        //divide by 10 to get the interval between [-20, 20], and then divide by 100 to get percentage
        jitter = (r.nextInt(400) - 200) / 1000d;
    }
    
    public long currentTimeMillis() {
        long current = System.currentTimeMillis();
        long diff =current - startTime;
        double coef = diff / 1000;
        return startTime + Math.round(diff * Math.pow((1+jitter), coef));
    }
}
