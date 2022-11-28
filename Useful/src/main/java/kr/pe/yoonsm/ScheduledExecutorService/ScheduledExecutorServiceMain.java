package kr.pe.yoonsm.ScheduledExecutorService;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yoonsm@daou.co.kr on 2022-11-23
 */
public class ScheduledExecutorServiceMain {

    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        Runnable runnable = () -> {
            System.out.println("++ Repeat task : " + LocalTime.now());
            sleepSec(3);
            System.out.println("-- Repeat task : " + LocalTime.now());
        };
        int initialDelay = 2;
        int delay = 3;

        // schedule the job
        System.out.println("Scheduled task : " + LocalTime.now());
        executor.scheduleWithFixedDelay(
                runnable, initialDelay, delay, TimeUnit.SECONDS);
    }

    private static void sleepSec(int sec) {
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
