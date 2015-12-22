set(PROJECT_NAME spi_iqrf_write_read)

set(SPI_EXAMPLE_WRITE_READ_SRC_FILES
	spiiqrf_example_writeread.c
)

include_directories(${CMAKE_SOURCE_DIR}/include)

add_executable(${PROJECT_NAME} ${SPI_EXAMPLE_WRITE_READ_SRC_FILES})
target_link_libraries(${PROJECT_NAME}
		sysfs_gpio
                rpi_spidev)

install(TARGETS ${PROJECT_NAME} DESTINATION ${CMAKE_INSTALL_PREFIX}/sbin)
