#include "com_alderstone_multitouch_mac_touchpad_TouchpadObservable.h"

#include <math.h>
#include <unistd.h>
#include <CoreFoundation/CoreFoundation.h>


#include <stdio.h>      /* standard C i/o facilities */
#include <stdlib.h>     /* needed for atoi() */
#include <unistd.h>  	/* defines STDIN_FILENO, system calls,etc */
#include <sys/types.h>  /* system data type definitions */
#include <sys/socket.h> /* socket specific definitions */
#include <netinet/in.h> /* INET constants and stuff */
#include <arpa/inet.h>  /* IP address conversion stuff */
#include <netdb.h>	/* gethostbyname */




#define MT_CLASS "com/alderstone/multitouch/mac/touchpad/TouchpadObservable"
#define MT_METHOD "mtcallback"
#define MT_SIG "(IDIIFFFFFFFF)V"

typedef struct { float x,y; } mtPoint;
typedef struct { mtPoint pos,vel; } mtReadout;


typedef struct {
	int frame;
	double timestamp;
	int identifier, state, foo3, foo4;
	mtReadout normalized;
	float size;
	int zero1;
	float angle, majorAxis, minorAxis; // ellipsoid
	mtReadout mm;
	int zero2[2];
	float unk2;
} Finger;

typedef long MTDeviceRef;
typedef int (*MTContactCallbackFunction)(int,Finger*,int,double,int);

MTDeviceRef MTDeviceCreateDefault();
void MTRegisterContactFrameCallback(MTDeviceRef, MTContactCallbackFunction);
void MTDeviceStart(MTDeviceRef, int dummy);
void MTDeviceStop(MTDeviceRef);

static int initOk = 0;
static JavaVM *jvm = NULL;
static MTDeviceRef dev;

static int mtshutdown = 0;

jclass cls;
jmethodID mid;

int mtcallback(int device, Finger *data, int nFingers, double timestamp, int frame) {
	JNIEnv *env = NULL;
	//if (!(frame % 50)) printf("Native: MT Frame= %d\n", frame);
	if (mtshutdown) return 0;

	int res = (*jvm)->AttachCurrentThread(jvm, (void**)&env, NULL);

	if (res == 0) {
		if (cls != NULL){

			if (mid) {

				for (int i=0; i<nFingers; i++) {
					Finger *f = &data[i];
					(*env)->CallStaticVoidMethod(env,  cls, mid, f->frame, timestamp, f->identifier,
												 f->state, f->size,
												 f->normalized.pos.x, f->normalized.pos.y,
												 f->normalized.vel.x, f->normalized.vel.y,
												 f->angle, f->majorAxis, f->minorAxis);
				}
			}
		}
		res = (*jvm)->DetachCurrentThread(jvm);
	}

	return 1;
}

void startMT() {
	dev = MTDeviceCreateDefault(0);
	if (dev) {
		MTRegisterContactFrameCallback(dev, mtcallback);
	}
	MTDeviceStart(dev,0	);
}

void stopMT() {
	MTDeviceStop(dev);
}


JNIEXPORT jint JNICALL Java_com_alderstone_multitouch_mac_touchpad_TouchpadObservable_registerListener(JNIEnv *env, jobject obj) {

	if (jvm == NULL)
		(*env)->GetJavaVM(env, &jvm);

	cls = (*env)->FindClass(env, MT_CLASS);

	// make it a global ref so that we can use it in a nother thread.  (TODO: look at weakref for GC)
	cls = (*env)->NewGlobalRef(env, cls);
	if (cls == NULL){
		printf("Java class %s not found!\n", MT_CLASS);
		initOk=0;
	} else 	{
		mid = (*env)->GetStaticMethodID(env, cls, MT_METHOD, MT_SIG);
		if (mid == NULL) {
			printf("Java callback method %s not found!\n", MT_METHOD);
			initOk = 0;
		} else {
			startMT();
			initOk = 1;
		}
	}

	return initOk==1?0:-1;
}

JNIEXPORT jint JNICALL Java_com_alderstone_multitouch_mac_touchpad_TouchpadObservable_deregisterListener(JNIEnv *env, jobject this) {
	mtshutdown=1;
	//TODO: should use a sync guard incase mtcallback is dispatching to Java.
	if (initOk) {
		stopMT();
		//TODO: delte global ref
	}
	return 0;
}

	/*
	 printf("Frame %7d: Angle %6.2f, ellipse %6.3f x%6.3f; "
	 "position (%6.3f,%6.3f) vel (%6.3f,%6.3f) "
	 "ID %d, state %d [%d %d?] size %6.3f, %6.3f?\n",
	 f->frame,
	 f->angle * 90 / atan2(1,0),
	 f->majorAxis,
	 f->minorAxis,
	 f->normalized.pos.x,
	 f->normalized.pos.y,
	 f->normalized.vel.x,
	 f->normalized.vel.y,
	 f->identifier, f->state, f->foo3, f->foo4,
	 f->size, f->unk2);
	 */
