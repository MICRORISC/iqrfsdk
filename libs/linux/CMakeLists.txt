cmake_minimum_required(VERSION 2.8)
project(IQRF_LINUX)

if(MACHINE)
	add_definitions(-D${MACHINE})
endif(MACHINE)

add_subdirectory(src)
add_subdirectory(examples)

