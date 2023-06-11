package com.example.alarmserver.services.sip;

import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.VideoPreview;
import org.pjsip.pjsua2.VideoWindow;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_media_status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SipCall extends Call {
    public VideoWindow videoWindow;
    public VideoPreview videoPreview;

    SipCall(SipAccount account, int callId) {
        super(account, callId);
        videoWindow = null;
    }

    @Override
    public void onCallState(OnCallStateParam prm) {
        try {
            CallInfo ci = getInfo();
            if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                SipApp.ep.utilLogWrite(3, "SipCall", this.dump(true, ""));
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        // Should not delete this call instance (self) in this context,
        // so the observer should manage this call instance deletion
        // out of this callback context.
        SipApp.observer.notifyCallState(this);
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam param) {
        CallInfo ci;
        try {
            ci = getInfo();
        } catch (Exception e) {
            return;
        }

        CallMediaInfoVector mediaInfos = ci.getMedia();
        // FIXME: mediaInfos maybe null
        for (int i = 0; i < mediaInfos.size(); i++) {
            CallMediaInfo cmi = mediaInfos.get(i);
            if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                    (cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
                            cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD)) {
                // connect ports
                try {
                    AudioMedia am = getAudioMedia(i);
                    SipApp.ep.audDevManager().getCaptureDevMedia().startTransmit(am);
                    am.startTransmit(SipApp.ep.audDevManager().getPlaybackDevMedia());
                } catch (Exception e) {
                    log.warn("Failed connecting media ports. {}", e.getMessage());
                    continue;
                }
            } else if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO &&
                    cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE &&
                    cmi.getVideoIncomingWindowId() != pjsua2.INVALID_ID) {
                videoWindow = new VideoWindow(cmi.getVideoIncomingWindowId());
                videoPreview = new VideoPreview(cmi.getVideoCapDev());
            }
        }

        SipApp.observer.notifyCallMediaState(this);
    }
}
