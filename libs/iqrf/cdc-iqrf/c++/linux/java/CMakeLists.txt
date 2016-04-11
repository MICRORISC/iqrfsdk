cmake_minimum_required(VERSION 2.8)
set(project cdc_java)

find_package(Java REQUIRED)
find_package(JNI REQUIRED)

if (JNI_FOUND)
	message (STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
	message (STATUS "JNI_LIBRARIES=${JNI_LIBRARIES}")
	include_directories(${JNI_INCLUDE_DIRS})
endif()

set(cdc_java_SRC_FILES
	CDCLib_JavaStub.cpp
)

include_directories(${CMAKE_SOURCE_DIR}/include/cdc_java)
include_directories(${CMAKE_SOURCE_DIR}/include)

add_library(${project} SHARED ${cdc_java_SRC_FILES})

install(TARGETS ${project} DESTINATION ${CMAKE_INSTALL_PREFIX}/lib)
