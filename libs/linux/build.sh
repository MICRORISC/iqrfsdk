#!/bin/sh

MACHINE="$1"
BIN_DIR_NAME="`pwd`/bin/usr"

# if machine is empty defaukt is RPI
if [ "x$1" = "x" ]; then
	MACHINE="RPI"
fi

BUILD_DIR_NAME=".build-$MACHINE"

if [ ! -d "$BUILD_DIR_NAME" ]; then
	mkdir "$BUILD_DIR_NAME"
fi

pushd "$BUILD_DIR_NAME"
if [ "$MACHINE" = "RPI" ]; then
	cmake -DCMAKE_TOOLCHAIN_FILE=../RPI-cross.cmake ../ -DCMAKE_INSTALL_PREFIX:PATH="$BIN_DIR_NAME" -DMACHINE="${MACHINE}" || exit 1
else
# build for x86
	cmake ../ || exit 1
fi

# build
make -j4 || exit 1

# clear output
rm -r "$BIN_DIR_NAME_${MACHINE}"

# install binaries and libs
make install

popd

