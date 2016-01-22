/* 
 * Copyright 2015 MICRORISC s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <string>
#include <set>
#include <map>
#include <windows.h>
#include <sstream>
#include <cdc/CDCMessageParser.h>
#include <cdc/CDCMessageParserException.h>

/*
 * Implementation class.
 */
class CDCMessageParserPrivate {
public:
	CDCMessageParserPrivate();
	~CDCMessageParserPrivate();

	/* Information about state. */
	struct StateInfo {
		MessageType msgType;    // associated message type
		bool multiType;			// if more message types are possible

	};

	/* Associates state and input. */
	struct StateInputPair {
		unsigned int stateId;
		unsigned int input;
	};

	/* Comparison object of 2 States. */
	struct StateInputPairCompare {
		bool operator() (const StateInputPair& lhs, const StateInputPair& rhs) const
		{
			if (lhs.stateId != rhs.stateId) {
				return lhs.stateId < rhs.stateId;
			}
			return lhs.input < rhs.input;
		}
	};

	/* Results of processing of special state. */
	struct StateProcResult {
		unsigned int newState;		// new state
		unsigned int lastPosition;  // last processed position
		bool formatError;           // indication of format error
	};

	typedef std::set<unsigned int> setOfStates;

	typedef std::map<unsigned int, StateInfo> mapOfStates;

	typedef std::map<StateInputPair, unsigned int, StateInputPairCompare> stateInputToStateMap;


    // map of information about each state
    mapOfStates statesInfoMap;

	// set of finite states
	setOfStates finiteStates;

	// states, which require special processing
	setOfStates specialStates;

	// map of all transitions between states
	stateInputToStateMap transitionMap;

    // last parsed data
	ustring lastParsedData;

	// last parse result information
	ParseResult lastParseResult;

    /* Set of all values of SPIModes. */
	std::set<SPIModes> spiModes;


	/* INITIALIZATION.*/
	void insertStatesInfo(unsigned int states[], unsigned int statesSize,
		MessageType msgType);

	/* Inserting states, which have multiple message types associated with. */
    void insertMultiTypeStatesInfo(unsigned int states[], unsigned int statesSize);

    void initStatesInfoMap(void);

	void insertTransition(unsigned int stateId, unsigned int input,
		unsigned int nextStateId);

	/* Inits transition map. */
	void initTransitionMap(void);

	/* Inits finite states. */
	void initFiniteStates(void);

	/* Inits special states. */
    void initSpecialStates(void);

	/* Initializes spiModes set. */
	void initSpiModes(void);


	/* SPECIAL STATES PROCESSING. */
	/* Processes state 17. */
	StateProcResult processUSBInfo(ustring& data, unsigned int pos);

    /* Processes state 21. */
	StateProcResult processTRInfo(ustring& data, unsigned int pos);

    /* Processes state 50. */
	StateProcResult processAsynData(ustring& data, unsigned int pos);

	 /* Switch function of processing some special state. */
	StateProcResult processSpecialState(unsigned int state, ustring& data,
		unsigned int pos);



	/* Indicates, wheather specified state is final state. */
	bool isFiniteState(unsigned int state);

    /* Indicates, wheather specified state is special state. */
	bool isSpecialState(unsigned int state);

	/* Returns state after specified transition. */
	unsigned int doTransition(unsigned int state, unsigned char input);

	/* Parses specified data. */
	ParseResult parseData(ustring& data);
};


/* Initial state. */
const unsigned int INITIAL_STATE = 0;

/* Indicates, that there is no transition possible. */
const unsigned int NO_TRANSITION = 65535;

/* All inputs - something like '*' in reg. expressions. */
const unsigned int INPUT_ALL = 1000;


// critical section for thread safe access public interface methods
CRITICAL_SECTION csUI;

/*
 * For converting string literals to unsigned string literals.
 */
inline const unsigned char* uchar_str(const char* s){
  return reinterpret_cast<const unsigned char*>(s);
}

/*
 * Indicates, wheather specified state is final state.
 */
bool CDCMessageParserPrivate::isFiniteState(unsigned int state) {
	setOfStates::iterator statesIt = finiteStates.find(state);
	if (statesIt == finiteStates.end()) {
		return false;
	}

	return true;
}

/*
 * Indicates, wheather specified state is special state.
 */
bool CDCMessageParserPrivate::isSpecialState(unsigned int state) {
	setOfStates::iterator statesIt = specialStates.find(state);
	if (statesIt == specialStates.end()) {
		return false;
	}

	return true;
}

/*
 * Returns state after specified transition.
 * If no transition exists, return NO_TRANSITION.
 */
unsigned int CDCMessageParserPrivate::doTransition(unsigned int state,
		unsigned char input) {
	StateInputPair stateInput = { state, input };

	stateInputToStateMap::iterator nextStateIt = transitionMap.find(stateInput);
	if (nextStateIt != transitionMap.end()) {
		return nextStateIt->second;
	}

    StateInputPair stateInputAll = { state, INPUT_ALL };
	stateInputToStateMap::iterator nextStateAllIt = transitionMap.find(stateInputAll);
	if (nextStateAllIt != transitionMap.end()) {
		return nextStateAllIt->second;
	}

	return NO_TRANSITION;
}

/*
 * Inserts states, which all have specified message type associated with.
 */
void CDCMessageParserPrivate::insertStatesInfo(unsigned int states[], unsigned int
		statesSize, MessageType msgType) {
	for (unsigned int i = 0; i < statesSize; i++) {
		StateInfo stateInfo = { msgType, false };
		statesInfoMap.insert(std::pair<unsigned int, StateInfo>(states[i], stateInfo));
	}
}

/* Inserts states, which all have more message types associated with. */
void CDCMessageParserPrivate::insertMultiTypeStatesInfo(unsigned int states[],
		unsigned int statesSize) {
    for (unsigned int i = 0; i < statesSize; i++) {
		StateInfo stateInfo = { MSG_ERROR, true };
		statesInfoMap.insert(std::pair<unsigned int, StateInfo>(states[i], stateInfo));
	}
}

/* Inits states info map. */
void CDCMessageParserPrivate::initStatesInfoMap(void) {
	unsigned int multiTypeStates[] = { 0, 1, 9, 16, 33 };
	insertMultiTypeStatesInfo(multiTypeStates, 5);

	unsigned int errStates[] = { 2, 3, 4, 5 };
	insertStatesInfo(errStates, 4, MSG_ERROR);

	unsigned int testStates[] = { 6, 7, 8 };
	insertStatesInfo(testStates, 3, MSG_TEST);

	unsigned int resUsbStates[] = { 10, 101, 102, 103 };
	insertStatesInfo(resUsbStates, 4, MSG_RES_USB);

	unsigned int resTRStates[] = { 11, 12, 13, 14, 15 };
	insertStatesInfo(resTRStates, 5, MSG_RES_TR);

	unsigned int usbInfoStates[] = { 17, 18, 19 };
	insertStatesInfo(usbInfoStates, 3, MSG_USB_INFO);

	unsigned int trInfoStates[] = { 20, 21, 22, 23 };
	insertStatesInfo(trInfoStates, 4, MSG_TR_INFO);

	unsigned int usbConStates[] = { 24, 25, 26, 27, 28 };
	insertStatesInfo(usbConStates, 5, MSG_USB_CONN);

	unsigned int spiStatusStates[] = { 29, 30, 31, 32 };
	insertStatesInfo(spiStatusStates, 4, MSG_SPI_STAT);

	unsigned int dataSendStates[] = {	34, 35, 36, 37, 38,
										39, 40, 41, 42, 43,
										44, 45, 46, 47  };
	insertStatesInfo(dataSendStates, 14, MSG_DATA_SEND);

	unsigned int dataReceiveStates[] = { 48, 49, 50, 51, 52 };
	insertStatesInfo(dataReceiveStates, 5, MSG_ASYNC);

	unsigned int switchStates[] = { 53, 54, 55, 56, 57 };
	insertStatesInfo(switchStates, 5,  MSG_SWITCH);
}

/* Inserts transition into transition map. */
void CDCMessageParserPrivate::insertTransition(unsigned int stateId, unsigned int input,
		unsigned int nextStateId) {
	StateInputPair inputPair = { stateId, input };
	transitionMap.insert(std::pair<StateInputPair, int>(inputPair, nextStateId));
}

/* Inits transition map. */
void CDCMessageParserPrivate::initTransitionMap(void) {
    // beginning
	insertTransition(0, '<', 1);
	insertTransition(1, 'E', 2);
	insertTransition(1, 'O', 6);
	insertTransition(1, 'R', 9);
	insertTransition(1, 'I', 16);
	insertTransition(1, 'B', 24);
	insertTransition(1, 'S', 29);
	insertTransition(1, 'D', 33);
	insertTransition(1, 'U', 53);

	// ERR
	insertTransition(2, 'R', 3);
	insertTransition(3, 'R', 4);
	insertTransition(4, 0x0D, 5);

	// MSG_TEST OK
	insertTransition(6, 'K', 7);
	insertTransition(7, 0x0D, 8);

	// RESET USB
    insertTransition(9, ':', 10);
    insertTransition(10, 'O', 101);
    insertTransition(101, 'K', 102);
    insertTransition(102, 0x0D, 103);

	// RESET MODULE
    insertTransition(9, 'T', 11);
	insertTransition(11, ':', 12);
	insertTransition(12, 'O', 13);
	insertTransition(13, 'K', 14);
	insertTransition(14, 0x0D, 15);

	// USB INFO
	insertTransition(16, ':', 17);

	// handled via special function
	//insertTransition(17, text, 18);
	insertTransition(18, 0x0D, 19);

	// MODULE INFO
	insertTransition(16, 'T', 20);
	insertTransition(20, ':', 21);

	// handled via special function
	//insertTransition(21, text, 22);
	insertTransition(22, 0x0D, 23);

    // USB CONNECTION INDICATION
	insertTransition(24, ':', 25);
	insertTransition(25, 'O', 26);
	insertTransition(26, 'K', 27);
	insertTransition(27, 0x0D, 28);

	// SPI STATUS
	insertTransition(29, ':', 30);
	insertTransition(30, INPUT_ALL, 31);
	insertTransition(31, 0x0D, 32);

    // DATA SEND
	insertTransition(33, 'S', 34);
	insertTransition(34, ':', 35);
	insertTransition(35, 'O', 36);
	insertTransition(36, 'K', 37);
	insertTransition(37, 0x0D, 38);

	insertTransition(35, 'E', 39);
	insertTransition(39, 'R', 40);
	insertTransition(40, 'R', 41);
	insertTransition(41, 0x0D, 42);

	insertTransition(35, 'B', 43);
	insertTransition(43, 'U', 44);
	insertTransition(44, 'S', 45);
	insertTransition(45, 'Y', 46);
	insertTransition(46, 0x0D, 47);

	// DATA RECEIVE
    insertTransition(33, 'R', 48);
	insertTransition(48, INPUT_ALL, 49);
	insertTransition(49, ':', 50);

	// handled via special function
	//insertTransition(50, data, 51);
	insertTransition(51, 0x0D, 52);

    // CDC SWITCH
    insertTransition(53, ':', 54);
    insertTransition(54, 'O', 55);
    insertTransition(55, 'K', 56);
	insertTransition(56, 0x0D, 57);
}

/* Inits finite states set. */
void CDCMessageParserPrivate::initFiniteStates(void) {
	finiteStates.insert(5);
	finiteStates.insert(8);
	finiteStates.insert(15);
	finiteStates.insert(19);
	finiteStates.insert(23);
	finiteStates.insert(28);
	finiteStates.insert(32);
	finiteStates.insert(38);
	finiteStates.insert(42);
	finiteStates.insert(47);
	finiteStates.insert(52);
	finiteStates.insert(57);
	finiteStates.insert(103);
}

/* Inits finite states set. */
void CDCMessageParserPrivate::initSpecialStates(void) {
	specialStates.insert(17);
	specialStates.insert(21);
	specialStates.insert(50);
}

/* Initializes spiModes set. */
void CDCMessageParserPrivate::initSpiModes() {
	spiModes.insert(DISABLED);
	spiModes.insert(SUSPENDED);
	spiModes.insert(BUFF_PROTECT);
	spiModes.insert(CRCM_ERR);
	spiModes.insert(READY_COMM);
	spiModes.insert(READY_PROG);
	spiModes.insert(READY_DEBUG);
	spiModes.insert(SLOW_MODE);
	spiModes.insert(HW_ERROR);
}

CDCMessageParserPrivate::CDCMessageParserPrivate() {
	initStatesInfoMap();
	initTransitionMap();
	initFiniteStates();
	initSpecialStates();
	initSpiModes();

	lastParseResult.msgType = MSG_ERROR;
	lastParseResult.resultType = PARSE_NOT_COMPLETE;
	lastParseResult.lastPosition = 0;
}

CDCMessageParserPrivate::~CDCMessageParserPrivate() {
	specialStates.clear();
	finiteStates.clear();
	transitionMap.clear();
	statesInfoMap.clear();
	spiModes.clear();
}


bool checkUSBDeviceType(unsigned char byteToCheck) {
	return true;
}



bool checkUSBDeviceVersion(unsigned char byteToCheck) {
	if ((byteToCheck >= '0') && (byteToCheck <= '9')) {
		return true;
	}

	if (byteToCheck == '.') {
		return true;
	}

	return false;
}

bool checkUSBDeviceId(unsigned char byteToCheck) {
	if ((byteToCheck >= '0') && (byteToCheck <= '9')) {
		return true;
	}
    if ( ( byteToCheck >= 'A' ) && ( byteToCheck <= 'H' ) ) {
		return true;
	}

	return false;
}


CDCMessageParserPrivate::StateProcResult CDCMessageParserPrivate::processUSBInfo(ustring& data,
		unsigned int pos) {
	StateProcResult procResult = { 17, pos, false };

	if (pos == (data.size() - 1)) {
		return procResult;
	}

	const unsigned int TYPE = 0;
	const unsigned int VERSION = 1;
	const unsigned int ID = 2;

	unsigned int activeSection = TYPE;
	procResult.newState = 18;

	for (unsigned int i = pos; i < data.size(); i++) {
		procResult.lastPosition = i;

		if (data[i] == 0x0D) {
			if (activeSection == ID) {
				procResult.newState = 19;
				break;
			}
		}

		if (data[i] == '#') {
			if (activeSection == TYPE) {
				activeSection = VERSION;
			} else if (activeSection == VERSION) {
				activeSection = ID;
			} else {
				procResult.formatError = true;
				break;
			}

			continue;
		}

		switch (activeSection) {
			case TYPE:
				if (!checkUSBDeviceType(data[i])) {
                	procResult.formatError = true;
				}
			break;

			case VERSION:
				if (!checkUSBDeviceVersion(data[i])) {
                	procResult.formatError = true;
				}
			break;

			case ID:
				if (!checkUSBDeviceId(data[i])) {
                	procResult.formatError = true;
				}
			break;
		}

		if (procResult.formatError) {
        	break;
		}
	}

	return procResult;
}

/* Processes state 21. */
CDCMessageParserPrivate::StateProcResult CDCMessageParserPrivate::processTRInfo(ustring& data,
		unsigned int pos) {
	StateProcResult procResult = { 21, pos, false };

	if (pos == (data.size() - 1)) {
		return procResult;
	}

	const unsigned int MODULE_DATA_SIZE = 8;

	procResult.newState = 22;
	if ((pos-1 + MODULE_DATA_SIZE) >= data.size()) {
		procResult.lastPosition = data.size()-1;
	} else {
      	procResult.lastPosition = pos-1 + MODULE_DATA_SIZE;
	}

	return procResult;
}

/* Processes state 50. */
CDCMessageParserPrivate::StateProcResult CDCMessageParserPrivate::processAsynData(ustring& data,
		unsigned int pos) {
	StateProcResult procResult = { 50, pos, false };

	if (pos == (data.size() - 1)) {
		return procResult;
	}

	procResult.newState = 51;
	unsigned int dataLength = data.at(pos-2);

	if ((pos + dataLength) >= data.size()) {
		procResult.lastPosition = data.size()-1;
	} else {
		procResult.lastPosition = (pos-1) + dataLength;
	}

	return procResult;
}

/*
 * Processes specified special state.
 */
CDCMessageParserPrivate::StateProcResult CDCMessageParserPrivate::processSpecialState(
		unsigned int state, ustring& data, unsigned int pos) {
	switch (state) {
		case 17:
			return processUSBInfo(data, pos);
		case 21:
			return processTRInfo(data, pos);
		case 50:
			return processAsynData(data, pos);
	}

	// error - invalid parser state
	std::stringstream excStream;
	excStream << "Unknown special state: " << state;
	throw CDCMessageParserException((excStream.str()).c_str());
}

ParseResult CDCMessageParserPrivate::parseData(ustring& data) {
	lastParsedData = data;
	lastParseResult.resultType = PARSE_NOT_COMPLETE;
	unsigned int state = INITIAL_STATE;

	for (unsigned int pos = 0; pos < lastParsedData.size(); pos++) {
		lastParseResult.lastPosition = pos;

		// special handling of some states
		if (isSpecialState(state)) {
			StateProcResult procResult = processSpecialState(state,
				lastParsedData, pos);
            lastParseResult.lastPosition = procResult.lastPosition;
			if (procResult.formatError) {
				lastParseResult.resultType = PARSE_BAD_FORMAT;
				return lastParseResult;
			} else {
				state = procResult.newState;
				pos = procResult.lastPosition;

				// in the case of final state, return related message type
				if (isFiniteState(state)) {
					mapOfStates::iterator stateInfoIt = statesInfoMap.find(state);
					lastParseResult.msgType = stateInfoIt->second.msgType;
					lastParseResult.resultType = PARSE_OK;
					return lastParseResult;
				}
			}

			continue;
		}

		// do transition to next state
		state = doTransition(state, lastParsedData[pos]);
		if (state == NO_TRANSITION) {
			lastParseResult.resultType = PARSE_BAD_FORMAT;
			return lastParseResult;
		}

		// in the case of final state, return related message type
		if (isFiniteState(state)) {
			mapOfStates::iterator stateInfoIt = statesInfoMap.find(state);
			lastParseResult.msgType = stateInfoIt->second.msgType;
			lastParseResult.resultType = PARSE_OK;
			return lastParseResult;
		}
	}

	return lastParseResult;
}


/* PUBLIC INTERFACE. */
CDCMessageParser::CDCMessageParser() {
	implObj = new CDCMessageParserPrivate();
	InitializeCriticalSection(&csUI);
}

CDCMessageParser::~CDCMessageParser() {
	delete implObj;
	DeleteCriticalSection(&csUI);
}

ParseResult CDCMessageParser::parseData(ustring& data) {
	EnterCriticalSection(&csUI);

	ParseResult parseResult = implObj->parseData(data);

	LeaveCriticalSection(&csUI);
	return parseResult;
}

DeviceInfo* CDCMessageParser::getParsedDeviceInfo(ustring& data) {
	EnterCriticalSection(&csUI);

	DeviceInfo* devInfo = new DeviceInfo();

	// type parsing
	size_t firstHashPos = data.find('#', 3);
	size_t typeSize = firstHashPos - 3;
	ustring typeStr = data.substr(3, typeSize);

	devInfo->type = new char[typeSize + 1];
	strcpy(devInfo->type, (const char*)typeStr.c_str());
	devInfo->typeLen = typeSize;

	// firmware version parsing
    size_t secondHashPos = data.find('#', firstHashPos+1);
    size_t fmSize = secondHashPos - firstHashPos - 1;
	ustring fmStr = data.substr(firstHashPos+1, fmSize);

	devInfo->firmwareVersion = new char[fmSize + 1];
	strcpy(devInfo->firmwareVersion, (const char*)fmStr.c_str());
	devInfo->fvLen = fmSize;

    // serial number parsing
	size_t crPos = data.find(13, secondHashPos+1);
    size_t snSize = crPos - secondHashPos - 1;
	ustring snStr = data.substr(secondHashPos+1, snSize);

    devInfo->serialNumber = new char[snSize + 1];
	strcpy(devInfo->serialNumber, (const char*)snStr.c_str());
	devInfo->snLen = snSize;

	LeaveCriticalSection(&csUI);
	return devInfo;
}

ModuleInfo* CDCMessageParser::getParsedModuleInfo(ustring& data) {
	EnterCriticalSection(&csUI);

	ModuleInfo* modInfo = new ModuleInfo();
    size_t msgBodyPos = 4;

    modInfo->serialNumber[0] = data.at(msgBodyPos);
	modInfo->serialNumber[1] = data.at(msgBodyPos+1);
	modInfo->serialNumber[2] = data.at(msgBodyPos+2);
    modInfo->serialNumber[3] = data.at(msgBodyPos+3);

	unsigned int infoId = ModuleInfo::SN_SIZE;
	modInfo->osVersion = data.at(msgBodyPos+infoId);
	infoId++;
	modInfo->PICType = data.at(msgBodyPos+infoId);
    infoId++;

	for (unsigned int i = 0; i < ModuleInfo::BUILD_SIZE; i++, infoId++) {
		modInfo->osBuild[i] = data.at(msgBodyPos+infoId);
	}

	LeaveCriticalSection(&csUI);
	return modInfo;
}

SPIStatus CDCMessageParser::getParsedSPIStatus(ustring& data) {
	EnterCriticalSection(&csUI);

	SPIStatus spiStatus;
    size_t msgBodyPos = 3;

	int parsedValue = data.at(msgBodyPos);
	if (parsedValue < 0) {
		parsedValue += 256;
	}

	if (implObj->spiModes.find((SPIModes)parsedValue) != implObj->spiModes.end()) {
		spiStatus.SPI_MODE = (SPIModes)parsedValue;
		spiStatus.isDataReady = false;
	} else {
		spiStatus.DATA_READY = (SPIModes)parsedValue;
		spiStatus.isDataReady = true;
	}

	LeaveCriticalSection(&csUI);
	return spiStatus;
}

DSResponse CDCMessageParser::getParsedDSResponse(ustring& data) {
	EnterCriticalSection(&csUI);

	size_t msgBodyPos = 4;
	size_t bodyLen = data.length() - 1 - msgBodyPos;
	ustring msgBody = data.substr(msgBodyPos, bodyLen);

	if (msgBody == uchar_str("OK")) {
        LeaveCriticalSection(&csUI);
		return OK;
	}

	if (msgBody == uchar_str("ERR")) {
		LeaveCriticalSection(&csUI);
		return ERR;
	}

	if (msgBody == uchar_str("BUSY")) {
		LeaveCriticalSection(&csUI);
		return BUSY;
	}

	LeaveCriticalSection(&csUI);

	// error - unknown type of reponse
	std::stringstream excStream;
	excStream << "Unknown DS response value: " << msgBody.c_str();
	throw CDCMessageParserException((excStream.str()).c_str());
}

ustring CDCMessageParser::getParsedDRData(ustring& data) {
	EnterCriticalSection(&csUI);

    size_t userDataStart = 5;
	size_t userDataLen = data.length() - 1 - userDataStart;
	ustring userData = data.substr(5, userDataLen);

	LeaveCriticalSection(&csUI);
	return userData;
}
