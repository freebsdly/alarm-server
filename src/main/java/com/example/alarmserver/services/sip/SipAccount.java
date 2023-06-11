package com.example.alarmserver.services.sip;

import java.util.ArrayList;
import java.util.List;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnRegStateParam;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class SipAccount extends Account {
    private List<SipBuddy> buddyList = new ArrayList<SipBuddy>();
    private AccountConfig cfg;

    SipAccount(AccountConfig config) {
        super();
        cfg = config;
    }

    public SipBuddy addBuddy(BuddyConfig buddyCfg) {
        /* Create Buddy */
        SipBuddy bud = new SipBuddy(buddyCfg);
        try {
            bud.create(this, buddyCfg);
        } catch (Exception e) {
            log.warn("create buddy failed. {}", e.getMessage());
            bud.delete();
            bud = null;
        }

        if (bud != null) {
            buddyList.add(bud);
            if (buddyCfg.getSubscribe())
                try {
                    bud.subscribePresence(true);
                } catch (Exception e) {
                    log.warn("subscribe presence buddy failed. {}", e.getMessage());
                }
        }

        return bud;
    }

    public void deleteBuddy(SipBuddy buddy) {
        buddyList.remove(buddy);
        buddy.delete();
    }

    public void deleteBuddy(int index) {
        SipBuddy bud = buddyList.get(index);
        buddyList.remove(index);
        bud.delete();
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        SipApp.observer.notifyRegState(prm.getCode(), prm.getReason(), prm.getExpiration());
    }

    /*
     * 处理好友的来电
     */
    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        log.info("======== Incoming call ======== ");
        SipCall call = new SipCall(this, prm.getCallId());
        SipApp.observer.notifyIncomingCall(call);
    }

    /*
     * 处理好友发送的消息
     */
    @Override
    public void onInstantMessage(OnInstantMessageParam prm) {
        log.info("======== Incoming pager ======== ");
        log.info("From      : {}\nTo        : {}\nContact   : {}\nMimetype  : {}\nBody      : {}",
                prm.getFromUri(), prm.getToUri(), prm.getContactUri(), prm.getContentType(), prm.getMsgBody());
    }
}
