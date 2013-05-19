TOP_LOCAL_PATH := $(call my-dir)

include $(call all-subdir-makefiles)
LOCAL_PATH := $(TOP_LOCAL_PATH)

  

include $(CLEAR_VARS)
LOCAL_STATIC_LIBRARIES := fftw3

LOCAL_MODULE    := Fourierwin
LOCAL_SRC_FILES := Fourierwin.cpp

include $(BUILD_SHARED_LIBRARY)