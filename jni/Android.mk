TOP_LOCAL_PATH := $(call my-dir)

include $(call all-subdir-makefiles)
LOCAL_PATH := $(TOP_LOCAL_PATH)

 LOCAL_CFLAGS := -g 

include $(CLEAR_VARS)
LOCAL_STATIC_LIBRARIES := fftw3

LOCAL_MODULE    := Fourierwin
LOCAL_SRC_FILES := Fourierwin.cpp
LOCAL_LDLIBS := -Lbuild/platforms/android-1.5/arch-arm/usr/lib -llog 

include $(BUILD_SHARED_LIBRARY)

LOCAL_PATH := $(call my-dir)
