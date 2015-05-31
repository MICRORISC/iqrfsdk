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
CND_CONF=Release
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/modules/CDCImplException.o \
	${OBJECTDIR}/modules/CDCMessageParserException.o \
	${OBJECTDIR}/modules/CDCReceiveException.o \
	${OBJECTDIR}/modules/CDCSendException.o \
	${OBJECTDIR}/platforms/linux/CDCImpl.o \
	${OBJECTDIR}/platforms/linux/CDCMessageParser.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib.a

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib.a: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib.a
	${AR} -rv ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib.a ${OBJECTFILES} 
	$(RANLIB) ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib.a

${OBJECTDIR}/modules/CDCImplException.o: modules/CDCImplException.cpp 
	${MKDIR} -p ${OBJECTDIR}/modules
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/modules/CDCImplException.o modules/CDCImplException.cpp

${OBJECTDIR}/modules/CDCMessageParserException.o: modules/CDCMessageParserException.cpp 
	${MKDIR} -p ${OBJECTDIR}/modules
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/modules/CDCMessageParserException.o modules/CDCMessageParserException.cpp

${OBJECTDIR}/modules/CDCReceiveException.o: modules/CDCReceiveException.cpp 
	${MKDIR} -p ${OBJECTDIR}/modules
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/modules/CDCReceiveException.o modules/CDCReceiveException.cpp

${OBJECTDIR}/modules/CDCSendException.o: modules/CDCSendException.cpp 
	${MKDIR} -p ${OBJECTDIR}/modules
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/modules/CDCSendException.o modules/CDCSendException.cpp

${OBJECTDIR}/platforms/linux/CDCImpl.o: platforms/linux/CDCImpl.cpp 
	${MKDIR} -p ${OBJECTDIR}/platforms/linux
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/platforms/linux/CDCImpl.o platforms/linux/CDCImpl.cpp

${OBJECTDIR}/platforms/linux/CDCMessageParser.o: platforms/linux/CDCMessageParser.cpp 
	${MKDIR} -p ${OBJECTDIR}/platforms/linux
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/platforms/linux/CDCMessageParser.o platforms/linux/CDCMessageParser.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib.a

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
