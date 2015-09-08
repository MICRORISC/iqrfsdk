rpi_spi_iqrf_javastub.so: rpi_spi_iqrf_javastub.o
	gcc -march=armv7-a -shared rpi_spi_iqrf_javastub.o ../../../bin/librpi_spi_iqrf.a -lpthread -Wl,-soname -Wl,librpi_spi_iqrf_javastub.so -o librpi_spi_iqrf_javastub.so

rpi_spi_iqrf_javastub.o: rpi_spi_iqrf_javastub.c
	#g++ -fPIC -g -Wall -I./include -I/usr/lib/jvm/java-6-openjdk/include -I/usr/lib/jvm/java-6-openjdk/include/linux rpi_io_javastub.c -c
	gcc -march=armv7-a -fPIC -g -Wall -I../../../include -I/usr/lib/jvm/jdk-8-oracle-arm-vfp-hflt/include/ -I/usr/lib/jvm/jdk-8-oracle-arm-vfp-hflt/include/linux rpi_spi_iqrf_javastub.c -c
	#gcc -fPIC -g -Wall -I../../../include -I/home/fantomas/Applications/Java/jdk1.7.0_02/include -I/home/fantomas/Applications/Java/jdk1.7.0_02/linux rpi_io_javastub.c -c

tobin:
	mv librpi_spi_iqrf_javastub.so ../../../bin

clean:
	rm -rf *.o librpi_spi_iqrf_javastub.so
