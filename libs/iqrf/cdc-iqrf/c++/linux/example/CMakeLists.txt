set(project cdc_example)

set(cdc_example_SRC_FILES
	writeread.cpp
)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

add_executable(${project} ${cdc_example_SRC_FILES})
target_link_libraries(${project} cdc)

install(TARGETS ${project} DESTINATION ${CMAKE_INSTALL_PREFIX}/sbin)
