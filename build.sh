#!/bin/sh
# FourierTest/build.sh
# Compiles fftw3 for Android
# Make sure you have NDK_ROOT defined in .bashrc or .bash_profile
export NDK_ROOT="/Users/lukestanford/android-ndk"
INSTALL_DIR="`pwd`/jni/fftw3"
SRC_DIR="`pwd`/../fftw-3.3.2"

cd $SRC_DIR

export PATH="$NDK_ROOT/toolchains/arm-linux-androideabi-4.4.3/prebuilt/darwin-x86/bin/:$PATH"
export SYS_ROOT="$NDK_ROOT/platforms/android-14/arch-arm/"
export CC="arm-linux-androideabi-gcc --sysroot=$SYS_ROOT"
export LD="arm-linux-androideabi-ld"
export AR="arm-linux-androideabi-ar"
export RANLIB="arm-linux-androideabi-ranlib"
export STRIP="arm-linux-androideabi-strip"

mkdir -p $INSTALL_DIR
./configure --host=arm-eabi --build=i386-apple-darwin12.2.0 --prefix=$INSTALL_DIR LIBS="-lc -lgcc"

make
make install

exit 0