package com.example.alarmserver.services.sip;

import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SipLogWriter extends LogWriter
{
    @Override
    public void write(LogEntry entry)
    {
        log.info(entry.getMsg());
    }
}
