#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Include project Makefile
ifeq "${IGNORE_LOCAL}" "TRUE"
# do not include local makefile. User is passing all local related variables already
else
include Makefile
# Include makefile containing local settings
ifeq "$(wildcard nbproject/Makefile-local-default.mk)" "nbproject/Makefile-local-default.mk"
include nbproject/Makefile-local-default.mk
endif
endif

# Environment
MKDIR=gnumkdir -p
RM=rm -f 
MV=mv 
CP=cp 

# Macros
CND_CONF=default
ifeq ($(TYPE_IMAGE), DEBUG_RUN)
IMAGE_TYPE=debug
OUTPUT_SUFFIX=cof
DEBUGGABLE_SUFFIX=cof
FINAL_IMAGE=dist/${CND_CONF}/${IMAGE_TYPE}/pic18_v100.X.${IMAGE_TYPE}.${OUTPUT_SUFFIX}
else
IMAGE_TYPE=production
OUTPUT_SUFFIX=hex
DEBUGGABLE_SUFFIX=cof
FINAL_IMAGE=dist/${CND_CONF}/${IMAGE_TYPE}/pic18_v100.X.${IMAGE_TYPE}.${OUTPUT_SUFFIX}
endif

# Object Directory
OBJECTDIR=build/${CND_CONF}/${IMAGE_TYPE}

# Distribution Directory
DISTDIR=dist/${CND_CONF}/${IMAGE_TYPE}

# Source Files Quoted if spaced
SOURCEFILES_QUOTED_IF_SPACED=../../Microchip/USB/usb_device.c ../../Microchip/USB/usb_function_generic.c ../../Microchip/USB/usb_function_cdc.c ../user.c ../usb_descriptors.c ../main.c ../autonetwork.c ../dpa_library.c ../uart.c

# Object Files Quoted if spaced
OBJECTFILES_QUOTED_IF_SPACED=${OBJECTDIR}/_ext/713068069/usb_device.o ${OBJECTDIR}/_ext/713068069/usb_function_generic.o ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o ${OBJECTDIR}/_ext/1472/user.o ${OBJECTDIR}/_ext/1472/usb_descriptors.o ${OBJECTDIR}/_ext/1472/main.o ${OBJECTDIR}/_ext/1472/autonetwork.o ${OBJECTDIR}/_ext/1472/dpa_library.o ${OBJECTDIR}/_ext/1472/uart.o
POSSIBLE_DEPFILES=${OBJECTDIR}/_ext/713068069/usb_device.o.d ${OBJECTDIR}/_ext/713068069/usb_function_generic.o.d ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o.d ${OBJECTDIR}/_ext/1472/user.o.d ${OBJECTDIR}/_ext/1472/usb_descriptors.o.d ${OBJECTDIR}/_ext/1472/main.o.d ${OBJECTDIR}/_ext/1472/autonetwork.o.d ${OBJECTDIR}/_ext/1472/dpa_library.o.d ${OBJECTDIR}/_ext/1472/uart.o.d

# Object Files
OBJECTFILES=${OBJECTDIR}/_ext/713068069/usb_device.o ${OBJECTDIR}/_ext/713068069/usb_function_generic.o ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o ${OBJECTDIR}/_ext/1472/user.o ${OBJECTDIR}/_ext/1472/usb_descriptors.o ${OBJECTDIR}/_ext/1472/main.o ${OBJECTDIR}/_ext/1472/autonetwork.o ${OBJECTDIR}/_ext/1472/dpa_library.o ${OBJECTDIR}/_ext/1472/uart.o

# Source Files
SOURCEFILES=../../Microchip/USB/usb_device.c ../../Microchip/USB/usb_function_generic.c ../../Microchip/USB/usb_function_cdc.c ../user.c ../usb_descriptors.c ../main.c ../autonetwork.c ../dpa_library.c ../uart.c


CFLAGS=
ASFLAGS=
LDLIBSOPTIONS=

############# Tool locations ##########################################
# If you copy a project from one host to another, the path where the  #
# compiler is installed may be different.                             #
# If you open this project with MPLAB X in the new host, this         #
# makefile will be regenerated and the paths will be corrected.       #
#######################################################################
# fixDeps replaces a bunch of sed/cat/printf statements that slow down the build
FIXDEPS=fixDeps

.build-conf:  ${BUILD_SUBPROJECTS}
ifneq ($(INFORMATION_MESSAGE), )
	@echo $(INFORMATION_MESSAGE)
endif
	${MAKE}  -f nbproject/Makefile-default.mk dist/${CND_CONF}/${IMAGE_TYPE}/pic18_v100.X.${IMAGE_TYPE}.${OUTPUT_SUFFIX}

MP_PROCESSOR_OPTION=18F26J50
MP_PROCESSOR_OPTION_LD=18f26j50
MP_LINKER_DEBUG_OPTION=  -u_DEBUGSTACK
# ------------------------------------------------------------------------------------
# Rules for buildStep: assemble
ifeq ($(TYPE_IMAGE), DEBUG_RUN)
else
endif

# ------------------------------------------------------------------------------------
# Rules for buildStep: compile
ifeq ($(TYPE_IMAGE), DEBUG_RUN)
${OBJECTDIR}/_ext/713068069/usb_device.o: ../../Microchip/USB/usb_device.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/713068069" 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_device.o.d 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_device.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -D__DEBUG -D__MPLAB_DEBUGGER_REAL_ICE=1 -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/713068069/usb_device.o   ../../Microchip/USB/usb_device.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/713068069/usb_device.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/713068069/usb_device.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/713068069/usb_function_generic.o: ../../Microchip/USB/usb_function_generic.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/713068069" 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_function_generic.o.d 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_function_generic.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -D__DEBUG -D__MPLAB_DEBUGGER_REAL_ICE=1 -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/713068069/usb_function_generic.o   ../../Microchip/USB/usb_function_generic.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/713068069/usb_function_generic.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/713068069/usb_function_generic.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/713068069/usb_function_cdc.o: ../../Microchip/USB/usb_function_cdc.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/713068069" 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o.d 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -D__DEBUG -D__MPLAB_DEBUGGER_REAL_ICE=1 -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o   ../../Microchip/USB/usb_function_cdc.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/713068069/usb_function_cdc.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/user.o: ../user.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/user.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/user.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -D__DEBUG -D__MPLAB_DEBUGGER_REAL_ICE=1 -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/user.o   ../user.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/user.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/user.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/usb_descriptors.o: ../usb_descriptors.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/usb_descriptors.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/usb_descriptors.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -D__DEBUG -D__MPLAB_DEBUGGER_REAL_ICE=1 -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/usb_descriptors.o   ../usb_descriptors.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/usb_descriptors.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/usb_descriptors.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/main.o: ../main.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/main.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/main.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -D__DEBUG -D__MPLAB_DEBUGGER_REAL_ICE=1 -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/main.o   ../main.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/main.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/main.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/autonetwork.o: ../autonetwork.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/autonetwork.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/autonetwork.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -D__DEBUG -D__MPLAB_DEBUGGER_REAL_ICE=1 -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/autonetwork.o   ../autonetwork.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/autonetwork.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/autonetwork.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/dpa_library.o: ../dpa_library.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/dpa_library.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/dpa_library.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -D__DEBUG -D__MPLAB_DEBUGGER_REAL_ICE=1 -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/dpa_library.o   ../dpa_library.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/dpa_library.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/dpa_library.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/uart.o: ../uart.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/uart.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/uart.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -D__DEBUG -D__MPLAB_DEBUGGER_REAL_ICE=1 -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/uart.o   ../uart.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/uart.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/uart.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
else
${OBJECTDIR}/_ext/713068069/usb_device.o: ../../Microchip/USB/usb_device.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/713068069" 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_device.o.d 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_device.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/713068069/usb_device.o   ../../Microchip/USB/usb_device.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/713068069/usb_device.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/713068069/usb_device.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/713068069/usb_function_generic.o: ../../Microchip/USB/usb_function_generic.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/713068069" 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_function_generic.o.d 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_function_generic.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/713068069/usb_function_generic.o   ../../Microchip/USB/usb_function_generic.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/713068069/usb_function_generic.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/713068069/usb_function_generic.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/713068069/usb_function_cdc.o: ../../Microchip/USB/usb_function_cdc.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/713068069" 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o.d 
	@${RM} ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o   ../../Microchip/USB/usb_function_cdc.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/713068069/usb_function_cdc.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/713068069/usb_function_cdc.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/user.o: ../user.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/user.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/user.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/user.o   ../user.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/user.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/user.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/usb_descriptors.o: ../usb_descriptors.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/usb_descriptors.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/usb_descriptors.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/usb_descriptors.o   ../usb_descriptors.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/usb_descriptors.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/usb_descriptors.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/main.o: ../main.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/main.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/main.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/main.o   ../main.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/main.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/main.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/autonetwork.o: ../autonetwork.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/autonetwork.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/autonetwork.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/autonetwork.o   ../autonetwork.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/autonetwork.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/autonetwork.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/dpa_library.o: ../dpa_library.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/dpa_library.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/dpa_library.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/dpa_library.o   ../dpa_library.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/dpa_library.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/dpa_library.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
${OBJECTDIR}/_ext/1472/uart.o: ../uart.c  nbproject/Makefile-${CND_CONF}.mk
	@${MKDIR} "${OBJECTDIR}/_ext/1472" 
	@${RM} ${OBJECTDIR}/_ext/1472/uart.o.d 
	@${RM} ${OBJECTDIR}/_ext/1472/uart.o 
	${MP_CC} $(MP_EXTRA_CC_PRE) -p$(MP_PROCESSOR_OPTION) -I"C:/Program Files (x86)/Microchip/mplabc18/v3.38/h" -I"../../Microchip/Include" -I"../../../pic_apl" -I"../../Include" -I".." -I"../../Microchip/Include/USB" -ml -oa- -Ls  -I ${MP_CC_DIR}\\..\\h  -fo ${OBJECTDIR}/_ext/1472/uart.o   ../uart.c 
	@${DEP_GEN} -d ${OBJECTDIR}/_ext/1472/uart.o 
	@${FIXDEPS} "${OBJECTDIR}/_ext/1472/uart.o.d" $(SILENT) -rsi ${MP_CC_DIR}../ -c18 
	
endif

# ------------------------------------------------------------------------------------
# Rules for buildStep: link
ifeq ($(TYPE_IMAGE), DEBUG_RUN)
dist/${CND_CONF}/${IMAGE_TYPE}/pic18_v100.X.${IMAGE_TYPE}.${OUTPUT_SUFFIX}: ${OBJECTFILES}  nbproject/Makefile-${CND_CONF}.mk    ../18f26j50_g_GWBL.lkr
	@${MKDIR} dist/${CND_CONF}/${IMAGE_TYPE} 
	${MP_LD} $(MP_EXTRA_LD_PRE) "..\18f26j50_g_GWBL.lkr"  -p$(MP_PROCESSOR_OPTION_LD)  -w -x -u_DEBUG -m"$(BINDIR_)$(TARGETBASE).map" -w -l"C:/Program Files (x86)/Microchip/mplabc18/v3.38/bin" -l".."  -z__MPLAB_BUILD=1  -u_CRUNTIME -z__MPLAB_DEBUG=1 -z__MPLAB_DEBUGGER_REAL_ICE=1 $(MP_LINKER_DEBUG_OPTION) -l ${MP_CC_DIR}\\..\\lib  -o dist/${CND_CONF}/${IMAGE_TYPE}/pic18_v100.X.${IMAGE_TYPE}.${OUTPUT_SUFFIX}  ${OBJECTFILES_QUOTED_IF_SPACED}   
else
dist/${CND_CONF}/${IMAGE_TYPE}/pic18_v100.X.${IMAGE_TYPE}.${OUTPUT_SUFFIX}: ${OBJECTFILES}  nbproject/Makefile-${CND_CONF}.mk   ../18f26j50_g_GWBL.lkr
	@${MKDIR} dist/${CND_CONF}/${IMAGE_TYPE} 
	${MP_LD} $(MP_EXTRA_LD_PRE) "..\18f26j50_g_GWBL.lkr"  -p$(MP_PROCESSOR_OPTION_LD)  -w  -m"$(BINDIR_)$(TARGETBASE).map" -w -l"C:/Program Files (x86)/Microchip/mplabc18/v3.38/bin" -l".."  -z__MPLAB_BUILD=1  -u_CRUNTIME -l ${MP_CC_DIR}\\..\\lib  -o dist/${CND_CONF}/${IMAGE_TYPE}/pic18_v100.X.${IMAGE_TYPE}.${DEBUGGABLE_SUFFIX}  ${OBJECTFILES_QUOTED_IF_SPACED}   
endif


# Subprojects
.build-subprojects:


# Subprojects
.clean-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r build/default
	${RM} -r dist/default

# Enable dependency checking
.dep.inc: .depcheck-impl

DEPFILES=$(shell mplabwildcard ${POSSIBLE_DEPFILES})
ifneq (${DEPFILES},)
include ${DEPFILES}
endif
