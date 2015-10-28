set(PROJECT_NAME io_example_button)

set(IO_EXAMPLE_BUTTON_SRC_FILES
	io_example_button.c
)

include_directories(${CMAKE_SOURCE_DIR}/src/lib/sysfs_gpio
		${CMAKE_SOURCE_DIR}/include)

add_executable(${PROJECT_NAME} ${IO_EXAMPLE_BUTTON_SRC_FILES})
target_link_libraries(${PROJECT_NAME} sysfs_gpio)

install(TARGETS ${PROJECT_NAME} DESTINATION ${CMAKE_INSTALL_PREFIX}/sbin)
