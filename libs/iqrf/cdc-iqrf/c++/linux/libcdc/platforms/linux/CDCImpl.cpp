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

#include <set>
#include <map>

#include <sys/eventfd.h>
#include <termios.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <sys/time.h>
#include <unistd.h>
#include <sys/select.h>

#include <stdio.h>
#include <sstream>
#include <iostream>
#include <errno.h>
#include <limits.h>
#include <string.h>

#include <cdc/CDCImpl.h>
#include <cdc/CDCMessageParser.h>
using namespace std;


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
		unsigned int len;
	};

	/* Info about parsed data. */
	struct ParsedMessage {
		ustring message;
		ParseResult parseResult;
	};


    /* Information about what kind of event to wait for. */
	enum EventType { READ_EVENT, WRITE_EVENT };


	/* OPERATION TIMEOUTS. */
	/* Starting read thread (in seconds). */
	static const unsigned int TM_START_READ = 5;

	/* Canceling read thread (in seconds). */
	static const unsigned int TM_CANCEL_READ = 5;

	/* Sending message to COM-port (in seconds). */
	static const unsigned int TM_SEND_MSG = 5;

	/* Waiting for a response (in seconds). */
	static const unsigned int TM_WAIT_RESP = 5;


    // handle to COM-port
	int portHandle;

    // string identification of used COM-port
	char* commPort;

    // handle to incomming message reading thread
	pthread_t readMsgHandle;

	/*
	 * Signal for main thread, that a new mesage-respond was read from COM-port.
	 */
	int newMsgEvent;

	/* Signal for main thread, that read thread has started. */
	int readStartEvent;

	/* Signal for read thread to cancel. */
	int readEndEvent;

    /* End-response signal from ending read thread to main thread. */
    int readEndResponse;

	/*
	 * Last received parsed response from COM-port.
	 * Not asynchronous message.
	 */
	ParsedMessage lastResponse;

	/* Mapping from message types to response headers. */
	std::map<MessageType, std::string> messageHeaders;

	/* Parser of incomming messages from COM-port. */
	CDCMessageParser* msgParser;


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
	void init();

	/* Initializes messageHeaders map. */
	void initMessageHeaders();

    /* Initializes lastResponse. */
	void initLastResponse(void);

    /* Initializes lastReceptionError. */
	void initLastReceptionError(void);

    /* Initialize mutexes. */
    void initMutexes(void);

	/* Configures and opens port for communication. */
	void openPort();

	/* Starts reading thread. */
	void startReadThread();



    /* EXCEPTION AND ERROR PROCESSING HELPER FUNCTIONS. */
	/* Creates and returns error string from specified parameters. */
	std::string createErrorString(const char* cause, int errorCode);

	/* Creates and returns error c-string from specified parameters. */
	char* createErrorChars(const char* cause, int errorCode);



    /* SIGNALING FUNCTIONS. */
    /* Sets specified event to signaling state. */
    int setEvent(int event);

    /* Select function wrapper. */
    int selectEvents(std::set<int>& fds, EventType evType, unsigned int timeout);

    /*
     * Blocks, until specified event is not in signaling state.
     * If timeout is not 0, waits at max for specified timeout(in seconds).
     */
    int waitForEvent(int event, unsigned int timeout);

    /*
     * Blocks, until one of the specified events signals.
     * If timeout is not 0, waits at max for specified timeout(in seconds).
     */
    int waitForMultipleEvents(std::set<int>& events, unsigned int timeout);



	/* READING OF INCOMMING MESSAGES. */
	/* Stub for private function thread of reading incomming port messages. */
	static void* readMsgThreadStub(void* data);

	/* Function of reading thread of incomming COM port messages. */
	int readMsgThread();

	/* Reads data from port and appends them to the specified buffer. */
	int appendDataFromPort(ustring& destBuffer);

    /* Extracts and process all messages in specified buffer. */
	void processAllMessages(ustring& msgBuffer);

	/* Parses next message string from specified buffer and returns it. */
	ParsedMessage parseNextMessage(ustring& msgBuffer);

	/* Processes specified message - include parsing. */
	void processMessage(ParsedMessage& parsedMessage);



	/* COMMAND - RESPONSE CYCLE. */
	/* Checks processing conditions. */
	void checkProcessingCondition();

	/* Construct command and returns it. */
	Command constructCommand(MessageType msgType, ustring data);

	/* Sends command, waits for response a checks the response. */
	void processCommand(Command& cmd);

	/* Sends command stored in buffer to COM port. */
	void sendCommand(Command& cmd);

	/* Bufferize specified command for passing to COM-port. */
	BuffCommand commandToBuffer(Command& cmd);

	/* Waits for response of sent message. */
	void waitForResponse();
};

/*
 * For converting string literals to unsigned string literals.
 */
inline const unsigned char* uchar_str(const char* s){
  return reinterpret_cast<const unsigned char*>(s);
}

// critical section objects for thread safe access to some fields
static pthread_mutex_t csLastRecpError;
static pthread_mutex_t csReadingStopped;
static pthread_mutex_t csAsyncListener;


void CDCImplPrivate::setAsyncListener(AsyncMsgListener listener) {
	pthread_mutex_lock(&csAsyncListener);
	asyncListener = listener;
	pthread_mutex_unlock(&csAsyncListener);
}

bool CDCImplPrivate::getReceptionStopped(void) {
	pthread_mutex_lock(&csReadingStopped);
	bool tmpReadingStopped = receptionStopped;
	pthread_mutex_unlock(&csReadingStopped);

	return tmpReadingStopped;
}

void CDCImplPrivate::setReceptionStopped(bool value) {
	pthread_mutex_lock(&csReadingStopped);
	receptionStopped = value;
	pthread_mutex_unlock(&csReadingStopped);
}

/* Initializes messageHeaders map. */
void CDCImplPrivate::initMessageHeaders() {
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

void CDCImplPrivate::initMutexes(void) {
    if (pthread_mutex_init(&csLastRecpError, NULL) != 0) {
        string errStr = createErrorString("Initialization of last "
                            "reception error mutex failed", errno);
        throw CDCImplException(errStr.c_str());
	}

	if (pthread_mutex_init(&csReadingStopped, NULL) != 0) {
        string errStr = createErrorString("Initialization of reception "
                            "stopped mutex failed", errno);
        throw CDCImplException(errStr.c_str());
	}

	if (pthread_mutex_init(&csAsyncListener, NULL) != 0) {
        string errStr = createErrorString("Initialization of asynchronous "
                            "listener mutex failed", errno);
        throw CDCImplException(errStr.c_str());
	}
}

/* Encapsulates basic initialization process. */
void CDCImplPrivate::init() {
	this->portHandle = -1;
	openPort();

    newMsgEvent = eventfd(0, 0);
    if (newMsgEvent == -1) {
        string errStr = createErrorString("Create new message event failed with error ",
        errno);
        throw CDCImplException(errStr.c_str());
    }

	readEndEvent = eventfd(0, 0);
	if (readEndEvent == -1) {
        string errStr = createErrorString("Create read end event failed with error ",
			errno);
		throw CDCImplException(errStr.c_str());
	}

    readEndResponse = eventfd(0, 0);
	if (readEndResponse == -1) {
        string errStr = createErrorString("Create read end response failed with error ",
			errno);
		throw CDCImplException(errStr.c_str());
	}

    initMessageHeaders();
	initLastResponse();
	initLastReceptionError();

	asyncListener = NULL;
	receptionStopped = false;

    // Initialize the critical sections for thread safe access.
    initMutexes();

	msgParser = new CDCMessageParser();

	startReadThread();
}

/*
 * Creates new instance with COM-port set to "/dev/ttyS0".
 */
CDCImplPrivate::CDCImplPrivate() {
	this->commPort = (char*)"/dev/ttyACM0";
	init();
}

/*
 * Creates new instance with specified COM-port.
 * @param commPort COM-port to communicate with
 */
CDCImplPrivate::CDCImplPrivate(const char* commPort) {
	this->commPort = (char*)commPort;
	init();
}

/*
 * Destroys communication object and frees all needed resources.
 */
CDCImplPrivate::~CDCImplPrivate() {
    if (setEvent(readEndEvent) != sizeof(uint64_t)) {
        cerr << "Setting read end event failed " << errno << "\n";
    }

    int joinResult = 0;
    int waitResult = waitForEvent(readEndResponse, TM_CANCEL_READ);
    switch (waitResult) {
        case -1:
            cerr << "Waiting for read end event failed " << errno << "\n";
            goto CLEAR;
        case 0:
            cerr << "Waiting for read end event was timeouted\n";
			goto CLEAR;
        default:
            break;
    }

    joinResult = pthread_join(readMsgHandle, NULL);
    if (joinResult != 0) {
        cerr << "Waiting for read thread end failed " << joinResult << "\n";
    }

    CLEAR:
	messageHeaders.clear();

	close(readStartEvent);
	close(newMsgEvent);
	close(readEndEvent);
	close(readEndResponse);

	close(portHandle);

    if (pthread_mutex_destroy(&csLastRecpError) != 0) {
        cerr << "Destroy of last reception error mutex failed: " << errno << "\n";
    }

	if (pthread_mutex_destroy(&csReadingStopped) != 0) {
        cerr << "Destroy of reception stopped mutex failed: " << errno << "\n";
	}

	if (pthread_mutex_destroy(&csAsyncListener) != 0) {
        cerr << "Destroy of asynchronous listener mutex failed: " << errno << "\n";
	}

	delete msgParser;

	if (lastReceptionError != NULL) {
    	delete lastReceptionError;
	}
}

/* Configures and opens port for communication. */
void CDCImplPrivate::openPort() {
    portHandle = open(commPort, O_RDWR | O_NOCTTY);

	//  Handle the error.
	if (portHandle == -1) {
		string errStr = createErrorString("Port handle creation failed with error ", errno);
		throw CDCImplException(errStr.c_str());
	}

    if (isatty(portHandle) == 0) {
        string errStr = createErrorString("Specified file is not associated with terminal ", errno);
		throw CDCImplException(errStr.c_str());
    }

    struct termios portOptions;

    // get current settings of the serial port
    if (tcgetattr(portHandle, &portOptions) == -1) {
        string errStr = createErrorString("Port parameters getting failed with error ", errno);
		throw CDCImplException(errStr.c_str());
    }

    /*
     * Turn of:
     * - stripping input bytes to 7 bits
     * - discarding carriage return characters from input
     * - carriage return characters passed to the application as newline characters
     * - newline characters passed to the application as carriage return characters
     */
    portOptions.c_iflag &= ~(PARMRK | IGNBRK | BRKINT | ISTRIP | IGNCR
                            | ICRNL | INLCR | IXON);


    /*
     * Characters are transmitted as-is.
     */
    portOptions.c_oflag &= ~(OPOST);

    /*
     * Enable reading of incomming characters, 8 bits per byte.
     */
    portOptions.c_cflag |= CREAD;
    portOptions.c_cflag &= ~(CSIZE | PARENB | CSTOPB);
    portOptions.c_cflag |= CS8;

    // setting NONCANONICAL input processing mode
    portOptions.c_lflag &= ~(ICANON | ECHO | ISIG | IEXTEN);
    portOptions.c_lflag |= NOFLSH;

    // speed settings
    cfsetispeed(&portOptions, B57600);
    cfsetospeed(&portOptions, B57600);

    // at least 1 character to read
    portOptions.c_cc[VMIN] = 1;
    portOptions.c_cc[VTIME] = 0;

    if (tcsetattr(portHandle, TCSANOW, &portOptions) == -1) {
        string errStr = createErrorString("Port parameters setting failed with error ", errno);
		throw CDCImplException(errStr.c_str());
    }

    // required to make flush to work because of Linux kernel bug
    if ( sleep(2) != 0 ) {
        string errStr = "Sleeping before flushing the port not elapsed";
		throw CDCImplException(errStr.c_str());
    }

    if ( tcflush(portHandle, TCIOFLUSH) != 0 ) {
        string errStr = createErrorString("Port flushing failed with error ", errno);
		throw CDCImplException(errStr.c_str());
    }
}

/*
 * Creates and starts incomming messages reading thread.
 */
void CDCImplPrivate::startReadThread() {
	readStartEvent = eventfd(0, 0);
    if (readStartEvent == -1) {
        string errStr = createErrorString("Read start event creation failed with error",
			errno);
		throw CDCImplException(errStr.c_str());
    }

    // thread will be created with default attributes
    int createResult = pthread_create(&readMsgHandle, NULL, &readMsgThreadStub,
                        (void*)this);
    if (createResult != 0) {
        string errStr = createErrorString("Creating read thread failed with error",
			createResult);
		throw CDCImplException(errStr.c_str());
    }

    string errStr;

    // waiting for reading thread to start
    int waitResult = waitForEvent(readStartEvent, TM_START_READ);
    switch (waitResult) {
        case -1:
            errStr = createErrorString("Waiting for starting read thread "
                "failed with error", errno);
            throw CDCImplException(errStr.c_str());
        case 0:
            throw CDCImplException("Waiting for starting read thread timeouted");
        default:
            // OK
            break;
    }
}

/*
 * Sets specified event to signaling state.
 */
int CDCImplPrivate::setEvent(int event) {
    uint64_t readEndData = 1;
    return (write(event, &readEndData, sizeof(uint64_t)));
}

/* Wrapper for standard 'select' function. */
int CDCImplPrivate::selectEvents(std::set<int>& fds, EventType evType, unsigned int timeout) {
    if (fds.empty()) {
        return 0;
    }

    int maxFd = 0;
    fd_set selFds;
    FD_ZERO(&selFds);

    set<int>::iterator fdsIt = fds.begin();
    for (;fdsIt != fds.end(); fdsIt++) {
        FD_SET(*fdsIt, &selFds);
        if (*fdsIt > maxFd) {
            maxFd = (*fdsIt);
        }
    }

    maxFd++;
    if (timeout != 0) {
        struct timeval waitTime;
        waitTime.tv_sec = timeout;
        waitTime.tv_usec = 0;

        if (evType == READ_EVENT) {
            return select(maxFd, &selFds, NULL, NULL, &waitTime);
        }
        if (evType == WRITE_EVENT) {
            return select(maxFd, NULL, &selFds, NULL, &waitTime);
        }

        // no other event type - params error
        return -1;
    }

    if (evType == READ_EVENT) {
        return select(maxFd, &selFds, NULL, NULL, NULL);
    }
    if (evType == WRITE_EVENT) {
        return select(maxFd, NULL, &selFds, NULL, NULL);
    }

    // no other event type - params error
    return -1;
}

/*
 * Blocks, until specified event is not in signaling state.
 * If timeout is not 0, waits at max for specified timeout(in seconds).
 */
int CDCImplPrivate::waitForEvent(int event, unsigned int timeout) {
    set<int> events;
    events.insert(event);
    return (selectEvents(events, READ_EVENT, timeout));
}

/*
 * Blocks, until one of the specified events signals.
 * If timeout is not 0, waits at max for specified timeout(in seconds).
 */
int CDCImplPrivate::waitForMultipleEvents(std::set<int>& events, unsigned int timeout) {
    return selectEvents(events, READ_EVENT, timeout);
}

/* Creates and returns error string from specified parameters. */
std::string CDCImplPrivate::createErrorString(const char* cause, int errorCode) {
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
	pthread_mutex_lock(&csLastRecpError);

	unsigned int descrLen = strlen(descr);
	char* tempDescr = new char[descrLen+1];
	strncpy(tempDescr, descr, descrLen);
	tempDescr[descrLen] = '\0';

	if (lastReceptionError != NULL) {
    	delete lastReceptionError;
	}

	lastReceptionError = tempDescr;

	pthread_mutex_unlock(&csLastRecpError);
}

/* Clones last reception error. */
char* CDCImplPrivate::cloneLastReceptionError(void) {
    pthread_mutex_lock(&csLastRecpError);

	if (lastReceptionError == NULL) {
        pthread_mutex_unlock(&csLastRecpError);
		return NULL;
	}

	unsigned int descrLen = strlen(lastReceptionError);
	char* clonedError = new char[descrLen+1];
	strncpy(clonedError, lastReceptionError, descrLen);
	clonedError[descrLen] = '\0';

	pthread_mutex_unlock(&csLastRecpError);

	return clonedError;
}

/*
 * Stub for private function thread of reading incomming COM-port messages.
 */
void* CDCImplPrivate::readMsgThreadStub(void* data) {
	return (void*)(((CDCImplPrivate*)data)->readMsgThread());
}

/*
 *	Function of reading thread of incomming COM-port messages.
 */
int CDCImplPrivate::readMsgThread() {
    ustring receivedBytes;
    fd_set waitEvents;
    char* errorDescr = NULL;

    int maxEventNum = ((portHandle > readEndEvent)? portHandle:readEndEvent) + 1;

    // signal for main thread to continue with initialization
    int startResult = setEvent(readStartEvent);
    if (startResult != sizeof(uint64_t)) {
        errorDescr = createErrorChars("Setting start event failed with error ",
						startResult);
        setLastReceptionError(errorDescr);
        goto READ_ERROR;
    }

    receivedBytes.clear();
    while (true) {
        FD_ZERO(&waitEvents);
        FD_SET(portHandle, &waitEvents);
        FD_SET(readEndEvent, &waitEvents);

        int waitResult = select(maxEventNum, &waitEvents, NULL, NULL, NULL);
        switch (waitResult) {
            case -1:
                errorDescr = createErrorChars("Waiting for event in read cycle"
                                                    " failed with error ", errno);
                setLastReceptionError(errorDescr);
				goto READ_ERROR;
                break;
            case 0:
                // only in the case of timeout period expires
                break;
            default:
                // read in characters into input buffer
                if (FD_ISSET(portHandle, &waitEvents)) {
                    try {
                        int messageEnd = appendDataFromPort(receivedBytes);
                        if (messageEnd != -1) {
                            processAllMessages(receivedBytes);
                        }
                    } catch (CDCReceiveException& e) {
                        setLastReceptionError(e.what());
						goto READ_ERROR;
					}
                }

                // read end
                if (FD_ISSET(readEndEvent, &waitEvents)) {
                    goto READ_END;
                }
        }

    }

READ_END:
    setEvent(readEndResponse);
    return 0;

READ_ERROR:
    setReceptionStopped(true);
    return 1;
}

/*
 * Reads data from port and appends them to specified buffer until no
 * other data are in input buffer of the port.
 * @return position of message end character present in the specified buffer <br>
 *		   -1, if no message end character was appended into specified buffer
 * @throw CDCReceiveException
 */
int CDCImplPrivate::appendDataFromPort(ustring& destBuffer) {
	int messageEnd = -1;

    /*
     * buffer size will be determined according to max. number of chars inside
     * received message
     */
    size_t BUFF_SIZE = 100;
    unsigned char* buffer = new unsigned char[BUFF_SIZE];

    ssize_t readResult = read(portHandle, (void*)buffer, BUFF_SIZE);
    if (readResult == -1) {
        // error in communication
        string errStr = createErrorString("Appending data from COM-port "
                            "failed with error ", errno);
        throw CDCReceiveException(errStr.c_str());
    }

    destBuffer.append(buffer, readResult);
    size_t endPos = destBuffer.find(0x0D);
    if (endPos != string::npos) {
        messageEnd = endPos;
    }

	return messageEnd;
}

/*
 * Extracts and processes all messages inside the specified buffer.
 * @throw CDCReading Exception
 */
void CDCImplPrivate::processAllMessages(ustring& msgBuffer) {
	//std::cout << "processAllMessages - start: msgBuffer=" << msgBuffer.c_str() << "\n";

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
		    //std::cout << "processAllMessages - msgBuffer is empty, end\n";
			return;
		}

		parsedMessage = parseNextMessage(msgBuffer);
	}

	//std::cout << "processAllMessages - end\n";
}


/*
 * Extracts message string from specified buffer and returns
 * it in the form of string. If the buffer does not contain
 * full message, empty string is returned.
 */
CDCImplPrivate::ParsedMessage CDCImplPrivate::parseNextMessage(ustring& msgBuffer) {
	ParsedMessage parsedMessage;
	ustring parsedMsg;

    //std::cout << "parseNextMessage - start: msgBuffer=" << msgBuffer.c_str() << "\n";

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

    /*
    std::cout << "parseNextMessage - end: result type=" << parseResult.resultType;
    std::cout << ", message=" << parsedMessage.message.c_str() << "\n";
	*/
	return parsedMessage;
}

/*
 * Process specified message. First of all, the message is parsed.
 * If the message is asynchronous message, then  registered listener(if exists)
 * is called. Otherwise, last response is updated and "new message"
 * signal for main thread is set.
 * @throw CDCReceptionException
 */
void CDCImplPrivate::processMessage(ParsedMessage& parsedMessage) {
    /*
    std::cout << "processMessage - start: parsedMessage.message=";
    std::cout << parsedMessage.message.c_str() << "\n";

    std::cout << ", parsedMessage.msgType=";
    std::cout << parsedMessage.parseResult.msgType << "\n";
    */
	if (parsedMessage.parseResult.msgType == MSG_ASYNC) {
		pthread_mutex_lock(&csAsyncListener);
		if (asyncListener != NULL) {
			ustring userData = msgParser->getParsedDRData(parsedMessage.message);

            unsigned char* userDataBytes = new unsigned char[userData.length()+1];
            userData.copy(userDataBytes, userData.length());
            userDataBytes[userData.length()] = '\0';

			asyncListener(userDataBytes, userData.length());
			delete[] userDataBytes;
		}
		pthread_mutex_unlock(&csAsyncListener);

		return;
	}

	// copy last parsed message into last response
	lastResponse.parseResult = parsedMessage.parseResult;
	lastResponse.message = parsedMessage.message;

    // signaling new message event to main thread
    if (setEvent(newMsgEvent) != sizeof(uint64_t)) {
        string errStr = createErrorString("Signaling new message event failed "
							"with error ", errno);
		throw CDCReceiveException(errStr.c_str());
    }

    //std::cout << "processMessage - end\n";
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
	Command cmd;
	cmd.msgType = msgType;
	cmd.data = data;

	return cmd;
}

/*
 * Sends command, waits for response a checks the response.
 * @param cmd command to process.
 * @throw CDCImplException if some error occurs during processing
 */
void CDCImplPrivate::processCommand(Command& cmd) {
	checkProcessingCondition();

	sendCommand(cmd);

	//std::cout << "processCommand: Command sent\n";
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
    BuffCommand buffCmd = commandToBuffer(cmd);
    unsigned char* dataToWrite = buffCmd.cmd;
    int dataLen = buffCmd.len;

    set<int> fds;
    fds.insert(portHandle);

    while (dataLen > 0) {
        int selResult = selectEvents(fds, WRITE_EVENT, TM_SEND_MSG);
        if (selResult == -1) {
            string errStr = createErrorString("Sending message failed with error ",
                            errno);
            throw CDCSendException(errStr.c_str());
        }

        if (selResult == 0) {
            throw CDCSendException("Waiting for send timeouted");
        }

        int writeResult = write(portHandle, dataToWrite, dataLen);
        if (writeResult == -1) {
            string errStr = createErrorString("Sending message failed with error ",
                            errno);
            throw CDCSendException(errStr.c_str());
        }

        dataLen -= writeResult;
        dataToWrite += writeResult;
    }
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
 */
void CDCImplPrivate::waitForResponse() {
    string errStr;

    int waitResult = waitForEvent(newMsgEvent, TM_WAIT_RESP);
    switch (waitResult) {
        case -1:
            errStr = createErrorString("Waiting for response failed with error ",
                     errno);
            throw CDCReceiveException(errStr.c_str());
        case 0:
            throw CDCReceiveException("Waiting for response timeouted");
        default:
            // OK
            uint64_t respData = 0;
            if (read(newMsgEvent, &respData, sizeof(uint_fast64_t)) == -1) {
                string errStr = createErrorString("Waiting for response failed "
                                "with error ", errno);
                throw CDCReceiveException(errStr.c_str());
            }

            break;
    }
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
	//std::cout << "resetUSBDevice -start: \n";

	CDCImplPrivate::Command cmd = implObj->constructCommand(MSG_RES_USB, uchar_str(""));
	implObj->processCommand(cmd);

	//std::cout << "resetUSBDevice -end\n";
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

