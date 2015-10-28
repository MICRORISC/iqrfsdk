set(SYSFS_GPIO_SRC_FILES
	sysfs_gpio.c
)

include_directories(${CMAKE_SOURCE_DIR}/include)

add_library(sysfs_gpio
		SHARED
		${SYSFS_GPIO_SRC_FILES}
)

install(TARGETS sysfs_gpio DESTINATION ${CMAKE_INSTALL_PREFIX}/lib)
