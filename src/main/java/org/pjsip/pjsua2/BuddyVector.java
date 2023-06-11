/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class BuddyVector extends java.util.AbstractList<Buddy> implements java.util.RandomAccess {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected BuddyVector(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(BuddyVector obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(BuddyVector obj) {
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
        pjsua2JNI.delete_BuddyVector(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public BuddyVector(Buddy[] initialElements) {
    this();
    reserve(initialElements.length);

    for (Buddy element : initialElements) {
      add(element);
    }
  }

  public BuddyVector(Iterable<Buddy> initialElements) {
    this();
    for (Buddy element : initialElements) {
      add(element);
    }
  }

  public Buddy get(int index) {
    return doGet(index);
  }

  public Buddy set(int index, Buddy e) {
    return doSet(index, e);
  }

  public boolean add(Buddy e) {
    modCount++;
    doAdd(e);
    return true;
  }

  public void add(int index, Buddy e) {
    modCount++;
    doAdd(index, e);
  }

  public Buddy remove(int index) {
    modCount++;
    return doRemove(index);
  }

  protected void removeRange(int fromIndex, int toIndex) {
    modCount++;
    doRemoveRange(fromIndex, toIndex);
  }

  public int size() {
    return doSize();
  }

  public BuddyVector() {
    this(pjsua2JNI.new_BuddyVector__SWIG_0(), true);
  }

  public BuddyVector(BuddyVector other) {
    this(pjsua2JNI.new_BuddyVector__SWIG_1(BuddyVector.getCPtr(other), other), true);
  }

  public long capacity() {
    return pjsua2JNI.BuddyVector_capacity(swigCPtr, this);
  }

  public void reserve(long n) {
    pjsua2JNI.BuddyVector_reserve(swigCPtr, this, n);
  }

  public boolean isEmpty() {
    return pjsua2JNI.BuddyVector_isEmpty(swigCPtr, this);
  }

  public void clear() {
    pjsua2JNI.BuddyVector_clear(swigCPtr, this);
  }

  public BuddyVector(int count, Buddy value) {
    this(pjsua2JNI.new_BuddyVector__SWIG_2(count, Buddy.getCPtr(value), value), true);
  }

  private int doSize() {
    return pjsua2JNI.BuddyVector_doSize(swigCPtr, this);
  }

  private void doAdd(Buddy x) {
    pjsua2JNI.BuddyVector_doAdd__SWIG_0(swigCPtr, this, Buddy.getCPtr(x), x);
  }

  private void doAdd(int index, Buddy x) {
    pjsua2JNI.BuddyVector_doAdd__SWIG_1(swigCPtr, this, index, Buddy.getCPtr(x), x);
  }

  private Buddy doRemove(int index) {
    long cPtr = pjsua2JNI.BuddyVector_doRemove(swigCPtr, this, index);
    return (cPtr == 0) ? null : new Buddy(cPtr, false);
  }

  private Buddy doGet(int index) {
    long cPtr = pjsua2JNI.BuddyVector_doGet(swigCPtr, this, index);
    return (cPtr == 0) ? null : new Buddy(cPtr, false);
  }

  private Buddy doSet(int index, Buddy val) {
    long cPtr = pjsua2JNI.BuddyVector_doSet(swigCPtr, this, index, Buddy.getCPtr(val), val);
    return (cPtr == 0) ? null : new Buddy(cPtr, false);
  }

  private void doRemoveRange(int fromIndex, int toIndex) {
    pjsua2JNI.BuddyVector_doRemoveRange(swigCPtr, this, fromIndex, toIndex);
  }

}
