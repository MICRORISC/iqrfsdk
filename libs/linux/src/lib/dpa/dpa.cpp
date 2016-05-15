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
    return ((myserial != NULL) && s_interface == DPA_INTERFACE_UART);

}

//***************** UART helper routines ****************

#define FLAG_SEQUENCE_BYTE (0x7E)
#define CONTROL_ESCAPE_BYTE (0x7D)
#define ESCAPE_BIT (0x20)

static void doCRC8(unsigned char inData, unsigned char *crc)
 {
    int bitsLeft;

     for (bitsLeft = 8; bitsLeft > 0; bitsLeft--, inData >>= 1)
     {
         if (((*crc ^ inData) & 0x01) != 0)
            *crc = ( ( *crc >> 1 ) ^ 0x8C );
         else
            *crc >>= 1;
     }
}

int prepare_hdcl_buffer(const unsigned char *in_data, const int in_len, unsigned char *out_data, int max_out_len)
{
    unsigned char crc = 0xFF; // base seed
    int j = 1;

    for (int i = 0; i < in_len; i++)
        doCRC8(in_data[i], &crc);

    printf("CRC:%x\n", crc);

    out_data[0] = FLAG_SEQUENCE_BYTE;

    for (int i = 0; i < in_len; i++) {
        unsigned char data = in_data[i];
        if (data == FLAG_SEQUENCE_BYTE || data == CONTROL_ESCAPE_BYTE) {
            out_data[j++] = CONTROL_ESCAPE_BYTE;
            out_data[j] = data ^ ESCAPE_BIT;
        } else {
            out_data[j] = data;
        }
        j++;
        if (j > max_out_len) {
            fprintf(stderr, "Output buffer overflow\n");
            return -1;
        }
    }
    out_data[j++] = crc;
    out_data[j++] = FLAG_SEQUENCE_BYTE;

    for (int x = 0; x < j; x++ )
        printf("0x%02x ", out_data[x]);
    printf("\n");

    return j;
}

//-----------------------------------------------------------------------------

int dpa_init(enum DPA_INTERFACE interface, const char *device)
{
	if (interface == DPA_INTERFACE_CDC) {
		// initialize CDC interface
        /*CDCImpl* testImp = NULL;
	    try {
	        cdc = new CDCImpl(device);
        } catch (CDCImplException& e) {
            cerr << e.getDescr() << endl;
        }*/
    } else if (interface == DPA_INTERFACE_UART) {
      // initialize UART
        try {
            myserial = new serial::Serial(device, 9600, serial::Timeout::simpleTimeout(250));
            printf("isOpen:%d\n", myserial->isOpen());
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

    /*if (isCDC())
		cdc->registerAsyncMsgListener(callback);
    */
	return 0;
}

/**
 * Send DPA request
 */
int dpa_send_request(unsigned char *data, const unsigned int length)
{
    DSResponse response = ERR;
	if (isCDC()) {
      /*  try {
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
        }*/

        return response;
    } else if (isSerial()) {
        printf("Serial\n");
        // prepare buffer
        unsigned char out_buf[64];
        int len = prepare_hdcl_buffer(data, length, out_buf, sizeof(out_buf));
        if (len < 0) {
            fprintf(stderr, "Error during preparing buffer\n");
            return -1;
        }
        try {
            size_t write_len = myserial->write(out_buf, len);
            printf("write_len:%lu\n", write_len);
        } catch (exception &e) {
            std::cerr << "Exception during write:" << e.what() << std::endl;
        }

        unsigned char buf[64];
        memset(buf, 0, sizeof(buf));
        size_t ret_len;
        try {
            ret_len = myserial->read(buf, sizeof(buf));
            printf("read len:%lu\n", ret_len);
        } catch (exception &e) {
            std::cerr << "Exception during read:" << e.what() << std::endl;
        }
        for (int i = 0; i < ret_len; ++i)
            printf("0x%02x ", buf[i]);
        printf("\n");

    }
    return 0;
}
