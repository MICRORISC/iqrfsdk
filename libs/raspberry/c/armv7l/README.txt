To crosscompile examples update path for toolchain in RPI-cross.cmake and
issue ./build.sh command. Binaries + libs are produces in .build directory
and automatically installed in bin directory. You can then user rsync (or scp)
to copy binaries to target.
