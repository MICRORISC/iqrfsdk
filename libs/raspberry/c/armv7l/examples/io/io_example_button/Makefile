io_example_button: io_example_button.o
	gcc -march=armv7-a -Wall io_example_button.o ../../../bin/librpi_io.a -o io_example_button

io_example_button.o: io_example_button.c
	gcc -march=armv7-a -Wall -I ../../../include io_example_button.c -c

clean:
	rm -rf *.o io_example_button

