#include <errno.h>
//#include <DPA.h>
#include <dpa_api.h>
//#include <dpa_int.h>

#include <serial/serial.h>

#include <cdc/CDCImpl.h>

#include <iostream>
#include <exception>
using namespace std;

static bool initialized = false;
static CDCImpl *cdc = NULL;
static serial::Serial *myserial = NULL;
static enum DPA_INTERFACE s_interface = DPA_INTERFACE_UNKNOWN;

static bool isCDC()
{
	return ((cdc != NULL) && s_interface == DPA_INTERFACE_CDC);
}

static bool isSerial()
{
    return ((my_serial != NULL) && s_interface == DPA_INTERFACE_UART);

}

int dpa_init(enum DPA_INTERFACE interface, const char *device)
{
	if (interface == DPA_INTERFACE_CDC) {
		// initialize CDC interface
		CDCImpl* testImp = NULL;
	    try {
	        cdc = new CDCImpl(device);
	    } catch (CDCImplException& e) {
            cerr << e.getDescr() << endl;
	    }
    } else if (interface == DPA_INTERFACE_UART) {
      // initialize UART
        try {
            myserial = new serial::Serial(device, 115200);
        } catch(exception &e) {
            std::cerr << "Exception:" << e.what() << std::endl;
            return -EIO;
        }
    } else {
		return -ENODEV;
	}

	initialized = true;
	s_interface = interface;

	return 0;
}

/**
 * Register callback for responses/notofications
 */
int dpa_register_response_handler(void (* callback)(unsigned char *data, unsigned int length))
{
	if (!initialized || (s_interface == DPA_INTERFACE_UNKNOWN)) {
		return -ENODEV;
	}

	if (isCDC())
		cdc->registerAsyncMsgListener(callback);

	return 0;
}

/**
 * Send DPA request
 */
int dpa_send_request(unsigned char *data, const unsigned int length)
{
    DSResponse response = ERR;
	if (isCDC()) {
        try {
            // sending read temperature request and checking response of the device
            response = cdc->sendData(data, length);
            if (response != OK) {
                // bad response processing...
                cout << "Response not OK: " << response << endl;
            }
        } catch (CDCSendException& ex) {
            cout << ex.getDescr() << endl;
            return -EAGAIN;
            // send exception processing...
        } catch (CDCReceiveException& ex) {
            cout << ex.getDescr() << endl;
            // receive exception processing...
            return -EAGAIN;
        }

        return response;
    } else if (isSerial()) {
        myserial->write(data, length);
    }
    return 0;
}
