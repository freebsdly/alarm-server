package com.example.alarmserver.services.sip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.ContainerNode;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.IpChangeParam;
import org.pjsip.pjsua2.JsonDocument;
import org.pjsip.pjsua2.LogConfig;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.UaConfig;
import org.pjsip.pjsua2.pj_log_decoration;
import org.pjsip.pjsua2.pjmedia_srtp_use;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SipApp extends pjsua2 {
    public static Endpoint ep = new Endpoint();
    public static SipAppObserver observer;
    public List<SipAccount> accList = new ArrayList<SipAccount>();

    private List<SipAccountConfig> accountCfgs = new ArrayList<SipAccountConfig>();
    private EpConfig epConfig = new EpConfig();
    private TransportConfig sipTpConfig = new TransportConfig();
    private String appDir;

    /* Maintain reference to log writer to avoid premature cleanup by GC */
    private SipLogWriter logWriter;

    private final String configName = "pjsua2.json";
    private final int SIP_PORT = 6000;
    private final int LOG_LEVEL = 4;

    public void init(SipAppObserver obs, String app_dir) {
        init(obs, app_dir, false);
    }

    public void init(SipAppObserver obs, String app_dir, boolean own_worker_thread) {
        observer = obs;
        appDir = app_dir;

        /* Create endpoint */
        try {
            ep.libCreate();
        } catch (Exception e) {
            return;
        }

        /* Load config */
        String configPath = appDir + "/" + configName;
        File f = new File(configPath);
        if (f.exists()) {
            loadConfig(configPath);
        } else {
            /* Set 'default' values */
            sipTpConfig.setPort(SIP_PORT);
        }

        /* Override log level setting */
        epConfig.getLogConfig().setLevel(LOG_LEVEL);
        epConfig.getLogConfig().setConsoleLevel(LOG_LEVEL);

        /* Set log config. */
        LogConfig log_cfg = epConfig.getLogConfig();
        logWriter = new SipLogWriter();
        log_cfg.setWriter(logWriter);
        log_cfg.setDecor(log_cfg.getDecor() &
                ~(pj_log_decoration.PJ_LOG_HAS_CR |
                        pj_log_decoration.PJ_LOG_HAS_NEWLINE));

        /* Write log to file (just uncomment whenever needed) */
        // String log_path =
        // android.os.Environment.getExternalStorageDirectory().toString();
        // log_cfg.setFilename(log_path + "/pjsip.log");

        /* Set ua config. */
        UaConfig ua_cfg = epConfig.getUaConfig();
        ua_cfg.setUserAgent("Pjsua2 Android " + ep.libVersion().getFull());

        /* STUN server. */
        // StringVector stun_servers = new StringVector();
        // stun_servers.add("stun.pjsip.org");
        // ua_cfg.setStunServer(stun_servers);

        /* No worker thread */
        if (own_worker_thread) {
            ua_cfg.setThreadCnt(0);
            ua_cfg.setMainThreadOnly(true);
        }

        /* Init endpoint */
        try {
            ep.libInit(epConfig);
        } catch (Exception e) {
            return;
        }

        /* Create transports. */
        try {
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        try {
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, sipTpConfig);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        try {
            sipTpConfig.setPort(SIP_PORT + 1);
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TLS, sipTpConfig);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        /* Set SIP port back to default for JSON saved config */
        sipTpConfig.setPort(SIP_PORT);

        /* Create accounts. */
        for (SipAccountConfig accountCfg : accountCfgs) {
            /* Customize account config */
            accountCfg.getAccountCfg().getNatConfig().setIceEnabled(true);
            accountCfg.getAccountCfg().getVideoConfig().setAutoTransmitOutgoing(true);
            accountCfg.getAccountCfg().getVideoConfig().setAutoShowIncoming(true);

            /* Enable SRTP optional mode and without requiring SIP TLS transport */
            accountCfg.getAccountCfg().getMediaConfig().setSrtpUse(pjmedia_srtp_use.PJMEDIA_SRTP_OPTIONAL);
            accountCfg.getAccountCfg().getMediaConfig().setSrtpSecureSignaling(0);

            SipAccount account = addAccount(accountCfg.getAccountCfg());
            if (account == null)
                continue;

            /* Add Buddies */
            for (BuddyConfig buddyCfg : accountCfg.getBuddyCfgs()) {
                account.addBuddy(buddyCfg);
            }
        }

        /* Start. */
        try {
            ep.libStart();
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }
    }

    public SipAccount addAccount(AccountConfig cfg) {
        SipAccount acc = new SipAccount(cfg);
        try {
            acc.create(cfg);
        } catch (Exception e) {
            log.warn(e.getMessage());
            acc = null;
            return null;
        }

        accList.add(acc);
        return acc;
    }

    public void delAccount(SipAccount acc) {
        accList.remove(acc);
    }

    private void loadConfig(String filename) {
        JsonDocument json = new JsonDocument();

        try {
            /* Load file */
            json.loadFile(filename);
            ContainerNode root = json.getRootContainer();

            /* Read endpoint config */
            epConfig.readObject(root);

            /* Read transport config */
            ContainerNode tp_node = root.readContainer("SipTransport");
            sipTpConfig.readObject(tp_node);

            /* Read account configs */
            accountCfgs.clear();
            ContainerNode accs_node = root.readArray("accounts");
            while (accs_node.hasUnread()) {
                SipAccountConfig acc_cfg = new SipAccountConfig();
                acc_cfg.readObject(accs_node);
                accountCfgs.add(acc_cfg);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        /*
         * Force delete json now, as I found that Java somehow destroys it
         * after lib has been destroyed and from non-registered thread.
         */
        json.delete();
    }

    private void buildAccConfigs() {
        /* Sync accCfgs from accList */
        accountCfgs.clear();
        for (SipAccount acc : accList) {
            SipAccountConfig accountCfg = new SipAccountConfig();
            accountCfg.setAccountCfg(acc.getCfg());

            accountCfg.getBuddyCfgs().clear();
            for (SipBuddy bud : acc.getBuddyList()) {
                accountCfg.getBuddyCfgs().add(bud.cfg);
            }

            accountCfgs.add(accountCfg);
        }
    }

    private void saveConfig(String filename) {
        JsonDocument json = new JsonDocument();

        try {
            /* Write endpoint config */
            json.writeObject(epConfig);

            /* Write transport config */
            ContainerNode tp_node = json.writeNewContainer("SipTransport");
            sipTpConfig.writeObject(tp_node);

            /* Write account configs */
            buildAccConfigs();
            ContainerNode accs_node = json.writeNewArray("accounts");
            for (int i = 0; i < accountCfgs.size(); i++) {
                accountCfgs.get(i).writeObject(accs_node);
            }

            /* Save file */
            json.saveFile(filename);
        } catch (Exception e) {
        }

        /*
         * Force delete json now, as I found that Java somehow destroys it
         * after lib has been destroyed and from non-registered thread.
         */
        json.delete();
    }

    public void handleNetworkChange() {
        try {
            log.warn("Network change detected");
            IpChangeParam changeParam = new IpChangeParam();
            ep.handleIpChange(changeParam);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public void deinit() {
        String configPath = appDir + "/" + configName;
        saveConfig(configPath);

        /*
         * Try force GC to avoid late destroy of PJ objects as they should be
         * deleted before lib is destroyed.
         */
        Runtime.getRuntime().gc();

        /*
         * Shutdown pjsua. Note that Endpoint destructor will also invoke
         * libDestroy(), so this will be a test of double libDestroy().
         */
        try {
            ep.libDestroy();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        /*
         * Force delete Endpoint here, to avoid deletion from a non-
         * registered thread (by GC?).
         */
        ep.delete();
        ep = null;
    }
}
