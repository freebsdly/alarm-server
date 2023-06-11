package com.example.alarmserver.services.sip;

import java.util.ArrayList;
import java.util.List;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.ContainerNode;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class SipAccountConfig {
    private AccountConfig accountCfg = new AccountConfig();
    private List<BuddyConfig> buddyCfgs = new ArrayList<BuddyConfig>();
    private final String accountKey = "Account";
    private final String buddiesKey = "buddies";

    public void readObject(ContainerNode node) {
        try {
            ContainerNode accountNode = node.readContainer(accountKey);
            accountCfg.readObject(accountNode);
            ContainerNode buddiesNode = accountNode.readArray(buddiesKey);
            buddyCfgs.clear();
            while (buddiesNode.hasUnread()) {
                BuddyConfig buddyCfg = new BuddyConfig();
                buddyCfg.readObject(buddiesNode);
                buddyCfgs.add(buddyCfg);
            }
        } catch (Exception e) {
            log.warn("read account configuration failed. {}", e.getMessage());
        }
    }

    public void writeObject(ContainerNode node) {
        try {
            ContainerNode accountNode = node.writeNewContainer(accountKey);
            accountCfg.writeObject(accountNode);
            ContainerNode buddiesNode = accountNode.writeNewArray(buddiesKey);
            for (BuddyConfig buddyCfg : buddyCfgs) {
                buddyCfg.writeObject(buddiesNode);
            }
        } catch (Exception e) {
            log.warn("write account configuration failed. {}", e.getMessage());
        }
    }
}
