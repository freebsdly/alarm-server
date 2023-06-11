/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class OnCallSdpCreatedParam {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected OnCallSdpCreatedParam(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(OnCallSdpCreatedParam obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(OnCallSdpCreatedParam obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_OnCallSdpCreatedParam(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setSdp(SdpSession value) {
    pjsua2JNI.OnCallSdpCreatedParam_sdp_set(swigCPtr, this, SdpSession.getCPtr(value), value);
  }

  public SdpSession getSdp() {
    long cPtr = pjsua2JNI.OnCallSdpCreatedParam_sdp_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SdpSession(cPtr, false);
  }

  public void setRemSdp(SdpSession value) {
    pjsua2JNI.OnCallSdpCreatedParam_remSdp_set(swigCPtr, this, SdpSession.getCPtr(value), value);
  }

  public SdpSession getRemSdp() {
    long cPtr = pjsua2JNI.OnCallSdpCreatedParam_remSdp_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SdpSession(cPtr, false);
  }

  public OnCallSdpCreatedParam() {
    this(pjsua2JNI.new_OnCallSdpCreatedParam(), true);
  }

}