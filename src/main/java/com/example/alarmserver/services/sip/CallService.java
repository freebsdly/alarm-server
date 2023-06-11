package com.example.alarmserver.services.sip;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AccountRegConfig;
import org.pjsip.pjsua2.AccountSipConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.StringVector;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CallService {
    private static SipApp app = new SipApp();
    private static MyObserver observer = new MyObserver();
    private static SipAccount account = null;
    private static AccountConfig accCfg = null;

    public CallService() {
        Runtime.getRuntime().addShutdownHook(new SipShutdownHook(Thread.currentThread()));
        runWorker();
    }

    private static void runWorker() {
        try {
            app.init(observer, ".", true);
        } catch (Exception e) {
            System.out.println(e);
            app.deinit();
            System.exit(-1);
        }

        if (app.accList.size() == 0) {
            accCfg = new AccountConfig();
            accCfg.setIdUri("sip:localhost");
            account = app.addAccount(accCfg);

            accCfg.setIdUri("sip:test@pjsip.org");
            AccountSipConfig sipCfg = accCfg.getSipConfig();
            AuthCredInfoVector ciVec = sipCfg.getAuthCreds();
            ciVec.add(new AuthCredInfo("Digest",
                    "*",
                    "test",
                    0,
                    "passwd"));

            StringVector proxy = sipCfg.getProxies();
            proxy.add("sip:pjsip.org;transport=tcp");

            AccountRegConfig regCfg = accCfg.getRegConfig();
            regCfg.setRegistrarUri("sip:pjsip.org");
            account = app.addAccount(accCfg);
        } else {
            account = app.accList.get(0);
            accCfg = account.getCfg();
        }

        try {
            account.modify(accCfg);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        while (!Thread.currentThread().isInterrupted()) {
            // Handle events
            SipApp.ep.libHandleEvents(10);

            // Check if any call instance need to be deleted
            observer.check_call_deletion();

            try {
                Thread.currentThread();
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                break;
            }
        }
        app.deinit();
    }
}
