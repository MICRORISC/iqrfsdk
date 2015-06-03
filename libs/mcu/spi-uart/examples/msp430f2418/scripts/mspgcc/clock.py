# Utility functions for the DCO clock in MSP430 devices.
#
# Functions to measure the DCO clock and functions to do a software FLL to
# callibrate the clock to a gived frequency.
#
# $Id: clock.py,v 1.1 2006/04/11 18:35:23 cliechti Exp $

import cStringIO
import sys
import memory, jtag

debug = False
# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

#see mspgcc cvs/jtag/funclets/counter.S
COUNTER_FUNCLET = """\
@0200
00 02 0a 02 2a 02 60 07 00 00 32 c2 d2 40 f9 ff
57 00 d2 40 f4 ff 58 00 d2 40 ec ff 56 00 0e 43
0f 43 1e 53 0f 63 fd 3f 03 43 ff 3f
q
"""


def getDCOFreq(dcoctl, bcsctl1, bcsctl2=0):
    """measure DCO frequency on a F1xx or F2xx device.
    
       return: frequency in Hz"""
    funclet = memory.Memory()
    funclet.loadTIText(cStringIO.StringIO(COUNTER_FUNCLET))
    
    funclet[0].data = funclet[0].data[:6] \
                    + chr(dcoctl) + chr(bcsctl1) + chr(bcsctl2) \
                    + funclet[0].data[9:]
    runtime = jtag._parjtag.funclet(funclet[0].data, 100)
    count = jtag._parjtag.regread(14) | (jtag._parjtag.regread(15) << 16)
    
    return 1000*count*4/runtime

def setDCO(fmin, fmax, maxrsel=7, dcor=False):
    """Software FLL for F1xx and F2xx devices.
    
       return: (frequency, DCOCTL, BCSCTL1)"""
    if debug: sys.stderr.write("setDCO target: %dHz < frequency < %dHz\n" % (fmin, fmax))
    resolution = 128
    dco = 3<<5
    bcs1 = maxrsel
    fast = True
    upper = False
    lower = False
    
    for tries in range(50):
        if upper and lower and resolution > 1:
            resolution /= 2
            if resolution < 1: resolution = 1
            if debug: sys.stderr.write("switching to higher resolution (-> %d)\n" % (resolution,))
            upper = False
            lower = False
        frequency = getDCOFreq(dco, bcs1, dcor and 1 or 0)
        if frequency > fmax:
            if debug: sys.stderr.write("%luHz is too high, decreasing (was: BCSCTL1=0x%02x; DCOCTL=0x%02x)\n" % (frequency, bcs1, dco))
            upper = True
            dco -= resolution
            if dco <= 0:
                dco = 255
                bcs1 -= 1
                if bcs1 < 0:
                    if resolution > 1:
                        #try again with minimum settings but increased resolution
                        resolution /= 2
                        if resolution < 1: resolution = 1
                        bcs1 = 0
                        dco = 0
                    else:
                        raise IOError("Couldn't get DCO working with correct frequency. Device is not slower than %dHz." % (frequency,))
        elif frequency < fmin:
            if debug: sys.stderr.write("%luHz is too low, increasing (was: BCSCTL1=0x%02x; DCOCTL=0x%02x)\n" % (frequency, bcs1, dco))
            lower = True
            dco += resolution
            if dco > 255:
                dco = 0
                bcs1 += 1
                if bcs1 > maxrsel:
                    if resolution > 1:
                        #try again with maximum settings but increased resolution
                        resolution /= 2
                        if resolution < 1: resolution = 1
                        bcs1 = maxrsel
                        dco = 255
                    else:
                        raise IOError("Couldn't get DCO working with correct frequency. Device is not faster than %dHz." % (frequency,))
        else:
            if debug: sys.stderr.write("%luHz is OK (BCSCTL1=0x%02x; DCOCTL=0x%02x)\n" % (frequency, bcs1, dco))
            return frequency, dco, bcs1
    raise IOError("Couldn't get DCO working with correct frequency. Tolerance too tight? Last frequency was %dHz" % (frequency,))

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

#see mspgcc cvs/jtag/funclets/counterplus.S
COUNTERPLUS_FUNCLET = """\
@0200
00 02 0c 02 3c 02 00 0e 80 80 00 00 32 c2 72 d0
40 00 d2 40 f4 ff 52 00 d2 40 ed ff 51 00 d2 40
e9 ff 53 00 d2 40 e4 ff 54 00 d2 40 da ff 50 00
0e 43 0f 43 1e 53 0f 63 fd 3f 03 43 ff 3f
q
"""

def getDCOPlusFreq(scfi0, scfi1, scfqctl, fll_ctl0, fll_ctl1):
    """measure DCO frequency on a F4xx device
    
       return: frequency in Hz"""
    funclet = memory.Memory()
    funclet.loadTIText(cStringIO.StringIO(COUNTERPLUS_FUNCLET))
    funclet[0].data = funclet[0].data[:6] \
                    + chr(scfi0) + chr(scfi1) \
                    + chr(scfqctl) + chr(fll_ctl0) \
                    + chr(fll_ctl1) + funclet[0].data[11:]
    #~ funclet..[0x205] = scfi0, scfi1, scfqctl, fll_ctl0, fll_ctl1
    runtime = jtag._parjtag.funclet(funclet[0].data, 100)
    count = jtag._parjtag.regread(14) | (jtag._parjtag.regread(15) << 16)
    return 1000*count*4/runtime

def setDCOPlus(fmin, fmax):
    """Software FLL for F4xx devices.
    
       return: (frequency, SCFI0, SCFI1, SCFQCTL, FLL_CTL0, FLL_CTL1)"""
    first = 0
    last = 27 << 5
    
    if debug: sys.stderr.write("setDCOPlus target: %dHz < frequency < %dHz\n" % (fmin, fmax))

    # Binary search through the available frequencies, selecting the highest
    # frequency whithin the acceptable range
    while first + 1 < last:
        mid = (last + first) / 2
        # Select DCO range from 0.23MHz to 11.2MHz. Specify frequency via Ndco.
        # Disable Modulation. Enable DCO+.
        frequency = getDCOPlusFreq(mid&3, mid>>2, 0x80, 0x80, 0)
        if frequency > fmax:
            if debug: sys.stderr.write("%luHz is too high, decreasing\n" % frequency)
            last = mid
        elif frequency < fmin:
            if debug: sys.stderr.write("%luHz is too low, increasing\n" % frequency)
            first = mid
        else:
            break

    frequency = getDCOPlusFreq(mid&3, mid>>2, 0x80, 0x80, 0)
    if debug: sys.stderr.write("%luHz\n" % frequency)
    if fmin <= frequency <= fmax:
        return frequency, mid&3, mid>>2, 0x80, 0x80, 0
    raise IOError("Couldn't get DCO working with correct frequency.")
