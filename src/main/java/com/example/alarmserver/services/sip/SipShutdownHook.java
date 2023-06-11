package com.example.alarmserver.services.sip;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SipShutdownHook extends Thread {
    Thread thread;

    public SipShutdownHook(Thread thr) {
        thread = thr;
    }

    public void run() {
        thread.interrupt();
        try {
            thread.join();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
