set(project cdc)

set(cdc_SRC_FILES
	src/CDCImpl.cpp
	src/CDCImplException.cpp
	src/CDCMessageParser.cpp
	src/CDCMessageParserException.cpp
	src/CDCReceiveException.cpp
	src/CDCSendException.cpp
)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

add_library(${project}
                SHARED
		${cdc_SRC_FILES}
)
# link to pthread
target_link_libraries(${project} pthread)

install(TARGETS ${project} DESTINATION ${CMAKE_INSTALL_PREFIX}/lib)

#example build
add_subdirectory(example)
add_subdirectory(java)
