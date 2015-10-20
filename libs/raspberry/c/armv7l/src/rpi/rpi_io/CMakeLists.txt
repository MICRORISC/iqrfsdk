set(RPI_IO_SRC_FILES
	${CMAKE_SOURCE_DIR}/src/commons/errors/errors.c
	rpi_io.c
)

include_directories(${CMAKE_SOURCE_DIR}/include)

# update to RPI when not running on RPI2
add_definitions(-D RPI2)

add_library(iqrf_rpi_io
		SHARED
		${RPI_IO_SRC_FILES}
)

install(TARGETS iqrf_rpi_io DESTINATION ${CMAKE_INSTALL_PREFIX}/lib)
