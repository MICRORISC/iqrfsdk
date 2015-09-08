spiiqrf_example_writeread: spiiqrf_example_writeread.o
	gcc -march=armv7-a -Wall spiiqrf_example_writeread.o ../../../bin/librpi_spi_iqrf.a -o spiiqrf_example_writeread

spiiqrf_example_writeread.o: spiiqrf_example_writeread.c
	gcc -march=armv7-a -Wall -I ../../../include spiiqrf_example_writeread.c -c

clean:
	rm -rf *.o spiiqrf_example_writeread
