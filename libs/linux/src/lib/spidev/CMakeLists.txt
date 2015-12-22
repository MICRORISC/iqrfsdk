set(SPIDEV_SRC_FILES
	spidev.c
)

include_directories(${CMAKE_SOURCE_DIR}/include)

add_library(rpi_spidev
                SHARED
		${SPIDEV_SRC_FILES}
)

install(TARGETS rpi_spidev DESTINATION ${CMAKE_INSTALL_PREFIX}/lib)
