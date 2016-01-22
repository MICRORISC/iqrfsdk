cmake_minimum_required(VERSION 2.8)

if(UNIX)
	add_subdirectory(linux)
elseif(WIN32)
	add_subdirectory(windows)
endif(UNIX)

