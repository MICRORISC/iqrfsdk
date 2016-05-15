set(DPASRC_FILES
	dpa.cpp
)

# build serial library

set(serial_SRCS
    ${CMAKE_SOURCE_DIR}/../external/serial/src/serial.cc
    ${CMAKE_SOURCE_DIR}/../external/serial/include/serial/serial.h
    ${CMAKE_SOURCE_DIR}/../external/serial/include/serial/v8stdint.h
    ${CMAKE_SOURCE_DIR}/../external/serial/src/impl/unix.cc
)

## Add serial library
add_library(serial SHARED ${serial_SRCS})
target_link_libraries(serial rt)

## Build your executable

include_directories(${CMAKE_SOURCE_DIR}/include)
include_directories(${CMAKE_SOURCE_DIR}/../iqrf/cdc-iqrf/c++/linux/include)
include_directories(${CMAKE_SOURCE_DIR}/../external/serial/include)

add_library(iqrfdpa
                SHARED
		${DPASRC_FILES}
)

target_link_libraries(iqrfdpa
                        serial
)

install(TARGETS iqrfdpa DESTINATION ${CMAKE_INSTALL_PREFIX}/lib)
