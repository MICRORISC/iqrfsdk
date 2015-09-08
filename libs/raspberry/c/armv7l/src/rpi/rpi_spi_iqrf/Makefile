librpi_spi_iqrf.a: rpi_spi_iqrf.o
	ar -x ../../../bin/librpi_io.a
	ar cru librpi_spi_iqrf.a errors.o rpi_io.o rpi_spi_iqrf.o

rpi_spi_iqrf.o: rpi_spi_iqrf.c
	gcc -march=armv7-a -fPIC -Wall -I ../../../include -c rpi_spi_iqrf.c -o rpi_spi_iqrf.o

tobin:
	mv librpi_spi_iqrf.a ../../../bin

clean:
	rm -rf *.o librpi_spi_iqrf.a

