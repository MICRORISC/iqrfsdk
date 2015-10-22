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
	${OBJECTDIR}/_ext/2042185529/CDCImplException.o \
	${OBJECTDIR}/_ext/2042185529/CDCMessageParserException.o \
	${OBJECTDIR}/_ext/2042185529/CDCReceiveException.o \
	${OBJECTDIR}/_ext/2042185529/CDCSendException.o \
	${OBJECTDIR}/_ext/1277114939/CDCImpl.o \
	${OBJECTDIR}/_ext/1277114939/CDCMessageParser.o \
	${OBJECTDIR}/platforms/linux/CDCLib_JavaStub.o


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
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib_javastub.a

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib_javastub.a: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib_javastub.a
	${AR} -rv ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib_javastub.a ${OBJECTFILES} 
	$(RANLIB) ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib_javastub.a

${OBJECTDIR}/_ext/2042185529/CDCImplException.o: ../../c++/cdclib/modules/CDCImplException.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/2042185529
	${RM} "$@.d"
	$(COMPILE.cc) -g -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/2042185529/CDCImplException.o ../../c++/cdclib/modules/CDCImplException.cpp

${OBJECTDIR}/_ext/2042185529/CDCMessageParserException.o: ../../c++/cdclib/modules/CDCMessageParserException.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/2042185529
	${RM} "$@.d"
	$(COMPILE.cc) -g -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/2042185529/CDCMessageParserException.o ../../c++/cdclib/modules/CDCMessageParserException.cpp

${OBJECTDIR}/_ext/2042185529/CDCReceiveException.o: ../../c++/cdclib/modules/CDCReceiveException.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/2042185529
	${RM} "$@.d"
	$(COMPILE.cc) -g -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/2042185529/CDCReceiveException.o ../../c++/cdclib/modules/CDCReceiveException.cpp

${OBJECTDIR}/_ext/2042185529/CDCSendException.o: ../../c++/cdclib/modules/CDCSendException.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/2042185529
	${RM} "$@.d"
	$(COMPILE.cc) -g -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/2042185529/CDCSendException.o ../../c++/cdclib/modules/CDCSendException.cpp

${OBJECTDIR}/_ext/1277114939/CDCImpl.o: ../../c++/cdclib/platforms/linux/CDCImpl.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1277114939
	${RM} "$@.d"
	$(COMPILE.cc) -g -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1277114939/CDCImpl.o ../../c++/cdclib/platforms/linux/CDCImpl.cpp

${OBJECTDIR}/_ext/1277114939/CDCMessageParser.o: ../../c++/cdclib/platforms/linux/CDCMessageParser.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1277114939
	${RM} "$@.d"
	$(COMPILE.cc) -g -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1277114939/CDCMessageParser.o ../../c++/cdclib/platforms/linux/CDCMessageParser.cpp

${OBJECTDIR}/platforms/linux/CDCLib_JavaStub.o: platforms/linux/CDCLib_JavaStub.cpp 
	${MKDIR} -p ${OBJECTDIR}/platforms/linux
	${RM} "$@.d"
	$(COMPILE.cc) -g -Ih -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/platforms/linux/CDCLib_JavaStub.o platforms/linux/CDCLib_JavaStub.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libcdclib_javastub.a

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
