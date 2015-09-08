io_example_led: io_example_led.o
	gcc -march=armv7-a -Wall io_example_led.o ../../../bin/librpi_io.a -o io_example_led

io_example_led.o: io_example_led.c
	gcc -march=armv7-a -Wall -I ../../../include io_example_led.c -c

clean:
	rm -rf *.o io_example_led
