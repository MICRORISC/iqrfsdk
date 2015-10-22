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

#include <windows.h>
#include <winbase.h>
#include <stdio.h>
#include <vector>
#include <map>
#include <set>
#include <string>
#include <sstream>
#include <iostream>
#include <fstream>
#include <limits.h>

#include <iomanip>

#include <cdc/CDCImpl.h>
#include <cdc/CDCMessageParser.h>
using namespace std;


/*
 * Implementation class.
 */
class CDCImplPrivate {
public:
	CDCImplPrivate();
	CDCImplPrivate(const char* commPort);
    ~CDCImplPrivate();

	/* Command, which will be sent to COM-port. */
	struct Command {
		MessageType msgType;
		ustring data;
	};

	/* Bufferized command for passing to COM-port. */
	struct BuffCommand {
		unsigned char* cmd;
		DWORD len;
	};

	/* Info about parsed data. */
	struct ParsedMessage {
		ustring message;
		ParseResult parseResult;
	};


	/* OPERATION TIMEOUTS. */
	/* Starting read thread. */
	static const DWORD TM_START_READ = 5000;

	/* Canceling read thread. */
	static const DWORD TM_CANCEL_READ = 5000;

	/* Sending message to COM-port. */
	static const DWORD TM_SEND_MSG = 5000;

	/* Waiting for a response. */
	static const DWORD TM_WAIT_RESP = 5000;


	HANDLE portHandle;		// handle to COM-port
	LPCTSTR commPort;
	HANDLE readMsgHandle;

	/*
	 * Signal for main thread, that a new mesage-respond was read from COM-port.
	 */
	HANDLE newMsgEvent;

	/* Signal for main thread, that read thread has started. */
	HANDLE readStartEvent;

	/* Signal for read thread to cancel. */
	HANDLE readEndEvent;


    /* Mapping from message types to response headers. */
	std::map<MessageType, std::string> messageHeaders;

	/* Parser of incomming messages from COM-port. */
	CDCMessageParser* msgParser;

	/*
	 * Last received parsed response from COM-port.
	 * Not asynchronous message.
	 */
	ParsedMessage lastResponse;

	/* Registered listener of asynchronous messages reception. */
	AsyncMsgListener asyncListener;
	void setAsyncListener(AsyncMsgListener listener);

	/* Indicates, wheather is reading thread stopped. */
	bool receptionStopped;
	void setReceptionStopped(bool value);
	bool getReceptionStopped(void);

	/* Last reception error. */
	char* lastReceptionError;
	void setLastReceptionError(const char* descr);
	char* cloneLastReceptionError(void);



	/* INITIALIZATION. */
	/* Encapsulates basic initialization process. */
	void init(void);

	/* Initializes messageHeaders map. */
	void initMessageHeaders(void);

	/* Initializes lastResponse. */
	void initLastResponse(void);

    /* Initializes lastReceptionError. */
	void initLastReceptionError(void);

	/* Configures and opens port for communication. */
	void openPort(void);

	/* Starts reading thread. */
	void startReadThread(void);



	/* EXCEPTION AND ERROR PROCESSING HELPER FUNCTIONS. */
	/* Creates and returns error string from specified parameters. */
	std::string createErrorString(const char* cause, int errorCode);

	/* Creates and returns error c-string from specified parameters. */
	char* createErrorChars(const char* cause, int errorCode);



	/* READING OF INCOMMING MESSAGES. */
	/* Stub for private function thread of reading incomming port messages. */
	static DWORD WINAPI readMsgThreadStub(LPVOID data);

	/* Function of reading thread of incomming COM port messages. */
	DWORD WINAPI readMsgThread(void);

	/* Reads data from port and appends them to the specified buffer. */
	int appendDataFromPort(LPOVERLAPPED overlap, ustring& destBuffer);

	/* Extracts and process all messages in specified buffer. */
	void processAllMessages(ustring& msgBuffer);

	/* Parses next message string from specified buffer and returns it. */
	ParsedMessage parseNextMessage(ustring& msgBuffer);

	/* Processes specified message - include parsing. */
	void processMessage(ParsedMessage& parsedMessage);



	/* COMMAND - RESPONSE CYCLE. */
	/* Checks conditions of processing a command. */
	void checkProcessingCondition(void);

	/* Construct command and returns it. */
	Command constructCommand(MessageType msgType, ustring data);

	/* Sends command, waits for response a checks the response. */
	void processCommand(Command& cmd);

	/* Sends command stored in buffer to COM port. */
	void sendCommand(Command& cmd);

	/* Bufferize specified command for passing to COM-port. */
	BuffCommand commandToBuffer(Command& cmd);

	/* Waits for response of sent message. */
	DWORD waitForResponse(void);

	/* Checks, if specified value is the correct value of SPIStatus. */
	bool isSPIStatusValue(ustring& statValue);
};


/*
// name of file to log into
const char* LOG_FILE = "cdclib.log";


// file logging object
fstream flog;

// creates new logging file
void createNewLogFile() {
	flog.open(LOG_FILE);
	if (flog.fail() || flog.bad()) {
		cerr << "Logging initialization failed, error: " << GetLastError() << "\n";
		throw CDCImplException("Error while openning logging file ");
	} else {
		cout << "Logging init started\n";
	}

	flog << "Initialization started\n";
}
*/

/*
 * For converting string literals to unsigned string literals.
 */
inline const unsigned char* uchar_str(const char* s){
  return reinterpret_cast<const unsigned char*>(s);
}

// critical section objects for thread safe access to some fields
CRITICAL_SECTION csLastRecpError;
CRITICAL_SECTION csReadingStopped;
CRITICAL_SECTION csAsyncListener;


void CDCImplPrivate::setAsyncListener(AsyncMsgListener listener) {
	EnterCriticalSection(&csAsyncListener);
	asyncListener = listener;
	LeaveCriticalSection(&csAsyncListener);
}

bool CDCImplPrivate::getReceptionStopped(void) {
	EnterCriticalSection(&csReadingStopped);
	bool tmpReadingStopped = receptionStopped;
	LeaveCriticalSection(&csReadingStopped);

	return tmpReadingStopped;
}

void CDCImplPrivate::setReceptionStopped(bool value) {
	EnterCriticalSection(&csReadingStopped);
	receptionStopped = value;
	LeaveCriticalSection(&csReadingStopped);
}

/* Initializes messageHeaders map. */
void CDCImplPrivate::initMessageHeaders(void) {
	messageHeaders.insert(pair<MessageType, string>(MSG_TEST, "OK"));
	messageHeaders.insert(pair<MessageType, string>(MSG_RES_USB, "R"));
	messageHeaders.insert(pair<MessageType, string>(MSG_RES_TR, "RT"));
	messageHeaders.insert(pair<MessageType, string>(MSG_USB_INFO, "I"));
	messageHeaders.insert(pair<MessageType, string>(MSG_TR_INFO, "IT"));
	messageHeaders.insert(pair<MessageType, string>(MSG_USB_CONN, "B"));
	messageHeaders.insert(pair<MessageType, string>(MSG_SPI_STAT, "S"));
	messageHeaders.insert(pair<MessageType, string>(MSG_DATA_SEND, "DS"));
	messageHeaders.insert(pair<MessageType, string>(MSG_SWITCH, "U"));
	messageHeaders.insert(pair<MessageType, string>(MSG_ASYNC, "DR"));
}

void CDCImplPrivate::initLastResponse(void)  {
	lastResponse.message = ustring(uchar_str(""));
	lastResponse.parseResult.msgType = MSG_ERROR;
	lastResponse.parseResult.resultType = PARSE_NOT_COMPLETE;
	lastResponse.parseResult.lastPosition = 0;
}

void CDCImplPrivate::initLastReceptionError(void) {
	lastReceptionError = NULL;
}


/* Encapsulates basic initialization process. */
void CDCImplPrivate::init() {
    //createNewLogFile();

	this->portHandle = NULL;
	openPort();
	
	newMsgEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
	if (newMsgEvent == NULL) {
		string errStr = createErrorString("Create new message event failed with error ", 
			GetLastError());
		throw CDCImplException(errStr.c_str());
	}

	readEndEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
	if (readEndEvent == NULL) {
		string errStr = createErrorString("Create read end event failed with error ", 
			GetLastError());
		throw CDCImplException(errStr.c_str());
	}

	initMessageHeaders();
	initLastResponse();
	initLastReceptionError();

	asyncListener = NULL;
	receptionStopped = false;

    // Initialize the critical section for thread safe access to lastReceptioError.
	InitializeCriticalSection(&csLastRecpError);
	InitializeCriticalSection(&csReadingStopped);
	InitializeCriticalSection(&csAsyncListener);

	msgParser = new CDCMessageParser();

	startReadThread();
}

/*
 * Converts specified character string to wide-character string and returns it.
 */
wchar_t* convertToWideChars(const char* charStr) {
	size_t charsToConvert = mbstowcs(NULL, charStr, strlen(charStr));
	size_t maxCount = charsToConvert + 1;
	wchar_t* wcStr = new wchar_t[charsToConvert+1];
	memcpy(wcStr, '\0', (charsToConvert + 1) * sizeof(wchar_t));
	size_t convertedChars = mbstowcs(wcStr, charStr, maxCount);
	/*
	if (mbstowcs_s(&convertedChars, wcStr, charStrSize, charStr, _TRUNCATE) != 0) {
		return L'\0';
	}
	*/
	if (charsToConvert != convertedChars) {
		return L'\0';
	}

	return wcStr;
}

/*
 * Creates new instance with COM-port set to COM1.
 */
CDCImplPrivate::CDCImplPrivate() {
	this->commPort = TEXT("COM1");
	init();
}

/*
 * Creates new instance with specified COM-port.
 * @param commPort COM-port to communicate with
 */
CDCImplPrivate::CDCImplPrivate(const char* commPort) {
	#ifdef _UNICODE
		LPCWSTR wcharCommPort = convertToWideChars(commPort);
		if (wcharCommPort == L'\0') {
			throw CDCImplException("Port name character conversion failed");	
		}
		this->commPort = wcharCommPort;
	#else
        this->commPort = commPort;
	#endif

	init();
}

/*
 * Destroys communication object and frees all needed resources.
 */
CDCImplPrivate::~CDCImplPrivate() {
	if (!SetEvent(readEndEvent)) {
		cerr << "Setting read end event failed " << GetLastError() << "\n";	
	}
	
	DWORD waitResult = WaitForSingleObject(readMsgHandle, TM_CANCEL_READ);
	switch (waitResult) {
		case WAIT_TIMEOUT:
			cerr << "Waiting for read end event was timeouted\n";
			break;
		case WAIT_FAILED:
			cerr << "Waiting for read end event failed " << GetLastError() << "\n";
			break;
		default:
			break;
	}
	
	messageHeaders.clear();

	CloseHandle(readStartEvent);
	CloseHandle(newMsgEvent);
	CloseHandle(readEndEvent);

	CloseHandle(portHandle);
	CloseHandle(readMsgHandle);

	DeleteCriticalSection(&csLastRecpError);
	DeleteCriticalSection(&csReadingStopped);
	DeleteCriticalSection(&csAsyncListener);

	delete msgParser;

	if (lastReceptionError != NULL) {
    	delete lastReceptionError;
	}
	//flog.close();
}


/* 
 * Prints configuration setting of port. 
 * For testing purposes only.
 */
static void printCommState(DCB dcb) {
    printf("\nBaudRate = %d, ByteSize = %d, Parity = %d, StopBits = %d\n", 
              dcb.BaudRate, 
              dcb.ByteSize, 
              dcb.Parity,
              dcb.StopBits );
}

/* 
 * Prints specified timetouts on standard output. 
 * For testing purposes only.
 */
static void printTimeouts(COMMTIMEOUTS timeouts) {
	printf("\nRead interval = %d, Read constant = %d, " 
		"Read multiplier = %d, Write constant = %d, Write multiplier = %d\n", 
		timeouts.ReadIntervalTimeout, 
		timeouts.ReadTotalTimeoutConstant, 
		timeouts.ReadTotalTimeoutMultiplier,
		timeouts.WriteTotalTimeoutConstant,
		timeouts.WriteTotalTimeoutMultiplier);
}

/*
 * Appends COM-port prefix to the specified COM-port and returns it.
 */
static LPTSTR getCompletePortName(LPCTSTR portName) {
	LPTSTR portPrefix = "\\\\.\\";

	int completeSize = lstrlen(portPrefix) + lstrlen(portName);
	LPTSTR completePortName = new TCHAR[completeSize + 1];
	memset(completePortName, '\0', (completeSize + 1) * sizeof(TCHAR));
	if (lstrcat(completePortName, portPrefix) == NULL) {
    	return NULL;
	}

	if (lstrcat(completePortName, portName) == NULL) {
    	return NULL;
	}

	return completePortName;
}

/* Configures and opens port for communication. */
void CDCImplPrivate::openPort() {
	LPTSTR completePortName = getCompletePortName(commPort);
	if (completePortName == NULL) {
		throw CDCImplException("Complete port name creation failed");
	}

	portHandle = CreateFile( completePortName,
					  GENERIC_READ | GENERIC_WRITE, // read and write
					  0,      //  must be opened with exclusive-access
					  NULL,   //  default security attributes
					  OPEN_EXISTING, //  must use OPEN_EXISTING
					  FILE_FLAG_OVERLAPPED, // overlapped operation
					  NULL ); //   must be NULL for comm devices

	//  Handle the error.
	if (portHandle == INVALID_HANDLE_VALUE) {
		string errStr = createErrorString("Port handle creation failed with error ", GetLastError());
		throw CDCImplException(errStr.c_str());
	}
	
	DCB dcb;
	//SecureZeroMemory(&dcb, sizeof(DCB));
	memset(&dcb, 0, sizeof(DCB));
	dcb.DCBlength = sizeof(DCB);

	BOOL getStateResult = GetCommState(portHandle, &dcb);
	if (!getStateResult) {
		string errStr = createErrorString("Port state getting failed with error ", GetLastError());
		throw CDCImplException(errStr.c_str());
	}

	// set comm parameters
	dcb.BaudRate = CBR_57600;     //  baud rate
	dcb.ByteSize = 8;             //  data size, xmit and rcv
	dcb.Parity   = NOPARITY;      //  parity bit
	dcb.StopBits = ONESTOPBIT;    //  stop bit

	BOOL setStateResult = SetCommState(portHandle, &dcb);
	if (!setStateResult) {
		string errStr = createErrorString("Port state setting failed with error ", GetLastError());
		throw CDCImplException(errStr.c_str());
	}
	
	// printCommState(dcb);

	COMMTIMEOUTS timeouts;
	//SecureZeroMemory(&timeouts, sizeof(COMMTIMEOUTS));
	memset(&timeouts, 0, sizeof(COMMTIMEOUTS));

	BOOL getToutsResult = GetCommTimeouts(portHandle, &timeouts);
	if (!getToutsResult) {
		string errStr = createErrorString("Port timeouts getting failed with error", 
			GetLastError());
		throw CDCImplException(errStr.c_str());
	}

	//printTimeouts(timeouts);

	timeouts.ReadIntervalTimeout=50;
	timeouts.ReadTotalTimeoutConstant=50;
	timeouts.ReadTotalTimeoutMultiplier=10;
	timeouts.WriteTotalTimeoutConstant=50;
	timeouts.WriteTotalTimeoutMultiplier=10;

	if(!SetCommTimeouts(portHandle, &timeouts)) {
		string errStr = createErrorString("Port timeouts setting failed with error",
			GetLastError());
		throw CDCImplException(errStr.c_str());
	}
}

/*
 * Creates and starts incomming messages reading thread.
 */
void CDCImplPrivate::startReadThread() {
	readStartEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
	if (readStartEvent == NULL) {
		string errStr = createErrorString("Read start event creation failed with error", 
			GetLastError());
		throw CDCImplException(errStr.c_str());
	}

	readMsgHandle = CreateThread(NULL, 0, readMsgThreadStub, this, 0, NULL);
	if (readMsgHandle == NULL) {
		string errStr = createErrorString("Creating read thread failed with error", 
			GetLastError());
		throw CDCImplException(errStr.c_str());
	}
	
	if (!ResetEvent(readStartEvent)) {
		string errStr = createErrorString("Reset start read event failed with error", 
			GetLastError());
		throw CDCImplException(errStr.c_str());
	}
	
	// waiting for reading thread
	DWORD waitResult = WaitForSingleObject(readStartEvent, INFINITE);
	if (waitResult) {
		string errStr = createErrorString("Waiting for starting read thread failed with error", 
			GetLastError());
		throw CDCImplException(errStr.c_str());
	}
}

/* Creates and returns error string from specified parameters. */
string CDCImplPrivate::createErrorString(const char* cause, int errorCode) {
	stringstream excStream;
	excStream << cause << errorCode;
	return excStream.str();
}

/* Creates and returns error c-string from specified parameters. */
char* CDCImplPrivate::createErrorChars(const char* cause, int errorCode) {
	stringstream excStream;
	excStream << cause << errorCode;
	string errString = excStream.str();

	unsigned int errorLen = errString.length();
	char* error = new char[errorLen+1];
    errString.copy(error, errorLen);
    error[errorLen] = '\0';

	return error;
}

/* Sets last reception error according to parameters. */
void CDCImplPrivate::setLastReceptionError(const char* descr) {
	EnterCriticalSection(&csLastRecpError);

	unsigned int descrLen = strlen(descr);
	char* tempDescr = new char[descrLen+1];
	strncpy(tempDescr, descr, descrLen);
	tempDescr[descrLen] = '\0';

	if (lastReceptionError != NULL) {
    	delete lastReceptionError;
	}

	lastReceptionError = tempDescr;

	LeaveCriticalSection(&csLastRecpError);
}

/* Clones last reception error. */
char* CDCImplPrivate::cloneLastReceptionError(void) {
    EnterCriticalSection(&csLastRecpError);

	if (lastReceptionError == NULL) {
        LeaveCriticalSection(&csLastRecpError);
		return NULL;
	}

	unsigned int descrLen = strlen(lastReceptionError);
	char* clonedError = new char[descrLen+1];
	strncpy(clonedError, lastReceptionError, descrLen);
	clonedError[descrLen] = '\0';

	LeaveCriticalSection(&csLastRecpError);

	return clonedError;
}

/*
 * Stub for private function thread of reading incomming COM-port messages.
 */
DWORD WINAPI CDCImplPrivate::readMsgThreadStub(LPVOID data) {
	return ((CDCImplPrivate*)data)->readMsgThread();
}

/*
 *	Function of reading thread of incomming COM port messages.
 */
DWORD WINAPI CDCImplPrivate::readMsgThread() {
	DWORD eventFlags = EV_RXCHAR;
	ustring receivedBytes;
	receivedBytes.clear();

	DWORD bytesTotal = 0;
	unsigned char byteRead = '\0';

	BOOL fWaitingOnRead = FALSE;
	DWORD occuredEvent = 0;

	int count1 = 0;
	int count2 = 0;

	// critical initialization setting - if it fails, cannot continue
	if (!SetCommMask(portHandle, eventFlags)) {
		char* errorDescr = createErrorChars("SetCommMask failed with error ",
						GetLastError());
		setLastReceptionError(errorDescr);
		goto READ_ERROR;
	}

	OVERLAPPED overlap;
	//SecureZeroMemory(&overlap, sizeof(OVERLAPPED));
	memset(&overlap, 0, sizeof(OVERLAPPED));

	// critical initialization setting - if it fails, cannot continue
	overlap.hEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
	if (overlap.hEvent == NULL) {
		char* errorDescr = createErrorChars("Create read char event failed "
								"with error ", GetLastError());
		setLastReceptionError(errorDescr);
		goto READ_ERROR;
	}

	HANDLE waitEvents[2];
	waitEvents[0] = overlap.hEvent;
	waitEvents[1] = readEndEvent;

READ_BEGIN:
while (true) {
		//cout << "WaitCommEvent" << endl;
		DWORD waitEventResult = WaitCommEvent(portHandle, &occuredEvent, &overlap);

		// signal for main thread to start incomming user requests
		if (!SetEvent(readStartEvent)) {
			char* errorDescr = createErrorChars("Setting event for read start "
									"failed with error ", GetLastError());
			setLastReceptionError(errorDescr);
			goto READ_ERROR;
		}

		if (!waitEventResult) {
			if (GetLastError() != ERROR_IO_PENDING){
				char* errorDescr = createErrorChars("Waiting for char event "
										"failed with error ", GetLastError());
				setLastReceptionError(errorDescr);
				goto READ_ERROR;
			}
			else {
				// Waiting for WaitCommEvent to finish
				//cout << "WaitCommEvent waiting to finish" << endl;

				// Wait a little while for an event to occur.
				//const DWORD READ_TIMEOUT = 500;
				DWORD waitResult = WaitForMultipleObjects(2, waitEvents, FALSE, INFINITE);
				switch(waitResult)
				{
					// Event occurred.
					case WAIT_OBJECT_0:

						if (!GetOverlappedResult(portHandle, &overlap, &bytesTotal, FALSE)) {
							char* errorDescr = createErrorChars("Waiting for char "
										"event failed with error ", GetLastError());
							setLastReceptionError(errorDescr);
							goto READ_ERROR;
						}
						else {
							// WaitCommEvent returned.
							// Deal with reading event as appropriate.
							//cout << "WaitCommEvent returned in overlapped" << endl;
						}
						break;

					case (WAIT_OBJECT_0 + 1):
						//cout << "End reading thread..." << endl;
						goto READ_END;

					case WAIT_TIMEOUT:
						// Operation isn't complete yet. fWaitingOnStatusHandle flag
						// isn't changed since I'll loop back around and I don't want
						// to issue another WaitCommEvent until the first one finishes.
						//
						// This is a good time to do some background work.
						break;

					default:
						// Error in the WaitForSingleObject; abort
						// This indicates a problem with the OVERLAPPED structure's
						// event handle.
						break;
				}
			}
		}
		else {
			// WaitCommEvent returned immediately.
			// Deal with reading event as appropriate.
			//cout << "WaitCommEvent returned immediately" << endl;
		}

		//reading loop
		do {
			if (!fWaitingOnRead) {

				// Issue read operation - overlapped - just once, must waiting for completion
				DWORD dwReadResult = ReadFile(portHandle, &byteRead, 1, &bytesTotal, &overlap);

				if (!dwReadResult) {
					// read not delayed?
					if (GetLastError() != ERROR_IO_PENDING) {
						// Error in communications; report it.
						char* errorDescr = createErrorChars("Reading "
										"failed with error ", GetLastError());
						setLastReceptionError(errorDescr);
						goto READ_ERROR;
					}
					else {
						//cout << "Waiting for reading..." << endl;

						//cout << "Read result:" << dwReadResult << endl;
						//cout << "Last error:" << GetLastError() << endl;
						//cout << "Read byte:" << byteRead << endl;
						//cout << "TotalBytes:" << bytesTotal << endl;

						fWaitingOnRead = TRUE;
					}
				}
				else {
					// read completed immediately
					try {
						//cout << "Reading immediately:" << ++count1 << endl;

						//cout << "Read result:" << dwReadResult << endl;
						//cout << "Last error:" << GetLastError() << endl;
						//cout << "Read byte:" << byteRead << endl;
						//cout << "TotalBytes:" << bytesTotal << endl;

						receivedBytes.push_back(byteRead);

						if (byteRead == 0x0D) {
/*
							basic_string<unsigned char>::iterator its;
							cout.setf(ios_base::hex, ios_base::basefield);
							cout.setf(ios_base::uppercase);
							for(its = receivedBytes.begin(); its != receivedBytes.end(); its++)
								cout << setw(3) << (int) *its;
							cout << endl;
*/
							processAllMessages(receivedBytes);
						}
					} catch (CDCReceiveException& e) {
						setLastReceptionError(e.what());
						goto READ_ERROR;
					}

					// ready to read out another byte (bytesTotal) if available
					fWaitingOnRead = FALSE;
				}
			}

			if (fWaitingOnRead) {
				//cout << "WaitForMultipleObjects" << endl;

				//const DWORD READ_TIMEOUT = 500;
				DWORD waitResult = WaitForMultipleObjects(2, waitEvents, FALSE, INFINITE);
				switch(waitResult)
				{
					// Read completed.
					case WAIT_OBJECT_0:

						if (!GetOverlappedResult(portHandle, &overlap, &bytesTotal, FALSE)) {
							char* errorDescr = createErrorChars("Waiting for reading "
										"event failed with error ", GetLastError());
							setLastReceptionError(errorDescr);
							goto READ_ERROR;
						}
						else {
							// Read completed successfully.
							try {
								//cout << "Reading overlap:" << ++count2 << endl;
								//cout << "TotalBytes:" << bytesTotal << endl;

								if(bytesTotal != 0) {
									//cout << "Read byte:" << byteRead << endl;
									receivedBytes.push_back(byteRead);
								}

								if (byteRead == 0x0D) {
/*
									basic_string<unsigned char>::iterator its;
									cout.setf(ios_base::hex, ios_base::basefield);
									cout.setf(ios_base::uppercase);
									for(its = receivedBytes.begin(); its != receivedBytes.end(); its++)
										cout << setw(3) << (int) *its;
									cout << endl;
*/
									processAllMessages(receivedBytes);
								}
							} catch (CDCReceiveException& e) {
								setLastReceptionError(e.what());
								goto READ_ERROR;
							}
						}

						//  Reset flag so that another opertion can be issued.
						fWaitingOnRead = FALSE;
						break;

					case (WAIT_OBJECT_0 + 1):
						cout << "End reading thread..." << endl;
						goto READ_END;

					case WAIT_TIMEOUT:
						// Operation isn't complete yet. fWaitingOnRead flag isn't
						// changed since I'll loop back around, and I don't want
						// to issue another read until the first one finishes.
						//
						// This is a good time to do some background work.
						//cout << "Reading timeouted..." << endl;
						break;

					default:
						// Error in the WaitForSingleObject; abort.
						// This indicates a problem with the OVERLAPPED structure's
						// event handle.
						char* errorDescr = createErrorChars(
							"Waiting for event in read cycle failed with error ", GetLastError());
						setLastReceptionError(errorDescr);
						goto READ_ERROR;
				}
			}
		} while (bytesTotal);
	}

READ_END:
	CloseHandle(overlap.hEvent);
	return 0;

READ_ERROR:
	CloseHandle(overlap.hEvent);
	setReceptionStopped(true);
	return 1;
}

/*
 * Extracts and processes all messages inside the specified buffer.
 * @throw CDCReading Exception
 */
void CDCImplPrivate::processAllMessages(ustring& msgBuffer) {
	if (msgBuffer.empty()) {
		return;
	}

	ParsedMessage parsedMessage = parseNextMessage(msgBuffer);
	while ( parsedMessage.parseResult.resultType != PARSE_NOT_COMPLETE ) {
		if ( parsedMessage.parseResult.resultType == PARSE_BAD_FORMAT ) {
			// throw all bytes from the buffer up to next 0x0D
			size_t endMsgPos = msgBuffer.find(0x0D, parsedMessage.parseResult.lastPosition);
			if (endMsgPos == string::npos) {
				msgBuffer.clear();
			}  else {
				msgBuffer.erase(0, endMsgPos+1);
			}

			setLastReceptionError("Bad message format");
		} else {
			msgBuffer.erase(0, parsedMessage.parseResult.lastPosition+1);
			processMessage(parsedMessage);
		}

		if (msgBuffer.empty()) {
			return;
		}

		parsedMessage = parseNextMessage(msgBuffer);
	}
}

/*
 * Extracts message string from specified buffer and returns
 * it in the form of string. If the buffer does not contain
 * full message, empty string is returned.
 */
CDCImplPrivate::ParsedMessage CDCImplPrivate::parseNextMessage(ustring& msgBuffer) {
	ParsedMessage parsedMessage;
	ustring parsedMsg;

	ParseResult parseResult = msgParser->parseData(msgBuffer);
	switch (parseResult.resultType) {
		case PARSE_OK:
			parsedMsg = msgBuffer.substr(0, parseResult.lastPosition+1);
			parsedMessage.message = parsedMsg;
			break;

		case PARSE_NOT_COMPLETE:
			parsedMessage.message = ustring(uchar_str(""));
			break;

		case PARSE_BAD_FORMAT:
            parsedMessage.message = ustring(uchar_str(""));
			break;
	}

    parsedMessage.parseResult = parseResult;
	return parsedMessage;
}

/*
 * Process specified message. First of all, the message is parsed. 
 * If the message is asynchronous message, then  registered listener(if exists)
 * is called. Otherwise, last response is updated and "new message" 
 * signal for main thread is set.
 * @throw CDCReceiveException
 */
void CDCImplPrivate::processMessage(ParsedMessage& parsedMessage) {
	if (parsedMessage.parseResult.msgType == MSG_ASYNC) {
		EnterCriticalSection(&csAsyncListener);
		if (asyncListener != NULL) {
			ustring userData = msgParser->getParsedDRData(parsedMessage.message);

            unsigned char* userDataBytes = new unsigned char[userData.length()+1];
            userData.copy(userDataBytes, userData.length());
            userDataBytes[userData.length()] = '\0';

			asyncListener(userDataBytes, userData.length());
			delete[] userDataBytes;
		}
		LeaveCriticalSection(&csAsyncListener);

		return;
	} 

	// copy last parsed message into last response
	lastResponse.parseResult = parsedMessage.parseResult;
	lastResponse.message = parsedMessage.message;
	
	if (!SetEvent(newMsgEvent)) {
		string errStr = createErrorString("Signaling new message event failed "
							"with error ", GetLastError());
		throw CDCReceiveException(errStr.c_str());
	}
}

/*
 * Checks processing condition - before command processing.
 */
void CDCImplPrivate::checkProcessingCondition() {
	if (getReceptionStopped()) {
		throw CDCSendException("Reading is actually stopped");
	}
}

/* 
 * Construct command and returns it. 
 * @return command of specified message type with data.
 */
CDCImplPrivate::Command CDCImplPrivate::constructCommand(MessageType msgType, ustring data) {
	//flog << "implObj->constructCommand - begin:\n";

	Command cmd;
	cmd.msgType = msgType;
	cmd.data = data;

	//flog << "implObj->constructCommand - end\n" ;
	return cmd;
}

/*  
 * Sends command, waits for response a checks the response.
 * @param cmd command to process.
 * @throw CDCImplException if some error occurs during processing
 */
void CDCImplPrivate::processCommand(Command& cmd) {
	checkProcessingCondition();

	if (!ResetEvent(newMsgEvent)) {
		string errStr = createErrorString("Reseting new message event failed with error", 
			GetLastError());
		throw CDCReceiveException(errStr.c_str());
	}

	sendCommand(cmd);
	waitForResponse();

	if (lastResponse.parseResult.msgType != cmd.msgType) {
		throw CDCReceiveException("Response has bad type.");
	}
}

/*
 * Sends command stored in buffer to COM port.
 * @param cmd command to send to COM-port.
 */
void CDCImplPrivate::sendCommand(Command& cmd) {
	OVERLAPPED overlap;
	//SecureZeroMemory(&overlap, sizeof(OVERLAPPED));
	memset(&overlap, 0, sizeof(OVERLAPPED));

	overlap.hEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
	if (overlap.hEvent == NULL) {
		string errStr = createErrorString("Creating send event failed with error ",
			GetLastError());
		throw CDCSendException(errStr.c_str());
	}

	BuffCommand buffCmd = commandToBuffer(cmd);
	DWORD bytesWritten = 0;
	if (!WriteFile(portHandle, buffCmd.cmd, buffCmd.len, &bytesWritten, &overlap)) {
		if (GetLastError() != ERROR_IO_PENDING) {
			string errStr = createErrorString("Sending message failed with error ",
				GetLastError());
			throw CDCSendException(errStr.c_str());
		} else {
			DWORD waitResult = WaitForSingleObject(overlap.hEvent, TM_SEND_MSG);
			switch(waitResult) {
				case WAIT_OBJECT_0:
					if (!GetOverlappedResult(portHandle, &overlap, &bytesWritten, FALSE)) {
						string errStr = createErrorString("Waiting for send failed with error ",
							GetLastError());
						throw CDCSendException(errStr.c_str());
					} else {
						// Write operation completed successfully
					}
					 break;
				case WAIT_TIMEOUT:
					throw CDCSendException("Waiting for send timeouted");
				default:
					string errStr = createErrorString("Waiting for send failed with error ",
						GetLastError());
					throw CDCSendException(errStr.c_str());
			}
		}
	} else {
		// Write operation completed successfully
	}

   CloseHandle(overlap.hEvent);
}

/* 
 * Bufferize specified command, so that it can be passed to COM-port.
 * @param cmd command to bufferize
 * @return bufferrized form of command
 */
CDCImplPrivate::BuffCommand CDCImplPrivate::commandToBuffer(Command& cmd) {
	ustring tmpStr(uchar_str(">"));
	if (cmd.msgType != MSG_TEST) {
		tmpStr.append(uchar_str(messageHeaders[cmd.msgType].c_str()));
	}
	if (cmd.msgType == MSG_DATA_SEND) {
		if (cmd.data.size() > UCHAR_MAX) {
			throw CDCSendException("Data size too large");	
		}

		tmpStr.append(1, cmd.data.size());

		// appending data length in hex format
		/*
		stringstream strStream;
		strStream << hex << cmd.data.size();
		string dataLenStr = strStream.str();
		if (dataLenStr.size() == 1) {
			tmpStr.append("0");
		}
		
		tmpStr.append(dataLenStr);
		*/
		tmpStr.append(uchar_str(":"));
		tmpStr.append(cmd.data);
	}
	tmpStr.append(1, 0x0D);

	BuffCommand buffCmd;
	buffCmd.cmd = new unsigned char[tmpStr.size()];
	tmpStr.copy(buffCmd.cmd, tmpStr.size());
	buffCmd.len = tmpStr.size();

	return buffCmd;
}

/*
 * Waits for response of sent message.
 * @return WAIT_OBJECT_0 - response OK.
 *		    WAIT_TIMEOUT - waiting timeouted
 *			other value - error
 */
DWORD CDCImplPrivate::waitForResponse() {
	std::stringstream excStream;
	DWORD waitResult = WaitForSingleObject(newMsgEvent, TM_WAIT_RESP);
	switch (waitResult) {
		case WAIT_OBJECT_0:
			// OK
			break;
		case WAIT_TIMEOUT:
			throw CDCReceiveException("Waiting for response timeouted");
		default:
			excStream << "WaitForSingleObject failed with error " << GetLastError() << "\n";
			throw CDCReceiveException(excStream.str().c_str());
	}

	return waitResult;
}

/* 
 * Checks, if specified value is the correct value of SPIStatus.
 * @param statValue string value to check for
 * @return TRUE stat value is correct value of SPIStatus
 *		   FALSE otherwise
 */
bool CDCImplPrivate::isSPIStatusValue(ustring& statValue) {
	int parsedValue = strtol((const char*)statValue.c_str(), NULL, 16);
	if (parsedValue <= DISABLED && parsedValue <= HW_ERROR) {
		return true;
	}

	return false;
}




/* --- PUBLIC INTERFACE */
CDCImpl::CDCImpl() {
	implObj = new CDCImplPrivate();
}

CDCImpl::CDCImpl(const char* commPort) {
    implObj = new CDCImplPrivate(commPort);
}

CDCImpl::~CDCImpl() {
	delete implObj;
}

/*
 * Performs communication test.
 * @return TRUE if the test succeeds
 *		   FALSE otherwise
 */
bool CDCImpl::test() {
	//flog << "test - begin:\n";

	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_TEST, uchar_str(""));
	implObj->processCommand(cmd);

	//flog << "test - end\n";
	return true;
}

void CDCImpl::resetUSBDevice() {
	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_RES_USB, uchar_str(""));
	implObj->processCommand(cmd);
}

void CDCImpl::resetTRModule() {
	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_RES_TR, uchar_str(""));
	implObj->processCommand(cmd);
}

void CDCImpl::indicateConnectivity() {
	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_USB_CONN, uchar_str(""));
	implObj->processCommand(cmd);
}

DeviceInfo* CDCImpl::getUSBDeviceInfo(void) {
	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_USB_INFO, uchar_str(""));
	implObj->processCommand(cmd);
	return implObj->msgParser->getParsedDeviceInfo(implObj->lastResponse.message);
}

ModuleInfo* CDCImpl::getTRModuleInfo(void) {
	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_TR_INFO, uchar_str(""));
	implObj->processCommand(cmd);
	return implObj->msgParser->getParsedModuleInfo(implObj->lastResponse.message);
}

SPIStatus CDCImpl::getStatus(void) {
	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_SPI_STAT, uchar_str(""));
	implObj->processCommand(cmd);
	return implObj->msgParser->getParsedSPIStatus(implObj->lastResponse.message);
}

DSResponse CDCImpl::sendData(unsigned char* data, unsigned int dlen) {
	ustring dataStr(data, dlen);
	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_DATA_SEND, dataStr);
	implObj->processCommand(cmd);
	return implObj->msgParser->getParsedDSResponse(implObj->lastResponse.message);
}

void CDCImpl::switchToCustom(void) {
	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_SWITCH, uchar_str(""));
	implObj->processCommand(cmd);
}


bool CDCImpl::isReceptionStopped(void) {
	return implObj->getReceptionStopped();
}

/*
 * Returns last reception error.
 */
char* CDCImpl::getLastReceptionError(void) {
	return implObj->cloneLastReceptionError();
}

/* Registers user-defined listener of asynchronous messages. */
void CDCImpl::registerAsyncMsgListener(AsyncMsgListener asyncListener) {
	implObj->setAsyncListener(asyncListener);
}

/* Unregisters listener of asynchronous messages. */
void CDCImpl::unregisterAsyncMsgListener(void) {
	implObj->setAsyncListener(NULL);
}

