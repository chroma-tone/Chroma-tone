/*
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

#include <stdint.h>
#include <math.h>
#include <fftw3.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jchar* Java_com_audiologic_Fourierwin_Newfourier_fprocess(JNIEnv * env,jobject thiz, jdoubleArray array)
{
    // Variable Declaration

	double* bufferPtr = (env)->GetDoubleArrayElements( array, NULL);
	int size = (env)->GetArrayLength(array);



    double *out,*mag,*phase;
    double real,imag;
    int i,j;

    fftw_complex *out_cpx, *mod_cpx;
    fftw_plan fft;
    fftw_plan ifft;
    char* result;

    //Allocate Memory
    out_cpx = (fftw_complex*) fftw_malloc(sizeof(fftw_complex)*(size/2+1));
    mod_cpx = (fftw_complex*) fftw_malloc(sizeof(fftw_complex)*(size/2+1));
    out = (double *) malloc(size*sizeof(double));
    mag = (double *) malloc(size*sizeof(double));
    phase = (double *) malloc(size*sizeof(double));
    result = (char *) malloc(size*sizeof(char));

    fft = fftw_plan_dft_r2c_1d(size, bufferPtr, out_cpx, FFTW_ESTIMATE);  //Setup fftw plan for fft 1 dimensional, real signal
    ifft = fftw_plan_dft_c2r_1d(size, mod_cpx, out, FFTW_ESTIMATE);   //Setup fftw plan for ifft 1 dimensional, complex signal

    fftw_execute(fft);	//perform fft
	for(j=0;j<size/2+1;j++)
	{
        	real = out_cpx[j][0];	//Extract real component
		imag = out_cpx[j][1];   //Extract imaginary component
		mag[j] = sqrt((real*real)+(imag*imag));  // Calculate the Magnitude
		phase[j] = atan2(imag,real); // Calculate the Phase
	}

	**********MODIFICATION***************************
	//You can perform frequency domain modification here
	*************************************************

	for(j=0;j<size/2+1;j++)
	{
		mod_cpx[j][0] = (mag[j]*cos(phase[j]));  //Construct new real component
		mod_cpx[j][1] = (mag[j]*sin(phase[j]));  //Construct new imaginary  component
	}

    fftw_execute(ifft); //perform ifft

    // Print input and output


    for(i=0;i<size;i++)
    {
	out[i] = out[i]/size;
	result [i] =out[i];
    }

    // Free all memory
    fftw_destroy_plan(fft);
    fftw_destroy_plan(ifft);
    fftw_free(out_cpx);
    fftw_free(mod_cpx);
    free(out);
    free(mag);
    free(phase);
    (env)->ReleaseDoubleArrayElements( array, bufferPtr, 0);
    jchar *jcharBuffer1 = (jchar *)calloc(sizeof(jchar), size);
    jcharArray resultBuffer1 = (env)->NewCharArray(size);
    for (int i = 0; i < size; i ++) {
    	jcharBuffer1[i] = (jchar)result[i];
    }
    env->SetCharArrayRegion(resultBuffer1, 0, size, jcharBuffer1);
    free(jcharBuffer1);
    return (env)->GetCharArrayElements(resultBuffer1,NULL);



}
#ifdef __cplusplus
}
#endif
*/

/** Copyright (C) 2009 by Aleksey Surkov.
 **
 ** Permission to use, copy, modify, and distribute this software and its
 ** documentation for any purpose and without fee is hereby granted, provided
 ** that the above copyright notice appear in all copies and that both that
 ** copyright notice and this permission notice appear in supporting
 ** documentation.  This software is provided "as is" without express or
 ** implied warranty.
 */

#include <math.h>
#include <android/log.h>

#include "Fourierwin.h"

#define LOGV(v) \
  __android_log_write(ANDROID_LOG_VERBOSE, "fft-jni" , (v))

#define LOGE(v) \
  __android_log_write(ANDROID_LOG_ERROR, "fft-jni", (v))

template<class T> inline void swap(T &x, T&y) {
	T z;
	z = x; x = y; y = z;
}

// Taken from http://www.ddj.com/cpp/199500857
// which took it from Numerical Recipes in C++, p.513
void DoFFTInternal(jdouble* data, jint nn) {
	unsigned long n, mmax, m, j, istep, i;
	jdouble wtemp, wr, wpr, wpi, wi, theta;
	jdouble tempr, tempi;

	// reverse-binary reindexing
	n = nn<<1;
	j=1;
	for (i=1; i<n; i+=2) {
		if (j>i) {
			swap(data[j], data[i]);
			swap(data[j+1], data[i+1]);
		}
		m = nn;
		while (m>=2 && j>m) {
			j -= m;
			m >>= 1;
		}
		j += m;
	};


	// here begins the Danielson-Lanczos section
	mmax=2;
	while (n>mmax) {
		istep = mmax<<1;
		theta = -(2*M_PI/mmax);
		wtemp = sin(0.5*theta);
		wpr = -2.0*wtemp*wtemp;
		wpi = sin(theta);
		wr = 1.0;
		wi = 0.0;
		for (m=1; m < mmax; m += 2) {
			for (i=m; i <= n; i += istep) {
				j=i+mmax;
				tempr = wr*data[j] - wi*data[j+1];
				tempi = wr * data[j+1] + wi*data[j];


				data[j] = data[i] - tempr;
				data[j+1] = data[i+1] - tempi;
				data[i] += tempr;
				data[i+1] += tempi;
			}
			wtemp=wr;
			wr += wr*wpr - wi*wpi;
			wi += wi*wpr + wtemp*wpi;
		}
		mmax=istep;
	}
}

int jniRegisterNativeMethods(JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    LOGV("Registering natives:");
    LOGV(className);
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class:");
        LOGE(className);
        return -1;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        LOGE("RegisterNatives failed for:");
        LOGE(className);
        return -1;
    }
    LOGV("Successfully registered natives.");
    return 0;
}

static JNINativeMethod gMethods[] = {
    {"fprocess", "([DI)V", (void *)Java_com_audiologic_Fourierwin_PitchDetect_fprocess},
};

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    jniRegisterNativeMethods(env, "com/audiologic/Fourierwin/PitchDetect", gMethods, 1);
    return JNI_VERSION_1_4;
}

void Java_com_audiologic_Fourierwin_PitchDetect_fprocess(
            JNIEnv* env,
	        jobject thiz,
            jdoubleArray data,
            jint size) {
  jdouble *source_data = env->GetDoubleArrayElements(data, JNI_FALSE);
  DoFFTInternal(source_data, size);
}

