package com.example.alarmserver;

public class MyShutdownHook extends Thread {
    Thread thread;

    public MyShutdownHook(Thread thr) {
        thread = thr;
    }

    public void run() {
        thread.interrupt();
        try {
            thread.join();
        } catch (Exception e) {
            ;
        }
    }
}
