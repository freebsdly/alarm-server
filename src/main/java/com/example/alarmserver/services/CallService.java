package com.example.alarmserver.services;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AccountRegConfig;
import org.pjsip.pjsua2.AccountSipConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.StringVector;
import org.springframework.stereotype.Service;

import com.example.alarmserver.MyAccount;
import com.example.alarmserver.MyApp;
import com.example.alarmserver.MyObserver;
import com.example.alarmserver.MyShutdownHook;

@Service
public class CallService {
    private static MyApp app = new MyApp();
    private static MyObserver observer = new MyObserver();
    private static MyAccount account = null;
    private static AccountConfig accCfg = null;

    // Snippet code to set native window to output video
    /*
     * private void setOutputVidWin() {}
     * VideoWindowHandle vidWH = new VideoWindowHandle();
     * vidWH.getHandle().setWindow(getNativeWindow());
     * try {
     * currentCall.vidWin.setWindow(vidWH);
     * } catch (Exception e) {
     * System.out.println(e);
     * }
     * }
     */

    public CallService() {
        Runtime.getRuntime().addShutdownHook(new MyShutdownHook(Thread.currentThread()));
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
            account = app.addAcc(accCfg);

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
            account = app.addAcc(accCfg);
        } else {
            account = app.accList.get(0);
            accCfg = account.cfg;
        }

        try {
            account.modify(accCfg);
        } catch (Exception e) {
        }

        while (!Thread.currentThread().isInterrupted()) {
            // Handle events
            MyApp.ep.libHandleEvents(10);

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
