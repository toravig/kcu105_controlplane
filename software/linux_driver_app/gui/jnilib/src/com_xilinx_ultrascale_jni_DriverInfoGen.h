/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_xilinx_ultrascale_jni_DriverInfoGen */

#ifndef _Included_com_xilinx_ultrascale_jni_DriverInfoGen
#define _Included_com_xilinx_ultrascale_jni_DriverInfoGen
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_init
  (JNIEnv *, jclass);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    flush
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_flush
  (JNIEnv *, jobject);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    get_PCIstate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1PCIstate
  (JNIEnv *, jobject);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    get_EngineState
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1EngineState
  (JNIEnv *, jobject);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    get_DMAStats
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1DMAStats
  (JNIEnv *, jobject);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    get_TRNStats
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1TRNStats
  (JNIEnv *, jobject);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    get_SWStats
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1SWStats
  (JNIEnv *, jobject);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    get_PowerStats
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1PowerStats
  (JNIEnv *, jobject);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    startTest
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_startTest
  (JNIEnv *, jobject, jint, jint, jint);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    stopTest
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_stopTest
  (JNIEnv *, jobject, jint, jint, jint);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    setLinkSpeed
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_setLinkSpeed
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    setLinkWidth
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_setLinkWidth
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    LedStats
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_LedStats
  (JNIEnv *, jobject);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    ReadCmd
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_ReadCmd
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    WriteCmd
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_WriteCmd
  (JNIEnv *, jobject, jint, jint, jlong);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    ReaddUMP
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_ReadDump
  (JNIEnv *, jobject, jint, jint, jint);

/*
 * Class:     com_xilinx_ultrascale_jni_DriverInfoGen
 * Method:    BarInfo
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_BarInfo
(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
