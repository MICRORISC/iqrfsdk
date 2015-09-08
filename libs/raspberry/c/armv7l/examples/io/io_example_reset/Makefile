io_example_reset: io_example_reset.o
	gcc -march=armv7-a -Wall io_example_reset.o ../../../bin/librpi_io.a -o io_example_reset

io_example_reset.o: io_example_reset.c
	gcc -march=armv7-a -Wall -I ../../../include io_example_reset.c -c

clean:
	rm -rf *.o io_example_reset
