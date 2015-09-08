librpi_io.a: rpi_io.o
	ar -x ../../../bin/liberrors.a
	ar cru librpi_io.a errors.o rpi_io.o

rpi_io.o: rpi_io.c
	gcc -march=armv7-a -fPIC -Wall -D RPI2 -I ../../../include -c rpi_io.c -o rpi_io.o

tobin:
	mv librpi_io.a ../../../bin

clean:
	rm -rf *.o librpi_io.a

