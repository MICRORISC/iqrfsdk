LIBDPA = .

NAMES    = dpa_main
CPU      = msp430f2418
CC       = msp430-gcc
OBJCOPY  = msp430-objcopy
BSL	 = ./scripts/lnode-bsl

SRC = dpa_main.c \
      $(LIBDPA)/drivers/board.c \
      $(LIBDPA)/drivers/debugpins.c \
      $(LIBDPA)/drivers/leds.c \
      $(LIBDPA)/drivers/bsp_timer.c \
      $(LIBDPA)/drivers/uart0.c \
      $(LIBDPA)/drivers/uart1.c \
      $(LIBDPA)/drivers/spi.c \
      $(LIBDPA)/lib/dpa_library.c 

OBJECTS = $(SRC:.c=.o)

INCLUDES = -I$(LIBDPA)/include

CFLAGS = -mmcu=${CPU} -Wall ${INCLUDES} -g -O2

TARGETS += $(addsuffix .hex, $(NAMES)) $(addsuffix .elf, $(NAMES))


all : $(TARGETS)

%.hex : %.elf
	$(OBJCOPY) -O ihex $< $@

%.elf : ${OBJECTS}
	$(CC) -mmcu=${CPU} -o $@ $(OBJECTS) $(SRC_$(basename $@):.c=.o)


$(OBJECTS) : %.o:%.c
	$(CC) -c $(CFLAGS) $< -o $@

upload:
	$(BSL) --invert-sda --invert-scl -c /dev/ttyUSB0 -r -e -I -p $(NAMES).hex

clean :
	rm -f ${TARGETS} ${OBJECTS} *.elf *.hex

