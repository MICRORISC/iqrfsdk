#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux-x86
CND_DLIB_EXT=so
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/io_example_button.o \
	${OBJECTDIR}/io_example_led.o \
	${OBJECTDIR}/io_example_read.o \
	${OBJECTDIR}/io_example_reset.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=-fexceptions
CXXFLAGS=-fexceptions

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-L../cdclib/dist/Debug/GNU-Linux-x86 -lcdclib

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rpi-io-examples

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rpi-io-examples: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.c} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rpi-io-examples ${OBJECTFILES} ${LDLIBSOPTIONS} -pthread

${OBJECTDIR}/io_example_button.o: io_example_button.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -I../cdclib/h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/io_example_button.o io_example_button.c

${OBJECTDIR}/io_example_led.o: io_example_led.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -I../cdclib/h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/io_example_led.o io_example_led.c

${OBJECTDIR}/io_example_read.o: io_example_read.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -I../cdclib/h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/io_example_read.o io_example_read.c

${OBJECTDIR}/io_example_reset.o: io_example_reset.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -I../cdclib/h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/io_example_reset.o io_example_reset.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rpi-io-examples

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
