#!/bin/sh

BUILD_DIR_NAME=".build"
BIN_DIR_NAME="`pwd`/bin/usr"


if [ ! -d "$BUILD_DIR_NAME" ]; then
	mkdir "$BUILD_DIR_NAME"
fi

pushd "$BUILD_DIR_NAME"
cmake -DCMAKE_TOOLCHAIN_FILE=../RPI-cross.cmake ../ -DCMAKE_INSTALL_PREFIX:PATH="$BIN_DIR_NAME" || exit 1

# build
make -j4 || exit 1

# clear output
rm -r "$BIN_DIR_NAME"

# install binaries and libs
make install

popd

