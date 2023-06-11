/* 
 * Copyright (C) 2013 Teluu Inc. (http://www.teluu.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

package com.example.alarmserver.services.sip;

import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

public class MyObserver implements SipAppObserver {
        private static SipCall currentCall = null;
        private boolean del_call_scheduled = false;

        public void check_call_deletion() {
                if (del_call_scheduled && currentCall != null) {
                        currentCall.delete();
                        currentCall = null;
                        del_call_scheduled = false;
                }
        }

        @Override
        public void notifyRegState(int code, String reason, long expiration) {
        }

        @Override
        public void notifyIncomingCall(SipCall call) {
                /* Auto answer. */
                CallOpParam call_param = new CallOpParam();
                call_param.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
                try {
                        currentCall = call;
                        currentCall.answer(call_param);
                } catch (Exception e) {
                        System.out.println(e);
                        return;
                }
        }

        @Override
        public void notifyCallMediaState(SipCall call) {
        }

        public void notifyCallState(SipCall call) {
                if (currentCall == null || call.getId() != currentCall.getId())
                        return;
                CallInfo ci;
                try {
                        ci = call.getInfo();
                } catch (Exception e) {
                        ci = null;
                }
                if (ci != null && ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                        // Should not delete call instance in this context,
                        // so let's just schedule it, the call will be deleted
                        // in our main worker thread context.
                        del_call_scheduled = true;
                }
        }

        @Override
        public void notifyBuddyState(SipBuddy buddy) {
        }

        @Override
        public void notifyChangeNetwork() {
        }
}